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

/**
 * 
 * Until now, the first and last
 * 
 * @author Tobias Helms
 * 
 * @param <T>
 */
public class GridDirectory<T> {

  private GridComponent<T> rootNode;

  /**
   * At the edges of the root directory the grid directories have an unlimited
   * size.
   **/
  private boolean unlimitedSize;

  /** Maximum number of elements in one bucket. **/
  private int maxBucketSize;

  /** Minimum load of a bucket. **/
  private double minBucketDensity;

  /** Maximum load of a bucket. **/
  private double maxBucketDensity;

  /** Number of elements. **/
  public int numOfElements;

  /** All element lists of the leaves. **/
  private List<Map<T, T>> leafLists;

  /**
   * List of the scales of the boundaries of the root directory (|scales| =
   * #dimensions). Until now they are constant.
   **/
  private List<List<Double>> gridScales;

  /**
   * List of all scale values of the elements
   */
  private List<List<List<Double>>> scaleElements;

  protected String data2String(double[] data) {
    StringBuilder result = new StringBuilder();
    for (double x : data) {
      Double y = x;
      result = result.append(String.valueOf(y));
    }
    return result.toString();
  }

  /**
   * Get the index of the first element which is greater than the given one.
   * 
   * @param x
   * @return the index, -1 if x is not inside the scale
   */
  public int positionIndex(double x, List<Double> scale) {
    if (scale.size() == 0 || x < scale.get(0)
        || x > scale.get(scale.size() - 1)) {
      return -1;
    }

    int result = 0;
    for (double boundary : scale) {
      if (Double.compare(x, boundary) < 0) {
        break;
      }
      ++result;
    }

    // the last scale is inclusive
    if (result == scale.size()) {
      return -1;
    }

    return result - 1;
  }

  /**
   * Clear all elements but keep the file structure.
   */
  public void clear() {
    for (Map<T, T> e : leafLists) {
      e.clear();
    }
    numOfElements = 0;
  }

  /**
   * Return all elements.
   */
  public List<Map<T, T>> getAll() {
    return rootNode.getLists();
  }

  /**
   * TODO: th162 replace this method with a more elegant implementation with the
   * node and leaf classes!
   * 
   * @param node
   * @param index
   * @param level
   * @return
   */
  protected List<Map<T, T>> splitBuckets(GridNode<T> node, int index, int level) {
    if (level == 0) {
      GridComponent<T> componentToSplit = node.getChildren().get(index);

      GridComponent<T> newComponent = componentToSplit.copy();
      node.getChildren().add(index, newComponent);
      leafLists.addAll(newComponent.getLists());

      return componentToSplit.take();
    }
    List<Map<T, T>> elements = new ArrayList<>();
    for (GridComponent<T> c : node.getChildren()) {
      elements.addAll(splitBuckets((GridNode<T>) c, index, level - 1));
    }
    return elements;
  }

  /**
   * TODO: th162 replace this method with a more elegant implementation with the
   * node and leaf classes!
   * 
   * @param dimension
   * @param scales
   * @return
   */
  protected GridComponent<T> createList(int dimension, List<List<Double>> scales) {
    if (dimension == 0) {
      // add a leaf at the current position
      GridLeaf<T> result = new GridLeaf<>();
      leafLists.add(result.getElements());
      return result;
    }

    GridNode<T> result = new GridNode<>();
    List<GridComponent<T>> list = new ArrayList<>();
    for (int i = 0; i < scales.get(scales.size() - dimension).size() - 1; ++i) {
      list.add(createList(dimension - 1, scales));
    }
    result.setChildren(list);
    return result;
  }

  public GridDirectory(List<List<Double>> scales, int maxBucketSize,
      double minBucketDensity, double maxBucketDensity, boolean unlimitedSize) {

    this.setMaxBucketSize(maxBucketSize);
    this.setMinBucketDensity(minBucketDensity);
    this.setMaxBucketDensity(maxBucketDensity);
    this.unlimitedSize = unlimitedSize;
    this.gridScales = scales;
    this.leafLists = new ArrayList<>();
    this.scaleElements = new ArrayList<>();

    // Add lists of lists for each scaleElement
    if (scales != null) {
      for (List<Double> s : scales) {
        List<List<Double>> newScaleElements = new ArrayList<>();
        for (@SuppressWarnings("unused")
        Double v : s) {
          List<Double> elements = new ArrayList<>();
          newScaleElements.add(elements);
        }
        this.scaleElements.add(newScaleElements);
      }
    }

    rootNode = createList(scales == null ? 0 : scales.size(), scales);
  }

  public int[] coordinates(double[] data) {
    int[] coords = new int[gridScales.size()];
    for (int i = 0; i < gridScales.size(); ++i) {
      coords[i] = positionIndex(data[i], gridScales.get(i));
      if (coords[i] == -1) {
        return null;
      }
    }
    return coords;
  }

  public int[] coordinates(double[] data, List<Integer> ignoredIndexes) {
    int[] coords = new int[gridScales.size()];
    for (int i = 0; i < gridScales.size(); ++i) {
      coords[i] = positionIndex(data[i], gridScales.get(i));
      if (coords[i] == -1 && !ignoredIndexes.contains(i)) {
        return null;
      }
    }

    for (int tmp : ignoredIndexes) {
      coords[tmp] = -1;
    }
    return coords;
  }

