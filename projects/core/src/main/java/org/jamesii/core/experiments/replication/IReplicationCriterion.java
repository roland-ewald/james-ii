/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.experiments.RunInformation;

/**
 * Interface for objects that want to control the number of replications for
 * each experiment.
 * 
 * @author Roland Ewald Date: 10.05.2007
 * 
 */
public interface IReplicationCriterion extends Serializable {

  /**
   * Returns a criterion's first guess as how any replications would be
   * sufficient from now on. E.g., return 0 means that the criterion is already
   * satisfied, returning 10 means that at least 10 more replications are
   * necessary.
   * 
   * @param runInformation
   *          the run information of all past runs
   * @return number of replications that are yet to be done
   */
  int sufficientReplications(List<RunInformation> runInformation);

}
