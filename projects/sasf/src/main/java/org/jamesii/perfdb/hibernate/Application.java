/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.util.Date;

import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IResultDataProvider;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;

/**
 * Hibernate implementation of an application.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
// Hibernate uses the private methods
public class Application extends IDEntity implements IApplication {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7895415503505154226L;

  /** The simulation data provider. */
  private IResultDataProvider<?> resultDataProvider;

  /**
   * The simulation problem instance that shall be solved with the given
   * configuration.
   */
  private ProblemInstance problemInstance;

  /** The runtime configuration that was used for solving the problem instance. */
  private RuntimeConfiguration runtimeConfig;

  /** Configuration of infrastructure. */
  private HardwareSetup hardwareSetup;

  /** The execution date. */
  private Date executionDate;

  /**
   * Empty constructor for beans compliance.
   */
  public Application() {
  }

  /**
   * Instantiates a new application.
   * 
   * @param pInst
   *          the problem instance
   * @param rtConfig
   *          the runtime configuration
   * @param hwSetup
   *          the hardware setup
   * @param resultProv
   *          the result data provider
   * @param execDate
   *          the date of execution
   */
  public Application(ProblemInstance pInst, RuntimeConfiguration rtConfig,
      HardwareSetup hwSetup, IResultDataProvider<?> resultProv, Date execDate) {
    problemInstance = pInst;
    runtimeConfig = rtConfig;
    hardwareSetup = hwSetup;
    resultDataProvider = resultProv;
    executionDate = execDate;
  }

  @Override
  public IResultDataProvider<?> getDataProvider() {
    return resultDataProvider;
  }

  @Override
  public IProblemInstance getProblemInstance() {
    return problemInstance;
  }

  @Override
  public IRuntimeConfiguration getRuntimeConfiguration() {
    return runtimeConfig;
  }

  @Override
  public IHardwareSetup getSetup() {
    return hardwareSetup;
  }

  @Override
  public void setDataProvider(IResultDataProvider<?> dataProvider) {
    resultDataProvider = dataProvider;
  }

  @Override
  public void setProblemInstance(IProblemInstance probInst) {
    problemInstance = (ProblemInstance) probInst;
  }

  @Override
  public void setRuntimeConfiguration(IRuntimeConfiguration rtConfig) {
    PerformanceDatabase.checkForHibernateEntities(new Object[] { rtConfig },
        RuntimeConfiguration.class);
    runtimeConfig = (RuntimeConfiguration) rtConfig; // NOSONAR:{checked_above}
  }

  @Override
  public void setSetup(IHardwareSetup setup) {
    PerformanceDatabase.checkForHibernateEntities(new Object[] { setup },
        HardwareSetup.class);
    hardwareSetup = (HardwareSetup) setup; // NOSONAR:{checked_above}
  }

  @Override
  public Date getExecutionDate() {
    return executionDate;
  }

  private RuntimeConfiguration getRuntimeConfig() {
    return runtimeConfig; // NOSONAR:{used_by_hibernate}
  }

  private void setRuntimeConfig(RuntimeConfiguration rtConfig) {
    runtimeConfig = rtConfig; // NOSONAR:{used_by_hibernate}
  }

  private ProblemInstance getProblemInst() {
    return problemInstance; // NOSONAR:{used_by_hibernate}
  }

  private void setProblemInst(ProblemInstance prInst) {
    problemInstance = prInst; // NOSONAR:{used_by_hibernate}
  }

  private HardwareSetup getHardwareSetup() {
    return hardwareSetup; // NOSONAR:{used_by_hibernate}
  }

  private void setHardwareSetup(HardwareSetup hwSetup) {
    hardwareSetup = hwSetup; // NOSONAR:{used_by_hibernate}
  }

  private void setExecutionDate(Date execDate) {
    executionDate = execDate; // NOSONAR:{used_by_hibernate}
  }

}
