/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;

import org.jamesii.SimSystem;
import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.util.collection.ArrayMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.misc.Pair;

import simulator.mlspace.AbstractMLSpaceProcessor;
import simulator.mlspace.eventrecord.HybridEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;
import simulator.mlspace.eventrecord.IEventRecord;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.ISubvolEventRecord.ISubvolChange;
import simulator.mlspace.util.Function;

/**
 * @author Arne Bittig
 * @date 06.10.2012
 */
public class EventRecordObserver extends AbstractEffectObserver<IEventRecord> {

  /** Identifier for parameter block specification of respective c'tor param */
  public static final String FLUSH_THRESHOLD = "FlushThreshold";

  /** Identifier for parameter block specification of respective c'tor param */
  public static final String SKIP_DIFFUSION_IN_SV_STATE = null;

  /** Properties of produced csv file */
  protected static final char CSV_SEP = ',';

  private static final char CSV_SEP_SURROUND = '"';

  private static final String CSV_SEP_SURROUND_ESCAPE = "\\\"";

  protected static final char NEW_LINE = '\n';

  /** Identifier for record items */
  private static final String SURROUNDING_ENTITY_PROPERTY = "SurroundingEntity";

  private static final String SV_STATE_PROPERTY = "State";

  private static final String RULE_APPLICATION_MARKER = "AppliedRule";

  private static final String NUM_COLLISIONS = "#Collisions";

  /** Subvol content string formatter */
  private final Function<Map<NSMEntity, Integer>, String> svStateToString;

  /**
   * The internal data. Map contract violation is intentional (e.g. "key" time
   * 0.0 is used once for each intially present entity
   */
  private final Map<Double, List<ChangeRecord>> unwrittenChanges =
      new ArrayMap<>();

  private String filename;

  private final int flushThreshold;

  private OutputStreamWriter fw;

  private final Map<IShapedComponent, String> compIDs = new LinkedHashMap<>();

  private final Map<ISubvol, String> svIDs = new LinkedHashMap<>();

  private final Map<MLSpaceRule, String> ruleIDs = new LinkedHashMap<>();

  /**
   * @param filename
   *          Name of file to write to (used as is)
   * @param flushThreshold
   *          Number of records (lines) to store in memory before flushing to
   *          file (<code>null</code> or {@link Integer#MAX_VALUE} for all)
   * @param skipDiffusionAttribForSvState
   *          Flag for formatting subvol state string
   */
  public EventRecordObserver(String filename, Integer flushThreshold,
      boolean skipDiffusionAttribForSvState) {
    this.filename = filename;
    this.flushThreshold =
        flushThreshold != null ? flushThreshold : Integer.MAX_VALUE;
    if (skipDiffusionAttribForSvState) {
      this.svStateToString =
          new SvStateToString(new EntityStringWithoutDiffusion());
    } else {
      this.svStateToString = new DefaultToString<>();
    }
  }

  /**
   * Change name of file to which output is written (triggers writing of changes
   * to and closing of current file, if any, if new name is different from old
   * one)
   * 
   * @param newFilename
   *          New filename
   */
  protected void setFilename(String newFilename) {
    if (filename != null && filename.equals(newFilename)) {
      return;
    }
    writeChanges();
    unwrittenChanges.clear();
    closeFileWriter(fw);
    fw = null;
    this.filename = newFilename;

  }

  private static <T> String getDefaultID(T entity, Map<T, String> map) {
    String id = map.get(entity);
    if (id == null) {
      id = entity.toString();
      if (id.startsWith("/*")) {
        id = id.substring(2, id.indexOf("*/"));
        // CHECK: provisions for string with improperly closed /*?
      } else {
        int splitPos = smallestPositive(id.indexOf('('), id.indexOf(' '));
        if (splitPos > 0) {
          id = id.substring(0, splitPos);
        }
      }
      id = id + ':' + map.size();
      map.put(entity, id);
    }
    return id;
  }

