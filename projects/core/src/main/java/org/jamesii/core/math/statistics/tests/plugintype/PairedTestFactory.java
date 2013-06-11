/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.math.statistics.tests.IPairedTest;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for all factories creating a {@link IPairedTest}.
 * 
 * @author Stefan Leye
 * 
 */
public abstract class PairedTestFactory extends Factory<IPairedTest> implements
    IParameterFilterFactory {

  /**
   * The serialisation ID.
   */
  private static final long serialVersionUID = -6503841749842665528L;

  /**
   * Return a new instance of the
   * {@link org.jamesii.core.math.statistics.tests.IPairedTest} to be used.
   * 
   * @param parameter
   *          configuration parameters
   * 
   * @return test class
   */
  @Override
  public abstract IPairedTest create(ParameterBlock parameter);
}
