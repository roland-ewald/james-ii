/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

/**
 * Exception that is thrown if performance database is applied to non-hibernate
 * objects.
 * 
 * @author Roland Ewald
 */
public class NoHibernateEntityException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5729948831961934067L;

  /**
   * Instantiates a new exception.
   * 
   * @param object
   *          the object that caused the problem
   */
  public NoHibernateEntityException(Object object) {
    super("[Hibernate-PerfDB]: Object " + object + " of class "
        + object.getClass().getCanonicalName() + " cannot be handled.");
  }

}
