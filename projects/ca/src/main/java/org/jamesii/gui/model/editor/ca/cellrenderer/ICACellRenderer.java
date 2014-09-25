/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.cellrenderer;

import java.util.List;

import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ICAValueMapper;
import org.jamesii.gui.visualization.grid.IGridCellRenderer;

/**
 * Interface for cell renderers for arbitrary (1D or 2D, at least) cellular
 * automata. Every CA cell renderer can have several different inputs, each of
 * which has a connected value mapper that converts a specific state to a type
 * used by the input, i. e. colors, images and the like.
 * 
 * @author Johannes Rössel
 */
public interface ICACellRenderer extends IGridCellRenderer {

  /**
   * Attaches a ValueMapper to a given input.
   * 
   * @param inputIndex
   *          The input index to attach the ValueMapper to.
   * @param m
   *          The ValueMapper to attach.
   */
  void setMapper(int inputIndex, ICAValueMapper m);

  /**
   * Retrieves a list of ValueMappers used in this CA cell renderer.
   * 
   * @return A list of all ValueMappers attached to inputs of this renderer.
   */
  List<ICAValueMapper> getMappers();

  /**
   * Retrieves a list of available inputs.
   * 
   * @return The list of inputs available in this CA cell renderer.
   */
  List<Class<?>> getInputs();

  /**
   * Returns a friendly name for a given input that describes the purpose of the
   * input. Such as “color”, “background color”, “image” or similar.
   * 
   * @param inputIndex
   *          The index of the input.
   * @return The input's name, for display purposes.
   */
  String getInputName(int inputIndex);
}
