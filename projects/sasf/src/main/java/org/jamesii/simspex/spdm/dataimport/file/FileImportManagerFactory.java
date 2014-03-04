/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport.file;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.dataimport.plugintype.DMDataImporterFactory;
import org.jamesii.core.parameters.ParameterBlock;


/**
 * Factory for import of files for data-mining.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class FileImportManagerFactory extends DMDataImporterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8160502821365036654L;

  /** Parameter name for directory parameter, type: {@link String}. */
  public static final String TARGET_DIR = "directory";

  /** Parameter name for maximisation flag, type: {@link Boolean}. */
  public static final String MAXIMISE_PERFORMANCE = "maximizePerformance";

  @Override
  public IDMDataImportManager<PerformanceTuple> create(ParameterBlock params) {
    return new FileImportManager(params.getSubBlockValue(TARGET_DIR,
        "perf_data"), params.getSubBlockValue(MAXIMISE_PERFORMANCE, false));
  }

}
