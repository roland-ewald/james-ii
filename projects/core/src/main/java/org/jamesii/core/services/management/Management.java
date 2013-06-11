/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.management;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.Entity;

/**
 * Basic class for the management of services. Provides the default
 * functionality required for the management and the access to services.
 * 
 * @param <I>
 *          The type which shall be manged by using this class.
 * 
 * @author Jan Himmelspach
 */
public class Management<I> extends Entity implements IManagement<I> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -496634285631102515L;

  /** The basic internal management data structure. */
  private volatile List<I> elements = new ArrayList<>();

  @Override
  public synchronized void register(I element) {
    elements.add(element);
    changed(new RegInfo<>(element, InfoType.REGISTER));
  }

  @Override
  public synchronized void unregister(I element) {
    elements.remove(element);
    changed(new RegInfo<>(element, InfoType.UNREGISTER));
  }

  @Override
  public synchronized void contains(I element) {
    elements.contains(element);
  }

  @Override
  public synchronized I get() {
    return null;
  }

  @Override
  public synchronized List<I> getSome(int max) {
    return null;
  }

  @Override
  public List<I> getList() {
    return new ArrayList<>(elements);
  }

  @Override
  public synchronized int size() {
    return elements.size();
  }

  @Override
  public I get(int index) {
    return elements.get(index);
  }

  /**
   * @return the elements
   */
  public final List<I> getElements() {
    return elements;
  }

}
