/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.model.IModel;
import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.eventset.IEventQueue;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.binding.IEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.entities.spatial.Compartment.CompComplex;
import model.mlspace.entities.spatial.IMoveableEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.TimedReactionRule;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;
import simulator.mlspace.brownianmotion.IPositionUpdater;
import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.event.ISpatialEntityEvent;
import simulator.mlspace.event.MoveEvent;
import simulator.mlspace.event.TimedReactEvent;
import simulator.mlspace.eventrecord.ContSpaceEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange.Type;
import simulator.mlspace.util.MLSpaceLogger.DebugLevel;

/**
 * Base class for continuous-space simulator, implementing event queue handling
 * {@link AbstractMLSpaceProcessor#updateEventQueue(simulator.mlspace.event.IMLSpaceEvent, simulator.mlspace.eventrecord.IEventRecord)}
 * and the non-spatial part of reaction handling (i.e. compartment zeroth- and
 * first-order reactions, with spatial effects handled by abstract methods to be
 * implemented by a subclass).
 * 
 * The also provided {@link #initReactionRules(IMLSpaceModel)} method
 * initializes some internal fields (containing zeroth and first order reaction
 * rules) and must be called in a subclass' constructor (as it returns second
 * order reactions, which are only needed in the subclass, it is not called in
 * this class' constructor).
 * 
 * @author Arne Bittig
 * @date Mar 19, 2012
 */
