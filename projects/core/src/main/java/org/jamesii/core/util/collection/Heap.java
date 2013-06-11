/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the idea of a heap.
 * 
 * idea: - Heap is a (binary) tree - Every node has a higher priority than every
 * descendant node - First position -> Element with highest priority - note:
 * element at pos. 0 is not used
 * 
 * @param <E>
 *          the type to be stored in the heap (must be comparable)
 * 
 * @author es074
 */
public class Heap<E extends Comparable<E>> {

  /** data storage. */
  private List<E> data = null;

  /** counts the number of elements stored in the heap. */
  private int size = 0;

  /**
   * Intialize new heap.
   */
  public Heap() {
    super();
    data = new ArrayList<>();
    size = 0;
    data.add(0, null);
  }

  /**
   * Adds the.
   * 
   * @param e
   *          elementtype
   */
  public void add(E e) {
    size++;
    data.add(size, e); // append e at the end of the heap
    if (size > 1) {
      upheap(size); // search for position of e
    }
  }

  /**
   * Downheap.
   * 
   * @param start
   *          the start
   * 
   *          searches for the right position after extracting first element;
   * 
   */
  private void downheap(int start) {
    E e1;
    int parent = start;
    // print();
    while (parent * 2 <= size) {
      int actson = parent * 2;
      // if right child < left child we use the right child
      if ((actson < size)
          && (data.get(actson).compareTo(data.get(actson + 1)) > 0)) {
        actson++;
      }
      // if current top element is larger as the smallest child swap them
      if (data.get(parent).compareTo(data.get(actson)) > 0) {
        e1 = data.get(parent);
        data.set(parent, data.get(actson));
        data.set(actson, e1);
        parent = actson;
      } else { // if none of the children is smaller get out - heap is
        // ready
        break;
      }
    }
    // print();
    // System.out.println("eof upheap");
  }

  /**
   * Extract top.
   * 
   * @return returns first element, removes first element from heap
   */
  public E extractTop() {
    if (size == 0) {
      return null;
    }

    E e1 = data.get(1);
    data.set(1, data.get(size));
    data.remove(size);
    size--;
    // print();
    downheap(1);

    return e1;
  }

  /**
   * Find the position of the given element (thereby the compareTo method of e
   * is used!).
   * 
   * Please note: The element returned is the first one for which compareTo
   * returns true - thus if it would return true for a number of elements you do
   * note have any hint which element is found (but this depends on the
   * compareTo method).
   * 
   * This method is on O(n).
   * 
   * @param e
   *          the element to be found
   * 
   * @return the position of the element in the internal data structure or -1 if
   *         not in there
   */
  protected int find(E e) {
    for (int i = 1; i <= size; i++) {
      if (data.get(i).compareTo(e) == 0) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Checks if is empty.
   * 
   * @return true if no element stored in the heap; else false
   */
  public boolean isEmpty() {
    return (size == 0);
  }

  /**
   * Prints the heap.
   */
  public void print() {
    for (E d : data) {
      System.out.print(d + " - ");
    }
    System.out.print("\n");
  }

  /**
   * Removes the first element from the heap.
   */
  public void remove() {
    if (size > 1) {
      data.set(1, data.get(size));
      data.remove(size);
      size--;
      downheap(1);
    }
  }

  /**
   * Remove the given entry from the heap. This method is fairly expensive at it
   * requires up to n calls to the {@link #add(Comparable)} method.
   * 
   * In addition it requires to call the {@link #find(Comparable)} method (O(n))
   * and thus has at a complexity of 2 * O(n) + n * O(log n).
   * 
   * It just removes the element from the list, and reinserts the tail into the
   * heap, that is all the elements being "larger" than the one removed.
   * 
   * @param e
   *          the element to be removed from the queue
   * @return returns true if the entry has found and got removed
   */
  public boolean remove(E e) {
    int pos = find(e);
    // if (pos == -1) System.out.println("ele not found!!");
    if (pos == -1) {
      return false; // element not found ...
    }

    // get a copy of the list
    List<E> dataBackup = new ArrayList<>(data);

    // remove the tail
    for (int i = data.size() - 1; i >= pos; i--) {
      data.remove(i);
      size--;
    }

    // re-insert tail
    for (int i = pos + 1; i < dataBackup.size(); i++) {
      add(dataBackup.get(i));
    }

    return true;
  }

  /**
   * Returns the number of elements currently in the heap.
   * 
   * @return the size of the heap, i.e. number of stored elements
   */
  public int size() {
    return size;
  }

  /**
   * Top.
   * 
   * @return returns first element of the heap
   */
  public E top() {
    if (size == 0) {
      return null;
    }
    return (data.get(1));
  }

  @Override
  public String toString() {
    return data.toString();
  }

  /**
   * Upheap.
   * 
   * @param curpos
   *          current position of the element inside the heap
   */
  private void upheap(int curpos) {
    E e1;
    // System.out.println("upheaping");
    // print();
    while (curpos > 1 && data.get(curpos / 2).compareTo(data.get(curpos)) > 0) {
      e1 = data.get(curpos / 2);
      data.set((curpos / 2), (data.get(curpos)));
      data.set(curpos, e1);
      curpos /= 2;
      // print();
    }
    // System.out.println("eof upheaping");
  }

  /**
   * Returns the internal list. warning: modifications to this list can harm the
   * "heap".
   * 
   * @return the list
   */
  public List<E> getList() {
    return data;
  }

}