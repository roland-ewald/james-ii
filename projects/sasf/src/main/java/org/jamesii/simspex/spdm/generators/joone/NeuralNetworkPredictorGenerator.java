/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.AbstractPredictorGenerator;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.joone.engine.Layer;
import org.joone.engine.LinearLayer;
import org.joone.engine.Monitor;
import org.joone.engine.NeuralNetEvent;
import org.joone.engine.NeuralNetListener;
import org.joone.engine.Synapse;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.StreamInputSynapse;
import org.joone.net.NeuralNet;

/**
 * Generates predictors based on neural networks. Input and output layer are
 * {@link LinearLayer} objects. All hidden layers are connected with the same
 * kind of {@link Synapse}. Various parameters (regarding
 * {@link org.joone.engine.Learner} etc.) can be set. Supports multi-threading.
 * 
 * @author Roland Ewald
 * 
 */
public class NeuralNetworkPredictorGenerator extends AbstractPredictorGenerator
    implements NeuralNetListener {

  /** Learner to be used. */
  private NNLearner learner;

  /** Type of hidden layer to be created. */
  private NNLayer layer;

  /**
   * Type of synapse between first hidden layer and last one (the others are of
   * type {@link org.joone.engine.FullSynapse}).
   */
  private NNSynapse synapse;

  /** Number of total training cycles. */
  private int cycles;

  /** Momentum of the learning. */
  private double momentum;

  /** Learning rate. */
  private double learningRate;

  /** Number of neurons per hidden layer. */
  private int hiddenLayerRows;

  /** Number of hidden layers to be created. */
  private int hiddenLayers;

  /** Flag to control use of multi-threading. */
  private boolean multiThreading;

  /**
   * Default constructor.
   * 
   * @param nnLearner
   *          learning algorithm to be used
   * @param nnLayer
   *          layer type (determines transfer functions etc.)
   * @param nnSynapse
   *          synapse type between (hidden) layers
   * @param neuronsPerLayer
   *          number of neurons per (hidden) layer
   * @param numOfLayers
   *          number of (hidden) layers
   * @param trainingCycles
   *          number of training cycles
   * @param moment
   *          momentum of learning algorithm
   * @param learnRate
   *          learning rate
   * @param multiThread
   *          flag to switch on multi-threading
   */
  public NeuralNetworkPredictorGenerator(NNLearner nnLearner, NNLayer nnLayer,
      NNSynapse nnSynapse, int neuronsPerLayer, int numOfLayers,
      int trainingCycles, double moment, double learnRate, boolean multiThread) {
    learner = nnLearner;
    layer = nnLayer;
    synapse = nnSynapse;
    hiddenLayerRows = neuronsPerLayer;
    hiddenLayers = numOfLayers;
    cycles = trainingCycles;
    momentum = moment;
    learningRate = learnRate;
    multiThreading = multiThread;
  }

  @Override
  public <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> trainingData, PerfTupleMetaData metaData) {

    int numOfAttributes = metaData.getNumOfAttributes();

    NeuralNet nn = createNeuralNetwork(numOfAttributes);

    configureLearning(trainingData, nn.getMonitor());

    // Create input synapse
    PerformanceTupleInputSynapse inpSynapse =
        new PerformanceTupleInputSynapse(trainingData, metaData);
    nn.getInputLayer().addInputSynapse(inpSynapse);
    if (numOfAttributes < 1) {
      throw new IllegalArgumentException(
          "the number of attributes must be > 0.");
    }
    inpSynapse.setAdvancedColumnSelector(numOfAttributes == 1 ? "1" : "1-"
        + numOfAttributes);

    trainNetwork(nn, inpSynapse);

    return new JoonePerfPredictor(nn, inpSynapse.getAttributes(),
        inpSynapse.getNomAttribEncodings());
  }

  /**
   * Creates neural network.
   * 
   * @param numAttribs
   *          number of input values (determines size of input layer)
   * @return neural network
   */
  protected NeuralNet createNeuralNetwork(int numAttribs) {

    NeuralNet nn = new NeuralNet();
    List<Layer> nnLayers = new ArrayList<>();
    LinearLayer inputLayer = new LinearLayer();
    inputLayer.setRows(numAttribs);
    nnLayers.add(inputLayer);

    // Create network structure iteratively
    Layer lastLayer = inputLayer;
    for (int i = 0; i < hiddenLayers; i++) {

      Layer currentLayer = NNLayer.newInstance(layer);
      currentLayer.setRows(hiddenLayerRows);

      Synapse currentSynapse = NNSynapse.newInstance(synapse);

      lastLayer.addOutputSynapse(currentSynapse);
      currentLayer.addInputSynapse(currentSynapse);

      nnLayers.add(currentLayer);
      lastLayer = currentLayer;
    }
    Synapse synapseToOutLayer = NNSynapse.newInstance(synapse);
    lastLayer.addOutputSynapse(synapseToOutLayer);

    LinearLayer outputLayer = new LinearLayer();
    outputLayer.addInputSynapse(synapseToOutLayer);
    outputLayer.setRows(1);

    //Add Layers to neural network (doing it afterwards prevents Joone warnings)
    nn.addLayer(nnLayers.remove(0), NeuralNet.INPUT_LAYER);
    for (Layer hiddenLayer : nnLayers) {
      nn.addLayer(hiddenLayer, NeuralNet.HIDDEN_LAYER);
    }
    nn.addLayer(outputLayer, NeuralNet.OUTPUT_LAYER);
    return nn;
  }

  /**
   * Configure learning setup.
   * 
   * @param trainingData
   *          available training data
   * @param mon
   *          the neural network's monitor
   */
  protected void configureLearning(
      List<? extends PerformanceTuple> trainingData, Monitor mon) {
    mon.setLearningRate(learningRate);
    mon.setMomentum(momentum);
    mon.addLearner(0, learner.toString());
    mon.setTrainingPatterns(trainingData.size());
    mon.setTotCicles(cycles);
    mon.setLearning(true);
    mon.setBatchSize(trainingData.size());
    mon.setSingleThreadMode(!multiThreading);
    mon.addNeuralNetListener(this);
  }

  /**
   * Trains neural network.
   * 
   * @param net
   *          the neural network to be trained
   * @param inpSynapse
   *          the input synapse holding the training data
   */
  protected void trainNetwork(NeuralNet net,
      PerformanceTupleInputSynapse inpSynapse) {
    TeachingSynapse trainer = new TeachingSynapse();
    StreamInputSynapse desiredOutput = inpSynapse.createDesiredOutputSynapse();
    desiredOutput.setAdvancedColumnSelector("1");
    trainer.setDesired(desiredOutput);
    net.getOutputLayer().addOutputSynapse(trainer);
    net.setTeacher(trainer);
    net.go(true);

    // Clean up
    net.setTeacher(null);
    net.getInputLayer().removeInputSynapse(inpSynapse);
    net.getOutputLayer().removeOutputSynapse(trainer);
  }

  @Override
  public void cicleTerminated(NeuralNetEvent e) {
    SimSystem.report(Level.FINEST, "Cycle terminated");
  }

  @Override
  public void errorChanged(NeuralNetEvent e) {
    SimSystem
        .report(
            Level.FINEST,
            "Error changed: (R)MSE = "
                + ((Monitor) e.getSource()).getGlobalError());
  }

  @Override
  public void netStarted(NeuralNetEvent e) {
    SimSystem.report(Level.FINEST, "Net started");
  }

  @Override
  public void netStopped(NeuralNetEvent e) {
    SimSystem.report(Level.FINEST, "Net stopped");
  }

  @Override
  public void netStoppedError(NeuralNetEvent e, String error) {
    SimSystem.report(Level.FINEST, "Net stopped with error:" + error);
  }
}
