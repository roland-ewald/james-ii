/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;

/**
 * Class Fifo implements a first-in-first-out queue storing not-null objects.
 * The queue's length dynamically increases and decreases.
 * 
 * based on a version contained in JAMES created by Petra and Dirk Tyschler
 * 
 * @author Christan Ober
 * 
 * @param <E>
 *          the type of the elements
 * 
 *          history 16.09.2003 Extended and adapted for COSA by Christian Ober<br>
 *          history 04.08.2004 Removed cloneable support - currently not needed
 *          (JH)<br>
 *          history 10.11.2004 Added cloneable support - needed by AM project
 *          (JH)<br>
 */
public final class Fifo<E> implements Serializable, Cloneable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -7826859608300029912L;

  /** The values stored in the Fifo. */
  private List<E> values = new ArrayList<>();

  /**
   * Constructs an empty Fifo.
   */
  public Fifo() {
    this.values = new ArrayList<>();
  }

  /**
   * Constructs a Fifo holding the given vector. This method is private and can
   * only be used within this class definition, e.g. method clone(). If the
   * given vector is null then a new vector is constructed.
   * 
   * @param vector
   *          from which a new queue is generated
   */
  private Fifo(List<E> vector) {
    this.values = ((vector != null) ? vector : new ArrayList<E>());
  }

  /**
   * Adds the given object to the end of the queue if it is not null. Otherwise
   * an IllegalParameterException is thrown
   * 
   * @param object
   *          the object to add (must be != null!)
   * @throws NullPointerException
   *           exception thrown if the element is null
   */
  public synchronized void add(E object) {
    if (object == null) {
      throw new IllegalArgumentException("Cannot add null to a Fifo");
    }
    values.add(object);
    notifyAll();
  }

  /**
   * Returns the object at front of the queue, which is the oldest, but does not
   * delete it. The returned object can not be null. If there is no object to
   * get, the consumer (calling this method) is blocked until a producer puts an
   * object.
   * 
   * @return the first object of the queue
   */
  public synchronized E blockedGet() {
    while (isEmpty()) {
      try {
        wait();
      } catch (InterruptedException e) {
        SimSystem.report(e);
      }
    }
    E object = get();
    notifyAll();
    return object;
  }

  /**
   * Gets and deletes an object from the queue. If there is no object to get,
   * the consumer (calling this method) is blocked until a producer puts an
   * object.
   * 
   * @return an object
   */
  public synchronized E blockedOut() {
    while (isEmpty()) {
      try {
        wait();
      } catch (InterruptedException e) {
        SimSystem.report(e);
      }
    }
    E object = out();
    notifyAll();
    return object;
  }

  /**
   * Returns a clone of this Fifo.
   * 
   * @return a clone of this Fifo
   */
  @Override
  public synchronized Object clone() {
    return new Fifo<>(new ArrayList<>(values));
  }

  /**
   * Returns the object at front of the queue, which is the oldest, but does not
   * delete it. The returned object can not be null. This method throws a
   * <code>java.util.NoSuchElementException</code> if the queue was empty.
   * 
   * @return the first object of the queue without deleting it
   */
  public synchronized E get() {
    return values.get(0);
  }

  /**
   * Returns <CODE>true</CODE> if the queue is empty otherwise
   * <CODE>false</CODE>.
   * 
   * @return true if the queue is empty
   */
  public synchronized boolean isEmpty() {
    return values.isEmpty();
  }

  /**
   * Returns and deletes the object at front of the queue, which is the oldest.
   * The returned object can not be null. This method throws a
   * java.util.NoSuchElementException if the queue was empty.
   * 
   * @return the first object of the queue
   */
  public synchronized E out() {
    E object = values.get(0);
    values.remove(0);
    return object;
  }

  /**
   * Removes all objects in the queue without returning anything.
   */
  public synchronized void removeAllElements() {
    values.clear();
    notifyAll();
  }

  /**
   * Returns the number of objects still being in the queue.
   * 
   * @return the number of elements which are in the queue
   */
  public synchronized int size() {
    return values.size();
  }

  /**
   * Returns a string representation of the queue.
   * 
   * @return a string which is representing the queue
   */
  @Override
  public synchronized String toString() {
    return values.toString();
  }

}
