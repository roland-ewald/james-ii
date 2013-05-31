/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.util.List;

import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Default modifier for {@link IParamBlockUpdate} instances.
 * 
 * @author Roland Ewald
 * @param <U>
 *          the type of the updates
 */
public class ParamBlockUpdateModifier<U extends IParamBlockUpdate> extends
    SequenceModifier<U> {
  static {
    SerialisationUtils.addDelegateForConstructor(
        ParamBlockUpdateModifier.class,
        new IConstructorParameterProvider<ParamBlockUpdateModifier<?>>() {
          @Override
          public Object[] getParameters(
              ParamBlockUpdateModifier<?> updateModifier) {
            return new Object[] { updateModifier.getValues() };
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -9200112922127987476L;

  /**
   * Default constructor.
   * 
   * @param rules
   *          the rules
   */
  public ParamBlockUpdateModifier(List<U> rules) {
    super(rules);
  }
}
