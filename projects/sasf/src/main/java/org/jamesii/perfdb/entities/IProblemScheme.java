/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.net.URI;

/**
 * General interface for problem schemes (e.g., benchmark models, problem
 * generators, etc.).
 * 
 * @author Roland Ewald
 * 
 */
public interface IProblemScheme extends INamedDBEntity {

  /**
   * Gets the type of the benchmark model.
   * 
   * @return the type
   */
  String getType();

  /**
   * Sets the type of the benchmark model.
   * 
   * @param type
   *          the new type
   */
  void setType(String type);

  /**
   * Gets the model URI.
   * 
   * @return the model URI
   */
  URI getUri();

  /**
   * Sets the model URI.
   * 
   * @param uri
   *          the new model URI
   */
  void setUri(URI uri);
}