  public GridLeaf<T> getLeaf(double[] data) {
    if (gridScales == null) {
      return (GridLeaf<T>) rootNode;
    }

    return getLeaf(coordinates(data));
  }

  public GridLeaf<T> getLeaf(int[] coords) {
    return rootNode.getLeaf(coords);
  }

  public Map<T, T> partialMatch(int[] coords) {
    if (numOfElements == 0) {
      return new HashMap<>();
    }

    if (gridScales == null) {
      return ((GridLeaf<T>) rootNode).getElements();
    }

    return rootNode.partialMatch(coords, 0);
  }

  public List<Map<T, T>> addElement(T element, double[] data) {
    ++numOfElements;
    if (gridScales == null) {
      ((GridLeaf<T>) rootNode).getElements().put(element, element);
      String dataString = data2String(data);
      Integer number =
          ((GridLeaf<T>) rootNode).getDataStrings().get(dataString);
      if (number == null) {
        ((GridLeaf<T>) rootNode).getDataStrings().put(dataString, 1);
      } else {
        ((GridLeaf<T>) rootNode).getDataStrings().put(dataString, number + 1);
      }
      return new ArrayList<>();
    }

    int[] coords = coordinates(data);

    // add the data to the scaleElements (do not double them of course)
    for (int i = 0; i < data.length; ++i) {
      List<Double> elementList = scaleElements.get(i).get(coords[i]);
      if (!elementList.contains(data[i])) {
        elementList.add(data[i]);
      }
    }

    GridLeaf<T> leaf = getLeaf(coords);
    leaf.getElements().put(element, element);

    String dataString = data2String(data);
    Integer number = leaf.getDataStrings().get(dataString);
    if (number == null) {
      leaf.getDataStrings().put(dataString, 1);
    } else {
      leaf.getDataStrings().put(dataString, number + 1);
    }

    // check if one bucket if full
    if (leaf.getDataStrings().size() > getMaxBucketSize()) {
      // System.out.println("FULL!");
      // TODO: add a more intelligent strategy to find new boundaries
      int dim = 0;
      int maximum = scaleElements.get(0).get(coords[0]).size();
      // take the dimension with the smallest number of elements between the
      // boundaries
      for (int i = 1; i < gridScales.size(); ++i) {
        if (scaleElements.get(i).get(coords[i]).size() > maximum) {
          maximum = scaleElements.get(i).get(coords[i]).size();
          dim = i;
        }
      }

      // add a new boundary
      double newBound =
          (gridScales.get(dim).get(coords[dim]) + gridScales.get(dim).get(
              coords[dim] + 1)) / 2;
      gridScales.get(dim).add(coords[dim] + 1, newBound);
      scaleElements.get(dim).get(coords[dim]).clear();
      scaleElements.get(dim).add(coords[dim], new ArrayList<Double>());

      List<Map<T, T>> result =
          splitBuckets((GridNode<T>) rootNode, coords[dim], dim);
      for (Map<T, T> e : result) {
        numOfElements -= e.size();
      }

      return result;
    }
    return new ArrayList<>();
  }

  public boolean removeElement(Object element, double[] data) {
    GridLeaf<T> leaf;

    if (gridScales == null || gridScales.size() == 0) {
      leaf = (GridLeaf<T>) rootNode;
    } else {
      int[] coords = coordinates(data);
      leaf = rootNode.getLeaf(coords);
    }

    Map<T, T> elements = leaf.getElements();
    if (elements.containsKey(element)) {
      --numOfElements;
      elements.remove(element);
      String dataString = data2String(data);
      Integer number = leaf.getDataStrings().get(dataString);
      if (number > 1) {
        leaf.getDataStrings().put(dataString, number - 1);
      }
      return true;
    }
    return false;
  }

  public final List<List<Double>> getGridScales() {
    return gridScales;
  }

  public GridComponent<T> getRootNode() {
    return rootNode;
  }

  public final int getNumOfElements() {
    return numOfElements;
  }

  /**
   * @return the minBucketDensity
   */
  protected final double getMinBucketDensity() {
    return minBucketDensity;
  }

  /**
   * @param minBucketDensity
   *          the minBucketDensity to set
   */
  protected final void setMinBucketDensity(double minBucketDensity) {
    this.minBucketDensity = minBucketDensity;
  }

  /**
   * @return the maxBucketSize
   */
  protected final int getMaxBucketSize() {
    return maxBucketSize;
  }

  /**
   * @param maxBucketSize
   *          the maxBucketSize to set
   */
  protected final void setMaxBucketSize(int maxBucketSize) {
    this.maxBucketSize = maxBucketSize;
  }

  /**
   * @return the maxBucketDensity
   */
  protected final double getMaxBucketDensity() {
    return maxBucketDensity;
  }

  /**
   * @param maxBucketDensity
   *          the maxBucketDensity to set
   */
  protected final void setMaxBucketDensity(double maxBucketDensity) {
    this.maxBucketDensity = maxBucketDensity;
  }

  /**
   * @param rootNode
   *          the rootNode to set
   */
  protected final void setRootNode(GridComponent<T> rootNode) {
    this.rootNode = rootNode;
  }

}
