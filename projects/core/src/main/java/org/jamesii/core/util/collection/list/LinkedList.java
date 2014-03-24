/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * An alternative linked list implementation. The implementation provided in
 * Java does not allow an efficient modification of the list if the list
 * iterator is used: in this case the iterator will hand over an index to the
 * main class to modify the list, thereby the iterator has already the pointer
 * where to change. This means that in the Java impl the pointer has to be
 * searched again. This implementation here avoids that.
 * 
 * @author Jan Himmelspach
 * 
 * @param <E>
 */
public class LinkedList<E> implements List<E>, Deque<E> {

  /**
   * The first node in the list.
   */
  private Entry<E> root;

  /**
   * The last node in the list.
   */
  private Entry<E> end;

  /**
   * The number of elements in the list.
   */
  private int size = 0;

  @Override
  public int size() {
    return getSize();
  }

  @Override
  public boolean isEmpty() {
    return (getSize() == 0);
  }

  @Override
  public boolean contains(Object o) {
    return this.firstOccurrence(o) != null;
  }

  @Override
  public Iterator<E> iterator() {
    return new Iter(this);
  }

  @Override
  public Object[] toArray() {
    Object[] result = new Object[getSize()];
    Entry<E> cursor = getRoot();
    for (int i = 0; i < getSize(); i++) {
      result[i] = cursor.value;
      cursor = cursor.next;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T[] toArray(T[] a) {
    return (T[]) toArray();
  }

  @Override
  public boolean add(E e) {
    addLast(e);
    return true;
  }

  @Override
  public boolean remove(Object o) {
    return removeFirstOccurrence(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {

    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    if (c.isEmpty()) {
      return false;
    }
    for (E e : c) {
      addLast(e);
    }
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    Entry<E> position = getEntry(index);

    for (E e : c) {
      position = addAfter(position, e);
    }

    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean result = false;
    for (Object o : c) {
      result = result || remove(o);
    }
    return result;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void clear() {
    setRoot(null);
    setEnd(null);
    setSize(0);

  }

  @Override
  public E get(int index) {
    return getEntry(index).value;
  }

  @Override
  public E set(int index, E element) {
    Entry<E> e = getEntry(index);
    E oldValue = e.value;
    e.value = element;
    return oldValue;
  }

  /**
   * Add the new element after the entry passed.
   * 
   * @param position
   * @param value
   */
  protected Entry<E> addAfter(Entry<E> position, E value) {
    Entry<E> newEntry = createEntry(value);

    if (position == null) {
      if (getRoot() == null) {
        setRoot(newEntry);
        setEnd(newEntry);
      } else {
        newEntry.next = getRoot();
        getRoot().previous = newEntry;
        setRoot(newEntry);
      }
      return newEntry;
    }

    if (position.next != null) {
      position.next.previous = newEntry;
    } else {
      setEnd(newEntry);
    }

    newEntry.next = position.next;
    position.next = newEntry;
    newEntry.previous = position;

    return newEntry;
  }

  /**
   * Add the new element before the entry passed.
   * 
   * @param position
   * @param value
   */
  private Entry<E> addBefore(Entry<E> position, E value) {

    Entry<E> newEntry = createEntry(value);

    newEntry.previous = position.previous;
    position.previous = newEntry;
    newEntry.next = position;

    if (newEntry.previous != null) {
      position.previous.next = newEntry;
    } else {
      setRoot(newEntry);
    }
    return newEntry;
  }

  @Override
  public void add(int index, E element) {
    if (getSize() == index) {
      addLast(element);
      return;
    }
    addBefore(getEntry(index), element);
  }

  private Entry<E> getEntry(int index) {
    if (index < 0 || index >= getSize()) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: "
          + getSize());
    }

    // first half, better to start from the root
    if (index < getSize() / 2) {
      Entry<E> current = getRoot();
      for (int i = 0; i < index; i++) {
        current = current.next;
      }

      return current;
    }

    // second half, better to start from the last element
    Entry<E> current = getEnd();
    for (int i = getSize() - 1; i > index; i--) {
      current = current.previous;
    }

    return current;
  }

  @Override
  public E remove(int index) {
    return remove(getEntry(index));
  }

  @Override
  public int indexOf(Object o) {
    Entry<E> current = getRoot();
    for (int i = 0; i < getSize(); i++) {
      if (current.value == o) {
        return i;
      }
      current = current.next;
    }
    return -1;
  }

  @Override
  public int lastIndexOf(Object o) {
    Entry<E> current = getEnd();
    for (int i = getSize(); i > 0; i--) {
      if (current.value == o) {
        return i;
      }
      current = current.previous;
    }
    return -1;
  }

  @Override
  public ListIterator<E> listIterator() {

    return new LIter(this);
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    LinkedList<E> result = new LinkedList<>();
    Entry<E> cursor = getEntry(fromIndex);
    for (int i = fromIndex; i < toIndex; i++) {
      result.addLast(cursor.value);
      cursor = cursor.next;
    }
    return null;
  }

  @Override
  public void addFirst(E e) {
    Entry<E> newRoot = createEntry(e);
    addFirst(newRoot);
  }

  /**
   * Internal method for adding entries to the head of the list.
   * 
   * @param newRoot
   */
  protected void addFirst(Entry<E> newRoot) {
    if (isEmpty()) {
      setRoot(newRoot);
      setEnd(getRoot());
    } else {
      newRoot.next = getRoot();
      getRoot().previous = newRoot;
      setRoot(newRoot);
    }
    setSize(getSize() + 1);
  }

  @Override
  public void addLast(E e) {
    Entry<E> newEntry = createEntry(e);
    addLast(newEntry);
  }

  /*
   * Internal method for adding entries to the end of the list.
   */
  protected void addLast(Entry<E> newEntry) {
    if (isEmpty()) {
      setRoot(newEntry);
      setEnd(newEntry);
    } else {
      getEnd().next = newEntry;
      newEntry.previous = getEnd();
      setEnd(newEntry);
    }
    setSize(getSize() + 1);
  }

  @Override
  public boolean offerFirst(E e) {
    addFirst(e);
    return true;
  }

  @Override
  public boolean offerLast(E e) {
    return add(e);
  }

  @Override
  public E removeFirst() {
    E result = remove();
    if (result == null) {
      throw new NoSuchElementException(
          "The list does not contain a first element.");
    }
    return result;
  }

  @Override
  public E removeLast() {
    if (isEmpty()) {
      throw new NoSuchElementException(
          "The list does not contain a last element.");
    }
    return pollLast();
  }

  @Override
  public E pollFirst() {
    return remove();
  }

  @Override
  public E pollLast() {
    if (isEmpty()) {
      return null;
    }

    E result = getEnd().value;

    setEnd(getEnd().previous);

    if (getEnd() != null) {
      getEnd().next = null;
    }
    setSize(getSize() - 1);
    return result;
  }

  @Override
  public E getFirst() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return getRoot().value;
  }

  @Override
  public E getLast() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return peekLast();
  }

  @Override
  public E peekFirst() {
    if (isEmpty()) {
      return null;
    }
    return getRoot().value;
  }

  @Override
  public E peekLast() {
    if (isEmpty()) {
      return null;
    }
    return null;
  }

  /**
   * Return the first occurrence of the object passed.
   * 
   * @param o
   *          the object top search for
   * @return the entry referring the object or null
   */
  private Entry<E> firstOccurrence(Object o) {
    Entry<E> current = getRoot();
    for (int i = 0; i < getSize(); i++) {
      if (current.value == o
          || (o != null ? o.equals(current.value) : current.value.equals(o))) {
        return current;
      }
      current = current.next;
    }
    return null;
  }

  /**
   * Return the last occurrence of the object passed.
   * 
   * @param o
   *          the object top search for
   * @return the entry referring the object or null
   */
  private Entry<E> lastOccurrence(Object o) {
    Entry<E> current = getEnd();
    for (int i = 0; i < getSize(); i++) {
      if (current.value == o
          || (o != null ? o.equals(current.value) : current.value.equals(o))) {
        return current;
      }
      current = current.previous;
    }
    return null;
  }

  /**
   * Remove the passed entry from the list
   * 
   * @param entry
   */
  protected E remove(Entry<E> entry) {

    if (entry.previous != null) {
      entry.previous.next = entry.next;
    } else {
      setRoot(entry.next);
    }

    if (entry.next != null) {
      entry.next.previous = entry.previous;
    } else {
      setEnd(entry.previous);
    }

    setSize(getSize() - 1);
    return entry.value;
  }

  @Override
  public boolean removeFirstOccurrence(Object o) {
    Entry<E> entry = firstOccurrence(o);
    if (entry != null) {
      remove(entry);
      return true;
    }
    return false;
  }

  @Override
  public boolean removeLastOccurrence(Object o) {
    Entry<E> entry = lastOccurrence(o);
    if (entry != null) {
      remove(entry);
      return true;
    }
    return false;
  }

  @Override
  public boolean offer(E e) {
    return offerLast(e);
  }

  @Override
  public E remove() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    E result = getRoot().value;
    if (getEnd() == getRoot()) {
      setEnd(null);
    }
    setRoot(getRoot().next);
    if (getRoot() != null) {
      getRoot().previous = null;
    }
    setSize(getSize() - 1);
    return result;
  }

