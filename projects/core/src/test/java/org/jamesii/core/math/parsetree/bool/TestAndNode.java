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
import org.jamesii.core.math.parsetree.bool.AndNode;
import org.jamesii.core.math.parsetree.bool.OrNode;

/**
 * The Class TestAndNode.
 */
public class TestAndNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new AndNode(left, right);
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
    assertTrue(!result.getValue());

    b = getInstance(new ValueNode<>(false), new ValueNode<>(false));
    result = b.calc(null);
    assertTrue(!result.getValue());

    // check sub tree handling
    b =
        getInstance(new OrNode(new ValueNode<>(true), new ValueNode<>(true)),
            new ValueNode<>(false));
    result = b.calc(null);
    assertTrue(!result.getValue());

    b =
        getInstance(new ValueNode<>(false), new OrNode(new ValueNode<>(true),
            new ValueNode<>(false)));
    result = b.calc(null);
    assertTrue(!result.getValue());

    b =
        getInstance(new OrNode(new ValueNode<>(false), new ValueNode<>(true)),
            new AndNode(new ValueNode<>(true), new ValueNode<>(true)));
    result = b.calc(null);
    assertTrue(result.getValue());
  }

}
