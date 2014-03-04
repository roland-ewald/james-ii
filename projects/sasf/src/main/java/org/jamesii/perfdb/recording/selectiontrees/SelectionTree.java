/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.trees.Tree;
import org.jamesii.perfdb.util.ParameterBlocks;

/**
 * Subclass for easier handling. Represents a selection tree, i.e. a tree that
 * specifies the selected structure of factories.
 * 
 * @author Roland Ewald
 * 
 */
public class SelectionTree extends
    Tree<SelectedFactoryNode, Edge<SelectedFactoryNode>> {
  static {
    SerialisationUtils.addDelegateForConstructor(SelectionTree.class,
        new IConstructorParameterProvider<SelectionTree>() {
          @Override
          public Object[] getParameters(SelectionTree tree) {
            Object[] params =
                new Object[] { tree.getVertices(), tree.getVEMap() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 2155327870732069316L;

  /**
   * Default constructor.
   */
  public SelectionTree() {
    super(new ArrayList<SelectedFactoryNode>());
  }

  /**
   * Instantiates a new selection tree.
   * 
   * @param info
   *          the selection information for the root, may be null
   */
  public SelectionTree(SelectionInformation<?> info) {
    this();
    SelectedFactoryNode selFactoryNode = new SelectedFactoryNode(info);
    this.addVertex(selFactoryNode);
    this.setTreeRoot(selFactoryNode);
  }

  /**
   * Instantiates a new selection tree.
   * 
   * @param verts
   *          the vertices
   * @param edgeMap
   *          the edge map
   */
  public SelectionTree(List<SelectedFactoryNode> verts,
      Map<SelectedFactoryNode, Edge<SelectedFactoryNode>> edgeMap) {
    super(verts, edgeMap);
  }

  /**
   * Compresses the structure of the tree into an easily comparable hash
   * function. In contrast to {@link Object#hashCode()}, this number has to be
   * the same for two selection trees representing the same selection.
   * 
   * @return the tree's hash number //TODO: Merge with hash code...
   */
  public long getHash() {
    return ParameterBlocks.toUniqueString(toParamBlock()).hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SelectionTree)) {
      return false;
    }
    return ParameterBlocks.toUniqueString(toParamBlock()).equals(
        ParameterBlocks.toUniqueString(((SelectionTree) o).toParamBlock()));
  }

  @Override
  public int hashCode() {
    return ParameterBlocks.toUniqueString(toParamBlock()).hashCode();
  }

  /**
   * Converts the selection tree to a parameter block that results in the same
   * selection tree.
   * 
   * @return the parameter block
   */
  public ParameterBlock toParamBlock() {
    ParameterBlock block = new ParameterBlock();
    toParamBlock(block, getRoot());
    return block;
  }

  /**
   * Recursive method to convert a selection (sub) tree to a parameter block.
   * 
   * @param block
   *          the parameter block
   * @param root
   *          the root of the sub tree
   */
  protected void toParamBlock(ParameterBlock block, SelectedFactoryNode root) {

    SelectionInformation<?> selInfo = root.getSelectionInformation();
    ParameterBlock parentBlock = block;
    // Copy selection information if available
    if (selInfo != null) {
      ParameterBlock subBlock = selInfo.getParameter();
      if (subBlock == null) {
        subBlock = new ParameterBlock();
      }
      subBlock.setValue(selInfo.getFactoryClass().getName());
      block.addSubBlock(SimSystem.getRegistry()
          .getBaseFactoryForAbstractFactory(selInfo.getAbstractFactory())
          .getName(), subBlock);
      parentBlock = subBlock;
    }

    // Recursion to child nodes
    for (SelectedFactoryNode child : getChildren(root)) {
      toParamBlock(parentBlock, child);
    }
  }

  /**
   * Gets the set of unique factories from this tree.
   * 
   * @return the unique factories
   */
  public List<Class<? extends Factory<?>>> getUniqueFactories() {
    List<Class<? extends Factory<?>>> factories =
        new ArrayList<>();
    for (SelectedFactoryNode node : getVertices()) {
      if (node.getSelectionInformation() != null) {
        factories.add(node.getSelectionInformation().getFactoryClass());
      }
    }
    return factories;
  }

  /**
   * Gets a condensed representation of the selection tree. Note: parameters and
   * factory names that are not abbreviated are deemed unimportant (and are
   * hence ignored!).
   * 
   * @param abbreviations
   *          a map that defines abbreviations (i.e., automatic replacements):
   *          OLD => NEW
   * @return a condensed representation
   */
  public String getCondensedRepresentation(Map<String, String> abbreviations) {
    return getCondensedRepresentation(getRoot(), abbreviations);
  }

  /**
   * Gets the condensed representation.
   * 
   * @param subTreeRoot
   *          the root of the current sub-tree
   * @param abbreviations
   *          the abbreviations
   * @return the condensed representation
   */
  private String getCondensedRepresentation(SelectedFactoryNode subTreeRoot,
      Map<String, String> abbreviations) {

    SelectionInformation<?> selectionInformation =
        subTreeRoot.getSelectionInformation();
    String factoryClassName =
        selectionInformation == null ? "" : selectionInformation
            .getFactoryClass().getCanonicalName();
    String ownName = "";
    String paramRepresentation = "";

    if (subTreeRoot.hasSelectionInformation()
        && abbreviations.containsKey(factoryClassName)) {
      ownName = abbreviations.get(factoryClassName);
      paramRepresentation =
          getCondensedParameterRepresentation(
              selectionInformation.getParameter(), abbreviations);
    }

    List<String> childRepresentations =
        getCondensedChildRepresentations(subTreeRoot, abbreviations);

    return mergeCondensedRepresentation(ownName, paramRepresentation,
        childRepresentations);
  }

  /**
   * Creates the condensed representation.
   * 
   * @param ownName
   *          the own name of the factory class
   * @param paramRepresentation
   *          the parameter representation of the factory
   * @param childRepresentations
   *          the representations of the childs
   * @return the condensed representation
   */
  public String mergeCondensedRepresentation(String ownName,
      String paramRepresentation, List<String> childRepresentations) {
    if (ownName.isEmpty() && childRepresentations.isEmpty()) {
      return "";
    }

    if (ownName.isEmpty() && paramRepresentation.isEmpty()
        && childRepresentations.size() == 1) {
      return childRepresentations.get(0);
    }

    return mergeNonemptyCondensedRepresentation(ownName, paramRepresentation,
        childRepresentations);
  }

  /**
   * Creates the condensed representation in case the own name is non-empty.
   * 
   * @param ownName
   *          the own name
   * @param paramRepresentation
   *          the parameter representation
   * @param childRepresentations
   *          the child representations
   * @return the string
   */
  private String mergeNonemptyCondensedRepresentation(String ownName,
      String paramRepresentation, List<String> childRepresentations) {
    StringBuffer representation = new StringBuffer("[");
    representation.append(ownName);
    representation.append(paramRepresentation);
    if (!paramRepresentation.isEmpty() && !childRepresentations.isEmpty()) {
      representation.append(' ');
    }
    for (String childRepresentation : childRepresentations) {
      representation.append(childRepresentation);
    }
    representation.append(']');

    return representation.toString();
  }

  /**
   * Gets the (alphabetically sorted) condensed representations of a node's
   * child nodes.
   * 
   * @param node
   *          the node
   * @param abbreviations
   *          the abbreviations
   * @return the condensed child representations
   */
  public List<String> getCondensedChildRepresentations(
      SelectedFactoryNode node, Map<String, String> abbreviations) {
    List<String> childRepresentations = new ArrayList<>();
    for (SelectedFactoryNode child : getChildren(node)) {
      if (child.hasSelectionInformation()) {
        String childRepresentation =
            getCondensedRepresentation(child, abbreviations);
        if (!childRepresentation.isEmpty()) {
          childRepresentations.add(childRepresentation);
        }
      }
    }
    Collections.sort(childRepresentations);
    return childRepresentations;
  }

  /**
   * Gets the condensed parameter representation.
   * 
   * @param parameterBlock
   *          the parameter block
   * @param abbreviations
   *          the abbreviations
   * @return the condensed parameter representation
   */
  private String getCondensedParameterRepresentation(
      ParameterBlock parameterBlock, Map<String, String> abbreviations) {
    StringBuffer representation = new StringBuffer("(");
    boolean firstParam = true;
    for (Entry<String, ParameterBlock> subBlockEntry : parameterBlock
        .getSubBlocks().entrySet()) {
      if (abbreviations.containsKey(subBlockEntry.getKey())) {
        if (firstParam) {
          firstParam = false;
        } else {
          representation.append(' ');
        }

        representation.append(abbreviations.get(subBlockEntry.getKey()));
        representation.append('=');
        representation.append(subBlockEntry.getValue().getValue());
      }
    }
    representation.append(")");
    return representation.toString();
  }
}