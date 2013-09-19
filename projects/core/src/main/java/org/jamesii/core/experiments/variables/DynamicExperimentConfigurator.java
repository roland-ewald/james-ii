/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * This is a helper class for
 * {@link org.jamesii.core.experiments.BaseExperiment}, which manages all
 * additional configuration options that can be defined via instances of
 * {@link ExperimentVariables}. For example: additional model and computation
 * instrumenters, additional replication criteria, and so on.
 * 
 * @author Roland Ewald
 */
public class DynamicExperimentConfigurator {

  /** List of all model instrumenter factories and parameters. */
  private Map<ModelInstrumenterFactory, ParameterBlock> modelInstrumenterFactories =
      new HashMap<>();

  /** List of all computation instrumenter factories and parameters. */
  private Map<ComputationInstrumenterFactory, ParameterBlock> computationInstrumenterFactories =
      new HashMap<>();

  /** The replication criteria defined within experiment variables. */
  private List<ParameterizedFactory<RepCriterionFactory>> repCriterionFactories =
      new ArrayList<>();

  /** The computation stop factory. */
  private ComputationTaskStopPolicyFactory compStopFactory = null;

  /** The computation stop parameters. */
  private ParameterBlock stopParameters = null;

  /** The execution parameters. */
  private final ParameterBlock executionParameters;

  /** The model parameters. */
  private final Map<String, Object> modelParameters;

  /**
   * Instantiates a new dynamic experiment configurator.
   * 
   * @param execParams
   *          the exec params
   * @param modelParams
   *          the model params
   */
  public DynamicExperimentConfigurator(ParameterBlock execParams,
      Map<String, Object> modelParams) {
    executionParameters = execParams;
    modelParameters = modelParams;
  }

  /**
   * Configures fields with given experiment variables.
   * 
   * @param expVariables
   *          the experiment variables
   */
  public void configureWith(ExperimentVariables expVariables) {

    if (expVariables == null) {
      return;
    }

    expVariables.storeSettingToMap(modelParameters, executionParameters);

    ParameterBlock experimentParameters =
        expVariables.getExperimentParameters();

    if (experimentParameters == null) {
      return;
    }

    configureInstrumenters(experimentParameters);
    configureReplicationCriteria(experimentParameters);
    configureStopParameters(experimentParameters);
  }

  /**
   * Configure replication criterion factories.
   * 
   * @param experimentParameters
   *          the experiment parameters
   */
  private void configureReplicationCriteria(ParameterBlock experimentParameters) {
    repCriterionFactories =
        experimentParameters.getSubBlockValue(
            ExperimentVariables.REPLICATION_CRITERION_FACTORIES,
            new ArrayList<ParameterizedFactory<RepCriterionFactory>>());
  }

  /**
   * Configure instrumenters.
   * 
   * @param experimentParameters
   *          the experiment parameters
   */
  private void configureInstrumenters(ParameterBlock experimentParameters) {
    modelInstrumenterFactories =
        experimentParameters.getSubBlockValue(
            ExperimentVariables.MODEL_INSTRUMENTER_FACTORIES,
            new HashMap<ModelInstrumenterFactory, ParameterBlock>());
    computationInstrumenterFactories =
        experimentParameters.getSubBlockValue(
            ExperimentVariables.SIMULATION_INSTRUMENTER_FACTORIES,
            new HashMap<ComputationInstrumenterFactory, ParameterBlock>());
  }

  /**
   * Configure stop parameters.
   * 
   * @param experimentParameters
   *          the experiment parameters
   */
  private void configureStopParameters(ParameterBlock experimentParameters) {
    ParameterBlock stopParams =
        experimentParameters.getSubBlock(ExperimentVariables.STOP_TIME_FACTORY);
    if (stopParams != null) {
      compStopFactory =
          (ComputationTaskStopPolicyFactory) SimSystem.getRegistry()
              .getFactory((String) stopParams.getValue());
      stopParameters = stopParams;
    }
  }

  /**
   * Gets the stop factory.
   * 
   * @return the stop factory
   */
  public ComputationTaskStopPolicyFactory getStopFactory() {
    return this.compStopFactory;
  }

  /**
   * Gets the stop parameters.
   * 
   * @return the stop parameters
   */
  public ParameterBlock getStopParameters() {
    return stopParameters;
  }

  /**
   * Gets the model instrumenters.
   * 
   * @return the model instrumenters
   */
  public Map<ModelInstrumenterFactory, ParameterBlock> getModelInstrumenterFactories() {
    return this.modelInstrumenterFactories;
  }

  /**
   * Gets the computation instrumenters.
   * 
   * @return the computation instrumenters
   */
  public Map<ComputationInstrumenterFactory, ParameterBlock> getComputationInstrumenterFactories() {
    return this.computationInstrumenterFactories;
  }

  /**
   * Gets the list of replication criterion factories.
   * 
   * @return the replication criterion factories
   */
  public List<ParameterizedFactory<RepCriterionFactory>> getReplicationCriterionFactories() {
    return repCriterionFactories;
  }

}
