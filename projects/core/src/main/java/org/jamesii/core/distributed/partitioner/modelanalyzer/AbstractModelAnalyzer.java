/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.modelanalyzer;

import org.jamesii.core.model.IModel;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * 
 * 
 * Abstract superclass for all model analyzation algorithms.
 * 
 * The {@link #analyzeModel(IModel)} method will be called during the
 * partitioning to retrieve the model graph in a standardized form: each part of
 * a model which can be computed using a different resource has to be
 * represented as vertex, each potential communication between those parts has
 * to be given as an edge. Vertex and edge labels denote the load expected for
 * the vertex / communication. If these are not set they might be null which
 * might break algorithms or which might lead to unwanted behaviour (e.g., all
 * vertices might be placed on a single host as all have no computational load).
 * Partitioning algorithms will use the labels to decide on which computational
 * resource which part will be placed.
 * 
 * Created on Nov 16, 2004
 * 
 * @author Roland Ewald
 */
public abstract class AbstractModelAnalyzer {

  /**
   * Analyzes a given model. The graph returned has to
   * <ul>
   * <li>contain a vertex per model part</li>
   * <li>edges between the model parts denoting potential communication</li>
   * <li>vertex labels (floating point number) representing the computational
   * load per vertex</li>
   * <li>edge label (floating point number) representing the communication
   * intensity per edge</li>
   * </ul>
   * 
   * Vertex and edge labels should only rarely be zero.
   * 
   * @param model
   * @return graph representation of the model
   */
  public abstract ISimpleGraph analyzeModel(IModel model);

}
