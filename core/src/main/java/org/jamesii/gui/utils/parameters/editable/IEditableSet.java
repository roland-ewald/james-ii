/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable;

import java.util.List;

/**
 * This interface is for all sets of parameters. E.g., sequences or choices.
 * 
 * Created on June 4, 2004
 * 
 * @author Roland Ewald
 */
public interface IEditableSet {

  /**
   * Adds an allowed parameter.
   * 
   * @param parameter
   *          the parameter
   */
  void addAllowedParameterType(IEditable<?> parameter);

  /**
   * Adds an Object (that has to be created via createNewParameter() before) to
   * the parameter list.
   * 
   * @param parameter
   *          the parameter to be added
   * 
   * @return position of new element in list, if insertion was successful,
   *         otherwise -1
   */
  int addParameter(IEditable<?> parameter);

  /**
   * Deletes a parameter.
   * 
   * @param parameter
   *          the parameter to be deleted
   * 
   * @return true, if operation successful, otherwise false (e.g. due to
   *         occurrence constraints)
   */
  boolean deleteParameter(IEditable<?> parameter);

  /**
   * Returns a list with all 'parameter types'.
   * 
   * @return list of editables
   */
  List<IEditable<?>> getAllowedParameterTypes();

  /**
   * Counts entities per editable parameter.
   * 
   * @param parameter
   *          the parameter to be counted
   * 
   * @return number of entities of a parameter in this parameter set
   */
  int getEntityCountForParameter(IEditable<?> parameter);

  /**
   * Gets the max count for parameter.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return maximal number of entities of that parameter in this set
   */
  int getMaxCountForParameter(IEditable<?> parameter);

  /**
   * Gets the min count for parameter.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return minimal number of entities of that parameter in this set
   */
  int getMinCountForParameter(IEditable<?> parameter);

  /**
   * Returns ArrayList with all Parameters that are _instantiated_ (e.g. a
   * choice will only allow *one* instance of its parameter-type set). E. g. the
   * choice group also consists of more than one parameter object, but it will
   * return an arraylist with only _one_ parameter (or howmany the user can
   * choose)
   * 
   * @return list of instantiated parameters
   */
  List<IEditable<?>> getParameters();

  /**
   * Checks for editable elements.
   * 
   * @return true, if the parameter set has allowed parameters, that could be
   *         added
   */
  boolean hasEditableElements();

}
