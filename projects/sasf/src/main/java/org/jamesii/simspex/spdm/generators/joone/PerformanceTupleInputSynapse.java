/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.joone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.joone.io.MemoryInputSynapse;


/**
 * Input synapse that transforms performance tuples into Joone-compatible
 * double-arrays.
 * 
 * @author Roland Ewald
 * 
 */
public class PerformanceTupleInputSynapse extends MemoryInputSynapse {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1052927638037434410L;

  /** List of performance tuples used for input. */
  private final List<PerformanceTuple> input =
      new ArrayList<>();

  /** Stores configuration performance for corresponding performance tuple. */
  private final double[][] performances;

  /** Meta-data on performance tuples. */
  private final PerfTupleMetaData metaData;

  /** Input patterns. */
  private final double[][] inputData;

  /**
   * Order of the attributes. Nominal attributes first (to facilitate
   * implementation)
   */
  private final List<String> attributes;

  /** Nominal attribute encodings (ordered as defined by attributes list). */
  private final List<Map<String, Double>> nomAttribEncodings;

  /**
   * Default constructor.
   * 
   * @param performanceData
   *          that data to be fed into the neural network
   * @param metaDat
   *          meta-data concerning input tuples
   */
  public PerformanceTupleInputSynapse(
      List<? extends PerformanceTuple> performanceData,
      PerfTupleMetaData metaDat) {
    input.addAll(performanceData);
    metaData = metaDat;

    Map<String, Set<String>> nominalAttribs = metaData.getNominalAttribs();
    attributes = new ArrayList<>(nominalAttribs.keySet());
    nomAttribEncodings = getNomAttribEncodings(nominalAttribs, attributes);
    attributes.addAll(metaData.getNumericAttribs());

    int numOfTuples = input.size();
    int numOfAttributes = attributes.size();
    inputData = new double[numOfTuples][numOfAttributes];
    performances = new double[numOfTuples][1];
    constructNNData();
  }

  /**
   * Constructs data compatible with Joone's neural networks.
   */
  final void constructNNData() {
    for (int i = 0; i < input.size(); i++) {
      PerformanceTuple perfTuple = input.get(i);
      Map<String, Object> tupleAttribs = perfTuple.getAllAttributes();
      for (int j = 0; j < attributes.size(); j++) {
        inputData[i][j] =
            JooneUtils.calculateAttribute(attributes.get(j), tupleAttribs,
                j < nomAttribEncodings.size() ? nomAttribEncodings.get(j)
                    : null);
      }
      performances[i][0] = perfTuple.getPerformance();
    }
    setInputArray(inputData);
  }

  /**
   * Retrieve mappings for encoding nominal attributes: value->code. The
   * encoding starts with 1 (-1 is used to encode missing values).
   * 
   * @param nominalAttribs
   *          mapping of nominal attributes to the set of the values each may
   *          have
   * @param attribs
   *          list of (nominal) attributes
   * @return the nom attrib encodings
   */
  private List<Map<String, Double>> getNomAttribEncodings(
      Map<String, Set<String>> nominalAttribs, List<String> attribs) {
    // Create real values for all nominal attributes
    List<Map<String, Double>> result = new ArrayList<>();
    for (String attributeName : attribs) {
      Set<String> possibiities = nominalAttribs.get(attributeName);
      if (possibiities != null) {
        Map<String, Double> nomAttribMapping = new HashMap<>();
        int classIndex = 1;
        for (String possibility : possibiities) {
          nomAttribMapping.put(possibility, (double) classIndex);
          classIndex++;
        }
        result.add(nomAttribMapping);
      } else {
        break; // numeric attributes have been appended afterwards
      }
    }
    return result;
  }

  /**
   * Creates input synapse that contains the desired outputs.
   * 
   * @return memory synapse holding the desired output
   */
  public MemoryInputSynapse createDesiredOutputSynapse() {
    MemoryInputSynapse synapse = new MemoryInputSynapse();
    synapse.setInputArray(performances);
    return synapse;
  }

  /**
   * Gets the performances.
   * 
   * @return the performances
   */
  public double[][] getPerformances() {
    return performances;
  }

  /**
   * Gets the meta data.
   * 
   * @return the meta data
   */
  public PerfTupleMetaData getMetaData() {
    return metaData;
  }

  /**
   * Gets the input data.
   * 
   * @return the input data
   */
  public double[][] getInputData() {
    return inputData;
  }

  /**
   * Gets the attributes.
   * 
   * @return the attributes
   */
  public List<String> getAttributes() {
    return attributes;
  }

  /**
   * Gets the encodings of the nominal attributes.
   * 
   * @return the nominal attribute encodings
   */
  public List<Map<String, Double>> getNomAttribEncodings() {
    return nomAttribEncodings;
  }

}
