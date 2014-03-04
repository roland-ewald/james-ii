/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IParameter;

/**
 * Component to augment a {@link ParameterBlock} before it is used to query
 * factories in the {@link SelectionTreeSet}. This is useful whenever a factory
 * expects data on their execution context to be present in the
 * {@link ParameterBlock}.
 * 
 * @author Roland Ewald
 */
public interface IParameterBlockAugmenter {

  /**
   * This method shall be used to create a new parameter block based on the
   * original parameter block and the selection tree set / current node to
   * extract hierarchical information from the selection tree set.
   * 
   * @param originalParamBlock
   *          the original parameter block
   * @param selectionTreeSet
   *          the selection tree set
   * @param currentNode
   *          the current node
   * @param topLevelParameter
   *          the top level parameter
   * @return the augmented/adjusted parameter block
   */
  ParameterBlock augment(ParameterBlock originalParamBlock,
      SelectionTreeSet selectionTreeSet, SelTreeSetVertex currentNode,
      IParameter topLevelParameter);

}
