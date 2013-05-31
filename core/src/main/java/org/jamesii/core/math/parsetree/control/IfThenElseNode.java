/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.control;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents an if-then-else statement.
 * 
 * @author Jan Himmelspach
 */
public class IfThenElseNode extends Node {

  private static final long serialVersionUID = -1253649706420537810L;

  /** The condition. */
  private INode condition;

  /** The then stmt. */
  private INode thenStmt;

  /** The else stmt. */
  private INode elseStmt;

  /**
   * The Constructor.
   * 
   * @param c
   *          the c
   * @param t
   *          the t
   * @param e
   *          the e
   */
  public IfThenElseNode(INode c, INode t, INode e) {
    this.condition = c;
    this.thenStmt = t;
    this.elseStmt = e;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<Boolean> cond = condition.calc(cEnv);
    if (cond.getValue()) {
      return (N) thenStmt.calc(cEnv);
    }

    return (N) elseStmt.calc(cEnv);
  }

  @Override
  public String toString() {
    String result =
        "if " + getCondition() + " then \n  " + getThenStmt() + "\nelse\n  "
            + getElseStmt() + "\nfi";

    return result;
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(condition);
    result.add(thenStmt);
    result.add(elseStmt);
    return result;
  }

  /**
   * Gets the condition.
   * 
   * @return the condition
   */
  public INode getCondition() {
    return condition;
  }

  /**
   * Gets the then stmt.
   * 
   * @return the then stmt
   */
  public INode getThenStmt() {
    return thenStmt;
  }

  /**
   * Gets the else stmt.
   * 
   * @return the else stmt
   */
  public INode getElseStmt() {
    return elseStmt;
  }
}
