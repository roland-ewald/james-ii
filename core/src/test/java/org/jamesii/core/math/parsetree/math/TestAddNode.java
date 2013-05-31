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
import org.jamesii.core.math.parsetree.math.AddNode;

/**
 * The Class TestAddNode.
 */
public class TestAddNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new AddNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode node = getInstance(new ValueNode<>(2.), new ValueNode<>(1.));
    ValueNode<Double> res = node.calc(null);
    assertTrue(res.getValue().compareTo(3.) == 0);

    node = getInstance(new ValueNode<>(2), new ValueNode<>(1));
    ValueNode<Integer> res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(3) == 0);

    node = getInstance(new ValueNode<>(2), new ValueNode<>(3.));
    ValueNode<Double> res3 = node.calc(null);
    assertTrue(res3.getValue().compareTo(5.) == 0);

    node =
        getInstance(getInstance(new ValueNode<>(1), new ValueNode<>(3.)),
            getInstance(new ValueNode<>(2), new ValueNode<>(3.)));
    ValueNode<Double> res4 = node.calc(null);
    assertTrue(res4.getValue().compareTo(9.) == 0);

  }

}
