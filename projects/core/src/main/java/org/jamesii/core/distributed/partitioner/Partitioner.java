/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.distributed.partitioner.infrastructureanalyzer.AbstractInfrasctructureAnalyzer;
import org.jamesii.core.distributed.partitioner.infrastructureanalyzer.plugintype.AbstractInfrastructureAnalyzerFactory;
import org.jamesii.core.distributed.partitioner.infrastructureanalyzer.plugintype.InfrastructureAnalyzerFactory;
import org.jamesii.core.distributed.partitioner.modelanalyzer.AbstractModelAnalyzer;
import org.jamesii.core.distributed.partitioner.modelanalyzer.plugintype.AbstractModelAnalyzerFactory;
import org.jamesii.core.distributed.partitioner.modelanalyzer.plugintype.ModelAnalyzerFactory;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractPartitioningAlgorithm;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.AbstractPartitioningFactory;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.PartitioningFactory;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.graph.ISimpleGraph;
import org.jamesii.core.util.misc.Strings;

/**
 * Main class of the partitioning framework. Calls the factories creation
 * functions to obtain suitable factories, then uses the factory to get suitable
 * infrastructure analysis -, model analysis - and partitioning algorithms.
 * 
 * Created Nov 15, 2004
 * 
 * @author Roland Ewald
 */
public class Partitioner {

  /**
   * List of all available infrastructure analyzer factories for the current
   * task. (Hardware analysis)
   */
  private List<InfrastructureAnalyzerFactory> infrastructAnalysisFactories;

  /** Infrastructure analyzer. (Hardware analysis) */
  private AbstractInfrasctructureAnalyzer infrastructureAnalyzer = null;

  /** Infrastructure graph. (Hardware analysis) */
  private ISimpleGraph infrastructureGraph = null;

  /**
   * List of all available model analyzer factories for the current task. (model
   * analysis)
   */
  private List<ModelAnalyzerFactory> modelAnalyzerFactories;

  /** Model analyzer. (model analysis) */
  private AbstractModelAnalyzer modelAnalyzer = null;

  /** Model graph. (model analysis) */
  private ISimpleGraph modelGraph = null;

  /** Partitioning algorithm. */
  private AbstractPartitioningAlgorithm partitioningAlgorithm = null;

  /** Partition mapping of the current task. */
  private PartitionMapping partitionMapping = null;

  /** List of all available partitioning factories for the current task. */
  private List<PartitioningFactory> partitioningFactories;

  /**
   * Gets the infrastruct analysis factories.
   * 
   * @return the infrastruct analysis factories
   */
  public List<InfrastructureAnalyzerFactory> getInfrastructAnalysisFactories() {
    return infrastructAnalysisFactories;
  }

  /**
   * Gets the infrastructure analyzer.
   * 
   * @return the infrastructure analyzer
   */
  public AbstractInfrasctructureAnalyzer getInfrastructureAnalyzer() {
    return infrastructureAnalyzer;
  }

  /**
   * Gets the infrastructure graph.
   * 
   * @return the infrastructure graph
   */
  public ISimpleGraph getInfrastructureGraph() {
    return infrastructureGraph;
  }

  /**
   * Gets the ma factories.
   * 
   * @return the ma factories
   */
  public List<ModelAnalyzerFactory> getMaFactories() {
    return modelAnalyzerFactories;
  }

  /**
   * Gets the model analyzer.
   * 
   * @return the model analyzer
   */
  public AbstractModelAnalyzer getModelAnalyzer() {
    return modelAnalyzer;
  }

  /**
   * Gets the model graph.
   * 
   * @return the model graph
   */
  public ISimpleGraph getModelGraph() {
    return modelGraph;
  }

  /**
   * Gets the partitioning algorithm.
   * 
   * @return the partitioning algorithm
   */
  public AbstractPartitioningAlgorithm getPartitioningAlgorithm() {
    return partitioningAlgorithm;
  }

  /**
   * Gets the partitioning factories.
   * 
   * @return the partitioning factories
   */
  public List<PartitioningFactory> getPartitioningFactories() {
    return partitioningFactories;
  }

  /**
   * Main function of the framework.
   * 
   * @param model
   *          the model to be partitioned
   * @param resources
   *          the resourced ready for parallel execution
   * @param parameters
   *          partitioning parameters
   * 
   * @return partition mapping
   */
  public Partition partitionize(IModel model,
      List<ISimulationServer> resources, ParameterBlock parameters) {

    if (resources == null) {
      return new Partition(model, null, null);
    }

    if (resources.isEmpty()) {
      throw new RuntimeException(
          "No ressources available! Cannot create a distributed run.");
    }

    Partition singlePartition = new Partition(model, resources.get(0), null);

    if (resources.size() == 1) {
      return singlePartition;
    }

    // Get filtered algorithms for each task at first
    try {

      ParameterBlock maParameters =
          ParameterBlocks.getSBOrEmpty(parameters,
              ModelAnalyzerFactory.class.getName()).addSubBl(
              AbstractModelAnalyzerFactory.MODEL, model);
      modelAnalyzerFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelAnalyzerFactory.class, maParameters);

      ParameterBlock iaParameters =
          ParameterBlocks.getSBOrEmpty(parameters,
              InfrastructureAnalyzerFactory.class.getName());
      infrastructAnalysisFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractInfrastructureAnalyzerFactory.class, iaParameters);

