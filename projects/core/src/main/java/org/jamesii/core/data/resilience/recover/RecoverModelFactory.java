/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.resilience.recover;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for factories creating a {@link IRecoverModel}.
 * 
 * @author Thomas Noesinger
 */
public abstract class RecoverModelFactory extends Factory<IRecoverModel> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 8490466623228554768L;

  /**
   * Creates a new instance of RecoverModelFactory.
   */
  public RecoverModelFactory() {
  }

  /**
   * Creates a new RecoverModel object.
   * 
   * @param parameter
   *          the parameters for the recover model
   * 
   * @return the recover model
   */
  public abstract IRecoverModel createRecoverModel(ParameterBlock parameter);

}
