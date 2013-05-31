/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.text.CharacterIterator;

/**
 * An implementation of the {@link CharacterIterator} interface that iterates
 * over a {@link CharSequence}.
 * 
 * @author Johannes Rössel
 */
public class CharSequenceCharacterIterator implements CharacterIterator {

  private CharSequence sequence;

  private int index;

  private int begin;

  private int end;

  public CharSequenceCharacterIterator(CharSequence seq) {
    this(seq, 0);
  }

  public CharSequenceCharacterIterator(CharSequence seq, int index) {
    this(seq, 0, seq.length(), index);
  }

  public CharSequenceCharacterIterator(CharSequence sequence, int begin,
      int end, int index) {
    if (sequence == null) {
      throw new NullPointerException("sequence may not be null.");
    }
    if (begin < 0 || begin > sequence.length()) {
      throw new IllegalArgumentException(
          "begin may not be outside the CharSequence.");
    }
    if (end > sequence.length() || end < 0) {
      throw new IllegalArgumentException("end may not be outside the sequence.");
    }
    if (index < begin || index > end) {
      throw new IllegalArgumentException(
          "index may not be outside the range specified by begin and end.");
    }

    this.sequence = sequence;
    this.begin = begin;
    this.end = end;
    this.index = index;
  }

  @Override
  public char first() {
    if (begin == end) {
      return DONE;
    }

    index = getBeginIndex();
    return current();
  }

  @Override
  public char last() {
    if (begin == end) {
      return DONE;
    }

    index = getEndIndex() - 1;
    return current();
  }

  @Override
  public char current() {
    if (getIndex() >= getEndIndex()) {
      return DONE;
    }

    return sequence.charAt(index);
  }

  @Override
  public char next() {
    index++;

    if (getIndex() >= getEndIndex()) {
      index = getEndIndex();
      return DONE;
    }

    return current();
  }

  @Override
  public char previous() {
    if (getIndex() == getBeginIndex()) {
      return DONE;
    }

    index--;

    return current();
  }

  @Override
  public char setIndex(int position) {
    if (position < getBeginIndex() || position > getEndIndex()) {
      throw new IllegalArgumentException(
          "position may only range from getBeginIndex() to getEndIndex().");
    }

    index = position;

    if (position == getEndIndex()) {
      return DONE;
    }

    return sequence.charAt(position);
  }

  @Override
  public int getBeginIndex() {
    return begin;
  }

  @Override
  public int getEndIndex() {
    return end;
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public Object clone() {
    return new CharSequenceCharacterIterator(sequence, begin, end, index);
  }
}
