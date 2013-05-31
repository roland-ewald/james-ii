/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.misc.Strings;

import junit.framework.TestCase;

/**
 * Unit tests for string methods.
 * 
 * @author Roland Ewald
 * @author Johannes Rössel
 */
public class TestStrings extends TestCase {

  /** The map to be tested. Needs to be {@link Serializable}. */
  Map<String, Object> map1 = new HashMap<>();

  @Override
  public void setUp() {
    map1.put("testKey", 23);
  }

  @Override
  public void tearDown() {
    map1.clear();
  }

  /**
   * Test serialize.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  // Object is casted to HashMap
  public void testSerialize() throws Exception {
    byte[] serializedObject = SerialisationUtils.serialize((Serializable) map1);
    testDeserializedMap((HashMap<String, Object>) SerialisationUtils
        .deserialize(serializedObject));
  }

  /**
   * Test serialize b64 string.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  // Object is casted to HashMap
  public void testSerializeB64String() throws Exception {
    String testString =
        SerialisationUtils.serializeToB64String((Serializable) map1);
    testDeserializedMap((HashMap<String, Object>) SerialisationUtils
        .deserializeFromB64String(testString));
  }

  /**
   * Test number of significant fraction digits.
   */
  public void testNumberOfSignificantFractionDigits() {
    int numOfDecimals = Strings.getNumberOfSignificantFractionDigits(0.505); // -
    // 1.234E02
    assertEquals(1, numOfDecimals);
  }

  /**
   * Test deserialized map.
   * 
   * @param map2
   *          the map2
   */
  protected void testDeserializedMap(Map<String, Object> map2) {
    assertEquals(map1, map2);
    assertEquals(23, map2.get("testKey"));
  }

  /**
   * Test count substrings.
   */
  public void testCountSubstrings() {

    int count = Strings.countSubstrings("abcabcabc", "ab");
    assertEquals(3, count);
    count = Strings.countSubstrings("abcabcabc", "ab");
    assertEquals(3, count);
    count = Strings.countSubstrings("", "ab");
    assertEquals(0, count);
    count = Strings.countSubstrings("ab", "");
    assertEquals(0, count);
    count = Strings.countSubstrings("", "");
    assertEquals(0, count);
    count = Strings.countSubstrings("abcabc<abc", "<ab");
    assertEquals(1, count);
  }

  /**
   * Test Levenshtein distance.
   */
  public void testLevenshteinDistance() {
    assertEquals(0, Strings.getLevenshteinDistance("kitten", "kitten"));
    assertEquals(3, Strings.getLevenshteinDistance("kitten", "sitting"));
    assertEquals(3, Strings.getLevenshteinDistance("Saturday", "Sunday"));
    assertEquals(1, Strings.getLevenshteinDistance("kitten", "sitten"));
    assertEquals(0, Strings.getLevenshteinDistance("", ""));
    assertEquals(4, Strings.getLevenshteinDistance("", "1234"));
    assertEquals(4, Strings.getLevenshteinDistance("1234", ""));
  }

  /**
   * Test Hamming distance
   */
  public void testHammingDistance() {
    assertEquals(0, Strings.getHammingDistance("kitten", "kitten"));
    assertEquals(1, Strings.getHammingDistance("kitten", "Kitten"));
    assertEquals(3, Strings.getHammingDistance("toned", "roses"));
    assertEquals(3, Strings.getHammingDistance("roses", "toned"));
    assertEquals(1, Strings.getHammingDistance("00110", "00100"));
    assertEquals(2, Strings.getHammingDistance("1011101", "1001001"));
    assertEquals(-1, Strings.getHammingDistance("kittin", "kitti"));
  }

  /**
   * Test disp class name.
   */
  public void testDispClassName() {
    assertEquals("String", Strings.dispClassName(String.class));
    assertEquals("Integer", Strings.dispClassName(Integer.class));
  }

