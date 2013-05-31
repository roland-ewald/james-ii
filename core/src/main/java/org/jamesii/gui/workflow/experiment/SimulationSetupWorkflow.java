/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.net.URI;

import org.jamesii.core.base.Entity;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.experiment.ExperimentPerspective;
import org.jamesii.gui.wizard.AbstractWizardController;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardPage;

/**
 * The Class BasicWorkflow. This "workflow" is a simple way to setup certain
 * types of experiments/simulations. It allows to select a model, to select and
 * setup the special experiment type, to setup general experiment parameters, to
 * store the experiment, and to execute the experiment.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 * @author Jan Himmelspach
 */
public class SimulationSetupWorkflow extends AbstractWizardController {

  /**
   * Instantiates a new simulation setup workflow.
   */
  public SimulationSetupWorkflow() {
    super("loadModel");
    addWizardPage("loadModel", new ModelLoader());
    addWizardPage("chooseExperiment", new ExperimentChooser());
    addWizardPage("showEditor", new ExperimentSetup());
    addWizardPage("chooseInstrumenter", new InstrumenterSetup());
    addWizardPage("chooseDatasink", new DataSinkSetup());
  }

  @Override
  public void cancel(IWizard wizard, IWizardPage page) {
    // nothing to do, yet
  }

  @Override
  public void finish(IWizard wizard) {
    BaseExperiment exp = wizard.getValue(ExperimentSetup.EXPERIMENT);

    // add data sink setup
    DataStorageFactory dsf = wizard.getValue(DataSinkSetup.DATASINK_FACTORY);
    ParameterBlock block = wizard.getValue(DataSinkSetup.DATASINK_PARAMETERS);
    if (dsf != null) {
      exp.setDataStorageFactory(new ParameterizedFactory<>(
          dsf, block));
    }

    ModelInstrumenterFactory mf =
        wizard.getValue(InstrumenterSetup.MODEL_INSTRUMENTER_FACTORY);
    ComputationInstrumenterFactory sf =
        wizard.getValue(InstrumenterSetup.SIMULATION_INSTRUMENTER_FACTORY);
    // read already selected parameters
    ParameterBlock mBlock =
        wizard.getValue(InstrumenterSetup.MODEL_INSTRUMENTER_PARAMETERS);
    ParameterBlock sBlock =
        wizard.getValue(InstrumenterSetup.SIMULATION_INSTRUMENTER_PARAMETERS);

    if (sf != null) {
      exp.setComputationInstrumenterFactory(new ParameterizedFactory<>(
          sf, sBlock));
    }
    if (mf != null) {
      exp.setModelInstrumenterFactory(new ParameterizedFactory<>(
          mf, mBlock));
    }

    // SimSystem.getTempDirectory()
    Entity.report("Starting the experiment from the workflow");

    // read model from
    // ISymbolicModel<?> model = (ISymbolicModel<?>)
    // wizard.getValue("model");

    /** The model location (temporary file of model description). */
    URI modelLocation = null;

    modelLocation = (URI) wizard.getValue(ModelLoader.MODEL_URI);
    // Entity.report("Will use model " + modelLocation);
    // Entity.report("Will not use model " +
    // wizard.getValue("modellocation"));

    exp.setModelLocation(modelLocation);

    exp.setName("Tut. WF Exp.: " + Files.getFileName(modelLocation));

    // exp.setDefaultSimStopTime(1000);
    // exp.setStartComputationTasksPaused(true);
    // exp.setComputationTaskInterStepDelay(500);

    // let's run the experiment
    ExperimentPerspective.scheduleExperiment(exp, false);
  }

  @Override
  protected boolean finishable(String currentPage, IWizard wizard) {
    return true;
  }

}
