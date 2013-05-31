/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel.coarsening;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion.AbstractAbortCriterion;
import org.jamesii.core.util.graph.ISimpleGraph;

// TODO: Auto-generated Javadoc
/**
 * AbstractCoarsenAlgorithm is used for the coarsening phase in Multilevel
 * Partitioning Algorithms.
 * 
 * Created on September 15, 2007
 * 
 * @author rn003
 */
public abstract class AbstractCoarsenAlgorithm {

  /** Should be increased by overwritten methods. */
  private int actualLevel;

  /** The input. */
  private ISimpleGraph input;

  /** hold the smaller getting graphs, first is original. */
  private List<ISimpleGraph> coarseLevels;

  /**
   * hold the mappings (which node coarsened to which one), 'original' node ->
   * 'new' node (in smaller graph).
   */
  private List<Map<Integer, Integer>> mappings;

  /** thing to control number of iterations. */
  private AbstractAbortCriterion aborter;

  /**
   * Reads the original input graph.
   * 
   * @param input
   *          big unbalanced graph
   * @param aborter
   *          the aborter
   */
  public AbstractCoarsenAlgorithm(ISimpleGraph input,
      AbstractAbortCriterion aborter) {
    setCoarseLevels(new ArrayList<ISimpleGraph>());
    getCoarseLevels().add(input);
    setMappings(new ArrayList<Map<Integer, Integer>>());
    this.setActualLevel(0);
    this.aborter = aborter;
  }

  /**
   * Gets the actual level.
   * 
   * @return The actual level. Increased by nextCoarseningLevel, decreased by
   *         refinePartition.
   */
  public int getActualLevel() {
    return this.actualLevel;
  }

  /**
   * Gets the graph at level.
   * 
   * @param level
   *          the level
   * 
   * @return A graph at given coarsening level. Level 0 means original graph.
   */
  public ISimpleGraph getGraphAtLevel(int level) {
    return getCoarseLevels().get(level);
  }

  /**
   * Returns all levels of coarse graphs. Use this to give to Refiner.
   * 
   * @return the all coarsen graphs
   */
  public List<ISimpleGraph> getAllCoarsenGraphs() {
    return this.getCoarseLevels();
  }

  /**
   * Returns all the mappings of nodes from one level to the next. Use this to
   * give to Refiner.
   * 
   * @return the all coarsen mappings
   */
  public List<Map<Integer, Integer>> getAllCoarsenMappings() {
    return this.getMappings();
  }

  /**
   * Coarsens the initial graph since the AbortCriterion aborts the coarsening.
   * 
   * @return The deepest coarsening level. Use getGraphAtLevel() to get the
   *         graph.
   */
  public int coarsenGraph() {
    aborter.putLevelInformation(0, getCoarseLevels().get(0));
    while (aborter.shouldContinue()) {
      ISimpleGraph next = nextCoarseningLevel();
      aborter.putLevelInformation(getActualLevel(), next);
    }
    return getActualLevel();
  }

  /**
   * Coarsen the actual graph one level deeper. Has to update coarseLevels,
   * mappings and actualLevel!!!
   * 
   * @return a smaller Version of the actual graph
   */
  public abstract ISimpleGraph nextCoarseningLevel();

  /**
   * @return the coarseLevels
   */
  protected List<ISimpleGraph> getCoarseLevels() {
    return coarseLevels;
  }

  /**
   * @param coarseLevels
   *          the coarseLevels to set
   */
  protected void setCoarseLevels(List<ISimpleGraph> coarseLevels) {
    this.coarseLevels = coarseLevels;
  }

  /**
   * @param actualLevel
   *          the actualLevel to set
   */
  protected void setActualLevel(int actualLevel) {
    this.actualLevel = actualLevel;
  }

  /**
   * @return the mappings
   */
  protected List<Map<Integer, Integer>> getMappings() {
    return mappings;
  }

  /**
   * @param mappings
   *          the mappings to set
   */
  protected void setMappings(List<Map<Integer, Integer>> mappings) {
    this.mappings = mappings;
  }
}
