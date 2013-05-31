/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.experiments.steering.IExperimentSteerer;
import org.jamesii.core.experiments.steering.SteeredExperimentVariables;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.ExperimentVariablesTest;
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;

/**
 * Test case for {@link IExperimentSteerer}, checks if they conform to the
 * interface.
 * 
 * @param <X>
 *          the type of the experiment steerer
 * 
 * @author Roland Ewald
 */
public abstract class TestExperimentSteering<X extends IExperimentSteerer>
    extends ExperimentVariablesTest {

  /** The number of steerers considered. */
  public static final int NUM_OF_STEERERS = 2;

  /** The number of variable assignments. */
  public static final int NUM_VAR_ASSIGNMENTS = 2;

  /** The number of replications. */
  public static final int NUM_REPLICATIONS = 10;

  /** The first steerer to be tested. */
  protected X steerer1;

  /** The second steerer to be tested (same class, other instance). */
  protected X steerer2;

  /** The steerer variable. */
  protected ExperimentSteererVariable<X> expSteererVariable;

  /** Experiment steerer variables. */
  protected SteeredExperimentVariables<X> steererExpVars;

  @Override
  public void setUp() {
    super.setUp();

    steerer1 = createExperimentSteerer();
    steerer2 = createExperimentSteerer();

    List<X> steerers = new ArrayList<>();
    steerers.add(steerer1);
    steerers.add(steerer2);

    SequenceModifier<X> modifier = new SequenceModifier<>(steerers);
    expSteererVariable =
        new ExperimentSteererVariable<>("steererVar", getSteererClass(),
            steerer1, modifier);

    // Add experiment steerer variable as first variable
    ExperimentVariables expVars = getTestExperiment().getExperimentVariables();
    ExperimentVariables newLevel = new ExperimentVariables();
    newLevel.addVariable(expSteererVariable);
    newLevel.setSubLevel(expVars);
    getTestExperiment().setExperimentVariables(newLevel);

    steererExpVars = new SteeredExperimentVariables<>(getSteererClass());
    ExperimentVariables lLevel =
        getTestExperiment().getExperimentVariables().getLowestSubLevel();
    lLevel.setSubLevel(steererExpVars);

  }

  /**
   * The two steerer shall be executed with all combinations.
   * 
   * @return the setup number
   */
  @Override
  protected int getSetupNumber() {
    return NUM_OF_STEERERS * super.getSetupNumber() * numberOfConfigs();
  }

  /**
   * Creates the {@link IExperimentSteerer} to be tested.
   * 
   * @return the {@link IExperimentSteerer} to be tested
   */
  protected abstract X createExperimentSteerer();

  /**
   * Get class of experiment steerer.
   * 
   * @return class of the steerer
   */
  protected abstract Class<X> getSteererClass();

  /**
   * Return fixed number of configurations the steerer will generate in this
   * setting. Return -1 if this cannot be determined
   * 
   * @return fixed number of configurations the steerer will generate
   */
  public int numberOfConfigs() {
    return NUM_VAR_ASSIGNMENTS;
  }

}
