/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace;

import org.jamesii.core.model.formalism.Formalism;

/**
 * Formalism information for ML-Space
 *
 * @author Arne Bittig
 *
 */
public class MLSpaceFormalism extends Formalism {

  /** Serialization ID. */
  private static final long serialVersionUID = 3399037318647134260L;

  /**
   * Formalism information for ML-Space
   */
  public MLSpaceFormalism() {
    super("NCM", "NCM", "Next SpatialEntity Method",
        "Next SpatialEntity Method Stub", TimeBase.CONTINUOUS,
        SystemSpecification.DISCRETE, TimeProgress.EVENT);
  }

}
