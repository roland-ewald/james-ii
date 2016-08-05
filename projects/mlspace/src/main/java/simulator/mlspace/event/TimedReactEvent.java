/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.event;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.TimedReactionRule;
import model.mlspace.rules.match.Match;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * This TimedReactEvent class encapsulates zero- and first-order reactions
 * related to one compartment (zero-order reactions must take place in a
 * context, depend on the surrounding compartment, more precisely its volume).
 * Rates of applicable reactions are stored internally and are updated with
 * every call to {@link #getSumOfReactionRates(Collection, Collection)}.
 * 
 * @author Arne Bittig
 * @date 01.06.2012
 */
public class TimedReactEvent implements ISpatialEntityEvent {

  private final SpatialEntity comp;

  /** Reaction rate for each subvol-related reaction rule */
  private final Map<Match<SpatialEntity>, Double> reactionRates =
      new LinkedHashMap<>();

  /**
   * @param comp
   */
  public TimedReactEvent(SpatialEntity comp) {
    this.comp = comp;
  }

  @Override
  public SpatialEntity getTriggeringComponent() {
    return comp;
  }

  /**
   * Calculate sum of reaction rates of reactions triggered by this compartment
   * only (not by collision with another) according to the usual SSA. Rates are
   * cached internally for subsequent calls to {@link #getFiringRule(IRandom)}.
   * 
   * @param firstOrderReactions
   * @param zeroOrderReactions
   * @return Sum of reaction rates
   */
  public Double getSumOfReactionRates(
      Collection<TimedReactionRule> firstOrderReactions,
      Collection<TimedReactionRule> zeroOrderReactions) {
    reactionRates.clear();
    return calculateReationRates(firstOrderReactions, comp.getEnclosingEntity(),
        comp) + calculateReationRates(zeroOrderReactions, comp, null);
  }

  /**
   * Calculate rate for each of the given reactions and store in internal
   * {@link #reactionRates} map, passing the given context and given
   * (potentially moving) entity to
   * {@link ReactionRule#match(model.mlspace.entities.AbstractModelEntity, model.mlspace.entities.AbstractModelEntity)
   * the rule's match method} while assuming no presence of a colliding entity.
   * 
   * @param reactions
   *          Collection of {@link ReactionRule}s (all of same order)
   * @param context
   *          The context, for zero-order reactions the triggering entity
   * @param ent
   *          The triggering entity for first-order reactions (null for zeroth
   *          order reactions)
   * @return sum of rates of given reactions (side effect: each individual rate
   *         stored in internal map)
   */
  private double calculateReationRates(Collection<TimedReactionRule> reactions,
      SpatialEntity context, SpatialEntity ent) {
    if (reactions == null) {
      return 0.;
    }
    double sumR = 0.;
    for (TimedReactionRule rule : reactions) {
      Match<SpatialEntity> match = rule.match(context, ent);
      double thisR = match.getRate();
      if (thisR > 0) {
        sumR += thisR;
        reactionRates.put(match, thisR);
      }
    }
    return sumR;
  }

  /**
   * Randomly select reaction rule in proportion to respective propensity
   * 
   * @param rand
   *          Random number generator
   * @return reaction rule applicable to this Comp
   */
  public Match<SpatialEntity> getFiringRule(IRandom rand) {
    return org.jamesii.core.math.random.RandomSampler.sampleRouletteWheel(
        reactionRates, rand);
  }

  @Override
  public String toString() {
    return "react of/in " + comp;
  }
}
