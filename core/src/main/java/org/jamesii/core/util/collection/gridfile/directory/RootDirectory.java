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
import java.util.Map.Entry;

/**
 * The root directory of the grid file. Until now it's constant. There are no
 * merge and join methods. There is only one grid directory for the space
 * outside the scales!
 * 
 * @author Tobias Helms
 * 
 * @param <T>
 */
public class RootDirectory<T> extends GridDirectory<GridDirectory<T>> {

  private GridDirectory<T> unlimitedDirectory;

  /**
   * An array is sufficient, because the number of dimensions is fixed.
   */
  private double[] minBound;

  /**
   * See minBound.
   */
  private double[] maxBound;

  public RootDirectory(List<List<Double>> scales, int maxBucketSize,
      double minBucketDensity, double maxBucketDensity) {
    super(scales, maxBucketSize, minBucketDensity, maxBucketDensity, true);

    numOfElements++;
    unlimitedDirectory =
        new GridDirectory<>(null, maxBucketSize, minBucketDensity,
            maxBucketDensity, true);
    minBound = new double[scales.size()];
    maxBound = new double[scales.size()];
  }

  @Override
  protected GridComponent<GridDirectory<T>> createList(int dimension,
      List<List<Double>> scales) {
    return createList(scales == null ? 0 : scales.size(), scales, null, null);
  }

  /**
   * TODO th162 Don't save the grid directories at the last level in lists,
   * because there is at most one directory!
   * 
   * @param dimension
   * @param scales
   * @param minBoundary
   * @param maxBoundary
   * @return
   */
  protected GridComponent<GridDirectory<T>> createList(int dimension,
      List<List<Double>> scales, List<Double> minBoundary,
      List<Double> maxBoundary) {
    // int x = 0;
    if (dimension == scales.size()) {
      minBoundary = new ArrayList<>();
      maxBoundary = new ArrayList<>();
    }

    if (dimension == 0) {
      List<List<Double>> newScales = new ArrayList<>();
      for (int i = 0; i < scales.size(); ++i) {
        List<Double> scale = new ArrayList<>();
        scale.add(minBoundary.get(i));
        scale.add(maxBoundary.get(i));
        newScales.add(scale);
      }

      // add a (one!) grid directory at the current position
      Map<GridDirectory<T>, GridDirectory<T>> result = new HashMap<>();
      GridDirectory<T> newOne =
          new GridDirectory<>(newScales, getMaxBucketSize(),
              getMinBucketDensity(), getMaxBucketDensity(), false);
      ++numOfElements;
      result.put(newOne, newOne);
      GridComponent<GridDirectory<T>> leaf = new GridLeaf<>(result);
      return leaf;
    }

    List<GridComponent<GridDirectory<T>>> result = new ArrayList<>();
    int level = scales.size() - dimension;
    for (int i = 0; i < scales.get(level).size() - 1; ++i) {
      minBoundary.add(scales.get(level).get(i));
      maxBoundary.add(scales.get(level).get(i + 1));

      result.add(createList(dimension - 1, scales, minBoundary, maxBoundary));

      minBoundary.remove(level);
      maxBoundary.remove(level);
    }
    return new GridNode<>(result);
  }

  @Override
  public GridLeaf<GridDirectory<T>> getLeaf(int[] coords) {
    if (coords == null) {
      Map<GridDirectory<T>, GridDirectory<T>> list = new HashMap<>();
      list.put(unlimitedDirectory, unlimitedDirectory);
      GridLeaf<GridDirectory<T>> tmp = new GridLeaf<>(list);
      return tmp;
    }
    return super.getLeaf(coords);
  }

  @Override
  public void clear() {
    unlimitedDirectory.clear();
    for (Map<GridDirectory<T>, GridDirectory<T>> map : getRootNode().getLists()) {
      for (Entry<GridDirectory<T>, GridDirectory<T>> e : map.entrySet()) {
        e.getKey().clear();
      }
    }
  }

