/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.print;

import org.jamesii.core.math.parsetree.INode;

/**
 * The Class PrintManager.
 */
public interface IPrintManager {

  /**
   * To string.
   * 
   * @param node
   *          the node
   * 
   * @return the string
   */
  String toString(INode node);

}
