/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Interface that an {@link IApplication} implementing class returns using
 * {@link IApplication#getApplicationInformation()} method. This is used to
 * provide version information mainly of the GUI version and application title
 * used by the {@link SplashScreen}
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IApplicationInformation {
  /**
   * provides the applications gui vendor
   * 
   * @return the vendor
   */
  String getVendor();

  /**
   * provides the applications gui major version
   * 
   * @return the major version number
   */
  int getVersionMajor();

  /**
   * provides the applications gui minor version
   * 
   * @return the minor version number
   */
  int getVersionMinor();

  /**
   * provides the applications gui patch level
   * 
   * @return the patch level
   */
  int getVersionPatchLevel();

  /**
   * provides the applications gui build version
   * 
   * @return the build number
   */
  int getVersionBuild();

  /**
   * provides the applications title
   * 
   * @return the application title
   */
  String getTitle();

  /**
   * Should return a concatenation of major, minor, patch level and build
   * version number, similar to this:<br/>
   * <code>VERSION=String.format("GUI Version: %d.%d.%d.%d",
   *   VERSION_MAJOR, VERSION_MINOR, VERSION_PATCHLEVEL,
   *   VERSION_BUILD)</code>
   * 
   * @return the version of the application
   */
  String getVersion();
}
