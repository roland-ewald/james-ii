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
import org.jamesii.core.math.parsetree.math.PowerNode;

/**
 * The Class TestPowerNode.
 */
public class TestPowerNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new PowerNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode node = getInstance(new ValueNode<>(2.), new ValueNode<>(1.));
    ValueNode<Double> res = node.calc(null);
    assertTrue(res.getValue().compareTo(2.) == 0);

    BinaryNode node2 = getInstance(new ValueNode<>(2), new ValueNode<>(1));
    ValueNode<Double> res2 = node2.calc(null);
    assertTrue(res2.getValue().compareTo(2.) == 0);

    BinaryNode node3 = getInstance(new ValueNode<>(2), new ValueNode<>(3.));
    ValueNode<Double> res3 = node3.calc(null);
    assertTrue(res3.getValue().compareTo(8.) == 0);

    node3 = getInstance(new ValueNode<>(2), new ValueNode<>(0.));
    res3 = node3.calc(null);
    assertTrue(res3.getValue().compareTo(1.) == 0);

    BinaryNode node4 =
        getInstance(getInstance(new ValueNode<>(1), new ValueNode<>(3.)),
            getInstance(new ValueNode<>(2), new ValueNode<>(3.)));
    ValueNode<Double> res4 = node4.calc(null);
    assertTrue(res4.getValue().compareTo(1.) == 0);

    node4 =
        getInstance(getInstance(new ValueNode<>(2), new ValueNode<>(3.)),
            getInstance(new ValueNode<>(2), new ValueNode<>(3.)));
    res4 = node4.calc(null);
    assertTrue(res4.getValue().compareTo(16777216.) == 0);

    node4 =
        getInstance(getInstance(new ValueNode<>(2), new ValueNode<>(3.)),
            getInstance(new ValueNode<>(2), new ValueNode<>(1.)));
    res4 = node4.calc(null);
    assertTrue(res4.getValue().compareTo(64.) == 0);

  }

}
