/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.plugintype;

import java.util.List;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.formalism.Formalism;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for all model formalism factories
 * 
 * This is not "really" a model factory: no model is instantiated and returned,
 * except for an 'empty' model which describes with which a model editor should
 * start ( {@link #create()} ) .
 * 
 * In addition a model factory should return a
 * {@link org.jamesii.core.model.formalism.Formalism} object via the
 * {@link #getFormalism()} method. This formalism description can be used later
 * on by a GUI or wherever else to display information about the formalism. In
 * addition it can be used to automatically determine the type of the formalism.
 * 
 * @author Jan Himmelspach
 */
public abstract class ModelFactory extends Factory<ISymbolicModel<?>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7080111444178638957L;

  /**
   * Instantiates a new model factory.
   */
  public ModelFactory() {
    super();
  }

  /**
   * Creates an 'empty' model.
   * 
   * @return the i symbolic model<?>
   */
  public abstract ISymbolicModel<?> create();

  @Override
  public ISymbolicModel<?> create(ParameterBlock block, Context context) {
    return create();
  }

  /**
   * Returns an object describing the formalism.
   * 
   * @return the formalism
   */
  public abstract Formalism getFormalism();

  /**
   * Return the list of interfaces used for models of this formalism ( IModel
   * extending interfaces).
   * 
   * @return the supported interfaces
   */
  public abstract List<Class<? extends IModel>> getSupportedInterfaces();

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder(super.toString());

    Formalism f = getFormalism();
    result.append("\n ");
    result.append(f.getAcronym());
    result.append(" (");
    result.append(f.getName());
    result.append(")");
    result.append("\n ");
    result.append(f.getComment());

    result.append("\n Model classes interfaces");
    if (getSupportedInterfaces() != null) {
      for (Class<?> c : getSupportedInterfaces()) {
        result.append("\n  - ");
        result.append(c.getName());
      }
    } else {
      result.append("\n  No model interfaces specified");
    }

    return result.toString();
  }

}
