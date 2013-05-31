/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.encoding.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.encoding.IEncoding;

/**
 * Abstract base class for encoding guessing factories.
 * 
 * @author Jan Himmelspach
 */
public abstract class EncodingFactory extends Factory<IEncoding> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8461839717367315942L;

  /**
   * Creates a encoding guesser.
   * 
   * 
   * @param parameter
   *          the parameter
   * 
   * @return An instance of the respective encoding guesser.
   */
  @Override
  public abstract IEncoding create(ParameterBlock parameter);

}
