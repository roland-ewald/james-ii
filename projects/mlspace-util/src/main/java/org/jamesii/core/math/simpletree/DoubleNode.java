/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree;

import java.util.Map;

/**
 * Simple parse tree base structure
 *
 * @author Arne Bittig
 * @date 11.03.2014
 *
 * @param <N>
 *          Number type
 */
public interface DoubleNode {

  /**
   * Calculate the value of the node (or implied sub-tree) with given variable
   * values
   *
   * @param environment
   *          Values of variables used
   * @return Expression's value
   */
  double calculateValue(Map<String, ? extends Number> environment);

  /**
   * Try to simplify subtree by replacing nodes that always produce the same
   * fixed value with this value (optional operation)
   *
   * @return simplified node, or node itself if no simplification possible
   */
  DoubleNode simplify();
}
