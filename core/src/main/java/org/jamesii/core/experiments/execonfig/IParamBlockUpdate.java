/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.io.Serializable;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * Interface for all kinds of updating a parameter block. Used to configure the
 * execution automatically.
 * 
 * @author Roland Ewald
 * 
 */
public interface IParamBlockUpdate extends Serializable {

  /**
   * Updates given {@link ParameterBlock} and children. May add/removed
   * arbitrary content.
   * 
   * @param paramBlock
   *          the parameter block to be updated
   */
  void update(ParameterBlock paramBlock);

}
