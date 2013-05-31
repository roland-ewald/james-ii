/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.experiments.variables.modifier.IVariableModifier;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.model.variables.IVariable;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * An experiment variable, belongs to the {@link ExperimentVariables} in the
 * {@link org.jamesii.core.experiments.BaseExperiment}.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * @param <V>
 *          the type of the experiment variable's value
 */
public class ExperimentVariable<V> extends NamedEntity implements IVariable<V> {
  static {
    SerialisationUtils.addDelegateForConstructor(ExperimentVariable.class,
        new IConstructorParameterProvider<ExperimentVariable<?>>() {
          @Override
          public Object[] getParameters(ExperimentVariable<?> expVar) {
            Object[] params =
                new Object[] { expVar.getName(), expVar.getValue(),
                    expVar.getModifier() };
            return params;
          }
        });
  }

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 8839470542101466789L;

  /**
   * Initial value.
   */
  private V initialValue;

  /**
   * Reference to the object that modifies the variable.
   */
  private IVariableModifier<V> modifier;

  /**
   * Value of variable.
   */
  private V value;

  /**
   * Default constructor.
   * 
   * @param name
   *          name of the variable
   * @param value
   *          current and initial value of the variable
   * @param modifier
   *          the modifier to be used
   */
  @Deprecated
  public ExperimentVariable(String name, V value, IVariableModifier<V> modifier) {
    super(name);
    this.initialValue = value;
    this.value = value;
    if (modifier != null) {
      this.modifier = modifier;
    } else {
      List<V> singleValueList = new ArrayList<V>();
      singleValueList.add(value);
      this.modifier = new SequenceModifier<>(singleValueList);
    }
  }

  /**
   * Single value exp var
   * 
   * @param name
   *          variable name
   * @param value
   *          value
   */
  public ExperimentVariable(String name, V value) {
    super(name);
    this.initialValue = value;
    this.value = value;
    List<V> singleValueList = new ArrayList<V>();
    singleValueList.add(value);
    this.modifier = new SequenceModifier<>(singleValueList);
  }

  /**
   * Exp var with modifier.
   * 
   * @param name
   *          name of the variable
   * @param modifier
   *          the modifier to be used
   */
  public ExperimentVariable(String name, IVariableModifier<V> modifier) {
    super(name);
    if (modifier == null) {
      throw new IllegalStateException();
    }
    this.initialValue = null;
    this.value = null;
    this.modifier = modifier;
  }

  /**
   * the initial value of the variable.
   * 
   * @return the initial value of the variable
   */
  public V getInitialValue() {
    return initialValue;
  }

  /**
   * Gets the modifier.
   * 
   * @return the modifier
   */
  public IVariableModifier<V> getModifier() {
    return modifier;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  @Override
  public V getValue() {
    return value;
  }

  /**
   * Compute the next variable values. Calls its modifier if there is a new
   * value. If there is no next variable the variable's value will remain on the
   * the last value.
   * 
   * @param variables
   *          experiment variables
   * @return true if there is a next variable otherwise false
   */
  public boolean next(ExperimentVariables variables) {
    try {
      value = modifier.next(variables);
      return true;
    } catch (NoNextVariableException nnve) {
      return false;
    }

  }

  /**
   * Reset the variable and the associated modifier.
   */
  public void reset() {
    value = initialValue;
    modifier.reset();
  }

  /**
   * Sets the initial value.
   * 
   * @param initalValue
   *          initial value
   */
  public void setInitialValue(V initalValue) {
    this.initialValue = initalValue;
  }

  /**
   * Set modifier.
   * 
   * @param modifier
   *          the modifier of this variable
   */
  public void setModifier(IVariableModifier<V> modifier) {
    this.modifier = modifier;
  }

  /**
   * Set a new value.
   * 
   * @param val
   *          the value to be set
   */
  @Override
  public void setValue(V val) {
    value = val;
  }

  /**
   * Necessary to access variables that are owned by an
   * {@link org.jamesii.core.experiments.steering.IExperimentSteerer}, to check
   * whether this relies on results.
   * 
   * @return true, if relies on results
   */
  public boolean reliesOnResults() {
    return false;
  }

}
