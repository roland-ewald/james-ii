/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.Environment;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Modifies a value with a mathematical expression.
 * 
 * @author Jan Himmelspach
 */
public abstract class MathModifier<T> extends VariableModifier<T> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 990216663108053903L;

  /**
   * the math expression
   */
  private INode mathExpression;

  /**
   * The environment which has to be linked by all identifier nodes of the
   * mathExpression.
   */
  private IEnvironment<String> environment = new Environment<>();

  /**
   * Default constructor.
   * 
   * @param mathExpression
   *          the math expression
   */
  public MathModifier(INode mathExpression) {
    super();
    this.mathExpression = mathExpression;
    reset();
  }

  @Override
  public T next(ExperimentVariables variables) throws NoNextVariableException {

    // move, link variables to the environment of the math expression
    ExperimentVariables vars = variables;

    StringBuffer level = new StringBuffer();

    while (vars != null) {
      for (ExperimentVariable<?> v : vars.getVariables()) {
        environment.setValue(level + v.getName(), v.getValue());
      }
      vars = vars.getSubLevel();
      level.append(".");
    }

    // compute the math expression
    ValueNode<T> result = getMathExpression().calc(environment);

    if (isFinished(result.getValue())) {
      throw new NoNextVariableException();
    }

    return result.getValue();
  }

  @Override
  public void reset() {

  }

  /**
   * Stop.
   * 
   * @param value
   *          the value
   * 
   * @return true, if successful
   */
  protected abstract boolean isFinished(T value);

  /**
   * Sets the math expression.
   * 
   * @param mathExpression
   *          the new math expression
   */
  public final void setMathExpression(INode mathExpression) {
    this.mathExpression = mathExpression;
  }

  /**
   * Gets the math expression.
   * 
   * @return the math expression
   */
  public final INode getMathExpression() {
    return mathExpression;
  }

}
