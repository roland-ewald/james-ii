/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import java.io.Serializable;

/**
 * Title: CoSA: Observer Description: Copyright: Copyright (c) 2003 Company:
 * University of Rostock, Faculty of Computer Science Modeling and Simulation
 * group
 * 
 * @author Jan Himmelspach
 * @version 1.0
 * @param <E>
 *          Type of entity to observe
 */
public abstract class Observer<E extends IObservable> implements Serializable,
    IObserver<E> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 6223966382985800249L;

  /**
   * @see org.jamesii.core.observe.IObserver#update(org.jamesii.core.observe.IObservable)
   */
  @Override
  public abstract void update(E entity);

  @Override
  public void update(E entity, Object hint) {
    update(entity);
  }
}
