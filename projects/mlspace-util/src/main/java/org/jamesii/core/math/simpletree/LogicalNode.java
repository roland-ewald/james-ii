package org.jamesii.core.math.simpletree;

import java.util.Map;

/**
 * @author Arne Bittig
 * @date 01.08.2014
 */
public interface LogicalNode {

  /**
   * Calculate the value of the node (or implied sub-tree) with given variable
   * values
   *
   * @param environment
   *          Values of variables used
   * @return Expression's value
   */
  boolean calculateValue(Map<String, ? extends Number> environment);

}
