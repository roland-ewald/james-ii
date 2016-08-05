/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.csv;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.snapshot.AbstractTimeTakingSnapshotObserver;
import org.jamesii.core.observe.snapshot.ISnapshotPolicy;
import org.jamesii.core.observe.snapshot.TimeRecorder;
import org.jamesii.core.util.ITime;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * IObserver implementation that writes column-wise data into csv files.
 * (Basically a hack for the MLSpace observers that maybe, just maybe can be
 * expressed more in proper org.jamesii fashion using a data storage...but maybe
 * not). Actual data recording must be done by the subclasses, this class
 * provides functionality for determining when to record (snapshots), recording
 * simulation and elapsed wall clock time, and writing the csv file output.
 *
 * @author Arne Bittig
 * @param <O>
 *          Type of observable (usually processor/simulator)
 * @param <SID>
 *          Snapshot ID type
 */
public class SnapshotCSVObserver<O extends IObservable & ITime<Double>, SID>
    extends AbstractTimeTakingSnapshotObserver<O, SID> {

  /**
   * @author Arne Bittig
   * @param <O>
   *          Type of observable (usually processor/simulator)
   */
  public interface SnapshotPlugin<O extends IObservable & ITime<Double>> {

    /**
     * Delegate for {@link SnapshotCSVObserver#updateState(IObservable, int)}
     *
     * @param obs
     *          Observed simulation (or other object)
     * @param i
     *          Number of snapshot (0-based)
     */
    void updateState(O obs, int i);

    Map<String, ? extends List<?>> getObservationData();
  }

  /** Path and name of file to write to */
  private final String csvFilePathAndPrefix;

  private boolean alreadyWritten = false;

  private final CSVUtils.CSVWriter csvWriter;

  private final List<SnapshotPlugin<O>> plugins;

  /**
   * Constructor to be called by the others; snapshotTimes or snapshotSpacing
   * must be given (i.e. if the first is null, the second must be > 0; if the
   * first is not null, the second is ignored)
   *
   * @param sp
   * @param plugins
   * @param csvFilePathAndPrefix
   * @param csvWriter
   */
  public SnapshotCSVObserver(ISnapshotPolicy<SID> sp,
      List<SnapshotPlugin<O>> plugins, String csvFilePathAndPrefix, // String
      // observesWhat,
      CSVUtils.CSVWriter csvWriter) {
    super(sp, new TimeRecorder<SID>(true, true));
    this.plugins = plugins;
    this.csvFilePathAndPrefix = csvFilePathAndPrefix;
    // appendIDToFileName(csvFilePathAndPrefix, observesWhat == null ?
    // this.getClass().getName() : observesWhat);
    this.csvWriter = csvWriter;
  }

  protected List<SnapshotPlugin<O>> getPlugins() {
    return plugins;
  }

  @Override
  protected void updateState(O proc, int i) {
    for (SnapshotPlugin<O> plugin : plugins) {
      plugin.updateState(proc, i);
    }
  }

  /**
   *
   * Write data to file if not already done so (retrieves data from subclass via
   * {@link #getObservationData()})
   */
  @Override
  protected void cleanUp() {
    alreadyWritten = alreadyWritten || csvFilePathAndPrefix == null;
    if (alreadyWritten) {
      return;
    }
    Map<String, List<?>> columns = collectFullInfo();

    alreadyWritten = csvWriter.write(columns, csvFilePathAndPrefix);
    if (!alreadyWritten) {
      ApplicationLogger.log(Level.WARNING, "Error creating file. Skipping.");
    }
  }

  protected Map<String, List<?>> collectFullInfo() {
    Map<String, List<?>> columns =
        new LinkedHashMap<>(getTimeRecord().getAllInfoAsMap());
    boolean lastRowRedundant = true;
    int lastRow = columns.values().iterator().next().size() - 1;

    for (SnapshotPlugin<O> plugin : plugins) {
      for (Map.Entry<String, ? extends List<?>> e : plugin.getObservationData()
          .entrySet()) {
        String key = escapeCSVSep(e.getKey()); // necessary because data written
        // directly after method call
        List<?> colContent = e.getValue();
        if (columns.containsKey(key)) {
          columns.put(key + plugin.toString(), colContent);
        } else {
          columns.put(key, colContent);
        }
        if (colContent.size() > lastRow) {
          lastRowRedundant = lastRow > 0 && lastRowRedundant
              && colContent.get(lastRow).equals(colContent.get(lastRow - 1));
        }
      }
    }

    if (lastRowRedundant) {
      for (Entry<String, List<?>> next : columns.entrySet()) {
        List<?> value = next.getValue();
        if (value.size() > lastRow) {
          next.setValue(value.subList(0, lastRow));
        }
      }
    }

    return columns;
  }

  protected final String escapeCSVSep(String key) {
    if (key.indexOf(csvWriter.getCsvSep()) == -1) {
      return key;
    }
    if (key.startsWith("\"") && key.endsWith("\"")) {
      return key;
    }
    return "\"" + key + "\"";
  }

}
