package org.jamesii.core.util.sorting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesii.core.util.sorting.ISortingAlgorithm;

import junit.framework.TestCase;

/**
 * The Class TestSortingAlgorithm.
 * 
 * Can be used to test sorting algorithms based on the ISortingAlgorithm
 * interface.
 * 
 * If the {@link #debugOut} is enabled tests will take longer (because a string
 * containing all test case values is generated per test case execution), but
 * you'll get more information about the test failed.
 * 
 * @author Jan Himmelspach
 */
public abstract class TestSortingAlgorithm extends TestCase {

  /**
   * Creates the algorithm. Overwrite in descendant classes to test concrete
   * sorting algorithms.
   * 
   * @return the i sorting algorithm
   */
  protected abstract ISortingAlgorithm createAlgorithm();

  /** The test cases. */
  private List<List<? extends Comparable<?>>> testCases;

  /** The sort alg. */
  private ISortingAlgorithm sortAlg;

  /** The debug out. Set to true to see the values with which the test failed. */
  protected boolean debugOut = false;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    int largeSetBounds = 800;

    sortAlg = createAlgorithm();

    // string based tests

    testCases = new ArrayList<>();

    ArrayList<String> testCase = null;

    // testCases.add(testCase);

    // test empty list
    testCase = new ArrayList<>();
    testCases.add(testCase);

    // test list with one entry, an empty string
    testCase = new ArrayList<>();
    testCase.add("");
    testCases.add(testCase);

    // test list with one entry, a non-empty string
    testCase = new ArrayList<>();
    testCase.add("a");
    testCases.add(testCase);

    // test list with two identical strings
    testCase = new ArrayList<>();
    testCase.add("a");
    testCase.add("a");
    testCases.add(testCase);

    // test list with two strings already ordered (ascending)
    testCase = new ArrayList<>();
    testCase.add("a");
    testCase.add("b");
    testCases.add(testCase);

    // test list with two strings already ordered (descending)
    testCase = new ArrayList<>();
    testCase.add("b");
    testCase.add("a");
    testCases.add(testCase);

    // test list with similar string, unordered
    testCase = new ArrayList<>();
    testCase.add("a");
    testCase.add("ab");
    testCase.add("aa");
    testCases.add(testCase);

    // test list with one entry, an empty string, and several others
    testCase = new ArrayList<>();
    testCase.add("f");
    testCase.add("");
    testCase.add("2");
    testCase.add("ab");
    testCase.add("aa");
    testCases.add(testCase);

    // integer based test cases

    ArrayList<Integer> testCaseInt;

    // test empty list again
    testCaseInt = new ArrayList<>();
    testCases.add(testCaseInt);

    // test list with two similar values
    testCaseInt = new ArrayList<>();
    testCaseInt.add(0);
    testCaseInt.add(0);
    testCases.add(testCaseInt);

    // test list, similar values all over, besides the one on the middle
    testCaseInt = new ArrayList<>();
    for (int i = 0; i < 100; i++) {
      testCaseInt.add(0);
    }
    testCaseInt.add(50, 1);
    testCases.add(testCaseInt);

    // test list, negative and positive values, ordered ascending
    testCaseInt = new ArrayList<>();
    for (int i = 0; i < largeSetBounds; i++) {
      testCaseInt.add(i - largeSetBounds / 2);
    }
    testCases.add(testCaseInt);

    // test list, negative and positive values, unordered (hopefully)
    Random rand = new Random(1654654);
    testCaseInt = new ArrayList<>();
    for (int i = 0; i < largeSetBounds; i++) {
      testCaseInt.add(rand.nextInt(100000) - 100000 / 2);
    }
    testCases.add(testCaseInt);

    // test list, negative and positive values, unordered (hopefully)
    rand = new Random(342765834);
    testCaseInt = new ArrayList<>();
    for (int i = 0; i < largeSetBounds; i++) {
      testCaseInt.add(rand.nextInt(100000) - 100000 / 2);
    }
    testCases.add(testCaseInt);

    testCaseInt = new ArrayList<>();
    for (int i = 0; i < largeSetBounds; i++) {
      testCaseInt.add(i);
    }
    testCases.add(testCaseInt);

    testCaseInt = new ArrayList<>();
    for (int i = largeSetBounds; i > 0; i--) {
      testCaseInt.add(i);
    }
    testCases.add(testCaseInt);

