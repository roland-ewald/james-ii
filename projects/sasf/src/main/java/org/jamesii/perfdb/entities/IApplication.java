/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.util.Date;

/**
 * Interface of objects representing the application of a configuration to a
 * simulation problem instance and a hardware setup. It also provides an
 * (optional) object of type {@link IResultDataProvider}, which allows to look
 * up simulation results. This makes it possible to define performance measures
 * based on validity and accuracy.
 * 
 * @author Roland Ewald
 */
public interface IApplication extends IIDEntity {

  /**
   * Gets the problem instance.
   * 
   * @return the problem instance
   */
  IProblemInstance getProblemInstance();

  /**
   * Sets the problem instance.
   * 
   * @param problemInstance
   *          the new problem instance
   */
  void setProblemInstance(IProblemInstance problemInstance);

  /**
   * Gets the runtime configuration.
   * 
   * @return the runtime configuration
   */
  IRuntimeConfiguration getRuntimeConfiguration();

  /**
   * Sets the runtime configuration.
   * 
   * @param runtimeConfiguration
   *          the new runtime configuration
   */
  void setRuntimeConfiguration(IRuntimeConfiguration runtimeConfiguration);

  /**
   * Gets the hardware setup.
   * 
   * @return the setup
   */
  IHardwareSetup getSetup();

  /**
   * Sets the hardware setup.
   * 
   * @param setup
   *          the new setup
   */
  void setSetup(IHardwareSetup setup);

  /**
   * Gets the result data provider.
   * 
   * @return the result data provider
   */
  IResultDataProvider<?> getDataProvider();

  /**
   * Sets the result data provider.
   * 
   * @param dataProvider
   *          the new result data provider
   */
  void setDataProvider(IResultDataProvider<?> dataProvider);

  /**
   * Gets the execution date of the application.
   * 
   * @return the execution date
   */
  Date getExecutionDate();
}
