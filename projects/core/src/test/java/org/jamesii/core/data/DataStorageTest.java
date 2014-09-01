/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.runtime.IWriteReadDataStorage;
import org.jamesii.core.data.runtime.SystemInformation;
import org.jamesii.core.data.storage.DataStorageException;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.core.util.misc.Quadruple;
import org.jamesii.core.util.misc.Triple;

/**
 * Test class for data storages.
 * 
 * @author Sebastian Lieske
 * 
 *         TODO: Document the test! What are 'some aspects of parallelism'?
 */
public abstract class DataStorageTest extends TestCase {

  /**
   * Construct local hash map.
   * 
   * @param outermap
   *          the outermap
   * @param attrib
   *          the attrib
   * @param time
   *          the time
   * @param data
   *          the data
   */
  protected static void constructLocalHashMap(

  Map<String, Map<Double, List<Object>>> outermap,

  String attrib, double time, Object data) {

    Map<Double, List<Object>> innermap;
    List<Object> list;

    if (outermap.containsKey(attrib)) {
      innermap = outermap.get(attrib);
    } else {
      innermap = new HashMap<>();
    }

    if (innermap.containsKey(time)) {
      list = innermap.get(time);
    } else {
      list = new ArrayList<>();
    }

    list.add(data);
    innermap.put(time, list);
    outermap.put(attrib, innermap);

  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    // junit.swingui.TestRunner.run(DataStorageTest.class);
  }

  /** Data storage to be tested. */
  private IWriteReadDataStorage dataStorage;

  /**
   * Creates the data storage.
   * 
   * @return the i data storage
   */
  public abstract IWriteReadDataStorage createDataStorage();

  @Override
  protected void setUp() throws Exception {
    getDataStorage().setExperimentID(UniqueIDGenerator.createUniqueID());
    getDataStorage().setConfigurationID(null,
        UniqueIDGenerator.createUniqueID());
    getDataStorage().setComputationTaskID(null, null,
        UniqueIDGenerator.createUniqueID());
  }

  /**
   * Test experiment info.
   * 
   * @throws Exception
   *           the exception
   */
  public void testExperimentInfo() throws Exception {
    Serializable expid =
        getDataStorage().setExperimentID(UniqueIDGenerator.createUniqueID());
    ExperimentInfo ei1 = new ExperimentInfo(new URI("uri1"), new URI("uri2"));
    ei1.setDescription("nappinappiger nappinap");
    getDataStorage().writeExperimentInformation(expid, ei1);
    ExperimentInfo ei2 = getDataStorage().getExperimentInformation(expid);
    // go through the attributes of the experiment items:
    assertEquals(ei1.getDataBase(), ei2.getDataBase());
    assertEquals(ei1.getDescription(), ei2.getDescription());
    assertEquals(ei1.getIdent(), ei2.getIdent());
    // now let's have a look on ExperimentItems being null:
    expid =
        getDataStorage().setExperimentID(UniqueIDGenerator.createUniqueID());
    getDataStorage().writeExperimentInformation(expid, null);
    assertNull(getDataStorage().getExperimentInformation(expid));
  }