    testCaseInt = new ArrayList<>();
    for (int i = 0; i < largeSetBounds; i += 2) {
      testCaseInt.add(i);
      testCaseInt.add(i);
    }
    testCases.add(testCaseInt);

    testCaseInt = new ArrayList<>();
    for (int i = largeSetBounds; i > 0; i -= 2) {
      testCaseInt.add(i);
      testCaseInt.add(i);
    }
    testCases.add(testCaseInt);

    testCaseInt = new ArrayList<>();
    for (int i = 0; i < largeSetBounds; i += 2) {
      testCaseInt.add(i);
      testCaseInt.add(i);
      testCaseInt.add(i + 1);
    }
    testCases.add(testCaseInt);

    testCaseInt = new ArrayList<>();
    for (int i = largeSetBounds; i > 0; i -= 2) {
      testCaseInt.add(i);
      testCaseInt.add(i);
      testCaseInt.add(i - 1);
    }
    testCases.add(testCaseInt);
  }

  /**
   * List to string. Convert a passed list into a comma separated list of
   * values.
   * 
   * @param list
   *          the list
   * 
   * @return the string
   */
  protected static String listToString(List<?> list) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      if (i != 0) {
        result.append(", " + list.get(i));
      } else {
        result.append("" + list.get(i));
      }
    }
    return result.toString();
  }

  /**
   * Check sorting of list.
   * 
   * @param testCaseNo
   *          the number of the test case
   * @param list
   *          the list
   * @param asc
   *          ascending ( true ) or descending
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void checkList(int testCaseNo, List<Comparable> list, boolean asc) {
    for (int i = 1; i < list.size(); i++) {
      Comparable a = list.get(i - 1);
      Comparable b = list.get(i);
      if (asc) {
        assertTrue(testCaseToString(testCaseNo, asc), a.compareTo(b) <= 0);
      } else {
        assertTrue(testCaseToString(testCaseNo, asc), a.compareTo(b) >= 0);
      }
    }
  }

  /**
   * Test case to string.
   * 
   * @param testCaseNo
   *          the test case no
   * @param asc
   *          true if ascending sort order
   * 
   * @return the string
   */
  protected String testCaseToString(int testCaseNo, boolean asc) {
    String result = "Test case : " + testCaseNo + " - sort order (" + asc + ")";
    if (debugOut) {
      result += " " + listToString(testCases.get(testCaseNo));
    }

    return "Test case : " + testCaseNo + result;
  }

  /**
   * Test sort in place.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void testSortInPlace() {
    // <T> void sortInPlace(List<Comparable<T>> list, boolean ascending);
    for (int tC = 0; tC < testCases.size(); tC++) {
      List<Comparable> list = new ArrayList(testCases.get(tC));
      sortAlg.sortInPlace(list, true);
      assertEquals(testCaseToString(tC, true), list.size(), testCases.get(tC)
          .size());
      checkList(tC, list, true);

      list = new ArrayList(testCases.get(tC));
      sortAlg.sortInPlace(list, false);
      assertEquals(testCaseToString(tC, false), list.size(), testCases.get(tC)
          .size());
      checkList(tC, list, false);
    }
  }

  /**
   * Check unchanged. Both list have to have the same objects at the same
   * positions. This methods returns "true" (trivial case) if list1 == list2,
   * and if for any item at index i in list1 the same item exists in list2 at
   * the same index. This can be used for checking whether a list is unmodified
   * or not.
   * 
   * @param list1
   *          the list1
   * @param list2
   *          the list2
   */
  protected void checkUnchanged(List<?> list1, List<?> list2) {
    assertEquals(list1.size(), list2.size());
    for (int i = 0; i < list1.size(); i++) {
      assertTrue(list1.get(i) == list2.get(i));
    }
  }

  /**
   * Test sort.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void testSort() {
    for (int tC = 0; tC < testCases.size(); tC++) {
      List<Comparable> list = new ArrayList(testCases.get(tC));
      List<Comparable> resultList = sortAlg.sort(list, true);
      assertTrue(list != resultList);
      checkUnchanged(list, testCases.get(tC));
      checkList(tC, resultList, true);

      list = new ArrayList(testCases.get(tC));
      resultList = sortAlg.sort(list, false);
      assertTrue(list != resultList);
      checkUnchanged(list, testCases.get(tC));
      checkList(tC, resultList, false);
    }
  }

}
