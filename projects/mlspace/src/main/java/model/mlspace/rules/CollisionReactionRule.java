/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.util.collection.ArrayMap;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.rules.match.SuccessfulMatchWithBindings;

/**
 * @author Arne Bittig
 * @date 08.06.2012
 */
public class CollisionReactionRule extends NonTransferRule {

  private static final long serialVersionUID = -1259700540684048180L;

  /**
   * @param lhs
   *          Rule left hand side (context, consumed & modified entities)
   * @param produced
   *          Entities that appear only on the right hand side of the rule
   * @param contextMods
   *          Modifications of context entity
   * @param allOtherMods
   *          Modifications to other entities
   * @param rate
   *          Rate constant or probability expression
   */
  public CollisionReactionRule(String name, RuleSide lhs,
      List<IAttributeModification> contextMods,
      List<List<IAttributeModification>> allOtherMods,
      List<InitEntity> produced, DoubleNode rate) {
    super(name, lhs, contextMods, allOtherMods, produced, rate);
  }

  /**
   * Check whether given rule applies to given entities and return value of the
   * rate (or propensity) field if yes (0 otherwise) (separate calls to
   * {@link #matchContext(AbstractModelEntity)} should not be needed)
   * 
   * @param context
   *          Context in which rule could apply to movedEnt and collEnt, e.g.
   *          surrounding compartment (may be identical to collEnt for some
   *          transfer rules)
   * @param movedEnt
   *          First reacting entity, or entity that triggered rule evaluation
   * @param collEnt
   *          Second reacting entity (i.e. movedEnt's possible reaction
   *          partner), or entity encountered by movedEnt
   * @return rate value
   */
  public <E extends AbstractModelEntity> List<Match<E>> match(E context,
      E movedEnt, E collEnt) {
    Map<String, Object> env = matchContext(context);
    if (env == null) {
      return Collections.EMPTY_LIST;// Matches.failure();
    }
    // second-order reaction (between individuals, i.e. no kinetics)
    List<RuleEntity> reactants = getLeftHandSide().getReactants();
    assert reactants.size() == 2;
    RuleEntity rEnt0 = reactants.get(0);
    RuleEntity rEnt1 = reactants.get(1);
    Map<String, BindingAction> bs0 = null;
    Map<String, BindingAction> bs1 = null;
    if (rEnt0 instanceof RuleEntityWithBindings) {
      bs0 = ((RuleEntityWithBindings) rEnt0).getAffectedBindingSites();
    }
    if (rEnt1 instanceof RuleEntityWithBindings) {
      bs1 = ((RuleEntityWithBindings) rEnt1).getAffectedBindingSites();
    }
    Match<E> match01 = null;
    if (rEnt0.matches(movedEnt, env) && rEnt1.matches(collEnt, env)) {
      match01 = createMatch(context, env, movedEnt, collEnt, bs0, bs1);
    }
    if (rEnt0.matches(collEnt, env) && rEnt1.matches(movedEnt, env)) {
      Match<E> match10 = createMatch(context, env, collEnt, movedEnt, bs0, bs1);
      if (match01 != null) {
        return Arrays.asList(match10, match01);
      } else {
        return Collections.singletonList(match10);
      }
    } else {
      if (match01 != null) {
        return Collections.singletonList(match01);
      } else {
        return Collections.EMPTY_LIST;
      }
    }
  }

  private <E extends AbstractModelEntity> Match<E> createMatch(E context,
      Map<String, Object> env, E ent0, E ent1, Map<String, BindingAction> bs0,
      Map<String, BindingAction> bs1) {
    Map<E, List<IAttributeModification>> mods = new ArrayMap<>(3);
    List<IAttributeModification> mod0 = getEntityMod(0);
    if (!mod0.isEmpty()) {
      mods.put(ent0, mod0);
    }
    List<IAttributeModification> mod1 = getEntityMod(1);
    if (!mod1.isEmpty()) {
      mods.put(ent1, mod1);
    }
    if (!getContextMod().isEmpty()) {
      mods.put(context, getContextMod());
    }
    if (bs0 == null && bs1 == null) {
      return new SuccessfulMatch<>(this, getRate((Map) env), env, mods);
    } else {
      return new SuccessfulMatchWithBindings<>(this, getRate((Map) env), env,
          mods, ent0, bs0, ent1, bs1);
    }
  }

  /**
   * Get second-order reactions matching given colliding entities & context from
   * given collection of reactions, including evaluating reaction probability
   * 
   * @param context
   *          Entity in which reaction takes place
   * @param ent1
   *          First reacting entity (usually the moving one)
   * @param ent2
   *          Second reacting entity (usually the one ent1 bumped into)
   * @param secondOrderReactionRules
   *          Potentially matching reactions
   * @param rand
   *          Random number generator for >0, <1 reaction probabilities
   * @return Collection of applicable reactions (randomly permuted)
   */
  public static <E extends AbstractModelEntity> List<Match<E>> getPotentialSecondOrderReactions(
      E context, E ent1, E ent2,
      Collection<CollisionReactionRule> secondOrderReactionRules,
      IRandom rand) {
    Map<Match<E>, Double> applReactRules = new LinkedHashMap<>();
    double threshold = rand.nextDouble();
    for (CollisionReactionRule react1 : secondOrderReactionRules) {
      for (Match<E> match : react1.match(context, ent1, ent2)) {
        if (match.getRate() > threshold) {
          applReactRules.put(match, match.getRate());
        }
      }
    }
    return RandomSampler.permuteRouletteWheel(applReactRules, rand);
  }

}