  /**
   * Test experiment system information.
   * 
   * @throws Exception
   *           the exception
   */
  public void testExperimentSystemInformation() throws Exception {
    IUniqueID uid = UniqueIDGenerator.createUniqueID();
    Serializable expid = getDataStorage().setExperimentID(uid);
    long mid = 987987;
    String version = "AlphaBetaGamma123";
    JavaInfo ji1 = new JavaInfo();
    getDataStorage().writeExperimentSystemInformation(uid, mid, version, ji1);
    SystemInformation si =
        getDataStorage().getExperimentSystemInformation(expid);
    // go through the attributes of the SystemInformation items:
    assertEquals(mid, si.getMachineId());
    assertEquals(version, si.getJamesVersion());
    JavaInfo ji2 = si.getJavaInfo();
    assertEquals(ji1.getName(), ji2.getName());
    assertEquals(ji1.getVersion(), ji2.getVersion());
    assertEquals(ji1.getVendor(), ji2.getVendor());
    assertEquals(ji1.getInstDir(), ji2.getInstDir());
    assertEquals(ji1.getClassFormatVersion(), ji2.getClassFormatVersion());
    assertEquals(ji1.getClassPath(), ji2.getClassPath());
    assertEquals(ji1.getExtensionDir(), ji2.getExtensionDir());
    assertEquals(ji1.getSessionStarted(), ji2.getSessionStarted());
    assertEquals(ji1.getUpTime(), ji2.getUpTime());
    assertEquals(ji1.getOs(), ji2.getOs());
    assertEquals(ji1.getOsVersion(), ji2.getOsVersion());
    assertEquals(ji1.getOsArch(), ji2.getOsArch());
    assertEquals(ji1.getCpus(), ji2.getCpus());
    assertEquals(ji1.getTotalMemory(), ji2.getTotalMemory());
    assertEquals(ji1.getFreeMemory(), ji2.getFreeMemory());
    assertEquals(ji1.getMaxMemory(), ji2.getMaxMemory());
    assertEquals(ji1.getThreads(), ji2.getThreads());
    assertEquals(ji1.getUserName(), ji2.getUserName());
    assertEquals(ji1.getUserHome(), ji2.getUserHome());
    assertEquals(ji1.getUserWorkingDir(), ji2.getUserWorkingDir());

    // now check what happens, if one of the parameters reference null:
    expid =
        getDataStorage().setExperimentID(
            uid = UniqueIDGenerator.createUniqueID());
    // avoids duplicate entries ;)
    getDataStorage().writeExperimentSystemInformation(uid, mid, version, null);
    assertTrue(getDataStorage().getExperimentSystemInformation(expid)
        .getJavaInfo() == null);
    assertNull(getDataStorage().getExperimentSystemInformation(expid)
        .getJavaInfo());
    expid =
        getDataStorage().setExperimentID(
            uid = UniqueIDGenerator.createUniqueID());
    getDataStorage().writeExperimentSystemInformation(uid, mid, null, ji1);
    assertNull(getDataStorage().getExperimentSystemInformation(expid)
        .getJamesVersion());
  }

  /**
   * Test read and write.
   */
  public void testReadAndWrite() {

    Serializable confid =
        getDataStorage().setConfigurationID(null,
            UniqueIDGenerator.createUniqueID());

    Serializable taskid =
        getDataStorage().setComputationTaskID(null, null,
            UniqueIDGenerator.createUniqueID());
    long dataid = 900;
    String attrib = "C70";
    double time = 200.7;
    Object daten1 = "Daten1";

    getDataStorage().writeData(dataid, attrib, time, daten1);
    getDataStorage().writeData(dataid, attrib, time, daten1);
    getDataStorage().writeData(dataid, attrib, time, "987987");
    List<?> al =
        getDataStorage().readData(confid, taskid, dataid, attrib, time);

    assertTrue(al.get(0).equals(daten1));
    assertTrue(al.get(1).equals(daten1));
    assertTrue(al.get(2).equals("987987"));

    getDataStorage().writeData(dataid, attrib, time + 8, daten1);
    Map<Double, List<Object>> hm =
        getDataStorage().readData(confid, taskid, dataid, attrib);
    Set<Double> s = hm.keySet();
    Iterator<Double> iter = s.iterator();
    while (iter.hasNext()) {
      Object o = iter.next();
      System.out.println("HM key: " + o);
      System.out.println("listed: " + hm.get(o));
    }

  }

  /**
   * Test read and write data entirely.
   */
  public void testReadAndWriteDataEntirely() {

    // create some initial values:
    Serializable confid =
        getDataStorage().setConfigurationID(null,
            UniqueIDGenerator.createUniqueID());

    Serializable simid =
        getDataStorage().setComputationTaskID(null, null,
            UniqueIDGenerator.createUniqueID());
    long dataid = 100;
    String attrib1 = "A";
    String attrib2 = "B";
    double time = 10.0;
    int data = 1234;

    Map<String, Map<Double, List<Object>>> localMap = new HashMap<>();

    // now store data and construct local HashMap in parallel:
    getDataStorage().writeData(dataid, attrib1, time, data);
    constructLocalHashMap(localMap, attrib1, time, data);
    getDataStorage().writeData(dataid, attrib2, time, data);
    constructLocalHashMap(localMap, attrib2, time, data);
    getDataStorage().writeData(dataid, attrib1, time + 5.0, data);
    constructLocalHashMap(localMap, attrib1, time + 5.0, data);
    getDataStorage().writeData(dataid, attrib2, time + 5.0, data);
    constructLocalHashMap(localMap, attrib2, time + 5.0, data);
    getDataStorage().writeData(dataid, attrib1, time, 70);
    constructLocalHashMap(localMap, attrib1, time, 70);
    getDataStorage().writeData(dataid, attrib1, time, 70);
    constructLocalHashMap(localMap, attrib1, time, 70);
    getDataStorage().writeData(dataid, attrib2, time, data + 5);
    constructLocalHashMap(localMap, attrib2, time, data + 5);

    // finally read from db and compare:
    Map<String, Map<Double, List<Object>>> fromDb =
        getDataStorage().readDataEntirely(confid, simid, dataid);
    assertEquals(localMap, fromDb);
    // System.out.println(localMap);
    // System.out.println(fromDb);

    // search for yetis:
    Map<String, Map<Double, List<Object>>> reinhold =
        getDataStorage().readDataEntirely(confid, Long.valueOf(2000), dataid);
    // System.out.println(reinhold);
    assertTrue(reinhold.isEmpty());
    reinhold = getDataStorage().readDataEntirely(dataid + 1000);
    assertTrue(reinhold.isEmpty());
  }

