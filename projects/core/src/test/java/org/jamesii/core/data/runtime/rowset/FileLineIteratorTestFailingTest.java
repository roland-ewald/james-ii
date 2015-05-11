/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.jamesii.core.data.runtime.rowset.FileLineIterator;
import org.jamesii.core.util.StopWatch;

import junit.framework.TestCase;

/**
 * Tests for the {@link FileLineIterator} class.
 * 
 * @author Johannes Rössel
 */
public class FileLineIteratorTestFailingTest extends TestCase {
  private FileLineIterator it;

  private static File blank;

  private static File singleline;

  private static File small;

  private static File large;

  static {
    blank = createTestFile("blank", 0);
    singleline = createTestFile("singleline", 1);
    small = createTestFile("small", 10000);
    large = createTestFile("large", 10000000);
  }

  private static File createTestFile(String name, int numLines) {
    try {
      File f = File.createTempFile("MMFLITest_" + name, ".txt");
      f.deleteOnExit();
      try (BufferedWriter w =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
              Charset.forName("UTF-8")))) {
        for (int i = 0; i < numLines; i++) {
          w.write("This is line ");
          w.write(Integer.toString(i));
          w.write(".\r\n");
        }
      }
      return f;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  void setUp(File f) {
    try {
      it = new FileLineIterator(f);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void tearDown() throws Exception {
    if (it != null) {
      it.close();
      it = null;
    }
    Thread.sleep(100);
  }

  public void testInvokingCloseTwiceDoesNotThrowException() throws Exception {
    setUp(small);
    it.close();
    it.close();
  }

  public void testCloseClosesFileToo() throws Exception {
    setUp(small);
    RandomAccessFile f = it.getFile();
    it.close();
    try {
      f.read();
      fail();
    } catch (IOException e) {
    }
  }

  public void testReadFirstLineWithCurrentSmallFile() {
    setUp(small);
    assertEquals("This is line 0.", it.current().toString());
  }

  public void testReadFirstLineWithCurrentLargeFile() {
    setUp(large);
    assertEquals("This is line 0.", it.current().toString());
  }

  public void testReadSecondLineWithNextSmallFile() {
    setUp(small);
    assertEquals("This is line 1.", it.next().toString());
  }

  public void testReadSecondLineWithNextLargeFile() {
    setUp(large);
    assertEquals("This is line 1.", it.next().toString());
  }

  public void testReadThirdLineWithNextSmallFile() {
    setUp(small);
    it.next();
    assertEquals("This is line 2.", it.next().toString());
  }

  public void testReadThirdLineWithNextLargeFile() {
    setUp(large);
    it.next();
    assertEquals("This is line 2.", it.next().toString());
  }

  public void testReadFirstLineWithRelativeSmallFile() {
    setUp(small);
    assertEquals("This is line 0.", it.relative(0).toString());
  }

  public void testReadFirstLineWithRelativeLargeFile() {
    setUp(large);
    assertEquals("This is line 0.", it.relative(0).toString());
  }

  public void testReadSecondLineWithRelativeForwardsSmallFile() {
    setUp(small);
    assertEquals("This is line 1.", it.relative(1).toString());
  }

  public void testReadSecondLineWithRelativeForwardsLargeFile() {
    setUp(large);
    assertEquals("This is line 1.", it.relative(1).toString());
  }

  public void testReadThirdLineWithRelativeForwardsSmallFile() {
    setUp(small);
    assertEquals("This is line 2.", it.relative(2).toString());
  }

  public void testReadThirdLineWithRelativeForwardsLargeFile() {
    setUp(large);
    assertEquals("This is line 2.", it.relative(2).toString());
  }

  public void testReadFirstLineWithAbsoluteSmallFile() {
    setUp(small);
    assertEquals("This is line 0.", it.absolute(0).toString());
  }

  public void testReadFirstLineWithAbsoluteLargeFile() {
    setUp(large);
    assertEquals("This is line 0.", it.absolute(0).toString());
  }

  public void testReadSecondLineWithAbsoluteSmallFile() {
    setUp(small);
    assertEquals("This is line 1.", it.absolute(1).toString());
  }

  public void testReadSecondLineWithAbsoluteLargeFile() {
    setUp(large);
    assertEquals("This is line 1.", it.absolute(1).toString());
  }

  public void testReadThirdLineWithAbsoluteSmallFile() {
    setUp(small);
    assertEquals("This is line 2.", it.absolute(2).toString());
  }

  public void testReadThirdLineWithAbsoluteLargeFile() {
    setUp(large);
    assertEquals("This is line 2.", it.absolute(2).toString());
  }

  public void testReadFirstLineWithRelative1BackwardsSmallFile() {
    setUp(small);
    it.next();
    assertEquals("This is line 0.", it.relative(-1).toString());
  }

  public void testReadFirstLineWithRelative1BackwardsLargeFile() {
    setUp(large);
    it.next();
    assertEquals("This is line 0.", it.relative(-1).toString());
  }

  public void testReadFirstLineWithRelative2BackwardsSmallFile() {
    setUp(small);
    it.next();
    it.next();
    assertEquals("This is line 0.", it.relative(-2).toString());
  }

  public void testReadFirstLineWithRelative2BackwardsLargeFile() {
    setUp(large);
    it.next();
    it.next();
    assertEquals("This is line 0.", it.relative(-2).toString());
  }

  public void testPreviousAfterAbsoluteGoesToCorrectLine() {
    setUp(large);
    it.absolute(50);
    assertEquals("This is line 49.", it.previous().toString());
  }

  public void testPreviousAfterRelativeGoesToCorrectLine() {
    setUp(large);
    it.relative(50);
    assertEquals("This is line 49.", it.previous().toString());
  }

  public void testPreviousAfterRelativeNextRelativeGoesToCorrectLine() {
    setUp(large);
    it.relative(50);
    it.next();
    it.relative(24);
    assertEquals("This is line 74.", it.previous().toString());
  }

  public void testPreviousAfterAbsoluteNextAbsoluteGoesToCorrectLine() {
    setUp(large);
    it.absolute(50);
    it.next();
    it.absolute(75);
    assertEquals("This is line 74.", it.previous().toString());
  }

  public void testPreviousAfterAbsolutePreviousAbsoluteGoesToCorrectLine() {
    setUp(large);
    it.absolute(50);
    it.previous();
    it.absolute(75);
    assertEquals("This is line 74.", it.previous().toString());
  }

  public void testPreviousUponFirstLineReturnsNull() {
    setUp(small);
    assertNull(it.previous());
  }

  public void testNextUponLastLineReturnsNullSmallFile() {
    setUp(small);
    it.last();
    assertNull(it.next());
  }

  public void testNextUponLastLineReturnsNullSmallFileSmallBuffer()
      throws Exception {
    it = new FileLineIterator(small);
    it.setBufferSize(1);
    it.last();
    assertNull(it.next());
  }

  public void testNextUponLastLineReturnsNullLargeFile() {
    setUp(large);
    it.last();
    assertNull(it.next());
  }

  public void testIndexIsInitially0() {
    setUp(small);
    assertEquals(0, it.index());
  }

  public void testNextAfterAbsoluteGoesToCorrectLine() {
    setUp(large);
    it.absolute(50);
    assertEquals("This is line 51.", it.next().toString());
  }

  public void testNextAfterRelativeGoesToCorrectLine() {
    setUp(large);
    it.relative(50);
    assertEquals("This is line 51.", it.next().toString());
  }

  public void testNextAfterRelativePreviousRelativeGoesToCorrectLine() {
    setUp(large);
    it.relative(50);
    it.previous();
    it.relative(26);
    assertEquals("This is line 76.", it.next().toString());
  }

  public void testNextAfterAbsolutePreviousAbsoluteGoesToCorrectLine() {
    setUp(large);
    it.absolute(50);
    it.previous();
    it.absolute(75);
    assertEquals("This is line 76.", it.next().toString());
  }

  public void testNextAfterAbsoluteNextAbsoluteGoesToCorrectLine() {
    setUp(large);
    it.absolute(50);
    it.next();
    it.absolute(75);
    assertEquals("This is line 76.", it.next().toString());
  }

  public void testNextAdvancesIndex() {
    setUp(small);
    it.next();
    assertEquals(1, it.index());
  }

  public void testAbsoluteWithNegativeArgumentReturnsNull() {
    setUp(small);
    assertNull(it.absolute(-1));
  }

  public void testAbsoluteWithNegativeArgumentLeavesIndexAt0() {
    setUp(small);
    it.absolute(-1);
    assertEquals(0, it.index());
  }

  public void testAbsoluteWithArgumentOutsideFileReturnsNull() {
    setUp(small);
    assertNull(it.absolute(99999));
  }

  public void testAbsoluteWithArgumentOutsideFileLeavesIndexAtLastLine() {
    setUp(small);
    it.absolute(99999);
    assertEquals(9999, it.index());
  }

  public void testAbsoluteMultipleInvocationsReturnRequestedLines() {
    setUp(large);
    assertEquals("This is line 50.", it.absolute(50).toString());
    assertEquals("This is line 100.", it.absolute(100).toString());
    assertEquals("This is line 10.", it.absolute(10).toString());
  }

  public void testRelativeWithArgumentOutsideFileReturnsNull() {
    setUp(small);
    assertNull(it.relative(99999));
  }

  public void testRelativeWithArgumentOutsideFileNegativeReturnsNull() {
    setUp(small);
    assertNull(it.relative(-99999));
  }

  public void testRelativeWithArgumentOutsideFileLeavesIndexAtLastLine() {
    setUp(small);
    it.relative(99999);
    assertEquals(9999, it.index());
  }

  public void testRelativeWithArgumentOutsideFileNegativeLeavesIndexAt0() {
    setUp(small);
    it.relative(-999);
    assertEquals(0, it.index());
  }

  public void testRelativeMultipleInvocationsReturnRequestedLines() {
    setUp(large);
    it.relative(25);
    assertEquals("This is line 50.", it.relative(25).toString());
    assertEquals("This is line 100.", it.relative(50).toString());
    assertEquals("This is line 10.", it.relative(-90).toString());
  }

  public void testReadFirstLineWithCurrentAndSmallBuffer() {
    setUp(small);
    it.setBufferSize(1);
    assertEquals("This is line 0.", it.current().toString());
  }

  public void testLastLeavesIndexAtLastLine() {
    setUp(small);
    it.last();
    assertEquals(9999, it.index());
  }

  public void testLastReturnsLastLineSmallFile() {
    setUp(small);
    assertEquals("This is line 9999.", it.last().toString());
  }

  public void testLastReturnsLastLineLargeFile() {
    setUp(large);
    assertEquals("This is line 9999999.", it.last().toString());
  }

  public void testFirstReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.first());
  }

