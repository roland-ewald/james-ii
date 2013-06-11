/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.steering;

import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.modifier.IVariableModifier;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Variable that iterates through a list of {@link IExperimentSteerer}.
 * 
 * @param <X>
 *          actual type of the experiment steerer variable
 * 
 * @author Roland Ewald
 */
public class ExperimentSteererVariable<X extends IExperimentSteerer> extends
    ExperimentVariable<X> {
  static {
    SerialisationUtils.addDelegateForConstructor(
        ExperimentSteererVariable.class,
        new IConstructorParameterProvider<ExperimentSteererVariable<?>>() {
          @Override
          public Object[] getParameters(ExperimentSteererVariable<?> variable) {
            return new Object[] { variable.getName(), variable.getValue(),
                variable.getSteererClass(), variable.getModifier() };
          }
        });
  }

  /** Serialization ID. */
  private static final long serialVersionUID = -4505809870267929277L;

  /** Class of the steerer. */
  private Class<X> steererClass;

  /**
   * Default constructor.
   * 
   * @param name
   *          name of the variable
   * @param classOfSteerer
   *          class of the steerer
   * @param value
   *          initial value
   * @param sequenceModifier
   *          sequence modifier containing the rest of the values
   */
  public ExperimentSteererVariable(String name, Class<X> classOfSteerer,
      X value, IVariableModifier<X> sequenceModifier) {
    super(name, value, sequenceModifier);
    setName(name + "_" + hashCode());
    steererClass = classOfSteerer;
  }

  /**
   * Instantiates a new experiment steerer variable.
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   * @param classOfSteerer
   *          the class of steerer
   * @param sequenceModifier
   *          the sequence modifier
   */
  ExperimentSteererVariable(String name, X value, Class<X> classOfSteerer,
      IVariableModifier<X> sequenceModifier) {
    super(name, value, sequenceModifier);
    steererClass = classOfSteerer;
  }

  /**
   * Gets the steerer class.
   * 
   * @return the steerer class
   */
  public Class<X> getSteererClass() {
    return steererClass;
  }

  /**
   * Sets the steerer class.
   * 
   * @param steererClass
   *          the new steerer class
   */
  public void setSteererClass(Class<X> steererClass) {
    this.steererClass = steererClass;
  }

}
