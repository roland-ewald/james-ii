/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * Implements a data structure to save the results of a partitioning algorithm.
 * Basically, this is a mapping from models to a processor, this means form a
 * vertex index of the model graph to a vertex index of the hardware graph, e.
 * g.
 * 
 * '2 => 3' means that the model associated with the second vertex in the model
 * graph will be executed on the processor associated with the third vertex in
 * the hardware graph
 * 
 * Created on Nov 15, 2004
 * 
 * @author Roland Ewald
 * 
 * @see org.jamesii.core.distributed.partition.Partition
 */
public class PartitionMapping extends HashMap<Integer, Integer> {

  /** Serialisation ID. */
  static final long serialVersionUID = -650416872356495744L;

  /** counts how many different processors are used in this mapping. */
  private int numProcessors = 0;

  /**
   * Standard constructor.
   */
  public PartitionMapping() {
    super();
  }

  /**
   * The Constructor.
   * 
   * @param initialCapacity
   *          the initial capacity
   */
  public PartitionMapping(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * The Constructor.
   * 
   * @param initialCapacity
   *          the initial capacity
   * @param loadFactor
   *          the load factor
   */
  public PartitionMapping(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  /**
   * Calculates the sum of labels of all edges between the two partitions.
   * PartitionMapping must therefore be a bisection.
   * 
   * @param model
   *          Graph on which the PartitionMapping is defined
   * 
   * @return the accumulated cut, null if this is no bisection
   */
  public Double calculateBisectionCut(ISimpleGraph model) {
    if (!this.isBisection()) {
      return null;
    }
    double cut = 0;
    Map<Integer, Map<Integer, Double>> edgeMap = model.getEdgeLabels();
    for (Integer first : model.getVertices()) {
      for (Integer second : model.getNeighboursOfNode(first)) {
        if (second > first && this.get(first) != this.get(second)) {
          cut += edgeMap.get(first).get(second);
        }
      }
    }
    return cut;
  }

  @Override
  public void clear() {
    this.numProcessors = 0;
    super.clear();
  }

  /**
   * To how many different processors does this mapping map to.
   * 
   * @return number of processors
   */
  public int getNumberOfProcessors() {
    return this.numProcessors;
  }

  /**
   * Is this a partition into 2 parts?.
   * 
   * @return true, if is bisection
   */
  public boolean isBisection() {
    return this.numProcessors == 2;
  }

  @Override
  public Integer put(Integer arg0, Integer arg1) {
    if (!this.containsValue(arg1)) {
      this.numProcessors++;
    }
    return super.put(arg0, arg1);
  }

  @Override
  public void putAll(Map<? extends Integer, ? extends Integer> arg0) {
    for (Map.Entry<? extends Integer, ? extends Integer> entry : arg0
        .entrySet()) {
      this.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public Integer remove(Object arg0) {
    Integer value = super.remove(arg0);
    if (value != null && !this.containsValue(value)) {
      this.numProcessors--;
    }
    return value;
  }

  /**
   * Return string representation of a partition.
   * 
   * @return String representation of partition
   */
  @Override
  public String toString() {

    StringBuilder output = new StringBuilder("\n");

    for (Entry<Integer, Integer> entry : this.entrySet()) {
      output.append("\n");
      output.append(entry.getKey());
      output.append(" => ");
      output.append(entry.getValue());
    }

    return output.toString();
  }

}