  // TODO: extend this!
  /**
   * Test read and write latest.
   */
  public void testReadAndWriteLatest() {

    Serializable confid =
        getDataStorage().setConfigurationID(null,
            UniqueIDGenerator.createUniqueID());

    Serializable simid =
        getDataStorage().setComputationTaskID(null, null,
            UniqueIDGenerator.createUniqueID());

    getDataStorage().writeData(100, "A50", 200.0, "Homer");
    getDataStorage().writeData(100, "A50", 200.0, "Bart");
    getDataStorage().writeData(100, "A50", 200.0, "March");
    getDataStorage().writeData(100, "B50", 200.0, 111);
    getDataStorage().writeData(100, "B50", 200.0, 222);
    getDataStorage().writeData(100, "C50", 200.0, (long) 111);
    getDataStorage().writeData(100, "C50", 200.0, (long) 222);

    assertEquals("March", getDataStorage().readLatestData(100, "A50"));
    assertEquals(222, getDataStorage().readLatestData(100, "B50"));
    assertEquals((long) 222, getDataStorage().readLatestData(100, "C50"));

    getDataStorage().setComputationTaskID(null, null,
        UniqueIDGenerator.createUniqueID());
    getDataStorage().writeData(100, "B50", 200.0, 333);
    assertEquals(333, getDataStorage().readLatestData(100, "B50"));
    assertEquals(222, getDataStorage()
        .readLatestData(confid, simid, 100, "B50"));
  }

  /**
   * Test read and write latest entirely.
   */
  public void testReadAndWriteLatestEntirely() {
    Serializable confid =
        getDataStorage().setConfigurationID(null,
            UniqueIDGenerator.createUniqueID());
    Serializable taskid =
        getDataStorage().setComputationTaskID(null, null,
            UniqueIDGenerator.createUniqueID());
    long dataid = 123;
    getDataStorage().writeData(dataid, "Attrib1", 12.3, "Zauberei");
    getDataStorage().writeData(dataid, "Attrib2", 12.3, 500);
    getDataStorage().writeData(dataid, "Attrib2", 12.3, 510);
    getDataStorage().writeData(dataid + 10, "Attrib2", 12.3, 520);
    getDataStorage().setComputationTaskID(null, null,
        UniqueIDGenerator.createUniqueID());
    getDataStorage().writeData(dataid, "Attrib2", 12.3, 530);
    Map<String, Object> hm =
        getDataStorage().readLatestDataEntirely(confid, taskid, dataid);

    Set<String> keys = hm.keySet();
    assertTrue(keys.contains("Attrib1"));
    assertTrue(keys.contains("Attrib2"));

    assertEquals("Zauberei", hm.get("Attrib1"));
    assertEquals(
        "Expected was 510 as it has been written to the storage after the value 500 for the attribute!s",
        510, hm.get("Attrib2"));

    // finally the mandatory search for yetis:
    hm = getDataStorage().readLatestDataEntirely(confid, taskid, dataid + 1122);
    assertTrue(hm.isEmpty());
  }

