/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

import java.io.Serializable;

/**
 * A history environment is an environment which provides the possibility to
 * access past values. Whenever it is triggered a copy of the variables in the
 * environment is made and stored in the history list. These can be accessed
 * later on by using the extended getValue method.
 * 
 * 
 * @author Jan Himmelspach
 * @param <K>
 *          the type of the keys (usually "names", i.e., strings will be used
 *          here) used to identify variables
 */
public interface IHistoryEnvironment<K extends Serializable> extends
    IEnvironment<K> {

  /**
   * If called the current list of variable values will be added to the history.
   * The environment might be empty after having finished this call! WARNING:
   * check carefully what the implementation is doing. It might not do a deep
   * cloning!!!
   */
  void timeStep();

  /**
   * Get the value of the identifier/variable with the given ident. The relTime
   * parameter is the relative time step of which the variable's value shall be
   * retrieved. If relTime is called with a value of 0 it is identical to the
   * inherited getValue call, a relTime value of 1 means one step back, a.s.o..
   * 
   * @param ident
   *          the ident
   * @param relTime
   *          the rel time
   * 
   * @return the value
   */
  Object getValue(K ident, Integer relTime);

}
