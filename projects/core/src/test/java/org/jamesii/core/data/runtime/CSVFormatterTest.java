/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import org.jamesii.core.data.runtime.CSVFormatException;

import junit.framework.TestCase;
import static org.jamesii.core.data.runtime.CSVFormatter.*;

public class CSVFormatterTest extends TestCase {

  public void testFormatCSVThrowsExceptionIfFieldsWasNull() {
    try {
      formatCSV(null, ',');
      fail();
    } catch (NullPointerException e) {
    }
  }

  public void testFormatCSVThrowsExceptionIfSingleFieldWasNull() {
    try {
      formatCSV(new String[] { "a", null, "c" }, ',');
      fail();
    } catch (NullPointerException e) {
    }
  }

  public void testFormatCSVReturnsEmptyStringIfFieldsWasEmpty() {
    assertEquals("", formatCSV(new String[] {}, ','));
  }

  public void testFormatCSVReturnsEmptyStringIfFieldsContainedOnlyEmptyString() {
    assertEquals("", formatCSV(new String[] { "" }, ','));
  }

  public void testFormatCSVMultipleEmptyStrings() {
    assertEquals(",,", formatCSV(new String[] { "", "", "" }, ','));
  }

  public void testFormatCSVMultipleStringsConsistingOfDelimitersAndQuotes() {
    assertEquals("\",\",\",,\",\"\"\"\"\",\"\"\"\",,\",",
        formatCSV(new String[] { ",", ",,", "\"\",\"\",,", "" }, ','));
  }

  public void testFormatCSVReturnsFieldIfThereIsOnlyOneField() {
    assertEquals("a", formatCSV(new String[] { "a" }, ','));
  }

  public void testFormatCSVMultipleFieldsNoQuotesNecessary() {
    assertEquals("a,b,c", formatCSV(new String[] { "a", "b", "c" }, ','));
  }

  public void testFormatCSVMultipleFieldsQuotesNecessary() {
    assertEquals("\"a,x\",b,c",
        formatCSV(new String[] { "a,x", "b", "c" }, ','));
  }

  public void testFormatCSVMultipleFieldsNoQuotesNecessaryFunkyDelimiter() {
    assertEquals("aAbAc", formatCSV(new String[] { "a", "b", "c" }, 'A'));
  }

  public void testFormatCSVMultipleFieldsNoQuotesNecessaryFunkyDelimiter2() {
    assertEquals("a,xAbAc", formatCSV(new String[] { "a,x", "b", "c" }, 'A'));
  }

  public void testFormatCSVMultipleFieldsQuotesNecessaryFunkyDelimiter() {
    assertEquals("\"a\"abac", formatCSV(new String[] { "a", "b", "c" }, 'a'));
  }

  public void testFormatCSVMultipleFieldsQuotesAlwaysNecessary() {
    assertEquals("\"a,b\",\"b,c\",\"c,d\"",
        formatCSV(new String[] { "a,b", "b,c", "c,d" }, ','));
  }

  public void testFormatCSVSingleFieldEmbeddedQuotes() {
    assertEquals("\"a\"\"b\"", formatCSV(new String[] { "a\"b" }, ','));
  }

  public void testFormatCSVMultipleFieldsEmbeddedQuotes() {
    assertEquals("\"a\"\"b\",\"This is \"\"some\"\" test\"",
        formatCSV(new String[] { "a\"b", "This is \"some\" test" }, ','));
  }

  public void testFormatCSVThrowsExceptionOnEmbeddedLF() {
    try {
      formatCSV(new String[] { "a", "foo\nbar" }, ',');
      fail();
    } catch (CSVFormatException e) {
    }
  }

  public void testFormatCSVThrowsExceptionOnEmbeddedCR() {
    try {
      formatCSV(new String[] { "a", "foo\rbar" }, ',');
      fail();
    } catch (CSVFormatException e) {
    }
  }

  public void testFormatCSVThrowsExceptionOnEmbeddedCRLF() {
    try {
      formatCSV(new String[] { "a", "foo\r\nbar" }, ',');
      fail();
    } catch (CSVFormatException e) {
    }
  }

  public void testQuoteNoChangesNecessaryEmptyString() {
    assertEquals("", quote("", ','));
  }

  public void testQuoteNoChangesNecessary() {
    assertEquals("a", quote("a", ','));
  }

  public void testQuoteEmbeddedQuotes() {
    assertEquals("\"a\"\"b\"", quote("a\"b", ','));
  }

  public void testQuoteEmbeddedDelimiter() {
    assertEquals("\"a,b\"", quote("a,b", ','));
  }

  public void testQuoteEmbeddedDelimiterFunkyDelimiter() {
    assertEquals("\"a\"", quote("a", 'a'));
  }

  public void testQuoteThrowsExceptionOnEmbeddedLF() {
    try {
      quote("foo\nbar", ',');
      fail();
    } catch (CSVFormatException e) {
    }
  }

  public void testQuoteThrowsExceptionOnEmbeddedCR() {
    try {
      quote("foo\rbar", ',');
      fail();
    } catch (CSVFormatException e) {
    }
  }

  public void testQuoteThrowsExceptionOnEmbeddedCRLF() {
    try {
      quote("foo\r\nbar", ',');
      fail();
    } catch (CSVFormatException e) {
    }
  }

}
