/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.model.aggregator;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.visualization.chart.model.aggregator.plugintype.AggregatorFactory;

/**
 * @author Stefan Rybacki
 * 
 */
public class DeviationAggregatorFactory extends AggregatorFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4644268278710852594L;

  /**
   * The singleton aggregator.
   */
  private static final IAggregator aggregator = new DeviationAggregator();

  @Override
  public IAggregator create(ParameterBlock params) {
    return aggregator;
  }

}
