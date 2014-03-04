/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.features.plugintype;

import org.jamesii.asf.spdm.Features;

/**
 * Extracts feature to be associated with
 * {@link org.jamesii.gui.application.IApplication}.
 * 
 * @see org.jamesii.gui.application.IApplication
 * 
 * @author Roland Ewald
 */
public interface IFeatureExtractor<P> {

  /**
   * Extracts feature from a problem representation.
   * 
   * @param problemRepresentation
   *          the problem representation (domain-specific)
   * @return the extracted features
   */
  Features extractFeatures(P problemRepresentation);
}
