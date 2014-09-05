/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.exploration.ISimSpaceExplorer;


/**
 * Base factory for simulation space explorer.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class SimSpaceExplorerFactory extends
    Factory<ISimSpaceExplorer> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5496284589669436578L;

  /**
   * Creates simulation space explorer.
 * @param absParameters
   *          parameters to configure object creation
 * @return simulation space explorer
   */
  public abstract ISimSpaceExplorer create(ParameterBlock params, Context context);

}
