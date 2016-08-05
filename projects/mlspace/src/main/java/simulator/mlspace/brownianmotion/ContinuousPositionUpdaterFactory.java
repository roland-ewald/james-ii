/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import java.util.Collection;
import java.util.Collections;

import org.jamesii.core.factories.Context;
import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.math.random.distributions.AbstractNormalDistribution;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for {@link ContinuousPositionUpdaterIndependent},
 * {@link ContinuousPositionUpdaterScaledUniform} and
 * {@link ContinuousPositionUpdaterPolar}, possibly using
 * {@link WrappedContPosUpdWithTimeScatter}.
 *
 * @author Arne Bittig
 *
 */
public class ContinuousPositionUpdaterFactory extends PositionUpdaterFactory {

  /** Serialization ID */
  private static final long serialVersionUID = 448688871949723391L;

  /**
   * Enumeration of approaches for determining random vector/coordinates of d
   * dimensions
   * 
   * @author Arne Bittig
   * @date 06.03.2013
   */
  public static enum Mode {
    /**
     * 1 normally distributed length, d-1 angles from suitable uniform
     * distributions, polar/spherical-to-cartesian conversion
     */
    POLAR_SPHERICAL,
    /**
     * d normally distributed random numbers (as it was done in GrInd; probably
     * least efficient of the three approaches)
     */
    INDEPENDENT,
    /**
     * d uniform random numbers from [0,1), vector scaled to normally
     * distributed random length
     */
    SCALED_UNIFORM
  }

  /** Parameter block identifier for the setting "spherical coordinates?" */
  public static final String MODE = "RandomVectorMode";

  private static final Mode DEFAULT_MODE = Mode.POLAR_SPHERICAL;

  /**
   * Parameter block identifier for the normally distributed pseudo-random
   * number generator
   */
  public static final String NORM_DIST = "NormalDistribution";

  /**
   * Parameter block identifier for the step size/travel distance setting
   * (overrides other related settings)
   */
  public static final String TRAVEL_DISTANCE = "TravelDistance";

  /**
   * Parameter block identifier for the setting
   * "scaling factor of the step size/travel distance" (ignored if
   * {@link #TRAVEL_DISTANCE} is specified or if {@link #SPATIAL_ENTITIES} is
   * not specified)
   */
  public static final String TRAVEL_DISTANCE_SCALING = "TravelDistanceScaling";

  /**
   * Parameter block identifier for spatial entities from which default average
   * travel distance is taken
   */
  public static final String SPATIAL_ENTITIES = "SpatialEntities";

  /** Parameter block identifier for random scatter of update time steps */
  public static final String TIME_SCATTER_DIST = "TimeScatterDistribution";

  /**
   * Default travel distance is this factor times extension of smallest spatial
   * entity initially present in model. Factor 1/3 has been determined in some
   * experiments to be the on the upper end of the range with tolerable effects
   * on results.
   */
  public static final Double DEFAULT_TRAVEL_DISTANCE_SCALING = 1. / 3;

  @Override
  public IContinuousPositionUpdater create(ParameterBlock params,
      Context context) {
    IRandom rand = getRand(params);
    AbstractNormalDistribution normDistrib = params.getSubBlockValue(NORM_DIST);
    if (normDistrib == null && rand == null) {
      throw new IllegalArgumentException("Position updater requires"
          + " random number generator or normal distribution");
    }

    Double targetAvgDistance = getAvgDistanceParam(params);
    IVectorFactory vecFac = getVecFac(params);
    if (vecFac == null) {
      throw new IllegalArgumentException("Vector factory required"
          + " but not specified.");
    }

    IContinuousPositionUpdater positionUpdater;
    Mode mode = params.getSubBlockValue(MODE, DEFAULT_MODE);
    switch (mode) {
    case INDEPENDENT:
      positionUpdater =
          createClassicPositionUpdater(rand, normDistrib, targetAvgDistance,
              vecFac);
      break;
    case SCALED_UNIFORM:
      positionUpdater =
          createScalingPositionUpdater(rand, normDistrib, targetAvgDistance,
              vecFac);
      break;
    case POLAR_SPHERICAL:
      positionUpdater =
          createPolarPositionUpdater(rand, normDistrib, targetAvgDistance,
              vecFac);
      break;
    default:
      throw new IllegalStateException();
    }
    return checkForAndAddScatter(positionUpdater,
        params.getSubBlockValue(TIME_SCATTER_DIST), rand);
  }

