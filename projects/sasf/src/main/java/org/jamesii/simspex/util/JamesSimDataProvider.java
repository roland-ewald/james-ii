/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.perfdb.entities.IResultDataProvider;

/**
 * Implementation of a data provider that uses the James II system of data
 * storage: experiment ID and simulation ID, as well as data storage factory and
 * parameters.
 * 
 * @author Roland Ewald
 */
public abstract class JamesSimDataProvider<D> implements IResultDataProvider<D> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2112991630901807425L;

  /** Experiment ID. */
  private IUniqueID expID;

  /** Configuration ID. */
  private IUniqueID confID;

  /** Task ID. */
  private ComputationTaskIDObject taskID;

  /** Data storage factory. */
  private Class<? extends DataStorageFactory> dsFactory;

  /** Data storage parameters. */
  private ParameterBlock dsParameters;

  /** The instantiated data storage. */
  private transient IDataStorage<?> dataStorage;

  /**
   * Instantiates a new simulation data provider.
   */
  public JamesSimDataProvider() {
  }

  public JamesSimDataProvider(RunInformation runInfo) {
    expID = runInfo.getExpID();
    confID = runInfo.getConfID();
    taskID = runInfo.getComputationTaskID();
    dsFactory = runInfo.getDataStorageFactory();
    dsParameters = runInfo.getDataStorageParams();
    init();
  }

  private void init() {
    if (dsFactory != null && dsParameters != null) {
      DataStorageFactory dsf = null;
      try {
        dsf = dsFactory.newInstance();
      } catch (Exception ex) {
        throw new IllegalArgumentException(ex);
      }
      dataStorage = dsf.create(dsParameters);
    }
  }

  /**
   * Gets the exp id.
   * 
   * @return the exp id
   */
  public IUniqueID getExpID() {
    return expID;
  }

  /**
   * Sets the exp id.
   * 
   * @param expID
   *          the new exp id
   */
  public void setExpID(IUniqueID expID) {
    this.expID = expID;
  }

  /**
   * Gets the task id.
   * 
   * @return the task id
   */
  public ComputationTaskIDObject getTaskID() {
    return taskID;
  }

  /**
   * Sets the task id.
   * 
   * @param compTaksID
   *          the new task id
   */
  public void setTaskID(ComputationTaskIDObject compTaksID) {
    this.taskID = compTaksID;
  }

  /**
   * Gets the data storage factory.
   * 
   * @return the data storage factory
   */
  public Class<? extends DataStorageFactory> getDsFactory() {
    return dsFactory;
  }

  /**
   * Sets the data storage factory.
   * 
   * @param dsFactory
   *          the new data storage factory
   */
  public void setDsFactory(Class<? extends DataStorageFactory> dsFactory) {
    this.dsFactory = dsFactory;
    init();
  }

  public ParameterBlock getDsParameters() {
    return dsParameters;
  }

  public void setDsParameters(ParameterBlock dsParameters) {
    this.dsParameters = dsParameters;
    init();
  }

  /**
   * Creates the data storage.
   * 
   * @return the data storage
   * @throws InstantiationException
   *           the instantiation exception
   * @throws IllegalAccessException
   *           the illegal access exception
   */
  protected IDataStorage<?> createDataStorage() throws InstantiationException,
      IllegalAccessException {
    if (dsFactory == null) {
      return null;
    }
    if (dataStorage != null) {
      return dataStorage;
    }
    dataStorage = (dsFactory.newInstance()).create(dsParameters);
    dataStorage.setExperimentID(expID);
    dataStorage.setConfigurationID(expID, confID);
    dataStorage.setComputationTaskID(expID, confID, taskID.getId());
    return dataStorage;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof JamesSimDataProvider)) {
      return false;
    }
    JamesSimDataProvider<?> dataProvider = (JamesSimDataProvider<?>) o;

    boolean dataTypeEqual = getDataType().equals(dataProvider.getDataType());

    boolean factoriesEqual =
        ((dsFactory == null && dataProvider.getDsFactory() == null) || (dsFactory != null && dsFactory
            .equals(dataProvider.getDsFactory())));

    boolean parametersEqual =
        ((dsParameters == null && dataProvider.getDsParameters() == null) || (dsParameters != null && dsParameters
            .equals(dataProvider.getDsParameters())));

    return dataTypeEqual && factoriesEqual && parametersEqual;
  }

  @Override
  public int hashCode() {
    return (getDataType() != null ? getDataType().hashCode() : 0)
        + (dsFactory != null ? dsFactory.hashCode() : 0)
        + (dsParameters != null ? dsParameters.hashCode() : 0);
  }

}
