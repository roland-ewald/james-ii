/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport.plugintype;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.core.factories.AbstractFactory;

/**
 * Abstract factory for data importer factories.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractDMDataImporterFactory extends
    AbstractFactory<DMDataImporterFactory<PerformanceTuple>> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 546933330721092506L;

}