  private static int smallestPositive(int i1, int i2) {
    if (i1 <= 0) {
      return i2;
    } else if (i2 <= 0) {
      return i1;
    }
    return i1 < i2 ? i1 : i2;
  }

  private String getCompID(IShapedComponent comp) {
    return getDefaultID(comp, compIDs);
  }

  private String getSvID(Subvol sv) {
    if (sv == null) {
      return "";
    }
    String id = svIDs.get(sv);
    if (id == null) {
      id = sv.getName();
      if (id.isEmpty()) {
        if (sv.getPosition() == null) {
          return getDefaultID(sv, svIDs);
        }
        id = "Sv at " + sv.getPosition();
      }
      svIDs.put(sv, id);
    }
    return id;
  }

  @Override
  protected boolean init(AbstractMLSpaceProcessor<?, IEventRecord> proc) {
    createCompAndSvRecords(proc, 0.0);
    checkFlushAndWrite();
    return true;
  }

  /**
   * Record state of comps and svs known to given simulator
   * 
   * @param proc
   *          Simulator
   * @param time
   *          time index for comp and sv state records (in case initial state
   *          can only be recorded after first sim step, i.e. when
   *          proc.getTime() > 0 already)
   */
  protected void createCompAndSvRecords(
      AbstractMLSpaceProcessor<?, IEventRecord> proc, double time) {
    for (SpatialEntity comp : proc.getSpatialEntities().getAllNodes()) {
      unwrittenChanges.put(time, createCompRecord(comp, time > 0.));
    }
    boolean skipSurroundingEntity = proc.getSpatialEntities().isEmpty();
    for (Subvol sv : proc.getSubvols()) {
      unwrittenChanges.put(time,
          createSvRecord(sv, skipSurroundingEntity, time > 0.));
    }
  }

  private List<ChangeRecord> createCompRecord(SpatialEntity comp,
      boolean oldFlag) {
    String compID = getCompID(comp);
    List<ChangeRecord> rv = new ArrayList<>();
    {
      ChangeRecord posRec = new ChangeRecord();
      posRec.setChangedEntity(compID);
      posRec.setChangedProperty(SpatialAttribute.POSITION);
      posRec.setOldOrNewValue(comp.getPosition(), oldFlag);
      rv.add(posRec);
    }
    {
      ChangeRecord volRec = new ChangeRecord();
      volRec.setChangedEntity(compID);
      volRec.setChangedProperty(SpatialAttribute.SIZE);
      volRec.setOldOrNewValue(comp.getShape().getSize(), oldFlag);
      rv.add(volRec);
    }
    if (comp.getEnclosingEntity() != null) {
      ChangeRecord parRec = new ChangeRecord();
      parRec.setChangedEntity(compID);
      parRec.setChangedProperty(SURROUNDING_ENTITY_PROPERTY);
      parRec.setOldOrNewValue(getCompID(comp.getEnclosingEntity()), oldFlag);
      rv.add(parRec);
    }
    for (String att : comp.getAttributeNames()) {
      ChangeRecord rec = new ChangeRecord();
      rec.setChangedEntity(compID);
      rec.setChangedProperty(att);
      rec.setOldOrNewValue(comp.getAttribute(att), oldFlag);
      rv.add(rec);
    }
    return rv;
  }

