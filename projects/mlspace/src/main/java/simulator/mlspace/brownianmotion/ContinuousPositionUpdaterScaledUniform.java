/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 *
 */
package simulator.mlspace.brownianmotion;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Position update with random step length determined using a random vector with
 * uniform coordinates, scaled to an appropriate length
 * 
 * @author Arne Bittig
 * @date May 14, 2012
 */
public class ContinuousPositionUpdaterScaledUniform extends
    AbstractContinuousPositionUpdater {

  private static final long serialVersionUID = -2886656943191639126L;

  private final int dim;

  /**
   * @param rand
   * @param targetTravelDistance
   * @param vecFac
   */
  public ContinuousPositionUpdaterScaledUniform(IRandom rand,
      double targetTravelDistance, IVectorFactory vecFac) {
    super(vecFac, rand, targetTravelDistance);
    this.dim = vecFac.getDimension();
  }

  /**
   * @param normDist
   * @param targetTravelDistance
   * @param vecFac
   */
  public ContinuousPositionUpdaterScaledUniform(
      AbstractNormalDistribution normDist, double targetTravelDistance,
      IVectorFactory vecFac) {
    super(vecFac, normDist, targetTravelDistance);
    this.dim = vecFac.getDimension();
  }

  @Override
  protected IDisplacementVector randomVector(double twiceDTimesDeltaT,
      IRandom random, AbstractNormalDistribution normalDist) {
    double[] c = new double[dim];
    double lengthSquared = 0.;
    for (int i = 0; i < dim; i++) {
      double value = 2 * random.nextDouble() - 1;
      c[i] = value;
      value *= value;
      lengthSquared += value;
    }
    double sc = Math.sqrt(twiceDTimesDeltaT / lengthSquared * dim);
    IDisplacementVector rv = getVectorFactory().newDisplacementVector(c);
    normalDist.setDeviation(sc);
    rv.scale(normalDist.getRandomNumber());
    return rv;
  }

}
