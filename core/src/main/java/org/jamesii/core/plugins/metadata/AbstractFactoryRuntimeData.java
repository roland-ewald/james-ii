/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Runtime data for {@link AbstractFactory} implementations.
 * 
 * @author Roland Ewald
 */
public class AbstractFactoryRuntimeData<F extends Factory<?>, AF extends AbstractFactory<F>> {
  static {
    SerialisationUtils.addDelegateForConstructor(
        AbstractFactoryRuntimeData.class,
        new IConstructorParameterProvider<AbstractFactoryRuntimeData<?, ?>>() {
          @Override
          public Object[] getParameters(
              AbstractFactoryRuntimeData<?, ?> oldInstance) {
            return new Object[] { oldInstance.getAbstractFactoryClass(),
                oldInstance.getBaseFactoryClass() };
          }
        });
  }

  private Map<String, Serializable> additionalData = new HashMap<>();

  /** The base factory class. */
  private final Class<F> baseFactoryClass;

  /** The abstract factory class. */
  private final Class<AF> abstractFactoryClass;

  /**
   * Instantiates a new abstract factory runtime data.
   * 
   * @param abstrFactoryClass
   *          the abstract factory class
   * @param baFactoryClass
   *          the base factory class
   */
  AbstractFactoryRuntimeData(Class<AF> abstrFactoryClass,
      Class<F> baFactoryClass) {
    abstractFactoryClass = abstrFactoryClass;
    baseFactoryClass = baFactoryClass;
  }

  /**
   * Gets the base factory class.
   * 
   * @return the baseFactoryClass
   */
  public Class<F> getBaseFactoryClass() {
    return baseFactoryClass;
  }

  /**
   * Gets the abstract factory class.
   * 
   * @return the abstractFactoryClass
   */
  public Class<AF> getAbstractFactoryClass() {
    return abstractFactoryClass;
  }

  /**
   * Gets the additional data.
   * 
   * @return the additional data
   */
  public Map<String, Serializable> getAdditionalData() {
    return additionalData;
  }

  /**
   * Sets the additional data.
   * 
   * @param additionalData
   *          the additional data
   */
  public void setAdditionalData(Map<String, Serializable> additionalData) {
    this.additionalData = additionalData;
  }

  /**
   * Gets data stored for a specific ID.
   * 
   * @param dataId
   *          the data id
   * @return the data
   */
  @SuppressWarnings("unchecked")
  public <X> X getAdditionalData(String dataId) {
    return (X) this.additionalData.get(dataId);
  }

  /**
   * Sets the additional data.
   * 
   * @param dataId
   *          the data id
   * @param data
   *          the data
   */
  public void setAdditionalData(String dataId, Serializable data) {
    this.additionalData.put(dataId, data);
  }

  /**
   * Checks for additional data with the given id.
   * 
   * @param dataId
   *          the data id
   * @return true, if data is non-null
   */
  public boolean hasAdditionalData(String dataId) {
    return this.additionalData.get(dataId) != null;
  }

}
