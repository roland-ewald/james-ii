package org.jamesii.core.math.simpletree;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.simpletree.binary.AddNode;
import org.jamesii.core.math.simpletree.binary.DivNode;
import org.jamesii.core.math.simpletree.binary.PowerNode;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;
import org.jamesii.core.math.simpletree.nullary.VariableNode;
import org.jamesii.core.math.simpletree.unary.AbstractUnaryNode;
import org.jamesii.core.math.simpletree.unary.InverseNode;
import org.jamesii.core.math.simpletree.unary.SquareNode;

/**
 * @author Arne Bittig
 * @date 30.09.2014
 */
public class NodeTest extends ChattyTestCase {

  public void testSimplify() {
    VariableNode varX = new VariableNode("x");
    DoubleNode testCaseMult =
        new DivNode(new InverseNode(new AddNode(new FixedValueNode(0.),
            new FixedValueNode(1.))), new InverseNode(new PowerNode(varX,
            new FixedValueNode(2.))));
    // (1/(0+1))/(1/x^2)
    DoubleNode testCaseMultSimple = testCaseMult.simplify();
    assertTrue(testCaseMultSimple instanceof SquareNode); // x²
    assertSame(varX, ((AbstractUnaryNode) testCaseMultSimple).getSub());
  }
}
