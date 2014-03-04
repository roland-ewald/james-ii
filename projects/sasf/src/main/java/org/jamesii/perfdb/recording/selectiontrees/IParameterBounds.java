/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

/**
 * Simple interface for boundary configuration.
 * 
 * @author Roland Ewald
 * 
 */
public interface IParameterBounds<N extends Number> {

  N getLowerBound();

  void setLowerBound(N bound);

  N getUpperBound();

  void setUpperBound(N bound);

}
