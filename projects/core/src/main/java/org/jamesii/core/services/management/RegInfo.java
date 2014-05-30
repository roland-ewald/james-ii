/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.management;

import java.io.Serializable;

/**
 * Small helper class. Indicates whether the changed occurred is due to a
 * register or unregister process.
 * 
 * @author Gabriel Blum
 * @param <I>
 */
public class RegInfo<I> implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4390092775270796177L;

  /** The element. */
  private I element;

  /** The type. */
  private InfoType type;

  /**
   * Constructor for bean compliance.
   */
  public RegInfo() {
  }

  /**
   * Instantiates a new reg info.
   * 
   * @param ele
   *          the ele
   * @param t
   *          the t
   */
  public RegInfo(I ele, InfoType t) {
    element = ele;
    type = t;
  }

  /**
   * Gets the element.
   * 
   * @return the element
   */
  public final I getElement() {
    return element;
  }

  /**
   * Sets the element.
   * 
   * @param element
   *          the new element
   */
  public final void setElement(I element) {
    this.element = element;
  }

  /**
   * Gets the type.
   * 
   * @return the type
   */
  public final InfoType getType() {
    return type;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          the new type
   */
  public final void setType(InfoType type) {
    this.type = type;
  }

}
