/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.composed;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * Allows to apply multiple computation instrumenters.
 * 
 * @author Roland Ewald
 * 
 */
public class ComputationInstrumenterListFactory extends
    ComputationInstrumenterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2810353022767234373L;

  /**
   * The parameter that stores the list of computation instrumenter factories.
   * Type: List<ComputationInstrumenterFactory>.
   */
  public static final String INSTR_LIST = "InstrumenterFactoryList";

  /** The list of computation instrumenter factories. */
  private List<ComputationInstrumenterFactory> comInstrFacList;

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    comInstrFacList =
        ParameterBlocks.getSBOrDefault(parameters, INSTR_LIST,
            new ArrayList<ComputationInstrumenterFactory>()).getValue();
    for (ComputationInstrumenterFactory simInstrFac : comInstrFacList) {
      if (simInstrFac.supportsParameters(parameters) > 0) {
        return 1;
      }
    }
    return 0;
  }

  @Override
  public IComputationInstrumenter create(ParameterBlock parameter, Context context) {

    List<IComputationInstrumenter> instrumenters = new ArrayList<>();

    for (ComputationInstrumenterFactory simInstrFac : comInstrFacList) {
      if (simInstrFac.supportsParameters(parameter) > 0) {
        instrumenters.add(simInstrFac.create(parameter, SimSystem.getRegistry().createContext()));
      }
    }

    return new ComputationInstrumenterListWrapper(instrumenters);
  }

}
