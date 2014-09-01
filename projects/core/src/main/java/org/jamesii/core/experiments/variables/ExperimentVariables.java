/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.variables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.execonfig.ExecutionConfigurationVariable;
import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The variables (parameters) of an experiment. The variables are organised in
 * levels. Per level you can have any number of variables which are modified in
 * parallel as soon as there is no more change on the sub level which will be
 * reset in this case.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * 
 */
public class ExperimentVariables extends NamedEntity {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -4802186622913251936L;

  /**
   * Identifier for the model instrumenter in a parameter block. Warning: the
   * instrumenters won't be forwarded by the BaseExperiment anymore
   */
  @Deprecated
  public static final String MODEL_INSTRUMENTERS = "modelInstrumenters";

  /**
   * Identifier for the simulation instrumenter in a parameter block. Warning:
   * the instrumenters won't be forwarded by the BaseExperiment anymore
   */
  @Deprecated
  public static final String SIMULATION_INSTRUMENTERS =
      "simulationInstrumenters";

  /**
   * Identifier for the model instrumenter factories in a HashMap with their a
   * parameter blocks.
   */
  public static final String MODEL_INSTRUMENTER_FACTORIES =
      "modelInstrumenterFactories";

  /**
   * Identifier for the simulation instrumenter factories in a HashMap with
   * their a parameter blocks.
   */
  public static final String SIMULATION_INSTRUMENTER_FACTORIES =
      "simulationInstrumenterFactories";

  /** Identifier for the replication criterion factories in a parameter block. */
  public static final String REPLICATION_CRITERION_FACTORIES =
      "replicationCriteria";

  /**
   * Identifier for the stop time factory and its parameters in a parameter
   * block.
   */
  public static final String STOP_TIME_FACTORY = "stopTimeFactory";

  /** Variables are organised in a 'tree' having one sub branch at most. */
  private ExperimentVariables subLevel = null;

  /**
   * List of variables on that level.
   */
  private List<ExperimentVariable<?>> variables = new ArrayList<>();

  /**
   * Constructor for bean compliance.
   */
  public ExperimentVariables() {
    super("");
  }

  /**
   * Adds a variable.
   * 
   * @param variable
   */
  public void addVariable(ExperimentVariable<?> variable) {
    variables.add(variable);
  }

  /**
   * Returns first instance of a given class within the parameter tree.
   * 
   * @param <E>
   *          type of the class
   * @param cl
   *          the class
   * @return first instance of a given class within the parameter tree
   */
  @SuppressWarnings("unchecked")
  public <E> E find(Class<?> cl) {
    E result = null;
    for (ExperimentVariable<?> variable : variables) {
      if (cl.isInstance(variable)) {
        result = (E) variable;
        return result;
      }
    }
    if (subLevel != null) {
      result = subLevel.find(cl); // cast required by javac
    }
    return result;
  }

  /**
   * Get the parameters for the experiment.
   * 
   * @return experiment parameters
   */
  public ParameterBlock getExperimentParameters() {
    ParameterBlock result =
        subLevel != null ? subLevel.getExperimentParameters()
            : new ParameterBlock();
    for (ExperimentVariable<?> expVar : variables) {
      ParameterBlock block = expVar.getModifier().getExperimentParameters();
      mergeBlocks(result, block);
    }
    return result;
  }

  /**
   * Merges two parameter blocks. Two parameters with the same identifier are
   * merged by merging the lists, if the values are lists.
   * 
   * @param block1
   *          first parameter block which at the end contains all the
   *          information
   * @param block2
   *          second parameter block
   */
  @SuppressWarnings("unchecked")
  // too general for generics
  protected void mergeBlocks(ParameterBlock block1, ParameterBlock block2) {
    if (block1 == null || block2 == null) {
      return;
    }
    for (Map.Entry<String, ParameterBlock> entry : block2.getSubBlocks()
        .entrySet()) {
      String paramName = entry.getKey();
      ParameterBlock subBlock1 = block1.getSubBlocks().get(paramName);
      ParameterBlock subBlock2 = entry.getValue();
      if (subBlock1 != null) {
        Object value1 = subBlock1.getValue();
        Object value2 = subBlock2.getValue();
        if (value1 instanceof List) {
          if (value2 instanceof List) {
            ((List<Object>) value1).addAll((List<Object>) value2);
          } else {
            ((List<Object>) value1).add(value2);
          }
        } else if (value2 instanceof List) {
          ((List<Object>) value2).add(value1);
          subBlock1.setValue(value2);
        }
        mergeBlocks(subBlock1, subBlock2);
      } else {
        block1.addSubBl(paramName, subBlock2);
      }
    }
  }

