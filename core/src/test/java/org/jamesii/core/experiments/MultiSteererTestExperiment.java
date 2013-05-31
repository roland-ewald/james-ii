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
import org.jamesii.core.experiments.variables.modifier.SequenceModifier;

/**
 * Tests two subsequent layer of {@link ExperimentSteererVariable}s in
 * conjunction with two {@link ExperimentSteererVariable} object on the top two
 * levels.
 * 
 * @param <X>
 *          the type of the experiment steerer to be tested
 * @author Roland Ewald
 */
public abstract class MultiSteererTestExperiment<X extends IExperimentSteerer>
    extends TestExperimentSteering<X> {

  /** Upper steerer variable. */
  protected ExperimentSteererVariable<X> upperExpSteererVariable;

  /** Newly generated steerer number one. */
  protected X newSteerer1;

  /** Newly generated steerer number two. */
  protected X newSteerer2;

  /** Lowest experiment steerer variables. */
  protected SteeredExperimentVariables<X> lowerSteererExpVars;

  @Override
  public void setUp() {
    super.setUp();

    // Creating *second* steerer variable

    newSteerer1 = createExperimentSteerer();
    newSteerer2 = createExperimentSteerer();

    List<X> steerers = new ArrayList<>();
    steerers.add(newSteerer1);
    steerers.add(newSteerer2);

    SequenceModifier<X> modifier = new SequenceModifier<>(steerers);
    upperExpSteererVariable =
        new ExperimentSteererVariable<>("steererVar", getSteererClass(),
            newSteerer1, modifier);

    // Add experiment steerer variable as first variable
    ExperimentVariables expVars = getTestExperiment().getExperimentVariables();
    ExperimentVariables newLevel = new ExperimentVariables();
    newLevel.addVariable(upperExpSteererVariable);
    newLevel.setSubLevel(expVars);
    getTestExperiment().setExperimentVariables(newLevel);

    // Add two integer experiment variable for testing
    // ExperimentVariable<Integer> x1 = new ExperimentVariable<Integer>("x1",
    // -1, new IncrementModifierInteger(1, 1, 1));

    lowerSteererExpVars = new SteeredExperimentVariables<>(getSteererClass());
    ExperimentVariables lLevel =
        getTestExperiment().getExperimentVariables().getLowestSubLevel();
    lLevel.setSubLevel(lowerSteererExpVars);

  }

  @Override
  public int getSetupNumber() {
    // There is an additional level with two steerers, each generation x
    // configurations
    return NUM_OF_STEERERS * numberOfConfigs() * super.getSetupNumber();
  }

  /**
   * Test steerer exp var.
   */
  public void testSteererExpVar() {
    ExperimentVariables expVariables =
        getTestExperiment().getExperimentVariables();
    expVariables.init(expVariables);
    List<TestExecSetup> paramSetups = getSetups(expVariables);
    int firstHalf = paramSetups.size() >> 1;
    for (int i = 0; i < firstHalf; i++) {
      assertEquals(
          newSteerer1,
          paramSetups.get(i).getFirstValue()
              .get("steererVar_" + upperExpSteererVariable.hashCode()));
    }
    for (int i = firstHalf; i < paramSetups.size(); i++) {
      assertEquals(
          newSteerer2,
          paramSetups.get(i).getFirstValue()
              .get("steererVar_" + upperExpSteererVariable.hashCode()));
    }
  }

  /**
   * Tests if the right steerer variable is found for each level of steered
   * experiment variables.
   */
  public void testSteererVariablesLookUp() {
    super.testParameterGeneration();
    // According to the look-up rules, the lowest experiment steerer variables
    // should be associated with the lower (original) ExperimentSteererVariable
    assertEquals(expSteererVariable, lowerSteererExpVars.getSteererVariable());
    // ... and vice versa
    assertEquals(upperExpSteererVariable, steererExpVars.getSteererVariable());

  }

}
