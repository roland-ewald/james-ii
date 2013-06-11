/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.preferences.config;

/**
 * 
 * MainConfFile
 * 
 * Class for Handling the main configuration file of the James GUI.
 * 
 * @see org.jamesii.gui.application.preferences.config.ConfFile
 * 
 *      Created: 13.04.2004
 * @author Roland Ewald
 */

public class MainConfFile extends ConfFile {

  /**
   * Serialisation ID.
   */
  static final long serialVersionUID = -3460687655153775013L;

  @Override
  public void setDefaults() {
    getValues().put("profile_path", "profiles");
  }

}