  /**
   * Get the next sub-level.
   * 
   * @return the next (sub-)level
   */
  public final ExperimentVariables getSubLevel() {
    return subLevel;
  }

  /**
   * Tests whether a sub-level exists.
   * 
   * @return true if a sub-level exists
   */
  public final boolean hasSubLevel() {
    return subLevel != null;
  }

  /**
   * Traverses all levels until the lowest is found. The lowest sub-level has no
   * sub level. Comes in handy when adding a layer to an existing set of
   * experiment variables.
   * 
   * @return reference to the lowest sub-level
   */
  public ExperimentVariables getLowestSubLevel() {
    if (subLevel == null) {
      return this;
    }

    ExperimentVariables sLevel = subLevel;
    while (sLevel != null) {
      if (sLevel.getSubLevel() == null) {
        return sLevel;
      }
      sLevel = sLevel.getSubLevel();
    }
    return null;
  }

  /**
   * Get list of experiment variables.
   * 
   * @return list of experiment variables
   */
  public final List<ExperimentVariable<?>> getVariables() {
    return variables;
  }

  /**
   * Recursive initialisation for repetition of experiment (so that all
   * modifiers are reset and start with initial values, when next is called
   * *next* time). In contrast,
   * {@link ExperimentVariables#reset(ExperimentVariables)} generates a *new*
   * variable setting with initial values and is used when
   * {@link ExperimentVariables#next(ExperimentVariables)} is called.
   * 
   * @param values
   *          the experiment values
   */
  public void init(ExperimentVariables values) {
    init(values, true);
  }

  /**
   * Initialises experiment variables. TAKE CARE: This is only public for
   * testing purposes, use {@link ExperimentVariables#init(ExperimentVariables)}
   * unless you know what you are doing.
   * 
   * @param values
   *          experiment values to be initialised
   * @param top
   *          flag to determine if this is the top level of experiment variables
   *          (triggers reset)
   */
  public void init(ExperimentVariables values, boolean top) {
    if (top) {
      this.reset(values);
    }
    if (subLevel == null) {
      for (ExperimentVariable<?> variable : variables) {
        variable.reset();
      }
    }
    if (subLevel != null) {
      subLevel.init(values, false);
    }
  }

  /**
   * Compute the next experiment parameters.
   * 
   * @param values
   *          reference to the topmost experiment variables
   * 
   * @return whether a new setup is found, this level is done, or it has not
   *         been decided yet
   */
  public SubLevelStatus next(ExperimentVariables values) {

    if (subLevel == null) {
      return nextThisLevel(values);
    }

    SubLevelStatus subLevelStatus = subLevel.next(values);
    if (subLevelStatus != SubLevelStatus.DONE) {
      return subLevelStatus;
    }

    SubLevelStatus myStatus = nextThisLevel(values);
    if (myStatus == SubLevelStatus.HAS_NEXT) {
      subLevel.reset(values);
    }
    return myStatus;
  }

  /**
   * Computes the next values for all variables on this level.
   * 
   * @param values
   *          reference to the topmost experiment variables
   * @return return status: all variables are 'done' or there is a next setup
   */
  public SubLevelStatus nextThisLevel(ExperimentVariables values) {
    boolean nextSetupExists = false;
    for (ExperimentVariable<?> variable : variables) {
      if (variable.next(values)) {
        nextSetupExists = true;
      }
    }
    return nextSetupExists ? SubLevelStatus.HAS_NEXT : SubLevelStatus.DONE;
  }

