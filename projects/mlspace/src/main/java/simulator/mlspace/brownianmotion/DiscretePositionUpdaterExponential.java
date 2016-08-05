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
import org.jamesii.core.math.random.generators.IRandom;

/**
 * Position update event generator treating spatial entities like particles in
 * reaction-diffusion simulation (NSM), calculating the time to a diffusion
 * event from an exponential distribution, then determining a random direction.
 * 
 * IGNORES DRIFT
 *
 * @author Arne Bittig
 */
public class DiscretePositionUpdaterExponential implements
    IDiscretePositionUpdater {

  private static final long serialVersionUID = 6305097330420258895L;

  private final IRandom rand;

  private final IVectorFactory vecFac;

  private IDisplacementVector steps;

  /** 2/x², 2/y² (2/z² if 3d) */
  private double[] stepSquareReciprocals;

  /** for diffusion time determination; also cached for roulette wheel sampling */
  private double stepSqRecSum;

  /**
   * vectors that are 0 in all but 1 dimension, where their value is the
   * negative of that of {@link #steps}
   */
  private IDisplacementVector[] minusSteps;

  /**
   * vectors that are 0 in all but 1 dimension, where their value equals to that
   * of {@link #steps}
   */
  private IDisplacementVector[] plusSteps;

  /**
   * @param steps
   *          size of update steps in each direction
   * @param vecFac
   *          Vector factory
   * @param rand
   *          Random number generator
   */
  public DiscretePositionUpdaterExponential(IDisplacementVector steps,
      IVectorFactory vecFac, IRandom rand) {
    this.vecFac = vecFac;
    this.rand = rand;
    this.steps = steps;
    overrideStepSize(steps);
  }

  @Override
  public IDisplacementVector getPositionUpdate(double deltaT,
      IMoveableEntity comp) {
    int dim =
        rouletteWheelSampleDirection(stepSquareReciprocals, stepSqRecSum, rand);
    if (rand.nextBoolean()) {
      return minusSteps[dim];
    } else {
      return plusSteps[dim];
    }
  }

  /**
   * @param weights
   * @param weightSum
   * @param rand
   * @return index
   */
  private static int rouletteWheelSampleDirection(double[] weights,
      double weightSum, IRandom rand) {
    double sumSoFar = 0.;
    double threshold = weightSum * rand.nextDouble();
    int idx = 0;
    do {
      sumSoFar += weights[idx++];
    } while (sumSoFar < threshold);
    return idx - 1;
  }

  @Override
  public double getReasonableTimeToNextUpdate(IMoveableEntity comp) {
    return -Math.log(rand.nextDouble())
        / (comp.getDiffusionConstant() * stepSqRecSum);
  }

  @Override
  public IDisplacementVector adjustUpdateVector(IDisplacementVector contV) {
    return DiscretePositionUpdaterMasked.adjustUpdateVector(contV, steps);
  }

  @Override
  public IDisplacementVector getStepSize() {
    return steps;
  }

  @Override
  public final boolean overrideStepSize(IDisplacementVector newSteps) {
    this.steps = newSteps;
    int dimensions = newSteps.getDimensions();
    stepSquareReciprocals = new double[dimensions];
    stepSqRecSum = 0;
    minusSteps = new IDisplacementVector[dimensions];
    plusSteps = new IDisplacementVector[dimensions];
    for (int i = 0; i < dimensions; i++) {
      double stepInD = newSteps.get(i + 1);
      double stepSqRec = 2. / (stepInD * stepInD);
      stepSquareReciprocals[i] = stepSqRec;
      stepSqRecSum += stepSqRec;
      double[] plusDisp = new double[dimensions];
      plusDisp[i] = stepInD;
      plusSteps[i] = vecFac.newDisplacementVector(plusDisp);
      double[] minusDisp = new double[dimensions];
      minusDisp[i] = -stepInD;
      minusSteps[i] = vecFac.newDisplacementVector(minusDisp);
    }
    return true;
  }

}