public abstract class AbstractContinuousSpaceProcessor extends
    AbstractMLSpaceProcessor<ISpatialEntityEvent, IContSpaceEventRecord> {

  private static final long serialVersionUID = 7851855596821144228L;

  /** Position update handler (to get null vectors of appropriate type) */
  private final IPositionUpdater posUpdater;

  /** map of next reaction event of each compartment (for eventQueue.requeue) */
  private final Map<SpatialEntity, TimedReactEvent> compReactEventMap =
      new LinkedHashMap<>();

  /** map of next position update event of each compartment */
  private final Map<SpatialEntity, MoveEvent> compMoveEventMap =
      new LinkedHashMap<>();

  /**
   * Rules for first-order reactions, indexed by species of reacting
   * compartments
   */
  private Map<Species, Collection<TimedReactionRule>> firstOrderReactionRules;

  /** Rules for zero-order reactions, indexed by species (of context) */
  private Map<Species, Collection<TimedReactionRule>> zeroOrderReactionRules;

  /**
   * Index of species in times rules that are sensitive to context attribute
   * changes
   */
  private final Map<Species, Map<Species, Collection<String>>> contextAttributeSensitiveReactions =
      new LinkedHashMap<>();

  private IHybridReactionHandler<SpatialEntity> hybridReactionHandler = null;

  /**
   * @param model
   *          ML-Space model
   * @param eventQueue
   *          EventQueue to use
   * @param rand
   *          Random number generator
   * @param posUpdater
   *          Position update method
   */
  protected AbstractContinuousSpaceProcessor(IModel model,
      IEventQueue<ISpatialEntityEvent, Double> eventQueue, IRandom rand,
      IPositionUpdater posUpdater, Timer timer) {
    super(model, eventQueue, rand, timer);
    this.posUpdater = posUpdater;
  }

  @Override
  protected String getStartLogMessage() {
    return super.getStartLogMessage() + "\nPosUpdater: "
        + posUpdater.toString();
  }

  /**
   * Get reaction rules from model and group them into zeroth, first and second
   * order reactions
   * 
   * @param model
   *          MLSpace model
   * @return Second order reactions (needed for collision-triggered reaction
   *         processing elsewhere; zeroth and first are assigned to private
   *         fields in the process)
   */
  protected Collection<CollisionReactionRule> initReactionRules(
      IMLSpaceModel model) {
    Collection<CollisionReactionRule> secondOrderReactionRules =
        model.getCollisionTriggeredRules();
    firstOrderReactionRules = new LinkedHashMap<>();
    zeroOrderReactionRules = new LinkedHashMap<>();
    Collection<TimedReactionRule> reactionRules = model.getTimedReactionRules();
    for (TimedReactionRule rr : reactionRules) {
      switch (rr.getOrder()) {
      case 1:
        Species appliesToSpec = rr.getChangedEntity().getSpecies();
        CollectionUtils.putIntoListMultiMap(firstOrderReactionRules,
            appliesToSpec, rr);
        initContextSensitiveRule(rr, appliesToSpec);
        break;
      case 0:
        CollectionUtils.putIntoListMultiMap(zeroOrderReactionRules,
            rr.getContext().getSpecies(), rr);
        break;
      default:
        getLogger().log(Level.WARNING, "Reaction rule of order " + rr.getOrder()
            + " will never be applied: " + rr);

      }
    }
    return secondOrderReactionRules;
  }

  private void initContextSensitiveRule(TimedReactionRule rr,
      Species changedSpecies) {
    RuleEntity context = rr.getContext();
    if (context == null) {
      return;
    }
    Collection<String> am = context.attributesToMatch();
    // TODO: same for binding sites to match?!
    if (am.isEmpty()) {
      return;
    }
    Species contextSpecies = context.getSpecies();
    Map<Species, Collection<String>> outerMap =
        contextAttributeSensitiveReactions.get(contextSpecies);
    if (outerMap == null) {
      outerMap = new LinkedHashMap<>();
      contextAttributeSensitiveReactions.put(contextSpecies, outerMap);
    }
    // Species changedSpecies = rr.getChangedEntity().getSpecies();
    Collection<String> attColl = outerMap.get(changedSpecies);
    if (attColl == null) {
      attColl = new LinkedHashSet<>(am);
      outerMap.put(changedSpecies, attColl);
    } else {
      attColl.addAll(am);
    }
  }

  private void initBindingSensitiveRule(TimedReactionRule rr) {
    RuleEntity changedEnt = rr.getChangedEntity();
    if (!(changedEnt instanceof RuleEntityWithBindings)) {
      return;
    }
    Map<String, ? extends IEntityWithBindings<?>> bs =
        ((RuleEntityWithBindings) changedEnt).bindingEntries();
    if (bs.isEmpty()) {
      return;
    }
    boolean boundStateMatched = false;
    // TODO!: extract rules that match binding site occupants by attributes or
    // futher bindings

  }

  /**
   * Initialize event queue and related entries related to (zeroth and first
   * order) reaction events of a single compartment
   * 
   * @param comp
   *          SpatialEntity
   * @return true iff a reaction event was scheduled (i.e. false if no first- or
   *         zeroth-order reactions related to comp apply)
   */
  protected final boolean initTimedReactEvent(SpatialEntity comp) {
    TimedReactEvent ev = new TimedReactEvent(comp);
    double sumReactRates =
        ev.getSumOfReactionRates(firstOrderReactionRules.get(comp.getSpecies()),
            zeroOrderReactionRules.get(comp.getSpecies()));
    compReactEventMap.put(comp, ev);
    if (sumReactRates == 0) {
      return false;
    }
    enqueueEvent(ev, getTime() + getExpDistTimeToNextEvent(sumReactRates));
    return true;
  }

  /**
   * Initialize event queue and related entries related to position update
   * events of a single compartment
   * 
   * @param comp
   *          SpatialEntity
   * @return true iff a move event was scheduled (i.e. false if comp is
   *         immobile)
   */
  protected final boolean initMoveEvent(SpatialEntity comp) {
    double timeToNextUpdate = getTimeToNextMove(comp);
    if (Double.isInfinite(timeToNextUpdate)) {
      return false;
    }
    MoveEvent ev = new MoveEvent(comp, getTime());
    enqueueEvent(ev, getTime() + timeToNextUpdate);
    compMoveEventMap.put(comp, ev);
    return true;
  }

  /**
   * Get reasonable time to next position update of given spatial entity.
   * Extracted to own method (instead of calling
   * {@link IPositionUpdater#getReasonableTimeToNextUpdate(model.mlspace.entities.spatial.IMoveableEntity)}
   * directly) to allow for handling of bound entities as one.
   * 
   * @param comp
   *          Spatial entity to update (or not)
   * @return Time until next position update (if infinite: no update, please)
   */
  private double getTimeToNextMove(SpatialEntity comp) {
    if (comp instanceof Compartment
        && ((Compartment) comp).getComplex() != null) {
      CompComplex complex = ((Compartment) comp).getComplex();
      if (complex.getAnchor() != comp) { // NOSONAR: obj. id. intentional
        return Double.POSITIVE_INFINITY;
      } else {
        return posUpdater.getReasonableTimeToNextUpdate(
            wrapDiffusionInComplexDummy(complex.getDiffusion()));
      }
    }

    return posUpdater.getReasonableTimeToNextUpdate(comp);
  }

  @Override
  public IContSpaceEventRecord handleEvent(ISpatialEntityEvent event) {
    if (event instanceof MoveEvent) {
      return handleMoveEvent((MoveEvent) event);
    } else if (event instanceof TimedReactEvent) {
      return handleTimedReactionEvent((TimedReactEvent) event);
    }
    // default:
    unknownEventWarning(event);
    return null;
  }

  /**
   * Handle position update of a compartment
   * 
   * @param event
   *          Event with event compartment and time since last update
   * @return Effects of the event
   */
  protected abstract IContSpaceEventRecord handleMoveEvent(MoveEvent event);

  /**
   * Handle creation of new spatial entities.
   * 
   * @param parent
   *          SpatialEntity inside which to produce the entities
   * @param nearComp
   *          SpatialEntity that triggered the call (null if parent was trigger)
   * @param list
   *          List of entity templates for production
   * @param variables
   *          Local variables (potentially used for attributes of new entities)
   * @return List of produced entities (null if unsuccessful; empty list if none
   *         should have been produced)
   */
  protected abstract List<SpatialEntity> produceEntities(SpatialEntity parent,
      SpatialEntity nearComp, List<InitEntity> list,
      Map<String, Object> variables);

  /**
   * Handle dissolution of compartment
   * 
   * @param comp
   *          SpatialEntity to dissolve
   * @return Collection of affected (formerly nested) spatial entities
   */
  protected abstract Collection<SpatialEntity> dissolveSpatialEntity(
      SpatialEntity comp);

  /**
   * Handle (zeroth or first order) reaction of a compartment
   * 
   * @param event
   *          Event with event compartment
   * @return Effects of the event
   */
  protected IContSpaceEventRecord handleTimedReactionEvent(
      TimedReactEvent event) {
    Match<SpatialEntity> match = event.getFiringRule(getRand());
    SpatialEntity comp = event.getTriggeringComponent();

    List<SpatialEntity> prodEnts;
    TimedReactionRule rule = (TimedReactionRule) match.getRule();
    if (rule.getOrder() == 1) {
      prodEnts = produceEntities(comp.getEnclosingEntity(), comp,
          rule.getProduced(), match.getEnv());
    } else {
      assert rule.getOrder() == 0;
      prodEnts =
          produceEntities(comp, null, rule.getProduced(), match.getEnv());
    }
    if (prodEnts == null) {// unsuccessful
      return ContSpaceEventRecord.newFailedReact(comp, rule);
    }

    SuccessfulMatch.ModRecord<SpatialEntity> modRecord = match.apply();

    Collection<SpatialEntity> nestedCompsOfConsumed = null;
    if (!match.getConsumed().isEmpty()) {
      nestedCompsOfConsumed = dissolveSpatialEntity(comp);
    }

    ContSpaceEventRecord effect =
        new ContSpaceEventRecord(comp, rule, modRecord);
    effect.addAllCreated(prodEnts);
    if (nestedCompsOfConsumed != null) {
      effect.addDestroyed(comp, nestedCompsOfConsumed);
    }
    return effect;

  }

  @Override
  public void updateEventQueue(ISpatialEntityEvent event,
      IContSpaceEventRecord firstEffect) {
    if (!firstEffect.getState().isSuccess()) {
      updateEventForFailedAttempt(event, firstEffect);
      return;
    }
    ArrayList<SpatialEntity> nestedAffected = new ArrayList<>();
    for (Map.Entry<SpatialEntity, ICompChange> e : firstEffect
        .getAllCompChanges().entrySet()) {
      SpatialEntity comp = e.getKey();
      ICompChange change = e.getValue();
      if (change.getType().getPriority() >= Type.DESTROYED.getPriority()) {
        removeEvents(comp, event);
        continue;
      }
      if (change.equals(ContSpaceEventRecord.CREATED)) {
        initMoveEvent(comp);
        initTimedReactEvent(comp);
        getLogger().checkAndLog(DebugLevel.COMP_CREATION_AND_DESTRUCTION,
            Level.INFO, "Created " + comp + "(as part of " + firstEffect + ")");
        continue;
      }
      nestedAffected.addAll(updateChangeEvent(event, comp, change));
    }
    if (!nestedAffected.isEmpty()) {
      nestedAffected.removeAll(firstEffect.getAllCompChanges().entrySet());
      nestedAffected.remove(event.getTriggeringComponent());
      for (SpatialEntity ent : nestedAffected) {
        updateTimedReactEvent(ent, null);
      }
    }
  }

  /**
   * Update event queue entry/entries for a changed entity (as opposed to a
   * destroyed or newly created one) (extracted method for less cyclomatic
   * complexity & call from hybrid simulator -- NSM event may change spatial
   * entity's diffusion attribute value)
   * 
   * @param event
   * @param comp
   * @param change
   * @return Sub-entities that are affected as well
   */
  public Collection<SpatialEntity> updateChangeEvent(IMLSpaceEvent<?> event,
      SpatialEntity comp, ICompChange change) {
    if (change.spatialAttsChanged() || event instanceof MoveEvent
        && comp == event.getTriggeringComponent()) {
      updateMoveEvent(comp, event);
    }
    boolean firstOrderRelevantChange =
        change.getType().getPriority() > ICompChange.Type.MOVED_ONLY
            .getPriority() || !change.getOldAtts().isEmpty()
        || change.getEnclosingEntityChange() != null;
    if (firstOrderRelevantChange || comp == event.getTriggeringComponent()
        && event instanceof TimedReactEvent) {
      updateTimedReactEvent(comp, event);
    }
    if (firstOrderRelevantChange) {
      return getIndirectlyAffectedEntities(comp, change.getOldAtts().keySet());
    } else {
      return Collections.emptySet();
    }
  }

  private void updateEventForFailedAttempt(ISpatialEntityEvent event,
      IContSpaceEventRecord firstEffect) {
    // update only event-triggering comp in case of failure
    SpatialEntity comp = event.getTriggeringComponent();
    assert comp == firstEffect.getCompChanges().keySet().iterator().next();
    if (firstEffect.getRules().isEmpty()) {
      updateMoveEvent(comp, event);
    } else {
      updateTimedReactEvent(comp, event);
    }
  }

  /**
   * Update event map w.r.t. compartment's next position update
   * 
   * @param comp
   *          Spatial component whose event to update
   * @param dequeuedEvent
   *          Event that was just processed and hence is not in the queue
   *          anymore (for avoiding potentially costly dequeue(event) call)
   */
  private void updateMoveEvent(SpatialEntity comp,
      IMLSpaceEvent<?> dequeuedEvent) {
    MoveEvent ev = compMoveEventMap.get(comp);
    if (ev == null) {
      // diffusion constant may be 0 or have changed from 0 to >0
      initMoveEvent(comp);
      return;
    }
    assert ev.getTriggeringComponent() == comp;
    Double prevNextEventTime = ev == dequeuedEvent // NOSONAR: intentional
        ? null : dequeueEvent(ev);
    double lastUpdateTime = ev.getTimeOfLastUpdate();
    double timeToNextUpdate = getTimeToNextMove(comp);

    ev.setTimeOfLastUpdate(getTime());
    if (Double.isInfinite(timeToNextUpdate)) {
      return;
    }

    if (prevNextEventTime != null) {
      if (prevNextEventTime.equals(lastUpdateTime)) {
        getLogger().log(Level.SEVERE, getEventQueueType()
            + " event dequeued at (putative) time of last update! CHECK!");
      } else {
        timeToNextUpdate = weightTimeToNextEvent(lastUpdateTime,
            prevNextEventTime, timeToNextUpdate);
      }
    } // else oldNextEventTime was Inf or ev was dequeued before ->
      // comp triggered update -> oldNextEventTime was now!
    enqueueEvent(ev, getTime() + timeToNextUpdate);
  }

  /**
   * Consider fraction of time already passed between last and next event in
   * calculating time to next event: Scale new update time considering how much
   * time to (previously considered) next update has already passed
   * 
   * @param timeOfLastUpdate
   * @param oldNextEventTime
   * @param newUpdTime
   * @return Scaled new update time
   */
  private double weightTimeToNextEvent(double timeOfLastUpdate,
      double oldNextEventTime, double newUpdTime) {
    double oldUpdTime = oldNextEventTime - timeOfLastUpdate;
    double sinceLastUpd = getTime() - timeOfLastUpdate;
    return (1. - sinceLastUpd / oldUpdTime) * newUpdTime;
  }

  /**
   * Update event map w.r.t. compartment's next first-order reaction
   * 
   * @param comp
   *          SpatialEntity in question
   * @param deqeuedEvent
   *          Event that was just processed and hence is not in the queue
   *          anymore (for avoiding potentially costly dequeue(event) call)
   */
  public void updateTimedReactEvent(SpatialEntity comp,
      IMLSpaceEvent<?> deqeuedEvent) {
    TimedReactEvent ev = compReactEventMap.get(comp);
    double sumReactRates =
        ev.getSumOfReactionRates(firstOrderReactionRules.get(comp.getSpecies()),
            zeroOrderReactionRules.get(comp.getSpecies()));
    if (deqeuedEvent != ev) { // NOSONAR: intentional obj. id.
      if (sumReactRates > 0) {
        requeueEvent(ev, getTime() + getExpDistTimeToNextEvent(sumReactRates));
      } else {
        dequeueEvent(ev);
      }
    } else if (sumReactRates > 0) {
      enqueueEvent(ev, getTime() + getExpDistTimeToNextEvent(sumReactRates));
    }
  }

  /**
   * When attributes of an entity change, events relating to context-sensitive
   * rules of contained entities must be updated.
   * 
   * @param comp
   *          Entity whose attributes changed
   * @param set
   *          attributes that changed
   * @return indirectly affected entities, e.g. contained ones
   */
  private Collection<SpatialEntity> getIndirectlyAffectedEntities(
      SpatialEntity comp, Set<String> changedAtts) {
    Map<Species, Collection<String>> relevantSpecies =
        contextAttributeSensitiveReactions.get(comp.getSpecies());
    if (relevantSpecies == null) {
      return Collections.emptySet();
    }
    Collection<SpatialEntity> rv = new ArrayList<>();
    assert!relevantSpecies.isEmpty();
    for (SpatialEntity contEnt : getContainedEntities(comp)) {
      Collection<String> relevantAtts =
          relevantSpecies.get(contEnt.getSpecies());
      if (relevantAtts == null) {
        continue;
      }
      assert!relevantAtts.isEmpty();
      if (!Collections.disjoint(changedAtts, relevantAtts)) {
        rv.add(contEnt);
        // updateTimedReactEvent(contEnt, null); // not done here directly as
        // directly affected entities may also be found here, for which this has
        // been done already
      }
    }
    return rv;
  }

  protected Collection<SpatialEntity> getContainedEntities(SpatialEntity comp) {
    return this.getSpatialEntities().getChildren(comp);
  }

  /**
   * @param comp
   *          Entity whose events to remove
   * @param dequeuedEvent
   *          Event that was just processed and hence is not in the queue
   *          anymore (for avoiding potentially costly dequeue(event) call)
   */
  private void removeEvents(SpatialEntity comp,
      ISpatialEntityEvent dequeuedEvent) {
    MoveEvent mEv = compMoveEventMap.remove(comp);
    if (mEv == null) {
      assert comp.getDiffusionConstant() == 0.;
      assert comp.getDrift() == null || comp.getDrift().isNullVector();
    } else if (mEv != dequeuedEvent) { // NOSONAR: intentional obj. id.
      dequeueEvent(mEv);
      assert mEv != null;
    }
    TimedReactEvent rEv = compReactEventMap.remove(comp);
    assert rEv != null; // CHECK? correct? TODO: test == dequeuedEvent
    Double rTime = dequeueEvent(rEv);
    getLogger().checkAndLog(DebugLevel.COMP_CREATION_AND_DESTRUCTION,
        Level.INFO, "Removed events for " + comp + "(" + rEv + "@" + rTime
            + " and " + mEv + ")");

  }

  /**
   * Check whether a compartment is not (usually: no longer) present in the
   * internal compartment->event maps; log error message if it is (internal
   * consistency check method mostly for debugging purposes)
   * 
   * @param removedComp
   *          Removed SpatialEntity
   * @return true if removedComp is not present in event maps
   */
  protected boolean isRemovedFromEventMaps(SpatialEntity removedComp) {
    boolean rv = true;
    if (getLogger().checkAndLog(compReactEventMap.containsKey(removedComp),
        Level.SEVERE,
        "Removed " + removedComp + " still has reaction event.")) {
      rv = false;
    }
    if (getLogger().checkAndLog(compMoveEventMap.containsKey(removedComp),
        Level.SEVERE, "Removed " + removedComp + " still has move event.")) {
      rv = false;
    }
    return rv;
  }

  /**
   * Assign / set mechanism for handling of non-spatial entities
   * 
   * @param hna
   *          Hybrid notification handler
   */
  public void setHybridReactionHandler(
      IHybridReactionHandler<SpatialEntity> hna) {
    hybridReactionHandler = hna;
  }

  /**
   * Notify {@link IHybridReactionHandler hybrid reaction handler} of entity
   * production that cannot be handled by continuous-space processor
   * (convenience / wrapper method for call in subclass)
   * 
   * @param nonSpatial
   *          Entities to produce
   * @param parent
   *          Entity in which to produce something
   * @param nearComp
   *          Entity near, but outside which to produce something
   * @see IHybridReactionHandler#recordHybridEntityProduction(Collection,
   *      org.jamesii.core.math.geometry.IShapedComponent,
   *      org.jamesii.core.math.geometry.IShapedComponent, MLSpaceRule)
   */
  protected void recordHybridEntityProduction(List<InitEntity> nonSpatial,
      SpatialEntity parent, SpatialEntity nearComp, Map<String, Object> env) {
    hybridReactionHandler.recordHybridEntityProduction(nonSpatial, parent,
        nearComp, env);
  }

  protected IMoveableEntity wrapDiffusionInComplexDummy(double diff) {
    return movEntDummyForComplex.set(diff);
  }

  private final MoveableEntityDummy movEntDummyForComplex =
      new MoveableEntityDummy();

  /**
   * @author Arne Bittig
   * @date 29.11.2013
   */
  private static final class MoveableEntityDummy implements IMoveableEntity {
    private double diff;

    public IMoveableEntity set(double diff) {
      this.diff = diff;
      return this;
    }

    @Override
    public void move(IDisplacementVector disp) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IPositionVector getPosition() {
      throw new UnsupportedOperationException();
    }

    @Override
    public IDisplacementVector getDrift() {
      return null;
    }

    @Override
    public double getDiffusionConstant() {
      return diff;
    }
  }

}
