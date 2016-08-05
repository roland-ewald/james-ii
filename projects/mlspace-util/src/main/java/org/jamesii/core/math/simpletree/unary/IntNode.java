/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.simpletree.unary;

import org.jamesii.core.math.simpletree.DoubleNode;

/**
 *
 * @author Arne Bittig
 * @date 11.03.2014
 */
public final class IntNode extends AbstractUnaryNode {

  public IntNode(DoubleNode sub) {
    super(sub);
  }

  @Override
  protected double performOperation(double val) {
    return (int) val;
  }

  @Override
  protected DoubleNode simplerCopy(DoubleNode sub) {
    return new IntNode(sub);
  }

  @Override
  public String toString() {
    return "[" + super.toString() + "]";
  }
}