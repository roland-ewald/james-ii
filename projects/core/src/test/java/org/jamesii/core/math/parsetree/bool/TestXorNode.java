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
import org.jamesii.core.math.parsetree.bool.XorNode;

/**
 * The Class TestXorNode.
 */
public class TestXorNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new XorNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode b = getInstance(new ValueNode<>(true), new ValueNode<>(false));
    ValueNode<Boolean> result = b.calc(null);
    assertTrue(result.getValue());

    b = getInstance(new ValueNode<>(true), new ValueNode<>(true));
    result = b.calc(null);
    assertTrue(!result.getValue());

    b = getInstance(new ValueNode<>(false), new ValueNode<>(true));
    result = b.calc(null);
    assertTrue(result.getValue());

    b = getInstance(new ValueNode<>(false), new ValueNode<>(false));
    result = b.calc(null);
    assertTrue(!result.getValue());
  }

}
