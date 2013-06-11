package org.jamesii.core.util.hierarchy;

/**
 * Perform tests of {@link AbstractHierarchyTest} on {@link TwoMapHierarchy}.
 * 
 * @author Arne Bittig
 */
public class LinkedHierarchyWithoutOrphansTest extends
    AbstractHierarchyTest<LinkedHierarchy<String>> {

  public LinkedHierarchyWithoutOrphansTest() {
    super(false);
  }

  @Override
  protected <T> IHierarchy<T> getNewHierarchy() {
    return new LinkedHierarchy<>(false);
  }

}
