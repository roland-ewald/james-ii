/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.TestValueNodeAbstract;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.math.AbsNode;
import org.jamesii.core.math.parsetree.math.MultNode;

/**
 * The Class TestAbsNode.
 */
public class TestAbsNode extends TestValueNodeAbstract<AbsNode, INode> {

  @Override
  public void testCalc() {
    INode node = getInstance(getA(0));

    ValueNode<Double> res = node.calc(null);
    assertTrue(res.getValue().compareTo(1.) == 0);

    node = getInstance(getA(1));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(1.) == 0);

    node = getInstance(getA(2));
    res = node.calc(null);
    assertTrue(res.getValue().compareTo(0.) == 0);

    node = getInstance(getA(3));
    ValueNode<Double> res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(1.) == 0);

    node = getInstance(getA(4));
    res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(1.) == 0);

    node = getInstance(getA(5));
    res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(0.) == 0);

    node =
        getInstance((new MultNode(new ValueNode<>(2), new ValueNode<>(-2.))));
    res2 = node.calc(null);
    assertTrue(res2.getValue().compareTo(4.) == 0);
  }

  @Override
  protected INode getA(int index) {
    switch (index) {
    case 0: {
      return new ValueNode<>(1);
    }
    case 1: {
      return new ValueNode<>(-1);
    }
    case 2: {
      return new ValueNode<>(0);
    }
    case 3: {
      return new ValueNode<>(1.);
    }
    case 4: {
      return new ValueNode<>(-1.);
    }
    case 5: {
      return new ValueNode<>(0.);
    }
    }

    return null;
  }

  @Override
  protected AbsNode getInstance(INode content) {
    return new AbsNode(content);
  }

}
