/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.eventset.IEventQueue;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.rules.NSMReactionRule;
import model.mlspace.rules.NonTransferRule;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.SpecialAttributeModification;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.rules.populationmatching.NSMMatch;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;
import simulator.mlspace.event.NSMEvent;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.SubvolEventRecord;

/**
 * NSM Processor for ML-Space
 * 
 * Ignores any compartments (individuals) in the model, but works on the classes
 * and structures that are meant to be used for hybrid simulation, too. The
 * actual calculations of reaction and diffusion rates happen in the
 * {@link Subvol} class, as they have to be done for each subvolume
 * independently anyway and it is more convenient to store the rate vectors
 * directly where they belong.
 * 
 * Creation date: 16.02.2011
 * 
 * @author Arne Bittig
 */
public class NSMProcessor
    extends AbstractMLSpaceProcessor<NSMEvent, ISubvolEventRecord> {

  /** Serialization ID */
  private static final long serialVersionUID = 4651759823269446109L;

  /** subvolume list for initialization and record keeping */
  private final Collection<Subvol> subvols;

  /** map of next event of each subvolume (for eventQueue.requeue) */
  private final Map<Subvol, NSMEvent> subvolEventMap = new LinkedHashMap<>();

  private final NSMEntityReplacer rep = new NSMEntityReplacer();

  private final Collection<NSMReactionRule> reactionRules;

  /**
   * Constructor
   * 
   * @param model
   *          Mesoscopic ML-Space model to simulate
   * @param eventQueue
   *          Event queue to use
   * @param random
   *          Random number generator to use
   */
  public NSMProcessor(IMLSpaceModel model,
      IEventQueue<NSMEvent, Double> eventQueue, IRandom random) {
    this(model, eventQueue, random, new Timer());
  }

  protected NSMProcessor(IMLSpaceModel model,
      IEventQueue<NSMEvent, Double> eventQueue, IRandom random, Timer timer) {
    super(model, eventQueue, random, timer);
    this.subvols = model.getSubvolumes();
    this.reactionRules = model.getNSMReactionRules();
    recalculateSubvolEvents(subvols);
  }

  /**
   * Actual event handling for
   * {@link simulator.mlspace.event.NSMEvent.NSMEventType#NSMDIFFUSION} and
   * {@link simulator.mlspace.event.NSMEvent.NSMEventType#NSMREACTION}.
   * 
   * @param event
   *          Event to be handled
   * @return Event effect (see {@link ISubvolEventRecord})
   */
  @Override
  public ISubvolEventRecord handleEvent(NSMEvent event) {
    switch (event.getType()) {
    case NSMDIFFUSION:
      return handleDiffusionEvent(event);
    case NSMREACTION:
      return handleReactionEvent(event);
    default:
      unknownEventWarning(event);
      return null;
    }
  }

  @Override
  public void updateEventQueue(NSMEvent event, ISubvolEventRecord effect) {
    recalculateSubvolEvents(effect.getSubvolChanges().keySet()); // TODO:...
    // pass event and check there whether requeue or enqueue needed
  }

  /**
   * Execute NSM reaction event
   * 
   * - selection of firing reaction rule and appropriate state update
   * 
   * @param event
   *          Event container (incl. subvol & potential rule matches)
   * @return Event effect (see {@link ISubvolEventRecord})
   */
  private ISubvolEventRecord handleReactionEvent(NSMEvent event) {
    NSMMatch<NSMEntity> match = event.getReactionMatch(getRand());
    Subvol sv = event.getTriggeringComponent();
    return handleActualReaction(match, sv);
  }

  /**
   * Actual reaction application. CHECK: move to
   * {@link NSMMatch#applyReplacing(model.mlspace.rules.match.Match.IReplacer)}
   * and incorportate stuff from overridden variant?
   * 
   * @param match
   * @param sv
   * @return
   */
  protected ISubvolEventRecord handleActualReaction(NSMMatch<NSMEntity> match,
      Subvol sv) {
    List<Map<NSMEntity, Integer>> matched = match.getMatched();

    IUpdateableMap<NSMEntity, Integer> updVec = new UpdateableAmountMap<>(
        matched.size(), Integer.MIN_VALUE, Integer.MAX_VALUE);

    NonTransferRule rule = match.getRule();
    Map<String, Object> variables = match.getEnv();
    int index = 0;

    for (Map<NSMEntity, Integer> e : matched) {
      NSMEntity selected = org.jamesii.core.math.random.RandomSampler
          .sampleRouletteWheel(e, getRand());
      List<IAttributeModification> mods = rule.getEntityMod(index++);
      if (SpecialAttributeModification.CONSUMED.equals(mods)) {
        sv.updateState(selected, -1);
        updVec.update(selected, -1);
      } else if (!mods.isEmpty()) {
        NSMEntity modEntNew = new NSMEntity(selected);
        SuccessfulMatch.applyAttMods(modEntNew, mods, variables);
        sv.updateState(modEntNew, 1);
        updVec.update(modEntNew, 1);
        sv.updateState(selected, -1);
        updVec.update(selected, -1);
      }
    }
    addProduced(sv, rule, 1, variables, updVec);
    return new SubvolEventRecord(sv, updVec, rule);
  }

  protected void addProduced(ISubvol sv, NonTransferRule rule, int amount,
      Map<String, Object> variables,
      IUpdateableMap<NSMEntity, Integer> updateRecord) {
    for (InitEntity pEnt : rule.getProduced()) {
      NSMEntity prodEnt = getModEntFac().createNSMEntity(pEnt, variables);
      sv.updateState(prodEnt, amount);
      updateRecord.update(prodEnt, amount);
    }
  }

  /**
   * Execute NSM diffusion event: select a target subvolume call state update
   * method {@link #handleUnhinderedDiffusion(Subvol, Subvol)}
   * 
   * @param nsmEvent
   *          Subvolume (diffusion origin; target is chosen randomly here)
   * @return Event effect (see {@link INSMEffect})
   */
  private ISubvolEventRecord handleDiffusionEvent(NSMEvent nsmEvent) {
    Subvol sv = nsmEvent.getTriggeringComponent();
    Subvol diffTarget = getDiffusionTarget(sv);
    NSMEntity ent = nsmEvent.getDiffusingEntity(getRand());
    return handleActualDiffusion(sv, diffTarget, ent);
  }

  private Subvol getDiffusionTarget(Subvol sv) {
    return org.jamesii.core.math.random.RandomSampler
        .sampleRouletteWheel(sv.getNeighborhoodMap(), getRand());
  }

  /**
   * Execute NSM diffusion event given source and target subvol, assuming no
   * boundary between them: get random entity to diffuse, update states
   * 
   * @param source
   *          Source subvol
   * @param diffTarget
   *          Target subvol
   * @return Event effect (see {@link ISubvolEventRecord})
   */
  // overridden in subclass
  protected ISubvolEventRecord handleActualDiffusion(Subvol source,
      Subvol diffTarget, NSMEntity ent) {
    source.updateState(ent, -1);
    diffTarget.updateState(ent, 1);
    return new SubvolEventRecord(source,
        Collections.<NSMEntity, Integer> singletonMap(ent, -1), diffTarget);
  }

  /**
   * Recalculate diffusion and reaction propensities for given subvolumes.
   * Enqueues new events by accessing {@link #getTime()}, i.e. that should have
   * be set to the time of the event that has just happened)
   * 
   * @param subvolsToUpdate
   *          Subvolumes affected by the previous event (i.e. those for which to
   *          recalculate the propensities)
   */
  private void recalculateSubvolEvents(Collection<Subvol> subvolsToUpdate) {
    for (Subvol sv : subvolsToUpdate) {
      NSMEvent ev = subvolEventMap.get(sv);
      boolean newEvent = false;
      if (ev == null) { // CHECK: skip if sv.state.isEmpty() ?!
        newEvent = true;
        ev = new NSMEvent(sv);
        subvolEventMap.put(sv, ev);
      }
      double sumDiffR = ev.getSumOfDiffusionRates();
      double sumReactR = ev.getSumOfReactionRates(reactionRules);
      double sumRates = sumDiffR + sumReactR;
      if (sumRates == 0.) {
        if (!newEvent) {
          dequeueEvent(ev);
        }
        continue;
      }

      double tne = getExpDistTimeToNextEvent(sumRates);
      NSMEvent.NSMEventType type;
      // CHECK: Rescaling as in NSM paper ?!
      if (getRand().nextDouble() * sumRates <= sumDiffR) {
        type = NSMEvent.NSMEventType.NSMDIFFUSION;
      } else {
        type = NSMEvent.NSMEventType.NSMREACTION;
      }
      ev.setNSMType(type);
      if (newEvent) {
        enqueueEvent(ev, getTime() + tne);
      } else {
        requeueEvent(ev, getTime() + tne);
      }
    }
  }

  @Override
  public Collection<Subvol> getSubvols() {
    return subvols;
  }

  protected NSMEntityReplacer getNSMEntityReplacer() {
    return rep;
  }

  protected static class NSMEntityReplacer
      implements Match.IReplacer<AbstractModelEntity> {

    private final Map<NSMEntity, NSMEntity> replaced = new LinkedHashMap<>();

    @Override
    public AbstractModelEntity replace(AbstractModelEntity ent) {
      if (ent instanceof NSMEntity) {
        NSMEntity rep = new NSMEntity((NSMEntity) ent);
        replaced.put((NSMEntity) ent, rep);
        return rep;
      }
      return ent;
    }

    public NSMEntity getReplacement(NSMEntity ent) {
      return replaced.remove(ent);
    }

  }
}