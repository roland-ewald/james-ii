/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.steering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.execonfig.IParamBlockUpdate;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.experiments.variables.SubLevelStatus;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Experiment variables that have a modifier which implements
 * {@link IExperimentSteerer}. This means, these variables are not changed by
 * some simple rule, but by an algorithm (that potentially takes results into
 * account, and therefore needs to specify model and simulation instrumenters).
 * Observers are notified when
 * {@link SteeredExperimentVariables#currentAssignment} changes. In case it is
 * null, the variables have been initialised again. This layer does not
 * necessarily have to contain any variables in
 * {@link ExperimentVariables#getVariables()}, as it might have a dynamic list
 * of parameters to set. The steerer controls the whole process via
 * {@link IExperimentSteerer#getNextVariableAssignment()}. Nevertheless,
 * defining experiment variables here might help the configuration of the
 * steerer.
 * 
 * @author Roland Ewald
 * @param <X>
 */
public class SteeredExperimentVariables<X extends IExperimentSteerer> extends
    ExperimentVariables {

  /** Serialisation ID. */
  private static final long serialVersionUID = -2705000857531728927L;

  /** Reference to the experiment steerer to be used. */
  private transient X steerer;

  /** Class of the experiment steerer (to find corresponding steerer variable). */
  private Class<X> steererClass;

  /** This variable holds the steerer to be used. */
  private transient ExperimentSteererVariable<X> steererVariable;

  /** Stores current assignment of variables. */
  private transient VariablesAssignment currentAssignment;

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public SteeredExperimentVariables() {
  }

  /**
   * Default constructor.
   * 
   * @param classOfSteerer
   *          class of the steerer to be used
   */
  public SteeredExperimentVariables(Class<X> classOfSteerer) {
    super();
    steererClass = classOfSteerer;
  }

  @Override
  public void init(ExperimentVariables values, boolean top) {

    if (top) {
      reset(values);
    }

    currentAssignment = null;
    changed();

    // Find k-th suitable steerer variable, with k-1 being the number of similar
    // steered experiment variables levels on top (the second steered variables
    // layer should use the second steerer variable)
    steererVariable = this.findSteererVariable(values);

    steerer = steererVariable.getValue();
    steerer.init();
    if (getSubLevel() != null && steerer.allowSubStructures()) {
      getSubLevel().init(values, false);
    }
  }

  /**
   * Find k-th suitable steerer variable, with k-1 being the number of similar
   * steered experiment variables levels on top (the second steered variables
   * layer should use the second steerer variable).
   * 
   * @param <C>
   *          the type of experiment steerer to be returned
   * 
   * @param currentLevel
   *          the current level of experiment variables
   * 
   * @return the steerer variable that sets the steerer for this instance
   * 
   * @throws NoSuchElementException
   *           if no suitable steerer variable was found
   */
  public <C extends IExperimentSteerer> ExperimentSteererVariable<C> findSteererVariable(
      ExperimentVariables currentLevel) {
    return findSteererVariable(currentLevel,
        new ArrayList<ExperimentSteererVariable<C>>(), 0);
  }

  /**
   * Find a steerer variable from given level. Uses a recursive linear search to
   * search for k-th suitable steerer variable, with k-1 being the number of
   * similar steered experiment variables levels on top (the second steered
   * variables layer should use the second steerer variable, and so on).
   * 
   * @param <C>
   *          the type of experiment steerer to be returned
   * 
   * @param currentLevel
   *          the current level to be searched
   * @param foundVariables
   *          list of found steerer variables of compatible type
   * @param foundSteeredVarsLevels
   *          number of {@link SteeredExperimentVariables} instances found on
   *          the way down
   * 
   * @return corresponding steerer variable for this instance
   * 
   * @throws NoSuchElementException
   *           if no suitable steerer variable was found
   */
  @SuppressWarnings("unchecked")
  protected <C extends IExperimentSteerer> ExperimentSteererVariable<C> findSteererVariable(
      ExperimentVariables currentLevel,
      List<ExperimentSteererVariable<C>> foundVariables,
      int foundSteeredVarsLevels) {

    // Is the current level one with steered variables?
    if (currentLevel instanceof SteeredExperimentVariables) {
      // Is it of the same kind, i.e. do we need to count this level for finding
      // the variable setting our steerer?
      if (((SteeredExperimentVariables<?>) currentLevel).getSteererClass()
          .equals(steererClass)) {
        // Is it us - then we can stop here and choose the k-th variable from
        // the list!
        if (currentLevel == this) {
          if (foundVariables.size() <= foundSteeredVarsLevels) {
            throw new RuntimeException();
          }
          return foundVariables.get(foundSteeredVarsLevels);
        }

        // Otherwise, we have to count this level
        foundSteeredVarsLevels++;
      }
    }

    // Now we populate the list of available steerer variables for our steerer
    // class
    for (ExperimentVariable<?> var : currentLevel.getVariables()) {
      if (var instanceof ExperimentSteererVariable<?>
          && ((ExperimentSteererVariable<?>) var).getSteererClass().equals(
              steererClass)) {
        foundVariables.add((ExperimentSteererVariable<C>) var);
      }
    }

    // We are not finished, we should go down the linked list
    if (currentLevel.hasSubLevel()) {
      return findSteererVariable(currentLevel.getSubLevel(), foundVariables,
          foundSteeredVarsLevels);
    }

    throw new NoSuchElementException();
  }

  /**
   * Get the parameters for the experiment.
   * 
   * @return experiment parameters
   */
  @Override
  public ParameterBlock getExperimentParameters() {
    ParameterBlock result =
        getSubLevel() != null ? getSubLevel().getExperimentParameters()
            : new ParameterBlock();
    ParameterBlock block = steerer.getExperimentParameters();
    mergeBlocks(result, block);
    return result;
  }

  @Override
  public SubLevelStatus nextThisLevel(ExperimentVariables values) {
    // Beginning of the experiment
    if (steerer == null) {
      reset(values);
      return SubLevelStatus.HAS_NEXT;
    }

    // Get next variable assignment
    currentAssignment = steerer.getNextVariableAssignment();
    changed();
    if (currentAssignment == null) {
      return steerer.isFinished() ? SubLevelStatus.DONE : SubLevelStatus.WAIT;
    }

    for (ExperimentVariable<?> variable : getVariables()) {
      assignVariable(variable, currentAssignment.get(variable.getName()));
    }
    return SubLevelStatus.HAS_NEXT;
  }

  /**
   * Assign variable.
   * 
   * @param <V>
   *          the type of the value to be set
   * 
   * @param variable
   *          the variable
   * @param value
   *          the value
   */
  @SuppressWarnings("unchecked")
  protected <V> void assignVariable(ExperimentVariable<V> variable, Object value) {
    variable.setValue((V) value);
  }

  /**
   * Get the new steerer and initialise everything.
   * 
   * @param values
   *          the values
   */
  @Override
  public void reset(ExperimentVariables values) {
    if (steerer != null && steerer.allowSubStructures()) {
      super.reset(values);
    }
    // Get new steerer
    if (steererVariable != null) {
      steerer = steererVariable.getValue();
      steerer.init();
      nextThisLevel(values);
    }
  }

  /**
   * Stores the current values of the experiment variables to a {@link Map} that
   * is handed over.
   * 
   * @param modelParams
   *          the parameter map to which the values are added
   * @param simParams
   *          parameters for the execution
   */
  @Override
  public void storeSettingToMap(Map<String, Object> modelParams,
      ParameterBlock simParams) {
    if (currentAssignment == null) {
      currentAssignment = steerer.getNextVariableAssignment();
      changed();
    }
    for (Entry<String, Serializable> assignment : currentAssignment.entrySet()) {
      if (assignment.getValue() instanceof IParamBlockUpdate) {
        ((IParamBlockUpdate) assignment.getValue()).update(simParams);
      } else if (!modelParams.containsKey(assignment.getKey())) {
        modelParams.put(assignment.getKey(), assignment.getValue());
      }
    }
    if (hasSubLevel() && steerer.allowSubStructures()) {
      getSubLevel().storeSettingToMap(modelParams, simParams);
    }
  }

  @Override
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInformation) {
    steerer.executionFinished(simConfig, runInformation);
  }

  /**
   * Gets the steerer.
   * 
   * @return the steerer
   */
  public X getSteerer() {
    return steerer;
  }

  /**
   * Gets the steerer class.
   * 
   * @return the steerer class
   */
  public Class<X> getSteererClass() {
    return steererClass;
  }

  /**
   * Sets the steerer class.
   * 
   * @param steererClass
   *          the new steerer class
   */
  public void setSteererClass(Class<X> steererClass) {
    this.steererClass = steererClass;
  }

  /**
   * Gets the steerer variable.
   * 
   * @return the steerer variable
   */
  public ExperimentSteererVariable<X> getSteererVariable() {
    return steererVariable;
  }

  /**
   * Gets the current assignment.
   * 
   * @return the current assignment
   */
  public VariablesAssignment getCurrentAssignment() {
    return currentAssignment;
  }

}
