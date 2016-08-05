/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.subvols.Subvol;

import org.jamesii.SimSystem;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.ISubvolEventRecord.ISubvolChange;
import simulator.mlspace.util.Function;

/**
 * @author Arne Bittig
 * @date 23.01.2013
 */
public class EventRecordAndLegendObserver extends EventRecordObserver {

  /**
   * Indentifier for respective setting in parameter block processed by
   * instrumenter
   */
  public static final String RECORD_EVENTS_WITH_LEGEND =
      "RecordEventsWithLegend";

  /**
   * Indentifier for respective setting in parameter block processed by
   * instrumenter
   */
  public static final String FINAL_STATE_FILE_SUFFIX = "RecordFinalState";

  private final String summaryFileName;

  private final boolean flushAfterInit;

  private final String finalStateFileName;

  private FileWriter summaryFileWriter;

  private final Function<AbstractModelEntity, String> entToString;

  /*
   * Collection of String, not NSMEntity, as when entToString groups entities
   * (e.g. by skipping the diffusion attribute, omitting the distinction between
   * fast and slow entities of the same species), the latter is too specific
   */
  private final Collection<String> nsmEntsRecorded = new LinkedHashSet<>();

  /**
   * 
   * @param filenameRecords
   *          Name of actual records file (produced in super class)
   * @param filenameFinalState
   *          Name of file to record final state to (null for none, same as
   *          records file to add at end there)
   * @param filenameSummary
   *          Name of summary file (produced here)
   * @param flushThreshold
   *          Number of records (lines) to store in memory before flushing to
   *          file (<code>null</code> or {@link Integer#MAX_VALUE} for all)
   * @param skipDiffusionAttribForSvState
   *          Flag for formatting subvol state string
   */
  public EventRecordAndLegendObserver(String filenameRecords,
      String filenameFinalState, String filenameSummary,
      Integer flushThreshold, boolean skipDiffusionAttribForSvState) {
    super(filenameRecords, flushThreshold, skipDiffusionAttribForSvState);
    this.finalStateFileName = filenameFinalState;
    this.flushAfterInit = flushThreshold != null;
    // filenameRecords.equals(filenameFinalState);
    this.summaryFileName = filenameSummary;
    this.entToString =
        skipDiffusionAttribForSvState ? new EventRecordObserver.EntityStringWithoutDiffusion()
            : new EventRecordObserver.DefaultToString<AbstractModelEntity>();
  }

  @Override
  protected boolean init(AbstractMLSpaceProcessor<?, IEventRecord> proc) {
    Collection<Subvol> subvols = proc.getSubvols();
    if (!subvols.isEmpty()) {
      writeSummaryItem("#subvols", subvols.size(), "");
      if (subvols.iterator().next().getShape() == null) {
        processSubvolSizes(subvols);
      } else {
        processSubvolExts(subvols);
      }
      processInitialEnitities(subvols);
      if (flushAfterInit) {
        try {
          summaryFileWriter.flush();
        } catch (IOException e) {
          SimSystem.report(e);
        }
      }
    }
    return super.init(proc);
  }

  private void processSubvolSizes(Collection<Subvol> subvols) {
    IUpdateableMap<Double, Integer> svVols = new UpdateableAmountMap<>();
    for (Subvol sv : subvols) {
      svVols.add(sv.getVolume());
    }
    for (Map.Entry<Double, Integer> e : svVols.entrySet()) {
      writeSummaryItem("Subvols: volume/amount", e.getKey(), e.getValue());
    }

  }

  private void processSubvolExts(Collection<Subvol> subvols) {
    IUpdateableMap<String, Integer> svSizes = new UpdateableAmountMap<>();
    Map<String, IDisplacementVector> svSizeStringMap = new LinkedHashMap<>();
    IPositionVector min = null;
    IPositionVector max = null;
    for (Subvol sv : subvols) {
      IShape shape = sv.getShape();
      IDisplacementVector maxExtVector = shape.getMaxExtVector();
      IDisplacementVector svExt = maxExtVector.times(2);
      String svExtStr = svExt.toString();
      svSizes.add(svExtStr);
      svSizeStringMap.put(svExtStr, svExt);
      if (max == null) {
        min = shape.getCenter().minus(maxExtVector);
        max = shape.getCenter().plus(maxExtVector);
      } else {
        min = Vectors.min(min, shape.getCenter().minus(maxExtVector));
        max = Vectors.max(max, shape.getCenter().plus(maxExtVector));
      }
    }
    writeSummaryItem("min coordinates", min, "");
    writeSummaryItem("max coordinates", max, "");
    double[] totalExt = min.displacementTo(max).toArray();
    for (Map.Entry<String, Integer> e : svSizes.entrySet()) {
      String svExtStr = e.getKey();
      writeSummaryItem("Subvols extension/amount", svExtStr, e.getValue());
      writeSummaryItem("Subdivisions", Vectors.arrayToString(
          Vectors.divide(totalExt, svSizeStringMap.get(svExtStr).toArray()),
          "|", "%.1f"), "if all subvols are of size " + svExtStr);
    }
  }

  private void processInitialEnitities(Collection<Subvol> subvols) {
    assert nsmEntsRecorded.isEmpty();
    for (Subvol sv : subvols) {
      for (NSMEntity ent : sv.getState().keySet()) {
        nsmEntsRecorded.add(entToString.apply(ent));
      }
    }
    for (String entStr : nsmEntsRecorded) {
      writeSummaryItem("Entity", entStr, "in subvol at start");
    }
  }

  @Override
  protected void recordEffect(Double time, IEventRecord effect) {
    if (effect instanceof ISubvolEventRecord) {
      for (ISubvolChange change : ((ISubvolEventRecord) effect)
          .getSubvolChanges().values()) {
        if (change == null) {
          continue;
        }
        for (NSMEntity ent : change.getStateChange().keySet()) {
          String entStr = entToString.apply(ent);
          if (nsmEntsRecorded.add(entStr)) {
            writeSummaryItem("Entity", entStr, "in subvol first at time "
                + time);
          }
        }
      }
    }
    super.recordEffect(time, effect);
  }

  private void writeSummaryItem(String itemDescr, Object itemContent,
      Object itemRemark) {
    try {
      if (summaryFileWriter == null) {
        summaryFileWriter = new FileWriter(summaryFileName);
      }
      summaryFileWriter.write(itemDescr + CSV_SEP
          + arrayToString(itemContent.toString(), itemRemark.toString())
          + NEW_LINE);
    } catch (IOException e) {
      SimSystem.report(e);
    }
  }

  @Override
  protected void cleanUp(AbstractMLSpaceProcessor<?, IEventRecord> proc) {
    writeSummaryItem("Model time at end of sim", proc.getTime(),
        proc.getNumOfStep() + " steps");
    closeFileWriter(summaryFileWriter);

    super.setFilename(finalStateFileName);
    createCompAndSvRecords(proc, proc.getTime());
    super.cleanUp(proc);
  }

}
