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
 * A simple increment modifier for doubles, see {@link IncrementModifierInteger} for the integer "sibling". 
 * 
 * @author Roland Ewald, Jan Himmelspach
 */
public class IncrementModifierDouble extends AbstractIncrementModifier<Double> {  


  static {
    SerialisationUtils.addDelegateForConstructor(
        IncrementModifierDouble.class,
        new IConstructorParameterProvider<IncrementModifierDouble>() {
          @Override
          public Object[] getParameters(IncrementModifierDouble modifier) {
            Object[] params =
                new Object[] { modifier.getStartValue(),
                    modifier.getIncrementBy(), modifier.getStopValue() };
            return params;
          }
        });
  }
  
  /** Serialisation ID. */
  private static final long serialVersionUID = -407186006312595157L;
  
  public IncrementModifierDouble(Double firstValue, Double inc, Double lastValue) {
    super(firstValue, inc, lastValue);    
  }

  @Override
  public Double next(ExperimentVariables variables)
      throws NoNextVariableException {
        
    Double result = getCurrentValue() + getIncrementBy();
    setCurrentValue (result); 
    if (result.compareTo(getStopValue()) > 0) {
      throw new NoNextVariableException();
    }

    return result;
  }

  @Override
  protected Double getInitValue() {
    return getStartValue() - getIncrementBy();
  }

 

}
