/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.core.model.formalism.Formalism;

/**
 * The formalism Class CA. Used to describe this "rule based" possibility to
 * define "simple" cellular automata.
 * 
 * @author Mathias Süß
 * @author Jan Himmelspach
 */
public class CA extends Formalism {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2637548894821384262L;

  /**
   * Create the default formalism description.
   */
  public CA() {
    super(
        "CARB",
        "CARB",
        "Cellular Automata (Rule-Based)",
        "Rule-Based Cellular Automata. Rule based language to setup 1D/2D cellular automata with an arbitrary neighbourhood definition.",
        TimeBase.DISCRETE, SystemSpecification.DISCRETE, TimeProgress.STEPWISE);
  }

}