  /**
   * Test display functions for maps.
   */
  public void testDispMap() {
    // The output functions should never return null
    assertNotNull(Strings.dispMap(null));
    assertNotNull(Strings.dispMap(null, null));
    assertNotNull(Strings.dispMap(null, null, null, null, false));

    Map<String, String> testMap = new HashMap<>();
    testMap.put("a1234", "b6456");
    testMap.put("c234", "d4565");
    String[] testStr =
        {
            Strings.dispMap(testMap, new ArrayList<>(testMap.keySet())),
            Strings.dispMap(testMap),
            Strings.dispMap(testMap, new ArrayList<>(testMap.keySet()), "-",
                "|", true) };
    for (int i = 0; i < testStr.length; i++) {
      String tStr = testStr[i];
      for (Entry<String, String> mapEntr : testMap.entrySet()) {
        assertTrue(tStr.contains(mapEntr.getValue()));
        // First presentation should only contain the values
        if (i > 0) {
          assertTrue(tStr.contains(mapEntr.getKey()));
        } else {
          assertTrue(!tStr.contains(mapEntr.getKey()));
        }
      }
    }
  }

  /**
   * Test starts with.
   */
  public void testStartsWith() {
    assertFalse(Strings.startsWith("", new char[] {}));
    assertFalse(Strings.startsWith("", new char[] { 'x', 'y' }));
    assertFalse(Strings.startsWith("abc", new char[] {}));
    assertFalse(Strings.startsWith("abc", new char[] { 'b' }));
    assertFalse(Strings.startsWith("abc", new char[] { 'x', 'b' }));
    assertFalse(Strings.startsWith("ábc", new char[] { 'a' }));

    assertTrue(Strings.startsWith("abc", new char[] { 'a' }));
    assertTrue(Strings.startsWith("abc", new char[] { 'x', 'a' }));
    assertTrue(Strings.startsWith("ábc", new char[] { 'á' }));
  }

  /**
   * Test format number for display.
   */
  public void testFormatNumberForDisplay() {
    // a bit hackish, I admit.
    char sep =
        ((DecimalFormat) NumberFormat.getInstance()).getDecimalFormatSymbols()
            .getDecimalSeparator();
    assertEquals("3", Strings.formatNumberForDisplay(3.1415926, 0));
    assertEquals("3" + sep + "142",
        Strings.formatNumberForDisplay(3.1415926, 3));
    assertEquals("3" + sep + "14100", Strings.formatNumberForDisplay(3.141, 5));
  }

  /**
   * Test character count.
   */
  public void testCharacterCount() {
    assertEquals(0, Strings.getCharacterCount("", 'a'));
    assertEquals(0, Strings.getCharacterCount("ax", 'a'));
    assertEquals(1, Strings.getCharacterCount("ba", 'a'));
    assertEquals(3, Strings.getCharacterCount("foo", 'a'));
  }

  /**
   * Tests string -> value conversion.
   */
  public void testStringToValue() {
    Integer two = Strings.stringToValue(Integer.class, "2");
    assertEquals(new Integer(2), two);
    Double pi = Strings.stringToValue(Double.class, "3.1415");
    assertEquals(new Double(3.1415), pi);
    String str = Strings.stringToValue(String.class, "3.1415");
    assertEquals("3.1415", str);
  }

  /**
   * Tests string -> value conversion.
   */
  public void testGetListValues() {
    // list tests
    List<String> csv = Strings.getListValues(String.class, "a,b,c");
    assertEquals(csv.toString(), 3, csv.size());

    List<Integer> csv2 = Strings.getListValues(Integer.class, "1,2,3");
    assertEquals(3, csv2.size());

    String test = "ListItem1";
    List<?> list = Strings.getListValues(String.class, test);
    assertEquals(1, list.size());
    String test2 = "ListItem1,ListItem2";
    List<?> list2 = Strings.getListValues(String.class, test2);
    assertEquals(2, list2.size());
    assertNotNull(list2);
  }
}
