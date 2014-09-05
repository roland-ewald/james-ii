/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.simple;


import java.util.ArrayList;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.adaptiverunner.AdaptiveTaskRunnerFactory;
import org.jamesii.simspex.exploration.ISimSpaceExplorer;
import org.jamesii.simspex.exploration.plugintype.SimSpaceExplorerFactory;


/**
 * Factory for the {@link SimpleASRSpaceExplorer}.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleASRSpaceExplorerFactory extends SimSpaceExplorerFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1697455688056830890L;

  @Override
  public ISimSpaceExplorer create(ParameterBlock params, Context context) {
    return new SimpleASRSpaceExplorer(params.getSubBlockValue(
        AdaptiveTaskRunnerFactory.PORTFOLIO,
        new ArrayList<ParameterBlock>()));
  }
}
