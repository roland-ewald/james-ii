/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory that creates the modeling perspective
 * 
 * @author Stefan Rybacki
 * 
 */
public class ModelPerspectiveFactory extends PerspectiveFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -6063703424099322234L;

  @Override
  public IPerspective create(ParameterBlock params) {
    return new ModelPerspective();
  }

}
