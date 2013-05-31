/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.experiments.RunInformation;

/**
 * ConfidenceIntervalCriterion.
 * 
 * Replications are performed until the half-width of the confidence interval
 * falls below a relative threshold or a maximum number of runs has been
 * performed.
 * 
 * @author Matthias Jeschke
 * @see <a href="<a href="http://dx.doi.org/10.1016/j.mbs.2009.06.006">Sandmann,
 *      W., Sequential estimation for prescribed statistical accuracy in
 *      stochastic simulation of biological systems, 2009</a>
 */
public abstract class DataStorageCriterion implements IReplicationCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2085768643565778230L;

  /**
   * The dataid of the object stored per computation of which the value of the
   * attribute shall be considered for the variance.
   */
  private Long dataid;

  /** The attribute name of the value to be used. */
  private String attribute;

  /** The storage. */
  private IDataStorage storage = null;

  /**
   * Instantiates a new replication variance criterion.
   * 
   * @param maxVariance
   *          the max variance, if the variance drops below this value no more
   *          replications will be requested by this criterion.
   * @param dataid
   *          the dataid of the object to be observed
   * @param attribute
   *          the attribute name if the object referred by the dataid to be used
   */
  public DataStorageCriterion(Long dataid, String attribute) {
    super();
    this.dataid = dataid;
    this.attribute = attribute;
  }

  /**
   * Sets the up storage.
   * 
   * @param rI
   *          the run information containing the setup information about the
   *          storage
   */
  private void setupStorage(RunInformation rI) {
    setStorage(SimSystem.getRegistry()
        .instantiateFactory(rI.getDataStorageFactory())
        .create(rI.getDataStorageParams()));
  }

  @Override
  public int sufficientReplications(List<RunInformation> runInformation) {
    // set up data storage with information from first RunInformation
    if (this.getStorage() == null) {
      if (runInformation.isEmpty()) {
        return 1;
      }
      try {
        this.setupStorage(runInformation.get(0));
      } catch (Exception e) {
        SimSystem.report(e);
        return 1;
      }
    }
    return this.getNumReplications(runInformation);
  }

  protected abstract int getNumReplications(List<RunInformation> runInformation);

  /**
   * Get the data storage.
   * 
   * @return
   */
  protected IDataStorage getStorage() {
    return storage;
  }

  /**
   * Set the data storage.
   * 
   * @param storage
   */
  protected void setStorage(IDataStorage storage) {
    this.storage = storage;
  }

  /**
   * @return the dataid
   */
  protected Long getDataid() {
    return dataid;
  }

  /**
   * @return the attribute
   */
  protected String getAttribute() {
    return attribute;
  }
}
