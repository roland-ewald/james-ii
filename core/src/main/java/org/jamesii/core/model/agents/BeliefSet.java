/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.agents;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * The class {@link BeliefSet}:
 * 
 * A special set containing different {@link IBelief}s and implementing the
 * basic {@link Set} interface.
 * 
 * @author Alexander Steiniger
 * 
 * @param <T>
 * @param <B>
 */
public class BeliefSet<T, B extends IBelief<T>> implements Set<B> {

  /** internal hashset containing all beliefs */
  private Set<B> elements = new HashSet<>();

  /**
   * @throws IllegalArgumentException
   *           if the given belief that shall be added to the set is
   *           <code>null</code>
   */
  @Override
  public boolean add(B belief) {
    // check if given belief is null
    if (belief == null) {
      throw new IllegalArgumentException(
          "The element to add to the BeliefSet is null.");
    }
    return elements.add(belief);
  }

  /**
   * @throws IllegalArgumentException
   *           if the given collection that shall be added to the set is
   *           <code>null</code>
   */
  @Override
  public boolean addAll(Collection<? extends B> collection) {
    // check if collection is null
    if (collection == null) {
      throw new IllegalArgumentException(
          "The collection to add to the BeliefSet is null.");
    }
    return elements.addAll(collection);
  }

  @Override
  public void clear() {
    elements.clear();
  }

  @Override
  public boolean contains(Object object) {
    return elements.contains(object);
  }

  @Override
  public boolean containsAll(Collection<?> collection) {
    return elements.containsAll(collection);
  }

  @Override
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  @Override
  public Iterator<B> iterator() {
    return elements.iterator();
  }

  /**
   * @throws IllegalArgumentException
   *           if the object that shall be removed from the set is
   *           <code>null</code>
   */
  @Override
  public boolean remove(Object object) {
    // check if the object to remove is null
    if (object == null) {
      throw new IllegalArgumentException(
          "The object to remove from the BeliefSet is null.");
    }
    return elements.remove(object);
  }

  /**
   * @throws IllegalArgumentException
   *           if the given collection that shall be removed from the set is
   *           <code>null</code>
   */
  @Override
  public boolean removeAll(Collection<?> collection) {
    // check if the collection to remove is null
    if (collection == null) {
      throw new IllegalArgumentException(
          "The collection to remove from the BeliefSet is null.");
    }
    return elements.removeAll(collection);
  }

  /**
   * @throws if
   *           the given collection to retain all beliefs from that are in the
   *           set is <code>null</code>
   */
  @Override
  public boolean retainAll(Collection<?> collection) {
    // check if the collection to retain is null
    if (collection == null) {
      throw new IllegalArgumentException(
          "The collection to retain all beliefs that are in the BeliefSet is null.");
    }
    return elements.retainAll(collection);
  }

  @Override
  public int size() {
    return elements.size();
  }

  @Override
  public Object[] toArray() {
    return elements.toArray();
  }

  /**
   * @throws IllegalArgumentException
   *           if the given array is <code>null</code>
   */
  @SuppressWarnings("hiding")
  @Override
  public <T> T[] toArray(T[] a) {
    // check if given array is null
    if (a == null) {
      throw new IllegalArgumentException("The given array is null.");
    }
    return elements.toArray(a);
  }

}
