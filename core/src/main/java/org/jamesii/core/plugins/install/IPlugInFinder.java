/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.install;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;

/**
 * The Interface IPlugInFinder. Depending on the usage of the system it might be
 * advisable to be able to support different mechanisms to find "Plug-ins". One
 * of those might be searching directories, and another one, e.g., loading a
 * cached list of elements installed.
 */
public interface IPlugInFinder {

  /**
   * Get the list of plug-ins found.
   * 
   * @return a list of plug-in data entries
   */
  List<IPluginData> getFoundPlugins();

  /**
   * Get the list of plug-in types found.
   * 
   * @return a list of plug-in type entries
   */
  List<IPluginTypeData> getFoundPluginTypes();

  /**
   * Get the list of paths where the plug-ins have been found.
   * 
   * @return the paths
   */
  Map<IPluginData, String> getPaths();

  /**
   * Gets the JAR file locations - they might have to be added to the class
   * path.
   * 
   * @return the list of JAR file locations
   */
  List<URL> getJARLocations();

}
