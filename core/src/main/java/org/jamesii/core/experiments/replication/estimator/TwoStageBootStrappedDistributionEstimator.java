/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication.estimator;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.math.statistics.univariate.bootstrapping.confintervals.PercentileBootstrap;
import org.jamesii.core.math.statistics.univariate.bootstrapping.sampling.IBootStrapper;
import org.jamesii.core.util.misc.Pair;

/**
 * Estimates the additional amount of required replications based on the normal
 * distribution.
 * 
 * @author Stefan Leye
 */
public class TwoStageBootStrappedDistributionEstimator extends
    TwoStageEstimator {

  /**
   * The boot strapping algorithm.
   */
  private IBootStrapper bStrapper;

  /**
   * The count of boot strapping stages.
   */
  private int bStrappingStages;

  /**
   * Basic constructor.
   * 
   * @param allowedError
   *          the allowed error.
   * @param confidence
   *          the desired confidence
   * @param bStrapper
   *          the boot strapping algorithm.
   * @param bStrappingStages
   *          the count of boot strapping stages.
   */
  public TwoStageBootStrappedDistributionEstimator(double allowedError,
      boolean absolute, double confidence, IBootStrapper bStrapper,
      int bStrappingStages) {
    super(allowedError, absolute, confidence);
    this.bStrapper = bStrapper;
    this.bStrappingStages = bStrappingStages;
  }

  @Override
  protected double getQuantil(double p, List<? extends Number> sample) {
    List<Pair<Double, Double>> distribution =
        bStrapper.bootStrap(sample, bStrappingStages, SimSystem
            .getRNGGenerator().getNextRNG());
    Double mean = 0.0;
    Double variance = 0.0;
    for (int j = 0; j < distribution.size(); j++) {
      double value = distribution.get(j).getFirstValue();
      variance = Variance.varianceAddEntry(variance, mean, value, j + 1);
      mean = ArithmeticMean.arithmeticMean(mean, value, j + 1);
    }
    double std = Math.sqrt(variance);
    double quantil = PercentileBootstrap.quantil(p, distribution);
    double median = distribution.get(distribution.size() / 2).getFirstValue();
    quantil = (quantil - median) / std;
    return quantil;
  }
}
