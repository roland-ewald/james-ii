/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.bool;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.TestBinaryNode;
import org.jamesii.core.math.parsetree.ValueNode;

/**
 * The Class TestImpliesNode.
 */
public class TestImpliesNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new ImpliesNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode b = getInstance(new ValueNode<>(true), new ValueNode<>(false));
    ValueNode<Boolean> result = b.calc(null);
    assertTrue(!result.getValue());

    b = getInstance(new ValueNode<>(true), new ValueNode<>(true));
    result = b.calc(null);
    assertTrue(result.getValue());

    b = getInstance(new ValueNode<>(false), new ValueNode<>(true));
    result = b.calc(null);
    assertTrue(result.getValue());

    b = getInstance(new ValueNode<>(false), new ValueNode<>(false));
    result = b.calc(null);
    assertTrue(result.getValue());

    b = getInstance(new ValueNode<>(2), new ValueNode<>(2));
    result = b.calc(null);
    assertTrue(result.getValue());

    b = getInstance(new ValueNode<>(0), new ValueNode<>(0));
    result = b.calc(null);
    assertTrue(result.getValue());
  }

}
