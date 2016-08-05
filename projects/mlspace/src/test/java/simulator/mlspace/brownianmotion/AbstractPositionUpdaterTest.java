package simulator.mlspace.brownianmotion;

import model.mlspace.entities.spatial.IMoveableEntity;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.distributions.NormalDistributionBoxMuller;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.mersennetwister.MersenneTwister;
import org.jamesii.core.math.statistics.univariate.MinMedianMeanMax;
import org.jamesii.core.util.misc.Pair;

/**
 * @author Arne Bittig
 * @date 14.10.2014
 */
public abstract class AbstractPositionUpdaterTest extends ChattyTestCase {

  private static final IRandom RAND = new MersenneTwister();

  private static final AbstractNormalDistribution[] NORM_DISTS =
      new AbstractNormalDistribution[] { new NormalDistribution(RAND),
          new NormalDistributionBoxMuller(RAND),
      // new NormalDistributionJava(new Random(), 0., 1.),
      // new NormalDistributionInverseTransformation(RAND),
      };

  private static final double MAXTIME = 100.;

  private static final int REPETITIONS = 1000;

  /**
   * @param vecFac
   *          Vector factory
   * @param avgDist
   * @param rand
   *          Pseudo-random number generator
   * @param normDist
   *          TODO
   * @return {@link IPositionUpdater} instance to be tested
   */
  protected abstract IPositionUpdater getPositionUpdater(IVectorFactory vecFac,
      double avgDist, IRandom rand, AbstractNormalDistribution normDist);

  /**
   * column headlines for the sysout of the following methods, not an actual
   * test
   */
  public void testColumnInfo() {
    StringBuilder headlines =
        new StringBuilder(
            "Position updated tested,dimensions,normal distribution,");
    for (String r : new String[] { "rand", "det" }) {
      for (double avgDist : new double[] { 1., 2. }) {
        for (String what : new String[] { "displacement", "steps" }) {
          headlines.append(what + avgDist + r
              + " (min/median/mean/max/std/amount),");
        }
      }
    }
    headlines.append("time (ms)");
    System.out.println(headlines.toString());
  }

  public void testDisplacement2d() {
    testDisplacement(2);
  }

  public void testDisplacement3d() {
    testDisplacement(3);
  }

  private void testDisplacement(int dim) {
    for (AbstractNormalDistribution normDist : NORM_DISTS) {
      System.out.print(this.getClass().getSimpleName() + "," + dim + ","
          + normDist.getClass().getSimpleName());
      double[] disps = new double[REPETITIONS];
      int[] steps = new int[REPETITIONS];
      long startTime = System.currentTimeMillis();
      for (boolean r : new boolean[] { true, false }) {
        for (double avgDist : new double[] { 1., 2. }) {
          for (int i = 0; i < REPETITIONS; i++) {
            Pair<Integer, Double> pair =
                getSquaredDisplacement(dim, MAXTIME, avgDist, r, normDist);
            disps[i] = pair.getSecondValue();
            steps[i] = pair.getFirstValue();
          }
          System.out.print(
          // "Disp " + avgDist + Boolean.toString(r).charAt(0) + ": "
              ",\"" + MinMedianMeanMax.minMedianMeanMaxStdAmountMutating(disps)
                  + "\",\""
                  + MinMedianMeanMax.minMedianMeanMaxStdAmountMutating(steps)
                  + "\"");
        }
      }
      long elapsedTime = System.currentTimeMillis() - startTime;
      System.out.println("," + elapsedTime);
    }
  }

  private Pair<Integer, Double> getSquaredDisplacement(int dim, double maxtime,
      double avgDist, boolean randomTimeSteps,
      AbstractNormalDistribution normDist) {
    addParameter("RNG", RAND);
    addParameter("dim", dim);
    addParameter("maxtime", maxtime);
    addParameter("timeSteps", randomTimeSteps ? avgDist == 1 ? "U(0,1)+U(0,1)"
        : "(U(0,1)+U(0,1))*" + avgDist : Double.toString(avgDist));
    IVectorFactory vecFac = new AVectorFactory(dim);
    IPositionUpdater posUpdater =
        getPositionUpdater(vecFac, avgDist, RAND, normDist);

    IMoveableEntity ent = newDummyEntity(vecFac, avgDist * avgDist);
    double timeSoFar = 0.;
    int numSteps = 0;
    while (timeSoFar < maxtime) {
      double steptime =
          posUpdater.getReasonableTimeToNextUpdate(ent)
              * (randomTimeSteps ? RAND.nextDouble() + RAND.nextDouble() : 1.);
      IDisplacementVector disp = posUpdater.getPositionUpdate(steptime, ent);
      // if (disp.lengthSquared() > avgDist * avgDist * dim) {
      // System.out.println("now");
      // }
      ent.move(disp);
      timeSoFar += steptime;
      numSteps++;
    }
    return new Pair<>(numSteps, ent.getPosition().distanceSquared(
        vecFac.origin()));
  }

  /**
   * @param vecFac
   * @param rand
   * @return
   */
  private static IMoveableEntity newDummyEntity(final IVectorFactory vecFac,
      final double diff) {
    return new IMoveableEntity() {

      IPositionVector pos = vecFac.origin();

      private final IDisplacementVector nullVec = vecFac.nullVector();

      @Override
      public void move(IDisplacementVector disp) {
        pos.add(disp);
      }

      @Override
      public IPositionVector getPosition() {
        return pos;
      }

      @Override
      public IDisplacementVector getDrift() {
        return nullVec;
      }

      @Override
      public double getDiffusionConstant() {
        return diff;
      }
    };
  }
}