  /**
   * Test sim and exp ids.
   */
  public void testSimAndExpIds() {
    // first of all, we nee a brand new storage:
    IWriteReadDataStorage bns = createDataStorage();

    // let's have a look on how the storage reacts, if we try to write data
    // without setting the ExpId, ConfId or SimId before:
    long expid = bns.getNumberOfExperiments();
    assertEquals(0, expid);
    long confid = bns.getNumberOfConfigurations(expid);
    assertEquals(0, confid);
    assertEquals(0, bns.getNumberOfComputations(expid, confid));

    // try to start a new configuration, though no ExpID is available:
    try {
      bns.setConfigurationID(null, UniqueIDGenerator.createUniqueID());
      fail("You must specify the ExpID before.");
    } catch (DataStorageException expected) {
    }

    // try to start a new simulation, though no ConfId and ExpID are available:
    try {
      bns.setComputationTaskID(null, null, UniqueIDGenerator.createUniqueID());
      fail("You must specify the ExpID and ConfID before.");
    } catch (DataStorageException expected) {
    }

    // this should throw an Exception, that no ExpId has been set:
    try {
      bns.writeData(42, "bla", 13.37, "zweiundvierzig");
      fail("ExpID hast to be set before.");
    } catch (DataStorageException expected) {
    }

    // this should throw an Exception, that no SimId has been set:
    try {
      bns.setExperimentID(UniqueIDGenerator.createUniqueID());
      bns.writeData(42, "bla", 13.37, "zweiundvierzig");
      fail("SimID has to be set before.");
    } catch (DataStorageException expected) {
    }

    // this should throw an Exception, that no SimId has been set:
    try {
      bns.setConfigurationID(null, UniqueIDGenerator.createUniqueID());
      bns.writeData(42, "bla", 13.37, "zweiundvierzig");
      fail("ConfID has to be set before.");
    } catch (DataStorageException expected) {
    }

    // this should not throw an exception, because now both Ids have been set:
    bns.setComputationTaskID(null, null, UniqueIDGenerator.createUniqueID());
    bns.writeData(42, "bla", 13.37, "zweiundvierzig");

    // now the SimId should be reset and that's why we will expect again an
    // exception like "no SimId set":
    try {
      bns.setExperimentID(UniqueIDGenerator.createUniqueID());
      bns.writeData(42, "bla", 13.37, "zweiundvierzig");
      fail("SimID has to be reset.");
    } catch (DataStorageException expected) {
    }

    // TODO:
    /*
     * //assertEquals(0,
     * stor1.getNumberOfSimulationRuns(stor1.getNumberOfExperiments())); long
     * simid = stor1.newSimulation(); assertEquals(simid, 1); simid =
     * stor1.newSimulation(); simid = stor1.newSimulation(); assertEquals(simid,
     * 3);
     */
  }

  /**
   * Test some aspects of parallelism.
   */
  public void testSomeAspectsOfParallelism() {
    IDataStorage stor1 = createDataStorage();
    stor1.setExperimentID(UniqueIDGenerator.createUniqueID());
    IDataStorage stor2 = createDataStorage();
    stor2.setExperimentID(UniqueIDGenerator.createUniqueID());
    assertEquals(stor1.getNumberOfExperiments(), stor2.getNumberOfExperiments());
  }

  /**
   * Tests data counting.
   * 
   * @return the pair< long, long>
   * 
   * @throws Exception
   *           the exception
   */
  public Triple<Serializable, Serializable, Serializable> testDataCount()
      throws Exception {

    Serializable expID =
        getDataStorage().setExperimentID(UniqueIDGenerator.createUniqueID());
    Serializable confID =
        getDataStorage().setConfigurationID(null,
            UniqueIDGenerator.createUniqueID());
    Serializable taskID =
        getDataStorage().setComputationTaskID(null, null,
            UniqueIDGenerator.createUniqueID());
    long dataID = 1234;
    for (int i = 0; i < 100; i++) {
      getDataStorage().writeData(dataID, "attrib1" + expID, 2.0 + i,
          new String("string"));
      getDataStorage().writeData(dataID, "attrib2" + expID, 2.0 + i,
          new Integer(23));
    }
    getDataStorage().flushBuffers();
    assertEquals((long) 198,
        (long) getDataStorage().countData(expID, confID, taskID, 0.0, 100.0));
    return new Triple<>(expID, confID, taskID);
  }

  /**
   * Tests reading simulation data from an interval.
   * 
   * @throws Exception
   *           the exception
   */
  public void testSimTimeIntervalReadout() throws Exception {
    Triple<Serializable, Serializable, Serializable> ids = testDataCount();
    List<Quadruple<Double, Long, String, Object>> data =
        getDataStorage()
            .readData(ids.getA(), ids.getB(), ids.getC(), 0.0, 50.0);
    assertEquals(98, data.size());
    // for (Quadruple<Double, Long, String, Object> d : data) {
    // System.out.println(d.e1 + "," + d.e2 + "," + d.e3 + "," + d.e4);
    // }
  }

  /**
   * Gets the data storage, which is initialized lazily (during the first call).
   * 
   * @return the data storage
   */
  public final IWriteReadDataStorage<Serializable> getDataStorage() {
    if (dataStorage == null) {
      dataStorage = createDataStorage();
    }
    return dataStorage;
  }
}
