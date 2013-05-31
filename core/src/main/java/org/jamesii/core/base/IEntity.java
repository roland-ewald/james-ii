/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

import org.jamesii.core.observe.IObservable;

/**
 * The Interface IEntity.
 * 
 * IEntity is the basic interface for all classes in the system which shall have
 * a unique id. The IEntity interface extends the IObservable interface.
 * 
 * @author Jan Himmelspach
 */
public interface IEntity extends IObservable {

  /**
   * Return a string containing information about the object.
   * 
   * This can be, e.g., "complete" information about any objects linked from the
   * implementing object. Please note: Depending on the class implementing this
   * method a call to this method might be very time and memory consuming.
   * 
   * @return the information about the entity as a string
   */
  String getCompleteInfoString();

  /**
   * Return a simple identifier of the object. simple identifiers are only
   * unique per VM instance. Each class implementing this interface has to auto
   * create this id upon creation.
   * 
   * @return simple identifier which has been auto created by calling the
   *         constructor
   */
  long getSimpleId();

}
