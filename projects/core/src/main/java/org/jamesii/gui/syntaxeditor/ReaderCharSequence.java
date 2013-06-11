/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.IOException;
import java.io.Reader;

/**
 * Utility class providing a {@link CharSequence} from a given {@link Reader}.
 * 
 * @author Stefan Rybacki
 */
public class ReaderCharSequence implements CharSequence {
  /**
   * internally used string builder to concatenate what is read from the given
   * {@link Reader}
   */
  private final StringBuilder builder = new StringBuilder();

  /**
   * A Wrapper that takes a {@link Reader} and creates a {@link CharSequence}
   * from it. Therefore the specified reader is read until its end and all read
   * characters are added to the internal char sequence.<br/>
   * Note: {@link Reader#close()} is not called on the specified reader
   * 
   * @param reader
   *          the reader to create the {@link CharSequence} from
   */
  public ReaderCharSequence(Reader reader) {
    if (reader == null) {
      throw new IllegalArgumentException("reader can't be null!");
    }

    try {
      char[] cbuf = new char[1024];
      int read;
      while ((read = reader.read(cbuf, 0, 1024)) >= 0) {
        builder.append(cbuf, 0, read);
      }
    } catch (IOException e) {
    }
  }

  @Override
  public char charAt(int index) {
    return builder.charAt(index);
  }

  @Override
  public int length() {
    return builder.length();
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    return builder.subSequence(start, end);
  }

}
