/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devs;

import org.jamesii.core.model.formalism.Formalism;

/**
 * The Class DEVSFormalism.
 * 
 * @author Jan Himmelspach
 */
public class DEVSFormalism extends Formalism {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7993088971676243589L;

  /**
   * Instantiates a new dEVS formalism.
   */
  public DEVSFormalism() {
    super("DEVS", "DEVS", "Discrete Event System Specification", "DEVS",
        TimeBase.CONTINUOUS, SystemSpecification.DISCRETE, TimeProgress.EVENT);
  }

}
