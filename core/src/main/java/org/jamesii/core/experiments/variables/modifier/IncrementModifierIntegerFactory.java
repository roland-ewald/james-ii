/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import org.jamesii.core.experiments.variables.modifier.plugintype.VariableModifierFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating IncrementModifierInteger objects.
 * 
 * @author Jan Himmelspach
 */
public class IncrementModifierIntegerFactory extends VariableModifierFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2702607164082604449L;

  @Override
  public IVariableModifier<?> create(ParameterBlock parameter) {
    Integer startVal = 0;
    Integer inc = 0;
    Integer stopVal = 0;
    return new IncrementModifierInteger(startVal, inc, stopVal);
  }

}
