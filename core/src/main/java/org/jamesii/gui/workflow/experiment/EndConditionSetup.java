/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * The Class EndConditionSetup.
 * 
 * @author Jan Himmelspach
 */
public class EndConditionSetup extends AbstractWizardPage {

  /** The page. */
  private JPanel page;

  /**
   * Instantiates a new model loader editor.
   */
  public EndConditionSetup() {
    super("Replications",
        "Setup the replications to be made per parameter combination of the experiment");
  }

  @Override
  protected JComponent createPage() {
    page = new JPanel();
    page.setLayout(new BorderLayout());

    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean canBack(IWizard wizard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return true;// ((modelLocation.getText() != null) &&
                // (!modelLocation.getText().isEmpty()));
  }

  @Override
  public IWizardHelpProvider getHelp() {
    // TODO Auto-generated method stub
    return null;
  }

}
