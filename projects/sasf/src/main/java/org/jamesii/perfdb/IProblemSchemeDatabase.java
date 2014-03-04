/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;


import java.net.URI;
import java.util.List;

import org.jamesii.perfdb.entities.IProblemScheme;

/**
 * Interface for database of {@link IProblemScheme} instances.
 * 
 * @author Roland Ewald
 * 
 */
public interface IProblemSchemeDatabase {

  /**
   * List all problem schemes.
   * 
   * @return list of problem schemes
   */
  List<IProblemScheme> getAllProblemSchemes();

  /**
   * Get the problem scheme with the given URI.
   * 
   * @param uri
   *          the scheme's URI
   * @return the problem scheme or null if not found
   */
  IProblemScheme getProblemScheme(URI uri);

  /**
   * Creates new problem scheme.
   * 
   * @param uri
   *          scheme URI
   * @param name
   *          scheme name
   * @param type
   *          scheme type
   * @param description
   *          scheme description
   * @return newly created scheme
   */
  IProblemScheme newProblemScheme(URI uri, String name, String type,
      String description);

  /**
   * Deletes problem scheme.
   * 
   * @param scheme
   *          scheme to be deleted
   * @return true, if deletion was successful, otherwise false
   */
  boolean deleteProblemScheme(IProblemScheme scheme);

}
