/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import model.mlspace.entities.spatial.IMoveableEntity;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.InverseGaussianDistribution;
import org.jamesii.core.math.random.generators.IRandom;

//TODO: theoretical background (generalisation to more than 1 dim.)
/**
 * Position update event generator based upon Brownian motion's first hitting
 * time of a given boundary (i.e. desired step size). Position update has to be
 * calculated for each dimension independently.
 *
 * For Brownian motion with parameter k and drift v, the intervals between
 * position updates of length a are distributed according to an inverse gaussian
 * distribution IG(a/v,a*a/k*k). For Brownian motion without drift, intervals
 * between position updates of either +a or -a (equal probablility) are
 * distributed according to a distribution with no explicit form and mean a*a,
 * which is approximated by an inverse gaussian distribution here as well.
 *
 * @author Arne Bittig
 */
@Deprecated
// unfinished, not yet working - dealing with different dimensions has to be
// worked out
public class DiscretePositionUpdaterDirect implements IDiscretePositionUpdater {

  private static final long serialVersionUID = 6305097330420258895L;

  // private InverseGaussianDistribution igDist;

  private final IRandom rand;

  private final double stepSize;

  private final IVectorFactory vecFac;

  /**
   * @param vecFac
   *          Vector factory
   * @param igDist
   *          Inverse Gaussian distribution
   * @param stepSize
   *          size of movement steps (only direction will be determined)
   */
  public DiscretePositionUpdaterDirect(IVectorFactory vecFac,
      InverseGaussianDistribution igDist, double stepSize) {
    this.vecFac = vecFac;
    this.rand = igDist.getRandom();
    // this.igDist = igDist;
    this.stepSize = stepSize;
  }

  /**
   * @param vecFac
   *          Vector factory
   * @param rand
   *          Random number generator
   * @param stepSize
   *          size of movement steps (only direction will be determined)
   */
  public DiscretePositionUpdaterDirect(IVectorFactory vecFac, IRandom rand,
      double stepSize) {
    this(vecFac, new InverseGaussianDistribution(rand), stepSize);
  }

  @Override
  public IDisplacementVector getPositionUpdate(double deltaT,
      IMoveableEntity comp) {
    IDisplacementVector drift = comp.getDrift();
    int dim = getRandDim(drift);
    boolean negDir =
        drift.get(dim) < 0 || drift.get(dim) == 0 && rand.nextBoolean();
    if (negDir) {
      return vecFac.unit(dim).times(-stepSize);
    }
    return vecFac.unit(dim).times(stepSize);
  }

  @Override
  public double getReasonableTimeToNextUpdate(IMoveableEntity comp) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public IDisplacementVector adjustUpdateVector(IDisplacementVector contV) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDisplacementVector getStepSize() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean overrideStepSize(IDisplacementVector steps) {
    // TODO Auto-generated method stub
    // return false;
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private int lastUsedDim = 0;

  private int getRandDim(IDisplacementVector drift) {
    if (drift.isNullVector()) {
      lastUsedDim = (int) (vecFac.getDimension() * rand.nextDouble() + 1);
    } else {
      IDisplacementVector driftAbs = drift.copy();
      driftAbs.absolute();
      lastUsedDim =
          org.jamesii.core.math.random.RandomSampler.sampleRouletteWheel(
              driftAbs.toArray(), rand);
    }
    return lastUsedDim;
  }

}
