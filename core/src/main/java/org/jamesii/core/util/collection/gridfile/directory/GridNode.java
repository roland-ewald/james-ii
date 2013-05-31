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

public class GridNode<T> extends GridComponent<T> {

  private List<GridComponent<T>> children;

  public GridNode() {
    super();
    this.children = new ArrayList<>();
  }

  @Override
  public GridComponent<T> copy() {
    List<GridComponent<T>> newChildren = new ArrayList<>();
    for (GridComponent<T> c : children) {
      newChildren.add(c.copy());
    }
    GridComponent<T> result = new GridNode<>(newChildren);
    return result;
  }

  @Override
  public List<Map<T, T>> take() {
    List<Map<T, T>> result = new ArrayList<>();
    for (GridComponent<T> c : children) {
      result.addAll(c.take());
    }
    return result;
  }

  @Override
  public GridLeaf<T> getLeaf(int[] coords) {
    return (GridLeaf<T>) getComponent(coords, coords.length);
  }

  @Override
  public GridComponent<T> getComponent(int[] coords, int level) {
    if (level == 0) {
      return this;
    }
    return children.get(coords[coords.length - level]).getComponent(coords,
        level - 1);
  }

  @Override
  public Map<T, T> partialMatch(int[] coords, int depth) {
    if (coords[depth] >= 0) {
      return children.get(coords[depth]).partialMatch(coords, depth + 1);
    }
    Map<T, T> result = new HashMap<>();
    for (GridComponent<T> child : children) {
      result.putAll(child.partialMatch(coords, depth + 1));
    }
    return result;
  }

  public GridNode(List<GridComponent<T>> children) {
    super();
    this.children = children;
  }

  public List<GridComponent<T>> getChildren() {
    return children;
  }

  public void setChildren(List<GridComponent<T>> children) {
    this.children = children;
  }

  @Override
  public void clear() {
    for (GridComponent<T> e : children) {
      e.clear();
    }
  }

  @Override
  public List<Map<T, T>> getLists() {
    List<Map<T, T>> result = new ArrayList<>();
    for (GridComponent<T> e : children) {
      result.addAll(e.getLists());
    }
    return result;
  }

}