  /**
   * Removes experiment variable.
   * 
   * @param variable
   *          experiment variable to be removed
   */
  public void removeVariable(ExperimentVariable<?> variable) {
    variables.remove(variable);
  }

  /**
   * Reset the variables of this level.
   * 
   * @param values
   *          the values
   */
  public void reset(ExperimentVariables values) {
    for (ExperimentVariable<?> variable : variables) {
      variable.reset();
      if (variable.getModifier() != null) {
        variable.next(values);
      }
    }
    if (subLevel != null) {
      subLevel.reset(values);
    }
  }

  /**
   * Set a new sub-level.
   * 
   * @param subLevel
   *          new sub-level
   */
  public void setSubLevel(ExperimentVariables subLevel) {
    this.subLevel = subLevel;
  }

  /**
   * Set new list of experiment variables.
   * 
   * @param variables
   *          list of experiment variables
   */
  public void setVariables(List<ExperimentVariable<?>> variables) {
    this.variables = variables;
  }

  /**
   * Stores the current values of the experiment variables to a {@link Map} that
   * is handed over.
   * 
   * @param modelParams
   *          the parameter map to which the model parameters are added
   * @param simParams
   *          the parameter block for all execution parameters
   */
  public void storeSettingToMap(Map<String, Object> modelParams,
      ParameterBlock simParams) {
    storeSettingToMap(modelParams, simParams, false);
  }

  /**
   * Stores the current values of the experiment variables to a {@link Map} that
   * is handed over.
   * 
   * @param modelParams
   *          the parameter map to which the model parameters are added
   * @param simParams
   *          the parameter block for all execution parameters
   * @param includeAllVars
   *          flag that determines if also instances of, e.g.,
   *          {@link ExperimentSteererVariable} shall be included
   */
  public void storeSettingToMap(Map<String, Object> modelParams,
      ParameterBlock simParams, boolean includeAllVars) {
    final Map<String, Object> map = modelParams;
    for (ExperimentVariable<?> variable : getVariables()) {
      // If given variable alters execution configuration, and not the model
      // parameters
      if (variable instanceof ExecutionConfigurationVariable<?>) {
        ((ExecutionConfigurationVariable<?>) variable).getValue().update(
            simParams);
      } else if (includeAllVars
          || !(variable instanceof ExperimentSteererVariable<?>)) {
        // Map will only be filled if this variable has not already been set by
        // an upper level
        if (!map.containsKey(variable.getName())) {
          map.put(variable.getName(), variable.getValue());
        }
      }
    }
    if (hasSubLevel()) {
      subLevel.storeSettingToMap(modelParams, simParams);
    }
  }

  /**
   * Notifies experiment variables that an execution has been finished.
   * 
   * @param simConfig
   *          the sim config
   * @param runInformation
   *          the run information
   */
  public void executionFinished(TaskConfiguration simConfig,
      RunInformation runInformation) {
  }

  /**
   * Finds a variable recursively by its name.
   * 
   * 
   * @param name
   *          the name of the variable to be found
   * @return returns the variable with given name
   * @throws NoSuchElementException
   *           if none was found
   */
  public ExperimentVariable<?> findVariable(String name) {
    for (ExperimentVariable<?> var : variables) {
      if (var.getName().compareTo(name) == 0) {
        return var;
      }
    }
    // if its not in this level, go down
    if (subLevel != null) {
      return subLevel.findVariable(name);
    }
    throw new NoSuchElementException();
  }

  /**
   * Lazy initialisation of experiment variables. Variables in the list are
   * added to the experiment, one per level.
   * 
   * @param variables
   *          variables to be set up
   * @return the topmost {@link ExperimentVariables} object
   */
  public static ExperimentVariables generateExperimentVariables(
      List<ExperimentVariable<?>> variables) {

    // Create simple nested structure
    List<List<ExperimentVariable<?>>> nestedStructure = new ArrayList<>();
    for (ExperimentVariable<?> variable : variables) {
      ArrayList<ExperimentVariable<?>> singleVarList = new ArrayList<>();
      singleVarList.add(variable);
      nestedStructure.add(singleVarList);
    }
    return generateNestedExperimentVariables(nestedStructure);
  }

