/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Camel distribution
 * 
 * Creates distributions looking like a camel (if humps are equal to 2) e.g.: --
 * --<br>
 * ----------<br>
 * 
 * 
 * This distribution has been introduced by Rönngren et. al and has been used
 * several times for evaluating the performance of event queues.
 * 
 * The parameters for this distribution are: - number of humps (humps - in the
 * example == 2) - width of humps (humpDomain - in the example == 0.4) - height
 * of humps (humpHitPercentage (in the example == 0.75))
 * 
 * @author Jan Himmelspach
 * 
 */
public class CamelDistribution extends AbstractDistribution {

  private static final long serialVersionUID = 5429429311714970996L;

  /**
   * domain range of the the numbers in the humps
   */
  private double humpDomain = .5;

  /**
   * percentage of generated values in the humps
   */
  private double humpHitPercentage = 0.2;

  /**
   * number of humps
   */
  private int humps = 2;

  /**
   * 
   * @param seed
   */
  public CamelDistribution(long seed) {
    super(seed);
  }

  /**
   * Create a camel distribution using the default random number generator and
   * the givenm parameters.
   * 
   * @param humps
   *          the humps
   * @param humpHitPercentage
   *          the hump hit percentage
   * @param humpDomain
   *          the hump domain
   * @param seed
   *          the seed
   */
  public CamelDistribution(long seed, int humps, double humpHitPercentage,
      double humpDomain) {
    super(seed);
    setParameters(humps, humpHitPercentage, humpDomain);
  }

  /**
   * The Constructor.
   * 
   * @param random
   *          the random
   */
  public CamelDistribution(IRandom random) {
    super(random);
  }

  /**
   * Create a camel distribution using the passed random generator and the given
   * parameters
   * 
   * @param random
   * @param humps
   * @param humpHitPercentage
   * @param humpDomain
   */
  public CamelDistribution(IRandom random, int humps, double humpHitPercentage,
      double humpDomain) {
    super(random);
    setParameters(humps, humpHitPercentage, humpDomain);
  }

  /**
   * Cameldistrib.
   * 
   * @param n
   *          number of humps
   * @param m
   *          area of the humps (m percent of the numbers to be in the humps)
   * @param u
   *          percent of the domain interval in the humps (domain range of the
   *          numbers in the humps)
   * @param random
   *          the random
   * 
   * @return the double
   */
  public double cameldistrib(int n, double m, double u, IRandom random) {

    /*
     * n = 2 | | --- | --- | --- a1 h1 a2 h2 a3
     */

    double domainIntervalPerHump = u / n;
    double domainIntervalRest = (1.0 - u) / (n + 1);

    double result = 0.0;

    // if we are in a hump
    if (random.nextDouble() < m) {
      // lets compute the position in the hump
      double pos = random.nextDouble() * domainIntervalPerHump; // [0..1] * u /
      // n
      // System.out.println (pos);
      // lets compute the hump to be used
      int hump = random.nextInt(n); // [0..n[
      // System.out.println (hump);
      // compute the return value, hump + 1 = number of areas besides the hump
      // up to the selected hump,
      result =
          (hump + 1) * domainIntervalRest + hump * domainIntervalPerHump + pos;
    } else { // we are not in a hump
      // lets compute the position in one of the areas beside the hump
      double pos = random.nextDouble() * domainIntervalRest;
      // lets compute the site to be used
      int sidearea = random.nextInt(n + 1);
      // compute the return value
      result =
          (sidearea * domainIntervalPerHump) + sidearea * domainIntervalRest
              + pos;
    }

    // System.out.println("camel: "+result);

    return result;
  }

  @Override
  public double getRandomNumber() {
    // TODO Auto-generated method stub
    return cameldistrib(humps, humpHitPercentage, humpDomain, getRandom());
  }

  @Override
  public AbstractDistribution getSimilar(IRandom newRandomizer) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Initialize the parameters of this camel distribution. It may be a bad idea
   * to call this method during event number generations (repeatability may get
   * lost, results may be weird).
   * 
   * @param humps
   * @param humpHitPercentage
   * @param humpDomain
   */
  public final void setParameters(int humps, double humpHitPercentage,
      double humpDomain) {
    this.humps = humps;
    this.humpHitPercentage = humpHitPercentage;
    this.humpDomain = humpDomain;
  }

  @Override
  public String toString() {
    return "Camel(" + humps + "," + humpHitPercentage + "," + humpDomain + ")";
  }

}
