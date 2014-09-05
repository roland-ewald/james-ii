/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * This criterion aggregated several other replication criteria by instantiating
 * them, querying them, and then returning the maximum number of replications
 * that is still required.
 * 
 * @author Roland Ewald
 */
public class MaximizingReplicationCriterion implements IReplicationCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6225038826080863645L;

  /** The replication criterion factories to be queried. */
  final List<ParameterizedFactory<RepCriterionFactory>> replicationCriterionFactories;

  /**
   * Instantiates a new maximizing replication criterion.
   * 
   * @param repCriterionFactories
   *          the replication criterion factories
   */
  public MaximizingReplicationCriterion(
      List<ParameterizedFactory<RepCriterionFactory>> repCriterionFactories) {
    this.replicationCriterionFactories = repCriterionFactories;

    if ((replicationCriterionFactories == null)
        || (replicationCriterionFactories.isEmpty())) {
      throw new InvalidParameterException(
          "At least one replication criterion factory needs to be handed over!");
    }

  }

  @Override
  public int sufficientReplications(List<RunInformation> runInformation) {
    int sufficientReplications = 0;
    for (ParameterizedFactory<RepCriterionFactory> repCriterionFactory : replicationCriterionFactories) {
      if (!repCriterionFactory.isInitialized()) {
        SimSystem.report(Level.WARNING,
            "Ignoring replication criterion factory '" + repCriterionFactory
                + "', it is not initialized properly.");
      }
      int sufficientRepsForCriterion =
          repCriterionFactory.getFactoryInstance()
              .create(repCriterionFactory.getParameters(), SimSystem.getRegistry().createContext())
              .sufficientReplications(runInformation);
      if (sufficientRepsForCriterion > sufficientReplications) {
        sufficientReplications = sufficientRepsForCriterion;
      }
    }
    return sufficientReplications;
  }

}
