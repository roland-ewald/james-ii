/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;

import org.jamesii.asf.database.ISelectorType;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.perfdb.hibernate.NamedDBEntity;


/**
 * Hibernate implementation of a selector type.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class SelectorType extends NamedDBEntity implements ISelectorType {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6992584920689704415L;

  /** Factory to create a selector generator. */
  private Class<? extends PerformancePredictorGeneratorFactory> selectorGeneratorFactory;

  @Override
  public Class<? extends PerformancePredictorGeneratorFactory> getSelectorGeneratorFactory() {
    return selectorGeneratorFactory;
  }

  @Override
  public void setSelectorGeneratorFactory(
      Class<? extends PerformancePredictorGeneratorFactory> sgFactory) {
    selectorGeneratorFactory = sgFactory;
  }

}
