/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;
import model.mlspace.subvols.SubvolInitializer;
import model.mlspace.subvols.SubvolUtils;

import org.jamesii.core.math.geometry.GeoUtils;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.hierarchy.LinkedHierarchy;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;

import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.event.ISpatialEntityEvent;
import simulator.mlspace.eventrecord.HybridEventRecord;
import simulator.mlspace.eventrecord.HybridEventRecord.CombinedCollection;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.SubvolEventRecord;
import simulator.mlspace.util.MLSpaceLogger.DebugLevel;

/**
 * Hybrid ML-Space simulator combining moving individual spatial entities with a
 * discretized structure for population-based simulation of dimensionless
 * entities. The former entities contain one or several of the discretized
 * subunits of the latter, i.e. the subunits of the lattice structure must be no
 * larger than the smallest moving individual.
 * 
 * @author Arne Bittig
 * @param <PE>
 *          Population event class
 */
public class HybridProcessor<PE extends IMLSpaceEvent<?>> extends
    AbstractHybridProcessor<ISpatialEntityEvent, PE> implements
    IHybridReactionHandler<SpatialEntity> {

  private static final long serialVersionUID = 3906639292936055289L;

  private final Map<SpatialEntity, Collection<Subvol>> compSvMap;

  /**
   * Hybrid simulator using given continuous space processor and given NCM-
   * (i.e. compartment-aware NSM-) processor.
   * 
   * The factory or user using this constructor must ensure that both processors
   * have been initialized with the same model and same event queue (the latter
   * must also be given as a parameter here).
   * 
   * @param contProc
   *          Continuous space processor
   * @param nsmProc
   *          NCM processor (i.e. compartment-aware NSM processor)
   * @param eventQueue
   *          Event queue used by both of them
   * @param timer
   *          Container of current time also used by contProc and nsmProc
   */
  protected HybridProcessor(AbstractContinuousSpaceProcessor contProc,
      IPopulationProcessor<PE> nsmProc, Class<PE> populationEventClass,
      IEventQueue<IMLSpaceEvent<?>, Double> eventQueue, Timer timer) {
    super(contProc, ISpatialEntityEvent.class, nsmProc, populationEventClass,
        eventQueue, timer);
    this.compSvMap = ((IMLSpaceModel) contProc.getModel()).getCompSvMap();
    if (getLogger().isDebugLevelSet(DebugLevel.COMP_MAPS_CORRECTNESS)) {
      checkForSvOverlapCorrectness();
    }
    contProc.setHybridReactionHandler(this);
  }

  @Override
  protected HybridEventRecord handleHybridEffect(
      IContSpaceEventRecord contEffect) {
    Collection<Subvol> reassignedSvs =
        reassignSubvolsForDestroyed(contEffect.getCompDestructions());

    CombinedCollection<ISubvolEventRecord> svEffects = null;
    if (!contEffect.getCompMoves().isEmpty()
        || !contEffect.getCompCreations().isEmpty()) {
      IHierarchy<Subvol> svMoves =
          getSubvolMoves(contEffect.getCompMoves(), compSvMap);
      Pair<List<DummySubvol>, Map<Subvol, SubvolEventRecord>> nscp =
          getNewSubvolContent(svMoves);
      List<DummySubvol> newSvContent = nscp.getFirstValue();
      Map<Subvol, SubvolEventRecord> svTakeOutEffects = nscp.getSecondValue();
      Pair<List<ISubvolEventRecord>, Collection<DummySubvol>> incorpRes =
          incorporateNewSvContent(newSvContent);
      Collection<DummySubvol> toBePushed = incorpRes.getSecondValue();
      List<ISubvolEventRecord> emptiedForCreatedEffects =
          insertNewlyCreated(contEffect.getCompCreations(), toBePushed);
      List<ISubvolEventRecord> pushEffects =
          pushSvContent(toBePushed, contEffect.getCompMoves());
      svEffects =
          new CombinedCollection<>(svTakeOutEffects.values(),
              incorpRes.getFirstValue());
      svEffects.addCollection(emptiedForCreatedEffects);
      svEffects.addCollection(pushEffects);
    }
    Collection<ISubvolEventRecord> hybridProdEffects =
        handleHybridEntityProduction();

    if (getLogger().isDebugLevelSet(DebugLevel.COMP_MAPS_CORRECTNESS)) {
      checkCompSvMapConsistency();
    }

    Collection<ISubvolEventRecord> svRecords =
        svEffects == null ? hybridProdEffects : svEffects
            .addCollection(hybridProdEffects);
    return new HybridEventRecord(contEffect, svRecords, reassignedSvs);
  }

  private Collection<Subvol> reassignSubvolsForDestroyed(
      Collection<SpatialEntity> compDestructions) {
    Collection<Subvol> rv = new ArrayList<>();
    for (SpatialEntity destEnt : compDestructions) {
      SpatialEntity parent = destEnt.getEnclosingEntity();
      Collection<Subvol> svsToReassign = compSvMap.remove(destEnt);
      for (Subvol sv : svsToReassign) {
        sv.setEnclosingEntity(parent);
      }
      compSvMap.get(parent).addAll(svsToReassign);
      rv.addAll(svsToReassign);
    }
    return rv;
  }

  private Collection<ISubvolEventRecord> handleHybridEntityProduction() {
    Collection<ISubvolEventRecord> effects =
        new ArrayList<>(hybridRecords.size());
    for (HybridRecord<SpatialEntity> hr : hybridRecords) {
      Collection<Subvol> relevantSvs =
          getCompSvsNearOtherComp(hr.getInComp(), hr.getNearComp());
      Collection<InitEntity> pEnts = hr.getEntiesToProduce();
      if (getLogger().checkAndLog(
          relevantSvs.isEmpty(),
          Level.SEVERE,
          "No free subvols in " + hr.getInComp() + " near " + hr.getNearComp()
              + " to produce " + pEnts + ". Omitted.")) {
        continue;
      }
      List<Subvol> targetSvs =
          RandomSampler.sample(pEnts.size(), relevantSvs, this.getRand());
      List<NSMEntity> targetSvUpdates = new ArrayList<>(targetSvs.size());
      int idx = 0;
      for (InitEntity pEnt : pEnts) {
        NSMEntity ent = this.getModEntFac().createNSMEntity(pEnt, hr.getEnv());
        targetSvs.get(idx++).updateState(ent, 1);
        targetSvUpdates.add(ent);
      }
      effects.add(new SubvolEventRecord(targetSvs, targetSvUpdates));
    }
    hybridRecords.clear();
    return effects;
  }

  private final Collection<HybridRecord<SpatialEntity>> hybridRecords =
      new LinkedList<>();

  @Override
  public HybridRecord<SpatialEntity> recordHybridEntityProduction(
      Collection<InitEntity> pEnts, SpatialEntity inComp,
      SpatialEntity nearComp, Map<String, Object> env) {
    HybridRecord<SpatialEntity> hr =
        new HybridRecord<>(pEnts, inComp, nearComp, env);
    hybridRecords.add(hr);
    return hr;
  }

  /**
   * @param comp
   *          SpatialEntity whose belonging Subvols to consider
   * @param nearComp
   *          SpatialEntity near which to look
   * @return Subvols belonging to comp neighboring one belonging to nearComp
   *         (TODO: change ret val to Map<Subvol,Double> if subvols may be of
   *         unequal size)
   */
  private Collection<Subvol> getCompSvsNearOtherComp(SpatialEntity comp,
      SpatialEntity nearComp) {
    if (nearComp == null) {
      return compSvMap.get(comp);
    }
    Collection<Subvol> nearCompSvs = compSvMap.get(nearComp);
    Collection<Subvol> rv = new LinkedHashSet<>();
    for (Subvol ncsv : nearCompSvs) {
      for (Subvol neigh : ncsv.getNeighbors()) {
        if (neigh.getEnclosingEntity() == comp) { // NOSONAR: intentional
          rv.add(neigh);
        }
      }
    }
    return rv;
  }

  /**
   * Incorporate designated new content into actual subvols, provided those are
   * empty or belong to the same enclosing compartment as the content of the
   * respective sv moving in. Collect cases where this is not possible (i.e.
   * subvol content is pushed away by moving comp) in the returned collection.
   * 
   * @param newSvContent
   * @param svTakeOutEffects
   * @return
   */
  private Pair<List<ISubvolEventRecord>, Collection<DummySubvol>> incorporateNewSvContent(
      List<DummySubvol> newSvContent) {
    Collection<DummySubvol> stillToBePushedContent = new ArrayList<>();
    List<ISubvolEventRecord> svEffects = new ArrayList<>(newSvContent.size());
    for (DummySubvol dummySv : newSvContent) {
      Subvol actualSv = dummySv.getRelatedRealSubvol();
      SpatialEntity beforeParent = actualSv.getEnclosingEntity();
      SpatialEntity afterParent = dummySv.getEnclosingEntity();
      if (dummySv.getState() == null) {
        // parent of sv moved away
        assert afterParent == null;
        assert actualSv.getState().isEmpty();
        // assert svMoves.getChildren(actualSv).isEmpty();
        // new parent of sv is enclosing comp or previous parent
        updateSvParentAndOverlapMap(actualSv, beforeParent.getEnclosingEntity());
        svEffects.add(new SubvolEventRecord(actualSv, Collections.EMPTY_MAP,
            beforeParent));
        continue;
      }
      if (beforeParent == afterParent) { // NOSONAR: intentional ==
        // content can stay if what moves in belongs to same parent
        Map<NSMEntity, Integer> incomingState = dummySv.getState();
        actualSv.updateState(incomingState);
        svEffects.add(new SubvolEventRecord(actualSv, incomingState));
        // no update of enclosing comp and overlap map needed
        // (but would not hurt for purposes of consistency check)
        continue;
      }
      if (!actualSv.getState().isEmpty()) {
        // remaining state: move with outgoing or into incoming sv
        // failed: push into (maybe otherwise unaffected) near sv later
        clearSubvolForPush(actualSv, beforeParent, stillToBePushedContent,
            svEffects);

      }
      assert actualSv.getState().isEmpty();
      // sv's content moved elsewhere (or sv was empty)
      Map<NSMEntity, Integer> incomingState = dummySv.getState();
      actualSv.updateState(incomingState);
      updateSvParentAndOverlapMap(actualSv, afterParent);
      svEffects.add(new SubvolEventRecord(actualSv, dummySv.getState(),
          beforeParent));
    }
    return new Pair<>(svEffects, stillToBePushedContent);
  }

  /**
   * Clear a subvol's state, create a record of the "clearing" and a dummy
   * containing the state and previous parent. To be used if (1) content could
   * not be moved along with an outgoing or into an incoming spatial entity and
   * is thus pushed or (2) a new spatial entity was created and takes up space
   * previously available to NSM entities in the respective surrounding one.
   * 
   * @param sv
   *          Subvol to clear for push
   * @param beforeParent
   *          Parent of subvol (usually still current enclosingEntity)
   * @param stillToBePushedContent
   *          Dummy Subvol collection to add push description to
   * @param svEffects
   *          Subvol change records to add new record to
   */
  private static void clearSubvolForPush(Subvol sv, SpatialEntity beforeParent,
      Collection<DummySubvol> stillToBePushedContent,
      Collection<ISubvolEventRecord> svEffects) {
    IUpdateableMap<NSMEntity, Integer> oldState = sv.clearState();
    stillToBePushedContent.add(new DummySubvol(sv, oldState, beforeParent));
    // may result in more rule applications; handled later
    svEffects.add(new SubvolEventRecord(sv, UpdateableAmountMap
        .negativeCopy(oldState)));
  }

  /**
   * @param createdComps
   * @param toBePushedSvContent
   * @return
   */
  private List<ISubvolEventRecord> insertNewlyCreated(
      Collection<SpatialEntity> createdComps,
      Collection<DummySubvol> toBePushedSvContent) {
    List<ISubvolEventRecord> creationClearingRecords = new ArrayList<>();
    for (SpatialEntity newComp : createdComps) {
      Collection<Subvol> parentsSubvols =
          compSvMap.get(newComp.getEnclosingEntity());
      Set<Subvol> subvolsForComp =
          SubvolInitializer.findSubvolsForComp(newComp, parentsSubvols
              .iterator().next());
      if (getLogger().isDebugLevelSet(DebugLevel.COMP_CREATION_AND_DESTRUCTION)) {
        validateSubvolsForNewComp(subvolsForComp, parentsSubvols,
            newComp.getEnclosingEntity());
      }
      for (Subvol sv : subvolsForComp) {
        if (!sv.getState().isEmpty()) {
          clearSubvolForPush(sv, sv.getEnclosingEntity(), toBePushedSvContent,
              creationClearingRecords);
        }
        sv.setEnclosingEntity(newComp);
      }
      parentsSubvols.removeAll(subvolsForComp);
      compSvMap.put(newComp, subvolsForComp);
    }
    return creationClearingRecords;
  }

  /**
   * @param subvolsForComp
   * @param parentsSubvols
   * @param parent
   */
  private static void validateSubvolsForNewComp(Set<Subvol> subvolsForComp,
      Collection<Subvol> parentsSubvols, SpatialEntity parent) {
    for (Subvol svForComp : subvolsForComp) {
      assert svForComp.getEnclosingEntity() == parent; // NOSONAR: id!!
      assert parentsSubvols.contains(svForComp);
    }
  }

  /**
   * Handle remaining content of subvols into which a compartment moved, where
   * it was not possible to incorporate all of the content into the incoming
   * compartment. In other words, the moving comp is pushing entities away.
   * 
   * @param stillToBePushedContent
   *          Dummy subvol with content to be pushed away and previous parent
   *          (parent of related real subvol has already been updated)
   * @param compMoves
   *          Comp move map (for finding push target)
   * @return
   */
  private List<ISubvolEventRecord> pushSvContent(
      Collection<DummySubvol> stillToBePushedContent,
      Map<SpatialEntity, IDisplacementVector> compMoves) {
    List<ISubvolEventRecord> records =
        new ArrayList<>(stillToBePushedContent.size());
    for (DummySubvol dummySv : stillToBePushedContent) {
      Subvol relSubvol = dummySv.getRelatedRealSubvol();
      SpatialEntity beforeParent = dummySv.getEnclosingEntity();
      SpatialEntity afterParent = relSubvol.getEnclosingEntity();
      assert afterParent != beforeParent; // NOSONAR: intentional obj. id.
      IDisplacementVector move = compMoves.get(afterParent);
      // move is null for newly created svs
      IPositionVector targetPos =
          move == null ? relSubvol.getPosition() : relSubvol.getPosition()
              .plus(move);
      Subvol target;
      // find some other suitable push targets
      // compSvMap is updated already (i.e. contains following state):
      Collection<Subvol> parentSvs = compSvMap.get(beforeParent);
      Subvol[] potentialTargets =
          parentSvs.toArray(new Subvol[parentSvs.size()]);
      // TODO: in rare & strange cases, this may be empty
      // (store and keep dummy somewhere?)
      Arrays.sort(potentialTargets,
          GeoUtils.distanceToPointComparator(targetPos));
      target = potentialTargets[0]; // TODO: >1 equally close ?!
      target.updateState(dummySv.getState());
      records.add(new SubvolEventRecord(target, dummySv.getState()));
    }
    return records;
  }

  /**
   * Check whether the flag for comp map correctness checks is set, perform
   * checks if needed, log warning if any check fails.
   */
  private void checkForSvOverlapCorrectness() {
    Map<SpatialEntity, Collection<Subvol>> errors =
        SubvolUtils.getCompSvMapErrors(getSpatialEntities().getAllNodes(),
            compSvMap);
    getLogger().checkAndLog(!errors.isEmpty(), Level.SEVERE,
        "Comp->Subvols map errors: " + errors);
    Map<SpatialEntity, Double> volDev =
        SubvolUtils.getVolumeDeviation(compSvMap);
    getLogger().log(
        Level.INFO,
        "Max size (area/volume) due to approximation by subvol: "
            + Collections.max(volDev.values()));
  }

  /**
   * @param movedComps
   *          Compartments->Displacement map (i.e. moves)
   * @param compSvOverlap
   *          SpatialEntity->Subvols overlap map (before move)
   * @return Subvol move "hierarchy" where parent==target & child==source
   */
  private static IHierarchy<Subvol> getSubvolMoves(
      Map<SpatialEntity, IDisplacementVector> movedComps,
      Map<SpatialEntity, Collection<Subvol>> compSvOverlap) {
    IHierarchy<Subvol> svMoves = new LinkedHierarchy<>();
    for (Map.Entry<SpatialEntity, IDisplacementVector> mce : movedComps
        .entrySet()) {
      Collection<Subvol> compSvs = compSvOverlap.get(mce.getKey());
      IDisplacementVector compMove = mce.getValue();
      for (Subvol sv : compSvs) {
        Subvol target =
            SubvolUtils.findSubvolIncludingPoint(sv,
                sv.getPosition().plus(compMove)); // CHECK: better way?
        svMoves.addChildParentRelation(sv, target);
      }
    }
    return svMoves;
  }

  private Pair<List<DummySubvol>, Map<Subvol, SubvolEventRecord>> getNewSubvolContent(
      IHierarchy<Subvol> svMoves) {
    Collection<Subvol> allAffectedSvs = svMoves.getAllNodes();
    List<DummySubvol> newSubvolStates = new ArrayList<>(allAffectedSvs.size());
    Map<Subvol, SubvolEventRecord> svChanges =
        new LinkedHashMap<>(allAffectedSvs.size());
    for (Subvol target : allAffectedSvs) {
      Collection<Subvol> sources = svMoves.getChildren(target);
      int numSources = sources.size();
      if (numSources == 0) {
        // "target" is actually not a target and only affected because
        // the compartment above it moved away
        newSubvolStates.add(new DummySubvol(target, null, null));
        continue;
      }
      Subvol[] sourceArray;
      // boolean targetStateIncluded = false;
      if (svMoves.getParent(target) == null) {
        // target is not source of some other sv, i.e. does not move
        // anywhere and its content stays
        sourceArray = sources.toArray(new Subvol[numSources + 1]);
        sourceArray[numSources++] = target;
        // targetStateIncluded = true;
      } else if (numSources == 1) { // shortcut
        Subvol source = sources.iterator().next();
        IUpdateableMap<NSMEntity, Integer> state = source.clearState();
        newSubvolStates.add(new DummySubvol(target, state, source
            .getEnclosingEntity()));
        assert !svChanges.containsKey(source);
        svChanges.put(
            source,
            new SubvolEventRecord(source, UpdateableAmountMap
                .negativeView(state)));
        continue;
      } else {
        sourceArray = sources.toArray(new Subvol[numSources]);
      }
      Arrays.sort(sourceArray, GeoUtils.PARENT_SIZE_COMPARATOR);
      Subvol currentSource = sourceArray[0];
      IUpdateableMap<NSMEntity, Integer> firstState =
          currentSource.clearState();
      DummySubvol dummySv =
          new DummySubvol(target, firstState, currentSource.getEnclosingEntity());// CHECK
                                                                                // parent!
      assert !svChanges.containsKey(currentSource);
      svChanges.put(currentSource, new SubvolEventRecord(currentSource,
          UpdateableAmountMap.negativeCopy(firstState)));
      for (int i = 1; i < numSources; i++) {
        currentSource = sourceArray[i];
        SpatialEntity sourceParent = currentSource.getEnclosingEntity();
        if (sourceParent.equals(dummySv.getEnclosingEntity())) {
          IUpdateableMap<NSMEntity, Integer> state = currentSource.clearState();
          dummySv.updateState(state);
          assert !svChanges.containsKey(currentSource);
          svChanges.put(currentSource, new SubvolEventRecord(currentSource,
              UpdateableAmountMap.negativeView(state)));
        } else {
          // Note: dummySv parent-parent may differ from sourceParent
          // e.g. if dummySv-parent was transferred
          assert !svChanges.containsKey(currentSource);
          svChanges.put(currentSource,
              moveSubvolContentInto(currentSource, dummySv));
        }
      }
      newSubvolStates.add(dummySv);
    }
    return new Pair<>(newSubvolStates, svChanges);
  }

  /**
   * Update a given Subvol's parent and its entries in the Comp->Subvols overlap
   * map (from what is currently returned by the respective
   * {@link Subvol#getEnclosingEntity()} method call to the given new enclosing
   * comp)
   * 
   * @param sv
   *          Subvol whose parent and comp overlap to update
   * @param newEnclosingEntity
   *          new parent
   */
  private void updateSvParentAndOverlapMap(Subvol sv,
      SpatialEntity newEnclosingEntity) {
    SpatialEntity oldEnclosingEntity = sv.getEnclosingEntity();
    boolean removeSuccess = compSvMap.get(oldEnclosingEntity).remove(sv);
    getLogger().checkAndLog(
        !removeSuccess,
        Level.SEVERE,
        "Comp-Svs-Overlap map" + " was inconsistent: Should have contained "
            + sv + " but did not.");

    sv.setEnclosingEntity(newEnclosingEntity);
    compSvMap.get(newEnclosingEntity).add(sv);
  }

  /**
   *
   */
  private void checkCompSvMapConsistency() {
    if (compSvMap.values().contains(null)) {
      ApplicationLogger.log(Level.SEVERE, "Null value in comp->sv map");
    }
    Collection<SpatialEntity> compsCopy =
        new ArrayList<>(this.getSpatialEntities().getAllNodes());
    compsCopy.removeAll(compSvMap.keySet());
    if (!compsCopy.isEmpty()) {
      ApplicationLogger.log(Level.SEVERE,
          "No sv associated with " + compsCopy.size() + " spatial entities:\n"
              + compsCopy);
    }
  }

  /**
   * Implementation of ISubvol to hold new state and parent comp of a subvol
   * affected by a compartment move (such that the old subvol state can be kept
   * there)
   * 
   * @author Arne Bittig
   * @date Mar 21, 2012
   */
  static class DummySubvol implements ISubvol {

    private static final long serialVersionUID = -6386804943693076432L;

    private final SpatialEntity enclosingEntity;

    private final Subvol relatedRealSubvol;

    private IUpdateableMap<NSMEntity, Integer> state;

    /**
     * "Dummy subvol", i.e. container for new subvol content and parent after
     * move. Note that state and enclosingEntity will usually come from one and the
     * same subvol, which is not passed as parameter here to avoid side-effects
     * of the constructor on one of its parameters (as usually those subvol's
     * state shall be cleared along with the DummySubvol construction (e.g.
     * using {@link Subvol#clearState()}).
     * 
     * @param relatedRealSubvol
     *          Actual subvol this dummy refers to
     * @param state
     *          State to incorporate into actual subvol
     * @param enclosingEntity
     *          New enclosing comp of actual subvol
     */
    DummySubvol(Subvol relatedRealSubvol,
        IUpdateableMap<NSMEntity, Integer> state, SpatialEntity enclosingEntity) {
      this.relatedRealSubvol = relatedRealSubvol;
      this.state = state;
      this.enclosingEntity = enclosingEntity;
    }

    Subvol getRelatedRealSubvol() {
      return relatedRealSubvol;
    }

    @Override
    public SpatialEntity getEnclosingEntity() {
      return enclosingEntity;
    }

    @Override
    public Map<NSMEntity, Integer> getState() {
      return state;
    }

    @Override
    public IUpdateableMap<NSMEntity, Integer> clearState() {
      IUpdateableMap<NSMEntity, Integer> oldState = state;
      this.state = new UpdateableAmountMap<>();
      return oldState;
    }

    @Override
    public Integer updateState(NSMEntity ent, Integer uval) {
      return state.update(ent, uval);
    }

    @Override
    public void updateState(Map<NSMEntity, Integer> updVec) {
      state.updateAll(updVec);
    }

    @Override
    public IPositionVector getPosition() {
      return null;
    }

    @Override
    public IShape getShape() {
      return null;
    }

    @Override
    public String toString() {
      return "Dummy"
          + (enclosingEntity == null ? " " : " in " + enclosingEntity.idString()) + ": "
          + state + " (" + "of " + relatedRealSubvol + ")";
    }
  }
}