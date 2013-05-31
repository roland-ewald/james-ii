/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.gridfile.directory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridLeaf<T> extends GridComponent<T> {

  /**
   * List of elements.
   */
  private Map<T, T> elements;

  /**
   * List of coordinates.
   */
  private Map<String, Integer> dataStrings;

  public GridLeaf() {
    super();
    this.elements = new HashMap<>();
    this.dataStrings = new HashMap<>();
  }

  public GridLeaf(Map<T, T> elements) {
    super();
    this.elements = elements;
    this.dataStrings = new HashMap<>();
  }

  @Override
  public GridComponent<T> copy() {
    Map<T, T> newElements = new HashMap<>();
    return new GridLeaf<>(newElements);
  }

  @Override
  public List<Map<T, T>> take() {
    List<Map<T, T>> result = new ArrayList<>();
    Map<T, T> resultMap = new HashMap<>();
    resultMap.putAll(elements);
    result.add(resultMap);
    elements.clear();
    dataStrings.clear();
    return result;
  }

  @Override
  public GridLeaf<T> getLeaf(int[] coords) {
    return this;
  }

  @Override
  public GridComponent<T> getComponent(int[] coords, int level) {
    return this;
  }

  @Override
  public Map<T, T> partialMatch(int[] coords, int depth) {
    return elements;
  }

  /**
   * Not needed? See getAll()
   * 
   * @return
   */
  public Map<T, T> getElements() {
    return elements;
  }

  public void setElements(Map<T, T> elements) {
    this.elements = elements;
  }

  public Map<String, Integer> getDataStrings() {
    return dataStrings;
  }

  public void setDataStrings(Map<String, Integer> coordinates) {
    this.dataStrings = coordinates;
  }

  @Override
  public void clear() {
    elements.clear();
  }

  @Override
  public List<Map<T, T>> getLists() {
    List<Map<T, T>> result = new ArrayList<>();
    result.add(elements);
    return result;
  }
}
