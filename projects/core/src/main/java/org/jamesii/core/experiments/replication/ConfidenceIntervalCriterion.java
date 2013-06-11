/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.runtime.IWriteReadDataStorage;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.math.statistics.univariate.InverseNormalDistributionFunction;
import org.jamesii.core.math.statistics.univariate.Variance;

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
public class ConfidenceIntervalCriterion extends DataStorageCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 653261938845071176L;

  /**
   * The mean sum. Internally used variable to contain the sum of all sample
   * values achieved so far (for computing the mean).
   */
  private double[] means = null;

  /**
   * The variance (internally used variable which contains the current
   * variance).
   */
  private double[] variances = null;

  /**
   * Indicates whether the required number of replications has been reached for
   * a species.
   */
  private int[] replications = null;

  /**
   * The variable to be checked.
   */
  private List<Integer> variablesToCheck = new ArrayList<>();

  /**
   * The relative half width threshold. Set in the constructor, default is 1%
   * (0.01).
   */
  private double relativeHalfWidthThreshold = 0.01;

  /**
   * The confidence level that should be achieved. Set in the constructor,
   * default is 95% (0.95).
   */
  private double confidenceLevel = 0.95;

  /**
   * The quantile corresponding to the confidence level. Its value depends on
   * the number of species: if alpha = 1 - confidence level is the significance
   * level, then alpha / N is the individual sign. level and thus 1 - (alpha/N)
   * is the individual confidence level.
   */
  private double iz = Double.NaN;

  /** Number of replications processed. */
  private int lastListSize = 0;

  /** Maximum number of replications. Set in the constructor, default is 1000. */
  private int maxReplications = 1000;

  /** Minimum number of replications. Set in the constructor, default is 100. */
  private int minReplications = 100;

  /**
   * Get the value of the minReplications.
   * 
   * @return the minReplications
   */
  public final int getMinReplications() {
    return minReplications;
  }

  /**
   * Set the minReplications to the value passed via the minReplications
   * attribute.
   * 
   * @param minReplications
   *          the minReplications to set
   */
  public final void setMinReplications(int minReplications) {
    this.minReplications = minReplications;
  }

  /**
   * Number of additional replications to be issued per estimation, default is
   * 10. A too high number means that computing time will be wasted, a too small
   * value means that parallelism might not be optimally exploited.
   */
  private int furtherReplications = 10;

  /**
   * Constructor for Bean compatibility. Should never be used manually.
   */
  public ConfidenceIntervalCriterion() {
    this(1l, "");
  }

  /**
   * Constructor. Sets half width threshold to 0.01, confidence interval to
   * 0.95, and maximum number of replications to 100.
   * 
   * @param dataid
   *          id of the data block
   * @param attribute
   *          attribute name
   */
  public ConfidenceIntervalCriterion(Long dataid, String attribute) {
    this(0.01, 0.95, 1000, 100, 10, dataid, attribute);
  }

  /**
   * Instantiates a new confidence interval criterion
   * 
   * @param relativeHalfWidthThreshold
   *          determines maximum confidence interval half width that is
   *          acceptable
   * @param confidenceLevel
   *          the confidence level that should be achieved
   * @param maxReplications
   *          maximum number of replications, the replication criterion will not
   *          ask for further replications if maxReplications have been executed
   *          - as a consequence the required confidence might not have been
   *          reached
   * @param minReplications
   *          minimum number of replications, the replication criterion will not
   *          compute any confidence parameters before this number of
   *          replications has been executed
   * @param furtherReplications
   *          number of replications to be computed in addition if neither the
   *          {@link #minReplications} is achieved nor the confidence is
   *          reached; these replications can be computed in parallel; a large
   *          number means that more replications can be computed in parallel
   *          and that the criterion is computed less times but may mean that we
   *          compute far too many replications, e.g., a single additional
   *          replication might always lead to the fulfillment of the criterion
   *          and if we ask for 100 additional replications here we might waste
   *          a lot of time
   * @param dataid
   *          id of the data block
   * @param attribute
   *          attribute name
   */
  public ConfidenceIntervalCriterion(Double relativeHalfWidthThreshold,
      Double confidenceLevel, int maxReplications, int minReplications,
      int furtherReplications, Long dataid, String attribute) {
    super(dataid, attribute);
    this.relativeHalfWidthThreshold = relativeHalfWidthThreshold;
    this.confidenceLevel = confidenceLevel;
    this.maxReplications = maxReplications;
    this.minReplications = minReplications;
    this.setFurtherReplications(furtherReplications);
  }

  /**
   * Read the data and update the internal data structure of this criterion.
   * 
   * @param runInformation
   * 
   * @param newListSize
   */
  protected void readData(List<RunInformation> runInformation, int newListSize) {
    Serializable expID = -1l;
    Serializable taskID = -1l;

    // update the means and variances for all species
    for (; lastListSize < newListSize; lastListSize++) {
      try {
        // fetch the last run information, hopefully this method is called per
        // run
        RunInformation rI = runInformation.get(lastListSize);
        int n = lastListSize + 1;

        // select the last run
        expID = getStorage().setExperimentID(rI.getExpID());
        getStorage().setConfigurationID(null, rI.getConfID());
        taskID =
            getStorage().setComputationTaskID(null, null,
                rI.getComputationTaskID());

        // read the latest value
        long[] sample =
            ((IWriteReadDataStorage<?>) getStorage()).readLatestData(
                getDataid(), getAttribute());

        if (Double.isNaN(this.iz)) {
          this.setUpQuantile(sample.length);
          this.means = new double[sample.length];
          this.variances = new double[sample.length];
          this.replications = new int[sample.length];
          for (int i = 0; i < sample.length; i++) {
            this.variablesToCheck.add(i);
          }
        }

        for (Integer i : this.variablesToCheck) {
          double s = sample[i];
          double oldMean = this.means[i];
          this.means[i] = (s + (n - 1) * oldMean) / n;
          if (n > 1) {
            this.variances[i] =
                Variance.variance(this.variances[i], oldMean, s, n);
          }
        }
      } catch (RuntimeException e) {
        SimSystem
            .report(
                Level.SEVERE,
                "Was not able to read the last value of "
                    + getAttribute()
                    + " in the computation task with the ID "
                    + runInformation.get(lastListSize).getComputationTaskID()
                    + " ("
                    + taskID
                    + ")"
                    + " in the experiment with id "
                    + runInformation.get(lastListSize).getExpID()
                    + " ("
                    + expID
                    + ")"
                    + ".\n As consequence this value will not be used. Please check your data storage for the reason.");
        SimSystem.report(e);
        continue;
      }

    }
  }

  @Override
  public int getNumReplications(List<RunInformation> runInformation) {

    int newListSize = runInformation.size();

    int lls = lastListSize;

    // make at least n replications before we use the criteria to determine the
    // required number for a certain confidence
    if (newListSize < minReplications) {
      // if we do not have computed the minimal number of replications we
      // request the next furtherReplications
      if (minReplications - newListSize > getFurtherReplications()) {
        // return max number of further replications
        return getFurtherReplications();
      }
      // return only the number of replications still missing (after that we
      // will have achieved the minimal number of replications); the number
      // returned is < furtherReplications
      return minReplications - newListSize;
    }

    // if we get here this means that we need to check for the confidence

    // at first we need to query the data source and to update the internal data
    // structures of this criterion
    readData(runInformation, newListSize);

    // if we have computed the maximum number of replications allowed by the
    // maxReplications parameters we will return 0 (no more replications)
    // independent from the confidence values computed
    if (lastListSize >= this.maxReplications) {
      return 0;
    }

    // if we get here we check for the the confidence stuff per variable; if at
    // least one is not done we will ask for executing furtherReplications
    // replications in addition

    // test if confidence interval half width is within limits set by threshold
    // if any species is not within limits, another replication is necessary
    boolean replRequired = false;
    Iterator<Integer> it = this.variablesToCheck.iterator();
    while (it.hasNext()) {
      int i = it.next();
      double bound = Math.abs(this.means[i]) * this.relativeHalfWidthThreshold;
      double hw = this.iz * (this.variances[i] / Math.sqrt(this.lastListSize));
      if (hw > bound) {
        replRequired = true;
        // System.err.println("variable " + i + " not finished " + bound +
        // " -- "
        // + hw);
      } else {
        // store how many replications it took for variable i
        this.replications[i] = this.lastListSize;
        SimSystem.report(Level.INFO, "variable " + i + " finished after >="
            + lls + " and <= " + newListSize + " with mean " + means[i]
            + " and variance " + variances[i]);

        // remove it from the list
        it.remove();
      }
    }

    return replRequired ? getFurtherReplications() : 0;
  }

  private void setUpQuantile(int numVariables) {
    double alpha = 1. - this.confidenceLevel;
    double individualAlpha = alpha / numVariables;
    this.iz =
        InverseNormalDistributionFunction.quantil(1. - individualAlpha / 2.,
            true);
  }

  public double getRelativeHalfWidthThreshold() {
    return relativeHalfWidthThreshold;
  }

  public void setRelativeHalfWidthThreshold(double relativeHalfWidthThreshold) {
    this.relativeHalfWidthThreshold = relativeHalfWidthThreshold;
  }

  public double getConfidenceLevel() {
    return confidenceLevel;
  }

  public void setConfidenceLevel(double confidenceLevel) {
    this.confidenceLevel = confidenceLevel;
  }

  public int getMaxReplications() {
    return maxReplications;
  }

  public void setMaxReplications(int maxReplications) {
    this.maxReplications = maxReplications;
  }

  @Override
  public String toString() {
    return "Confidence Interval Criterion (" + this.getClass().getName()
        + ") with at most " + getMaxReplications()
        + " replications. Relative half width threshold "
        + relativeHalfWidthThreshold + ". Confidence level: "
        + getConfidenceLevel();
  }

  public int getInitialReplications() {
    return minReplications;
  }

  /**
   * Get the value of the furtherReplications.
   * 
   * @return the furtherReplications
   */
  public int getFurtherReplications() {
    return furtherReplications;
  }

  /**
   * Set the furtherReplications to the value passed via the furtherReplications
   * attribute.
   * 
   * @param furtherReplications
   *          the furtherReplications to set
   */
  public void setFurtherReplications(int furtherReplications) {
    this.furtherReplications = furtherReplications;
  }
}
