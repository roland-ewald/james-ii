/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.math.integrators.IOde;
import org.jamesii.core.math.integrators.IOdeJacobian;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;
import org.jamesii.core.math.parsetree.variables.Identifier;

/**
 * The Class ODENode, represents an ordinary differential equation.
 * 
 * @author Stefan Leye
 * @author Sebastian Nähring
 */
public class ODENode extends Node implements IOde {

  /** The serial id. */
  private static final long serialVersionUID = -2232364531684963790L;

  /** The variable the ode calculates. */
  private String variable;

  /** The equation. */
  private Node equation;

  /** The environment holding the variable values for the initial state. */
  private IEnvironment<String> initialEnv;

  /** The local constant parameters. */
  private final Map<String, Double> localParams = new HashMap<>();

  /** The mapping from variable name to array index. */
  private final Map<String, Integer> variableMapping = new HashMap<>();

  /**
   * Instantiates a new ODE node. Initializes the variable mapping with all
   * Identifier nodes given in the equation.
   * 
   * @param variable
   *          the variable
   * @param equation
   *          the equation
   */
  public ODENode(String variable, Node equation) {
    this.variable = variable;
    this.equation = equation;
    initializeMapping();
  }

  /**
   * Sets the parameters of the ode node. A mapping from identifier to array
   * index will be created using the identifiers from the params list in
   * increasing order. The former mapping will be lost. Also the initial
   * environment will be set, which will be used for the initial state as well
   * as calculations.
   * 
   * @param params
   *          the identifiers which are needed for this node
   * @param initialEnv
   *          the initial enivronment holding the values for variables
   */
  public void setParams(List<Identifier<String>> params,
      IEnvironment<String> initialEnv) {
    if (params != null) {
      variableMapping.clear();
      int currentIndex = 0;
      for (Identifier<String> node : params) {
        variableMapping.put(node.getIdent(), currentIndex);
        currentIndex++;
      }
    }
    this.initialEnv = initialEnv;
  }

  /**
   * Same as above but doesn't set the parameters.
   * 
   * @param initialEnv
   *          the initial environment
   */
  public void setParams(IEnvironment<String> initialEnv) {
    setParams(null, initialEnv);
  }

  /**
   * Sets the parameter with the name of the given identifier to a local
   * parameter with the given value. So whenever the value of the node is
   * evaluated with a given environment the value of identifiers with this name
   * are overwritten.
   * 
   * @param ident
   *          the ident
   * @param value
   *          its value
   */
  public void setAsLocalParameter(String ident, Double value) {
    localParams.put(ident, value);
  }

  /**
   * Calls {@link #setAsLocalParameter(String, Double)} for all mappings in the
   * given map.
   * 
   * @param parameters
   *          the map with identifiers and values
   */
  public void setAsLocalParameters(Map<String, Double> parameters) {
    for (Entry<String, Double> parameter : parameters.entrySet()) {
      setAsLocalParameter(parameter.getKey(), parameter.getValue());
    }
  }

  /**
   * Overwrites all identifiers in the given environment that are within the
   * local parameters map with the same name.
   * 
   * @param <K>
   * @param cEnv
   *          the environment
   * @return a environment with overwritten value for identifiers in
   *         {@link #localParams}
   */
  @SuppressWarnings("unchecked")
  private <K extends Serializable> IEnvironment<K> updateLocals(
      IEnvironment<K> cEnv) {

    for (Entry<String, Double> e : localParams.entrySet()) {
      cEnv.setValue((K) (e.getKey()), e.getValue());
    }

    return cEnv;
  }

  /**
   * Returns the name of the variable the ode calculates.
   * 
   * @return the variable name
   */
  public String getVariable() {
    return this.variable;
  }

  /**
   * Returns the equation belonging to this node.
   * 
   * @return an INode holding the ode
   */
  public INode getEquation() {
    return this.equation;
  }

  /**
   * Sets the equation of this node to the given one.
   * 
   * @param equation
   *          the equation
   */
  public void setEquation(Node equation) {
    this.equation = equation;
  }

  /**
   * Initiates the {@link #variableMapping} using the dependencies from the
   * {@link #equation}.
   */
  @SuppressWarnings("unchecked")
  private void initializeMapping() {
    int currentIndex = 0;
    // add all dependencies to the mapping in increasing order
    for (INode node : Node.getDependencies(equation, 0)) {
      if (node instanceof Identifier<?>) {
        variableMapping.put(((Identifier<String>) node).getIdent(),
            currentIndex);
        currentIndex++;
      }
    }
  }

  @Override
  public double[] getInitialState() {
    return initialEnv == null ? null : new double[] { (Double) initialEnv
        .getValue(variable) };
  }

  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    return equation.calc(updateLocals(cEnv));
  }

  @Override
  public double[] calculate(double[] variables, double t) {
    // update environment with values from the array depending on the mapping
    for (Entry<String, Integer> ve : variableMapping.entrySet()) {
      initialEnv.setValue(ve.getKey(), variables[ve.getValue()]);
    }

    // calculate the value using the environment, update local values
    INode result = calc(updateLocals(initialEnv));
    if (result instanceof ValueNode<?>) {
      return new double[] { ((Double) ((ValueNode<?>) result).getValue() + 0d) };
    }

    return null;
  }

  @Override
  public IOdeJacobian getJacobian() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getDimension() {
    return 1;
  }

  @Override
  public Map<String, Integer> getVariableMapping() {
    return variableMapping;
  }

  @Override
  public Map<String, Integer> getResultMapping() {
    Map<String, Integer> result = new HashMap<>();
    result.put(getVariable(), 0);
    return result;
  }

}