  @Override
  public E poll() {
    return remove();
  }

  @Override
  public E element() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }
    return getRoot().value;
  }

  @Override
  public E peek() {
    if (isEmpty()) {
      return null;
    }
    return getRoot().value;
  }

  @Override
  public void push(E e) {
    addFirst(e);
  }

  @Override
  public E pop() {
    return removeFirst();
  }

  @Override
  public Iterator<E> descendingIterator() {
    return new DecIter(this);
  }

  /**
   * Can be overwritten by descendant in case of the need to create descendant
   * entry class types.
   * 
   * @param value
   * @return
   */
  protected Entry<E> createEntry(E value) {
    return new Entry<>(value);
  }

  /**
   * Get the root pointer.
   * 
   * @return
   */
  protected final Entry<E> getRoot() {
    return root;
  }

  /**
   * Set the root pointer.
   * 
   * @param root
   */
  protected final void setRoot(Entry<E> root) {
    this.root = root;
  }

  /**
   * Get the pointer to the last element in the list.
   * 
   * @return
   */
  protected final Entry<E> getEnd() {
    return end;
  }

  /**
   * Set the pointer to the (new) last entry in the list.
   * 
   * @param end
   */
  protected final void setEnd(Entry<E> end) {
    this.end = end;
  }

  /**
   * Get the size value.
   * 
   * @return
   */
  protected final int getSize() {
    return size;
  }

  /**
   * Set the size to the new value.
   * 
   * @param size
   */
  protected final void setSize(int size) {
    this.size = size;
  }

  /**
   * 
   * @author Jan Himmelspach
   * 
   * @param <E2>
   */
  protected static class Entry<E2> {
    E2 value = null;

    Entry<E2> next = null;

    Entry<E2> previous = null;

    public Entry(E2 value) {
      this.value = value;
    }

  }

  private class Iter implements Iterator<E> {

    Entry<E> cursor;

    LinkedList<E> list;

    int index;

    public Iter(LinkedList<E> list) {
      this.list = list;
      cursor = null;
      index = 0;
    }

    @Override
    public boolean hasNext() {
      return index != list.getSize();
    }

    @Override
    public E next() {
      if (index == getSize()) {
        throw new NoSuchElementException();
      }
      if (cursor == null) {
        cursor = list.getRoot();
      } else {
        cursor = cursor.next;
      }
      index++;
      return cursor.value;
    }

    @Override
    public void remove() {
      if (cursor == null) {
        return;
      }
      list.setSize(list.getSize() - 1);
      index--;
      if (cursor.previous != null) {
        if (getEnd() == cursor) {
          setEnd(cursor.previous);
        }
        cursor.previous.next = cursor.next;
        if (cursor.next != null) {
          cursor.next.previous = cursor.previous;
        }
      } else {
        list.setRoot(cursor.next);
        if (list.getRoot() != null) {
          list.getRoot().previous = null;
        }
      }
    }

  }

  private class LIter implements ListIterator<E> {

    /**
     * The entry the cursor is on.
     */
    private Entry<E> cursor;

    private LinkedList<E> list;

    private int index;

    public LIter(LinkedList<E> list) {
      this.list = list;
      cursor = null;
      index = 0;
    }

    @Override
    public boolean hasNext() {
      return index < list.getSize();

    }

    @Override
    public E next() {
      if (index == getSize()) {
        throw new NoSuchElementException();
      }

      if (index == 0) {
        cursor = list.getRoot();
      } else {
        cursor = cursor.next;
      }
      index++;
      return cursor.value;
    }

    @Override
    public boolean hasPrevious() {
      return index > 0;
    }

    @Override
    public E previous() {
      cursor = cursor.previous;
      index--;
      if (cursor == null) {
        return null;
      }
      return cursor.value;
    }

    @Override
    public int nextIndex() {
      return index + 1;
    }

    @Override
    public int previousIndex() {
      return index - 1;
    }

    @Override
    public void remove() {
      if (cursor == null) {
        return;
      }
      list.setSize(list.getSize() - 1);
      if (cursor.previous != null) {
        if (getEnd() == cursor) {
          setEnd(cursor.previous);
        }
        cursor.previous.next = cursor.next;
      } else {
        list.setRoot(cursor.next);
      }
    }

    @Override
    public void set(E e) {
      cursor.value = e;
    }

    @Override
    public void add(E e) {
      Entry<E> newEntry = list.createEntry(e);
      list.setSize(list.getSize() + 1);
      newEntry.previous = cursor;
      if (cursor != null) {
        newEntry.next = cursor.next;
        if (newEntry.next != null) {
          newEntry.next.previous = newEntry;
        }
        cursor.next = newEntry;
        if (list.getEnd() == cursor) {
          list.setEnd(newEntry);
        }
      } else { // cursor == null
        // cursor = newEntry;
        newEntry.next = getRoot();
        if (getRoot() != null) {
          getRoot().previous = newEntry;
        }
        list.setRoot(newEntry);

        if (list.getEnd() == null) {
          list.setEnd(newEntry);
        }
      }

    }

  }

  /**
   * Backward iterator.
   * 
   * @author Jan Himmelspach
   * 
   */
  private class DecIter implements Iterator<E> {

    /**
     * The entry at the cursor.
     */
    private Entry<E> cursor;

    /**
     * The reference to the list we are iterating on.
     */
    private LinkedList<E> list;

    public DecIter(LinkedList<E> list) {
      this.list = list;
      cursor = list.getEnd();
    }

    @Override
    public boolean hasNext() {
      return cursor != null && cursor.previous != null;
    }

    @Override
    public E next() {
      cursor = cursor.previous;
      return cursor.value;
    }

    @Override
    public void remove() {
      if (cursor == null) {
        return;
      }
      list.setSize(list.getSize() - 1);
      if (cursor.previous != null) {
        if (getEnd() == cursor) {
          setEnd(cursor.previous);
        }
        cursor.previous.next = cursor.next;
        if (cursor.next != null) {
          cursor.next.previous = cursor.previous;
        }
      } else {
        list.setRoot(cursor.next);
        if (list.getRoot() != null) {
          list.getRoot().previous = null;
        }
      }
    }

  }

}
