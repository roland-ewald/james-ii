/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import java.net.URI;

import org.jamesii.core.factories.Factory;

/**
 * Interface to categorize {@link Factory}s.
 * 
 * @author Stefan Rybacki
 */
public interface IURIHandling {

  /** The model URI. */
  String URI = "URI";

  /**
   * Returns true if the factory is able to handle models type and data source
   * as given by the URI.
   * 
   * @param uri
   *          the URI to be handled
   * @return true, if URI is supported
   */
  boolean supportsURI(URI uri);
}
