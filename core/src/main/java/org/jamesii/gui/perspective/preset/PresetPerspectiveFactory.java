/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.preset;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.perspective.IPerspective;
import org.jamesii.gui.perspective.plugintype.PerspectiveFactory;

/**
 * Factory that creates an addition to the default perspective. Providing a
 * simple mechanism to change between perspective presets.
 * 
 * @author Stefan Rybacki
 * 
 */
public class PresetPerspectiveFactory extends PerspectiveFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -6063710384240993227L;

  @Override
  public IPerspective create(ParameterBlock params) {
    return new PresetPerspective();
  }

}
