/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.print;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * The Class DefaultPrintManager.
 */
public class DefaultPrintManager implements IPrintManager {

  /**
   * Value node str.
   * 
   * @param node
   *          the node
   * 
   * @return the string
   */
  private String valueNodeStr(ValueNode<?> node) {

    if (node.getName().compareTo("") == 0) {
      if (node.getValue() == null) {
        return "null";
      }

      return node.getValue().toString();

    }

    StringBuffer result = new StringBuffer(node.getName() + "(");

    for (INode param : node.getChildren()) {
      if (param != node.getChildren().get(0)) {
        result.append(", ");
      }

      result.append(toString(param));
    }

    result.append(")");

    return result.toString();
  }

  /**
   * Binary node str.
   * 
   * @param node
   *          the node
   * 
   * @return the string
   */
  private String binaryNodeStr(BinaryNode node) {
    if (node.getName() == null || node.getName().length() == 0) {
      return node.toString();
    }

    // it is crucial to output the operator precedence in the string, too
    // to play it safe we add the parenthesises here

    // get toString result from the left node of this binary node
    String result = "(" + toString(node.getLeft());

    // add the operator symbol in between the two operands (infix)
    result += " " + node.getName() + " ";

    // get toString result from the right node of this binary node
    result += toString(node.getRight()) + ")";

    return result;
  }

  @Override
  public String toString(INode node) {

    if (node instanceof BinaryNode) {
      return binaryNodeStr((BinaryNode) node);
    }

    if (node instanceof ValueNode<?>) {
      return valueNodeStr((ValueNode<?>) node);
    }

    return node.toString();
  }

}