  private static IContinuousPositionUpdater createClassicPositionUpdater(
      IRandom rand, AbstractNormalDistribution normDist, Double avgDist,
      IVectorFactory vecFac) {
    if (normDist != null) {
      return new ContinuousPositionUpdaterIndependent(normDist, avgDist, vecFac);
    } else {
      return new ContinuousPositionUpdaterIndependent(rand, avgDist, vecFac);
    }
  }

  private static IContinuousPositionUpdater createPolarPositionUpdater(
      IRandom rand, AbstractNormalDistribution normDist, Double avgDist,
      IVectorFactory vecFac) {
    if (normDist != null) {
      return new ContinuousPositionUpdaterPolar(normDist, avgDist, vecFac);
    } else {
      return new ContinuousPositionUpdaterPolar(rand, avgDist, vecFac);
    }
  }

  private static IContinuousPositionUpdater createScalingPositionUpdater(
      IRandom rand, AbstractNormalDistribution normDist, Double avgDist,
      IVectorFactory vecFac) {
    if (normDist != null) {
      return new ContinuousPositionUpdaterScaledUniform(normDist, avgDist,
          vecFac);
    } else {
      return new ContinuousPositionUpdaterScaledUniform(rand, avgDist, vecFac);
    }
  }

  private static IContinuousPositionUpdater checkForAndAddScatter(
      IContinuousPositionUpdater positionUpdater, Object scatter, IRandom rand) {
    if (scatter == null || scatter.equals(false)) {
      return positionUpdater;
    }
    if (scatter instanceof IDistribution) {
      return new WrappedContPosUpdWithTimeScatter(positionUpdater,
          (IDistribution) scatter);
    } else { // use default distribution hard coded in created class
      return new WrappedContPosUpdWithTimeScatter(positionUpdater, rand);
    }
  }

  // /** Default value for {@link #TRAVEL_DISTANCE} if no spatial entities found
  // */
  // public static final double DEF_AVG_TRAVEL_DIST = 1.;

  private static Double getAvgDistanceParam(ParameterBlock params) {
    Double avgDist = params.getSubBlockValue(TRAVEL_DISTANCE);
    if (avgDist == null) {
      Collection<? extends IShapedComponent> spatialEntities =
          params.getSubBlockValue(SPATIAL_ENTITIES);
      if (spatialEntities != null) {
        avgDist = 2. * getHalfExtOfSmallestSpatialComponent(spatialEntities);
      } else {
        // avgDist = DEF_AVG_TRAVEL_DIST;
        throw new IllegalStateException(
            "No spatial entities found for determining travel distance; should have been added to parameter block by caller.");
      }

      avgDist *=
          params.getSubBlockValue(TRAVEL_DISTANCE_SCALING,
              DEFAULT_TRAVEL_DISTANCE_SCALING);
    } else if (avgDist <= 0) {
      throw new IllegalArgumentException();
    }
    return avgDist;
  }

  private static double getHalfExtOfSmallestSpatialComponent(
      Collection<? extends IShapedComponent> spatialEntities) {
    IShapedComponent smallComp =
        Collections.min(spatialEntities, GeoUtils.SIZE_COMPARATOR_ASCENDING);
    return Vectors.vecNormMin(smallComp.getShape().getMaxExtVector().toArray());
  }

}