  public void testFirstLeavesIndexAtFirstLineSmallFile() {
    setUp(small);
    it.first();
    assertEquals(0, it.index());
  }

  public void testFirstLeavesIndexAtFirstLineSingleLineFile() {
    setUp(singleline);
    it.first();
    assertEquals(0, it.index());
  }

  public void testFirstReturnsFirstLineSmallFile() {
    setUp(small);
    assertEquals("This is line 0.", it.first().toString());
  }

  public void testFirstReturnsFirstLineSingleLineFile() {
    setUp(singleline);
    assertEquals("This is line 0.", it.first().toString());
  }

  public void testLastReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.last());
  }

  public void testCurrentReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.current());
  }

  public void testAbsoluteWithArgument0ReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.absolute(0));
  }

  public void testAbsoluteWithArgument1ReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.absolute(1));
  }

  public void testAbsoluteWithArgumentMinus1ReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.absolute(-1));
  }

  public void testNextReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.next());
  }

  public void testPreviousReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.previous());
  }

  public void testRelative0ReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.relative(0));
  }

  public void testRelative1ReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.relative(1));
  }

  public void testRelativeMinus1ReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.relative(-1));
  }

  public void testRelativeNegativeArgumentReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.relative(-100));
  }

  public void testRelativePositiveArgumentReturnsNullOnEmptyFile() {
    setUp(blank);
    assertNull(it.relative(100));
  }

  public void testSetBufferSizeWithNegativeArgumentThrowsException() {
    setUp(small);
    try {
      it.setBufferSize(-1);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testSetBufferSizeZeroThrowsException() {
    setUp(small);
    try {
      it.setBufferSize(0);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testIsLastReturnsTrueOnLastLine() {
    setUp(small);
    it.last();
    assertTrue(it.isLast());
  }

  public void testIsLastReturnsFalseOnOtherLine() {
    setUp(small);
    it.first();
    assertFalse(it.isLast());
    it.absolute(5);
    assertFalse(it.isLast());
  }

  public void testIsFirstReturnsTrueOnFirstLine() {
    setUp(small);
    it.first();
    assertTrue(it.isFirst());
  }

  public void testIsFirstReturnsFalseOnOtherLine() {
    setUp(small);
    it.last();
    assertFalse(it.isFirst());
    it.absolute(5);
    assertFalse(it.isFirst());
  }

  public void testLastLeavesIndexAtLastLineSingleLineFile() {
    setUp(singleline);
    it.last();
    assertEquals(0, it.index());
  }

  public void testLastReturnsLastLineSingleLineFile() {
    setUp(singleline);
    assertEquals("This is line 0.", it.last().toString());
  }

  public void testAbsoluteWithNegativeArgumentLeavesIndexAt0SingleLineFile() {
    setUp(singleline);
    it.absolute(-1);
    assertEquals(0, it.index());
  }

  public void testAbsoluteWithArgumentOutsideFileReturnsNullSingleLineFile() {
    setUp(singleline);
    assertNull(it.absolute(1));
  }

  public void testAbsoluteWithArgumentOutsideFileLeavesIndexAtLastLineSingleLineFile() {
    setUp(singleline);
    it.absolute(1);
    assertEquals(0, it.index());
  }

  public void testMoveAfterWrite() {
    setUp(singleline);
    try {
      for (int i = 0; i < 100; i++) {
        it.write("This is an additonal line " + i);
      }

      it.absolute(101);
      assertEquals(100, it.index());

      StopWatch sw = new StopWatch();
      sw.start();
      for (int i = 0; i < 100001; i++) {
        it.write("This is an additonal line " + i);
      }
      sw.stop();
      System.out.println(sw.elapsedSeconds());
      it.next();
      assertEquals(101, it.index());

    } catch (SQLException e) {

      e.printStackTrace();
    }

  }

}
