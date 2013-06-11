/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.plugintype;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.distributed.partitioner.partitioning.multilevel.MultiLevelPartitioningFactory;
import org.jamesii.core.factories.AbstractFilteringFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Abstract factory to select a suitable partitioner factory.
 * 
 * The parameter block for filter has to contain a "partitionerType" value of
 * PartitionerType defined in this class!
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractPartitioningFactory extends
    AbstractFilteringFactory<PartitioningFactory> {

  /**
   * Defines different types of partitioners, and makes the type configurable.
   * It is, e.g., important that a multi-level partitioner is able to choose a
   * single-level partitioner to partition an already coarsened graph.
   * 
   * @author Roland Ewald
   * 
   */
  public static enum PartitionerType {

    /**
     * Only single-level partitioners are eligible.
     */
    SINGLE_LEVEL_ONLY,

    /**
     * Only multi-level partitioners are eligible.
     */
    MULTI_LEVEL_ONLY,

    /**
     * The type of the partitioner does not matter.
     */
    ANY_TYPE

  };

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 2331218783212641711L;

  /**
   * The model to be partitioned, type: {@link IModel}.
   */
  public static final String MODEL = "model";

  /**
   * Kind of partitioning algorithm, type {@link PartitionerType}.
   */
  public static final String PARTITIONER_TYPE = "partitionerType";

  /**
   * Standard constructor.
   */
  public AbstractPartitioningFactory() {
    super();
    addCriteria(new FactoryCriterion<PartitioningFactory>() {
      @Override
      public List<PartitioningFactory> filter(
          List<PartitioningFactory> factories, ParameterBlock parameter) {
        PartitionerType pt =
            parameter.getSubBlockValue(PARTITIONER_TYPE,
                PartitionerType.SINGLE_LEVEL_ONLY);
        if (pt == PartitionerType.ANY_TYPE) {
          return factories;
        }

        ArrayList<PartitioningFactory> facs = new ArrayList<>();
        for (PartitioningFactory factory : factories) {
          boolean isML = (factory instanceof MultiLevelPartitioningFactory);
          if (isML && pt == PartitionerType.MULTI_LEVEL_ONLY
              || (!isML && pt == PartitionerType.SINGLE_LEVEL_ONLY)) {
            facs.add(factory);
          }
        }
        return facs;
      }
    });
  }
}
