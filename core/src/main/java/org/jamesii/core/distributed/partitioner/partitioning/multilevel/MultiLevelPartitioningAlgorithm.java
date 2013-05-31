/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partitioner.PartitionMapping;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractPartitioningAlgorithm;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion.AbortCriterionFactory;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion.AbstractAbortCriterion;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion.AbstractAbortCriterionFactory;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.coarsening.AbstractCoarsenAlgorithm;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.coarsening.AbstractCoarsenFactory;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.coarsening.CoarsenFactory;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.refining.AbstractRefineAlgorithm;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.refining.AbstractRefineFactory;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.refining.RefineFactory;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.AbstractPartitioningFactory;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.PartitioningFactory;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.AbstractPartitioningFactory.PartitionerType;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * AbstractMultiLevelPartitioningAlgorithm.
 * 
 * All multi-level partitioning algorithms should be derived from this class. It
 * consists of a single-level partitioning algorithm and a
 * coarse/refine-algorithms, as well as a criterion to define when to stop
 * coarsening. This is the strategy pattern.
 * 
 * @see AbstractCoarsenAlgorithm
 * @see AbstractRefineAlgorithm
 * @see AbstractAbortCriterion
 * @see AbstractPartitioningAlgorithm
 * 
 * @author Ragnar Nevries
 */
public class MultiLevelPartitioningAlgorithm extends
    AbstractPartitioningAlgorithm {

  /** Reference to the model. */
  private IModel model;

  /** Reference to model graph. */
  private ISimpleGraph modelGraph;

  /** Reference to hardware graph. */
  private ISimpleGraph hardwareGraph;

  /** The coarsening algorithm. */
  private AbstractCoarsenAlgorithm coarseningAlgo;

  /** The refinement algorithm. */
  private AbstractRefineAlgorithm refineAlgorithm;

  /** The abort criterion. */
  private AbstractAbortCriterion abortCriterion;

  /** The partitioning algorithm. */
  private AbstractPartitioningAlgorithm singleLevelPartitioningAlgo;

  /** Coarse graph (for debugging purposes). */
  private ISimpleGraph coarseGraph;

  /** Partitioning for coarse graph (for debugging purposes). */
  private PartitionMapping coarseMapping;

  /**
   * Default constructor.
   * 
   * @param mod
   *          the model
   */
  public MultiLevelPartitioningAlgorithm(IModel mod) {
    model = mod;
  }

  @Override
  public PartitionMapping calculatePartition() {

    // Coarsen graph
    coarseningAlgo.coarsenGraph();

    coarseGraph =
        coarseningAlgo.getGraphAtLevel(coarseningAlgo.getActualLevel());

    // Initialize single-level algorithm and partition
    if (!singleLevelPartitioningAlgo.initializePartitioning(hardwareGraph,
        coarseGraph)) {
      throw new RuntimeException(
          "ML-Partitioning: Could not initialize single-level partitioner!");
    }

    coarseMapping = singleLevelPartitioningAlgo.calculatePartition();

    // Refine with partition results and coarsening information
    RefineFactory rFactory =
        SimSystem.getRegistry().getFactory(AbstractRefineFactory.class, null);
    refineAlgorithm =
        rFactory.getRefineAlgorithm(coarseningAlgo.getAllCoarsenGraphs(),
            coarseningAlgo.getAllCoarsenMappings());

    return refineAlgorithm.unCoarsenGraph(coarseMapping);
  }

  @Override
  public boolean initializePartitioning(ISimpleGraph hw, ISimpleGraph mod) {

    hardwareGraph = hw;
    modelGraph = mod;

    try {
      // Choose abort criterion
      AbortCriterionFactory acFactory =
          SimSystem.getRegistry().getFactory(
              AbstractAbortCriterionFactory.class, null);
      abortCriterion = acFactory.getAbortCriterion();

      // Choose coarsening algorithm
      CoarsenFactory crFactory =
          SimSystem.getRegistry()
              .getFactory(AbstractCoarsenFactory.class, null);
      coarseningAlgo =
          crFactory.getCoarsenAlgorithm(modelGraph, abortCriterion);

      ParameterBlock pb = new ParameterBlock();
      pb.addSubBlock("partitionerType", new ParameterBlock(
          PartitionerType.SINGLE_LEVEL_ONLY));

      // Choose single-level partitioner
      // The model is not given as it won't help the partitioner, he gets the
      // coarsened graph!
      PartitioningFactory pFactory =
          SimSystem.getRegistry().getFactory(AbstractPartitioningFactory.class,
              pb);
      singleLevelPartitioningAlgo =
          pFactory.create(null, modelGraph, hardwareGraph);
    } catch (Exception ex) {
      SimSystem.report(ex);
      return false;
    }

    return true;
  }

  @Override
  public boolean initializePartitioning(ISimpleGraph hw, ISimpleGraph mod,
      PartitionMapping oldPartition) {
    // we don't remember partitions
    return initializePartitioning(hw, mod);
  }

  /**
   * Gets the coarsening algorithm.
   * 
   * @return the coarsening algo
   */
  public AbstractCoarsenAlgorithm getCoarseningAlgo() {
    return coarseningAlgo;
  }

  /**
   * Sets the coarsening algo.
   * 
   * @param algo
   *          the new coarsening algo
   */
  public void setCoarseningAlgo(AbstractCoarsenAlgorithm algo) {
    coarseningAlgo = algo;
  }

  /**
   * Gets the single level partitioning algo.
   * 
   * @return the single level partitioning algo
   */
  public AbstractPartitioningAlgorithm getSingleLevelPartitioningAlgo() {
    return singleLevelPartitioningAlgo;
  }

  /**
   * Sets the single level partitioning algo.
   * 
   * @param algo
   *          the new single level partitioning algo
   */
  public void setSingleLevelPartitioningAlgo(AbstractPartitioningAlgorithm algo) {
    singleLevelPartitioningAlgo = algo;
  }

  /**
   * Gets the refine algorithm.
   * 
   * @return the refine algorithm
   */
  public AbstractRefineAlgorithm getRefineAlgorithm() {
    return refineAlgorithm;
  }

  /**
   * Sets the refine algorithm.
   * 
   * @param refineAlgorithm
   *          the new refine algorithm
   */
  public void setRefineAlgorithm(AbstractRefineAlgorithm refineAlgorithm) {
    this.refineAlgorithm = refineAlgorithm;
  }

  /**
   * Gets the abort criterion.
   * 
   * @return the abort criterion
   */
  public AbstractAbortCriterion getAbortCriterion() {
    return abortCriterion;
  }

  /**
   * Sets the abort criterion.
   * 
   * @param abortCriterion
   *          the new abort criterion
   */
  public void setAbortCriterion(AbstractAbortCriterion abortCriterion) {
    this.abortCriterion = abortCriterion;
  }

  /**
   * Gets the coarse graph.
   * 
   * @return the coarse graph
   */
  public ISimpleGraph getCoarseGraph() {
    return coarseGraph;
  }

  /**
   * Sets the coarse graph.
   * 
   * @param coarseGraph
   *          the new coarse graph
   */
  public void setCoarseGraph(ISimpleGraph coarseGraph) {
    this.coarseGraph = coarseGraph;
  }

  /**
   * Gets the coarse mapping.
   * 
   * @return the coarse mapping
   */
  public PartitionMapping getCoarseMapping() {
    return coarseMapping;
  }

  /**
   * Sets the coarse mapping.
   * 
   * @param coarseMapping
   *          the new coarse mapping
   */
  public void setCoarseMapping(PartitionMapping coarseMapping) {
    this.coarseMapping = coarseMapping;
  }
}
