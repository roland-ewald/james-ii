/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.core.math.parsetree.control;

import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.TestNode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.control.AssignmentNode;
import org.jamesii.core.math.parsetree.math.AddNode;
import org.jamesii.core.math.parsetree.variables.Environment;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * The Class TestAssignmentNode.
 */
public class TestAssignmentNode extends TestNode {

  @SuppressWarnings("unchecked")
  @Override
  public void testCalc() {
    IEnvironment<String> env = new Environment<>();
    AssignmentNode<String> an = new AssignmentNode<>("ID1", new ValueNode<>(5));
    AssignmentNode<String> an2 =
        new AssignmentNode<>("ID2", new ValueNode<>(5));
    AssignmentNode<String> an3 =
        new AssignmentNode<>("ID3", new AddNode(new ValueNode<>(5),
            new ValueNode<>(5)));
    AssignmentNode<String> an4 =
        new AssignmentNode<>("ID4", new ValueNode<>(5));
    an.calc(env);
    ValueNode<Integer> val = (ValueNode<Integer>) env.getValue("ID1");
    assertTrue(val.getValue().compareTo(5) == 0);

    val = (ValueNode<Integer>) env.getValue("ID2");
    assertTrue(val == null);

    an2.calc(env);
    an3.calc(env);
    an4.calc(env);

    val = (ValueNode<Integer>) env.getValue("ID2");
    assertTrue(val.getValue().compareTo(5) == 0);
    val = (ValueNode<Integer>) env.getValue("ID3");
    assertTrue(val.getValue().compareTo(10) == 0);
    val = (ValueNode<Integer>) env.getValue("ID4");
    assertTrue(val.getValue().compareTo(5) == 0);

    AssignmentNode<String> an4_2 =
        new AssignmentNode<>("ID4", new ValueNode<>(50));
    an4_2.calc(env);
    val = (ValueNode<Integer>) env.getValue("ID4");
    assertTrue(val.getValue().compareTo(50) == 0);
  }

  @Override
  public void testClone() {
    Node n = new ValueNode<>(5);
    AssignmentNode<String> an = new AssignmentNode<>("ID1", n);

    Node n2 = null;
    try {
      n2 = (Node) an.clone();
    } catch (CloneNotSupportedException e) {
      fail(e.getMessage());
    }
    assertTrue(an != n2);

  }

  @Override
  public void testGetChildren() {
    Node n = new ValueNode<>(5);
    AssignmentNode<String> an = new AssignmentNode<>("ID1", n);
    assertTrue(an.getChildren().size() == 1);
    assertTrue(an.getChildren().get(0) == n);
  }

}
