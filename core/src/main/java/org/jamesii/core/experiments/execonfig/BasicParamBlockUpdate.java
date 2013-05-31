/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * Basic class to easily implement different ways of updating a nested
 * {@link ParameterBlock}.
 * 
 * @author Roland Ewald
 */
public abstract class BasicParamBlockUpdate implements IParamBlockUpdate {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6295990017901829321L;

  /**
   * Signalises start of a new update.
   * 
   * @param root
   *          the root of the parameter tree
   */
  abstract void init(ParameterBlock root);

  /**
   * True if current path is matching the rule criteria.
   * 
   * @param path
   *          the current path
   * 
   * @return true, if {@link BasicParamBlockUpdate#modify(List)} shall be called
   */
  abstract boolean match(List<Pair<String, ParameterBlock>> path);

  /**
   * Modifies {@link ParameterBlock}.
   * 
   * @param path
   *          the path to the current element
   */
  abstract void modify(List<Pair<String, ParameterBlock>> path);

  /**
   * Signalises that the update is finished.
   * 
   * @return true if updating and traversing the tree should be stopped
   */
  abstract boolean stop();

  @Override
  public void update(ParameterBlock root) {
    List<Pair<String, ParameterBlock>> currentPath = new ArrayList<>();
    currentPath.add(new Pair<>("", root));
    init(root);
    traverseParamTree(currentPath);
  }

  /**
   * Traverses tree.
   * 
   * @param path
   *          the path to the current element in the parameter tree
   */
  protected void traverseParamTree(List<Pair<String, ParameterBlock>> path) {
    if (match(path)) {
      modify(path);
    }
    if (stop()) {
      return;
    }
    Pair<String, ParameterBlock> currentNode = getCurrentNode(path);
    for (Entry<String, ParameterBlock> child : currentNode.getSecondValue()
        .getSubBlocks().entrySet()) {
      List<Pair<String, ParameterBlock>> newPath = new ArrayList<>(path);
      newPath.add(new Pair<>(child.getKey(), child.getValue()));
      traverseParamTree(newPath);
      if (stop()) {
        return;
      }
    }
  }

  /**
   * Retrieves current node from the path. This method assumes that the list
   * which is handed over is never empty, i.e. at least the root parameter block
   * has to be in there.
   * 
   * @param path
   *          the current path
   * 
   * @return the last node in the path
   */
  static Pair<String, ParameterBlock> getCurrentNode(
      List<Pair<String, ParameterBlock>> path) {
    return path.get(path.size() - 1);
  }

  /**
   * Merges or overrides all content in destination with content from source.
   * 
   * @param source
   *          the source parameter block
   * @param dest
   *          the destination parameter block
   */
  static void merge(ParameterBlock source, ParameterBlock dest) {
    dest.setValue(source.getValue());
    for (Entry<String, ParameterBlock> block : source.getSubBlocks().entrySet()) {
      dest.addSubBlock(block.getKey(), block.getValue());
    }
  }

}
