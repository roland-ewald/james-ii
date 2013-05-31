/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.io.Serializable;

/**
 * This interface defines no strong demands on its implementers, since
 * practically any object can become acceptable knowledge. It's necessary anyway
 * to have a base interface for a common method to add accepted knowledge to a
 * parallel optimiser. A name is needed as well.
 * 
 * @author Peter Sievert
 */
public interface IAcceptedOptimizationKnowledge extends Serializable {

  /**
   * Give name.
   * 
   * @return the name
   */
  String giveName();

}
