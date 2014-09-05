/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.plugintype;

import org.jamesii.core.experiments.replication.IReplicationCriterion;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for all kinds of replication criterion factories.
 * 
 * @author Roland Ewald
 */
public abstract class RepCriterionFactory extends
    Factory<IReplicationCriterion> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1857810732008152633L;

  /**
   * Creates replication criterion.
 * @param params
   *          the parameters
 * @return newly created replication criterion
   */
  @Override
  public abstract IReplicationCriterion create(ParameterBlock params, Context context);

}
