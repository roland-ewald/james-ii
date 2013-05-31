/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory that creates the visualization perspective
 * 
 * @author Stefan Rybacki
 * 
 *         26.06.2007
 * 
 */
public class VisDataAnalysisPerspectiveFactory extends PerspectiveFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -6063710384342993226L;

  @Override
  public IPerspective create(ParameterBlock params) {
    return new VisDataAnalysisPerspective();
  }

}
