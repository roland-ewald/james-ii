/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 *
 */
package simulator.mlspace.eventrecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.TimedReactionRule;
import model.mlspace.rules.TransferRule;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.rules.match.SuccessfulMatchWithBindings;
import model.mlspace.rules.match.SuccessfulMatchWithBindings.BindingMod;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.util.logging.ApplicationLogger;

import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;

/**
 * ...
 * 
 * Contains reference to record of event that triggered this one (null if it was
 * directly triggered by a scheduled (reaction or move) event) and the next
 * event it triggered (not unlike a linked list).
 * 
 * @author Arne Bittig
 * 
 */
public class ContSpaceEventRecord extends
    AbstractEventRecord<SpatialEntity, ICompChange> implements
    IContSpaceEventRecord {

  private State state;

  private ContSpaceEventRecord triggeringEffect;

  private final Collection<IContSpaceEventRecord> triggeredEffects =
      new LinkedList<>();

  private int numInfo;

  /**
   * Factory method for record of failed move attempt of a spatial entity
   * 
   * @param comp
   *          Spatial entity
   * @return New event record
   */
  public static ContSpaceEventRecord newFailedMove(SpatialEntity comp) {
    ContSpaceEventRecord rv = new ContSpaceEventRecord();
    rv.putChange(comp, FAILED_MOVE_ATTEMPT);
    rv.setFailed();
    return rv;
  }

  /**
   * Factory method for record of failed first-order reaction application
   * attempt
   * 
   * @param comp
   *          Spatial entity
   * @param react
   *          Reaction rule
   * @return New event record
   */
  public static ContSpaceEventRecord newFailedReact(SpatialEntity comp,
      TimedReactionRule react) {
    ContSpaceEventRecord rv =
        new ContSpaceEventRecord(Collections.<MLSpaceRule> singleton(react));
    rv.putChange(comp, null);
    rv.setFailed();

    return rv;
  }

  /** no-args private c'tor for use in factory methods */
  private ContSpaceEventRecord() {
    super(Collections.<MLSpaceRule> emptySet());
  }

  /**
   * private c'tor for use in
   * {@link #newFailedReact(SpatialEntity, TimedReactionRule)} and
   * {@link #getAggregateCSERecord()}
   */
  private ContSpaceEventRecord(Collection<MLSpaceRule> rules) {
    super(rules);
  }

  /**
   * Applied rule effect
   * 
   * @param comp
   *          Spatial entity
   * @param rule
   *          Reaction rule
   * @param modRecord
   *          Modified attributes and previous values
   */
  public ContSpaceEventRecord(SpatialEntity comp, TimedReactionRule rule,
      SuccessfulMatch.ModRecord<SpatialEntity> modRecord) {
    super(Collections.<MLSpaceRule> singleton(rule));
    Collection<Compartment> bindMod = new LinkedList<>(); // better HashMap?
    if (modRecord instanceof SuccessfulMatchWithBindings.ModRecordWithBindings<?, ?>) {
      for (BindingMod<Compartment> bm : ((SuccessfulMatchWithBindings.ModRecordWithBindings<SpatialEntity, Compartment>) modRecord)
          .getBindMods()) {
        bindMod.add(bm.getChangedEntity());
      }
    }
    Map<SpatialEntity, Map<String, Object>> attMods = modRecord.getAttMods();
    SpatialEntity parent = comp.getEnclosingEntity();
    if (attMods.containsKey(parent)) {
      putChange(parent,
          new CompChange(parent, attMods.get(parent), bindMod.remove(parent)));
      // CHECK: binding change of enclosing comps? Uh oh...
    }

    Map<String, Object> oldAtts = attMods.get(comp);
    if (oldAtts == null) {
      oldAtts = Collections.emptyMap();
    }
    putChange(comp, new CompChange(comp, oldAtts, bindMod.remove(comp)));
    for (SpatialEntity otherComp : bindMod) {
      if (otherComp != comp) {
        putChange(otherComp, BIND_CHANGE);
      }
    }
    this.state = State.SUCCESS; // no further effects to consider
  }

  /**
   * Move-only effect (may trigger reactions...)
   * 
   * @param comp
   *          Spatial entitiy
   * @param movedAlong
   *          Nested entities that moved, too
   * @param posUpd
   *          Displacement they were moved by
   */
  public ContSpaceEventRecord(SpatialEntity comp,
      Collection<? extends SpatialEntity> movedAlong, IDisplacementVector posUpd) {
    super(Collections.<MLSpaceRule> emptySet());
    ICompChange change = new CompChange(posUpd);
    putChange(comp, change);
    for (SpatialEntity ent : movedAlong) {
      putChange(ent, change);
    }
    this.state = State.SUCCESS;
  }

  /**
   * Applied (transfer or reaction) rule effect
   * 
   * @param movingComp
   *          Spatial entity
   * @param otherComp
   *          Previous enclosing entity of comp
   * @param mods
   *          Modified attributes and previous values of movingComp and
   *          oriParComp
   * @param react
   *          Transfer rule
   */
  public ContSpaceEventRecord(SpatialEntity movingComp,
      SpatialEntity otherComp, SuccessfulMatch.ModRecord<SpatialEntity> mods,
      MLSpaceRule react) {
    super(Collections.singleton(react));
    assert !(react instanceof TimedReactionRule);

    Map<SpatialEntity, Map<String, Object>> attMods = mods.getAttMods();
    Collection<Compartment> bindMod = new LinkedList<>(); // better HashMap?
    if (mods instanceof SuccessfulMatchWithBindings.ModRecordWithBindings<?, ?>) {
      for (BindingMod<Compartment> bm : ((SuccessfulMatchWithBindings.ModRecordWithBindings<SpatialEntity, Compartment>) mods)
          .getBindMods()) {
        bindMod.add(bm.getChangedEntity());
      }
    }
    CompChange compChange =
        new CompChange(movingComp,
            attMods.containsKey(movingComp) ? attMods.get(movingComp)
                : Collections.<String, Object> emptyMap(),
            bindMod.remove(movingComp));
    if (react instanceof TransferRule) {
      // CHECK: transfer direction always correct?
      compChange.setEnclosingEntityChange(otherComp,
          movingComp.getEnclosingEntity());
    }
    putChange(movingComp, compChange);

    for (Map.Entry<SpatialEntity, Map<String, Object>> e : attMods.entrySet()) {
      SpatialEntity comp = e.getKey();
      if (comp == movingComp) {// NOSONAR: intentional obj. id.
        continue;
      }
      putChange(comp, new CompChange(comp, e.getValue(), bindMod.remove(comp)));
    }

    // record binding changes for not otherwise modified comps
    for (Compartment comp : bindMod) {
      if (!attMods.containsKey(comp) && comp != movingComp) {
        putChange(comp, BIND_CHANGE);
      }
    }

    this.state = State.SUCCESS; // no further effects to consider
  }

  /**
   * Set state to {@link IContSpaceEventRecord.State#FAILED failed}. To be
   * called by constructor of downstream failed event
   */
  protected final void setFailed() {
    this.state = State.FAILED;
    if (triggeringEffect != null) {
      triggeringEffect.setFailed();
    }
    // CHECK - also set all triggered effects to FAILED?!
  }

  /**
   * Does this record belong to a failed attempt or an actual effect?
   * 
   * @return {@link IContSpaceEventRecord.State#FAILED} or
   *         {@link IContSpaceEventRecord.State#SUCCESS}
   */
  @Override
  public final State getState() {
    return state;
  }

  @Override
  public boolean isSuccess() {
    return getState().isSuccess();
  }

  @Override
  public Map<SpatialEntity, ICompChange> getCompChanges() {
    return super.getChanges();
  }

  /**
   * @return Numeric information (usually move attempts)
   */
  @Override
  public final int getNumInfo() {
    return numInfo;
  }

  /**
   * @param numInfo
   *          Numeric information (usually move attempts)
   */
  public final void setNumInfo(int numInfo) {
    if (this.numInfo != 0) {
      throw new IllegalStateException(
          "Numeric information has already been set");
    }
    this.numInfo = numInfo;
  }

  /**
   * Add newly produced entities to record
   * 
   * @param createdEnts
   *          Created entities
   */
  public void addAllCreated(Collection<? extends SpatialEntity> createdEnts) {
    for (SpatialEntity ent : createdEnts) {
      putChange(ent, CREATED);
    }
  }

  /**
   * @param alsoMoved
   *          Nested entities that moved along with another, already recorded
   *          one
   * @param posUpd
   *          Displacement they were moved by
   */
  public void addAlsoMoved(Collection<SpatialEntity> alsoMoved,
      IDisplacementVector posUpd) {
    ICompChange change = new CompChange(posUpd);
    for (SpatialEntity ent : alsoMoved) {
      putChange(ent, change);
    }

  }

  /**
   * Add consumption of some spatial entity, including recording change of
   * surrounding entity for formerly nested entities
   * 
   * @param destroyedEnt
   *          Consumed spatial entity
   * @param nestedEnts
   *          Entities formerly inside the former
   */
  public void addDestroyed(SpatialEntity destroyedEnt,
      Collection<? extends SpatialEntity> nestedEnts) {
    putChange(destroyedEnt, DESTROYED);
    ICompChange parentChange = new CompChange(destroyedEnt);
    for (SpatialEntity ent : nestedEnts) {
      putChange(ent, parentChange);
    }
  }

  /**
   * @return Record of event that triggered this one (null if result of directly
   *         scheduled event)
   */
  private IContSpaceEventRecord getTriggeringEffect() {
    return triggeringEffect;
  }

  /**
   * @return Effects triggered by this one (empty list if none)
   */
  @Override
  public Collection<IContSpaceEventRecord> getTriggeredEffects() {
    return triggeredEffects;
  }

  /**
   * @param triggeringEffect
   *          Further effect triggered by this one
   */
  public void setTrigger(ContSpaceEventRecord triggeringEffect) {
    this.triggeringEffect = triggeringEffect;
    triggeringEffect.triggeredEffects.add(this);
    if (!this.getState().isSuccess()) {
      triggeringEffect.setFailed();
    }
  }

  @Override
  public Collection<? extends MLSpaceRule> getAllRules() {
    if (cachedAggregatedVersion == null) {
      aggregate();
    }
    return cachedAggregatedVersion.getRules();
  }

  @Override
  public Map<SpatialEntity, ICompChange> getAllCompChanges() {
    if (cachedAggregatedVersion == null) {
      aggregate();
    }
    return cachedAggregatedVersion.getCompChanges();
  }

  /**
   * Cache result of {@link #aggregateRecords(ContSpaceEventRecord)} (should
   * only be called once record does not change anymore
   */
  private ContSpaceEventRecord cachedAggregatedVersion;

  /**
   * Aggregate information on spatial entity changes (returns reference to own
   * instance if no triggered effects present). The aggregation result is
   * cached, i.e. calculations are not repeated for repeated calls to this
   * method. Thus, the method must not be called when triggered effects may
   * still be added (to this one or one directly or indirectly triggered).
   */
  private void aggregate() {
    if (this.getTriggeringEffect() != null) {
      throw new IllegalArgumentException(
          "Aggregation must start with first record in chain");
    }
    if (this.cachedAggregatedVersion != null) {
      throw new IllegalStateException();
    }
    if (this.getTriggeredEffects().isEmpty()) {
      this.cachedAggregatedVersion = this;
      return;
    }
    Map<SpatialEntity, ICompChange> changes =
        new LinkedHashMap<>(this.getChanges());
    Collection<MLSpaceRule> rules = new LinkedList<>(this.getRules());
    for (IContSpaceEventRecord tr : this.getTriggeredEffects()) {
      try {
        aggregateRecordsRecursively(tr, changes, rules);
      } catch (IllegalArgumentException ex) {
        ApplicationLogger.log(Level.SEVERE, "Exception in\n" + this);
        throw ex;
      }
    }
    ContSpaceEventRecord rv = new ContSpaceEventRecord(rules);
    for (Map.Entry<SpatialEntity, ICompChange> e : changes.entrySet()) {
      rv.putChange(e.getKey(), e.getValue());
    }
    this.cachedAggregatedVersion = rv;
    // return rv;
  }

  private static void aggregateRecordsRecursively(IContSpaceEventRecord tr2,
      Map<SpatialEntity, ICompChange> changes, Collection<MLSpaceRule> rules) {
    for (Map.Entry<SpatialEntity, ICompChange> e : tr2.getCompChanges()
        .entrySet()) {
      SpatialEntity ent = e.getKey();
      if (changes.containsKey(ent)) {
        changes.put(ent, CompChange.merge(changes.get(ent), e.getValue()));
      } else {
        changes.put(ent, e.getValue());
      }
    }
    rules.addAll(tr2.getRules());
    for (IContSpaceEventRecord tr : tr2.getTriggeredEffects()) {
      aggregateRecordsRecursively(tr, changes, rules);
    }
  }

  private Map<SpatialEntity, IDisplacementVector> cachedCompMoves = null;

  private Collection<SpatialEntity> cachedCreatedComps = null;

  private Collection<SpatialEntity> cachedDissolvedComps;

  @Override
  public Map<SpatialEntity, IDisplacementVector> getCompMoves() {
    if (cachedCompMoves == null) {
      determineCompChanges();
    }
    return cachedCompMoves;
  }

  @Override
  public Collection<SpatialEntity> getCompCreations() {
    if (cachedCreatedComps == null) {
      determineCompChanges();
    }
    return cachedCreatedComps;
  }

  @Override
  public Collection<SpatialEntity> getCompDestructions() {
    if (cachedDissolvedComps == null) {
      determineCompChanges();
    }
    return cachedDissolvedComps;
  }

  private void determineCompChanges() {
    cachedCompMoves = new LinkedHashMap<>();
    cachedCreatedComps = new ArrayList<>();
    cachedDissolvedComps = new ArrayList<>();
    for (Map.Entry<SpatialEntity, ICompChange> e : getAllCompChanges()
        .entrySet()) {
      ICompChange change = e.getValue();
      if (change.equals(CREATED)) {
        cachedCreatedComps.add(e.getKey());
      } else if (change.equals(DESTROYED)) {
        cachedDissolvedComps.add(e.getKey());
      } else {
        IDisplacementVector move = change.getPosUpd();
        if (move != null && !move.isNullVector()) {
          cachedCompMoves.put(e.getKey(), move);
        }
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(state);
    builder.append(" (");
    builder.append(numInfo);
    builder.append("): ");
    builder.append(super.toString());
    if (triggeredEffects != null) {
      builder.append(" triggered ");
      builder.append(triggeredEffects);
    }
    return builder.toString();
  }

  /**
   * Container for changes to a single spatial entity (enclosing comp, position,
   * attributes)
   * 
   * @author Arne Bittig
   * @date 01.10.2012
   */
  public static class CompChange extends AbstractEventRecord.Change implements
      ICompChange {

    protected static CompChange merge(ICompChange first, ICompChange second) {
      Map<String, Object> oldAtts;
      Map<String, Object> newAtts;
      if (second.getOldAtts() == null) {
        oldAtts = first.getOldAtts();
        newAtts = first.getNewAtts();
      } else if (first.getOldAtts() == null) {
        oldAtts = second.getOldAtts();
        newAtts = second.getNewAtts();
      } else {
        oldAtts = new LinkedHashMap<>(second.getOldAtts());
        oldAtts.putAll(first.getOldAtts());
        newAtts = new LinkedHashMap<>(first.getNewAtts());
        newAtts.putAll(second.getNewAtts());
      }
      IDisplacementVector posUpd = mergePosUpd(first, second);
      IShapedComponent beforeComp =
          first.getEnclosingEntityChange() != null ? first
              .getEnclosingEntityChange().getFirstValue() : second
              .getEnclosingEntityChange() != null ? second
              .getEnclosingEntityChange().getFirstValue() : null;
      IShapedComponent afterComp =
          second.getEnclosingEntityChange() != null ? second
              .getEnclosingEntityChange().getSecondValue() : first
              .getEnclosingEntityChange() != null ? first
              .getEnclosingEntityChange().getSecondValue() : null;
      if (beforeComp != null && beforeComp.equals(afterComp)) {
        IShapedComponent intermediate =
            first.getEnclosingEntityChange().getSecondValue();
        assert intermediate.equals(second.getEnclosingEntityChange()
            .getFirstValue());
        ApplicationLogger.log(Level.FINE, "Enclosing comp change and back: \n"
            + beforeComp + "->" + intermediate + "->" + afterComp);
        beforeComp = null;
        afterComp = null;
      }
      Type type1 = first.getType();
      Type type2 = second.getType();
      CompChange rv =
          new CompChange(type1.getPriority() > type2.getPriority() ? type1
              : type2, oldAtts, newAtts, posUpd, beforeComp, afterComp);
      return rv;
    }

    private static IDisplacementVector mergePosUpd(ICompChange first,
        ICompChange second) {
      IDisplacementVector posUpd = first.getPosUpd();
      if (posUpd == null) {
        posUpd = second.getPosUpd();
      } else if (second.getPosUpd() != null) {
        posUpd = posUpd.plus(second.getPosUpd());
      }
      return posUpd;
    }

    private final IDisplacementVector posUpd;

    private final Map<String, Object> oldAtts;

    private final Map<String, Object> newAtts;

    private final boolean spatialAttsChanged;

    private final Type type;

    /**
     * Constructor for modification changes
     * 
     * @param ent
     *          Entity (to extract current attribute values)
     * @param oldAtts
     *          Names of changed attributes and former values
     * @param bindChange
     *          Flag whether binding sites changed (counts as spatial att
     *          change)
     */
    public CompChange(AbstractModelEntity ent, Map<String, Object> oldAtts,
        boolean bindChange) {
      this.type = ICompChange.Type.ATTS_CHANGED_OR_CREATED;
      this.posUpd = null;
      this.oldAtts = oldAtts;
      this.newAtts = new LinkedHashMap<>(oldAtts);
      for (Map.Entry<String, Object> e : newAtts.entrySet()) {
        e.setValue(ent.getAttribute(e.getKey()));
      }
      this.spatialAttsChanged = bindChange || extractSpatialAttChange(oldAtts);
    }

    protected CompChange(IDisplacementVector posUpd) {
      this.type = ICompChange.Type.MOVED_ONLY;
      this.posUpd = posUpd;
      this.oldAtts = Collections.emptyMap();
      this.newAtts = Collections.emptyMap();
      this.spatialAttsChanged = false;
    }

    /**
     * Constructor for entity parent change due to previous parent's destruction
     * 
     * @param formerParent
     *          former enclosing entity
     */
    protected CompChange(SpatialEntity formerParent) {
      this.type = ICompChange.Type.ATTS_CHANGED_OR_CREATED;
      this.posUpd = null;
      this.oldAtts = Collections.emptyMap();
      this.newAtts = Collections.emptyMap();
      this.spatialAttsChanged = false;
      this.setEnclosingEntityChange(formerParent, null);
    }

    /**
     * Full constructor for {@link #merge(CompChange, CompChange)}
     * 
     * @param oldAtts
     * @param posUpd
     * @param beforeComp
     * @param afterComp
     */
    private CompChange(ICompChange.Type type, Map<String, Object> oldAtts,
        Map<String, Object> newAtts, IDisplacementVector posUpd,
        IShapedComponent beforeComp, IShapedComponent afterComp) {
      this.type = type;
      this.posUpd = posUpd;
      assert newAtts != null;
      this.oldAtts = oldAtts;
      this.newAtts = newAtts;
      // }
      this.spatialAttsChanged = extractSpatialAttChange(oldAtts);
      if (beforeComp == null != (afterComp == null)) {
        throw new IllegalStateException();
      }
      if (beforeComp != null) {
        this.setEnclosingEntityChange(beforeComp, afterComp);
      }
    }

    /**
     * Minimal constructor for dummy change instances
     * 
     */
    CompChange(Type type, boolean spatialAttsChanged) {
      this.type = type;
      this.posUpd = null;
      this.oldAtts = Collections.emptyMap();
      this.newAtts = Collections.emptyMap();
      this.spatialAttsChanged = spatialAttsChanged;
    }

    private static boolean extractSpatialAttChange(Map<String, Object> oldAtts) {
      // CHECK: compare to respective current value?!
      return oldAtts.containsKey(SpatialAttribute.DIFFUSION.toString())
          || oldAtts.containsKey(SpatialAttribute.DRIFT.toString())
          || oldAtts.containsKey(SpatialAttribute.VELOCITY.toString())
          || oldAtts.containsKey(SpatialAttribute.DIRECTION.toString());
    }

    @Override
    public final Type getType() {
      return type;
    }

    /**
     * @return Position update applied (null if none)
     */
    @Override
    public final IDisplacementVector getPosUpd() {
      return posUpd;
    }

    /**
     * @return Changed attributes and previous values
     */
    @Override
    public final Map<String, Object> getOldAtts() {
      return oldAtts;
    }

    /**
     * @return Changed attributes and (then) new values
     */
    @Override
    public Map<String, Object> getNewAtts() {
      return newAtts;
    }

    /**
     * @return true if spatial properties were affected
     */
    @Override
    public boolean spatialAttsChanged() {
      return spatialAttsChanged;
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      if (posUpd != null) {
        builder.append("move=");
        builder.append(posUpd);
        if (!oldAtts.isEmpty()) {
          builder.append(", ");
        }
      }
      if (oldAtts != null && !oldAtts.isEmpty()) {
        for (Map.Entry<String, Object> e : oldAtts.entrySet()) {
          String attName = e.getKey();
          builder.append(attName);
          builder.append(':');
          builder.append(e.getValue());
          builder.append("->");
          builder.append(newAtts.get(attName));
        }
      }
      builder.append(super.toString());
      return builder.toString();
    }
  }

  /** Marker instance for newly created entities */
  public static final ICompChange CREATED = new CompChange(
      ICompChange.Type.ATTS_CHANGED_OR_CREATED, true) {
    @Override
    public String toString() {
      return "CREATED";
    }
  };

  /** Marker instance for entity whose attempt failed */
  public static final ICompChange FAILED_MOVE_ATTEMPT = new CompChange(
      ICompChange.Type.UNCHANGED, false) {
    @Override
    public String toString() {
      return "FAILED_MOVE_ATTEMPT";
    }
  };

  /**
   * Marker instance for entity whose bindings changed (but no attribute change)
   * TODO: replace by proper binding recording!
   */
  public static final ICompChange BIND_CHANGE = new CompChange(
      ICompChange.Type.ATTS_CHANGED_OR_CREATED, true) {
    @Override
    public String toString() {
      return "binding site state change";
    }
  };

  /** Dummy instance for marking consumed spatial entities */
  public static final ICompChange DESTROYED = new CompChange(
      ICompChange.Type.DESTROYED, false) {
    @Override
    public String toString() {
      return "DESTROYED";
    }
  };

}
