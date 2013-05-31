package org.jamesii.core.util.collection.gridfile;

import java.util.Iterator;
import java.util.List;

/**
 * Iterator class for the grid file.
 * 
 * @author Tobias Helms
 * 
 * @param <T>
 */
public class GridIterator<T> implements Iterator<T> {

  private GridFile<T> file;

  private List<T> objectList; // maybe there's a way to avoid this list?

  private int counter;

  public GridIterator(GridFile<T> file) {
    this.file = file;
    objectList = file.allElements();
    this.counter = 0;
  }

  @Override
  public boolean hasNext() {
    return counter < objectList.size();
  }

  @Override
  public T next() {
    return objectList.get(counter++);
  }

  @Override
  public void remove() {
    file.remove(objectList.get(counter - 1));
  }

}
