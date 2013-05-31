package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.TestBinaryNode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.math.MultNode;

/**
 * The Class TestMinusNode.
 */
public class TestMultNode extends TestBinaryNode {

  @Override
  protected BinaryNode getInstance(INode left, INode right) {
    return new MultNode(left, right);
  }

  @Override
  public void testCalc() {
    BinaryNode node = getInstance(new ValueNode<>(2.), new ValueNode<>(1.));
    ValueNode<Double> res = node.calc(null);
    assertTrue(res.getValue().compareTo(2.) == 0);

    BinaryNode node2 = getInstance(new ValueNode<>(2), new ValueNode<>(1));
    ValueNode<Integer> res2 = node2.calc(null);
    assertTrue(res2.getValue().compareTo(2) == 0);

    BinaryNode node3 = getInstance(new ValueNode<>(2), new ValueNode<>(3.));
    ValueNode<Double> res3 = node3.calc(null);
    assertTrue(res3.getValue().compareTo(6.) == 0);

    BinaryNode node4 =
        getInstance(getInstance(new ValueNode<>(1), new ValueNode<>(3.)),
            getInstance(new ValueNode<>(2), new ValueNode<>(3.)));
    ValueNode<Double> res4 = node4.calc(null);
    assertTrue(res4.getValue().compareTo(18.) == 0);

  }

}
