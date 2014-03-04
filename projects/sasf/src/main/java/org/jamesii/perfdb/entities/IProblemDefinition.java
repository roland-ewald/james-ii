/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.io.Serializable;
import java.util.Map;

/**
 * Interface for problem definitions. It incorporates two maps: one for problem
 * scheme parameters, one for parameters related to the given problem
 * definition. Moreover, it contains a reference to the underlying
 * {@link IProblemScheme} that this definition relies upon. Two hash values are
 * computed for faster and easier database-lookup, see
 * {@link IProblemDefinition#getDefinitionParametersHash()},
 * {@link IProblemDefinition#getSchemeParametersHash()}.
 * 
 * @see IProblemScheme
 * 
 * @author Roland Ewald
 * 
 */
public interface IProblemDefinition extends IIDEntity {

  /**
   * Gets the problem scheme.
   * 
   * @return the problem scheme
   */
  IProblemScheme getProblemScheme();

  /**
   * Sets the problem scheme.
   * 
   * @param scheme
   *          the new problem scheme
   */
  void setProblemScheme(IProblemScheme scheme);

  /**
   * Gets the scheme parameters.
   * 
   * @return the parameters
   */
  Map<String, Serializable> getSchemeParameters();

  /**
   * Sets the scheme parameters.
   * 
   * @param parameters
   *          the parameters
   */
  void setSchemeParameters(Map<String, Serializable> parameters);

  /**
   * Gets a hash value of the scheme parameters. This is a simple auxiliary
   * function to make database-lookup faster (it is sometimes necessary to query
   * all models with a certain parameter setting from the database).
   * 
   * @return the parameters hash
   */
  long getSchemeParametersHash();

  /**
   * Gets the parameters that refer to this problem definition as such (not the
   * scheme).
   * 
   * @return the definition parameters
   */
  Map<String, Serializable> getDefinitionParameters();

  /**
   * Sets the definition parameters.
   * 
   * @param parameters
   *          the definition parameters
   */
  void setDefinitionParameters(Map<String, Serializable> parameters);

  /**
   * Gets a hash value of the scheme parameters. See
   * {@link IProblemDefinition#getSchemeParametersHash()} for more details.
   * 
   * @return the definition parameters hash
   */
  long getDefinitionParametersHash();

}
