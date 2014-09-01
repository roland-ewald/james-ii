/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables.modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.NoNextVariableException;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Modifier that iterates over a sequence of values.
 * 
 * @param <V>
 *          type of values in the sequence
 * 
 * @author Jan Himmelspach
 */
public class SequenceModifier<V> extends VariableModifier<V> {
  static {
    SerialisationUtils.addDelegateForConstructor(SequenceModifier.class,
        new IConstructorParameterProvider<SequenceModifier<?>>() {
          @Override
          public Object[] getParameters(SequenceModifier<?> updateModifier) {
            return new Object[] { updateModifier.getValues() };
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -4474965438899894680L;

  /**
   * Internally used iterator (for determining the next sequence item to be
   * used).
   */
  private transient Iterator<V> it;

  /** The list of values (the sequence). */
  private List<V> values;

  /**
   * Create a sequence modifier, use the passed list as sequence items.
   * 
   * @param values
   *          the sequence to be used
   */
  public SequenceModifier(List<V> values) {
    this.values = values;
    reset();
  }

  /**
   * Instantiates a new sequence modifier.
   * 
   * @param valueArray
   *          the value array
   */
  public SequenceModifier(V... valueArray) {
    this.values = new ArrayList<>();
    Collections.addAll(this.values, valueArray);
    reset();
  }

  @Override
  public V next(ExperimentVariables variables) throws NoNextVariableException {
    if (!it.hasNext()) {
      throw new NoNextVariableException();
    }
    return it.next();
  }

  @Override
  public void reset() {
    it = values.iterator();
  }

  /**
   * Gets the values.
   * 
   * @return the values
   */
  public List<V> getValues() {
    return values;
  }

  /**
   * Sets the values.
   * 
   * @param values
   *          the new values
   */
  public void setValues(List<V> values) {
    this.values = values;
    reset();
  }

}