  private List<ChangeRecord> createSvRecord(Subvol sv,
      boolean skipSurroundingEntity, boolean oldFlag) {
    String svID = getSvID(sv);
    List<ChangeRecord> rv = new ArrayList<>(2);
    // { // CHECK if useful
    // ChangeRecord volRec = new ChangeRecord();
    // volRec.setChangedEntity(svID);
    // volRec.setChangedProperty(SpatialAttribute.SIZE);
    // volRec.setNewValue(sv.getShape().getVolume());
    // rv.add(volRec);
    // }
    if (sv.getEnclosingEntity() != null && !skipSurroundingEntity) {
      ChangeRecord parRec = new ChangeRecord();
      parRec.setChangedEntity(svID);
      parRec.setChangedProperty(SURROUNDING_ENTITY_PROPERTY);
      parRec.setOldOrNewValue(getCompID(sv.getEnclosingEntity()), oldFlag);
      rv.add(parRec);
    }
    if (!sv.getState().isEmpty()) {
      ChangeRecord stateRec = new ChangeRecord();
      stateRec.setChangedEntity(svID);
      stateRec.setChangedProperty(SV_STATE_PROPERTY);
      stateRec.setOldOrNewValue(svStateToString.apply(sv.getState()), oldFlag);
      rv.add(stateRec);
    }
    return rv;
  }

  /**
   * in each call to #recordEffect, cache pointers to last position change
   * record in order to add the current position IN ONE PLACE ONLY
   */
  private final Map<SpatialEntity, ChangeRecord> lastCompPosChangeRec =
      new LinkedHashMap<>();

  /**
   * in each call to #recordEffect, cache pointers to last position change
   * record in order to add the current position IN ONE PLACE ONLY
   */
  private final Map<Subvol, ChangeRecord> lastSvStateChangeRec =
      new LinkedHashMap<>();

  @Override
  protected void recordEffect(Double time, IEventRecord effect) {
    if (effect instanceof IContSpaceEventRecord
        && !(effect instanceof HybridEventRecord && ((HybridEventRecord) effect)
            .isWrappedSubvolRecord())) {
      lastCompPosChangeRec.clear();
      unwrittenChanges.put(time,
          recordCompChanges((IContSpaceEventRecord) effect));
      for (Map.Entry<SpatialEntity, ChangeRecord> e : lastCompPosChangeRec
          .entrySet()) {
        e.getValue().setNewValue(e.getKey().getPosition());
      }
    }
    if (effect instanceof ISubvolEventRecord) {
      lastSvStateChangeRec.clear();
      if (effect instanceof HybridEventRecord) {
        for (ISubvolEventRecord svEffect : ((HybridEventRecord) effect)
            .getSvRecords()) {
          unwrittenChanges.put(time, recordSubvolChanges(svEffect));
        }
      } else {
        unwrittenChanges.put(time,
            recordSubvolChanges((ISubvolEventRecord) effect));
      }
      for (Map.Entry<Subvol, ChangeRecord> e : lastSvStateChangeRec.entrySet()) {
        e.getValue().setNewValue(svStateToString.apply(e.getKey().getState()));
      }
    }
    checkFlushAndWrite();
  }

  private List<ChangeRecord> recordCompChanges(IContSpaceEventRecord effect) {
    List<ChangeRecord> rv = new ArrayList<>();
    IShapedComponent triggeringEnt = effect.getTriggeringComponent();
    if (effect.getNumInfo() > 0) {
      ChangeRecord collRec = new ChangeRecord();
      collRec.setChangedEntity(getCompID(triggeringEnt));
      collRec.setChangedProperty(NUM_COLLISIONS);
      collRec.setValueChange(effect.getNumInfo());
      rv.add(collRec);
    }
    if (effect.getState().isSuccess()) {
      for (Map.Entry<SpatialEntity, ICompChange> e : effect.getCompChanges()
          .entrySet()) {
        rv.addAll(recordSingleCompChange(e.getKey(), triggeringEnt,
            e.getValue()));
      }
      addRuleRecords(effect, rv);
      for (IContSpaceEventRecord trigEffect : effect.getTriggeredEffects()) {
        rv.addAll(recordCompChanges(trigEffect));
      }
    }
    return rv;
  }

