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
import org.jamesii.core.util.misc.Pair;

/**
 * Node that represents a case statement.
 * 
 * Empty list indicates DEFAULT value of case.
 * 
 * @author Jan Himmelspach
 */
public class CaseNode extends Node {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 2167543436478039433L;

  /** The case stmt. */
  private INode caseStmt;

  /** The case terms. */
  private List<Pair<? extends Comparable<?>, INode>> caseTerms;

  /**
   * Instantiates a new case node.
   * 
   * @param c
   *          the c
   * @param caseTerms
   *          the case terms
   */
  public CaseNode(INode c, List<Pair<? extends Comparable<?>, INode>> caseTerms) {
    super();
    caseStmt = c;
    this.caseTerms = caseTerms;
  }

  @SuppressWarnings({ "unchecked", "cast", "rawtypes" })
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {

    ValueNode val = caseStmt.calc(cEnv);

    INode defaultExpr = null;

    for (Pair<? extends Comparable, INode> p : caseTerms) {
      // default case
      if (p.getFirstValue() == null) {
        defaultExpr = p.getSecondValue();
        // continue with checking all other cases
        continue;
      }

      // any other than default case
      if (p.getFirstValue().compareTo(val.getValue()) == 0) {
        return (N) p.getSecondValue().calc(cEnv);
      }
    }

    if (defaultExpr != null) {
      return (N) defaultExpr.calc(cEnv);
    }

    return null;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder("case " + getCaseStmt() + " of ");

    for (Pair<? extends Comparable<?>, INode> p : getCaseTerms()) {
      result.append("\n  " + p.getFirstValue() + " : " + p.getSecondValue());
    }

    result.append("esac");
    return result.toString();
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(caseStmt);
    for (Pair<? extends Comparable<?>, INode> p : caseTerms) {
      result.add(p.getSecondValue());
    }
    return result;
  }

  /**
   * Gets the case stmt.
   * 
   * @return the case stmt
   */
  public INode getCaseStmt() {
    return caseStmt;
  }

  /**
   * Gets the case terms.
   * 
   * @return the case terms
   */
  public List<Pair<? extends Comparable<?>, INode>> getCaseTerms() {
    return caseTerms;
  }
}
