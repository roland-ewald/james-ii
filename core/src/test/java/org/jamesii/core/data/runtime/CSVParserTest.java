/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import org.jamesii.core.data.runtime.CSVFormatException;
import org.jamesii.core.data.runtime.CSVParser;

import junit.framework.TestCase;

/**
 * Tests the {@link CSVParser} class.
 * 
 * @author Johannes Rössel
 */
public class CSVParserTest extends TestCase {

  private CSVParser parser;

  @Override
  protected void setUp() throws Exception {
    this.parser = new CSVParser();
  }

  @Override
  protected void tearDown() throws Exception {
    this.parser = null;
  }

  private void checkFields(String[] fields, String... expectedFields) {
    assertEquals(expectedFields.length, fields.length);
    for (int i = 0; i < fields.length; i++) {
      assertEquals(expectedFields[i], fields[i]);
    }
  }

  public void testSingleEmptyFieldNoQuotes() {
    checkFields(parser.parse(""), "");
  }

  public void testSingleEmptyFieldQuotes() {
    checkFields(parser.parse("\"\""), "");
  }

  public void testSingleSpaceFieldNoQuotes() {
    checkFields(parser.parse(" "), " ");
  }

  public void testSingleSpaceFieldQuotes() {
    checkFields(parser.parse("\" \""), " ");
  }

  public void testTwoFieldsEmptyFirstFieldNoQuotes() {
    checkFields(parser.parse(",a"), "", "a");
  }

  public void testTwoFieldsEmptyFirstFieldQuotedFirstField() {
    checkFields(parser.parse("\"\",a"), "", "a");
  }

  public void testTwoFieldsEmptyFirstFieldQuotedSecondField() {
    checkFields(parser.parse(",\"a\""), "", "a");
  }

  public void testTwoFieldsEmptySecondFieldNoQuotes() {
    checkFields(parser.parse("a,"), "a", "");
  }

  public void testTwoFieldsEmptySecondFieldQuotedFirstField() {
    checkFields(parser.parse("\"a\","), "a", "");
  }

  public void testTwoFieldsEmptySecondFieldQuotedSecondField() {
    checkFields(parser.parse("a,\"\""), "a", "");
  }

  public void testSingleFieldNoQuotes() {
    checkFields(parser.parse("Field 1"), "Field 1");
  }

  public void testSingleFieldQuotesNoQuotedCharacters() {
    checkFields(parser.parse("\"Field 1\""), "Field 1");
  }

  public void testSingleFieldQuotesQuotedSeparator() {
    checkFields(parser.parse("\"Field, 1\""), "Field, 1");
  }

  public void testSingleFieldQuotesQuotedQuote() {
    checkFields(parser.parse("\"Field\"\" 1\""), "Field\" 1");
  }

  public void testTwoFieldsNoQuotesCommaSeparated() {
    checkFields(parser.parse("Field 1,Field 2"), "Field 1", "Field 2");
  }

  public void testTwoFieldsNoQuotesTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("Field 1\tField 2"), "Field 1", "Field 2");
  }

  public void testTwoFieldsNoQuotesSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("Field1 Field2"), "Field1", "Field2");
  }

  public void testTwoFieldsNoQuotesSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("Field 1;Field 2"), "Field 1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedNoQuotedCharactersCommaSeparated() {
    checkFields(parser.parse("\"Field 1\",Field 2"), "Field 1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedNoQuotedCharactersTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("\"Field 1\"\tField 2"), "Field 1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedNoQuotedCharactersSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field1\" Field2"), "Field1", "Field2");
  }

  public void testTwoFieldsFirstQuotedNoQuotedCharactersSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field 1\";Field 2"), "Field 1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedQuotedDelimiterCommaSeparated() {
    checkFields(parser.parse("\"Field,1\",Field 2"), "Field,1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedQuotedDelimiterTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("\"Field\t1\"\tField 2"), "Field\t1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedQuotedDelimiterSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field 1\" Field2"), "Field 1", "Field2");
  }

  public void testTwoFieldsFirstQuotedQuotedDelimiterSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field;1\";Field 2"), "Field;1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedQuotedQuoteCommaSeparated() {
    checkFields(parser.parse("\"Field\"\"1\",Field 2"), "Field\"1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedQuotedQuoteTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("\"Field\"\"1\"\tField 2"), "Field\"1", "Field 2");
  }

  public void testTwoFieldsFirstQuotedQuotedQuoteSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field\"\"1\" Field2"), "Field\"1", "Field2");
  }

  public void testTwoFieldsFirstQuotedQuotedQuoteSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field\"\"1\";Field 2"), "Field\"1", "Field 2");
  }

  public void testTwoFieldsSecondQuotedNoQuotedCharactersCommaSeparated() {
    checkFields(parser.parse("Field 1,\"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsSecondQuotedNoQuotedCharactersTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("Field 1\t\"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsSecondQuotedNoQuotedCharactersSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("Field1 \"Field2\""), "Field1", "Field2");
  }

  public void testTwoFieldsSecondQuotedNoQuotedCharactersSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("Field 1;\"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsSecondQuotedQuotedDelimiterCommaSeparated() {
    checkFields(parser.parse("Field 1,\"Field,2\""), "Field 1", "Field,2");
  }

  public void testTwoFieldsSecondQuotedQuotedDelimiterTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("Field 1\t\"Field\t2\""), "Field 1", "Field\t2");
  }

  public void testTwoFieldsSecondQuotedQuotedDelimiterSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("Field1 \"Field 2\""), "Field1", "Field 2");
  }

  public void testTwoFieldsSecondQuotedQuotedDelimiterSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("Field 1;\"Field;2\""), "Field 1", "Field;2");
  }

  public void testTwoFieldsSecondQuotedQuotedQuoteCommaSeparated() {
    checkFields(parser.parse("Field 1,\"Field\"\"2\""), "Field 1", "Field\"2");
  }

  public void testTwoFieldsSecondQuotedQuotedQuoteTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("Field 1\t\"Field\"\"2\""), "Field 1", "Field\"2");
  }

  public void testTwoFieldsSecondQuotedQuotedQuoteSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("Field1 \"Field\"\"2\""), "Field1", "Field\"2");
  }

  public void testTwoFieldsSecondQuotedQuotedQuoteSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("Field 1;\"Field\"\"2\""), "Field 1", "Field\"2");
  }

  public void testTwoFieldsBothQuotedNoQuotedCharactersCommaSeparated() {
    checkFields(parser.parse("\"Field 1\",\"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsBothQuotedNoQuotedCharactersTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("\"Field 1\"\t\"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsBothQuotedNoQuotedCharactersSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field1\" \"Field2\""), "Field1", "Field2");
  }

  public void testTwoFieldsBothQuotedNoQuotedCharactersSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field 1\";\"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsBothQuotedQuotedDelimiterCommaSeparated() {
    checkFields(parser.parse("\"Field,1\",\"Field,2\""), "Field,1", "Field,2");
  }

  public void testTwoFieldsBothQuotedQuotedDelimiterTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("\"Field\t1\"\t\"Field\t2\""), "Field\t1",
        "Field\t2");
  }

  public void testTwoFieldsBothQuotedQuotedDelimiterSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field 1\" \"Field 2\""), "Field 1", "Field 2");
  }

  public void testTwoFieldsBothQuotedQuotedDelimiterSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field;1\";\"Field;2\""), "Field;1", "Field;2");
  }

  public void testTwoFieldsBothQuotedQuotedQuoteCommaSeparated() {
    checkFields(parser.parse("\"Field\"\"1\",\"Field\"\"2\""), "Field\"1",
        "Field\"2");
  }

  public void testTwoFieldsBothQuotedQuotedQuoteTabSeparated() {
    parser.setDelimiter('\t');
    checkFields(parser.parse("\"Field\"\"1\"\t\"Field\"\"2\""), "Field\"1",
        "Field\"2");
  }

  public void testTwoFieldsBothQuotedQuotedQuoteSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field\"\"1\" \"Field\"\"2\""), "Field\"1",
        "Field\"2");
  }

  public void testTwoFieldsBothQuotedQuotedQuoteSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field\"\"1\";\"Field\"\"2\""), "Field\"1",
        "Field\"2");
  }

  public void testMultiFieldsCommaSeparated() {
    checkFields(parser.parse("\"Field\"\"1\",\"Field,2\",Field 3,col4"),
        "Field\"1", "Field,2", "Field 3", "col4");
  }

  public void testMultiFieldsSpaceSeparated() {
    parser.setDelimiter(' ');
    checkFields(parser.parse("\"Field\"\"1\" \"Field 2\" Field3 col4"),
        "Field\"1", "Field 2", "Field3", "col4");
  }

  public void testMultiFieldsSemicolonSeparated() {
    parser.setDelimiter(';');
    checkFields(parser.parse("\"Field\"\"1\";\"Field;2\";Field 3;col4"),
        "Field\"1", "Field;2", "Field 3", "col4");
  }

  public void testLeadingSpaceSingleField() {
    checkFields(parser.parse(" foo"), " foo");
  }

  public void testTrailingSpaceSingleField() {
    checkFields(parser.parse("foo "), "foo ");
  }

  public void testLeadingSpaceTwoFieldsFirst() {
    checkFields(parser.parse(" foo,b"), " foo", "b");
  }

  public void testTrailingSpaceTwoFieldsFirst() {
    checkFields(parser.parse("foo ,b"), "foo ", "b");
  }

  public void testLeadingSpaceTwoFieldsSecond() {
    checkFields(parser.parse("b, foo"), "b", " foo");
  }

  public void testTrailingSpaceTwoFieldsSecond() {
    checkFields(parser.parse("b,foo "), "b", "foo ");
  }

  public void testLeadingSpaceThreeFieldsMiddle() {
    checkFields(parser.parse("b, foo,a"), "b", " foo", "a");
  }

  public void testTrailingSpaceThreeFieldsMiddle() {
    checkFields(parser.parse("b,foo ,a"), "b", "foo ", "a");
  }

  public void testExceptionThrownOnEndOfLineWithinQuotedStringSingleField() {
    try {
      parser.parse("\"foo");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(4, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnEndOfLineWithinQuotedStringTwoFields() {
    try {
      parser.parse("a,\"foo");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(6, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnEndOfLineWithinQuotedStringMultiFields() {
    try {
      parser.parse("\"a\",b,\"foo");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(10, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnExtraDataAfterQuotedStringSingleField() {
    try {
      parser.parse("\"foo\"a");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(5, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnExtraDataAfterQuotedStringTwoFields() {
    try {
      parser.parse("a,\"foo\"a");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(7, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnExtraDataAfterQuotedStringMultiFields() {
    try {
      parser.parse("a,\"b\",\"foo\"a");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(11, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnStrayQuoteInUnquotedStringSingleField() {
    try {
      parser.parse("foo\"bar");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(3, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnStrayQuoteInUnquotedStringTwoFields() {
    try {
      parser.parse("a,foo\"bar");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(5, e.getErrorPosition());
    }
  }

  public void testExceptionThrownOnStrayQuoteInUnquotedStringMultiFields() {
    try {
      parser.parse("a,\"b\",foo\"bar");
      fail();
    } catch (CSVFormatException e) {
      assertEquals(9, e.getErrorPosition());
    }
  }

}