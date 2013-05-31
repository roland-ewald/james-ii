/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.gui.model.windows.plugintype.ModelWindowFactory;
import org.jamesii.gui.utils.FactoryListCellRenderer;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * Wizard page used in the "Tutorial" workflow scenario. It provides the ability
 * to select a model editor for the formalism.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
class EditorSelector extends AbstractWizardPage implements
    ListSelectionListener {
  /**
   * a symbolic model implementing the formalism
   */
  private ISymbolicModel<?> model;

  /**
   * flag whether next button can be activated
   */
  private boolean canNext = false;

  /**
   * the actual SWING wizard page
   */
  private final JPanel page = new JPanel();

  /**
   * a list of available editors for the given formalism / symbolic model
   */
  private JList editorList;

  /**
   * The Constant EDITOR.
   */
  public static final String EDITOR = "editor";

  /**
   * Instantiates a new editor selector.
   */
  public EditorSelector() {
    super(
        "Editor selection",
        "Select an editor that is used to create and edit a model for the selected formalism");
  }

  @Override
  protected JComponent createPage() {
    editorList = new JList();
    editorList.addListSelectionListener(this);
    editorList.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        // if double click
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
            && editorList.getSelectedValue() != null) {
          fireNext();
        }
      }
    });

    page.setLayout(new BorderLayout());

    page.add(new JLabel("Select Editor:"), BorderLayout.NORTH);
    page.add(new JScrollPane(editorList), BorderLayout.CENTER);

    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    wizard.putValue(EDITOR, editorList.getSelectedValue());
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    model = wizard.getValue(FormalismChooser.MODEL);

    editorList.setModel(new EditorListModel(model));
    editorList.setCellRenderer(new FactoryListCellRenderer());
    canNext = false;

    fireStatesChanged();
  }

  @Override
  public boolean canBack(IWizard wizard) {
    return true;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return canNext;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    return null;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    ModelWindowFactory factory =
        (ModelWindowFactory) editorList.getSelectedValue();

    canNext = factory != null;
    fireStatesChanged();
  }

}