  private List<ChangeRecord> recordSingleCompChange(SpatialEntity comp,
      IShapedComponent triggeringEnt, ICompChange change) {
    String compID = getCompID(comp);
    String trigID = triggeringEnt == comp ? "" : getCompID(triggeringEnt); // NOSONAR:
    // intentional object identity (although SpatialEntity's equals is not
    // and may never be overwritten anyway)
    ChangeRecord rec = new ChangeRecord();
    rec.setChangedEntity(compID);
    rec.setOtherInvolvedEntity(trigID);
    List<ChangeRecord> rv = new ArrayList<>();
    if (change.getPosUpd() != null && !change.getPosUpd().isNullVector()) {
      rec.setChangedProperty(SpatialAttribute.POSITION);
      rec.setValueChange(change.getPosUpd());
      // rec.setNewValue(comp.getPosition());
      lastCompPosChangeRec.put(comp, rec);
      rv.add(rec);
      rec = new ChangeRecord();
      rec.setChangedEntity(compID);
      rec.setOtherInvolvedEntity(trigID);
    }
    Pair<IShapedComponent, IShapedComponent> enclosingEntityChange =
        change.getEnclosingEntityChange();
    if (enclosingEntityChange != null) {
      rec.setChangedProperty(SURROUNDING_ENTITY_PROPERTY);
      rec.setOldValue(getCompID(enclosingEntityChange.getFirstValue()));
      rec.setNewValue(getCompID(enclosingEntityChange.getSecondValue()));
      rv.add(rec);
      rec = new ChangeRecord();
      rec.setChangedEntity(compID);
      rec.setOtherInvolvedEntity(trigID);
    }
    for (Map.Entry<String, Object> e : change.getOldAtts().entrySet()) {
      String attName = e.getKey();
      rec.setChangedProperty(attName);
      rec.setOldValue(e.getValue());
      rec.setNewValue(change.getNewAtts().get(e.getKey()));
      rv.add(rec);
      rec = new ChangeRecord();
      rec.setChangedEntity(compID);
      rec.setOtherInvolvedEntity(trigID);
    }
    return rv;

  }

  /**
   * Record changes to subvol state and enclosing comp if applicable (protected
   * method to allow overriding in subclass)
   * 
   * @param effect
   *          Event record to process
   * @return List of change records
   */
  protected List<ChangeRecord> recordSubvolChanges(ISubvolEventRecord effect) {
    List<ChangeRecord> rv;
    Map<Subvol, ISubvolChange> svChanges = effect.getSubvolChanges();
    if (svChanges.size() == 2 && svChanges.values().contains(null)) {
      Iterator<Map.Entry<Subvol, ISubvolChange>> svCIt =
          svChanges.entrySet().iterator();
      Map.Entry<Subvol, ISubvolChange> svCE1 = svCIt.next();
      rv =
          recordSingleSubvolChange(svCE1.getKey(), svCIt.next().getKey(),
              svCE1.getValue());
    } else {
      rv = new ArrayList<>();
      for (Map.Entry<Subvol, ISubvolChange> e : effect.getSubvolChanges()
          .entrySet()) {
        rv.addAll(recordSingleSubvolChange(e.getKey(), null, e.getValue()));
      }
    }
    addRuleRecords(effect, rv);
    return rv;
  }

  private List<ChangeRecord> recordSingleSubvolChange(Subvol subvol1,
      Subvol subvol2, ISubvolChange change) {
    List<ChangeRecord> rv = new ArrayList<>();
    String sv1id = getSvID(subvol1);
    String sv2id = subvol2 == null ? "" : getSvID(subvol2);

    ChangeRecord rec = new ChangeRecord();
    rec.setChangedEntity(sv1id);
    rec.setOtherInvolvedEntity(sv2id);
    Pair<IShapedComponent, IShapedComponent> enclosingEntityChange =
        change.getEnclosingEntityChange();
    if (enclosingEntityChange != null) {
      rec.setChangedProperty(SURROUNDING_ENTITY_PROPERTY);
      rec.setOldValue(getCompID(enclosingEntityChange.getFirstValue()));
      rec.setNewValue(getCompID(enclosingEntityChange.getSecondValue()));
      rv.add(rec);
      rec = new ChangeRecord();
      rec.setChangedEntity(sv1id);
      rec.setOtherInvolvedEntity(sv2id);
    }
    if (!change.getStateChange().isEmpty()) {
      rec.setChangedProperty(SV_STATE_PROPERTY);
      rec.setValueChange(svStateToString.apply(change.getStateChange()));
      lastSvStateChangeRec.put(subvol1, rec);
      rv.add(rec);
    }
    if (subvol2 != null) {
      rec = new ChangeRecord();
      rec.setChangedEntity(sv2id);
      rec.setOtherInvolvedEntity(sv1id);
      rec.setChangedProperty(SV_STATE_PROPERTY);
      rec.setValueChange(svStateToString.apply(UpdateableAmountMap
          .negativeView(change.getStateChange())));
      rv.add(rec);
      lastSvStateChangeRec.put(subvol2, rec);
    }
    return rv;
  }

