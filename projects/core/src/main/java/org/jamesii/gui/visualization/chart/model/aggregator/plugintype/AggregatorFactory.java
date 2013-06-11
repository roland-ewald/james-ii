/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.visualization.chart.model.aggregator.IAggregator;

/**
 * Basic factory for all factories that create aggregators.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AggregatorFactory extends Factory<IAggregator> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 516790989428490245L;

  /**
   * Creates the aggregator.
   * 
   * @param params
   *          the params
   * 
   * @return the created aggregator
   */
  @Override
  public abstract IAggregator create(ParameterBlock params);
}
