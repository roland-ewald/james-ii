/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Bean to store plug-in information.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 */
public class PlugInInfo implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8038346530261444261L;

  /** List of found plug-in data. */
  private List<IPluginData> foundPlugins;

  /** List of found plug-in types. */
  private List<IPluginTypeData> foundPluginTypes;

  /** Plug-in locations. */
  private Map<IPluginData, String> paths;

  /**
   * Gets the found plugins.
   * 
   * @return the found plugins
   */
  public List<IPluginData> getFoundPlugins() {
    return foundPlugins;
  }

  /**
   * Sets the found plugins.
   * 
   * @param foundPlugins
   *          the new found plugins
   */
  public void setFoundPlugins(List<IPluginData> foundPlugins) {
    this.foundPlugins = foundPlugins;
  }

  /**
   * Gets the found plugin types.
   * 
   * @return the found plugin types
   */
  public List<IPluginTypeData> getFoundPluginTypes() {
    return foundPluginTypes;
  }

  /**
   * Sets the found plugin types.
   * 
   * @param foundPluginTypes
   *          the new found plugin types
   */
  public void setFoundPluginTypes(List<IPluginTypeData> foundPluginTypes) {
    this.foundPluginTypes = foundPluginTypes;
  }

  /**
   * Gets the paths.
   * 
   * @return the paths
   */
  public Map<IPluginData, String> getPaths() {
    return paths;
  }

  /**
   * Sets the paths.
   * 
   * @param paths
   *          the paths
   */
  public void setPaths(Map<IPluginData, String> paths) {
    this.paths = paths;
  }
}
