/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.text.CharacterIterator;

import org.jamesii.core.util.CharSequenceCharacterIterator;

import junit.framework.TestCase;

/**
 * Tests the {@link CharSequenceCharacterIterator} class.
 * 
 * @author Johannes Rössel
 */
public class CharSequenceCharacterIteratorTest extends TestCase {

  /** The {@link CharSequence} to use throughout the test. */
  private CharSequence seq;

  /** The iterator to test. */
  private CharSequenceCharacterIterator it;

  @Override
  protected void setUp() {
    this.seq = "abcdefgh";
  }

  @Override
  protected void tearDown() throws Exception {
    this.seq = null;
  }

  /**
   * Tests the
   * {@link CharSequenceCharacterIterator#CharSequenceCharacterIterator(CharSequence)}
   * constructor.
   */
  public void testCtorCharSequence() {
    it = new CharSequenceCharacterIterator(seq);
    assertEquals(0, it.getBeginIndex());
    assertEquals(seq.length(), it.getEndIndex());
    assertEquals(0, it.getIndex());
  }

  /**
   * Tests the
   * {@link CharSequenceCharacterIterator#CharSequenceCharacterIterator(CharSequence, int)}
   * constructor.
   */
  public void testCtorCharSequenceInt() {
    for (int pos : new int[] { 0, 1, 2, 3, 4, 5 }) {
      it = new CharSequenceCharacterIterator(seq, pos);
      assertEquals(0, it.getBeginIndex());
      assertEquals(seq.length(), it.getEndIndex());
      assertEquals(pos, it.getIndex());
    }
  }

