/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.joone.engine.Monitor;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;


/**
 * Performance predictor based on Joone's neural networks.
 * 
 * @author Roland Ewald
 * 
 */
public class JoonePerfPredictor implements IPerformancePredictor {

  /** Serialisation ID. */
  private static final long serialVersionUID = 9094466590403020248L;

  /** Reference to the trained neural network. */
  private NeuralNet neuralNet;

  /** Attribute names, defines their order regarding the networks input layer. */
  private String[] attributes;

  /**
   * Encoding mappings for all nominal attributes (access via corresponding
   * index in attributes list, nominal attributes are in front).
   */
  private List<Map<String, Double>> attributeEncodings;

  /** Input synapse to be used. */
  private MemoryInputSynapse inputSynapse = new MemoryInputSynapse();

  /** Output synapse to be used. */
  private MemoryOutputSynapse outputSynapse = new MemoryOutputSynapse();

  /**
   * Default constructor.
   * 
   * @param nNet
   *          trained neural network
   * @param attribs
   *          the list of attributes
   * @param nomAttribEncodings
   */
  public JoonePerfPredictor(NeuralNet nNet, List<String> attribs,
      List<Map<String, Double>> nomAttribEncodings) {
    neuralNet = nNet;
    attributes = attribs.toArray(new String[attribs.size()]);
    attributeEncodings = nomAttribEncodings;

    // Configure the neural net for prediction
    neuralNet.getInputLayer().addInputSynapse(inputSynapse);
    neuralNet.getOutputLayer().addOutputSynapse(outputSynapse);
    inputSynapse.setAdvancedColumnSelector("1-" + attributes.length);

    Monitor monitor = neuralNet.getMonitor();
    monitor.setTrainingPatterns(1);
    monitor.setTotCicles(1);
    monitor.setLearning(false);
  }

  @Override
  public double predictPerformance(Features features, Configuration config) {

    Map<String, Object> valueMap = new HashMap<String, Object>(features);
    valueMap.putAll(config);

    double[][] inputArray = new double[1][attributes.length];
    for (int i = 0; i < attributes.length; i++) {
      inputArray[0][i] =
          JooneUtils.calculateAttribute(attributes[i], valueMap,
              i < attributeEncodings.size() ? attributeEncodings.get(i) : null);
    }
    inputSynapse.setInputArray(inputArray);
    neuralNet.getMonitor().setSingleThreadMode(true);
    neuralNet.go(true);
    return outputSynapse.getNextPattern()[0];
  }
}
