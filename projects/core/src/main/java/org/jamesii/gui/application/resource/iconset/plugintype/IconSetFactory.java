/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource.iconset.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.resource.iconset.IIconSet;

/**
 * Basic factory for icon sets
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class IconSetFactory extends Factory<IIconSet> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1267909894284902432L;

  /**
   * Creates icon set
   * 
   * @param params
   *          parameters
   * @return icon set
   */
  @Override
  public abstract IIconSet create(ParameterBlock params);

}
