/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory that creates the experiment perspective.
 * 
 * @author Stefan Rybacki
 */
public class ExperimentPerspectiveFactory extends PerspectiveFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6063710384240993226L;

  @Override
  public IPerspective create(ParameterBlock params) {
    return new ExperimentPerspective();
  }

}
