/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca;

import org.jamesii.core.model.formalism.Formalism;

/**
 * The Class CA.
 */
public class CA extends Formalism {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9051668206460449179L;

  /**
   * Instantiates a new cA.
   */
  public CA() {
    super("CA", "CA", "Cellular Automata", "Simple Cellular Automata",
        TimeBase.DISCRETE, SystemSpecification.DISCRETE, TimeProgress.STEPWISE);
  }

}
