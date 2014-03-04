/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;

import org.jamesii.perfdb.entities.IRuntimeConfiguration;

/**
 * Interface for descriptors of a {@link IRuntimeConfiguration}.
 * 
 * @author Roland Ewald
 * 
 */
public interface IRTCDescriptor {

  /**
   * Describe the runtime configuration.
   * 
   * @param config
   *          the runtime configuration
   * 
   * @return the description strings, always should have the same number of
   *         elements
   */
  String[] describe(IRuntimeConfiguration config);

  /**
   * Gets the header for the different description columns.
   * 
   * @return the header of null, if no specific headers should be written
   */
  String[] getHeader();

}
