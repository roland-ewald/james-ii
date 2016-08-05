package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.statistics.univariate.ErrorFunction;

/**
 * <a href="http://en.wikipedia.org/wiki/L%C3%A9vy_distribution"> L&eacute;vy
 * distribution</a> random variables derived by inverse transformation sampling
 * (using {@link ErrorFunction#inverfc(double,double)}).
 *
 * @author Arne Bittig
 * @date 15.10.2014
 */
public class LevyDistribution extends AbstractDistribution {

  private static final long serialVersionUID = 1369424560547717720L;

  private static final double DEFAULT_PRECISION = 1e-5;

  private double cBy2 = 0.5;

  private double mu = 1.;

  private final double delta;

  /**
   * Levy Distribution with scale c=1 and location mu=0
   *
   * @param randomizer
   *          Random number generator
   */
  public LevyDistribution(IRandom randomizer) {
    this(randomizer, 1., 0., DEFAULT_PRECISION);
  }

  /**
   * @param randomizer
   *          Random number generator
   * @param c
   *          Scale parameter
   * @param mu
   *          Location Parameter
   * @param delta
   *          Precision for {@link ErrorFunction#inverfc(double, double)}
   */
  public LevyDistribution(IRandom randomizer, double c, double mu, double delta) {
    super(randomizer);
    setC(c);
    setMu(mu);
    this.delta = delta;
  }

  @Override
  public double getRandomNumber() {
    double erfcInvVal = ErrorFunction.inverfc(getRandom().nextDouble(), delta);
    return cBy2 / (erfcInvVal * erfcInvVal) + mu;
  }

  /**
   * @return scale parameter c
   */
  public final double getC() {
    return cBy2 * 2;
  }

  /**
   * @param c
   *          scale parameter c
   */
  public final void setC(double c) {
    this.cBy2 = c / 2;
  }

  /**
   * @return location parameter mu
   */
  public final double getMu() {
    return mu;
  }

  /**
   * @param mu
   *          location parameter mu
   */
  public final void setMu(double mu) {
    this.mu = mu;
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    return new LevyDistribution(newRandomizer, cBy2 * 2, mu, delta);
  }

}
