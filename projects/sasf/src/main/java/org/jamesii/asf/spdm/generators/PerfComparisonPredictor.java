/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import org.jamesii.asf.spdm.Features;

/**
 * Super class for {@link IPerfComparisonPredictor} implementations.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PerfComparisonPredictor implements
    IPerfComparisonPredictor {

  /** Serialisation ID. */
  private static final long serialVersionUID = -3025183106609337948L;

  /** Features of the current simulation problem. */
  private Features features = null;

  @Override
  public void setFeatures(Features feats) {
    features = feats;
  }

  protected Features getFeatures() {
    return features;
  }

}