  /**
   * Insert new grid directories at the edges of the grid.
   */
  public List<Map<T, T>> expandGridSpace() {
    numOfElements = 1;
    boolean[] newMinBounds = new boolean[getGridScales().size()];
    boolean[] newMaxBounds = new boolean[getGridScales().size()];

    for (int i = 0; i < getGridScales().size(); ++i) {
      newMinBounds[i] = false;
      newMaxBounds[i] = false;
      if (getGridScales().get(i).size() == 0) {
        newMinBounds[i] = true;
        newMaxBounds[i] = true;
        getGridScales().get(i).add(minBound[i] - 1.01);
        getGridScales().get(i).add(maxBound[i] + 1.01);
      } else {
        if (minBound[i] < getGridScales().get(i).get(0)) {
          newMinBounds[i] = true;
          double newBound =
              getGridScales().get(i).get(0) - 2
                  * (getGridScales().get(i).get(0) - minBound[i]);
          getGridScales().get(i).add(0, newBound);
        }
        if (maxBound[i] >= getGridScales().get(i).get(
            getGridScales().get(i).size() - 1)) {
          newMaxBounds[i] = true;
          int lastBoundaryIndex = getGridScales().get(i).size() - 1;
          double newBound =
              getGridScales().get(i).get(lastBoundaryIndex)
                  + 2
                  * (maxBound[i] - getGridScales().get(i)
                      .get(lastBoundaryIndex));
          getGridScales()
              .get(i)
              .add(
                  newBound > getGridScales().get(i).get(lastBoundaryIndex) ? newBound
                      : (newBound + 1.01));
        }
      }
    }

    GridComponent<GridDirectory<T>> newRootNode =
        createList(getGridScales().size(), getGridScales());
    int[] newCoords = new int[getGridScales().size()];
    int[] oldCoords = new int[getGridScales().size()];
    assignNewRootNode(newRootNode, newMinBounds, newMaxBounds, oldCoords,
        newCoords, 0);
    setRootNode(newRootNode);

    List<Map<T, T>> elements = new ArrayList<>();
    Map<T, T> elementsMap = new HashMap<>();
    // there always is exactly one map in the unlimited directory
    elementsMap.putAll(unlimitedDirectory.getAll().get(0));
    elements.add(elementsMap);
    unlimitedDirectory.clear();
    return elements;
  }

  protected void assignNewRootNode(GridComponent<GridDirectory<T>> newRootNode,
      boolean[] newMinBounds, boolean[] newMaxBounds, int[] oldCoords,
      int[] newCoords, int dim) {
    if (dim == getGridScales().size()) {
      newRootNode.getLeaf(newCoords).setElements(
          getRootNode().getLeaf(oldCoords).getElements());
    } else {
      for (int i = newMinBounds[dim] ? 1 : 0; i < getGridScales().get(dim)
          .size() - 1 - (newMaxBounds[dim] ? 1 : 0); ++i) {
        newCoords[dim] = i;
        oldCoords[dim] = newMinBounds[dim] ? i - 1 : i;
        assignNewRootNode(newRootNode, newMinBounds, newMaxBounds, oldCoords,
            newCoords, dim + 1);
      }
    }
  }

  public List<Map<T, T>> add(T element, double[] data) {
    int[] coords = coordinates(data);

    for (int i = 0; i < data.length; ++i) {
      if (data[i] < minBound[i] || getNumOfElements() == 0) {
        minBound[i] = data[i];
      }
      if (data[i] > maxBound[i] || getNumOfElements() == 0) {
        maxBound[i] = data[i];
      }
    }

    GridDirectory<T> dir;

    if (coords == null) {
      dir = unlimitedDirectory;
      // returns an empty list
      dir.addElement(element, data);
      if (unlimitedDirectory.getNumOfElements() > getMaxBucketSize()) {
        return expandGridSpace();
      }
      return new ArrayList<>();
    }

    dir = getLeaf(coords).getElements().values().iterator().next();
    return dir.addElement(element, data);

  }

  public GridDirectory<T> getUnlimitedDirectory() {
    return unlimitedDirectory;
  }

}
