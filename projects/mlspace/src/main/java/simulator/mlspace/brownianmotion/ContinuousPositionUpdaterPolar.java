/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Position update with random step length like the "classic" GrInd way, but
 * determined differently: via polar / (hyper-) spherical coordinates, i.e.
 * using one normally distributed random variable from N(0, n * D * dt) (with
 * inputs D - diffusion constant - and dt - time since last update; n
 * dimensions) and n-1 uniformly distributed angles; these are then converted to
 * cartesian coordinates
 *
 * @author Arne Bittig
 */
public class ContinuousPositionUpdaterPolar
    extends AbstractContinuousPositionUpdater {

  private static final long serialVersionUID = -1826528135830055909L;

  private final int dim;

  /**
   * Position update method using given time steps, producing update vectors
   * that may contain any real value
   * 
   * @param rand
   *          Random number generator
   * @param averageDistance
   *          Average distance ideally traveled in each update step (for
   *          {@link #getReasonableTimeToNextUpdate(model.mlspace.entities.spatial.IMoveableEntity)}
   *          )
   * @param vecFac
   *          Vector factory (for creation of update vectors of the desired
   *          class)
   */
  public ContinuousPositionUpdaterPolar(IRandom rand, double averageDistance,
      IVectorFactory vecFac) {
    super(vecFac, rand, averageDistance);
    this.dim = vecFac.getDimension();
  }

  /**
   * Position update method using given time steps, producing update vectors
   * that may contain any real value
   * 
   * @param normDist
   *          Normal distribution for random radial (set to mean 0, stdev 1)
   * @param averageDistance
   *          Average distance ideally traveled in each update step (for
   *          {@link #getReasonableTimeToNextUpdate(model.mlspace.entities.spatial.IMoveableEntity)}
   *          )
   * @param vecFac
   *          Vector factory (for creation of update vectors of the desired
   *          class)
   */
  public ContinuousPositionUpdaterPolar(AbstractNormalDistribution normDist,
      double averageDistance, IVectorFactory vecFac) {
    super(vecFac, normDist, averageDistance);
    this.dim = vecFac.getDimension();
  }

  @Override
  protected IDisplacementVector randomVector(double twiceDTimesDeltaT,
      IRandom rand, AbstractNormalDistribution normDist) {
    normDist.setDeviation(Math.sqrt(dim * twiceDTimesDeltaT));
    double r = normDist.getRandomNumber();
    switch (dim) {
    case 1:
      return getVectorFactory().newDisplacementVector(r);
    case 2:
      return getVectorFactory().newDisplacementVector(
          Vectors.polarToCartesian(r, rand.nextDouble() * Math.PI * 2.));
    case 3:
      double theta = rand.nextDouble() * Math.PI * 2.;
      double twoVminus1 = 2. * rand.nextDouble() - 1.;
      double phi = Math.acos(twoVminus1);
      double rSinTheta = r * Math.sin(theta);
      return getVectorFactory().newDisplacementVector(rSinTheta * twoVminus1,
          rSinTheta * Math.sin(phi), r * Math.cos(theta));
    default:
      throw new UnsupportedOperationException(
          "At most three dimensions supported");
    }
  }

}
