/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.instrumentation;

import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;

import simulator.mlspace.MLSpaceProcessorFactory;

/**
 * Factory for the snapshot observer instrumenter.
 *
 * @author Roland Ewald
 * @author Arne Bittig
 */
public class MLSpaceInstrumenterFactory extends ComputationInstrumenterFactory {

  private static final long serialVersionUID = 4303542156272254451L;

  @Override
  public IComputationInstrumenter create(ParameterBlock parameter, Context context) {
    return new MLSpaceInstrumenter(parameter);
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    return checkForImplementedModel(parameters,
        new MLSpaceProcessorFactory().getSupportedInterfaces());
  }

}
