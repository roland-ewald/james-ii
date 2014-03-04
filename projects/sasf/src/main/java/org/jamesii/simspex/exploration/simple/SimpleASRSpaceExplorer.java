/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.simple;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.replication.RepNumberCriterionFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.simspex.exploration.ExplorationPhase;


/**
 * A simulation explorer that presumes the activity of an
 * {@link org.jamesii.simspex.adaptiverunner.AdaptiveComputationTaskRunner} instance below.
 * It does not explicitly explore the performance of specific setups (these are
 * only used for calibration) and configures the number of replications via the
 * experiment variables (within the exploration phase).
 * 
 * 
 * @see org.jamesii.simspex.adaptiverunner.AdaptiveComputationTaskRunner
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleASRSpaceExplorer extends SimpleSimSpaceExplorer {

  /** Flag to signal whether the exploration has started. */
  private boolean explorationStarted = false;

  /**
   * Instantiates a new space explorer.
   * 
   * @param configs
   *          the runtime configurations
   */
  public SimpleASRSpaceExplorer(List<ParameterBlock> configs) {
    super(configs);
  }

  @Override
  protected ParameterBlock nextConfigToExplore() {

    // Exploration will be done by the ASR
    if (!explorationStarted) {
      explorationStarted = true;
      return new ParameterBlock();
    }

    // In case exploration has already been done, just quit
    explorationStarted = false;
    return null;
  }

  @Override
  public ParameterBlock getExperimentParameters() {
    ParameterBlock paramBlock = super.getExperimentParameters();
    if (getPhase() == ExplorationPhase.EXPLORATION) {
      List<ParameterizedFactory<RepCriterionFactory>> repCriterionFactories =
          new ArrayList<>();
      repCriterionFactories.add(new ParameterizedFactory<RepCriterionFactory>(
          new RepNumberCriterionFactory(), new ParameterBlock().addSubBl(
              RepNumberCriterionFactory.NUM_REPS, getNumOfReplications())));
      paramBlock.addSubBlock(
          ExperimentVariables.REPLICATION_CRITERION_FACTORIES,
          repCriterionFactories);
    }
    return paramBlock;
  }
}
