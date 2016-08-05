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

import model.mlspace.entities.spatial.IMoveableEntity;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Continuous-space position updater wrapping another, adding scatter to the
 * returned {@link #getReasonableTimeToNextUpdate(IMoveableEntity) time to next
 * update}.
 *
 * @author Arne Bittig
 * @date May 14, 2012
 */
public final class WrappedContPosUpdWithTimeScatter implements
    IContinuousPositionUpdater {

  private static final long serialVersionUID = 5133948075347837584L;

  private final IContinuousPositionUpdater wcpu;

  private final IDistribution scatterDist;

  /** lower bound for default (uniform) scatter distribution */
  private static final double UPD_TIME_SCATTER_LOWER = 0.9;

  /** upper bound of default (uniform) scatter distribution */
  private static final double UPD_TIME_SCATTER_UPPER = 1.1;

  /**
   * Wrapped continuous-space position updater, returning the same values as the
   * wrapped one except for the
   * {@link #getReasonableTimeToNextUpdate(IMoveableEntity) reasonable times to
   * next update} scattered with a random number from U(
   * {@value #UPD_TIME_SCATTER_LOWER},{@value #UPD_TIME_SCATTER_UPPER}).
   * 
   * @param contPosUpdToWrap
   *          Continuous-space position updater to wrap
   * @param rand
   *          Random number generator
   */
  public WrappedContPosUpdWithTimeScatter(
      IContinuousPositionUpdater contPosUpdToWrap, IRandom rand) {
    this(contPosUpdToWrap, new UniformDistribution(rand,
        UPD_TIME_SCATTER_LOWER, UPD_TIME_SCATTER_UPPER));
  }

  /**
   * Wrapped continuous-space position updater, returning the same values as the
   * wrapped one except for the
   * {@link #getReasonableTimeToNextUpdate(IMoveableEntity) reasonable times to
   * next update} scattered with a random number from the given distribution,
   * which should have mean 1!
   * 
   * @param contPosUpdToWrap
   *          Continuous-space position updater to wrap
   * @param scatterDist
   *          Distribution of scatter - mean 1 preferred (not enforced)
   */
  public WrappedContPosUpdWithTimeScatter(
      IContinuousPositionUpdater contPosUpdToWrap, IDistribution scatterDist) {
    this.wcpu = contPosUpdToWrap;
    this.scatterDist = scatterDist;
  }

  @Override
  public IDisplacementVector getPositionUpdate(double deltaT,
      IMoveableEntity comp) {
    return wcpu.getPositionUpdate(deltaT, comp);
  }

  @Override
  public double getReasonableTimeToNextUpdate(IMoveableEntity comp) {
    return wcpu.getReasonableTimeToNextUpdate(comp)
        * scatterDist.getRandomNumber();
  }

  @Override
  public IDisplacementVector adjustUpdateVector(IDisplacementVector contV) {
    return wcpu.adjustUpdateVector(contV);
  }

  @Override
  public double getTravelDistance() {
    return wcpu.getTravelDistance();
  }

}
