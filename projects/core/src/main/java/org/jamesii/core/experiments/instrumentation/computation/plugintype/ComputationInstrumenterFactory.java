/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.plugintype;

import java.net.URI;
import java.util.List;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class of all factories that create {@link IComputationInstrumenter}
 * entities.
 * 
 * @author Roland Ewald
 */
public abstract class ComputationInstrumenterFactory extends
    Factory<IComputationInstrumenter> implements IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3482416810840810240L;

  /**
   * Create simulation instrumenter.
   * 
   * @param parameter
   *          parameters needed to instantiate instrumenter
   * 
   * @return a newly created simulation instrumenter
   */
  @Override
  public abstract IComputationInstrumenter create(ParameterBlock parameter);

  /**
   * Check for implemented model.
   * 
   * @param parameters
   *          the parameters
   * @param supportedInterfaces
   *          the supported interfaces
   * 
   * @return the degree of compatibility
   */
  protected int checkForImplementedModel(ParameterBlock parameters,
      List<Class<?>> supportedInterfaces) {
    URI uri =
            parameters
                .getSubBlockValue(AbstractComputationInstrumenterFactory.MODELURI);
    String className = uri.getSchemeSpecificPart().substring(2);
    try {
      Class<?> modelClass = Class.forName(className);
      for (Class<?> supportedInterface : supportedInterfaces) {
        if (supportedInterface.isAssignableFrom(modelClass)) {
          return 1;
        }
      }
    } catch (ClassNotFoundException ex) {
      return 0;
    }
    return 0;
  }

}
