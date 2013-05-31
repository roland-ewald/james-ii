/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.TestBinaryNode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.math.ModNode;

/**
 * The Class TestModNode.
 */
public class TestModNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new ModNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode node = getInstance(new ValueNode<>(2), new ValueNode<>(1));
    ValueNode<Integer> res = node.calc(null);
    assertTrue(res.getValue().compareTo(0) == 0);

    node = getInstance(new ValueNode<>(3), new ValueNode<>(2));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(1) == 0);

  }

}
