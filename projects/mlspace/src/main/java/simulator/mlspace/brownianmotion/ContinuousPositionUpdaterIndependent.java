/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Position update determination for each dimension independently, i.e. the
 * "classic" GrInd way: one random variable from N(0, 2* D * dt) for each
 * direction (with inputs D - diffusion constant - and dt - time since last
 * update)
 * 
 * @author Arne Bittig
 */
public class ContinuousPositionUpdaterIndependent
    extends AbstractContinuousPositionUpdater {

  private static final long serialVersionUID = 8176193071962303570L;

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
   *          Vector factory for vector creation
   */
  public ContinuousPositionUpdaterIndependent(IRandom rand,
      double averageDistance, IVectorFactory vecFac) {
    super(vecFac, rand, averageDistance);
    this.dim = vecFac.getDimension();
  }

  /**
   * Position update method using given time steps, producing update vectors
   * that may contain any real value
   * 
   * @param normDist
   *          Normal distribution to use (mean 0, stdev 1)
   * @param averageDistance
   *          Average distance ideally traveled in each update step (for
   *          {@link #getReasonableTimeToNextUpdate(model.mlspace.entities.spatial.IMoveableEntity)}
   *          )
   * @param vecFac
   *          Vector factory for vector creation
   */
  public ContinuousPositionUpdaterIndependent(
      AbstractNormalDistribution normDist, double averageDistance,
      IVectorFactory vecFac) {
    super(vecFac, normDist, averageDistance);
    this.dim = vecFac.getDimension();
  }

  @Override
  protected IDisplacementVector randomVector(double twiceDTimesDeltaT,
      IRandom rand, AbstractNormalDistribution normDist) {
    normDist.setDeviation(Math.sqrt(twiceDTimesDeltaT));
    double[] coords = new double[dim];
    for (int i = dim - 1; i >= 0; i--) {
      coords[i] = normDist.getRandomNumber();
    }
    return getVectorFactory().newDisplacementVector(coords);
  }

}
