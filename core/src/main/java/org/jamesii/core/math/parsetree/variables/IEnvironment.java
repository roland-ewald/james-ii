/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

import java.io.Serializable;

/**
 * An environment shall maintain a list of tuples of identifiers and
 * corresponding values.
 * 
 * @author Jan Himmelspach
 * @param <K>
 *          the type of the keys (usually "names", i.e., strings will be used
 *          here) used to identify variables
 */
public interface IEnvironment<K extends Serializable> extends Serializable {

  /**
   * Set the value of the identifier/variable with the given ident.
   * 
   * @param ident
   *          the ident
   * @param value
   *          the value
   */
  void setValue(K ident, Object value);

  /**
   * Get the value of the identifer/variable with the given ident.
   * 
   * @param ident
   *          the ident
   * 
   * @return the value
   */
  Object getValue(K ident);

  /**
   * Check for existence of the given ident.
   * 
   * @param ident
   *          the ident
   * 
   * @return true, if contains ident
   */
  boolean containsIdent(K ident);

  /**
   * Removes the value for the given identifier.
   * 
   * @param ident
   *          the identifier
   */
  void removeValue(K ident);

  /**
   * Removes all previously set values.
   */
  void removeAll();
}
