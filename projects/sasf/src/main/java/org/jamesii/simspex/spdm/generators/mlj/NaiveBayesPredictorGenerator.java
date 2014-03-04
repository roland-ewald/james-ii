/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;

import nb.NaiveBayesCat;
import shared.Categorizer;
import shared.InstanceList;

/**
 * Predictor generator for Naive Bayes classifier.
 * 
 * @see NaiveBayesCat
 * 
 * @author Roland Ewald
 * 
 */
public class NaiveBayesPredictorGenerator extends MLJPredictorGenerator {

  /**
   * Instantiates a new Naive Bayes predictor generator.
   * 
   * @param numPerfClasses
   *          the number of performance classes
   */
  public NaiveBayesPredictorGenerator(int numPerfClasses) {
    super(numPerfClasses);
  }

  @Override
  public Categorizer getCategorizer(InstanceList instances) {
    return new NaiveBayesCat("NaiveBayesMLJ", instances);
  }

}