      ParameterBlock partParameters =
          ParameterBlocks.getSBOrEmpty(parameters,
              PartitioningFactory.class.getName()).addSubBl(
              AbstractPartitioningFactory.MODEL, model);
      partitioningFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractPartitioningFactory.class, partParameters);
    } catch (Exception ex) {
      SimSystem.report(ex);
      return singlePartition;
    }

    PartFactorySetup setup = getFactories();

    if (!setup.isValid()) {
      return singlePartition;
    }

    // Getting infrastructure analyzing algorithm
    infrastructureAnalyzer = setup.infFactory.create(resources);

    // Getting model analyzing algorithm
    modelAnalyzer = setup.modFactory.createFromModel(model);

    // Executing algorithms
    infrastructureGraph =
        infrastructureAnalyzer.analyzeInfrastrcutre(resources);

    modelGraph = modelAnalyzer.analyzeModel(model);

    // Getting adequate partitioning algorithm
    partitioningAlgorithm =
        setup.pFactory.create(model, modelGraph, infrastructureGraph);

    // Status report:
    Entity.report("Partitioner uses: ");
    Entity.report("\tInfrastructure analysis:"
        + infrastructureAnalyzer.getClass());
    Entity.report("\tModel analysis:" + modelAnalyzer.getClass());
    Entity.report("\tPartitioning algorithm:"
        + partitioningAlgorithm.getClass());

    partitioningAlgorithm.initializePartitioning(infrastructureGraph,
        modelGraph);

    partitionMapping = partitioningAlgorithm.calculatePartition();

    reconcileMapping(partitionMapping);

    SimSystem.report(Level.FINEST,
        "PARTITION: " + Strings.dispMap(partitionMapping));

    return setup.modFactory.getExecutablePartitionForModel(model, modelGraph,
        infrastructureGraph, partitionMapping);

  }

  /**
   * Reconciles the generated partitioning mapping with the current execution
   * mode of James II. It basically makes sure that the root model (index 0) is
   * mapped onto the current host, i.e. it swaps the root model's partition and
   * with that of the current host.
   * 
   * FIXME: This method is necessary because partitioning is already done on a
   * given host and it is presumed that this one will execute the topmost model.
   * 
   * @param partitionMap
   *          the actual partition mapping
   */
  private void reconcileMapping(PartitionMapping partitionMap) {
    int partitionOfTopMostModel = partitionMap.get(0);
    if (partitionOfTopMostModel != 0) {
      for (Map.Entry<Integer, Integer> node : partitionMap.entrySet()) {
        if (node.getValue() == 0) {
          partitionMap.put(node.getKey(), partitionOfTopMostModel);
        } else if (node.getValue() == partitionOfTopMostModel) {
          partitionMap.put(node.getKey(), 0);
        }
      }
      SimSystem.report(Level.WARNING,
          "Re-mapped partitioning to let root model run on local host.");
    }
  }

  /**
   * Looks for a suitable combination of model- and infrastructure analyzer for
   * all available partitioning schemes.
   * 
   * @return viable setup, if exists, test with
   *         {@link PartFactorySetup#isValid()}
   */
  protected PartFactorySetup getFactories() {

    PartitioningFactory currentPFactory = null;
    ModelAnalyzerFactory suitableMAFactory = null;
    InfrastructureAnalyzerFactory suitableIAFactory = null;

    int numOfPFactories = partitioningFactories.size();
    int numOfMAFactories = modelAnalyzerFactories.size();
    int numOfIAFactories = 0;
    if (infrastructAnalysisFactories != null) {
      numOfIAFactories = infrastructAnalysisFactories.size();
    }

    for (int i = 0; i < numOfPFactories; i++) {

      currentPFactory = partitioningFactories.get(i);
      suitableMAFactory = null;
      suitableIAFactory = null;

      // Check whether there is a compatible model analyzer algorithm
      for (int j = 0; j < numOfMAFactories; j++) {
        ModelAnalyzerFactory currentMAFactory = modelAnalyzerFactories.get(j);
        if (currentPFactory.supportsModelGraphLabels(
            currentMAFactory.getGeneratedEdgeLabel(),
            currentMAFactory.getGeneratedVertexLabel())) {
          suitableMAFactory = currentMAFactory;
          break;
        }
      }

      if (suitableMAFactory == null) {
        continue;
      }

      // Check whether there is a compatible hardware analyzer algorithm
      for (int j = 0; j < numOfIAFactories; j++) {
        InfrastructureAnalyzerFactory currentIAFactory =
            infrastructAnalysisFactories.get(j);
        if (currentPFactory.supportsHardwareGraphLabels(
            currentIAFactory.getGeneratedEdgeLabel(),
            currentIAFactory.getGeneratedVertexLabel())) {
          suitableIAFactory = currentIAFactory;
          break;
        }
      }

      if (suitableIAFactory != null) {
        break;
      }
    }

    return new PartFactorySetup(suitableMAFactory, suitableIAFactory,
        currentPFactory);
  }

  /**
   * Class that contains a suitable combination of partitioning algorithms and
   * model/infrastructure analyzers.
   */
  private class PartFactorySetup {

    /** Model analyzer factory. */
    private ModelAnalyzerFactory modFactory;

    /** Infrastructure analyzer factory. */
    private InfrastructureAnalyzerFactory infFactory;

    /** Partitioning factory. */
    private PartitioningFactory pFactory;

    /**
     * Instantiates a new part factory setup.
     * 
     * @param mFac
     *          the m fac
     * @param iFac
     *          the i fac
     * @param pFac
     *          the fac
     */
    PartFactorySetup(ModelAnalyzerFactory mFac,
        InfrastructureAnalyzerFactory iFac, PartitioningFactory pFac) {
      modFactory = mFac;
      infFactory = iFac;
      pFactory = pFac;
    }

    /**
     * Tests whether a suitable combination is set.
     * 
     * @return true, if this setup is valid
     */
    boolean isValid() {
      return modFactory != null && infFactory != null && pFactory != null;
    }
  }

}