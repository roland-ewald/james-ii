/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory that creates the default perspective.
 * 
 * @author Stefan Rybacki
 * 
 */
public class DefaultPerspectiveFactory extends PerspectiveFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -6063710384240993226L;

  @Override
  public IPerspective create(ParameterBlock params, Context context) {
    return DefaultPerspective.getInstance();
  }

}
