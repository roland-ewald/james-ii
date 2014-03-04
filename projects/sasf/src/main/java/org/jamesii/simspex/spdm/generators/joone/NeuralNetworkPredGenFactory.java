/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for neural network predictor generator.
 * 
 * @author Roland Ewald
 * 
 */
public class NeuralNetworkPredGenFactory extends
    PerformancePredictorGeneratorFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -9130163893232188456L;

  /**
   * Learner selection, encoded. Type: {@link Integer}, default is 1 (
   * {@link org.joone.engine.BasicLearner}, see {@link NNLearner} ).
   */
  public static final String LEARNER_TYPE = "learnerType";

  /**
   * Layer selection, encoded. Type: {@link Integer}, default is 1 (
   * {@link org.joone.engine.LinearLayer}, see {@link NNLayer} ).
   */
  public static final String LAYER_TYPE = "layerType";

  /**
   * Synapse selection, encoded. Type: {@link Integer}, default is one (
   * {@link org.joone.engine.FullSynapse}, see {@link NNSynapse}).
   */
  public static final String SYNAPSE_TYPE = "synapseType";

  /**
   * Neurons per layer. Type: {@link Integer}, default is 10.
   */
  public static final String NEURONS_PER_LAYER = "neuronsPerLayer";

  /**
   * Number of hidden layers. Type: {@link Integer}, default is 1.
   */
  public static final String NUM_OF_LAYERS = "numOfLayers";

  /**
   * Number of training cycles. Type: {@link Integer}, default is 100.
   */
  public static final String TRAINING_CYCLES = "trainingCycles";

  /**
   * Momentum of neural network learner. Momentum allows that previous weight
   * changes are taken into account as well (scaled by a concrete factor, which
   * is this one). Type: {@link Double}, should be in [0,1]. Default is 0.5.
   */
  public static final String MOMENTUM = "momentum";

  /**
   * Learning rate of the neural network learner. This is a factor that is
   * multiplied by the error term before this is used to update the weights, so
   * it should be in [0,1]. Type: {@link Double}, default is 1.
   */
  public static final String LEARNING_RATE = "learningRate";

  /**
   * Flag that allows to switch on multi-threading support in Joone (seems to
   * result in considerable slow-down when used for training small networks, use
   * carefully).
   */
  public static final String ENABLE_MULTI_THREADING = "multiThreadingEnabled";

  /** The default value for enabling multithreading. */
  private static final boolean DEFAULT_VAL_MULTITHREADING_ENABLED = false;

  /** The default learning rate. */
  private static final int DEFAULT_VAL_LEARNING_RATE = 1;

  /** The default momentum. */
  private static final double DEFAULT_VAL_MOMENTUM = 0.5;

  /** The default training cycles. */
  private static final int DEFAULT_VAL_TRAINING_CYCLES = 100;

  /** The default number of layers. */
  private static final int DEFAULT_VAL_NUM_LAYERS = 5;

  /** The default number of neurons per layer. */
  private static final int DEFAULT_VAL_NERUONS_PER_LAYER = 20;

  /** The default type of synapse. */
  private static final int DEFAULT_VAL_SYNAPSE_TYPE = 0;

  /** The default type of layer. */
  private static final int DEFAULT_VAL_LAYER_TYPE = 0;

  /** The default typpe of learner. */
  private static final int DEFAULT_VAL_LEARNER_TYPE = 2;

  @Override
  public IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example) {
    return new NeuralNetworkPredictorGenerator(NNLearner.getLearnerType(params
        .getSubBlockValue(LEARNER_TYPE, DEFAULT_VAL_LEARNER_TYPE)),
        NNLayer.getLayerType(params.getSubBlockValue(LAYER_TYPE,
            DEFAULT_VAL_LAYER_TYPE)), NNSynapse.getSynapseType(params
            .getSubBlockValue(SYNAPSE_TYPE, DEFAULT_VAL_SYNAPSE_TYPE)),
        params.getSubBlockValue(NEURONS_PER_LAYER,
            DEFAULT_VAL_NERUONS_PER_LAYER), params.getSubBlockValue(
            NUM_OF_LAYERS, DEFAULT_VAL_NUM_LAYERS), params.getSubBlockValue(
            TRAINING_CYCLES, DEFAULT_VAL_TRAINING_CYCLES),
        params.getSubBlockValue(MOMENTUM, DEFAULT_VAL_MOMENTUM),
        params.getSubBlockValue(LEARNING_RATE, DEFAULT_VAL_LEARNING_RATE),
        params.getSubBlockValue(ENABLE_MULTI_THREADING,
            DEFAULT_VAL_MULTITHREADING_ENABLED));
  }
}
