/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.util.ArrayList;

import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * Factory for {@link MaximizingReplicationCriterion}.
 * 
 * @author Roland Ewald
 * 
 */
public class MaximizingReplicationCriterionFactory extends RepCriterionFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4093389530850508176L;

  /**
   * The name for the parameter containing a list of parameterized replication
   * criterion factories.
   */
  public static final String REPLICATION_CRITERION_FACTORIES =
      "repCriterionFactories";

  @Override
  public IReplicationCriterion create(ParameterBlock params, Context context) {
    return new MaximizingReplicationCriterion(
        ParameterBlocks.getSubBlockValueOrDefault(params,
            REPLICATION_CRITERION_FACTORIES,
            new ArrayList<ParameterizedFactory<RepCriterionFactory>>()));
  }
}
