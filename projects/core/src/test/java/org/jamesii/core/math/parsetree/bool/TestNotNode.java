/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.bool;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.TestValueNodeAbstract;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.bool.NotNode;

/**
 * The Class TestNotNode.
 */
public class TestNotNode extends TestValueNodeAbstract<NotNode, INode> {

  @Override
  protected NotNode getInstance(INode content) {
    return new NotNode(content);
  }

  @Override
  public void testCalc() {

    NotNode n = getInstance(getA(1));

    ValueNode<Boolean> res = n.calc(null);
    assertTrue(res.getValue() == true);

    NotNode n2 = new NotNode(new NotNode(new ValueNode<>(false)));
    res = n2.calc(null);
    assertTrue(res.getValue() == false);

  }

  @Override
  protected INode getA(int index) {
    switch (index) {
    case 0: {
      return new ValueNode<>(true);
    }
    case 1: {
      return new ValueNode<>(false);
    }
    }

    return null;
  }

}
