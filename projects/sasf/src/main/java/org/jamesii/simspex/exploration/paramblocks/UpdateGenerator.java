/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.paramblocks;


import java.util.HashSet;
import java.util.Set;

import org.jamesii.core.experiments.execonfig.IParamBlockUpdate;
import org.jamesii.core.experiments.execonfig.SingularParamBlockUpdate;
import org.jamesii.core.experiments.steering.VariablesAssignment;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.misc.ParameterUtils;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;

/**
 * This class provides means for the generation of {@link IParamBlockUpdate}
 * instances that reflect the {@link SelectionTreeSetElement} of the
 * {@link SelectionTreeSet} at hand.
 * 
 * @author Roland Ewald
 * 
 */
public class UpdateGenerator {

  /**
   * Prefix for the variable names that are associated with the generated
   * {@link IParamBlockUpdate} instances.
   */
  private static final String UPDATE_NAME_PREFIX = "generatedUpdateRule";

  /**
   * The selection tree set.
   */
  private SelectionTreeSet selectionTreeSet;

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public UpdateGenerator() {
  }

  /**
   * Default constructor.
   * 
   * @param selTreeSet
   */
  public UpdateGenerator(SelectionTreeSet selTreeSet) {
    selectionTreeSet = selTreeSet;
  }

  /**
   * Generates the updates so that all elements as defined by the
   * {@link SelectionTreeSetElement} are selected.
   * 
   * @param parameterBlock
   *          the element from the set of configurations
   * @return a set of updates to be applied to the {@link ParameterBlock} that
   *         will be handed over to the simulation layer, so that the given
   *         {@link ParameterBlock} can be reconstructed there
   */
  public static Set<IParamBlockUpdate> generateUpdates(
      ParameterBlock parameterBlock) {
    Set<IParamBlockUpdate> resultSet = new HashSet<>();
    if (parameterBlock.hasSubBlock(ProcessorFactory.class.getName())) {
      resultSet.add(new SingularParamBlockUpdate(new String[] {},
          ProcessorFactory.class.getName(), parameterBlock
              .getSubBlock(ProcessorFactory.class.getName())));
    }
    return resultSet;
  }

  /**
   * Generates the updates to configure the system according to the given
   * {@link SelectionTreeSetElement} and letting it run until the given
   * simulation end time is reached.
   * 
   * @param simEndTime
   *          the simulation end time
   * @param parameterBlock
   *          the element of the {@link SelectionTreeSet} to be used
   * @return the set of parameter block updates
   */
  public static Set<IParamBlockUpdate> generateUpdates(Double simEndTime,
      ParameterBlock parameterBlock) {
    Set<IParamBlockUpdate> result = generateUpdates(parameterBlock);
    result.add(new SingularParamBlockUpdate(new String[] {},
        ParameterUtils.SIM_STOP_TIME, new ParameterBlock(simEndTime)));
    return result;
  }

  /**
   * Converts a given set of {@link IParamBlockUpdate} instances to a
   * {@link VariablesAssignment} containing them.
   * 
   * @param updates
   *          the set of updates
   * @return the corresponding variable assignment
   */
  public static VariablesAssignment getUpdateAssignment(
      Set<IParamBlockUpdate> updates) {
    VariablesAssignment va = new VariablesAssignment();
    int counter = 1;
    for (IParamBlockUpdate update : updates) {
      va.put(UPDATE_NAME_PREFIX + counter++, update);
    }
    return va;
  }

  /**
   * Gets the selection tree set.
   * 
   * @return the selection tree set
   */
  public SelectionTreeSet getSelectionTreeSet() {
    return selectionTreeSet;
  }

  /**
   * Sets the selection tree set.
   * 
   * @param set
   *          the new selection tree set
   */
  public void setSelectionTreeSet(SelectionTreeSet set) {
    this.selectionTreeSet = set;
  }

}
