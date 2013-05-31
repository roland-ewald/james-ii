/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf.plugintype;

import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.core.factories.Factory;

/**
 * Basic factory for all factories that contribute look and feels
 * 
 * @author Stefan Rybacki
 */
public abstract class LookAndFeelFactory extends Factory<LookAndFeelInfo> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1167909894284902432L;

}
