/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata.file;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.plugins.metadata.AbstractFactoryRuntimeData;
import org.jamesii.core.plugins.metadata.FactoryRuntimeData;
import org.jamesii.core.plugins.metadata.FailureTolerance;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Structure for runtime data as managed by the {@link RegFileDStorage}.
 * 
 * @author Roland Ewald
 */
public class RuntimeData implements Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(RuntimeData.class,
        new IConstructorParameterProvider<RuntimeData>() {
          @Override
          public Object[] getParameters(RuntimeData oldInstance) {
            return new Object[] { oldInstance.getAbstractFactoryData(),
                oldInstance.getFactoryData(), oldInstance.getFailureTolerance() };
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7009619418625705146L;

  /** The factory data map: factory class => factory runtime data. */
  private final Map<Class<? extends Factory<?>>, FactoryRuntimeData<? extends Factory<?>>> factoryData;

  /**
   * The abstract factory data map: abstract factory class => factory runtime
   * data.
   */
  private final Map<Class<? extends AbstractFactory<?>>, AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>>> abstractFactoryData;

  /** The failure tolerance of the registry. */
  private final FailureTolerance failureTolerance;

  /**
   * Instantiates new runtime data.
   * 
   * @param abstractFactoryData
   *          the abstract factory data
   * @param factoryData
   *          the factory data
   * @param failureTolerance
   *          the failure tolerance
   */
  public RuntimeData(
      Map<Class<? extends AbstractFactory<?>>, AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>>> abstractFactoryData,
      Map<Class<? extends Factory<?>>, FactoryRuntimeData<? extends Factory<?>>> factoryData,
      FailureTolerance failureTolerance) {
    this.abstractFactoryData =
        (abstractFactoryData == null) ? new HashMap<Class<? extends AbstractFactory<?>>, AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>>>()
            : abstractFactoryData;

    this.factoryData =
        (factoryData == null) ? new HashMap<Class<? extends Factory<?>>, FactoryRuntimeData<? extends Factory<?>>>()
            : factoryData;

    this.failureTolerance =
        failureTolerance == null ? FailureTolerance.ACCEPT_UNTESTED
            : failureTolerance;
  }

  /**
   * Gets the factory data.
   * 
   * @return the factory data
   */
  public Map<Class<? extends Factory<?>>, FactoryRuntimeData<? extends Factory<?>>> getFactoryData() {
    return factoryData;
  }

  /**
   * Gets the abstract factory data.
   * 
   * @return the abstract factory data
   */
  public Map<Class<? extends AbstractFactory<?>>, AbstractFactoryRuntimeData<? extends Factory<?>, ? extends AbstractFactory<?>>> getAbstractFactoryData() {
    return abstractFactoryData;
  }

  /**
   * Gets the failure tolerance.
   * 
   * @return the failure tolerance
   */
  public FailureTolerance getFailureTolerance() {
    return failureTolerance;
  }

}
