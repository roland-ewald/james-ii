/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Factory to create criterion for a fixed number of replications.
 * 
 * @author Roland Ewald
 */
public class RepNumberCriterionFactory extends RepCriterionFactory {
  /** Serialization ID. */
  private static final long serialVersionUID = 3033773664760815351L;

  /** Parameter name for number of replications. */
  public static final String NUM_REPS = "numOfReps";

  /** The default number of replications. */
  public static final int DEFAULT_NUMBER_REPLICATIONS = 1;

  @Override
  public IReplicationCriterion create(ParameterBlock params) {
    return new ReplicationNumberCriterion(
        ParameterBlocks.getSubBlockValueOrDefault(params, NUM_REPS,
            DEFAULT_NUMBER_REPLICATIONS));
  }

}
