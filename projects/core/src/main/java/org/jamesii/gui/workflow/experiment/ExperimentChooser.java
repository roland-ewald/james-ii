/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.FactoryListCellRenderer;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;
import org.jamesii.gui.workflow.experiment.plugintype.ExperimentSetupEditorFactory;

/**
 * Wizard page for the "Simulation" workflow. Allows to select the type of the
 * experiment / simulation to be setup.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 * @author Jan Himmelspach
 */
class ExperimentChooser extends AbstractWizardPage implements
    ListSelectionListener {

  /**
   * The Constant EXPERIMENT. The ID which is used to store the selected
   * experiment type/editor in the wizard's storage when persisting the data.
   */
  public static final String EXPERIMENT = "experiment";

  /**
   * list of formalisms
   */
  private JList list;

  /**
   * flag whether cancel can be used
   */
  private boolean canCancel = true;

  /**
   * flag activatingg/deactivating next button
   */
  private boolean canNext = false;

  /**
   * info label showing informations about the selected formalism
   */
  private JTextPane infoLabel;

  /**
   * Instantiates a new formalism chooser.
   */
  public ExperimentChooser() {
    super("Experiment/Simulation type selection",
        "Select an experiment type you want to setup");
  }

  @Override
  protected JComponent createPage() {
    // create a list with available formalisms
    list = new JList(new ExperimentTypeListModel());
    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
            && list.getSelectedValue() != null) {
          fireNext();
        }
      }
    });
    list.setCellRenderer(new FactoryListCellRenderer());
    list.addListSelectionListener(this);
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    infoLabel = new JTextPane();
    infoLabel.setEditable(false);

    JPanel panel = new JPanel(new GridLayout(0, 2));
    panel.add(new JScrollPane(list));
    panel.add(new JScrollPane(infoLabel));

    JPanel header = new JPanel(new GridLayout(0, 2));
    header.add(new JLabel("Available Experiments:"));
    header.add(new JLabel("Experiment Information:"));

    JPanel page = new JPanel(new BorderLayout());
    page.add(header, BorderLayout.PAGE_START);
    page.add(panel, BorderLayout.CENTER);

    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    // store selected experiment type/editor
    wizard.putValue(EXPERIMENT, list.getSelectedValue());
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    // forward if there is only one experiment to choose from
    if (list.getModel().getSize() == 1) {
      list.setSelectedIndex(0);
      canNext = true;
      fireNext();
    }
  }

  @Override
  public boolean canBack(IWizard wizard) {
    return false;
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
    try {
      final ExperimentSetupEditorFactory value =
          (ExperimentSetupEditorFactory) list.getSelectedValue();
      canNext = value != null;

      if (value != null) {
        // show information on the information text field
        infoLabel.setText(String.format("<html><h1>%s</h1>%s</html>",
            value.getReadableName(), value.getCompleteInfoString()));
        infoLabel.setContentType("text/html");
        infoLabel.setCaretPosition(0);
      } else {
        infoLabel.setText("");
      }
      fireStatesChanged();
    } catch (Exception ex) {
      infoLabel.setText("");
      SimSystem.report(ex);
    }
  }

}
