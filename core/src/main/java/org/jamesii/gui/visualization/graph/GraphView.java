/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.graph;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.jamesii.gui.visualization.graph.layout.ILayoutManager;
import org.jamesii.gui.visualization.graph.mapper.IRendererMapper;

/**
 * The Class GraphView.
 * 
 * @author Jan Himmelspach
 */
public class GraphView extends JComponent {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -775938249168925254L;

  /** The model. */
  private IGraphModel model;

  /**
   * The renderer mapper - which renderer to be used for which element in the
   * graph
   */
  private IRendererMapper mapper;

  /** The layout to be applied to nodes and edges */
  private ILayoutManager graphLayout;

  @Override
  protected void paintComponent(Graphics g) {

  }

  /**
   * @return the model
   */
  public IGraphModel getModel() {
    return model;
  }

  /**
   * @param model
   *          the model to set
   */
  public void setModel(IGraphModel model) {
    this.model = model;
  }

  /**
   * @return the mapper
   */
  public IRendererMapper getMapper() {
    return mapper;
  }

  /**
   * @param mapper
   *          the mapper to set
   */
  public void setMapper(IRendererMapper mapper) {
    this.mapper = mapper;
  }

  /**
   * @return the graphLayout
   */
  public ILayoutManager getGraphLayout() {
    return graphLayout;
  }

  /**
   * @param graphLayout
   *          the graphLayout to set
   */
  public void setGraphLayout(ILayoutManager graphLayout) {
    this.graphLayout = graphLayout;
  }

}
