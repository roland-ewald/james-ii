/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.io.Serializable;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.CharSequenceCharacterIterator;

/**
 * Helper class for parsing CSV files according to the usual rules. Those are:
 * <ul>
 * <li>Fields in a line are separated by a single character, usually a comma (
 * {@code ,}) (but might differ in regional variants).
 * <li>A field might appear simply as text ({@code foo}) or quoted with
 * double-quotes ({@code "foo"}).
 * <li>Within a quoted field the field delimiter is a valid character to use (
 * {@code "foo,bar"}). To use the delimiter in a field, it <em>must</em> be
 * quoted.
 * <li>Within a quoted field a single double-quote must be escaped by doubling
 * it ({@code "Some ""quoted"" value"}). To embed a double-quote within a field,
 * it <em>must</em> be quoted.
 * <li>Leading or trailing spaces are part of the field (<a
 * href='http://tools.ietf.org/html/rfc4180'>RFC 4180</a>:
 * <em>“Spaces are considered part of a field and should not be ignored.”</em>)
 * </ul>
 * There is one exception to the commonly-accepted parsing rules: Embedded line
 * breaks in a quoted field are not parsed but instead interpreted as the
 * premature end of a record. This was a deliberate decision given the scope of
 * this parser and the fact that it parses only a single line of input.
 * 
 * @author Johannes Rössel
 */
public class CSVParser implements Serializable {

  /**
   * The constant serial version uid.
   */
  private static final long serialVersionUID = 2291688330975302205L;

  /** Backing field for the delimiter to use when parsing a line. */
  private char delimiter;

  /**
   * Initialises an instance of the {@link CSVParser} class with the comma (
   * {@code ,}) as field delimiter.
   */
  public CSVParser() {
    this(',');
  }

  /**
   * Initialises an instance of the {@link CSVParser} class with the given field
   * delimiter.
   * 
   * @param delimiter
   *          The field delimiter to use. Common values are comma ({@code ,}) or
   *          semicolon ({@code ;}).
   */
  public CSVParser(char delimiter) {
    this.setDelimiter(delimiter);
  }

  /**
   * Sets the delimiter for parsing lines from the CSV file.
   * 
   * @param delimiter
   *          The new delimiter.
   */
  public void setDelimiter(char delimiter) {
    this.delimiter = delimiter;
  }

  /**
   * Returns the delimiter used for parsing lines of the CSV file.
   * 
   * @return The delimiter.
   */
  public char getDelimiter() {
    return delimiter;
  }

  /**
   * Parses a line according to the usual conventions and standards for CSV
   * files. This includes support for quoted fields and nested quotes, yet
   * embedded line breaks will not work (since this method only parses a single
   * line).
   * <p>
   * The line <blockquote> <code>abc,"def","foo,bar","baz""gak"</code>
   * </blockquote> will be parsed into <blockquote>
   * <code>['abc', 'def', 'foo,bar', 'baz"gak']</code></blockquote>
   * 
   * @param line
   *          The line from the CSV file to parse.
   * @return A String array, containing the individual fields.
   * @throws CSVFormatException
   *           if a parsing error occurs. Such an error can be one of the
   *           following conditions:
   *           <ul>
   *           <li>A double-quote character ({@code "}) appears outside a quoted
   *           field ({@code foo"bar}). <li>Additional characters are
   *           encountered outside a quoted string ({@code "foo"bar}). <li> The
   *           end of the line is encountered within a quoted string (
   *           {@code "foo}). Technically this is not an error condition per se,
   *           but rather a feature of CSV files not supported by this parser
   *           (since it operates on single lines of input).
   *           </ul>
   */
  public String[] parse(CharSequence line) {
    List<String> tokens = new ArrayList<>();
    CharacterIterator ci = new CharSequenceCharacterIterator(line);
    StringBuilder tok = new StringBuilder();
    State state = State.BEGIN;
    loop: for (char c = ci.first();; c = ci.next()) {
      switch (c) {
      case '"':
        switch (state) {
        case BEGIN:
          // start of a quoted string
          state = State.QUOTEDSTRING;
          break;
        case QUOTEDSTRING:
          // either end of the quoted string or start of a quoted
          // quote
          char c2 = ci.next();
          if (c2 == '"') {
            // double double-quotes – a quoted double-quote
            tok.append('"');
          } else if (c2 == CharacterIterator.DONE || c2 == delimiter) {
            // terminating quote for the quoted string
            tokens.add(tok.toString());
            tok = new StringBuilder();
            state = State.BEGIN;
            // avoid running the main loop body over the DONE (which would add
            // the now empty token to the list of tokens although no token
            // started)
            if (c2 == CharacterIterator.DONE) {
              break loop;
            }
          } else {
            throw new CSVFormatException(
                "Data encountered outside a quoted string.", ci.getIndex());
          }
          break;
        case STRING:
          // stray double-quote in the middle of an unquoted string
          throw new CSVFormatException(
              "Stray double-quote within unquoted field", ci.getIndex());
        default:
        }
        break;
      case CharacterIterator.DONE:
        if (state == State.QUOTEDSTRING) {
          // end of string in the middle of a quoted string – not
          // good
          throw new CSVFormatException(
              "Encountered end of line in a quoted string.", ci.getIndex());
        }
        tokens.add(tok.toString());
        break loop;
      default:
        if (c == getDelimiter()) {
          if (state == State.QUOTEDSTRING) {
            tok.append(c);
          } else {
            state = State.BEGIN;
            tokens.add(tok.toString());
            tok = new StringBuilder();
          }
          break;
        }
        if (state == State.BEGIN) {
          state = State.STRING;
        }
        if (state == State.STRING || state == State.QUOTEDSTRING) {
          tok.append(c);
        }
        break;
      }
    }

    return tokens.toArray(new String[] {});
  }

  /**
   * State enumeration for the parser.
   * 
   * @author Johannes Rössel
   */
  private enum State {
    /** Start state for a single field. */
    BEGIN,
    /** State while reading an unquoted field. */
    STRING,
    /** State while reading a quoted field. */
    QUOTEDSTRING
  }
}
