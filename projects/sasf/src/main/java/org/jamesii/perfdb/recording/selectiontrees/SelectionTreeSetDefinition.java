/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.trees.Tree;

/**
 * Definition for a set of selection trees. It has a tree form and customised
 * vertices.
 * 
 * @author Roland Ewald
 */
public class SelectionTreeSetDefinition extends
    Tree<SelTreeSetVertex, Edge<SelTreeSetVertex>> {
  static {
    SerialisationUtils.addDelegateForConstructor(
        SelectionTreeSetDefinition.class,
        new IConstructorParameterProvider<SelectionTreeSetDefinition>() {
          @Override
          public Object[] getParameters(SelectionTreeSetDefinition tree) {
            Object[] params =
                new Object[] { tree.getVertices(), tree.getVEMap() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -6673842165486825789L;

  /**
   * Instantiates a new selection tree set definition.
   */
  public SelectionTreeSetDefinition() {
    super(new ArrayList<SelTreeSetVertex>());
    SelTreeSetVertex root = new SelTreeSetVertex(0);
    addVertex(root);
    setTreeRoot(root);
  }

  public SelectionTreeSetDefinition(List<SelTreeSetVertex> verts,
      Map<SelTreeSetVertex, Edge<SelTreeSetVertex>> edgeMap) {
    super(verts, edgeMap);
  }
}
