package org.jamesii.core.util.hierarchy;

/**
 * Perform tests of {@link AbstractHierarchyTest} on {@link TwoMapHierarchy}.
 * 
 * @author Arne Bittig
 */
public class TwoMapHierarchyWithOrphansTest extends
    AbstractHierarchyTest<TwoMapHierarchy<String>> {

  public TwoMapHierarchyWithOrphansTest() {
    super(false);
  }

  @Override
  protected <T> IHierarchy<T> getNewHierarchy() {
    return new TwoMapHierarchy<>(true);
  }

}
