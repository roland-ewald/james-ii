/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.util.ParameterBlocks;

/**
 * Hibernate implementation for simulation problem.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
// Hibernate uses private methods
public class ProblemDefinition extends IDEntity implements IProblemDefinition {

  private static final long serialVersionUID = -3489612504785718147L;

  /** The underlying problem scheme. */
  private ProblemScheme problemScheme;

  /**
   * The definition parameters. These are parameters that refer to the problem
   * definition. They describe *how* the associated parameterized scheme shall
   * be processed. A typical example would be a simulation interval in case the
   * problem scheme is a benchmark model.
   */
  private Map<String, Serializable> definitionParameters =
      new HashMap<>();

  /**
   * Problem scheme parameters. These are parameters that define how the problem
   * scheme is configured. A typical example would be the model size in case the
   * scheme refers to a benchmark model.
   */
  private Map<String, Serializable> schemeParameters =
      new HashMap<>();

  /**
   * Empty constructor for beans compliance.
   */
  public ProblemDefinition() {
  }

  /**
   * Instantiates a new problem definition.
   * 
   * @param scheme
   *          the scheme
   * @param definitionParameters
   *          the definition parameters
   * @param schemeParameters
   *          the scheme parameters
   */
  public ProblemDefinition(ProblemScheme scheme,
      Map<String, Serializable> definitionParameters,
      Map<String, Serializable> schemeParameters) {
    this.problemScheme = scheme;
    this.definitionParameters.putAll(definitionParameters);
    this.schemeParameters.putAll(schemeParameters);
  }

  @Override
  public Map<String, Serializable> getSchemeParameters() {
    return schemeParameters;
  }

  @Override
  public void setSchemeParameters(Map<String, Serializable> schemeParameters) {
    if (!(schemeParameters instanceof Serializable)) {
      throw new IllegalArgumentException(
          "Scheme parameters need to be serializable.");
    }
    this.schemeParameters = schemeParameters;
  }

  @Override
  public Map<String, Serializable> getDefinitionParameters() {
    return definitionParameters;
  }

  @Override
  public void setDefinitionParameters(
      Map<String, Serializable> definitionParameters) {
    if (!(definitionParameters instanceof Serializable)) {
      throw new IllegalArgumentException(
          "Definition parameters need to be serializable.");
    }
    this.definitionParameters = definitionParameters;
  }

  @Override
  public IProblemScheme getProblemScheme() {
    return problemScheme;
  }

  @Override
  public void setProblemScheme(IProblemScheme problemScheme) {
    if (!(problemScheme instanceof ProblemScheme)) {
      throw new IllegalArgumentException();
    }
    this.problemScheme = (ProblemScheme) problemScheme;
  }

  @Override
  public long getSchemeParametersHash() {
    return calcParametersHash(schemeParameters);
  }

  /**
   * @return unique string representation of scheme parameters map (sorted by
   *         keys)
   */
  public String getSchemeParametersUniqueString() {
    return createSortedStringRepresentation(schemeParameters);
  }

  /**
   * @return unique string representation of definition parameters map (sorted
   *         by keys)
   */
  public String getDefinitionParametersUniqueString() {
    return createSortedStringRepresentation(definitionParameters);
  }

  protected String createSortedStringRepresentation(
      Map<String, Serializable> params) {
    StringBuilder sb = new StringBuilder();
    for (String key : getSortedKeys(params)) {
      sb.append(key + "=" + params.get(key) + ";");
    }
    return sb.toString();
  }

  @Override
  public long getDefinitionParametersHash() {
    return calcParametersHash(definitionParameters);
  }

  /**
   * Calculates hash for parameters map (Entry-wise).
   * 
   * @param params
   *          the params
   * 
   * @return the long
   */
  public static long calcParametersHash(Map<String, Serializable> params) {
    long hash = 0;
    List<String> parameterNames = getSortedKeys(params);
    for (String parameterName : parameterNames) {
      hash +=
          parameterName.hashCode() + calcObjectHash(params.get(parameterName));
    }
    return hash;
  }

  protected static List<String> getSortedKeys(Map<String, Serializable> params) {
    List<String> parameterNames = new ArrayList<>(params.keySet());
    Collections.sort(parameterNames);
    return parameterNames;
  }

  /**
   * Calculates the hash value for parameter objects. {@link ParameterBlock}
   * instances are handled differently: the hash-code of the result of
   * {@link ParameterBlock#toUniqueString(ParameterBlock)} is used here, so that
   * the hash-code is not changed by using different instances (e.g., of
   * factories), as long as all of them have the same string representation.
   * 
   * @param object
   *          the object
   * @return the hash code
   */
  private static int calcObjectHash(Serializable object) {
    if (!(object instanceof ParameterBlock)) {
      return object == null ? 0 : object.hashCode();
    }
    return ParameterBlocks.toUniqueString((ParameterBlock) object).hashCode();
  }

  // Hibernate support:

  private void setScheme(ProblemScheme scheme) {
    this.problemScheme = scheme; // NOSONAR:{used_by_hibernate}
  }

  private ProblemScheme getScheme() {
    return problemScheme; // NOSONAR:{used_by_hibernate}
  }

  /**
   * Sets the scheme parameters hash.
   * 
   * @param parametersHash
   *          the parametersHash to set
   */
  private void setSchemeParametersHash(long parametersHash) {
    // Will be calculated on the fly
  } // NOSONAR:{used_by_hibernate}

  /**
   * Sets the definition parameters hash.
   * 
   * @param parametersHash
   *          the parametersHash to set
   */
  private void setDefinitionParametersHash(long parametersHash) {
    // Will be calculated on the fly
  } // NOSONAR:{used_by_hibernate}

  @Override
  public String toString() {
    return "scheme" + getProblemScheme() + "\n\tscheme parameters: "
        + Strings.dispMap(getSchemeParameters()) + "\n\tdefinition parameters:"
        + Strings.dispMap(getDefinitionParameters());
  }
}
