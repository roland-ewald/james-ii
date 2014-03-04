/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IProblemScheme;

/**
 * Interface for problem definition data base.
 * 
 * @see IProblemDefinition
 * 
 * @author Roland Ewald
 * 
 */
public interface IProblemDefinitionDatabase {

  /**
   * Get all problem definitions.
   * 
   * @return list of all problems definitions that are stored
   * @throws Exception
   *           if DB look-up fails
   */
  List<IProblemDefinition> getAllProblemDefinitions();

  /**
   * Get all problem definitions associated with a certain problem scheme.
   * 
   * @param scheme
   *          the problem scheme
   * @return a list of all problem definitions defined on that scheme
   */
  List<IProblemDefinition> getAllProblemDefinitions(IProblemScheme scheme);

  /**
   * Get problem definition for given properties.
   * 
   * @param scheme
   *          the problem scheme
   * @param definitionParameters
   *          the definition parameters; will be ignored if null
   * @param schemeParameters
   *          the scheme parameters; will be ignored if null
   * @return the problem definition matching these properties, or null if not
   *         found
   */
  IProblemDefinition getProblemDefinition(IProblemScheme scheme,
      Map<String, Serializable> definitionParameters,
      Map<String, Serializable> schemeParameters);

  /**
   * Creates and stores a new problem definition. Before, it is checked whether
   * the problem already exists (in which case it is merely returned, not
   * created anew).
   * 
   * @param scheme
   *          the problem scheme
   * @param definitionParameters
   *          the definition parameters
   * @param schemeParameters
   *          the scheme parameters
   * @return the (maybe newly created) problem definition
   */
  IProblemDefinition newProblemDefinition(IProblemScheme scheme,
      Map<String, Serializable> definitionParameters,
      Map<String, Serializable> schemeParameters);

  /**
   * Deletes a problem definition.
   * 
   * @param problemDefinition
   *          the problem definition to be deleted
   * @return true if problem definition could be deleted, otherwise false
   */
  boolean deleteProblemDefinition(IProblemDefinition problemDefinition);

  /**
   * Adds new problem instance with given RNG seed to given problem definition.
   * If a problem instance with this RNG seed already exists for this problem,
   * the existing instance will be returned.
   * 
   * @param problemDefinition
   *          the problem definition for which the problem instance shall be
   *          generated
   * @param rngSeed
   *          the RNG seed that characterises this instance
   * @param rngFactoryName
   *          the name of the RNG factory that uses the seed as initial state
   * @return (maybe newly created) problem instance with given properties
   */
  IProblemInstance newProblemInstance(IProblemDefinition problemDefinition,
      long rngSeed, String rngFactoryName);

  /**
   * Get problem instance for given properties.
   * 
   * @param problemDefinition
   *          the problem definition
   * @param rngSeed
   *          the RNG seed
   * @return the problem instance matching these properties, or null if not
   *         found
   */
  IProblemInstance getProblemInstance(IProblemDefinition problemDefinition,
      long rngSeed);

  /**
   * Get all problem instance for given problem definition.
   * 
   * @param problemDefinition
   *          the problem definition
   * @return list of problem instances for this problem definition
   */
  List<IProblemInstance> getAllProblemInstances(
      IProblemDefinition problemDefinition);

  /**
   * Deletes problem instance for this problem definition.
   * 
   * @param problemInstance
   *          the problem instance
   * 
   * @return true if problem could be deleted, otherwise false
   */
  boolean deleteProblemInstance(IProblemInstance problemInstance);

}
