/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.model.features;

import org.jamesii.asf.integrationtest.bogus.application.model.IBogusModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.features.plugintype.ModelFeatureExtractor;
import org.jamesii.perfdb.recording.features.plugintype.ModelFeatureExtractorFactory;


/**
 * Feature extractor factory.
 * 
 * @author Roland Ewald
 */
public class BogusModelFeatureExtractorFactory extends
    ModelFeatureExtractorFactory<IBogusModel> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8380516539199748919L;

  /**
   * Instantiates a new bogus model feature extractor factory.
   */
  public BogusModelFeatureExtractorFactory() {
    super(IBogusModel.class);
  }

  @Override
  public ModelFeatureExtractor<IBogusModel> create(ParameterBlock params) {
    return new BogusModelFeatureExtractor();
  }

  @Override
  public String getFeatureName() {
    return "Bogus Model Features";
  }

  @Override
  public String getFeatureDescription() {
    return "Bogus features for ASF test model.";
  }

}
