/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests.wilcoxon;

import org.jamesii.core.factories.Context;
import org.jamesii.core.math.statistics.tests.IPairedTest;
import org.jamesii.core.math.statistics.tests.plugintype.AbstractPairedTestFactory;
import org.jamesii.core.math.statistics.tests.plugintype.PairedTestFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating
 * {@link org.jamesii.core.math.statistics.tests.wilcoxon.WilcoxonRankSumTest}
 * objects.
 * 
 * @author Stefan Leye
 */
public class WilcoxonRankSumTestFactory extends PairedTestFactory {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 8806658895945057716L;

  @Override
  public IPairedTest create(ParameterBlock parameter, Context context) {
    return new WilcoxonRankSumTest();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {

    if (params == null) {
      return 0;
    }

    boolean definedInValue =
        params.getValue() == null ? false : params.getValue().equals(
            this.getName());

    boolean definedInSubBlock =
        params.getSubBlockValue(AbstractPairedTestFactory.TEST_FACTORY_CLASS) == null ? false
            : params.getSubBlockValue(
                AbstractPairedTestFactory.TEST_FACTORY_CLASS).equals(
                this.getClass());

    if (definedInSubBlock || definedInValue) {
      return 1;
    }

    return 0;
  }
}