  private void addRuleRecords(IEventRecord effect, List<ChangeRecord> rv) {
    for (MLSpaceRule rule : effect.getRules()) {
      ChangeRecord ruleRec = new ChangeRecord();
      ruleRec.setChangedProperty(RULE_APPLICATION_MARKER);
      ruleRec.setValueChange(getDefaultID(rule, ruleIDs));
      rv.add(ruleRec);
    }
  }

  private void checkFlushAndWrite() {
    if (unwrittenChanges.size() > flushThreshold) {
      writeChanges();
      unwrittenChanges.clear();
    }
  }

  @Override
  protected void cleanUp(AbstractMLSpaceProcessor<?, IEventRecord> proc) {
    writeChanges();
    closeFileWriter(fw);
  }

  protected static void closeFileWriter(OutputStreamWriter fw) {
    if (fw != null) {
      try {
        fw.close();
      } catch (IOException e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Write content of unwrittenChanges field to file (open filewriter first if
   * necessary) WITHOUT clearing the unwrittenChanges field
   */
  private void writeChanges() {
    try {
      if (fw == null) {
        // ZipOutputStream zos = new ZipOutputStream(new
        // FileOutputStream(
        // filename + ".zip"));
        // zos.putNextEntry(new ZipEntry(filename));
        // fw = new OutputStreamWriter(zos);
        fw = new FileWriter(filename);

        fw.write("Time" + CSV_SEP + arrayToString(ChangeRecord.COLUMNS)
            + NEW_LINE);
      }
      for (Map.Entry<Double, List<ChangeRecord>> e : unwrittenChanges
          .entrySet()) {
        String time = e.getKey().toString();
        for (ChangeRecord change : e.getValue()) {
          fw.write(time + CSV_SEP // CHECK: NumberFormat?
              + arrayToString(change.getInfoPieces()) + NEW_LINE);
        }
      }
    } catch (IOException e) {
      SimSystem.report(e);
    }
  }

  protected static String arrayToString(String... columns) {
    StringBuilder strBuilder = new StringBuilder();
    for (String el : columns) {
      strBuilder.append(toString(el, CSV_SEP, CSV_SEP_SURROUND,
          CSV_SEP_SURROUND_ESCAPE));
      strBuilder.append(CSV_SEP);
    }
    return strBuilder.substring(0, strBuilder.length() - 1);
  }

  private static String toString(String el, char csvSep, char csvSepSurround,
      String csvSepSurroundEscape) {
    if (el == null) {
      return "";
    }
    if (el.indexOf(csvSep) < 0) {
      return el;
    }
    if (el.indexOf(csvSepSurround) < 0) {
      return csvSepSurround + el + csvSepSurround;
    }
    return csvSepSurroundEscape + el + csvSepSurroundEscape;
  }

  /**
   * Information pieces from {@link IEventRecord} reformatted for easier
   * storage.
   * 
   * Each setter may only be called once and not after the entire information
   * has already been queried (by {@link #getInfoPieces()}).
   * 
   * @author Arne Bittig
   * @date 06.10.2012
   */
  public static class ChangeRecord implements java.io.Serializable {

    private static final long serialVersionUID = -8920727440611171617L;

    private static final String[] COLUMNS = new String[] { "ChangedEntity",
        "OtherInvolvedEntity", "ChangedProperty", "OldValue", "NewValue",
        "ValueChange" };

    private final String[] infoPieces = new String[COLUMNS.length];

    private boolean queried = false;

    private void set(int index, Object value) {
      if (infoPieces[index] != null) {
        throw new IllegalStateException(COLUMNS[index]
            + " has already been set");
      }
      if (queried) {
        throw new IllegalStateException("Information has already "
            + "been extracted and cannot be set afterwards!");
      }
      this.infoPieces[index] = value == null ? null : value.toString();
    }

    /**
     * @param value
     *          Entity that changed
     */
    public void setChangedEntity(Object value) {
      this.set(0, value);
    }

    /**
     * @param value
     *          Entity involved in change of another
     */
    public void setOtherInvolvedEntity(Object value) {
      this.set(1, value);
    }

    /**
     * @param value
     *          Property that changed
     */
    public void setChangedProperty(Object value) {
      this.set(2, value);
    }

    /**
     * @param value
     *          Old value of changed property of changed entity
     */
    public void setOldValue(Object value) {
      this.set(3, value);
    }

    /**
     * Set old value or set new value, depending on flag
     * 
     * @param value
     *          Value of changed property
     * @param old
     *          Flag whether to set old value (if false: new)
     */
    public void setOldOrNewValue(Object value, boolean old) {
      this.set(old ? 3 : 4, value);
    }

    /**
     * @param value
     *          New value of changed property of changed entity
     */
    public void setNewValue(Object value) {
      this.set(4, value);
    }

    /**
     * @param value
     *          Change of value of changed entity's property
     */
    public void setValueChange(Object value) {
      this.set(5, value);
    }

    /**
     * @return the infoPieces
     */
    public final String[] getInfoPieces() {
      queried = true;
      return infoPieces;
    }
  }

  final class DefaultToString<T> implements Function.ToString<T> {
    @Override
    public String apply(T input) {
      return input.toString();
    }
  }

  static class SvStateToString implements
      Function.ToString<Map<NSMEntity, Integer>> {

    private final Function<? super NSMEntity, String> entToString;

    /**
     * @param entToString
     *          NSM entity string formatter
     */
    SvStateToString(Function<? super NSMEntity, String> entToString) {
      this.entToString = entToString;
    }

    @Override
    public String apply(Map<NSMEntity, Integer> input) {
      StringBuilder sb = new StringBuilder().append('{');
      for (Map.Entry<NSMEntity, Integer> e : input.entrySet()) {
        NSMEntity ent = e.getKey();
        sb.append(entToString.apply(ent));
        sb.append('=');
        sb.append(e.getValue().toString());
        sb.append(',');
      }
      if (sb.length() > 1) {
        sb.setCharAt(sb.length() - 1, '}');
      } else {
        sb.append('}');
      }
      return sb.toString();
    }
  }

  static class EntityStringWithoutDiffusion implements
      Function.ToString<AbstractModelEntity> {

    @Override
    public String apply(AbstractModelEntity ent) {
      StringBuilder sb = new StringBuilder();
      sb.append(ent.getSpecies().toString());
      boolean anyAttAppended = false;
      for (String att : ent.getAttributeNames()) {
        if (!att.equals(SpatialAttribute.DIFFUSION.toString())) {
          if (!anyAttAppended) {
            sb.append('(');
            anyAttAppended = true;
          } else {
            sb.append(',');
          }
          sb.append(att);
          sb.append(':');
          sb.append(ent.getAttribute(att));
        }
      }
      if (anyAttAppended) {
        sb.setCharAt(sb.length() - 1, ')');
      }
      return sb.toString();
    }
  }
}
