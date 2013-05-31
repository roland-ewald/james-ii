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
import org.jamesii.core.math.parsetree.bool.IsGreaterOrEqualNode;
import org.jamesii.core.math.parsetree.math.AddNode;

/**
 * The Class TestIsGreaterOrEqualNode.
 */
public class TestIsGreaterOrEqualNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new IsGreaterOrEqualNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode b = getInstance(new ValueNode<>(0), new ValueNode<>(1));
    ValueNode<Boolean> result = b.calc(null);
    assertTrue(result.getValue() == false);

    b = getInstance(new ValueNode<>(5), new ValueNode<>(1));
    result = b.calc(null);
    assertTrue(result.getValue() == true);

    b = getInstance(new ValueNode<>(5), new ValueNode<>(5));
    result = b.calc(null);
    assertTrue(result.getValue() == true);

    b = getInstance(new ValueNode<>(2.), new ValueNode<>(7.));
    result = b.calc(null);
    assertTrue(result.getValue() == false);

    b = getInstance(new ValueNode<>(42.), new ValueNode<>(3.5));
    result = b.calc(null);
    assertTrue(result.getValue() == true);

    // check sub tree handling
    b =
        getInstance(new AddNode(new ValueNode<>(5), new ValueNode<>(6)),
            new ValueNode<>(1));
    result = b.calc(null);
    assertTrue(result.getValue() == true);

    b =
        getInstance(new ValueNode<>(1), new AddNode(new ValueNode<>(5),
            new ValueNode<>(6)));
    result = b.calc(null);
    assertTrue(result.getValue() == false);

    b =
        getInstance(new AddNode(new ValueNode<>(1), new ValueNode<>(1)),
            new AddNode(new ValueNode<>(5), new ValueNode<>(6)));
    result = b.calc(null);
    assertTrue(result.getValue() == false);
  }

}
