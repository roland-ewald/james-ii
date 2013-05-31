/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

/**
 * Tests whether the {@link FileRowSet} class works like it should.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetFunctionalityTest extends AbstractFileRowSetTest {

  // absolute(int)

  public void testAbsoluteYieldsCorrectRowForward() throws Exception {
    load(medium);
    frs.absolute(10);
    verifyFields(9);
  }

  public void testAbsolute1YieldsCorrectRow() throws Exception {
    load(small);
    frs.absolute(1);
    verifyFields(0);
  }

  public void testAbsoluteMinus1YieldsCorrectRow() throws Exception {
    load(small);
    frs.absolute(-1);
    verifyFields(9);
  }

  public void testAbsoluteYieldsCorrectRowBackward() throws Exception {
    load(small);
    frs.absolute(-5);
    verifyFields(5);
  }

  public void testAbsoluteBeyondStartOfFileLeavesCursorBeforeFirst()
      throws Exception {
    load(small);
    frs.absolute(-100);
    assertTrue(frs.isBeforeFirst());
  }

  public void testAbsoluteBeyondStartOfFileReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.absolute(-100));
  }

  public void testAbsoluteBeyondEndOfFileLeavesCursorAfterLast()
      throws Exception {
    load(small);
    frs.absolute(100);
    assertTrue(frs.isAfterLast());
  }

  public void testAbsoluteBeyondEndOfFileReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.absolute(100));
  }

  public void testAbsoluteOnValidRowReturnsTrueForward() throws Exception {
    load(small);
    assertTrue(frs.absolute(5));
  }

  public void testAbsoluteOnValidRowReturnsTrueBackward() throws Exception {
    load(small);
    assertTrue(frs.absolute(-5));
  }

  public void testAbsoluteOnValidRowMovesCursorToThatRowForward()
      throws Exception {
    load(small);
    frs.absolute(5);
    assertEquals(5, frs.getRow());
  }

  public void testAbsoluteOnValidRowMovesCursorToThatRowBackward()
      throws Exception {
    load(small);
    frs.absolute(-5);
    assertEquals(6, frs.getRow());
  }

  // afterLast()

  public void testAfterLastMovesCursorAfterLastRow() throws Exception {
    load(small);
    frs.afterLast();
    assertTrue(frs.isAfterLast());
  }

  public void testAfterLastHasNoEffectOnBlankFile() throws Exception {
    load(blank);
    frs.afterLast();
    assertFalse(frs.isAfterLast());
    assertFalse(frs.isBeforeFirst());
  }

  // beforeFirst()

  public void testBeforeFirstMovesCursorBeforeFirstRow() throws Exception {
    load(small);
    frs.next();
    frs.beforeFirst();
    assertTrue(frs.isBeforeFirst());
  }

  public void testBeforeFirstHasNoEffectOnBlankFile() throws Exception {
    load(blank);
    frs.beforeFirst();
    assertFalse(frs.isBeforeFirst());
    assertFalse(frs.isAfterLast());
  }

  // cancelRowUpdates()

  public void testCancelRowUpdatesUndoesChanges() throws Exception {
    load(small);
    frs.next();
    frs.updateInt(1, 5);
    frs.cancelRowUpdates();
    assertEquals(0, frs.getInt(1));
  }

  // close()

  public void testCloseTwiceDoesNotThrowException() throws Exception {
    frs.close();
    // second invocation must not do anything out of the ordinary
    frs.close();
  }

  // static create()

  public void testCreateFileStringArrayCreatesRowSetWithTheGivenHeaders()
      throws Exception {
    File f = File.createTempFile("createTest", null);
    f.deleteOnExit();
    frs = FileRowSet.create(f, "Column 1", "Column 2", "Col3");
    verifyMetaData();
  }

  public void testCreateStringStringArrayCreatesRowSetWithTheGivenHeaders()
      throws Exception {
    File f = File.createTempFile("createTest", null);
    f.deleteOnExit();
    frs =
        FileRowSet.create(f.getAbsolutePath(), "Column 1", "Column 2", "Col3");
    verifyMetaData();
  }

  public void testCreateFileCharsetStringArrayCreatesRowSetWithTheGivenHeaders()
      throws Exception {
    File f = File.createTempFile("createTestUTF16", null);
    f.deleteOnExit();
    frs =
        FileRowSet.create(f, Charset.forName("UTF-16LE"), "Column 1",
            "Column 2", "Col3");
    verifyMetaData();
  }

  public void testCreateStringCharsetStringArrayCreatesRowSetWithTheGivenHeaders()
      throws Exception {
    File f = File.createTempFile("createTestUTF16", null);
    f.deleteOnExit();
    frs =
        FileRowSet.create(f.getAbsolutePath(), Charset.forName("UTF-16LE"),
            "Column 1", "Column 2", "Col3");
    verifyMetaData();
  }

  public void testCreateFileStringArrayCreatesEmptyRowSet() throws Exception {
    File f = File.createTempFile("createTest", null);
    f.deleteOnExit();
    frs = FileRowSet.create(f, "Column 1", "Column 2", "Col3");
    frs.afterLast();
    assertFalse(frs.isAfterLast());
  }

  public void testCreateStringStringArrayCreatesEmptyRowSet() throws Exception {
    File f = File.createTempFile("createTest", null);
    f.deleteOnExit();
    frs =
        FileRowSet.create(f.getAbsolutePath(), "Column 1", "Column 2", "Col3");
    frs.afterLast();
    assertFalse(frs.isAfterLast());
  }

  public void testCreateFileCharsetStringArrayCreatesEmptyRowSet()
      throws Exception {
    File f = File.createTempFile("createTestUTF16", null);
    f.deleteOnExit();
    frs =
        FileRowSet.create(f, Charset.forName("UTF-16LE"), "Column 1",
            "Column 2", "Col3");
    frs.afterLast();
    assertFalse(frs.isAfterLast());
  }

  public void testCreateStringCharsetStringArrayCreatesEmptyRowSet()
      throws Exception {
    File f = File.createTempFile("createTestUTF16", null);
    f.deleteOnExit();
    frs =
        FileRowSet.create(f.getAbsolutePath(), Charset.forName("UTF-16LE"),
            "Column 1", "Column 2", "Col3");
    frs.afterLast();
    assertFalse(frs.isAfterLast());
  }

  // execute()

  public void testExecuteGetsCorrectHeadersBlankFile() throws SQLException {
    load(blank);
    verifyMetaData();
  }

  public void testExecuteGetsCorrectHeadersSmallFile() throws SQLException {
    load(small);
    verifyMetaData();
  }

  public void testExecuteGetsCorrectHeadersMediumFile() throws SQLException {
    load(medium);
    verifyMetaData();
  }

  public void testExecuteGetsCorrectHeadersLargeFile() throws SQLException {
    load(large);
    verifyMetaData();
  }

  // findColumn(String)

  public void testFindColumnFirstColumn() throws Exception {
    load(small);
    assertEquals(1, frs.findColumn("Column 1"));
  }

  public void testFindColumnLastColumn() throws Exception {
    load(small);
    assertEquals(3, frs.findColumn("Col3"));
  }

  // first()

  public void testFirstMakesFirstRowTheCurrentRow() throws Exception {
    load(small);
    frs.first();
    assertEquals(1, frs.getRow());
  }

  public void testFirstReturnsFalseOnEmptyRowSet() throws Exception {
    load(blank);
    assertFalse(frs.first());
  }

  public void testFirstReturnsTrueOnNonEmptyRowSet() throws Exception {
    load(small);
    assertTrue(frs.first());
  }

  // getRow()

  public void testGetRowReturnsZeroBeforeFirstRow() throws Exception {
    load(small);
    frs.beforeFirst();
    assertEquals(0, frs.getRow());
  }

  public void testGetRowReturnsZeroAfterLastRow() throws Exception {
    load(small);
    frs.afterLast();
    assertEquals(0, frs.getRow());
  }

  public void testGetRowReturnsZeroOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    assertEquals(0, frs.getRow());
  }

  public void testGetRowReturnsZeroWhenNoFileWasLoaded() throws SQLException {
    assertEquals(0, frs.getRow());
  }

  public void testGetRowReturnsRowNumber() throws Exception {
    load(small);
    frs.next();
    assertEquals(1, frs.getRow());
  }

  // insertRow()

  public void testInsertRowInsertsRow() throws Exception {
    File f = createTestFile("insertTest", 10);
    load(f);
    frs.moveToInsertRow();
    frs.updateString(1, "a");
    frs.updateString(2, "b");
    frs.updateString(3, "c");
    frs.insertRow();
    frs.moveToCurrentRow();
    frs.last();
    assertEquals(11, frs.getRow());
  }

  public void testInsertRowInsertsCorrectValues() throws Exception {
    File f = createTestFile("insertTest", 10);
    load(f);
    frs.moveToInsertRow();
    frs.updateString(1, "a");
    frs.updateString(2, "b");
    frs.updateString(3, "c");
    frs.insertRow();
    frs.moveToCurrentRow();
    frs.last();
    assertEquals(11, frs.getRow());
  }

  // isAfterLast()

  public void testIsAfterLastReturnsFalseOnEmptyRowSet() throws SQLException {
    load(blank);
    assertFalse(frs.isAfterLast());
  }

  // isBeforeFirst()

  public void testIsBeforeFirstReturnsTrueAfterCreation() throws SQLException {
    load(small);
    assertTrue(frs.isBeforeFirst());
  }

  public void testIsBeforeFirstReturnsFalseOnEmptyRowSet() throws SQLException {
    load(blank);
    assertFalse(frs.isBeforeFirst());
  }

  // isClosed()

  public void testIsClosedReturnsFalseOnNotClosedRowSet() throws Exception {
    assertFalse(frs.isClosed());
  }

  public void testIsClosedReturnsTrueOnClosedRowSet() throws Exception {
    frs.close();
    assertTrue(frs.isClosed());
  }

  // isFirst()

  public void testIsFirstReturnsTrueOnFirstLine() throws Exception {
    load(small);
    frs.first();
    assertTrue(frs.isFirst());
  }

  public void testIsFirstReturnsFalseBeforeFirst() throws Exception {
    load(small);
    frs.beforeFirst();
    assertFalse(frs.isFirst());
  }

  public void testIsFirstReturnsFalseOnOtherLine() throws Exception {
    load(small);
    frs.absolute(5);
    assertFalse(frs.isFirst());
  }

  public void testIsFirstReturnsFalseAfterLast() throws Exception {
    load(small);
    frs.afterLast();
    assertFalse(frs.isFirst());
  }

  public void testIsFirstReturnsFalseOnLastLine() throws Exception {
    load(small);
    frs.last();
    assertFalse(frs.isFirst());
  }

  public void testIsFirstReturnsFalseOnInsertRowLine() throws Exception {
    load(small);
    frs.moveToInsertRow();
    assertFalse(frs.isFirst());
  }

  // isLast()

  public void testIsLastReturnsFalseOnFirstLine() throws Exception {
    load(small);
    frs.first();
    assertFalse(frs.isLast());
  }

  public void testIsLastReturnsFalseBeforeFirst() throws Exception {
    load(small);
    frs.beforeFirst();
    assertFalse(frs.isLast());
  }

  public void testIsLastReturnsFalseOnOtherLine() throws Exception {
    load(small);
    frs.absolute(5);
    assertFalse(frs.isLast());
  }

  public void testIsLastReturnsFalseAfterLast() throws Exception {
    load(small);
    frs.afterLast();
    assertFalse(frs.isLast());
  }

  public void testIsLastReturnsTrueOnLastLine() throws Exception {
    load(small);
    frs.last();
    assertTrue(frs.isLast());
  }

  public void testIsLastReturnsFalseOnInsertRow() throws Exception {
    load(small);
    frs.moveToInsertRow();
    assertFalse(frs.isLast());
  }

  // last()

  public void testLastReturnsFalseOnEmptyRowSet() throws Exception {
    load(blank);
    assertFalse(frs.last());
  }

  public void testLastReturnsTrueOnNonEmptyRowSet() throws Exception {
    load(small);
    assertTrue(frs.last());
  }

  public void testLastMakesLastRowTheCurrentRow() throws Exception {
    load(small);
    frs.last();
    assertEquals(10, frs.getRow());
  }

  public void testLastMovesToLastRow() throws Exception {
    load(small);
    frs.last();
    assertEquals(10, frs.getRow());
  }

  public void testLastYieldsLastRow() throws Exception {
    load(small);
    frs.last();
    verifyFields(9);
  }

  // next()

  public void testNextIncreasesRowCounter() throws Exception {
    load(small);
    frs.next();
    int currentRow = frs.getRow();
    frs.next();
    assertEquals(currentRow + 1, frs.getRow());
  }

  public void testNextFirstUseMakesFirstRowTheCurrentRow() throws SQLException {
    load(small);
    frs.next();
    assertEquals(1, frs.getRow());
  }

  public void testNextOnBeforeFirstYieldsFirstRow() throws Exception {
    load(small);
    frs.next();
    verifyFields(0);
  }

  public void testNextOnFirstRowYieldsSecondRow() throws Exception {
    load(small);
    frs.next();
    frs.next();
    verifyFields(1);
  }

  public void testNextReturnsFalseOnLastRow() throws SQLException {
    load(small);
    frs.last();
    assertFalse(frs.next());
  }

  public void testNextPositionsCursorAfterLastRowOnLastRow()
      throws SQLException {
    load(small);
    frs.last();
    frs.next();
    assertTrue(frs.isAfterLast());
  }

  public void testNextReturnsFalseOnEmptyRowSet() throws SQLException {
    load(blank);
    assertFalse(frs.next());
  }

  public void testNextReturnsFalseAfterLastRow() throws SQLException {
    load(small);
    frs.afterLast();
    assertFalse(frs.next());
  }

  public void testNextReturnsTrueOnFirstRow() throws Exception {
    load(small);
    frs.first();
    assertTrue(frs.next());
  }

  public void testNextReturnsTrueBeforeFirstRow() throws Exception {
    load(small);
    assertTrue(frs.next());
  }

  public void testNextReturnsTrueOnValidRow() throws Exception {
    load(small);
    frs.absolute(4);
    assertTrue(frs.next());
  }

  // moveToInsertRow()

  public void testMoveToInsertRowMovesToInsertRow() throws SQLException {
    load(small);
    frs.moveToInsertRow();
    // should fail otherwise since we didn't do a next() yet. Therefore the
    // cursor is still before the first row and an exception would be thrown
    frs.updateString(1, "");
  }

  // moveToCurrentRow

  public void testMoveToCurrentRowDoesNotThrowExceptionWhenOnTheCurrentRow()
      throws SQLException {
    load(small);
    frs.moveToCurrentRow();
  }

  public void testMoveToCurrentRowReturnsToCurrentRowAfterOnInsertRow()
      throws Exception {
    load(small);
    frs.absolute(3);
    frs.moveToInsertRow();
    frs.moveToCurrentRow();
    assertEquals(3, frs.getRow());
  }

  // previous()

  public void testPreviousReturnsFalseOnFirstRow() throws Exception {
    load(small);
    frs.first();
    assertFalse(frs.previous());
  }

  public void testPreviousReturnsFalseBeforeFirstRow() throws Exception {
    load(small);
    assertFalse(frs.previous());
  }

  public void testPreviousReturnsTrueOnValidRow() throws Exception {
    load(small);
    frs.absolute(4);
    assertTrue(frs.previous());
  }

  public void testPreviousOnSecondRowYieldsFirstRow() throws Exception {
    load(small);
    frs.next();
    frs.next();
    frs.previous();
    verifyFields(0);
  }

  public void testPreviousReturnsTrueOnLastRow() throws Exception {
    load(small);
    frs.last();
    assertTrue(frs.previous());
  }

  public void testPreviousReturnsTrueAfterLastRow() throws Exception {
    load(small);
    frs.afterLast();
    assertTrue(frs.previous());
  }

  public void testPreviousDecreasesRowCounter() throws Exception {
    load(small);
    frs.last();
    int currentRow = frs.getRow();
    frs.previous();
    assertEquals(currentRow - 1, frs.getRow());
  }

  // refrehRow()

  public void testRefreshRowDoesNotThrowException() throws Exception {
    load(small);
    frs.next();
    frs.refreshRow();
  }

  public void testRefreshRowUndoesChanges() throws Exception {
    load(small);
    frs.next();
    frs.updateInt(1, 5);
    frs.refreshRow();
    assertEquals(0, frs.getInt(1));
  }

  // relative(int)

  public void testRelativeYieldsCorrectRowForward() throws Exception {
    load(medium);
    frs.relative(15);
    verifyFields(14);
  }

  public void testRelativeYieldsCorrectRowBackward() throws Exception {
    load(small);
    frs.last();
    frs.relative(-5);
    verifyFields(4);
  }

  public void testRelativeBeyondStartOfFileLeavesCursorBeforeFirst()
      throws Exception {
    load(small);
    frs.absolute(5);
    frs.relative(-10);
    assertTrue(frs.isBeforeFirst());
  }

  public void testRelativeBeyondStartOfFileReturnsFalse() throws Exception {
    load(small);
    frs.absolute(5);
    assertFalse(frs.relative(-10));
  }

  public void testRelativeBeyondEndOfFileLeavesCursorAfterLast()
      throws Exception {
    load(small);
    frs.relative(100);
    assertTrue(frs.isAfterLast());
  }

  public void testRelativeBeyondEndOfFileReturnsFalse() throws Exception {
    load(small);
    assertFalse(frs.relative(100));
  }

  // setDataSourceName()

  public void testSetDataSourceNameSetsFileName() throws Exception {
    frs.setDataSourceName("something.csv");
    assertEquals("something.csv", frs.getFileName());
  }

  // setEncoding()

  public void testSetEncodingSetsEncodingBeforeExecute() throws Exception {
    frs.setEncoding(Charset.forName("US-ASCII"));
    assertEquals(Charset.forName("US-ASCII"), frs.getEncoding());
  }

  public void testSetEncodingPersistsEncodingAfterExecute() throws Exception {
    frs.setEncoding(Charset.forName("US-ASCII"));
    load(blank);
    assertEquals(Charset.forName("US-ASCII"), frs.getEncoding());
  }

  // setFileName()

  public void testSetFileNameSetsDataSourceName() throws Exception {
    frs.setFileName("something.csv");
    assertEquals("something.csv", frs.getDataSourceName());
  }

  // helpers

  private void verifyMetaData() throws SQLException {
    ResultSetMetaData md = frs.getMetaData();
    assertEquals(3, md.getColumnCount());
    assertEquals("Column 1", md.getColumnLabel(1));
    assertEquals("Column 2", md.getColumnLabel(2));
    assertEquals("Col3", md.getColumnLabel(3));
  }

  private void verifyFields(int index) throws SQLException {
    assertEquals(index, frs.getInt(1));
    assertEquals(index * 2, frs.getInt(2));
    assertEquals("foo", frs.getString(3));
  }

}
