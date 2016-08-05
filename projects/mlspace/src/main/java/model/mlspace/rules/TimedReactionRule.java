/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.util.collection.ArrayMap;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.Matches;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.rules.match.SuccessfulMatchWithBindings;

/**
 * @author Arne Bittig
 * @date 08.06.2012
 */
public class TimedReactionRule extends NonTransferRule {

  private static final long serialVersionUID = -45859377296140042L;

  /**
   * @param lhs
   *          Rule left-hand side
   * @param produced
   *          Entities that appear only on the right hand side of the rule
   * @param contextMods
   *          Modifications to context
   * @param allOtherMods
   *          Modifications to other entities (in the order they appear on the
   *          left rule side)
   * @param rate
   *          Reaction rate/probability expression
   */
  public TimedReactionRule(String name, RuleSide lhs,
      List<IAttributeModification> contextMods,
      List<List<IAttributeModification>> allOtherMods,
      List<InitEntity> produced, DoubleNode rate) {
    super(name, lhs, contextMods, allOtherMods, produced, rate);
    assert getOrder() <= 1;
  }

  /**
   * @return Entity pattern the rule applies to for first-order, null for
   *         zero-order reactions (e.g. for indexing rules by species)
   */
  public RuleEntity getChangedEntity() {
    List<RuleEntity> reactants = getLeftHandSide().getReactants();
    assert reactants.size() <= 1;
    if (reactants.isEmpty()) {
      return null;
    }
    return reactants.get(0);
  }

  /**
   * Check whether given rule (zero or first-order only) applies to given entity
   * and context, return 0 if not and value of the rate expression (scaled by
   * context size/volume for zero-order reations) otherwise
   * 
   * @param context
   *          Context in which rule would be applied, e.g. surrounding
   *          compartment
   * @param ent
   *          Entity to which first-order rule would be applied (must be null
   *          for zero-order rules to match)
   * @return Rate assuming mass-action kinetics, 0 if context or ent does not
   *         match rule or if rule order > 1
   */
  public Match<SpatialEntity> match(SpatialEntity context, SpatialEntity ent) {
    Map<String, Object> env = matchContext(context);
    if (env == null) {
      return Matches.failure();
    }
    if (getOrder() == 1) {
      Map<SpatialEntity, List<IAttributeModification>> mods = new ArrayMap<>(2);
      if (context != null) {
        mods.put(context, getContextMod());
      }
      return matchAndGetRateFirstOrder(ent, env, mods);
    } else {
      assert getOrder() == 0;
      return new SuccessfulMatch<>(this, getRateZeroOrder(context, env), env,
          Collections.singletonMap(context, getContextMod()));
    }
  }

  /**
   * Get rate of zero-order reaction (matching context should have been
   * established beforehand, no further matching necessary)
   * 
   * @param context
   *          Context (for size/volume; matching should have been done)
   * @param env
   * @return rate, size/volume-adjusted
   */
  private double getRateZeroOrder(AbstractModelEntity context,
      Map<String, Object> env) {
    Double contextVolume =
        (Double) context.getAttribute(SpatialAttribute.SIZE.toString());
    return contextVolume * getRate((Map) env); // TODO: type safety
  }

  /**
   * @param ent
   *          Affected entity
   * @param env
   *          Map of variables extracted from context, to be amended with those
   *          from ent
   * @param mods
   *          Map of modifications of matched entities, containing those for the
   *          context, to be amended with those for ent
   * @return Match (containing env and mods if successful)
   */
  private Match<SpatialEntity> matchAndGetRateFirstOrder(SpatialEntity ent,
      Map<String, Object> env,
      Map<SpatialEntity, List<IAttributeModification>> mods) {
    List<RuleEntity> reactants = getLeftHandSide().getReactants();
    assert reactants.size() == 1;
    RuleEntity reactant = reactants.get(0);
    if (!reactant.matches(ent, env)) {
      return Matches.failure();
    }
    mods.put(ent, getEntityMod(0));
    if (reactant instanceof RuleEntityWithBindings) {
      Map<String, BindingAction> bs0 =
          ((RuleEntityWithBindings) reactant).getAffectedBindingSites();
      if (!bs0.isEmpty()) {
        return new SuccessfulMatchWithBindings<>(this, getRate((Map) env), env,
            mods, ent, bs0, null, null);
      }
    }
    return new SuccessfulMatch<>(this, getRate((Map) env), env, mods);
  }
}