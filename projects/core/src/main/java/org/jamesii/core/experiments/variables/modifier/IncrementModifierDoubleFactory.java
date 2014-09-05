/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.modifier.plugintype.VariableModifierFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating IncrementModifierDouble objects.
 * 
 * @author Jan Himmelspach
 */
public class IncrementModifierDoubleFactory extends VariableModifierFactory {

  private static final long serialVersionUID = -4454475484313753768L;

  @Override
  public IVariableModifier<?> create(ParameterBlock parameter, Context context) {
    Double startVal = 0.;
    Double inc = 0.;
    Double stopVal = 0.;
    return new IncrementModifierDouble(startVal, inc, stopVal);
  }

}
