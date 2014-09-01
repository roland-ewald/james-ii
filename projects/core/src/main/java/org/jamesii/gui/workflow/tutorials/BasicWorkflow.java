/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.tutorials;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.AbstractComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.AbstractModelInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.misc.Files;
import org.jamesii.gui.experiment.ExperimentPerspective;
import org.jamesii.gui.utils.factories.FactorySelectionDialog;
import org.jamesii.gui.wizard.AbstractWizardController;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardPage;

/**
 * The Class BasicWorkflow. This "workflow" is a tutorial workflow -- it allows
 * to select a modeling formalism, to load/create a model, to edit one set of
 * parameters for this model, and to run this model in a single simulation run.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 */
public class BasicWorkflow extends AbstractWizardController {

  /**
   * The Constant FORMALISM_PAGE.
   */
  private static final String FORMALISM_PAGE = "formalism";

  /**
   * The Constant EDITORSELECTION_PAGE.
   */
  private static final String EDITORSELECTION_PAGE = "selectEditor";

  /**
   * The Constant MODELEDITOR_PAGE.
   */
  private static final String MODELEDITOR_PAGE = "createModel";

  /**
   * The Constant PARAMETEREDITOR_PAGE.
   */
  private static final String PARAMETEREDITOR_PAGE = "editParameters";

  /**
   * Instantiates a new basic workflow.
   */
  public BasicWorkflow() {
    super(FORMALISM_PAGE);
    addWizardPage(FORMALISM_PAGE, new FormalismChooser());
    addWizardPage(EDITORSELECTION_PAGE, new EditorSelector());
    addWizardPage(MODELEDITOR_PAGE, new ModelEditor());
    addWizardPage(PARAMETEREDITOR_PAGE, new ModelParameterEditor());
  }

  @Override
  public void cancel(IWizard wizard, IWizardPage page) {
    // nothing to do, yet
  }

  @Override
  public synchronized String getNextPage(String currentPage, IWizard wizard) {
    if (currentPage.equals(FORMALISM_PAGE)) {
      // if there is already an editor in the wizard store skip editor
      // selection
      if (wizard.getValue(EditorSelector.EDITOR) != null) {
        return MODELEDITOR_PAGE;
      }
    }

    return super.getNextPage(currentPage, wizard);
  }

  @Override
  public void finish(IWizard wizard) {
    BaseExperiment exp = new BaseExperiment();

    // SimSystem.getTempDirectory()

    SimSystem.report(Level.INFO, "Starting the experiment from the workflow");

    // read model from
    // ISymbolicModel<?> model = (ISymbolicModel<?>) wizard.getValue("model");

    /** The model location (temporary file of model description). */
    URI modelLocation = null;

    modelLocation = wizard.getValue(ModelEditor.MODEL_LOCATION);
    // Entity.report("Will use model " + modelLocation);
    // Entity.report("Will not use model " + wizard.getValue("modellocation"));

    Map<String, ?> state =
        wizard.getValue(ModelParameterEditor.MODEL_PARAMETERS);
    Object user = wizard.getValue(ModelParameterEditor.USER_PARAMETERS);

    exp.setModelLocation(modelLocation);

    exp.setName("Tut. WF Exp.: " + Files.getFileName(modelLocation));

    exp.setExperimentVariables(new ExperimentVariables());

    ArrayList<Object> al = new ArrayList<>();
    al.add(state);

    // set model parameters
    if (state != null) {
      exp.getFixedModelParameters().putAll(state);
    }

    al = new ArrayList<>();
    al.add(user);
    exp.getExperimentVariables().addVariable(
        new ExperimentVariable<>("user.parameters",
            new SequenceModifier<>(al)));

    ParameterBlock afp = new ParameterBlock();
    afp.addSubBlock(AbstractModelInstrumenterFactory.MODELURI, modelLocation);

    List<ModelInstrumenterFactory> modelInstrFacs = null;
    List<ComputationInstrumenterFactory> simInstrFacs = null;
    boolean modelInstrumenter = true;
    boolean simInstumenter = true;
    try {
      modelInstrFacs =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelInstrumenterFactory.class, afp);
    } catch (NoFactoryFoundException nffe) {
      modelInstrumenter = false;
    }

    try {
      simInstrFacs =
          SimSystem.getRegistry().getFactoryList(
              AbstractComputationInstrumenterFactory.class, afp);
    } catch (NoFactoryFoundException nffe) {
      simInstumenter = false;
      SimSystem.report(Level.INFO, null, "No instrumenter found for model: "
          + modelLocation, null);
    }
    if (simInstumenter || modelInstrumenter) {
      List<Factory> instrFacs = new ArrayList<>();
      if (modelInstrFacs != null) {
        instrFacs.addAll(modelInstrFacs);
      }
      if (simInstrFacs != null) {
        instrFacs.addAll(simInstrFacs);
      }

      FactorySelectionDialog<Factory<?>> diag =
          new FactorySelectionDialog(null, instrFacs, null,
              "Select Instrumentation", true);
      diag.setVisible(true);
      if (diag.isOkButtonPressed()) {
        List<ParameterizedFactory<Factory<?>>> selected =
            diag.getSelectedFactoriesAndParameters();

        ParameterizedFactory<Factory<?>> sel = new ParameterizedFactory<>();
        
        if (selected.size() > 0) {
          sel = selected.get(0);
        }

        if (sel.getFactory() instanceof ModelInstrumenterFactory) {
          ModelInstrumenterFactory min =
              (ModelInstrumenterFactory) sel.getFactory();
          
          exp.setModelInstrumenterFactory(new ParameterizedFactory<>(min,sel.getParameters()));
        }

        if (sel.getFactory() instanceof ComputationInstrumenterFactory) {
          ComputationInstrumenterFactory min =
              (ComputationInstrumenterFactory) sel.getFactory();
          
          exp.setComputationInstrumenterFactory(new ParameterizedFactory<>(min,sel.getParameters()));
        }
      }
      // for (ModelInstrumenterFactory m : instrFacs) {
      // if (m.getClass().getName().compareTo(sel.toString()) == 0) min = m;
      // }

      // set the instrumenter factory, that's the factory which takes care of
      // recording the simulation run data
    } else {
      SimSystem.report(Level.INFO, null, "No instrumenter found for model: "
          + modelLocation, null);
    }

    ExperimentPerspective.configureExperimentWithUIDefaults(exp);

    // let's run the experiment
    ExperimentPerspective.scheduleExperiment(exp, false);
  }

}
