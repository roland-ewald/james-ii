/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.util.List;

/**
 * The Interface IPluginTypeData.
 */
public interface IPluginTypeData {

  /** namespace of the XML Schema definition for plug-in data. */
  String NAMESPACE =
      "http://www.informatik.uni-rostock.de/mosi/cosa/plugintype";

  /**
   * Gets the abstract factory.
   * 
   * @return the abstract factory
   */
  String getAbstractFactory();

  /**
   * Gets the base factory.
   * 
   * @return the base factory
   */
  String getBaseFactory();

  /**
   * Gets the description.
   * 
   * @return the description
   */
  String getDescription();

  /**
   * Gets the ids.
   * 
   * @return the ids
   */
  IId getId();

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  List<IParameter> getParameters();

}
