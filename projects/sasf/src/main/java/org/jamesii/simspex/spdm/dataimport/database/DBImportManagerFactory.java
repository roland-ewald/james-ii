/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport.database;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.ProblemPerformanceTuple;
import org.jamesii.asf.spdm.dataimport.plugintype.DMDataImporterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.gui.SimSpExPerspective;

/**
 * Factory for a data importer from an
 * {@link org.jamesii.perfdb.IPerformanceDatabase}.
 * 
 * @author Roland Ewald
 * 
 */
public class DBImportManagerFactory extends DMDataImporterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -250765309914673340L;

  /** Name of the benchmark model to be analysed. */
  public static final String TARGET_MODEL = "targetModel";

  /** Name of the performance measurement to be analysed. */
  public static final String TARGET_PERF_MEASURE = "targetPerfMeasure";

  /**
   * The performance database from which to import data to SPDM. Type:
   * {@link org.jamesii.perfdb.IPerformanceDatabase}.
   */
  public static final String PERFORMANCE_DATABASE = "performanceDatabase";

  @Override
  public IDMDataImportManager<ProblemPerformanceTuple> create(
      ParameterBlock params) {
    return new DatabaseImportManager(params.getSubBlockValue(TARGET_MODEL,
        "BenchmarkModel"), params.getSubBlockValue(TARGET_PERF_MEASURE,
        "TotalRuntime"), params.getSubBlockValue(PERFORMANCE_DATABASE,
        SimSpExPerspective.getPerformanceDataBase()));
  }

}
