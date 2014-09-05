/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport.plugintype;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Base class for factories providing data-mining data import mechanisms.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class DMDataImporterFactory<P extends PerformanceTuple> extends
    Factory<IDMDataImportManager<P>> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -193950153599285175L;

  /**
   * Creates a data-mining data import manager.
 * @param params
   *          the parameters
 * @return the data import manager
   */
  @Override
  public abstract IDMDataImportManager<P> create(ParameterBlock params, Context context);

}
