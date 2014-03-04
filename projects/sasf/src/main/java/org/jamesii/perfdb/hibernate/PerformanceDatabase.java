/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.jamesii.SimSystem;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Generics;
import org.jamesii.perfdb.ConstraintException;
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
import org.jamesii.perfdb.util.HibernateConnectionData;
import org.jamesii.perfdb.util.ParameterBlocks;
import org.jamesii.perfdb.util.PerfDB;

/**
 * Hibernate - implementation of performance data base.
 * 
 * @see IPerformanceDatabase
 * 
 * @author Roland Ewald
 */
public class PerformanceDatabase implements IPerformanceDatabase {

  /** Path to hibernate configuration file. */
  public static final String HIBERNATE_CONFIG =
      "org/jamesii/perfdb/hibernate/hibernate.cfg.xml";

  /** HQL query to retrieve all runtime configurations for a problem definition. */
  private static final String HQL_QUERY_RTC =
      "select distinct a.runtimeConfig from Application a, ProblemInstance i where a.problemInst.ID = i.ID and i.problemDef.ID=";

  /** The name for an application in a filter. */
  private static final String APP_NAME = "app";

  /** The default error message in case an unsuitable entity was encountered. */
  protected static final String HIBERNATE_ENTITY_ERROR =
      "Only Hibernate entities are supported.";

  /** Connection data to data base. */
  private DBConnectionData dbConn = null;

  /** Hibernate configuration. */
  private Configuration config;

  /** Session factory. */
  private SessionFactory sessionFactory = null;

  /** Default session (for small operations). */
  private Session session = null;

  /** Flag to decide whether to flush the session after every insertion. */
  private boolean alwaysFlush = true;

  /**
   * Instantiates a new performance database.
   * 
   * @param data
   *          the data
   */
  public PerformanceDatabase(DBConnectionData data) {
    dbConn = data;
    config =
        new Configuration()
            .configure(HIBERNATE_CONFIG)
            .setProperty("hibernate.connection.username", dbConn.getUser())
            .setProperty("hibernate.connection.password", dbConn.getPassword())
            .setProperty("hibernate.connection.url", dbConn.getURL())
            // For HSQLDB (default):
            .setProperty("hibernate.connection.pool_size", "1")
            .setProperty("hibernate.connection.autocommit", "true")
            .setProperty("hibernate.cache.provider_class",
                "org.hibernate.cache.HashtableCacheProvider");
    // For testing: removes db at the end
    // .setProperty("hibernate.hbm2ddl.auto", "create-drop")
    // .setProperty("hibernate.show_sql", "true")

    if (dbConn instanceof HibernateConnectionData) {
      config.setProperty("hibernate.connection.driver_class", // NOSONAR:{checked_above}
          dbConn.getDriver()).setProperty("hibernate.dialect",
          ((HibernateConnectionData) dbConn).getDialect());
    }
    // .setProperty("hibernate.jdbc.batch_size", "20");
  }

  /**
   * Clears the database, then re-opens it to avoid entities created by the
   * previous session to interfere with the new database.
   */
  @Override
  public void clear() {
    SimSystem.report(Level.INFO, "Clearing database...");
    SchemaExport export = new SchemaExport(config);
    export.create(false, true);
    close();
    open();
  }

  @Override
  public void close() {
    if (session != null) {
      session.flush();
      session.close();
    }
    if (sessionFactory != null) {
      sessionFactory.close();
    }
    session = null;
    sessionFactory = null;
  }

  @Override
  public void create() {
    sessionFactory = config.buildSessionFactory();
  }

  @Override
  public void open() {
    if (session == null) {
      if (sessionFactory == null) {
        create();
      }
      session = sessionFactory.openSession();
      SchemaExport export = new SchemaExport(config);
      export.execute(false, true, false, true);
    }
    if (session != null && !session.isOpen()) {
      sessionFactory = config.buildSessionFactory();
    }
    session = sessionFactory.openSession();

    try {
      registerNewPerformanceMeasures();
      registerNewFeatureTypes();
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Failed to initialize performance metrics.", ex);
    }
  }

  /**
   * Gets the connection data.
   * 
   * @return the connection data
   */
  public DBConnectionData getDbConnectionData() {
    return dbConn;
  }

