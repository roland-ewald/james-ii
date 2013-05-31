/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.variables.IEnvironment;
import org.jamesii.core.math.parsetree.variables.Identifier;

/**
 * Node that represents a variable assignement.
 * 
 * @author Stefan Leye
 * 
 * @param <K>
 *          type of the identifier of the variable
 */
public class AssignmentNode<K extends Serializable> extends Node {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8183223965087185L;

  /**
   * The name of the identifier.
   */
  private K ident;

  /**
   * The value to be assigned to the identifier.
   */
  private INode value;

  /**
   * Instantiates a new assignment node.
   * 
   * @param ident
   *          the ident
   * @param value
   *          the value
   */
  public AssignmentNode(K ident, INode value) {
    this.ident = ident;
    this.value = value;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    INode v = value.calc(cEnv);
    ((IEnvironment<Serializable>) cEnv).setValue(ident, v);
    return (N) new Identifier<>(ident, v);
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(value);
    return result;
  }
}
