/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract class, to select a test factory.
 * 
 * @author Stefan Leye
 * 
 */
public class AbstractPairedTestFactory extends
    AbstractFilteringFactory<PairedTestFactory> {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -1174026798257972898L;

  /**
   * Tag for the type of the test.
   */
  public static final String TEST_FACTORY_CLASS = "testFactoryClass";

}
