/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.model.features;

import org.jamesii.asf.integrationtest.bogus.application.model.IBogusModel;
import org.jamesii.asf.spdm.Features;
import org.jamesii.perfdb.recording.features.plugintype.ModelFeatureExtractor;



/**
 * Simple model feature extractor.
 * 
 * @author Roland Ewald
 */
public class BogusModelFeatureExtractor extends
    ModelFeatureExtractor<IBogusModel> {

  @Override
  protected Features extractModelFeatures(IBogusModel model) {
    Features modelFeatures = new Features();
    modelFeatures.putAll(model.getContent());
    return modelFeatures;
  }

}
