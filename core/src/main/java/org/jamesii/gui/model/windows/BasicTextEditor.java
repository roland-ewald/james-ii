/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jamesii.SimSystem;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.gui.model.ISymbolicModelWindowManager;
import org.jamesii.gui.model.windows.plugintype.ModelWindow;
import org.jamesii.gui.syntaxeditor.SyntaxEditor;

/**
 * This should eventually become the basic editor class for textual
 * representations of models. At some point, we should check how to integrate
 * the built-in Java text components in a smarter way
 * (http://java.sun.com/docs/books
 * /tutorial/uiswing/components/generaltext.html).
 * 
 * @param <M>
 *          the type of the symbolic model
 * 
 * @author Matthias Jeschke
 * @author Roland Ewald
 */
@Deprecated
// Use org.jamesii.gui.syntaxeditor instead!
public abstract class BasicTextEditor<M extends ISymbolicModel<?>> extends
    ModelWindow<M> {

  /**
   * The default size of the editor window.
   */
  private static final int DEFAULTSIZE = 600;

  /** Text area to hold the model's textual representation. */
  private SyntaxEditor modelTextArea = new SyntaxEditor();

  /** Scroll pane for the text area. */
  private JScrollPane modelDefScrollPane = new JScrollPane(modelTextArea);

  /** The current model's name. */
  private String modelName;

  /** Title prefix of the editor window. */
  private String titlePrefix;

  /** The content. */
  private JPanel content;

  /**
   * Default constructor.
   * 
   * @param titlePref
   *          the window title prefix (will be displayed before the model's name
   *          in the window title)
   * @param mod
   *          the model
   * @param mwManager
   *          the model window manager
   */
  public BasicTextEditor(String titlePref, M mod,
      ISymbolicModelWindowManager mwManager) {
    super(getTitle(titlePref, mod), mod, mwManager);
    titlePrefix = titlePref;
    modelName = mod.getName();
    this.modelChanged();
  }

  /**
   * Generates the title of the editor.
   * 
   * @param titlePrefix
   *          the title prefix
   * @param mod
   *          the current model
   * 
   * @return the appropriate title for this window
   */
  public static String getTitle(String titlePrefix, ISymbolicModel<?> mod) {
    return titlePrefix + mod.getName();
  }

  /**
   * Checks if the model's name has been changed (if so, the change will be
   * propagated to the main window).
   */
  protected void checkModelName() {
    if (modelName.compareTo(getModel().getName()) != 0) {
      modelName = getModel().getName();
      fireTitleChanged();
      setTitle(getTitle(titlePrefix, getModel()));
    }
  }

  /**
   * Create a model from a string representation.
   * 
   * @param modelString
   *          the string that shall be converted into a model
   * 
   * @return the created model
   * 
   * @throws Exception
   *           if the parsing process failed, eg. the string is not a valid
   *           representation
   */
  protected abstract M getModelFromString(String modelString);

  /**
   * Returns a string representation of the given model.
   * 
   * @param mod
   *          the model to be represented as a string
   * 
   * @return string representation
   */
  protected abstract String getStringFromModel(M mod);

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(DEFAULTSIZE, DEFAULTSIZE);
  }

  /**
   * Model changed.
   * 
   * @see org.jamesii.gui.model.windows.plugintype.ModelWindow#modelChanged()
   */
  @Override
  public void modelChanged() {
    modelTextArea.setText(getStringFromModel(getModel()));
    checkModelName();
  }

  /**
   * Prepare model saving.
   * 
   * @see org.jamesii.gui.model.windows.plugintype.ModelWindow#prepareModelSaving()
   */
  @Override
  public void prepareModelSaving() {
    M parsedModel = null;

    // This way of handling removes all unsaved changes from other
    // editors, a more advanced synchronisation mechanism should be possible

    try {
      parsedModel = getModelFromString(modelTextArea.getText());
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, "Error while parsing:" + ex.getMessage(),
          ex);
    }
    syncModel(parsedModel);
  }

  /**
   * Synchronises the model of the model window with a newly created model from
   * the parser, ie which was created using getModelFromString().
   * 
   * @param mod
   *          the newly created model
   */
  protected abstract void syncModel(M mod);

  /**
   * Synchronises given model with the window's model and triggers an update.
   * 
   * @param mod
   *          the newly generated model
   */
  public void triggerModelUpdate(M mod) {
    syncModel(mod);
    checkModelName();
    getModelManager().modelUpdated(getModel(), this);
  }

  @Override
  protected JComponent createContent() {
    content = new JPanel();
    content.setLayout(new BorderLayout());
    content.add(modelDefScrollPane, BorderLayout.CENTER);
    return content;
  }

}
