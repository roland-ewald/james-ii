/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.ca.partitioning;

import java.lang.reflect.Array;

import org.jamesii.core.distributed.partitioner.modelanalyzer.AbstractModelAnalyzer;
import org.jamesii.core.model.IModel;
import org.jamesii.core.util.graph.ISimpleGraph;
import org.jamesii.core.util.graph.SimpleGraph;
import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.ICAGrid;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleCAAnalyzer.
 */
public class SimpleCAAnalyzer extends AbstractModelAnalyzer {

  /** The model graph. */
  private SimpleGraph modelGraph;

  /**
   * Instantiates a new simple ca analyzer.
   */
  public SimpleCAAnalyzer() {
    super();
  }

  @Override
  public ISimpleGraph analyzeModel(IModel model) {

    if (!(model instanceof ICAGrid)) {
      modelGraph = new SimpleGraph(1);
      return modelGraph;
    }

    ICAGrid caModel = (ICAGrid) model;

    int vertexCount = countNodes(caModel);

    modelGraph = new SimpleGraph(vertexCount);

    createEdges(caModel);

    // Setting calculation costs equally
    for (int i = 0; i < vertexCount; i++) {
      modelGraph.setLabel(i, 1.0);
    }

    return modelGraph;

  }

  /**
   * Retrieves information about.
   * 
   * @param model
   *          the model
   * 
   * @return the int
   */
  protected int countNodes(ICAGrid model) {
    int nodes = 1;

    int[] dimens = model.getDimensions();

    for (int dimen : dimens) {
      nodes *= dimen;
    }

    return nodes + 1;
  }

  /**
   * Creates the labeled tree (recursively).
   * 
   * @param model
   *          the model
   */
  protected void createEdges(ICAGrid model) {

    modelGraph.setObjectByVertex(0, model);

    createEdgeStep(model.getGrid(), 1);

  }

  /**
   * Generic doStep method, will work on any grid (nD)!!!! The passed onObject
   * must either be an instance of the Cell class or a part of an array.
   * 
   * @param onObject
   *          the on object
   * @param vertex
   *          the vertex
   */
  private void createEdgeStep(Object onObject, int vertex) {
    if (onObject instanceof Cell) {
      // if the object is a cell we'll simply have to compute the next
      // state
      // of it

      // TODO (re027) what do we have to do per cell????? addVertex ...
      modelGraph.setObjectByVertex(++vertex, onObject);
    } else {
      // if the object is not a cell it is an array
      for (int i = 0; i < Array.getLength(onObject); i++) {
        // here we "slice" the actual array into pieces and call the
        // doStep method for each array - piece
        // which may be either another array or an instance of a Cell
        // descendant class
        createEdgeStep(Array.get(onObject, i), vertex);
      }
    }
  }

}
