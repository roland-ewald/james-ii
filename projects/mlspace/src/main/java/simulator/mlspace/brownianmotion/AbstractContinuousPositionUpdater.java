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
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;

import model.mlspace.entities.spatial.IMoveableEntity;

/**
 * Base class for position update methods. Does nothing more than hold a
 * reference to the vector factory that shall be used for creating the
 * displacement vectors.
 *
 * @author Arne Bittig
 */
public abstract class AbstractContinuousPositionUpdater
    implements IContinuousPositionUpdater {

  /** Serialization ID */
  private static final long serialVersionUID = -4903283205124407617L;

  /** Vector factory to use to produce new vectors */
  private final IVectorFactory vecFac;

  /** Normal distribution used for Brownian position updates */
  private final AbstractNormalDistribution normDist;

  /** (uniform) random number generator */
  private final IRandom rand;

  /**
   * target travel distance (square of value is used in
   * {@link #getReasonableTimeToNextUpdate(IMoveableEntity)}, this value is kept
   * for {@link #getTravelDistance()})
   */
  private final double targetTravelDistance;

  /** square of {@link #targetTravelDistance} */
  private final double ttdSq;

  /**
   * 
   * @param vecFac
   *          Vector factory for vector creation
   * @param normDist
   *          Normal distribution to draw Brownian position updates from
   * @param rand
   *          Random number generator (uniform)
   * @param targetTravelDistance
   *          average distance traveled if next updates comes according to
   *          {@link #getReasonableTimeToNextUpdate(IMoveableEntity)}
   */
  private AbstractContinuousPositionUpdater(IVectorFactory vecFac,
      AbstractNormalDistribution normDist, IRandom rand,
      double targetTravelDistance) {
    this.vecFac = vecFac;
    this.normDist = normDist;
    this.rand = rand;
    this.targetTravelDistance = targetTravelDistance;
    this.ttdSq = targetTravelDistance * targetTravelDistance;
  }

  /**
   * 
   * @param vecFac
   *          Vector factory for vector creation
   * @param rand
   *          Random number generator (uniform)
   * @param targetTravelDistance
   *          average distance traveled if next updates comes according to
   *          {@link #getReasonableTimeToNextUpdate(IMoveableEntity)}
   */
  protected AbstractContinuousPositionUpdater(IVectorFactory vecFac,
      IRandom rand, double targetTravelDistance) {
    this(vecFac, new NormalDistribution(rand), rand, targetTravelDistance);
  }

  /**
   * 
   * @param vecFac
   *          Vector factory for vector creation
   * @param normDist
   *          Normal distribution to draw Brownian position updates from
   * @param targetTravelDistance
   *          average distance traveled if next updates comes according to
   *          {@link #getReasonableTimeToNextUpdate(IMoveableEntity)}
   */
  protected AbstractContinuousPositionUpdater(IVectorFactory vecFac,
      AbstractNormalDistribution normDist, double targetTravelDistance) {
    this(vecFac, normDist, normDist.getRandom(), targetTravelDistance);
  }

  @Override
  public final double getTravelDistance() {
    return targetTravelDistance;
  }

  /**
   * The vector factory used in the position updater
   * 
   * @return vector factory
   */
  public final IVectorFactory getVectorFactory() {
    return vecFac;
  }

  /**
   * Random vector to be determined (e.g. by drawing several times from a normal
   * distribution, or drawing one distance and one or more angles and
   * transforming between polar and cartesian coordinates).
   * 
   * @param twiceDTimesDeltaT
   *          2 * D * \delta t parameter (square of target length for 1d)
   * @param random
   *          Generator of uniformly distributed random numbers ((U(0,1))
   * @param normalDist
   *          Generator of normally distributed random numbers (N(mean,var),
   *          mean, variance to be set!)
   * 
   * @return Displacement vector with random coordinates for corresponding
   *         moving entity
   */
  protected abstract IDisplacementVector randomVector(double twiceDTimesDeltaT,
      IRandom random, AbstractNormalDistribution normalDist);

  @Override
  public IDisplacementVector getPositionUpdate(double deltaT,
      IMoveableEntity comp) {
    double variance = 2.0 * deltaT * comp.getDiffusionConstant();
    IDisplacementVector posUpd = randomVector(variance, rand, normDist);
    IDisplacementVector drift = comp.getDrift();
    if (drift != null && !drift.isNullVector()) {
      posUpd.add(drift.times(deltaT));
    }
    return posUpd;
  }

  @Override
  public double getReasonableTimeToNextUpdate(IMoveableEntity comp) {
    double timeForDiffusion = ttdSq / comp.getDiffusionConstant();

    IDisplacementVector drift = comp.getDrift();
    double driftLengthSqr = drift == null ? 0.0 : drift.lengthSquared();
    if (driftLengthSqr == 0.0) { // shortcut (value should lead to
      return timeForDiffusion; // infinite time for drift anyway)
    }
    double timeForDrift = Math.sqrt(ttdSq / driftLengthSqr);
    return Math.min(timeForDrift, timeForDiffusion);
  }

  @Override
  public IDisplacementVector adjustUpdateVector(IDisplacementVector contV) {
    return contV;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[targetTravelDistance="
        + targetTravelDistance + ", vecFac=" + vecFac + "]";
  }

}
