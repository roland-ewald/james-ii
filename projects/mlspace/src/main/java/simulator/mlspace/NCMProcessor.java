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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.NonTransferRule;
import model.mlspace.rules.TransferInRule;
import model.mlspace.rules.TransferOutRule;
import model.mlspace.rules.TransferRule;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.rules.match.SuccessfulMatch.ModRecord;
import model.mlspace.rules.populationmatching.NSMMatch;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.misc.Quadruple;

import simulator.mlspace.event.NSMEvent;
import simulator.mlspace.eventrecord.ContSpaceEventRecord.CompChange;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.SubvolEventRecord;

/**
 * Simulator for NSM with compartments (a.k.a. "Next Compartment Method")
 * 
 * @author Arne Bittig
 */
public class NCMProcessor extends NSMProcessor implements
    IPopulationProcessor<NSMEvent> {

  private static final long serialVersionUID = -2925057659685168271L;

  /** Rules for transfer of entities between compartments */
  private final Collection<TransferInRule> transferInRules;

  /** Rules for transfer of entities between compartments */
  private final Collection<TransferOutRule> transferOutRules;

  private final Collection<CollisionReactionRule> collisionRules;

  /**
   * @param model
   *          Model to simulate
   * @param eventQueue
   *          EventQueue to use
   * @param random
   *          Random number generator to use
   */
  public NCMProcessor(IMLSpaceModel model,
      IEventQueue<NSMEvent, Double> eventQueue, IRandom random) {
    this(model, eventQueue, random, new Timer());
  }

  /**
   * @param model
   * @param eventQueue
   * @param random
   * @param timer
   */
  protected NCMProcessor(IMLSpaceModel model,
      IEventQueue<NSMEvent, Double> eventQueue, IRandom random, Timer timer) {
    super(model, eventQueue, random, timer);
    this.transferInRules = model.getTransferInRules();
    this.transferOutRules = model.getTransferOutRules();
    // TODO? index transferrules by species of transferredentity?
    this.collisionRules = model.getCollisionTriggeredRules();
  }

  @Override
  protected ISubvolEventRecord handleActualReaction(NSMMatch<NSMEntity> match,
      Subvol sv) {
    ISubvolEventRecord rv = super.handleActualReaction(match, sv);
    List<IAttributeModification> contextMod = match.getRule().getContextMod();
    if (!contextMod.isEmpty()) {
      SpatialEntity enclosingEntity = sv.getEnclosingEntity();
      Map<String, Object> prevVals =
          SuccessfulMatch.applyAttMods(enclosingEntity, contextMod,
              match.getEnv());
      ((SubvolEventRecord) rv).setEnclosingEntityChange(enclosingEntity,
          new CompChange(enclosingEntity, prevVals, false));
    }
    return rv;
  }

  @Override
  protected ISubvolEventRecord handleActualDiffusion(Subvol source,
      Subvol target, NSMEntity ent) {
    SpatialEntity sourceEnclosingEnt = source.getEnclosingEntity();
    SpatialEntity targetEnclosingEnt = target.getEnclosingEntity();
    if (sourceEnclosingEnt == targetEnclosingEnt) {
      return super.handleActualDiffusion(source, target, ent);
    } else {
      Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>> quadruple =
          null;
      SpatialEntity boundaryEnt = null;
      if (sourceEnclosingEnt.getEnclosingEntity() == targetEnclosingEnt) {
        quadruple = handleInternalBoundaryCollision(source, target, ent, 1);
        boundaryEnt = sourceEnclosingEnt;
      } else if (targetEnclosingEnt.getEnclosingEntity() == sourceEnclosingEnt) {
        quadruple = handleExternalCollision(source, target, ent, 1);
        boundaryEnt = targetEnclosingEnt;
      } else if (targetEnclosingEnt == sourceEnclosingEnt) {
        throw new AssertionError("no boundary");
      } else {
        handleMultiBoundaryDiffusion(source, target, sourceEnclosingEnt,
            targetEnclosingEnt);
      }

      if (quadruple == null /* rules.isEmpty() */) { // TODO: out-and-in,
                                                     // multiple levels!
        return new SubvolEventRecord(source,
            Collections.<NSMEntity, Integer> emptyMap(), target,
            Collections.<NSMEntity, Integer> emptyMap(),
            Collections.<MLSpaceRule> emptyList());
      }
      SubvolEventRecord subvolEventRecord =
          new SubvolEventRecord(source, quadruple.getA(), target,
              quadruple.getB(), quadruple.getD());
      if (!quadruple.getC().isEmpty()) {
        assert boundaryEnt != null;
        subvolEventRecord.setEnclosingEntityChange(boundaryEnt, new CompChange(
            boundaryEnt, quadruple.getC(), false));
      }
      return subvolEventRecord;
    }
  }

  private boolean sameLevelWarningDisplayed = false;

  private boolean twoLevelWarningDisplayed = false;

  private void handleMultiBoundaryDiffusion(Subvol source, Subvol target,
      SpatialEntity sourceEnclosingEnt, SpatialEntity targetEnclosingEnt) {
    if (targetEnclosingEnt.getEnclosingEntity() == sourceEnclosingEnt
        .getEnclosingEntity()) {
      if (!sameLevelWarningDisplayed) {
        getLogger().log(
            Level.INFO,
            "Same-level (out-and-in) transfers not yet implemented. Source:"
                + source + ", target: " + target);
        sameLevelWarningDisplayed = true;
      }
    } else {
      if (!twoLevelWarningDisplayed) {
        getLogger().log(
            Level.INFO,
            "Subvol boundary represents "
                + "more than one compartment boundary."
                + " Proper implementation missing.");
        twoLevelWarningDisplayed = true;
      }
    }
  }

  @Override
  public Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>> handleExternalCollision(
      ISubvol source, ISubvol target, NSMEntity ent, int amount) {
    List<MLSpaceRule> appliedRules = new ArrayList<>();
    IUpdateableMap<NSMEntity, Integer> sourceChange = newAmountMap();
    IUpdateableMap<NSMEntity, Integer> targetChange = newAmountMap();
    Map<String, Object> collEntPrevVals = new LinkedHashMap<>();
    int remAmount =
        handleTransferOverBoundary(source, target, ent,
            target.getEnclosingEntity(), amount, transferInRules, appliedRules,
            sourceChange, targetChange, collEntPrevVals);
    if (remAmount == 0) { // shortcut
      return new Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>>(
          sourceChange, targetChange, collEntPrevVals, appliedRules);
    }
    // ModRecord<AbstractModelEntity> modRecord =
    handleReactionWithSpatialEntity(source, target.getEnclosingEntity(), ent,
        remAmount, appliedRules, sourceChange, collEntPrevVals);
    return new Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>>(
        sourceChange, targetChange, collEntPrevVals, appliedRules);
  }

  private Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>> handleInternalBoundaryCollision(
      ISubvol source, ISubvol target, NSMEntity ent, int amount) {
    List<MLSpaceRule> appliedRules = new ArrayList<>();
    IUpdateableMap<NSMEntity, Integer> sourceChange = newAmountMap();
    IUpdateableMap<NSMEntity, Integer> targetChange = newAmountMap();
    Map<String, Object> boundEntPrevVals = new LinkedHashMap<>();
    // int remAmount =
    handleTransferOverBoundary(source, target, ent,
        source.getEnclosingEntity(), amount, transferOutRules, appliedRules,
        sourceChange, targetChange, boundEntPrevVals);
    // if (remAmount == 0) { // shortcut
    return new Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>>(
        sourceChange, targetChange, boundEntPrevVals, appliedRules);
    // }
    // TODO: collision-from-inside reaction rules
  }

  private void handleReactionWithSpatialEntity(ISubvol source,
      SpatialEntity collEnt, NSMEntity nsmEnt, int amount,
      List<MLSpaceRule> applRules,
      IUpdateableMap<NSMEntity, Integer> sourceChange,
      Map<String, Object> collEntPrevVals) {
    SpatialEntity context = source.getEnclosingEntity();
    int remAmount = amount;
    List<Match<AbstractModelEntity>> matches;
    do {
      matches =
          CollisionReactionRule.getPotentialSecondOrderReactions(context,
              nsmEnt, collEnt, collisionRules, getRand());
      // CHECK: direction-dependence above?!

      for (Match<AbstractModelEntity> match : matches) {
        MLSpaceRule react = match.getRule();

        int numDiffEnts =
            getNumberOfAffectedEntities(remAmount, match.getRate());
        SuccessfulMatch.ModRecord<AbstractModelEntity> modRecord =
            match.applyReplacing(getNSMEntityReplacer());
        if (collectCollEntChanges(collEnt, collEntPrevVals, modRecord)) {
          numDiffEnts = 1; // attribute changes require one reaction application
                           // at a time
        }
        updateSubvols(source, sourceChange, source, sourceChange, nsmEnt,
            match, numDiffEnts, modRecord);
        addProduced(source, (NonTransferRule) react, numDiffEnts,
            match.getEnv(), sourceChange);
        applRules.add(react);
        // try to transfer remaining ents in following loop iterations
        remAmount -= numDiffEnts;
        if (remAmount == 0) {
          break;
        }
      }
    } while (remAmount > 0 && !matches.isEmpty());
    // return collEntPrevVals; // TODO: return applied rules (0, 1, 2...)?
  }

  private static boolean collectCollEntChanges(SpatialEntity collEnt,
      Map<String, Object> collEntPrevVals,
      SuccessfulMatch.ModRecord<AbstractModelEntity> modRecord) {
    Map<AbstractModelEntity, Map<String, Object>> attMods =
        modRecord.getAttMods();
    if (attMods.isEmpty()) {
      return false;
    }
    assert attMods.size() == 1;
    Map<String, Object> map = attMods.get(collEnt);
    for (Map.Entry<String, Object> e : map.entrySet()) {
      if (!collEntPrevVals.containsKey(e.getKey())) {
        collEntPrevVals.put(e.getKey(), e.getValue());
      }
    }
    return true;
  }

  /**
   * Update state of subvolumes in response to diffusion or reaction event (in
   * latter case: source==target)
   * 
   * @param source
   * @param nsmEnt
   * @param sourceChange
   * @param target
   * @param targetChange
   * @param match
   * @param numDiffEnts
   * @param modRecord
   */
  private void updateSubvols(ISubvol source,
      IUpdateableMap<NSMEntity, Integer> sourceChange, ISubvol target,
      IUpdateableMap<NSMEntity, Integer> targetChange, NSMEntity nsmEnt,
      Match<AbstractModelEntity> match, int numDiffEnts,
      SuccessfulMatch.ModRecord<AbstractModelEntity> modRecord) {
    if (match.getConsumed().contains(nsmEnt)) {
      source.updateState(nsmEnt, -numDiffEnts);
      sourceChange.update(nsmEnt, -numDiffEnts);
      assert !modRecord.getAttMods().containsKey(nsmEnt);
    } else {
      NSMEntity repEnt = getNSMEntityReplacer().getReplacement(nsmEnt);
      // TODO: check whether target is really collision partner (as
      // opposed to collision with surrounding comp's wall from inside)
      if (repEnt == null) {
        repEnt = nsmEnt;
      } else {
        assert !repEnt.equals(nsmEnt);
        modRecord.getAttMods().remove(nsmEnt);
      }
      source.updateState(nsmEnt, -numDiffEnts);
      sourceChange.update(nsmEnt, -numDiffEnts);
      target.updateState(repEnt, numDiffEnts);
      targetChange.update(repEnt, numDiffEnts);
    }
  }

  private int handleTransferOverBoundary(ISubvol source, ISubvol target,
      NSMEntity ent, SpatialEntity boundaryEnt, int amount,
      Collection<? extends TransferRule> rules, List<MLSpaceRule> applRules,
      IUpdateableMap<NSMEntity, Integer> sourceChange,
      IUpdateableMap<NSMEntity, Integer> targetChange,
      Map<String, Object> collEntPrevVals) {
    int remAmount = amount;
    for (TransferRule tr : rules) {
      if (remAmount == 0) {
        break;
      }
      // does the transfer rule apply to ent?
      Match<AbstractModelEntity> match = tr.match(ent, boundaryEnt);
      if (!match.isSuccess()) {
        continue;
      }
      // evaluate "rate expression" - sample transfer probability
      int numDiffEnts = getNumberOfAffectedEntities(remAmount, match.getRate());
      if (numDiffEnts == 0) {
        continue;
      }
      ModRecord<AbstractModelEntity> modRecord =
          match.applyReplacing(getNSMEntityReplacer());
      if (collectCollEntChanges(boundaryEnt, collEntPrevVals, modRecord)) {
        numDiffEnts = 1; // attribute changes require one reaction application
                         // at a time
      }
      updateSubvols(source, sourceChange, target, targetChange, ent, match,
          numDiffEnts, modRecord);

      applRules.add(tr);
      // try to transfer remaining ents in following loop
      // iterations
      remAmount -= numDiffEnts;
    }
    return remAmount;
  }

  /**
   * 
   * @param totalAmount
   *          Amount of entities in total
   * @param probability
   *          Probability of an entity diffusing
   * @return Amount of entities diffusing
   */
  private int getNumberOfAffectedEntities(int totalAmount, double probability) {
    int numDiffEnts = 0;
    for (int i = 0; i < totalAmount; i++) {
      if (getRand().nextDouble() < probability) {
        numDiffEnts++;
      }
    }
    return numDiffEnts;
  }

  /**
   * @return new amount map allowing negative amounts
   */
  private static UpdateableAmountMap<NSMEntity> newAmountMap() {
    return new UpdateableAmountMap<>(1, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

}
