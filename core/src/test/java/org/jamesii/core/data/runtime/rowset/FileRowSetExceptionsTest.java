/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.jamesii.core.data.runtime.rowset.FileRowSet;
import org.jamesii.core.data.runtime.rowset.FileRowSetException;

/**
 * Tests whether {@link FileRowSet} methods throw exceptions when they are
 * supposed to.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public class FileRowSetExceptionsTest extends AbstractFileRowSetTest {

  public void testAbsoluteThrowsExceptionOnZeroArgument() throws Exception {
    load(small);
    try {
      frs.absolute(0);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  public void testCreateFileStringArrayThrowsExceptionOnNullFirstArgument()
      throws Exception {
    try {
      FileRowSet.create((File) null, "a");
    } catch (IOException e) {
    }
  }

  public void testCreateFileStringArrayThrowsExceptionOnNullSecondArgument()
      throws Exception {
    File f = File.createTempFile("exceptionsTest", null);
    f.deleteOnExit();
    try {
      FileRowSet.create(f, (String[]) null);
    } catch (SQLException e) {
    }
  }

  public void testCreateStringStringArrayThrowsExceptionOnNullFirstArgument()
      throws Exception {
    try {
      FileRowSet.create((String) null, "a");
    } catch (NullPointerException e) {
    }
  }

  public void testCreateStringStringArrayThrowsExceptionOnNullSecondArgument()
      throws Exception {
    File f = File.createTempFile("exceptionsTest", null);
    f.deleteOnExit();
    try {
      FileRowSet.create(f.getAbsolutePath(), (String[]) null);
    } catch (SQLException e) {
    }
  }

  public void testCreateFileStringArrayThrowsExceptionOnEmptyHeaders()
      throws Exception {
    File f = File.createTempFile("exceptionsTest", null);
    f.deleteOnExit();
    try {
      FileRowSet.create(f);
    } catch (IllegalArgumentException e) {
    }
  }

  public void testCreateStringStringArrayThrowsExceptionOnEmptyHeaders()
      throws Exception {
    File f = File.createTempFile("exceptionsTest", null);
    f.deleteOnExit();
    try {
      FileRowSet.create(f.getAbsolutePath());
    } catch (IllegalArgumentException e) {
    }
  }

  public void testExecuteThrowsExceptionWhenFileNameIsNotSet() {
    try {
      frs.execute();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testFindColumnThrowsExceptionWithInvalidColumnLabel() {
    load(small);
    try {
      frs.findColumn("xyz");
      fail();
    } catch (SQLException e) {
      assertEquals(IllegalArgumentException.class, e.getCause().getClass());
    }
  }

  public void testFindColumnThrowsExceptionWithColumnLabelNull()
      throws Exception {
    load(small);
    try {
      frs.findColumn(null);
      fail();
    } catch (FileRowSetException e) {
    }
  }

  public void testInsertRowThrowsExceptionWhenNoFieldsAreSet() throws Exception {
    File f = createTestFile("insertTest", 10);
    load(f);
    frs.moveToInsertRow();
    try {
      frs.insertRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testInsertRowThrowsExceptionWhenNotAllFieldsAreSet()
      throws Exception {
    File f = createTestFile("insertTest", 10);
    load(f);
    frs.moveToInsertRow();
    frs.updateInt(1, 5);
    frs.updateInt(2, 7);
    try {
      frs.insertRow();
      fail();
    } catch (SQLException e) {
    }
  }

  public void testSetEncodingThrowsExceptionAfterOpeningFile() throws Exception {
    load(small);
    try {
      frs.setEncoding(Charset.forName("US-ASCII"));
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetEncodingThrowsExceptionWithNullArgument() throws Exception {
    try {
      frs.setEncoding(null);
    } catch (FileRowSetException e) {
    }
  }

  public void testSetFileNameThrowsExceptionAfterOpeningFile() throws Exception {
    load(small);
    try {
      frs.setFileName(blank.getAbsolutePath());
    } catch (UnsupportedOperationException e) {
    }
  }

  public void testSetFileNameThrowsExceptionWithNullArgument() throws Exception {
    try {
      frs.setFileName(null);
    } catch (FileRowSetException e) {
    }
  }
}
