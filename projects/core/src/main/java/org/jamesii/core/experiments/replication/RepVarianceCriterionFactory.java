/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory to create criterion for a max allowed variance.
 * 
 * @author Jan Himmelspach
 */
public class RepVarianceCriterionFactory extends RepCriterionFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3544009758339583873L;

  /** Parameter name for number of replications. */
  public static final String MAX_VARIANCE = "maxVariance";

  @Override
  public IReplicationCriterion create(ParameterBlock params, Context context) {
    Double maxVariance = 0.;
    // -- test cases begin
    Long dataid = 12345l;
    String attribute = "state_vector";
    int observedSpecies = 2;
    // -- test cases end

    if (params.hasSubBlock(MAX_VARIANCE)) {
      maxVariance = params.getSubBlock(MAX_VARIANCE).getValue();
    }

    return new ReplicationVarianceCriterion(maxVariance, dataid, attribute,
        observedSpecies);
  }

}
