/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;
import org.jamesii.gui.workflow.experiment.plugintype.ExperimentSetupEditorFactory;
import org.jamesii.gui.workflow.experiment.plugintype.IExperimentSetup;

/**
 * @author Stefan Rybacki
 */
public class ExperimentSetup extends AbstractWizardPage {
  static final String EXPERIMENT = "base_experiment";

  private JTabbedPane page;

  private IExperimentSetup editor;

  private BaseExperiment experiment;

  // FIXME sr137: give the editor the option to say next available or
  // next not available

  public ExperimentSetup() {
    super("Experiment Setup", "Edit Experiment specific Parameters");
  }

  @Override
  protected JComponent createPage() {
    return page =
        new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
  }

  @Override
  protected void persistData(IWizard wizard) {
    editor.setupExperiment(experiment);
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    fireNextChanged();
    // load editor factory from wizard storage and put it on page
    final ExperimentSetupEditorFactory experimentFactory =
        wizard.getValue(ExperimentChooser.EXPERIMENT);

    // load symbolic model
    final IModel model = wizard.getValue(ModelLoader.MODEL);
    experiment = wizard.getValue(EXPERIMENT);
    if (experiment == null) {
      wizard.putValue(EXPERIMENT, experiment = new BaseExperiment());
    }

    ParameterBlock params =
        new ParameterBlock(model, ExperimentSetupEditorFactory.MODEL);

    page.removeAll();
    editor = experimentFactory.create(params);
    editor.setupFromExperiment(experiment);
    for (int i = 0; i < editor.getPageCount(); i++) {
      page.addTab(editor.getPageTitle(i), editor.getPage(i));
    }
  }

  @Override
  public Icon getPageIcon() {
    return null;
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
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(640, 400);
  }

}
