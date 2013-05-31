/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization;

import java.util.List;

import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Manages list of optimisers and their (un-)registration as observers of
 * BaseExperiment.
 * 
 * @author Arvid Schwecke
 */
public class OptimizerVariable extends ExperimentSteererVariable<Optimizer> {
  static {
    SerialisationUtils.addDelegateForConstructor(OptimizerVariable.class,
        new IConstructorParameterProvider<OptimizerVariable>() {
          @Override
          public Object[] getParameters(OptimizerVariable variable) {
            return new Object[] { ((SequenceModifier<Optimizer>) variable
                .getModifier()).getValues() };
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -8740445011285037605L;

  /** ID of experiment variable. */
  private static final String id = "opt";

  /**
   * Default constructor.
   * 
   * @param optimizers
   *          the optimisers to be used
   */
  public OptimizerVariable(List<Optimizer> optimizers) {
    super(id, Optimizer.class, optimizers.get(0), new SequenceModifier<>(
        optimizers));
  }

  /**
   * Instantiates a new optimizer variable.
   * 
   * @param optimizers
   *          the optimizers
   */
  public OptimizerVariable(Optimizer[] optimizers) {
    super(id, Optimizer.class, optimizers[0],
        new SequenceModifier<>(optimizers));
  }

}
