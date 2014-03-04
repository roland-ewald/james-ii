/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;

import id3.ID3Inducer;
import shared.Categorizer;
import shared.InstanceList;

/**
 * Predictor that uses ID3 (a decision trees algorithm, predecessor of C4.5).
 * 
 * @author Roland Ewald
 * 
 */
public class ID3PredictorGenerator extends MLJPredictorGenerator {

  /**
   * Instantiates a new ID3 predictor generator.
   * 
   * @param numPerfClasses
   *          the number of performance classes
   */
  public ID3PredictorGenerator(int numPerfClasses) {
    super(numPerfClasses);
  }

  @Override
  public Categorizer getCategorizer(InstanceList instances) {
    ID3Inducer id3 = new ID3Inducer("ID3PerfClassSelector");
    id3.assign_data(instances);
    id3.train();
    id3.display_struct();
    return id3.get_categorizer();
  }
}