  /**
   * Register new performance measures.
   */
  private void registerNewPerformanceMeasures() {
    List<IPerformanceType> perfMeasures = getAllPerformanceTypes();
    Collection<PerformanceMeasurerFactory> newPerfMeasurerFactories =
        PerfDB.getNewPerfTypeFactories(perfMeasures);
    for (PerformanceMeasurerFactory pmFac : newPerfMeasurerFactories) {
      newPerformanceType(pmFac.getMeasurementName(),
          pmFac.getMeasurementDescription(), pmFac.getClass());
    }
  }

  /**
   * Register new feature types.
   */
  private void registerNewFeatureTypes() {
    List<IFeatureType> featureTypes = getAllFeatureTypes();
    Collection<FeatureExtractorFactory> newFeatureTypeFactories =
        PerfDB.getNewFeatureTypeFactories(featureTypes);
    for (FeatureExtractorFactory factory : newFeatureTypeFactories) {
      newFeatureType(factory.getFeatureName(), factory.getFeatureDescription(),
          factory.getClass());
    }
  }

  // Problem schemes

  @Override
  public boolean deleteProblemScheme(IProblemScheme scheme) {
    delete(scheme);
    return true;
  }

  @Override
  public List<IProblemScheme> getAllProblemSchemes() {
    List<IProblemScheme> schemes =
        Generics.autoCast(session.createQuery("from ProblemScheme").list());
    return schemes;
  }

  @Override
  public IProblemScheme getProblemScheme(URI uri) {
    List<IProblemScheme> schemes =
        Generics.autoCast(session.createQuery(
            "from ProblemScheme where uri='" + uri.toASCIIString() + "'")
            .list());
    if (schemes.size() > 1) {
      throw new ConstraintException(
          "Ambiguity: there are two probblem schemes that share the same uri ' "
              + uri.toASCIIString() + "'");
    }
    if (schemes.size() == 1) {
      return schemes.get(0);
    }
    return null;
  }

  @Override
  public IProblemScheme newProblemScheme(URI uri, String name, String type,
      String description) {
    IProblemScheme scheme = getProblemScheme(uri);
    if (scheme == null) {
      scheme = new ProblemScheme(name, description, uri, type);
      save(scheme);
    }
    return scheme;
  }

  // Hardware

  @Override
  public IMachine newMachine(String name, String desc, String macAddress,
      double sciMark) {
    Machine m = new Machine(name, desc, macAddress, sciMark);
    save(m);
    return m;
  }

  @Override
  public List<IMachine> getAllMachines() {
    List<IMachine> machines =
        Generics.autoCast(session.createQuery("from Machine").list());
    return machines;
  }

  @Override
  public List<IMachine> getAllMachines(String macAddress) {
    List<IMachine> machines =
        Generics.autoCast(session.createQuery(
            "from Machine where macAddress='" + macAddress + "'").list());
    return machines;
  }

  @Override
  public IHardwareSetup newHardwareSetup(String name, String desc,
      String nwTopology, long nwSpeed, Set<IMachine> machines) {
    Iterator<?> results =
        session.createQuery(
            "from HardwareSetup where networkTopology='" + nwTopology
                + "' and networkSpeed=" + nwSpeed).iterate();
    while (results.hasNext()) {
      IHardwareSetup setup = (IHardwareSetup) results.next();
      if (setup.getMachines().equals(machines)) {
        return setup;
      }
    }
    HardwareSetup hwSetup =
        new HardwareSetup(name, desc, nwTopology, nwSpeed, machines);
    save(hwSetup);
    return hwSetup;
  }

  @Override
  public boolean deleteHardwareSetup(IHardwareSetup setup) {
    delete(setup);
    return true;
  }

  @Override
  public boolean deleteMachine(IMachine machine) {
    if (getSetupCount(machine) > 0) {
      return false;
    }
    delete(machine);
    return true;
  }

  @Override
  public int getSetupCount(IMachine machine) {
    return ((Number) session
        .createQuery(
            "select count(s) from HardwareSetup s, Machine m where m in elements(s.machines) and m.ID="
                + machine.getID()).iterate().next()).intValue();
  }

  @Override
  public List<IHardwareSetup> getAllHardwareSetups() {
    List<IHardwareSetup> setups =
        Generics.autoCast(session.createQuery("from HardwareSetup").list());
    return setups;
  }

  // Problem definitions

  @Override
  public List<IProblemDefinition> getAllProblemDefinitions() {
    List<IProblemDefinition> problems =
        Generics.autoCast(session.createQuery("from ProblemDefinition").list());
    return problems;
  }

