/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Already implements the {@link #getVersion()} method as concatenation of
 * major, minor, patch level and build version.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractApplicationInformation implements
    IApplicationInformation {

  @Override
  public String getVersion() {
    return String.format("%d.%d.%d.%d", Integer.valueOf(getVersionMajor()),
        Integer.valueOf(getVersionMinor()),
        Integer.valueOf(getVersionPatchLevel()),
        Integer.valueOf(getVersionBuild()));
  }

}
