/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;

import static org.jamesii.simspex.util.DatabaseUtils.convertModelTypeToSchemeType;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Features;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IMachine;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.IFeatureExtractor;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.serialization.MyJamesSimDataProvider;
import org.jamesii.simspex.util.BenchmarkModelType;
import org.jamesii.simspex.util.DBConfiguration;
import org.jamesii.simspex.util.SimulationProblemDefinition;

import junit.framework.TestCase;

/**
 * General test to check the functionality of a performance database interface
 * implementation.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PerfDBTest extends TestCase {

  /** The performance database. */
  IPerformanceDatabase perfDB;

  /** The URI of the test model. */
  URI uri = null;

  /** MAC address of the test computer. */
  String macAddress;

  /** Name of test performance type. */
  final String perfTypeName = "MyType";

  /**
   * Dummy feature extractor.
   */
  final IFeatureExtractor<ParameterBlock> dummyExtractor =
      new IFeatureExtractor<ParameterBlock>() {
        @Override
        public Features extractFeatures(ParameterBlock params) {
          Features features = new Features();
          features
              .put(
                  "name",
                  ((IApplication) params
                      .getSubBlockValue(FeatureExtractorFactory.PROBLEM_REPRESENTATION))
                      .getProblemInstance().getProblemDefinition()
                      .getProblemScheme().getName());
          return features;
        }
      };

  public PerfDBTest() {
    super();
    try {
      uri = new URI("http://uri/test");
      macAddress = Strings.getMACAddressString(new byte[] { 1, 2, 3, 4 });
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    perfDB = getDataBase(DBConfiguration.getTestConnectionData());
    perfDB.clear();
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    perfDB.close();
  }

  /**
   * Retrieve the performance database that is under test.
   * 
   * @param dbConn
   *          the database connection to be used
   * @return the performance database to be tested
   * @throws Exception
   *           if database creation goes wrong
   */
  public abstract IPerformanceDatabase getDataBase(DBConnectionData dbConn)
      throws Exception;

  /**
   * General test, creates consistent test case one by one.
   * 
   * @throws Exception
   */
  public void testDatabaseInterface() throws Exception {
    problemSchemes();
    hardware();
    problemDefinitions();
    runtimeConfigurations();
    performance();
    features();
  }

  /**
   * Test for the problem scheme management interface.
   */
  public void problemSchemes() throws Exception {

    IProblemScheme scheme1 =
        perfDB.newProblemScheme(new URI("http://test/uri"), "TestScheme",
            convertModelTypeToSchemeType(BenchmarkModelType.COMMON),
            "Yet another test scheme");
    IProblemScheme scheme2 =
        perfDB.newProblemScheme(uri, "TestScheme2",
            convertModelTypeToSchemeType(BenchmarkModelType.APPLICATION),
            "And yet another test scheme");
    assertEquals(2, perfDB.getAllProblemSchemes().size());

    IProblemScheme scheme = perfDB.getProblemScheme(uri);
    assertEquals(scheme2.getName(), scheme.getName());
    assertEquals(scheme2.getDescription(), scheme.getDescription());
    assertEquals(scheme2.getType(), scheme.getType());
    assertEquals(scheme2.getUri(), scheme.getUri());

    assertEquals(true, perfDB.deleteProblemScheme(scheme1));
    assertEquals(1, perfDB.getAllProblemSchemes().size());

  }

  /**
   * Tests hardware management.
   */
  public void hardware() throws Exception {
    perfDB.newMachine("Hektor", "This is poor old Hektor.", macAddress, 23.45);
    perfDB.newMachine("DonQuijote", "",
        Strings.getMACAddressString(new byte[] { 5, 6, 7, 8 }), 2.3);
    List<IMachine> machines = perfDB.getAllMachines(macAddress);
    assertEquals(1, machines.size());
    machines = perfDB.getAllMachines();
    assertEquals(2, machines.size());

    perfDB.newHardwareSetup("TestSetup", "which is unique!", "Disconnected :)",
        0, new HashSet<>(machines));
    perfDB.newHardwareSetup("TestSetup", "which is unique!", "Disconnected :)",
        0, new HashSet<>(machines));
    HashSet<IMachine> otherMachines = new HashSet<>(machines);
    IMachine hal9000 =
        perfDB.newMachine("HAL 9000", "I'm sorry, Dave...",
            Strings.getMACAddressString(new byte[] { 2, 0, 0, 1 }), -1);
    otherMachines.add(hal9000);
    IHardwareSetup halSetup =
        perfDB.newHardwareSetup("TestSetup2", "which also is unique!",
            "strong", 0, otherMachines);
    List<IHardwareSetup> setups = perfDB.getAllHardwareSetups();
    assertEquals(2, setups.size());
    assertEquals(false, perfDB.deleteMachine(hal9000));
    perfDB.deleteHardwareSetup(halSetup);
    assertEquals(1, perfDB.getAllHardwareSetups().size());
    assertEquals(3, perfDB.getAllMachines().size());
    assertEquals(true, perfDB.deleteMachine(hal9000));
    assertEquals(2, perfDB.getAllMachines().size());
  }

  /**
   * Test for the problem definitions management interface.
   */
  public void problemDefinitions() throws Exception {
    IProblemScheme scheme = perfDB.getAllProblemSchemes().get(0);
    IProblemDefinition problemDefinition1 =
        perfDB.newProblemDefinition(scheme, SimulationProblemDefinition
            .getDefinitionParameters(SimTimeStopFactory.class,
                new ParameterBlock(1.0, SimTimeStopFactory.SIMEND)),
            new HashMap<String, Serializable>());
    perfDB.newProblemDefinition(scheme, SimulationProblemDefinition
        .getDefinitionParameters(SimTimeStopFactory.class, new ParameterBlock(
            1.0, SimTimeStopFactory.SIMEND)),
        new HashMap<String, Serializable>());
    IProblemDefinition problemDefinition2 =
        perfDB.newProblemDefinition(perfDB.newProblemScheme(new URI(
            "i://dont/care"), "Noname",
            convertModelTypeToSchemeType(BenchmarkModelType.SYNTHETIC), "-"),
            SimulationProblemDefinition.getDefinitionParameters(
                SimTimeStopFactory.class, new ParameterBlock(1.0,
                    SimTimeStopFactory.SIMEND)),
            new HashMap<String, Serializable>());
    assertEquals(1, perfDB.getAllProblemDefinitions(scheme).size());
    assertEquals(2, perfDB.getAllProblemDefinitions().size());
    HashMap<String, Serializable> testSchemeParameters =
        new HashMap<>();
    testSchemeParameters.put("just a", "value");
    perfDB.newProblemDefinition(scheme, SimulationProblemDefinition
        .getDefinitionParameters(SimTimeStopFactory.class, new ParameterBlock(
            1.0, SimTimeStopFactory.SIMEND)), testSchemeParameters);
    assertEquals(2, perfDB.getAllProblemDefinitions(scheme).size());
    perfDB.deleteProblemDefinition(problemDefinition1);
    assertEquals(1, perfDB.getAllProblemDefinitions(scheme).size());

    // Testing problem instances
    IProblemInstance pi =
        perfDB.newProblemInstance(problemDefinition2, 2345L, SimSystem
            .getRNGGenerator().getRNGFactory().getFactoryInstance().getClass()
            .getName());
    perfDB.newProblemInstance(problemDefinition2, 2346L, SimSystem
        .getRNGGenerator().getRNGFactory().getFactoryInstance().getClass()
        .getName());
    perfDB.newProblemInstance(problemDefinition2, 2345L, SimSystem
        .getRNGGenerator().getRNGFactory().getFactoryInstance().getClass()
        .getName());

    assertEquals(2, perfDB.getAllProblemInstances(problemDefinition2).size());
    perfDB.deleteProblemInstance(pi);
    assertEquals(1, perfDB.getAllProblemInstances(problemDefinition2).size());

  }

  /**
   * Tests runtime configuration management.
   */
  public void runtimeConfigurations() throws Exception {

    IProblemDefinition problemDefinition =
        perfDB.getAllProblemDefinitions().get(0);
    IProblemInstance problemInstance =
        perfDB.newProblemInstance(problemDefinition, 123L, SimSystem
            .getRNGGenerator().getRNGFactory().getFactoryInstance().getClass()
            .getName());
    SelectionTree selectionTree = new SelectionTree(null);
    IRuntimeConfiguration runtimeConfiguration =
        perfDB.newRuntimeConfiguration(selectionTree, false);
    perfDB.newRuntimeConfiguration(selectionTree, false);

    IHardwareSetup hardwareSetup = perfDB.getAllHardwareSetups().get(0);
    perfDB.newApplication(problemInstance, runtimeConfiguration, hardwareSetup,
        new MyJamesSimDataProvider());
    perfDB.newApplication(
        perfDB.newProblemInstance(problemDefinition, 1234L, SimSystem
            .getRNGGenerator().getRNGFactory().getFactoryInstance().getClass()
            .getName()), runtimeConfiguration, hardwareSetup,
        new MyJamesSimDataProvider());
    assertEquals(1, perfDB.getAllApplications(problemInstance).size());
    assertEquals(2, perfDB.getAllApplications(problemDefinition).size());

    assertEquals(1, perfDB.getAllRuntimeConfigs(problemDefinition).size());
    assertEquals(0,
        perfDB.getAllRuntimeConfigs(perfDB.getAllProblemDefinitions().get(1))
            .size());

    // Test versioning
    assertEquals(1, perfDB.getAllRuntimeConfigs().size());
    perfDB.newRuntimeConfiguration(selectionTree, true);
    assertEquals(2, perfDB.getAllRuntimeConfigs().size());
  }

  /**
   * Tests performance measurement management.
   */
  public void performance() throws Exception {

    IApplication app =
        perfDB.getAllApplications(perfDB.getAllProblemDefinitions().get(0))
            .get(0);

    perfDB.newPerformanceType(perfTypeName, "no desc",
        TotalRuntimePerfMeasurerFactory.class);
    perfDB.newPerformanceType("MyOtherType", "no desc",
        TotalRuntimePerfMeasurerFactory.class);
    IPerformanceType pm =
        perfDB.getPerformanceType(TotalRuntimePerfMeasurerFactory.class);
    assertEquals((new TotalRuntimePerfMeasurerFactory()).getMeasurementName(),
        pm.getName());

    perfDB.newPerformance(app, pm, 0.2);
    perfDB.newPerformance(app, pm, 0.4);
    assertEquals(2, perfDB.getAllPerformances(app).size());

  }

  /**
   * Tests feature management.
   */
  public void features() throws Exception {
    perfDB.newFeatureType("New Feat.", "without desc",
        FeatureExtractorFactory.class);
    IFeatureType featType =
        perfDB.newFeatureType("New Feat.", "without desc",
            FeatureExtractorFactory.class);
    assertTrue(
        "At least the new feature type should be registered in the database.",
        perfDB.getAllFeatureTypes().size() >= 1);
    IApplication app =
        perfDB.getAllApplications(perfDB.getAllProblemDefinitions().get(0))
            .get(0);
    perfDB.newFeature(app, featType, dummyExtractor);
    perfDB.newFeature(app, featType, dummyExtractor);
    assertEquals(1, perfDB.getAllFeatures(app).size());
  }

  /**
   * Get the performance database.
   * 
   * @return the performance database
   */
  public IPerformanceDatabase getPerfDB() {
    return perfDB;
  }

}
