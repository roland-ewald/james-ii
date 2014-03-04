/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;

import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * The interface for describing a problem definition.
 * 
 * @author Roland Ewald
 */
public interface IProblemDefinitionDescriptor {

  /**
   * Gets the description for the simulation problem.
   * 
   * @param problem
   *          the simulation problem
   * 
   * @return the description stringy, should always return the same number of
   *         elements
   */
  String[] describe(IProblemDefinition problem);

  /**
   * Gets the header for the different description columns.
   * 
   * @return the header of null, if no specific headers should be written
   */
  String[] getHeader();

}
