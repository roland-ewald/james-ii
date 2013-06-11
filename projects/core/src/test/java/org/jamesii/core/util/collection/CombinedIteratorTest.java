package org.jamesii.core.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.collection.CombinedIterator;

/**
 * Tests for {@link CombinedIterator}
 * 
 * @author Arne Bittig
 * @date 08.06.2012
 */
public class CombinedIteratorTest extends ChattyTestCase {

  private final Collection<Integer> coll1 = Arrays.asList(23, 42);

  private final Collection<Double> coll2 = new HashSet<>(
      Arrays.asList(2.3, 4.2));

  /**
   * Assert that two iterators return identical (!) elements in the same order
   * (i.e. iterate over them and assert element equality by <code>==</code>, not
   * {@link Object#equals(Object)})
   * 
   * @param it1
   *          one iterator
   * @param it2
   *          other iterator
   */
  public static final void assertIteratorEquality(Iterator<?> it1,
      Iterator<?> it2) {
    assertEquals(it1.hasNext(), it2.hasNext());
    while (it1.hasNext()) {
      assertTrue(it2.hasNext());
      assertTrue(it1.next() == it2.next());
    }
  }

  // /**
  // * Count elements in collection or whatever the iterator represents
  // *
  // * @param it
  // * iterator
  // * @return number of elements when iterating over it completely
  // */
  // public static final int countElements(Iterator<?> it) {
  // int count = 0;
  // while (it.hasNext()) {
  // count++;
  // it.next();
  // }
  // return count;
  // }

  /**
   * Test whether the two constructors return "equal" iterators
   */
  public void testConstructors() {
    CombinedIterator<Number> ci1 =
        new CombinedIterator<Number>(coll1.iterator(), coll2.iterator());
    assertIteratorEquality(ci1, new CombinedIterator<Object>(coll1, coll2));
  }

  /**
   * Test whether empty iterators in between change the result
   */
  public void testEmptyIt() {
    CombinedIterator<Number> ci1 =
        new CombinedIterator<Number>(coll1, Collections.<Float> emptySet(),
            coll2, Arrays.<Short> asList());
    // assertEquals(4, countElements(ci1));
    Collection<Number> list = new ArrayList<Number>(coll1);
    list.addAll(coll2);
    assertIteratorEquality(ci1, list.iterator());
  }

  /**
   * Test whether null values are correctly discarded
   */
  public void testNull() {
    CombinedIterator<Number> ci1 =
        new CombinedIterator<Number>(coll1.iterator(), null, coll2.iterator());
    // assertEquals(4, countElements(ci1));
    assertIteratorEquality(ci1, new CombinedIterator<Number>(coll1.iterator(),
        coll2.iterator()));
  }
}
