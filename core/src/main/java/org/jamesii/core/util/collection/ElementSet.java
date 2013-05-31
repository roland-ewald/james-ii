/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.util.collection.list.ReusableListIterator;

/**
 * The Class ElementSet. The Class ElementSet is a class providing a specialized
 * iterator to iterate through the elements. The {@link ReusableListIterator} is
 * a very simple but very efficient implementation of the {@link Iterator}
 * interface. This iterator is not synchronized, so be sure that you will not
 * use it in the case of concurrent access to this set.
 * 
 * @author Jan Himmelspach
 * @param <M>
 */
public class ElementSet<M> extends org.jamesii.core.base.Entity implements
    IElementSet<M>, Iterable<M> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 3511263245364135429L;

  /**
   * The element iterator. a reusable iterator ... only one process should use
   * this iterator at a time!! This iterator is not thread safe.
   */
  private ReusableListIterator<M> elementIterator;

  /** The elements of this set stored in a simple array list. */
  private List<M> velements = new ArrayList<>(1);

  /**
   * Creates a new instance of ElementSet.
   */
  public ElementSet() {
  }

  /**
   * Add the element passed to the element set.
   * 
   * @param obj
   *          the obj
   */
  protected void add(M obj) {
    velements.add(obj);
  }

  @Override
  public final Iterator<M> iterator() {
    if (elementIterator == null) {
      elementIterator = new ReusableListIterator<>(velements);
    } else {
      elementIterator.init();
    }
    return elementIterator;
  }

  @Override
  public Iterator<M> privateIterator() {
    return velements.iterator();
  }

  @Override
  public int size() {
    return velements.size();
  }

  /**
   * Gets the element at the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the m
   */
  public M get(int index) {
    return velements.get(index);
  }

  /**
   * @return the velements
   */
  protected final List<M> getVelements() {
    return velements;
  }

  /**
   * @param velements
   *          the velements to set
   */
  protected final void setVelements(List<M> velements) {
    this.velements = velements;
  }

  /**
   * @return the elementIterator
   */
  protected final ReusableListIterator<M> getElementIterator() {
    return elementIterator;
  }

  /**
   * @param elementIterator
   *          the elementIterator to set
   */
  protected final void setElementIterator(
      ReusableListIterator<M> elementIterator) {
    this.elementIterator = elementIterator;
  }
}
