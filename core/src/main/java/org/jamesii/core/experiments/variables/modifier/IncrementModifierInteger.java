/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Increments a value by a constant increment value.
 * 
 * @author Jan Himmelspach
 */
public class IncrementModifierInteger extends AbstractIncrementModifier<Integer> {
  
  static {
    SerialisationUtils.addDelegateForConstructor(
        IncrementModifierInteger.class,
        new IConstructorParameterProvider<IncrementModifierInteger>() {
          @Override
          public Object[] getParameters(IncrementModifierInteger modifier) {
            Object[] params =
                new Object[] { modifier.getStartValue(),
                    modifier.getIncrementBy(), modifier.getStopValue() };
            return params;
          }
        });
  }
  
  /**
   * The constant serial version UID.
   */
  private static final long serialVersionUID = 5802334884437316452L;

  public IncrementModifierInteger(Integer firstValue, Integer inc,
      Integer lastValue) {
    super(firstValue, inc, lastValue);
  }

  @Override
  public Integer next(ExperimentVariables variables)
      throws NoNextVariableException {
    Integer result = getCurrentValue() + getIncrementBy();
    setCurrentValue (result); 
    if (result > getStopValue()) {
      throw new NoNextVariableException();
    }

    return result;
  }

  @Override
  protected Integer getInitValue() {
    return getStartValue() - getIncrementBy();
  }  


}
