/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

/**
 * Perform tests of {@link AbstractHierarchyTest} on {@link LinkedHierarchy}.
 * 
 * @author Arne Bittig
 */
public class LinkedHierarchyWithOrphansTest extends
    AbstractHierarchyTest<LinkedHierarchy<String>> {

  public LinkedHierarchyWithOrphansTest() {
    super(false);
  }

  @Override
  protected <T> IHierarchy<T> getNewHierarchy() {
    return new LinkedHierarchy<>(true);
  }

}