  /**
   * Generate nested experiment variables.
   * 
   * @param variables
   *          list of exp-var lists, reflecting the desired structure
   * @return the experiment variables
   */
  public static ExperimentVariables generateNestedExperimentVariables(
      List<List<ExperimentVariable<?>>> variables) {

    ExperimentVariables parent = new ExperimentVariables();
    if (variables.isEmpty()) {
      return parent;
    }

    // Add first-level variables to overall parent
    for (ExperimentVariable<?> v : variables.get(0)) {
      parent.addVariable(v);
    }

    // Create the linked list of the other exp-vars layers...
    ExperimentVariables directParent = parent;
    for (int i = 1; i < variables.size(); i++) {
      ExperimentVariables child = new ExperimentVariables();
      List<ExperimentVariable<?>> varsOnThisLevel = variables.get(i);
      for (ExperimentVariable<?> v : varsOnThisLevel) {
        child.addVariable(v);
      }
      directParent.setSubLevel(child);
      directParent = child;
    }
    return parent;
  }

  /**
   * Traverses all experiment variables and calls a visitor.
   * 
   * @param experimentVars
   *          experiment variables to be traversed
   * @param visitor
   *          visitor to be used
   */
  protected static void traverseVariables(ExperimentVariables experimentVars,
      IVariableVisitor visitor) {
    if (experimentVars == null) {
      return;
    }
    ExperimentVariables expVars = experimentVars;
    while (expVars != null) {
      List<ExperimentVariable<?>> variables = expVars.getVariables();
      for (int i = 0; i < variables.size(); i++) {
        visitor.visit(variables.get(i));
      }
      expVars = expVars.getSubLevel();
    }
  }

  /**
   * Recursive function that checks whether this list of
   * {@link ExperimentVariables} contains an {@link ExperimentSteererVariable}.
   * 
   * @param expVars
   *          the list of experiment variables
   * @return true if the setup contains in instance of
   *         {@link ExperimentSteererVariable}
   */
  public static boolean containsSteerer(ExperimentVariables expVars) {
    List<ExperimentVariable<?>> variables = expVars.getVariables();
    for (ExperimentVariable<?> variable : variables) {
      if (variable instanceof ExperimentSteererVariable<?>) {
        return true;
      }
    }
    if (expVars.hasSubLevel()) {
      return containsSteerer(expVars.getSubLevel());
    }
    return false;
  }

  /**
   * Recursive function that lists all {@link ExperimentSteererVariable}
   * contained in this {@link ExperimentVariables} instance and its sub-levels.
   * 
   * @param expVars
   *          the list of experiment variables
   * @return (potentially empty) list of experiment steerer variables
   */
  public static List<ExperimentSteererVariable<?>> getExperimentSteererVariables(
      ExperimentVariables expVars) {
    List<ExperimentSteererVariable<?>> result = new ArrayList<>();
    return expVars == null ? result : getExperimentSteererVariables(expVars,
        result);
  }

  /**
   * Recursive function to traverse the lists and find all
   * {@link ExperimentSteererVariable} instances.
   * 
   * @param expVars
   *          the experiment variables
   * @param foundVars
   *          list to store found variables in
   * @return the list of found variables
   */
  protected static List<ExperimentSteererVariable<?>> getExperimentSteererVariables(
      ExperimentVariables expVars, List<ExperimentSteererVariable<?>> foundVars) {
    List<ExperimentVariable<?>> variables = expVars.getVariables();
    for (ExperimentVariable<?> variable : variables) {
      if (variable instanceof ExperimentSteererVariable<?>) {
        foundVars.add((ExperimentSteererVariable<?>) variable);
      }
    }
    if (expVars.hasSubLevel()) {
      return getExperimentSteererVariables(expVars.getSubLevel(), foundVars);
    }
    return foundVars;
  }

}
