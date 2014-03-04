/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.util.ICallBack;
import org.jamesii.core.util.graph.traverse.BreadthFirstSearch;
import org.jamesii.core.util.graph.trees.BasicTree;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Flattens a selection tree by prescribing an invertible transition between the
 * selection tree and a configuration. A configuration is a map from string to
 * object.
 * 
 * @see SelectionTree
 * @see Configuration
 * 
 * @author Roland Ewald
 */
public class SelectionTreeFlattener implements ICallBack<SelectedFactoryNode> {

  /** The separator between elements on the selection tree path. */
  static final String SEPARATOR = "&";

  /** The separator between parameters. */
  static final String PARAMETER_SEPARATOR = "%";

  /** The selection tree. */
  private final SelectionTree selectionTree;

  /** The destination. */
  private final Map<String, Object> destination;

  /** The child-to-parent map. */
  private final Map<SelectedFactoryNode, SelectedFactoryNode> childToParentMap;

  /**
   * Instantiates a new configuration flattener.
   * 
   * @param selectionTree
   *          the selection tree
   * @param destination
   *          the destination
   */
  public SelectionTreeFlattener(SelectionTree selectionTree,
      Map<String, Object> destination) {
    this.selectionTree = selectionTree;
    this.destination = destination;
    this.childToParentMap = selectionTree.getChildToParentMap();
  }

  /**
   * Flattens the selection tree.
   */
  public void flatten() {
    BreadthFirstSearch<SelectedFactoryNode> bfs =
        new BreadthFirstSearch<>();
    destination.clear();
    bfs.traverse(selectionTree, this);
  }

  @Override
  public boolean process(SelectedFactoryNode node) {
    if (!node.hasSelectionInformation()) {
      return true;
    }
    String path = addFactoryToConfig(node);
    List<Pair<String, Object>> params =
        extractAlgorithmParameters(node.getFactoryClass(), node.getParameter());
    addParametersToConfig(path, params);
    return true;
  }

  /**
   * Adds the parameter assignments to the configuration map.
   * 
   * @param path
   *          the path to the current node
   * @param params
   *          the parameters
   */
  void addParametersToConfig(String path, List<Pair<String, Object>> params) {
    for (Pair<String, Object> param : params) {
      this.destination.put(path + PARAMETER_SEPARATOR + param.getFirstValue(),
          param.getSecondValue());
    }
  }

  /**
   * Adds the factory name to the configuration.
   * 
   * @param node
   *          the node denoting the selection of the factory
   * @param selectionInfo
   *          the selection information
   * 
   * @return the path of the node
   */
  String addFactoryToConfig(SelectedFactoryNode node) {
    String path =
        getPath(node) + getPathSeparator() + node.getUniqueSubBlockName();
    this.destination.put(path, node.getFactoryClass().getName());
    return path;
  }

  /**
   * Gets the path separator.
   * 
   * @return the path separator
   */
  String getPathSeparator() {
    return "" + SEPARATOR + SEPARATOR;
  }

  /**
   * Gets the path from the root to the given node. It is of form PATH_SEPARATOR
   * + [Unique SelFacNode sub block name] + PATH_SEPARATOR + ... + [Unique
   * SelFacNode sub block name], with the last factory node being the parent of
   * this one.
   * 
   * @param node
   *          the factory node
   * 
   * @return the path
   */
  String getPath(SelectedFactoryNode node) {
    StringBuffer result = new StringBuffer();
    List<SelectedFactoryNode> nodesFromRoot =
        BasicTree.getVertexSequenceFromRoot(childToParentMap, node);
    for (SelectedFactoryNode nodeInSquence : nodesFromRoot) {
      result.append(getPathSeparator());
      result.append(nodeInSquence.getUniqueSubBlockName());
    }
    return result.toString();
  }

  /**
   * Extract parameters for factory. These have to be defined in the plug-in
   * description, otherwise they will be ignored.
   * 
   * @param factory
   *          the factory initialised with this parameter block
   * @param parameterBlock
   *          the parameter block
   * 
   * @return the list of parameter assignments
   */
  List<Pair<String, Object>> extractAlgorithmParameters(
      Class<? extends Factory<?>> factory, ParameterBlock parameterBlock) {
    List<Pair<String, Object>> parameterAssignments =
        new ArrayList<>();
    IFactoryInfo facInfo =
        SimSystem.getRegistry().getFactoryInfo(factory.getName());

    if (facInfo == null) {
      return parameterAssignments;
    }

    List<IParameter> parameters = facInfo.getParameters();
    for (IParameter parameter : parameters) {
      if (parameterBlock.hasSubBlock(parameter.getName())) {
        // TODO: One could also implement type-checking here... but maybe that's
        // too strict
        Object o = parameterBlock.getSubBlockValue(parameter.getName());
        parameterAssignments.add(new Pair<>(parameter.getName(),
            o));
      }
    }
    if (parameters.size() != parameterBlock.getSubBlocks().size()) {
      SimSystem.report(Level.WARNING, "Factory '" + factory.getName()
          + "' declares " + parameters.size() + " parameters, but there are "
          + parameterBlock.getSubBlocks().size() + " parameter sub-blocks.");
    }
    return parameterAssignments;
  }

}
