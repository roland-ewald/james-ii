/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.gui.perspective.IPerspective;

/**
 * Basic factory for all factories that create UI perspectives.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class PerspectiveFactory extends Factory<IPerspective> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 5167909894284902432L;

}