  /**
   * Tests whether the
   * {@link CharSequenceCharacterIterator#CharSequenceCharacterIterator(CharSequence, int)}
   * constructor exhibits the correct exceptions when fed with invalid
   * arguments.
   */
  public void testCtorCharSequenceIntExceptions() {
    // negative index in a normal sequence
    try {
      it = new CharSequenceCharacterIterator(seq, -1);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // way out of range index in a normal sequence
    try {
      it = new CharSequenceCharacterIterator(seq, 983457);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // out of range index in an empty sequence
    try {
      it = new CharSequenceCharacterIterator("", 1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  /**
   * Tests the
   * {@link CharSequenceCharacterIterator#CharSequenceCharacterIterator(CharSequence, int, int, int)}
   * constructor.
   */
  public void testCtorCharSequenceIntIntInt() {
    for (int beginIndex : new int[] { 0, 1, 2, 3, 4, 5 }) {
      for (int endIndex : new int[] { 0, 1, 2, 3, 4, 5 }) {
        for (int pos : new int[] { 0, 1, 2, 3, 4, 5 }) {
          if (beginIndex <= endIndex && pos >= beginIndex && pos <= endIndex) {
            it =
                new CharSequenceCharacterIterator(seq, beginIndex, endIndex,
                    pos);
            assertEquals(beginIndex, it.getBeginIndex());
            assertEquals(endIndex, it.getEndIndex());
            assertEquals(pos, it.getIndex());
          }
        }
      }
    }
  }

  public void testCtorCharSequenceIntIntIntExceptions() {
    // beginIndex and endIndex in range, pos outside to the left
    try {
      it = new CharSequenceCharacterIterator(seq, 0, 5, -1);
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      it = new CharSequenceCharacterIterator(seq, 3, 5, 2);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // beginIndex and endIndex in range, pos outside to the right
    try {
      it = new CharSequenceCharacterIterator(seq, 0, 3, 4);
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      it = new CharSequenceCharacterIterator(seq, 3, 5, 2345346);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // beginIndex larger than endIndex
    try {
      it = new CharSequenceCharacterIterator(seq, 5, 3, 4);
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      it = new CharSequenceCharacterIterator(seq, 5, 2, 1);
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      it = new CharSequenceCharacterIterator(seq, 3, 1, 5);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    }

    // beginIndex outside of the sequence to the left
    try {
      it = new CharSequenceCharacterIterator(seq, -1, 2, 0);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // beginIndex outside of the sequence to the right
    try {
      it = new CharSequenceCharacterIterator(seq, 100, 105, 102);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // endIndex outside of the sequence to the left
    try {
      it = new CharSequenceCharacterIterator(seq, -7, -3, -5);
      fail();
    } catch (IllegalArgumentException e) {
    }

    // endIndex outside of the sequence to the right
    try {
      it = new CharSequenceCharacterIterator(seq, 3, 100, 4);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  /**
   * Tests that all constructors react appropriately to the CharSequence being a
   * {@code null} argument.
   */
  public void testCtorNullExceptions() {
    try {
      it = new CharSequenceCharacterIterator(null);
      fail();
    } catch (NullPointerException e) {
    }

    try {
      it = new CharSequenceCharacterIterator(null, 0);
      fail();
    } catch (NullPointerException e) {
    }

    try {
      it = new CharSequenceCharacterIterator(null, 0, 0, 0);
      fail();
    } catch (NullPointerException e) {
    }
  }

  public void testFirst() {
    // Test various indexes (first() should return the character at the begin
    // index, not necessarily at 0).
    int[] beginIndexes = new int[] { 0, 1, 2, 3, 4, 5 };
    for (int beginIndex : beginIndexes) {
      it =
          new CharSequenceCharacterIterator(seq, beginIndex, seq.length(),
              beginIndex);
      assertEquals(seq.charAt(beginIndex), it.first());
      assertEquals(beginIndex, it.getIndex());
      it.next();
      assertEquals(seq.charAt(beginIndex), it.first());
      assertEquals(beginIndex, it.getIndex());
    }

    // Test an empty sequence in which case first() must return DONE.
    it = new CharSequenceCharacterIterator("");
    assertEquals(CharacterIterator.DONE, it.first());
    assertEquals(0, it.getIndex());
  }

  public void testLast() {
    // Test various end indexes (last() should return the last character within
    // the range, not necessarily in the sequence).
    int[] endIndexes = new int[] { 1, 2, 3, 4, 5 };
    for (int endIndex : endIndexes) {
      it = new CharSequenceCharacterIterator(seq, 0, endIndex, 0);
      assertEquals(seq.charAt(endIndex - 1), it.last());
      assertEquals(endIndex - 1, it.getIndex());
      it.previous();
      assertEquals(seq.charAt(endIndex - 1), it.last());
      assertEquals(endIndex - 1, it.getIndex());
    }

    // Test an empty sequence in which case last() must return DONE.
    it = new CharSequenceCharacterIterator("");
    assertEquals(CharacterIterator.DONE, it.last());
    assertEquals(0, it.getIndex());
  }

  public void testCurrent() {
    int[] indexes = new int[] { 0, 1, 2, 3, 4, 5, 6 };
    for (int index : indexes) {
      it = new CharSequenceCharacterIterator(seq, 0, seq.length(), index);
      assertEquals(seq.charAt(index), it.current());
      assertEquals(index, it.getIndex());
      it.next();
      assertEquals(seq.charAt(index + 1), it.current());
      assertEquals(index + 1, it.getIndex());
    }

    it.last();
    assertEquals(seq.charAt(seq.length() - 1), it.current());
    it.next();
    assertEquals(CharacterIterator.DONE, it.current());
  }

  public void testNext() {
    it = new CharSequenceCharacterIterator(seq);
    assertEquals(seq.charAt(0), it.current());
    assertEquals(0, it.getIndex());
    assertEquals(seq.charAt(1), it.next());
    assertEquals(1, it.getIndex());
    assertEquals(seq.charAt(2), it.next());
    assertEquals(2, it.getIndex());
    assertEquals(seq.charAt(3), it.next());
    assertEquals(3, it.getIndex());
    assertEquals(seq.charAt(4), it.next());
    assertEquals(4, it.getIndex());
    assertEquals(seq.charAt(5), it.next());
    assertEquals(5, it.getIndex());
    assertEquals(seq.charAt(6), it.next());
    assertEquals(6, it.getIndex());

    // Forward to the last character of the sequence. next() should
    // return DONE, then.
    it.last();
    it.next();
    int endIndex = it.getIndex();
    assertEquals(CharacterIterator.DONE, it.next());
    // next() shouldn't change the index when running off the end.
    assertEquals(endIndex, it.getIndex());
  }

  public void testPrevious() {
    it = new CharSequenceCharacterIterator(seq);
    it.last();
    int lastIndex = it.getIndex();
    assertEquals(seq.charAt(lastIndex), it.current());
    assertEquals(seq.charAt(lastIndex - 1), it.previous());
    assertEquals(lastIndex - 1, it.getIndex());
    assertEquals(seq.charAt(lastIndex - 2), it.previous());
    assertEquals(lastIndex - 2, it.getIndex());
    assertEquals(seq.charAt(lastIndex - 3), it.previous());
    assertEquals(lastIndex - 3, it.getIndex());
    assertEquals(seq.charAt(lastIndex - 4), it.previous());
    assertEquals(lastIndex - 4, it.getIndex());
    assertEquals(seq.charAt(lastIndex - 5), it.previous());
    assertEquals(lastIndex - 5, it.getIndex());
    assertEquals(seq.charAt(lastIndex - 6), it.previous());
    assertEquals(lastIndex - 6, it.getIndex());

    // Rewind to the first character of the sequence. previous() should
    // return DONE, then.
    it.first();
    assertEquals(CharacterIterator.DONE, it.previous());
    // previous() shouldn't change the index when running off the left end.
    assertEquals(0, it.getIndex());
  }

  public void testSetIndex() {
    int[] indexes = new int[] { 0, 1, 2, 3, 4, 5, 6 };
    it = new CharSequenceCharacterIterator(seq);
    for (int index : indexes) {
      assertEquals(seq.charAt(index), it.setIndex(index));
      assertEquals(index, it.getIndex());
    }

    assertEquals(CharacterIterator.DONE, it.setIndex(it.getEndIndex()));
    assertEquals(it.getEndIndex(), it.getIndex());
  }

  public void testSetIndexExceptions() {
    it = new CharSequenceCharacterIterator(seq);
    try {
      it.setIndex(-1);
      fail();
    } catch (IllegalArgumentException e) {
    }

    try {
      it.setIndex(200);
    } catch (IllegalArgumentException e) {
    }
  }

  public void testGetBeginIndex() {
    int[] beginIndexes = new int[] { 0, 1, 2, 3, 4, 5, 6 };
    for (int beginIndex : beginIndexes) {
      it =
          new CharSequenceCharacterIterator(seq, beginIndex, seq.length(),
              beginIndex);
      assertEquals(beginIndex, it.getBeginIndex());
    }
  }

  public void testGetEndIndex() {
    int[] endIndexes = new int[] { 1, 2, 3, 4, 5, 6 };
    for (int endIndex : endIndexes) {
      it = new CharSequenceCharacterIterator(seq, 0, endIndex, 0);
      assertEquals(endIndex, it.getEndIndex());
    }
  }

  public void testGetIndex() {
    it = new CharSequenceCharacterIterator(seq);
    assertEquals(0, it.getIndex());
    it.next();
    assertEquals(1, it.getIndex());
    it.next();
    assertEquals(2, it.getIndex());
    it.setIndex(4);
    assertEquals(4, it.getIndex());
  }

  public void testClone() {
    for (int beginIndex : new int[] { 0, 1, 2 }) {
      for (int endIndex : new int[] { 4, 5, 6 }) {
        // Try it with an iterator from start to finish.
        it =
            new CharSequenceCharacterIterator(seq, beginIndex, endIndex,
                beginIndex + 1);

        // Clone the iterator, should have the same sequence, the same begin and
        // end positions, the same index.
        CharSequenceCharacterIterator it2 =
            (CharSequenceCharacterIterator) it.clone();
        CharSequenceCharacterIterator it3 =
            (CharSequenceCharacterIterator) it.clone();

        assertEquals(it.getBeginIndex(), it2.getBeginIndex());
        assertEquals(it.getEndIndex(), it2.getEndIndex());
        assertEquals(it.getIndex(), it2.getIndex());

        while (it.next() != CharacterIterator.DONE) {
          assertEquals(it.current(), it2.next());
        }

        // The second clone shouldn't have changed.
        assertEquals(it.getBeginIndex(), it3.getBeginIndex());
        assertEquals(it.getEndIndex(), it3.getEndIndex());
        assertEquals(it.getBeginIndex() + 1, it3.getIndex());
      }
    }
  }
}
