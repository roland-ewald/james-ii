/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;
import java.util.List;
import java.net.URI;

/**
 * Interface that provides basic information on the loaded factories.
 * 
 * @author Jan Himmelspach
 */
public interface IFactoryInfo extends Serializable {

  /**
   * Gets the classname of the factory.
   * 
   * @return the classname as a String
   */
  String getClassname();

  /**
   * Gets the parameters which can be set for this factory.
   * 
   * @return the parameters that can be set for this factory
   */
  List<IParameter> getParameters();

  /**
   * Gets the description of the factory.
   * 
   * @return the description as a String
   */
  String getDescription();

  /**
   * Gets the file name resp. the location where this factory was announced
   * (usually a plugin.xml file)
   * 
   * @return the location of the plugin definition
   */
  String getPluginDefLocation();

  /**
   * Get the icon if an icon has been created for the factory.
   * 
   * @return the URI where the icon can be found
   */
  URI getIconURI();

  /**
   * Get the "friendly" name of the factory.
   * 
   * @return the friendly name if one has been defined
   */
  String getName();

}
