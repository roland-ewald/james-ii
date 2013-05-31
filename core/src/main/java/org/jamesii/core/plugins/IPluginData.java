/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.net.URI;
import java.util.List;

/**
 * Accessor to XML data that defines plugins
 * 
 * $Id$
 * 
 * @author Mathias Roehl created 05.05.2006; Stefan Rybacki, Jan Himmelspach
 */
public interface IPluginData {

  /** namespace of the XML Schema definition for plugin data. */
  String NAMESPACE = "http://www.informatik.uni-rostock.de/mosi/cosa/plugin";

  /**
   * Gets the dependencies.
   * 
   * @return the dependencies
   */
  List<IId> getDependencies();

  /**
   * Gets the factories.
   * 
   * @return the factories
   */
  List<IFactoryInfo> getFactories();

  /**
   * Gets the id.
   * 
   * @return the id
   */
  IId getId();

  /**
   * Gets the license URI
   * 
   * @return the URI where the license can be found
   */
  URI getLicenseURI();

  /**
   * Gets the plugin's location.
   * 
   * @return the plugin's location
   */
  String getPluginLocation();
}
