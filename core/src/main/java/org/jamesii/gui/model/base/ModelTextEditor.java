/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.base;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.model.symbolic.SymbolicModelException;
import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.model.ISymbolicModelWindowManager;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.syntaxeditor.IInfoProvider;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;
import org.jamesii.gui.syntaxeditor.SyntaxEditor;
import org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter;
import org.jamesii.gui.syntaxeditor.highlighting.IHighlighter;
import org.jamesii.gui.syntaxeditor.highlighting.DefaultHighlighter.Style;
import org.jamesii.gui.syntaxeditor.highlighting.plugintype.HighlightingFactory;

/**
 * A general text editor for symbolic models that takes advantage of provided
 * textual representation a model combined with a provided {@link IHighlighter}
 * for coloring.<br/>
 * Instead of creating your own text editor with syntax highlighting for a given
 * symbolic model just provide a suitable {@link ISymbolicModel} implementation
 * for the model and an appropriate {@link HighlightingFactory} where the
 * {@link HighlightingFactory#supportsInput(Object)} should return true if the
 * supported {@link ISymbolicModel} is provided as input.
 * 
 * @author Stefan Rybacki
 */
// TODO sr137: provide more extension points than only highlighting
// (e.g. Code
// Completion, Custom KeyBindings etc., more than one highlighter)
public class ModelTextEditor extends ModelWindow<ISymbolicModel<?>> {

  /**
   * the highlighter currently in use
   */
  private IHighlighter currentHighlighter;

  /**
   * all available highlighters for the given text
   */
  private List<IHighlighter> highlighters;

  /**
   * the syntax highlighting editor
   */
  private SyntaxEditor editor;

  /** The document. */
  private IDocument<Object> document;

  /**
   * The info providers.
   */
  private List<IModelInfoProvider> infoProviders;

  /**
   * Creates a new {@link ModelTextEditor} from the given model using the
   * specified highlighters and the specified title.
   * 
   * @param title
   *          the editor's title
   * @param model
   *          the model to edit
   * @param modelManager
   *          the model manager to use
   * @param highlighters
   *          the available highlighters for the given model
   * @param infoProviders
   *          the available {@link IInfoProvider}s for the given model
   */
  public ModelTextEditor(String title, ISymbolicModel<?> model,
      ISymbolicModelWindowManager modelManager,
      List<IHighlighter> highlighters, List<IModelInfoProvider> infoProviders) {
    super(title, model, modelManager);

    if (highlighters.size() <= 0) {
      highlighters.add(new DefaultHighlighter(Style.DEFAULT));
    }

    // TODO sr137: store last selected highlighter and use it next
    // time a
    // similar model is opened (or the same???) instead of always the
    // first one
    this.currentHighlighter = highlighters.get(0);
    this.highlighters = highlighters;
    this.infoProviders = infoProviders;

    if (currentHighlighter == null || model == null) {
      throw new SymbolicModelException(
          "Model, ModelManager, Highlighter can't be null!");
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void modelChanged() {
    try {
      int caretPos = editor.getCaretPosition();
      document =
          (IDocument<Object>) getModel().getAsDocument(
              currentHighlighter.getDocumentClass());
      if (document == null) {
        throw new SymbolicModelException("Could not convert model to document!");
      }

      String text = (String) document.getContent();
      if (text == null) {
        throw new SymbolicModelException(
            "Given model does not support a textual symbolic model!");
      }
      editor.setText(text);
      editor.setCaretPosition(caretPos);
    } catch (Exception e) {
      SimSystem.report(e);
    }

  }

  @Override
  public void prepareModelSaving() {
    document.updateContent(editor.getText());
    getModel().setFromDocument(document);
    try {
      editor.setText(getModel()
          .getAsDocument(currentHighlighter.getDocumentClass()).getContent()
          .toString());
    } catch (Exception e) {
      JOptionPane.showMessageDialog(editor,
          "An error occured while preparing for saving the model!", "Error",
          JOptionPane.ERROR_MESSAGE);
      SimSystem.report(e);
    }
  }

  @Override
  protected JComponent createContent() {
    JPanel panel = new JPanel(new BorderLayout());
    if (editor == null) {
      editor = new SyntaxEditor();
      if (currentHighlighter != null) {
        editor.setHighlighting(currentHighlighter.getLexer(),
            currentHighlighter.getSyntaxStylizer());
        editor.setShowAnnotations(true);
        editor.setShowLineNumbers(true);
        editor.setShowIcons(true);
        editor.setShowInfoView(true);
        for (IInfoProvider p : infoProviders) {
          editor.addInfoProvider(p);
        }
      }
    }

    panel.add(editor, BorderLayout.CENTER);

    modelChanged();

    return panel;
  }

  @Override
  public boolean isUndoRedoSupported() {
    return true;
  }

  @Override
  public JamesUndoManager getUndoManager() {
    return editor.getUndoManager();
  }

  @Override
  protected IAction[] generateAdditionalActions() {
    // only create actions for highlighters if there is more than one
    if (highlighters.size() <= 1) {
      return null;
    }

    List<IAction> actions = new ArrayList<>();

    Icon icon = null;
    icon =
        IconManager.getIcon(IconIdentifier.TEXTAREA_SMALL,
            "Change Highlighting");

    actions.add(new ActionSet("org.jamesii.texteditor.highlighterselection",
        "Change Highlighting", "", null, null, icon, this));

    for (final IHighlighter h : highlighters) {
      actions
          .add(new AbstractAction("org.jamesii.texteditor.highlighter."
              + h.getName(), h.getName(),
              new String[] { "org.jamesii.texteditor.highlighterselection" },
              this) {

            @Override
            public void execute() {
              editor.setHighlighting(h.getLexer(), h.getSyntaxStylizer());
              currentHighlighter = h;
              prepareModelSaving();
            }
          });
    }

    return actions.toArray(new IAction[actions.size()]);
  }

}
