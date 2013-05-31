package org.jamesii.core.util.collection.gridfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.util.collection.gridfile.directory.GridDirectory;
import org.jamesii.core.util.collection.gridfile.directory.GridLeaf;
import org.jamesii.core.util.collection.gridfile.directory.RootDirectory;

/**
 * Basic grid file implementation.
 * 
 * Some ideas about grid files in general and about the implementation design
 * can be found at ... There is a comparison of grid files and other index
 * structures at ...
 * 
 * If two elements have the same coordinates they only add 1 to the bucket
 * density! The problem is, if all elements are at the same position, how should
 * they split into two buckets with the same coordinates? Possible improvements:
 * 0. search the right scale with binary search 1. more intelligent expansion of
 * the root directory 3. merge and split with the root directory 4. add a
 * dynamic method to create the scales of each grid directories
 * 
 * @author Tobias Helms
 * 
 * @param <T>
 */
public class GridFile<T> implements Collection<T> {

  /** Extract the double values of the dimensions from the elements. **/
  private IDimensionExtractor<T> extractor;

  /**
   * Directory to find the bucket of an element. It's the only object containing
   * bucket references.
   */
  private RootDirectory<T> rootDirectory;

  /**
   * List of all elements to get all elements fast
   */
  // protected List<T> allElements;

  /**
   * HashMap of all elements of the grid file.
   */
  private Map<T, T> allElements;

  /** Number of dimensions. **/
  private int numOfDimensions;

  public double[] getData(T element) {
    double[] data = new double[numOfDimensions];
    for (int i = 0; i < numOfDimensions; ++i) {
      data[i] = extractor.getData(element, i);
    }
    return data;
  }

  /**
   * Remove all elements from the grid file but keep the file structure!
   */
  @Override
  public void clear() {
    allElements.clear();
    if (numOfDimensions > 0) {
      rootDirectory.clear();
    }
  }

  /**
   * 
   * 
   * @param extractor
   * @param scales
   * @param maxBucketSize
   * @param minBucketDensity
   * @param maxBucketDensity
   */
  public GridFile(IDimensionExtractor<T> extractor, List<List<Double>> scales,
      int maxBucketSize, double minBucketDensity, double maxBucketDensity) {
    super();

    this.extractor = extractor;
    this.numOfDimensions = scales.size();
    this.allElements = new HashMap<>();

    if (numOfDimensions > 0) {
      rootDirectory =
          new RootDirectory<>(scales, maxBucketSize, minBucketDensity,
              maxBucketDensity);
    }
  }

  @Override
  public boolean add(T element) {
    allElements.put(element, element);

    if (numOfDimensions > 0) {
      List<Map<T, T>> elements = rootDirectory.add(element, getData(element));
      for (Map<T, T> map : elements) {
        for (Entry<T, T> e : map.entrySet()) {
          allElements.remove(e);
          add(e.getKey());
        }
      }
    }

    return true;
  }

  /**
   * Return all elements in a list.
   */
  public List<T> allElements() {
    List<T> result = new ArrayList<>(allElements.size());
    for (T e : allElements.keySet()) {
      result.add(e);
    }
    return result;
  }

  public IDimensionExtractor<T> getDimensionExtractor() {
    return extractor;
  }

  public List<T> exactMatch(double[] data) {
    if (numOfDimensions == 0) {
      return new ArrayList<>(allElements.keySet());
    }

    Map<T, T> elements =
        rootDirectory.getLeaf(data).getElements().values().iterator().next()
            .getLeaf(data).getElements();

    List<T> resultList = new ArrayList<>();
    for (Entry<T, T> element : elements.entrySet()) {
      boolean isEqual = true;
      for (int i = 0; i < data.length; ++i) {
        if (extractor.getData(element.getKey(), i) != data[i]) {
          isEqual = false;
          break;
        }
      }
      if (isEqual) {
        resultList.add(element.getKey());
      }
    }
    return resultList;
  }

  public List<T> partialMatch(double[] data, List<Integer> ignoredIndexes) {
    if (numOfDimensions == 0 || ignoredIndexes.size() == numOfDimensions) {
      return new ArrayList<>(allElements.keySet());
    }

    if (ignoredIndexes.size() == 0) {
      return exactMatch(data);
    }

    int[] coords = rootDirectory.coordinates(data, ignoredIndexes);
    List<Map<T, T>> elements = new ArrayList<>();
    elements.add(((GridLeaf<T>) rootDirectory.getUnlimitedDirectory()
        .getRootNode()).getElements());

    if (coords != null) {
      Map<GridDirectory<T>, GridDirectory<T>> directories =
          rootDirectory.partialMatch(coords);

      for (Entry<GridDirectory<T>, GridDirectory<T>> dir : directories
          .entrySet()) {
        if (dir.getKey().getNumOfElements() > 0) {
          coords = dir.getKey().coordinates(data, ignoredIndexes);
          elements.add(dir.getKey().partialMatch(coords));
        }
      }
    }

    List<T> resultList = new ArrayList<>();
    for (Map<T, T> map : elements) {
      for (Entry<T, T> element : map.entrySet()) {
        boolean isEqual = true;
        for (int i = 0; i < data.length; ++i) {
          if (ignoredIndexes.contains(i)) {
            continue;
          }
          if (extractor.getData(element.getKey(), i) != data[i]) {
            isEqual = false;
            break;
          }
        }
        if (isEqual) {
          resultList.add(element.getKey());
        }
      }
    }

    return resultList;
  }

  /**
   * Remove an element from the grid file. Be careful: the parameter must be
   * from class T, because the extractor needs an object from class T to extract
   * the necessary data.
   */
  @Override
  public boolean remove(Object object) throws ClassCastException {
    @SuppressWarnings("unchecked")
    // throw an exception if it the object could not be casted
    T element = (T) object;
    if (numOfDimensions == 0) {
      if (allElements.remove(object) != null) {
        return true;
      }
      return false;
    }

    // if the data type is not T, the data extractor
    // should throw a runtime exception
    double[] data = getData(element);
    if (rootDirectory.getLeaf(data).getElements().values().iterator().next()
        .removeElement(object, data)) {
      // TODO: th162: update maxbound and minbound in root directory
      allElements.remove(object);
      return true;
    }
    return false;
  }

  public int getNumOfDimensions() {
    return numOfDimensions;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    boolean first = true;
    for (Entry<T, T> e : allElements.entrySet()) {
      if (!first) {
        builder.append(",");
      }
      builder.append(e.getKey().toString());
      first = false;
    }
    return builder.toString();
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    for (T e : c) {
      add(e);
    }
    return true;
  }

  @Override
  public boolean contains(Object o) {
    // take the map to check if the grid contains this element, i.e. use the
    // hash
    return allElements.containsKey(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    boolean result = true;
    for (Object o : c) {
      result = result && allElements.containsKey(o);
      if (!result) {
        break;
      }
    }
    return result;
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public Iterator<T> iterator() {
    return new GridIterator<>(this);
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    for (Object o : c) {
      remove(o);
    }
    return true;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean changed = false;
    for (Object o : c) {
      if (!contains(o)) {
        remove(o);
        changed = true;
      }
    }
    return changed;
  }

  @Override
  public int size() {
    return allElements.size();
  }

  /**
   * Not implemented yet.
   */
  @Override
  public Object[] toArray() {
    throw new RuntimeException("GridFile.toArray(): Not implemented yet!");
  }

  /**
   * Not implemented yet.
   */
  @Override
  public <E> E[] toArray(E[] a) {
    throw new RuntimeException("GridFile.toArray(E[]): Not implemented yet!");
  }

}
