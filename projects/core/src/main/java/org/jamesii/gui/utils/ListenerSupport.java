/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A bean class that implements a container for listeners. It manages and
 * discards listeners that are {@code null} and also takes care of listeners
 * that should be added but are already in the list of listeners. Those will not
 * be added twice. It also implements the {@link Collection} interface hence can
 * be used directly in foreach loops.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          the type of the listeners to support
 * 
 */
public class ListenerSupport<T> implements Collection<T> {
  /**
   * contains all registered listeners
   */
  private final List<T> listeners = Collections
      .synchronizedList(new ArrayList<T>());

  /**
   * Adds a listener. A listener is only added if it is not {@code null} and is
   * not already registered.
   * 
   * @param listener
   *          the listener to add
   */
  public final synchronized boolean addListener(T listener) {
    if (listener != null && !listeners.contains(listener)) {
      return listeners.add(listener);
    }
    return false;
  }

  /**
   * Removes a previously registered listener. If the listener was not
   * registered before no action is performed.
   * 
   * @param listener
   *          the listener to remove
   * @return true if this collection contained the given listener
   */
  public final synchronized boolean removeListener(T listener) {
    return listeners.remove(listener);
  }

  /**
   * The listeners as {@link Collection} to be used in foreach loops. The
   * {@link ListenerSupport} class is also directly useable as
   * {@link Collection}. This method is only kept for legacy reasons.
   * 
   * @return a {@link Collection} of registered listeners
   */
  public final synchronized Collection<T> getListeners() {
    return new ArrayList<>(listeners);
  }

  @Override
  public final boolean add(T e) {
    return addListener(e);
    // throw new UnsupportedOperationException("Use addListener() instead.");
  }

  @Override
  public final boolean addAll(Collection<? extends T> c) {
    throw new UnsupportedOperationException("Use addListener() instead.");
  }

  /**
   * Removes all registered listeners.
   */
  @Override
  public final void clear() {
    listeners.clear();
  }

  @Override
  public final boolean contains(Object o) {
    return listeners.contains(o);
  }

  @Override
  public final boolean containsAll(Collection<?> c) {
    return listeners.containsAll(c);
  }

  @Override
  public final boolean isEmpty() {
    return listeners.isEmpty();
  }

  @Override
  public final Iterator<T> iterator() {
    // defensive copy, this allows to remove listeners even if they
    // are currently used in a foreach loop
    // (TODO sr137: but then Iterator.remove() will affect only a new list that
    // is not referenced anywhere else, which is probably not what a user
    // expects?! Should it not return an iterator that throws on remove()?
    // --ab358)
    return getListeners().iterator();
  }

  @SuppressWarnings("unchecked")
  @Override
  public final boolean remove(Object o) {
    return removeListener((T) o);
    // throw new
    // UnsupportedOperationException("Use removeListener() instead.");
  }

  @Override
  public final boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException("Use removeListener() instead.");
  }

  @Override
  public final boolean retainAll(Collection<?> c) {
    return listeners.retainAll(c);
  }

  @Override
  public final int size() {
    return listeners.size();
  }

  @Override
  public final Object[] toArray() {
    return listeners.toArray();
  }

  @Override
  public final <E> E[] toArray(E[] a) {
    return listeners.toArray(a);
  }

}
