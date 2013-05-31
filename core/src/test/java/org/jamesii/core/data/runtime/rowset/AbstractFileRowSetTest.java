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
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.jamesii.core.data.runtime.rowset.FileRowSet;

import junit.framework.TestCase;

/**
 * Abstract base class for unit tests of the {@link FileRowSet} class. Due to
 * the size of the tested class the unit tests are divided in different classes,
 * each testing certain areas.
 * 
 * @author Johannes Rössel
 * 
 * @see FileRowSetTests
 * @see FileRowSetFunctionalityTest
 * @see FileRowSetFunctionalityGetterUpdaterTest
 * @see FileRowSetFunctionalityInapplicableTest
 * @see FileRowSetExceptionsTest
 * @see FileRowSetExceptionsInsertRowTest
 * @see FileRowSetExceptionsInapplicableTest
 * @see FileRowSetExceptionsClosedTest
 * @see FileRowSetExceptionsTypeConcurrencyTest
 * @see FileRowSetExceptionsCurrentRowTest
 * @see FileRowSetExceptionsInvalidColumnTest
 */
public abstract class AbstractFileRowSetTest extends TestCase {
  protected FileRowSet frs;

  protected static File blank;

  protected static File small;

  protected static File medium;

  protected static File large;

  protected static File types;

  static {
    blank = createTestFile("blank", 0);
    small = createTestFile("small", 10);
    medium = createTestFile("medium", 1000);
    large = createTestFile("large", 3000000);

    try {
      types = File.createTempFile("FRSTest_types", ".csv");
      types.deleteOnExit();
      try (BufferedWriter w =
          new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(types), Charset.forName("UTF-8")))) {
        w.write("Boolean,Date,Time,Timestamp,ByteArray,BigDecimal,URL\r\n");
        // lines in the file
        w.write(Boolean.toString(false));
        w.write(',');
        w.write("2011-02-04");
        w.write(',');
        w.write("15:34:13");
        w.write(',');
        w.write("2010-01-01 15:34:26.123456");
        w.write(',');
        w.write("SGVsbG8gV29ybGQ=");
        w.write(',');
        w.write("10");
        w.write(',');
        w.write("http://example.com");
        w.write("\r\n");

        w.write(Boolean.toString(true));
        w.write(',');
        w.write("2010-02-28");
        w.write(',');
        w.write("15:34:00");
        w.write(',');
        w.write("2010-01-01 15:34:26.1");
        w.write(',');
        w.write("Zm9v");
        w.write(',');
        w.write("3.1415926");
        w.write(',');
        w.write("ftp://example.com/some/path");
        w.write("\r\n");

        w.write(Boolean.toString(true));
        w.write(',');
        w.write("2010-02-28");
        w.write(',');
        w.write("15:34:00");
        w.write(',');
        w.write("2010-01-01 15:34:26");
        w.write(',');
        w.write("YmFy");
        w.write(',');
        w.write("-89347894893489593476897689347689347896789345768937896e25");
        w.write(',');
        w.write("http://example.com/some/path?a=query&string=here");
        w.write("\r\n");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a test file with a given name and number of records.
   * 
   * @param name
   *          The name for the test file.
   * @param numRecords
   *          The number of records to generate
   * @return A {@link File} object, representing the created file.
   */
  protected static File createTestFile(String name, int numRecords) {
    try {
      File f = File.createTempFile("FRSTest_" + name, ".csv");
      f.deleteOnExit();
      try (BufferedWriter w =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),
              Charset.forName("UTF-8")))) {
        w.write("Column 1,\"Column 2\",Col3\r\n");
        // lines in the file
        for (int i = 0; i < numRecords; i++) {
          w.write(Integer.toString(i));
          w.write(',');
          w.write(Integer.toString(i * 2));
          w.write(',');
          w.write("foo\r\n");
        }
      }
      return f;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void setUp() throws Exception {
    frs = new FileRowSet();
  }

  protected void load(File f) {
    frs.setFileName(f.getAbsolutePath());
    try {
      frs.execute();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void tearDown() throws Exception {
    frs.close();
    frs = null;
  }

}
