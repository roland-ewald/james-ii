/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import org.jamesii.core.math.parsetree.NodeFactory;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.math.AbsNode;
import org.jamesii.core.math.parsetree.math.MultNode;

import junit.framework.TestCase;

public class TestNodeFactory extends TestCase {

  public void testCreateNode() {
    NodeFactory nf = new NodeFactory();
    AbsNode n = nf.createNode(AbsNode.class, new ValueNode<>(-2));

    ValueNode<Double> resAbs = n.calc(null);

    assertTrue(resAbs.getValue().compareTo(2.) == 0);

    MultNode m =
        nf.createNode(MultNode.class, new ValueNode<>(-2), new ValueNode<>(3.));

    ValueNode<Double> resMult = m.calc(null);

    assertTrue(resMult.getValue().compareTo(-6.) == 0);
  }

}
