/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory for {@link SimSpExPerspective}.
 * 
 * @author Roland Ewald
 * 
 */
public class SimSpExPerspectiveFactory extends PerspectiveFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7713264856461851818L;

  @Override
  public IPerspective create(ParameterBlock params) {
    return new SimSpExPerspective();
  }

}
