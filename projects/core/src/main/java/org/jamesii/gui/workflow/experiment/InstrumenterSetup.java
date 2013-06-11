/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.AbstractComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.AbstractModelInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;
import org.jamesii.gui.wizard.AbstractWizardPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * @author Stefan Rybacki
 */
public class InstrumenterSetup extends AbstractWizardPage {
  private JComponent page;

  private ConfigureFactoryPanel<ModelInstrumenterFactory> mConfigurator;

  private ConfigureFactoryPanel<ComputationInstrumenterFactory> sConfigurator;

  public static final String MODEL_INSTRUMENTER_FACTORY = "model_ins_factory";

  public static final String MODEL_INSTRUMENTER_PARAMETERS =
      "model_ins_parameters";

  public static final String SIMULATION_INSTRUMENTER_FACTORY =
      "sim_ins_factory";

  public static final String SIMULATION_INSTRUMENTER_PARAMETERS =
      "sim_ins_parameters";

  @Override
  protected JComponent createPage() {
    page = new JPanel();
    page.setLayout(new BorderLayout());

    return page;
  }

  @Override
  protected void persistData(IWizard wizard) {
    wizard.putValue(MODEL_INSTRUMENTER_FACTORY, mConfigurator
        .getSelectedFactoryAndParameter().getFactory());
    wizard.putValue(MODEL_INSTRUMENTER_PARAMETERS, mConfigurator
        .getSelectedFactoryAndParameter().getParameters());

    wizard.putValue(SIMULATION_INSTRUMENTER_FACTORY, sConfigurator
        .getSelectedFactoryAndParameter().getFactory());
    wizard.putValue(SIMULATION_INSTRUMENTER_PARAMETERS, sConfigurator
        .getSelectedFactoryAndParameter().getParameters());
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
    page.removeAll();
    // read already selected factory
    ModelInstrumenterFactory mf = wizard.getValue(MODEL_INSTRUMENTER_FACTORY);
    ComputationInstrumenterFactory sf =
        wizard.getValue(SIMULATION_INSTRUMENTER_FACTORY);
    // read already selected parameters
    ParameterBlock mBlock = wizard.getValue(MODEL_INSTRUMENTER_PARAMETERS);
    ParameterBlock sBlock = wizard.getValue(SIMULATION_INSTRUMENTER_PARAMETERS);

    List<ModelInstrumenterFactory> mFactories = new ArrayList<>();
    List<ComputationInstrumenterFactory> sFactories = new ArrayList<>();
    try {
      ParameterBlock block =
          new ParameterBlock(wizard.getValue(ModelLoader.MODEL_URI),
              AbstractModelInstrumenterFactory.MODELURI);

      mFactories =
          SimSystem.getRegistry().getFactoryOrEmptyList(
              AbstractModelInstrumenterFactory.class, block);

      block =
          new ParameterBlock(wizard.getValue(ModelLoader.MODEL_URI),
              AbstractComputationInstrumenterFactory.MODELURI);
      sFactories =
          SimSystem.getRegistry().getFactoryOrEmptyList(
              AbstractComputationInstrumenterFactory.class, block);
    } catch (Exception e) {
      SimSystem.report(e);
    }

    mConfigurator =
        new ConfigureFactoryPanel<>(mFactories, "Select Model Instrumenter",
            mf, mBlock);
    sConfigurator =
        new ConfigureFactoryPanel<>(sFactories,
            "Select Simulation Instrumenter", sf, sBlock);

    page.setLayout(new GridLayout(1, 2));
    page.add(mConfigurator);
    page.add(sConfigurator);
    page.invalidate();
    page.repaint();
  }

  @Override
  public boolean canBack(IWizard wizard) {
    return false;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return true;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    // TODO Auto-generated method stub
    return null;
  }

}
