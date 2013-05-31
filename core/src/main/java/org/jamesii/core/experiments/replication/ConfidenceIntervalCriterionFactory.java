/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.replication;

import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory to create criterion for a max allowed variance.
 * 
 * @author Jan Himmelspach
 */
public class ConfidenceIntervalCriterionFactory extends RepCriterionFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3544009758339583873L;

  /** Parameter name for number of replications. */
  public static final String CONFIDENCE_LEVEL = "confidenceLevel";

  public static final String RELATIVE_HALF_WIDTH_THRESHOLD =
      "relativeHalfWidthThreshold";

  public static final String MAX_REPLICATIONS = "maxReplications";

  public static final String MIN_REPLICATIONS = "minReplications";

  public static final String FURTHER_REPLICATIONS = "furtherReplications";

  public static final String DATA_ID = "dataID";

  public static final String ATTRIBUTE_ID = "attributeID";

  @Override
  public IReplicationCriterion create(ParameterBlock params) {

    long dataid = 0l;
    String attribute = null;

    if (params != null) {
      dataid = params.getSubBlockValue(DATA_ID, 0l);
      attribute = params.getSubBlockValue(ATTRIBUTE_ID, "state_vector");
    }

    ConfidenceIntervalCriterion c =
        new ConfidenceIntervalCriterion(dataid, attribute);

    if (params != null) {
      c.setConfidenceLevel(params.getSubBlockValue(CONFIDENCE_LEVEL, 0.95));
      c.setMaxReplications(params.getSubBlockValue(MAX_REPLICATIONS, 1000));
      c.setMinReplications(params.getSubBlockValue(MIN_REPLICATIONS, 100));
      c.setFurtherReplications(params
          .getSubBlockValue(FURTHER_REPLICATIONS, 10));
      c.setRelativeHalfWidthThreshold(params.getSubBlockValue(
          RELATIVE_HALF_WIDTH_THRESHOLD, 0.01));
    }

    return c;
  }
}
