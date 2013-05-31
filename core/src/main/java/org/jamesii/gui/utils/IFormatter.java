/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

/**
 * A simple formatter interface used to create better outputs than
 * {@link Object#toString()} does in cases {@link #toString()} can't be
 * overwritten.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          the type of object to format
 */
public interface IFormatter<T> {

  /**
   * Creates a {@link String} from the given object.
   * 
   * @param t
   *          the object to format
   * @return the string
   */
  String format(T t);
}
