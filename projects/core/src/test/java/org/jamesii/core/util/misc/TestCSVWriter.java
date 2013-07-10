/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests for {@link CSVWriter}.
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Roland Ewald
 * 
 */
public class TestCSVWriter extends TestCase {

  double[][] simpleMatrix = { { 0.1, 0.2 }, { 0.3, 0.4 } };

  Double[][] nonPrimitiveMatrix = { { 0.1, 0.2 }, { 0.3, 0.4 } };

  public void testWriteMatrix() throws IOException {
    File f = getTestFile();
    CSVWriter.writeResult(nonPrimitiveMatrix, f.getAbsolutePath());
    List<String[]> content = new CSVReader().read(f.getAbsolutePath(), false);
    assertEquals(nonPrimitiveMatrix.length, content.size());
    assertEquals(nonPrimitiveMatrix[0].length, content.get(0).length);
    assertEquals(nonPrimitiveMatrix[1].length, content.get(1).length);
    assertEquals(nonPrimitiveMatrix[1][1].toString(), content.get(1)[1]);
    assertEquals(nonPrimitiveMatrix[0][1].toString(), content.get(0)[1]);
  }

  public void testWritePrimitiveMatrix() throws IOException {
    File f = getTestFile();
    CSVWriter.writeResult(simpleMatrix, f.getAbsolutePath());
    List<String[]> content = new CSVReader().read(f.getAbsolutePath(), false);
    assertEquals(simpleMatrix.length, content.size());
    assertEquals(simpleMatrix[0].length, content.get(0).length);
    assertEquals(simpleMatrix[1].length, content.get(1).length);
    assertEquals(Double.toString(simpleMatrix[1][1]), content.get(1)[1]);
    assertEquals(Double.toString(simpleMatrix[0][1]), content.get(0)[1]);
  }

  private File getTestFile() throws IOException {
    return TemporaryFileManager.createTempFile(getClass().getName(), "");
  }

}
