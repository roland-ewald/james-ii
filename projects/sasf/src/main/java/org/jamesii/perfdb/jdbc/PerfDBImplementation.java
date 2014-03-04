/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import java.io.Serializable;
import java.net.URI;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Generics;
import org.jamesii.perfdb.DatabaseAccessException;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IHardwareSetup;
import org.jamesii.perfdb.entities.IMachine;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IResultDataProvider;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.IFeatureExtractor;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * JDBC implementation of the performance database.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class PerfDBImplementation implements IPerformanceDatabase {

  /** Performance database to be used. */
  private PerformanceDatabase perfDB = null;

  public void init(DBConnectionData dbConn) {
    try {
      perfDB = new PerformanceDatabase(dbConn);
    } catch (SQLException | ClassNotFoundException e) {
      throw new DatabaseAccessException(e);
    }
    perfDB.createBase(null);
  }

  @Override
  public IFeatureType getFeatureForFactory(
      Class<? extends FeatureExtractorFactory> factoryClass) {
    try {
      return perfDB.getFeature().lookUp(factoryClass);
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public IFeatureType newFeatureType(String name, String desc,
      Class<? extends FeatureExtractorFactory> extractor) {
    FeatureType feat = new FeatureType(name, desc, extractor);
    try {
      feat.create();
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
    return feat;
  }

  @Override
  public List<IFeatureType> getAllFeatureTypes() {
    try {
      return Generics.changeListType(perfDB.getFeature().getEntities(null));
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public List<IProblemDefinition> getAllProblemDefinitions() {
    try {
      return Generics.changeListType(perfDB.getSimulationProblem().getEntities(
          null));
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public void open() {
    try {
      if (perfDB.getConnection() != null && perfDB.getConnection().isClosed()) {
        perfDB.openBase();
      }
    } catch (Exception e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public void clear() {
    CallableStatement st = null;

    try {
      st = perfDB.getConnection().prepareCall("CALL clearDatabase()");
      st.execute();
    } catch (ClassNotFoundException | SQLException e) {
      throw new DatabaseAccessException(e);
    } finally {
      if (st != null) {
        try {
          st.close();
        } catch (SQLException e) {
          throw new DatabaseAccessException(e);
        }
      }
    }
  }

  @Override
  public void close() {
    try {
      perfDB.closeBase();
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public void create() {
    perfDB.createBase(null);
  }

  @Override
  public IProblemScheme getProblemScheme(URI uri) {
    try {
      return perfDB.getBenchmarkModel().lookUp(uri);
    } catch (Exception e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public List<IPerformanceType> getAllPerformanceTypes() {
    try {
      return Generics.changeListType(perfDB.getPerformanceMeasure()
          .getEntities(null));
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public IProblemScheme newProblemScheme(URI uri, String name, String type,
      String description) {
    BenchmarkModel bm = new BenchmarkModel(uri, name, type, description);
    try {
      bm.create();
    } catch (SQLException e) {
      throw new IllegalArgumentException(e);
    }
    return bm;
  }

  @Override
  public IPerformanceType newPerformanceType(String typeName, String typeDesc,
      Class<? extends PerformanceMeasurerFactory> measurerClass) {
    PerformanceType pm = new PerformanceType(typeName, typeDesc, measurerClass);
    try {
      pm.create();
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
    return pm;
  }

  @Override
  public IPerformance newPerformance(IApplication app,
      IPerformanceType performanceMeasure, double performance) {
    Performance pm = new Performance(app, performanceMeasure, performance);
    try {
      pm.create();
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
    return pm;
  }

  @Override
  public List<IProblemScheme> getAllProblemSchemes() {
    try {
      return Generics.changeListType(perfDB.getBenchmarkModel().getEntities(
          null));
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public List<IProblemDefinition> getAllProblemDefinitions(IProblemScheme model) {
    try {
      return Generics.changeListType(perfDB.getSimulationProblem()
          .lookUp(model));
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public IRuntimeConfiguration getRuntimeConfig(SelectionTree selectionTree) {
    try {
      return perfDB.getRuntimeConfiguration().lookUp(selectionTree);
    } catch (Exception e) {
      throw new DatabaseAccessException(e);
    }
  }

  @Override
  public IRuntimeConfiguration newRuntimeConfiguration(
      SelectionTree selectionTree, boolean newVer) {
    RuntimeConfiguration rtConfig = new RuntimeConfiguration(selectionTree);
    try {
      rtConfig.create();
    } catch (SQLException e) {
      throw new DatabaseAccessException(e);
    }
    return rtConfig;
  }

  @Override
  public List<IRuntimeConfiguration> getAllRuntimeConfigs(
      IProblemDefinition simProb) {
    try {
      return Generics.changeListType(perfDB.getRuntimeConfiguration().lookUp(
          simProb));
    } catch (Exception e) {
      throw new DatabaseAccessException(e);
    }
  }

  public List<IRTConfigPerfProfile> getPerformanceProfile(
      IRuntimeConfiguration rtConfig) throws SQLException {
    if (!(rtConfig instanceof RuntimeConfiguration)) {
      throw new IllegalArgumentException();
    }
    return ((RuntimeConfiguration) rtConfig).lookUpPerformance();
  }

  public Map<IPerformanceType, IRuntimeConfiguration> getBestConfigurations(
      IProblemDefinition simProblem) throws SQLException {
    return perfDB.getBestConfigurations(simProblem);
  }

  @Override
  public boolean deleteProblemScheme(IProblemScheme model) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean deleteHardwareSetup(IHardwareSetup setup) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IHardwareSetup> getAllHardwareSetups() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IMachine> getAllMachines() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IHardwareSetup newHardwareSetup(String name, String desc,
      String nwTopology, long nwSpeed, Set<IMachine> machines) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IMachine> getAllMachines(String macAddress) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IMachine newMachine(String name, String desc, String macAddress,
      double sciMark) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean deleteMachine(IMachine machine) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getSetupCount(IMachine machine) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean deleteProblemDefinition(IProblemDefinition problem) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IPerformanceType getPerformanceType(
      Class<? extends PerformanceMeasurerFactory> measurerClass) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean deleteProblemInstance(IProblemInstance problemInst) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IApplication> getAllApplications(IProblemInstance probInstance) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IApplication> getAllApplications(IProblemDefinition simProb) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IRuntimeConfiguration> getAllRuntimeConfigurations(
      IProblemInstance probInstance) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IApplication newApplication(IProblemInstance probInst,
      IRuntimeConfiguration rtConfig, IHardwareSetup hwSetup,
      IResultDataProvider<?> sdProvider) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IProblemInstance getProblemInstance(IProblemDefinition simProblem,
      long randSeed) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IPerformance> getAllPerformances(IApplication application) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IProblemInstance> getAllProblemInstances(
      IProblemDefinition simProblem) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IApplication> getAllApplications(IProblemDefinition simProb,
      IRuntimeConfiguration rtConfig) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void flush() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IPerformance getPerformance(IApplication application,
      IPerformanceType perfType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IApplication> getAllApplications(IProblemInstance instance,
      IRuntimeConfiguration rtConfig) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IRuntimeConfiguration> getAllRuntimeConfigs() {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IRuntimeConfiguration> getAllCurrentRTConfigs(
      IProblemDefinition simProb) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IProblemDefinition getProblemDefinition(IProblemScheme bm,
      Map<String, Serializable> defParameters,
      Map<String, Serializable> parameters) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IProblemInstance newProblemInstance(IProblemDefinition simProblem,
      long randSeed, String rngFactoryName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IProblemDefinition newProblemDefinition(IProblemScheme bm,
      Map<String, Serializable> defParameters,
      Map<String, Serializable> parameters) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<IFeature> getAllFeatures(IApplication app) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IFeature getFeature(IApplication app, IFeatureType featureType) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IFeature newFeature(IApplication app, IFeatureType feature,
      IFeatureExtractor<ParameterBlock> extractor) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IFeature newFeature(IApplication app, IFeatureType featureType,
      Map<String, Serializable> featureValues) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<Long, Double> getAllPerformancesMap(IPerformanceType perfType) {
    throw new UnsupportedOperationException();
  }

}
