/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.util.List;

import org.jamesii.core.data.runtime.DatabaseCreationException;
import org.jamesii.core.data.runtime.IWriteReadDataStorage;
import org.jamesii.core.data.runtime.ObjectNotFoundException;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.data.storage.InvalidDataStorageSetupException;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.math.statistics.univariate.Variance;

/**
 * The Class ReplicationVarianceCriterion. Checks whether a selected value
 * (dataid, attribute) has already reached a certain (maximal) variance. If not
 * another replication will be executed. Please note: if the variance does never
 * drop below the value given this criterion will never stop.
 * 
 * @author Jan Himmelspach
 */
public class ReplicationVarianceCriterion implements IReplicationCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 653261938845071176L;

  /**
   * The mean sum. Internally used variable to contain the sum of all sample
   * values achieved so far (for computing the mean).
   */
  private double meanSum = 0.;

  /**
   * The variance (internally used variable which contains the current
   * variance).
   */
  private double variance = 0.;

  /**
   * The last list size. Internally used variable to find out how many
   * replications have been executed in between.
   */
  private int lastListSize = 0;

  /** The max variance. Set in the constructor. */
  private double maxVariance = 0.;

  /**
   * The dataid of the object stored per computation of which the value of the
   * attribute shall be considered for the variance.
   */
  private Long dataid;

  /** The attribute name of the value to be used. */
  private String attribute;

  /** The position of the observed species in the state vector. */
  private int observedSpecies;

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
   * @param observedSpecies
   *          the position of the observed species in the state vector
   */
  public ReplicationVarianceCriterion(Double maxVariance, Long dataid,
      String attribute, int observedSpecies) {
    super();
    this.maxVariance = maxVariance;
    this.dataid = dataid;
    this.attribute = attribute;
    this.observedSpecies = observedSpecies;
  }

  /**
   * Sets the up storage.
   * 
   * @param rI
   *          the run information containing the setup information about the
   *          storage
   */
  private void setupStorage(RunInformation rI) {

    if (storage != null) {
      return;
    }

    Class<? extends DataStorageFactory> facClass = rI.getDataStorageFactory();

    DataStorageFactory dsf = null;

    try {
      dsf = facClass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new DatabaseCreationException(ex);
    }

    storage = dsf.create(rI.getDataStorageParams());
    storage.setExperimentID(rI.getExpID());
  }

  @Override
  public int sufficientReplications(List<RunInformation> runInformation) {

    int newListSize = runInformation.size();

    if (newListSize < 10) {
      return 1;
    }

    for (; lastListSize < newListSize; lastListSize++) {
      // fetch the last run information, hopefully this method is called per
      // run
      RunInformation rI = runInformation.get(lastListSize);

      // make sure that we have access to the storage
      setupStorage(rI);

      storage.setConfigurationID(null, rI.getConfID());

      // select the last run
      storage.setComputationTaskID(null, null, rI.getComputationTaskID());

      // read the latest value
      // TODO: change handling of incoming information!
      Object sampleObj =
          ((IWriteReadDataStorage) storage).readLatestData(dataid, attribute);
      // System.out.println(sampleObj.getClass().getName());

      if (sampleObj == null) {
        throw new ObjectNotFoundException("The data object (" + dataid
            + ") / the attribute (" + attribute
            + ") to be used in the variance criterion is not in the database!");
      }

      long[] stateVector = null;
      if (sampleObj instanceof long[]) {
        stateVector = (long[]) sampleObj;
      } else {
        throw new InvalidDataStorageSetupException(
            "The data has not been stored in a vector. This criterion cannot deal with different data types so far.");
      }

      double sample = stateVector[observedSpecies];

      // startup phase
      if (lastListSize < 2) {

        if (lastListSize == 1) {
          variance = Variance.variance(new double[] { meanSum, sample });
        }
        meanSum += sample;
        continue;
      }

      // // avoiding division by zero
      // if (meanSum == 0) {
      // meanSum = sample;
      // return 1;
      // }

      variance =
          Variance.variance(variance, meanSum / (lastListSize + 1), sample,
              lastListSize + 1);

      // update the meanSum with the new sample value
      meanSum += sample;

    }

    // System.out.println("Variance is: " + variance
    // + " target is that this is lower than " + maxVariance);

    // return 0 if the variance is below the maxVariance, otherwise ask for
    // another replication
    return Double.compare(variance, maxVariance) <= 0 ? 0 : 1;
  }

  public int getInitialReplications() {
    return 1;
  }
}
