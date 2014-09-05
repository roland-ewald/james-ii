/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.simple;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.exploration.plugintype.SimSpaceExplorerFactory;


/**
 * Factory for simple simulation space explorer.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleSimSpaceExplorerFactory extends SimSpaceExplorerFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 2197908713817096835L;

  /**
   * Parameter to pass along a set of selection trees. Type:
   * {@link SelectionTreeSet}.
   */
  public static final String SELECTION_TREE_SET = "selTreeSet";

  @Override
  public SimpleSimSpaceExplorer create(ParameterBlock params, Context context) {
    if (params.hasSubBlock(SELECTION_TREE_SET)) {
      return new SimpleSimSpaceExplorer(
          (SelectionTreeSet) params.getSubBlockValue(SELECTION_TREE_SET));
    }
    return new SimpleSimSpaceExplorer();
  }

}
