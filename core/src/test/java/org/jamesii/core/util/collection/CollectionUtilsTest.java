package org.jamesii.core.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.logging.ApplicationLogger;

import junit.framework.TestCase;

/**
 * Test operations from {@link CollectionUtils}.
 * 
 * @author Arne Bittig
 */
public class CollectionUtilsTest extends TestCase {

  /**
   * Test {@link CollectionUtils#containsCollection(Collection, Collection)}
   */
  @SuppressWarnings("unchecked")
  public void testContainsCollection() {
    List<Integer> l1 = Arrays.asList(1, 2, 3, 1);
    List<Integer> l2 = new LinkedList<>();
    l2.add(3);
    l2.add(3);
    l2.add(2);
    l2.add(1);
    List<Integer> l3 = new ArrayList<>(Arrays.asList(2, 3, 2, 3, 2));
    List<Integer> l4 = Arrays.asList(4, 3, 2, 1);
    List<List<Integer>> collColl = Arrays.asList(l2, l3, l4);
    Collection<? extends Integer> contained =
        CollectionUtils.containsCollection(collColl, l1);
    ApplicationLogger.log(Level.INFO, contained.toString());
    assertNotNull(contained);
    assertNull(CollectionUtils
        .containsCollection(Arrays.asList(l2, l4, l1), l3));
  }

  /**
   * Test {@link CollectionUtils#intersectSorted(Iterable, Iterable)} and
   * {@link CollectionUtils#sortedCollectionsIntersect(Iterable, Iterable)}
   */
  public void testIntersectionOps() {
    List<String> s1 = Arrays.asList("ergear", "rfgw", "weger");
    List<String> s2 = Arrays.asList("awfd", "ergear", "rfgw", "seger");
    List<String> s3 = Arrays.asList("seger", "weger", "zzz");
    assertTrue(Arrays.equals(CollectionUtils.intersectSorted(s1, s2).toArray(),
        CollectionUtils.intersectSorted(s2, s1).toArray()));
    assertTrue(Arrays.equals(CollectionUtils.intersectSorted(s1, s3).toArray(),
        CollectionUtils.intersectSorted(s3, s1).toArray()));
    assertEquals(CollectionUtils.intersectSorted(s3, s1).size(), 1);
    assertEquals(CollectionUtils.intersectSorted(s2, s3).get(0), "seger");
    assertTrue(Arrays.equals(CollectionUtils.intersectSorted(s2, s2).toArray(),
        s2.toArray()));

    Collection<Double> i1 =
        Arrays.asList(1., 4., 7., 10., 13., 16., 19., 22., 25., 28., 31.);
    Collection<Double> i2 =
        Arrays.asList(0., 4., 8., 12., 16., 20., 24., 28., 32.);
    Collection<Double> i3 =
        Arrays.asList(0.5, 3., 5.5, 8., 10.5, 13., 15.5, 18., 20.5, 23., 25.5,
            28., 30.5);
    Collection<Double> i4 =
        Arrays.asList(0.5, 2.75, 5., 7.25, 9.5, 11.75, 14., 16.25, 18.5, 20.75,
            23., 25.25, 27.5, 29.75, 32.);
    assertEquals(CollectionUtils.intersectSorted(i1, i2).size(), 3);
    assertTrue(Arrays.equals(CollectionUtils.intersectSorted(i1, i3).toArray(),
        new Double[] { 13., 28. }));
    assertNull(CollectionUtils.sortedCollectionsIntersect(i1, i4));
    // assertTrue(MultiOccupancyStaticGridSpatialIndex
    // .sortedCollectionsIntersect(i2, i4));
    assertEquals(CollectionUtils.sortedCollectionsIntersect(i3, i4), .5);
    assertEquals(CollectionUtils.intersectSorted(i4, i2).get(0), 32.);
    assertTrue(Arrays.equals(CollectionUtils.intersectSorted(i1, i1).toArray(),
        i1.toArray()));
    assertTrue(Arrays.equals(
        CollectionUtils.intersectSorted(i1, new HashSet<Double>()).toArray(),
        new Object[0]));
    assertTrue(Arrays.equals(new Integer[0],
        CollectionUtils.intersectSorted(new ArrayList<Double>(), i1).toArray()));
  }
}
