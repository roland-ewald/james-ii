/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;
import java.net.URI;

/**
 * Unique plug-in identifier.
 * 
 * $Id$
 * 
 * @author Mathias Roehl created 05.05.2006
 */
public interface IId extends Serializable, Comparable<IId> {

  /**
   * Gets the name.
   * 
   * @return the name of the referenced component
   */
  String getName();

  /**
   * Gets the version.
   * 
   * @return the version of the referenced entity
   */
  String getVersion();

  /**
   * Get the icon if an icon has been created for the plug-in.
   * 
   * @return the URI where the icon can be found
   */
  URI getIconURI();
}