  @Override
  public List<IProblemDefinition> getAllProblemDefinitions(IProblemScheme scheme) {
    List<IProblemDefinition> problems =
        Generics.autoCast(session.createCriteria(ProblemDefinition.class)
            .add(Restrictions.eq("scheme", scheme)).list());
    return problems;
  }

  @Override
  public IProblemDefinition getProblemDefinition(IProblemScheme scheme,
      Map<String, Serializable> definitionParameters,
      Map<String, Serializable> schemeParameters) {
    Criteria c =
        session
            .createCriteria(ProblemDefinition.class)
            .add(Restrictions.eq("scheme", scheme))
            .add(
                Restrictions.eq("schemeParametersHash",
                    ProblemDefinition.calcParametersHash(schemeParameters)))
            .add(
                Restrictions.eq("definitionParametersHash",
                    ProblemDefinition.calcParametersHash(definitionParameters)));

    List<IProblemDefinition> problemDefinitions = Generics.autoCast(c.list());
    for (IProblemDefinition def : problemDefinitions) {
      if (checkParameterEquality(def.getSchemeParameters(), schemeParameters)
          && checkParameterEquality(def.getDefinitionParameters(),
              definitionParameters)) {
        return def;
      }
    }
    return null;
  }

  /**
   * Check parameter equality.
   * 
   * @param parameters1
   *          the first set of parameters
   * @param parameters2
   *          the second set of parameters
   * @return true, if parameters are equal
   */
  private boolean checkParameterEquality(Map<String, Serializable> parameters1,
      Map<String, Serializable> parameters2) {
    if (parameters1.size() != parameters2.size()) {
      return false;
    }
    List<String> keys = new ArrayList<>(parameters1.keySet());
    Collections.sort(keys);
    for (String key : keys) {
      if (!checkParameterValueEquality(parameters1.get(key),
          parameters2.get(key))) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks parameter value equality. {@link ParameterBlock} instances are
   * handled differently, they are checked by
   * {@link ParameterBlocks#structuralEqualityParamBlocks(ParameterBlock, ParameterBlock)}
   * .
   * 
   * @see ParameterBlocks
   * 
   * @param object1
   *          the first object
   * @param object2
   *          the second object
   * @return true, if objects are equal
   */
  private boolean checkParameterValueEquality(Serializable object1,
      Serializable object2) {
    if (object1 == null && object2 == null) {
      return true;
    }
    if (object1 == null || object2 == null) {
      return false;
    }

    if (object1 instanceof ParameterBlock && object2 instanceof ParameterBlock) {
      return ParameterBlocks.structuralEqualityParamBlocks(
          (ParameterBlock) object1, (ParameterBlock) object2);
    }

    return object1.equals(object2);
  }

  @Override
  public IProblemDefinition newProblemDefinition(IProblemScheme scheme,
      Map<String, Serializable> definitionParameters,
      Map<String, Serializable> schemeParameters) {
    IProblemDefinition problemDefinition =
        getProblemDefinition(scheme, definitionParameters, schemeParameters);
    if (problemDefinition != null) {
      return problemDefinition;
    }
    checkForHibernateEntities(new Object[] { scheme }, ProblemScheme.class);
    problemDefinition = // NOSONAR:{checked_above}
        new ProblemDefinition((ProblemScheme) scheme, definitionParameters,
            schemeParameters);
    save(problemDefinition);
    return problemDefinition;
  }

  @Override
  public boolean deleteProblemDefinition(IProblemDefinition problemDefinition) {
    // TODO: Change hibernate mapping to facilitate this (on-delete-cascade?)
    for (IProblemInstance problemInstance : getAllProblemInstances(problemDefinition)) {
      for (IApplication app : getAllApplications(problemInstance)) {
        for (IPerformance perf : getAllPerformances(app)) {
          delete(perf);
        }
        delete(app);
      }
      delete(problemInstance);
    }
    delete(problemDefinition);
    return true;
  }

  @Override
  public IProblemInstance getProblemInstance(
      IProblemDefinition problemDefinition, long randSeed) {
    Criteria c =
        session.createCriteria(ProblemInstance.class)
            .add(Restrictions.eq("problemDef", problemDefinition))
            .add(Restrictions.eq("randomSeed", randSeed));
    List<IProblemInstance> instances = Generics.autoCast(c.list());
    if (instances.size() > 1) {
      throw new ConstraintException(
          "Ambiguity: there are two problem instances for problem #"
              + problemDefinition.getID() + " with rand seed:" + randSeed);
    }
    if (instances.size() == 1) {
      return instances.get(0);
    }
    return null;

  }

  @Override
  public IProblemInstance newProblemInstance(
      IProblemDefinition problemDefinition, long randSeed, String rngFactoryName) {
    IProblemInstance pi = getProblemInstance(problemDefinition, randSeed);
    if (pi == null) {
      checkForHibernateEntities(new Object[] { problemDefinition },
          ProblemDefinition.class);
      pi = // NOSONAR:{checked_above}
          new ProblemInstance((ProblemDefinition) problemDefinition, randSeed,
              rngFactoryName);
      save(pi);
    }
    return pi;
  }

  @Override
  public boolean deleteProblemInstance(IProblemInstance problemInst) {
    delete(problemInst);
    return true;
  }

  @Override
  public List<IProblemInstance> getAllProblemInstances(
      IProblemDefinition problemDefinition) {
    List<IProblemInstance> insts =
        Generics.autoCast(session.createQuery(
            "from ProblemInstance where problem_id="
                + problemDefinition.getID() + "").list());
    return insts;
  }

  // Runtime Configurations

  @Override
  public IRuntimeConfiguration getRuntimeConfig(SelectionTree selectionTree) {
    List<IRuntimeConfiguration> configs =
        Generics.autoCast(session.createCriteria(RuntimeConfiguration.class)
            .add(Restrictions.eq("selectionTreeHash", selectionTree.getHash()))
            .add(Restrictions.eq("upToDate", true)).list());
    for (IRuntimeConfiguration conf : configs) {
      if (conf.getSelectionTree().equals(selectionTree) && conf.isUpToDate()) {
        return conf;
      }
    }
    return null;
  }

  @Override
  public IRuntimeConfiguration newRuntimeConfiguration(
      SelectionTree selectionTree, boolean newVersion) {
    IRuntimeConfiguration rtConf = getRuntimeConfig(selectionTree);

    // Create configuration if there is none
    if (rtConf == null) {
      rtConf =
          new RuntimeConfiguration(selectionTree, Calendar.getInstance()
              .getTime(), 1);
      save(rtConf);
      return rtConf;
    }

    // Otherwise return current configuration...
    if (!newVersion) {
      return rtConf;
    }

    // ... or go to the next version
    int version = rtConf.getVersion() + 1;
    ((RuntimeConfiguration) rtConf).markOutdated();
    IRuntimeConfiguration newRTConfig =
        new RuntimeConfiguration(selectionTree, Calendar.getInstance()
            .getTime(), version);
    save(rtConf);
    save(newRTConfig);
    return newRTConfig;
  }

  @Override
  public List<IRuntimeConfiguration> getAllRuntimeConfigs(
      IProblemDefinition problemDefinition) {
    List<IRuntimeConfiguration> configs =
        Generics.autoCast(session.createQuery(
            HQL_QUERY_RTC + problemDefinition.getID()).list());
    return configs;
  }

  @Override
  public List<IRuntimeConfiguration> getAllCurrentRTConfigs(
      IProblemDefinition problemDefinition) {
    List<IRuntimeConfiguration> configs =
        Generics.autoCast(session.createQuery(
            HQL_QUERY_RTC + problemDefinition.getID()
                + " and a.runtimeConfig.upToDate = true").list());
    return configs;
  }

  @Override
  public List<IRuntimeConfiguration> getAllRuntimeConfigs() {
    List<IRuntimeConfiguration> configs =
        Generics.autoCast(session.createCriteria(RuntimeConfiguration.class)
            .list());
    return configs;
  }

  // Applications

  @Override
  public List<IApplication> getAllApplications(IProblemInstance probInstance) {
    List<IApplication> apps =
        Generics.autoCast(session.createCriteria(Application.class)
            .add(Restrictions.eq("problemInst", probInstance)).list());
    return apps;
  }

  @Override
  public List<IApplication> getAllApplications(
      IProblemDefinition problemDefinition) {
    List<IApplication> apps =
        Generics
            .autoCast(session
                .createQuery(
                    "select distinct a from Application a left join fetch a.problemInst left join fetch a.problemInst.problemDef where a.problemInst.problemDef.ID="
                        + problemDefinition.getID()).list());
    return apps;
  }

  @Override
  public List<IApplication> getAllApplications(
      IProblemDefinition problemDefinition, IRuntimeConfiguration rtConfig) {
    List<IApplication> applications = new ArrayList<>();
    List<IProblemInstance> instances =
        getAllProblemInstances(problemDefinition);
    for (IProblemInstance instance : instances) {
      applications.addAll(getAllApplications(instance, rtConfig));
    }
    return applications;
  }

  @Override
  public List<IApplication> getAllApplications(IProblemInstance instance,
      IRuntimeConfiguration rtConfig) {
    List<IApplication> apps =
        Generics.autoCast(session.createCriteria(Application.class)
            .add(Restrictions.eq("problemInst", instance))
            .add(Restrictions.eq("runtimeConfig", rtConfig)).list());
    return apps;
  }

  @Override
  public List<IRuntimeConfiguration> getAllRuntimeConfigurations(
      IProblemInstance probInstance) {
    List<IRuntimeConfiguration> configs =
        Generics
            .autoCast(session
                .createQuery(
                    "select distinct r from RuntimeConfiguration r, Application a, ProblemInstance i where a.runtimeConfig.ID = r.ID and a.problemInst.ID="
                        + probInstance.getID()).list());
    return configs;
  }

  @Override
  public IApplication newApplication(IProblemInstance probInst,
      IRuntimeConfiguration rtConfig, IHardwareSetup hwSetup,
      IResultDataProvider<?> sdProvider) {
    checkForHibernateEntities(new Object[] { probInst, rtConfig, hwSetup },
        ProblemInstance.class, RuntimeConfiguration.class, HardwareSetup.class);
    Application a =
        new Application((ProblemInstance) probInst,
            (RuntimeConfiguration) rtConfig, (HardwareSetup) hwSetup,
            sdProvider, Calendar.getInstance().getTime());
    save(a);
    return a;
  }

  // Features

  @Override
  public List<IFeatureType> getAllFeatureTypes() {
    List<IFeatureType> featTypes =
        Generics.autoCast(session.createQuery("from FeatureType").list());
    return featTypes;
  }

  @Override
  public IFeatureType getFeatureForFactory(
      Class<? extends FeatureExtractorFactory> factory) {
    List<IFeatureType> featTypes =
        Generics.autoCast(session.createCriteria(FeatureType.class)
            .add(Restrictions.eq("featureExtractorFactory", factory)).list());
    if (featTypes.size() > 1) {
      throw new ConstraintException(
          "Ambiguity: there are at least two feature types that share the same factory '"
              + factory + "': #" + featTypes.get(0).getID() + " and #"
              + featTypes.get(1).getID());
    }
    if (featTypes.size() == 1) {
      return featTypes.get(0);
    }

    return null;
  }

  @Override
  public IFeatureType newFeatureType(String typeName, String typeDesc,
      Class<? extends FeatureExtractorFactory> factoryClass) {
    IFeatureType feat = getFeatureForFactory(factoryClass);
    if (feat != null) {
      return feat;
    }
    feat = new FeatureType(typeName, typeDesc, factoryClass);
    save(feat);
    return feat;
  }

  @Override
  public List<IFeature> getAllFeatures(IApplication app) {
    List<IFeature> features =
        Generics.autoCast(session.createCriteria(Feature.class)
            .add(Restrictions.eq(APP_NAME, app)).list());
    return features;
  }

  @Override
  public IFeature getFeature(IApplication app, IFeatureType featureType) {
    List<IFeature> features =
        Generics.autoCast(session.createCriteria(Feature.class)
            .add(Restrictions.eq(APP_NAME, app))
            .add(Restrictions.eq("featType", featureType)).list());
    if (features.size() > 1) {
      throw new ConstraintException(
          "Ambiguity: there are two feature entries for the same feature type ' "
              + featureType.getName() + "' and application #" + app.getID()
              + ": #" + features.get(0).getID() + " and #"
              + features.get(1).getID());
    }
    if (features.size() == 1) {
      return features.get(0);
    }
    return null;
  }

  @Override
  public IFeature newFeature(IApplication app, IFeatureType featureType,
      IFeatureExtractor<ParameterBlock> extractor) {
    return newFeature(app, featureType,
        extractor.extractFeatures(new ParameterBlock(app,
            FeatureExtractorFactory.PROBLEM_REPRESENTATION)));
  }

  @Override
  public IFeature newFeature(IApplication app, IFeatureType featureType,
      Map<String, Serializable> featureValues) {
    IFeature feat = getFeature(app, featureType);
    if (feat != null) {
      return feat;
    }
    checkForHibernateEntities(new Object[] { featureType, app },
        FeatureType.class, Application.class);
    feat = // NOSONAR:{checked_above}
        new Feature((FeatureType) featureType, (Application) app, featureValues);
    save(feat);
    return feat;
  }

  // Performance

  @Override
  public List<IPerformanceType> getAllPerformanceTypes() {
    List<IPerformanceType> perfTypes =
        Generics.autoCast(session.createQuery("from PerformanceType").list());
    return perfTypes;
  }

  @Override
  public IPerformanceType getPerformanceType(
      Class<? extends PerformanceMeasurerFactory> factory) {
    List<IPerformanceType> perfTypes =
        Generics
            .autoCast(session.createCriteria(PerformanceType.class)
                .add(Restrictions.eq("performanceMeasurerFactory", factory))
                .list());

    if (perfTypes.size() > 1) {
      throw new IllegalStateException(
          "Ambiguity: there are at least two performance types that share the same measurer factory '"
              + factory
              + "': #"
              + perfTypes.get(0).getID()
              + " and #"
              + perfTypes.get(1).getID());
    }

    if (perfTypes.size() == 1) {
      return perfTypes.get(0);
    }

    return null;
  }

  @Override
  public IPerformanceType newPerformanceType(String typeName, String typeDesc,
      Class<? extends PerformanceMeasurerFactory> measurerClass) {
    IPerformanceType pm = getPerformanceType(measurerClass);
    if (pm != null) {
      return pm;
    }
    pm = new PerformanceType(typeName, typeDesc, measurerClass);
    save(pm);
    return pm;
  }

  @Override
  public IPerformance newPerformance(IApplication app,
      IPerformanceType performanceMeasure, double performance) {
    checkForHibernateEntities(new Object[] { performanceMeasure, app },
        PerformanceType.class, Application.class);
    Performance pm = // NOSONAR:{checked_above}
        new Performance((Application) app,
            (PerformanceType) performanceMeasure, performance);
    save(pm);
    return pm;
  }

  @Override
  public List<IPerformance> getAllPerformances(IApplication application) {
    List<IPerformance> performances =
        Generics.autoCast(session.createCriteria(Performance.class)
            .add(Restrictions.eq(APP_NAME, application)).list());
    return performances;
  }

  @Override
  public Map<Long, Double> getAllPerformancesMap(IPerformanceType perfType) {
    Map<Long, Double> resultMap = new HashMap<>(500000);
    List<IPerformance> performances =
        Generics.autoCast(session.createCriteria(Performance.class)
            .add(Restrictions.eq("perfType", perfType)).list());
    for (IPerformance perf : performances) {
      resultMap.put(perf.getApplication().getID(), perf.getPerformance());
    }
    flush();
    return resultMap;
  }

  @Override
  public IPerformance getPerformance(IApplication application,
      IPerformanceType perfType) {
    List<IPerformance> performances =
        Generics.autoCast(session.createCriteria(Performance.class)
            .add(Restrictions.eq(APP_NAME, application))
            .add(Restrictions.eq("perfType", perfType)).list());
    if (performances.isEmpty()) {
      return null;
    }
    if (performances.size() > 1) {
      throw new IllegalStateException("Two performance of type '"
          + perfType.getName() + "' stored for applications with ID "
          + application.getID());
    }
    return performances.get(0);
  }

  /**
   * DB-write hook for switching flushing on or off.
   */
  protected void dbChanged() {
    if (alwaysFlush) {
      session.flush();
    }
  }

  /**
   * Saves an object with hibernate.
   * 
   * @param o
   *          the object to be saved
   */
  protected void save(Object o) {
    Transaction t = session.beginTransaction();
    session.save(o);
    t.commit();
    dbChanged();
  }

  /**
   * Deletes an object with hibernate.
   * 
   * @param o
   *          the object to be deleted
   */
  protected void delete(Object o) {
    Transaction t = session.beginTransaction();
    session.delete(o);
    t.commit();
    dbChanged();
  }

  @Override
  public void flush() {
    session.flush();
    session.clear();
  }

  /**
   * Checks for suitable hibernate entities.
   * 
   * @param objects
   *          the objects to be checked
   * @param types
   *          the types they should have
   */
  protected static void checkForHibernateEntities(Object[] objects,
      Class<?>... types) {
    if (objects.length != types.length) {
      throw new IllegalArgumentException(
          "Passed arrays need to have the same length");
    }
    for (int i = 0; i < objects.length; i++) {
      if (objects[i] != null
          && !types[i].isAssignableFrom(objects[i].getClass())) {
        throw new DatabaseAccessException(HIBERNATE_ENTITY_ERROR
            + "Not the object of type " + objects[i].getClass());
      }
    }
  }

}
