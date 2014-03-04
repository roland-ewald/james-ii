/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport.xml;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.plugintype.DMDataImporterFactory;
import org.jamesii.core.parameters.ParameterBlock;


/**
 * Factory for the creation of {@link XMLImportManager} instances.
 * 
 * @author Roland Ewald
 * 
 */
public class XMLImportManagerFactory extends DMDataImporterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -444862053587167791L;

  /**
   * Parameter to define the result file that shall be read, type:
   * {@link String}.
   */
  public static final String RESULT_FILE = "ResultFile";

  @Override
  public IDMDataImportManager create(ParameterBlock params) {
    return new XMLImportManager((String) params.getSubBlockValue(RESULT_FILE));
  }

}
