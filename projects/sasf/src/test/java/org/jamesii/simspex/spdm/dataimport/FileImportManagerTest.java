/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport;


import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.dataimport.file.FileImportManager;
import org.jamesii.simspex.spdm.dataimport.file.FileImportManagerFactory;

import junit.framework.TestCase;

/**
 * Tests the {@link FileImportManager}.
 * 
 * @author Kaustav Saha
 * @author Roland Ewald
 * 
 */
@Deprecated
public class FileImportManagerTest extends TestCase {

  /** Test file to be used. */
  public static final File TEST_FILE = new File(
      "./testdata/lcs/lcs_problem0.dat");

  /** Test directory with sample files. */
  public static final String TEST_DIR = "./testdata";

  /** The file manager. */
  IDMDataImportManager fileManager;

  /** Model description. */
  final HashMap<String, String> testModelDescrption =
      new HashMap<>();

  @Override
  protected void setUp() throws Exception {
    fileManager = createTestFileImportManager();
    testModelDescrption.put(FileImportManager.MODEL, "lcs");
  }

  @Override
  protected void tearDown() throws Exception {
    testModelDescrption.clear();
  }

  /**
   * Tests splitting into tokens.
   */
  public void testSplitLine() {
    String testString = "word__work";
    List<String> tokenList = FileImportManager.splitLine(testString);
    assertEquals(3, tokenList.size());
    assertEquals("word", tokenList.get(0));
    assertEquals("work", tokenList.get(2));
    assertTrue(tokenList.get(1).isEmpty());
  }

  /**
   * Tests generation of performance tuple.
   */
  public void testGeneratePerformanceTuple() {
    String sampleLine = "word__work_rng\t1.23\t5.00\t6.67";
    PerformanceTuple perfTuple =
        FileImportManager.generatePerformanceTuple(new Features(),
            FileImportManager.splitLine(sampleLine));
    assertEquals("word", perfTuple.getConfiguration()
        .get(FileImportManager.SIM));
    assertNull(perfTuple.getConfiguration().get(FileImportManager.SIMPARAMS));
    assertEquals("work", perfTuple.getConfiguration().get(FileImportManager.EQ));
    assertEquals("rng", perfTuple.getConfiguration().get(FileImportManager.RNG));
    assertEquals(5.00, perfTuple.getPerformance());
  }

  /**
   * Tests reading the file containing the performance data.
   */
  public void testProblemFileReading() {

    List<PerformanceTuple> tuples =
        FileImportManager.readProblemFile(TEST_FILE, testModelDescrption);
    assertEquals(162, tuples.size());

    PerformanceTuple testTuple = tuples.get(1);
    assertEquals("drm", testTuple.getConfiguration().get(FileImportManager.SIM));
    assertNull(testTuple.getConfiguration().get(FileImportManager.SIMPARAMS));
    assertNull(testTuple.getConfiguration().get(FileImportManager.EQ));
    assertEquals("isaac",
        testTuple.getConfiguration().get(FileImportManager.RNG));
    assertTrue(Math.abs(testTuple.getPerformance() - 37.98213) < 0.001);

    testTuple = tuples.get(41);
    assertEquals("nrma", testTuple.getConfiguration()
        .get(FileImportManager.SIM));
    assertNull(testTuple.getConfiguration().get(FileImportManager.SIMPARAMS));
    assertEquals("heap", testTuple.getConfiguration().get(FileImportManager.EQ));
    assertEquals("rwc", testTuple.getConfiguration().get(FileImportManager.RNG));
    assertTrue(Math.abs(testTuple.getPerformance() - 3.48973) < 0.001);
  }

  public void testTupleList() {
    PerformanceDataSet dataSet = fileManager.getPerformanceData();
    List<PerformanceTuple> tuples = dataSet.getInstances();
    assertEquals(344, tuples.size());
    PerformanceTuple testTuple = tuples.get(17);
    assertEquals("nrma", testTuple.getConfiguration()
        .get(FileImportManager.SIM));
    System.out.println("Eq:"
        + testTuple.getConfiguration().get(FileImportManager.EQ));
    assertEquals("2list", testTuple.getConfiguration()
        .get(FileImportManager.EQ));
    System.out.println("Rng:"
        + testTuple.getConfiguration().get(FileImportManager.RNG));
    assertEquals("rwc", testTuple.getConfiguration().get(FileImportManager.RNG));

  }

  public void testMetaData() {
    PerformanceDataSet dataSet = fileManager.getPerformanceData();
    PerfTupleMetaData metaData = dataSet.getMetaData();
    assertNotNull(metaData);
    assertEquals(4, metaData.getNominalAttribs().size());
    assertEquals(0, metaData.getNumericAttribs().size());
  }

  /**
   * Creates a {@link FileImportManager} instance for testing.
   * 
   * @return instantiation of {@link FileImportManager} assigned to some test
   *         data
   */
  public static IDMDataImportManager<PerformanceTuple> createTestFileImportManager() {
    return (new FileImportManagerFactory()).create(new ParameterBlock(TEST_DIR,
        FileImportManagerFactory.TARGET_DIR));
  }

}
