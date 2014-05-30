/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.hierarchy.TwoMapHierarchy;

/**
 * Perform tests of {@link AbstractHierarchyTest} on {@link TwoMapHierarchy}.
 * 
 * @author Arne Bittig
 */
public class TwoMapHierarchyWithoutOrphansTest extends
    AbstractHierarchyTest<TwoMapHierarchy<String>> {

  public TwoMapHierarchyWithoutOrphansTest() {
    super(false);
  }

  @Override
  protected <T> IHierarchy<T> getNewHierarchy() {
    return new TwoMapHierarchy<>(false);
  }

}
