/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.generic;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Utility plugin to easily attach single observers to arbitrary experiments,
 * without having to implement a custom {@link IComputationInstrumenter}.
 * <p/>
 * <b>Important</b>: The expected observer type is an {@link IObserver} for
 * {@link org.jamesii.core.base.IEntity} (i.e. this should be its type
 * parameter).
 * 
 * @see IObserver
 * @see GenericComputationInstrumenter
 * 
 * @author Roland Ewald
 */
public class GenericComputationInstrumenterFactory extends
    ComputationInstrumenterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1855972400185831664L;

  /**
   * Contains the observer to be attached to the computation task / simulator.
   * Expected type: {@link IObserver}.
   */
  public static final String OBSERVER_TO_USE = "observerToUse";

  /**
   * Contains the target at which the observer shall be registered. Expected
   * type: {@link GenericInstrumentationTarget}.
   */
  public static final String OBSERVATION_TARGET = "observationTarget";

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    return ParameterBlocks.hasSubBlock(parameters, OBSERVER_TO_USE) ? 1 : 0;
  }

  @Override
  public IComputationInstrumenter create(ParameterBlock parameter, Context context) {
    if (!ParameterBlocks.hasSubBlock(parameter, OBSERVER_TO_USE)
        || !(ParameterBlocks.getSubBlockValue(parameter, OBSERVER_TO_USE) instanceof IObserver<?>)) {
      throw new IllegalArgumentException("Observer '"
          + ParameterBlocks.getSubBlockValue(parameter, OBSERVER_TO_USE)
          + "' is not a supported.");
    }
    return new GenericComputationInstrumenter(
        (IObserver<?>) ParameterBlocks.getSubBlockValue(parameter,
            OBSERVER_TO_USE), ParameterBlocks.getSubBlockValueOrDefault(
            parameter, OBSERVATION_TARGET,
            GenericInstrumentationTarget.PROCESSOR_STATE));
  }
}
