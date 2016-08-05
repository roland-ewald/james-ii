/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for production of Brownian motion / position updater plugins
 *
 * @author Arne Bittig
 */
/**
 * @author Arne Bittig
 * @date May 10, 2012
 */
public abstract class PositionUpdaterFactory extends Factory<IPositionUpdater> {

  private static final long serialVersionUID = 2217894625144954808L;

  /** ID for the parameter block specifying the random number generator */
  public static final String RANDOM = "RNG"; // every PosUpdater needs one

  /** The parameter block identifier for the vector factory */
  public static final String VECTOR_FACTORY = "VectorFactory";

  /**
   * Create spatial index with the given parameters
 * @param params
   *          Parameter block
 * @return Spatial index
   */
  @Override
  public abstract IPositionUpdater create(ParameterBlock params, Context context);

  /**
   * @param params
   *          Parameter block from which to get random number generator
   * @return RNG (null if none specified)
   */
  protected static IRandom getRand(ParameterBlock params) {
    return params.<IRandom> getSubBlockValue(RANDOM);
  }

  /**
   * @param params
   *          Parameter block from which to try to get vector factory
   * @return Vector factory (null if none specified)
   */
  protected static IVectorFactory getVecFac(ParameterBlock params) {
    return params.<IVectorFactory> getSubBlockValue(VECTOR_FACTORY);
  }

}
