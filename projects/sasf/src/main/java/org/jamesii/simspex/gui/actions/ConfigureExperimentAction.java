/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.actions;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.experiments.steering.IExperimentSteerer;
import org.jamesii.core.experiments.steering.SteeredExperimentVariables;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.model.variables.IntVariable;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.simspex.exploration.ISimSpaceExplorer;
import org.jamesii.simspex.exploration.simple.SimpleSimSpaceExplorer;
import org.jamesii.simspex.gui.dialogs.ConfigureSimSpExWindow;


/**
 * Dummy action to configure a base experiment for simulation space exploration.
 * 
 * @author Roland Ewald
 * 
 */
public class ConfigureExperimentAction extends AbstractAction {

  /** The experiment to be used. */
  private final BaseExperiment exp;

  /** The configuration window. */
  private final ConfigureSimSpExWindow configWindow;

  /**
   * Default constructor.
   * 
   * @param experiment
   *          the experiment to be used
   * @param csspWin
   *          the window this action belongs to
   */
  public ConfigureExperimentAction(BaseExperiment experiment,
      ConfigureSimSpExWindow csspWin) {
    super("org.jamesii.simspex.experiment.configure", "Configure Exploration Experiment",
        new String[] { "" }, csspWin);
    exp = experiment;
    configWindow = csspWin;
  }

  @Override
  public void execute() {

    // Check whether the configuration of this experiment is meaningful
    ExperimentVariables expVars = exp.getExperimentVariables();
    if (expVars != null && ExperimentVariables.containsSteerer(expVars)) {
      ApplicationLogger
          .log(Level.INFO,
              "Will not configure experiment, it already contains a steerer variable.");
      configWindow.getSelectionTreeSet().generateFactoryCombinations();
      return;
    }

    // TODO add dialog for steerer selection and configuration
    ISimSpaceExplorer explorer =
        new SimpleSimSpaceExplorer(configWindow.getSelectionTreeSet());

    // TODO this should be editable via the GUI
    Set<BaseVariable<?>> expModelVars = explorer.getModelVariables();
    expModelVars.add(new IntVariable("numOfReactants", 5, 1, 10, 2));
    expModelVars.add(new IntVariable("numOfSpecies", 5, 1, 10, 2));
    expModelVars.add(new IntVariable("ReactPerSpecies", 2, 1, 5, 1));

    // Check whether the experiment is already set up with some other variables
    ExperimentVariables newExpVars = new ExperimentVariables();
    if (expVars != null) {
      newExpVars.setSubLevel(expVars);
    }

    List<IExperimentSteerer> steerers = new ArrayList<>();
    steerers.add(explorer);
    newExpVars.addVariable(new ExperimentSteererVariable<>(
        "SimSpExSteererVar", IExperimentSteerer.class, explorer,
        new SequenceModifier<>(steerers)));

    // The steerer's experiment variables are on the lowest level, they shall be
    // used for every defined setup
    newExpVars.getLowestSubLevel().setSubLevel(
        new SteeredExperimentVariables<>(
            IExperimentSteerer.class));
    exp.setExperimentVariables(newExpVars);
  }
}
