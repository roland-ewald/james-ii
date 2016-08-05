/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Context;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for position updaters producing update steps of fixed step sizes (or
 * integer multiples thereof)
 *
 * @author Arne Bittig
 */
public class DiscretePositionUpdaterFactory extends PositionUpdaterFactory {

  private static final long serialVersionUID = 5541997554500951636L;

  /** The parameter block identifier for the step size setting */
  public static final String STEPS = "Steps";

  private static final String POS_UPD_TO_MASK = "MaskedPosUpdater";

  /**
   * Param block identifier whether to create exponential dist based pos upd
   * (only if no {@link #POS_UPD_TO_MASK} present)
   */
  private static final String NO_DRIFT = "NoDrift";

  @Override
  public IPositionUpdater create(ParameterBlock params, Context context) {
    // create DiscretePositionUpdaterMasked
    IDisplacementVector steps = getStepParam(params);
    if (params.hasSubBlock(NO_DRIFT) && !params.hasSubBlock(POS_UPD_TO_MASK)) {
      return new DiscretePositionUpdaterExponential(steps, getVecFac(params),
          getRand(params));
    }

    IContinuousPositionUpdater posUpdTimeSteps =
        getOrCreateContPosUpdater(params, steps);
    return new DiscretePositionUpdaterMasked(steps, posUpdTimeSteps);
  }

  private static IDisplacementVector getStepParam(ParameterBlock params) {
    Object stepsSetting = params.getSubBlockValue(STEPS);
    IDisplacementVector steps;
    if (stepsSetting instanceof IDisplacementVector) {
      steps = (IDisplacementVector) stepsSetting;
    } else if (stepsSetting instanceof double[]) {
      IVectorFactory vecFac = getVecFac(params);
      if (vecFac == null) {
        throw new IllegalArgumentException("Steps given as array, but"
            + " no vector factory to create displacement vector.");
      }
      steps = vecFac.newDisplacementVector((double[]) stepsSetting);
    } else {
      throw new IllegalArgumentException("No distance steps given.");
    }
    return steps;
  }

  private static IContinuousPositionUpdater getOrCreateContPosUpdater(
      ParameterBlock params, IDisplacementVector steps) {
    IContinuousPositionUpdater posUpdTimeSteps =
        params.getSubBlockValue(POS_UPD_TO_MASK);

    if (posUpdTimeSteps == null) {
      // try to create a default one
      if (!params.hasSubBlock(ContinuousPositionUpdaterFactory.TRAVEL_DISTANCE)) {
        params.addSubBlock(ContinuousPositionUpdaterFactory.TRAVEL_DISTANCE,
            Vectors.vecNormMin(steps.toArray()) / 2.);
      }
      posUpdTimeSteps =
          new ContinuousPositionUpdaterFactory().create(params, SimSystem
              .getRegistry().createContext()); // CHECK:
      // try...catch?
    }
    return posUpdTimeSteps;
  }
}
