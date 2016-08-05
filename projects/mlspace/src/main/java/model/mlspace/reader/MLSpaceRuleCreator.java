/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.entities.AbstractEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.NSMReactionRule;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.TimedReactionRule;
import model.mlspace.rules.TransferInRule;
import model.mlspace.rules.TransferOutRule;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.SpecialAttributeModification;

/**
 * Method(s) for creating rules from the information pieces parsed on the left
 * and right hand side of the arrow and after the rate symbol
 * 
 * @author Arne Bittig
 * @date 02.05.2014 (date of extraction from {@link MLSpaceParserHelper})
 */
public final class MLSpaceRuleCreator {

  public static final String PROB_MARKER = "p";

  public static final String RATE_MARKER = "r";

  public static final Collection<String> MARKERS =
      Arrays.asList(PROB_MARKER, RATE_MARKER);

  private MLSpaceRuleCreator() {
  }

  /**
   * @param name
   *          rule name (default one will be created if null or empty)
   * @param left
   *          Left hand rule side
   * @param contextAfter
   *          Context on right hand side
   * @param entsAfter
   *          Entities on right hand side
   * @param rate
   *          Rate expression
   * @param rateOrProb
   *          Flag whether rate expression is actually rate or probability (not
   *          possible for all rules, may be empty)
   * @param modEntFac
   *          Model entity factory (to check whether entities are dimensionless
   *          or not)
   * @param rand
   *          Random number generator (for random value modifiers)
   * @return Rule
   */
  public static MLSpaceRule parseRule(String name, RuleSide left,
      ModEntity contextAfter, List<ModEntity> entsAfter, DoubleNode rate,
      String rateOrProb, ModelEntityFactory modEntFac, IRandom rand) {

    RuleEntity contextBefore = left.getContext();
    if (left.getContext() == null != (contextAfter == null)) {
      assert entsAfter != null && !entsAfter.isEmpty();
      if (rateOrProb.equals(RATE_MARKER)) {
        ApplicationLogger.log(Level.SEVERE,
            "Rate-to-probability conversion not yet implemented.\n"
                + "Treating " + rate + " as probability in " + left + " ->...");
      }
      return parseTransferRule(name, left, contextAfter, entsAfter, rate, rand);

    }

    List<RuleEntity> reactants = left.getReactants();
    List<List<IAttributeModification>> allMods =
        new ArrayList<>(reactants.size());
    nextRuleEnt: for (int i = 0; i < reactants.size(); i++) {
      RuleEntity reactant = reactants.get(i);
      Iterator<ModEntity> prodIt = entsAfter.iterator();
      while (prodIt.hasNext()) {
        ModEntity prod = prodIt.next();
        if (reactant.getSpecies().equals(prod.getSpecies())) {
          reactants.set(i, extractBindingModEnt(reactant, prod));
          allMods.add(i, extractAttMods(reactant, prod, rand));
          prodIt.remove();
          continue nextRuleEnt;
        }
      }
      allMods.add(i, SpecialAttributeModification.CONSUMED);
    }

    List<IAttributeModification> contextMods =
        contextBefore == null ? Collections.<IAttributeModification> emptyList()
            : extractAttMods(contextBefore, contextAfter, rand);
    List<InitEntity> products = new ArrayList<>(entsAfter.size());
    for (ModEntity e : entsAfter) {
      products.add(MLSpaceParserHelper.modToInitEntity(e));
    }

    if (isNSMTriggered(reactants, entsAfter, modEntFac)) {
      if (rateOrProb.equals(PROB_MARKER)) {
        ApplicationLogger.log(Level.SEVERE,
            "Probability not allowed for rules not involving spatial entities.\n"
                + "Treating " + rate + " as rate in " + left + " ->...");
      }
      return new NSMReactionRule(name, left, contextMods, allMods, products,
          rate);
    }
    if (reactants.size() <= 1) {
      if (rateOrProb.equals(PROB_MARKER)) {
        ApplicationLogger.log(Level.SEVERE,
            "Probability not allowed for 0th- and 1st-order rules.\n"
                + "Treating " + rate + " as rate in " + left + " ->...");
      }
      return new TimedReactionRule(name, left, contextMods, allMods, products,
          rate);
    }
    if (reactants.size() > 2) {
      logRuleError(
          "Collision-triggered rule with more than two reactants is unlikely to be ever matched",
          left, contextAfter, products, rate);
    }
    if (rateOrProb.equals(RATE_MARKER)) {
      ApplicationLogger.log(Level.SEVERE,
          "Rate-to-probability conversion not yet implemented.\n" + "Treating "
              + rate + " as probability in " + left + " ->...");
    }
    return new CollisionReactionRule(name, left, contextMods, allMods, products,
        rate);
  }

  /**
   * Check whether rule with given reactants and products is NSM-only
   * 
   * @param entsBefore
   *          Reactants
   * @param products
   *          Products
   * @param modEntFac
   * @return true if no reactant is spatial or if reactant list is empty and all
   *         products are spatial (a warning is displayed if no reactant is
   *         spatial but a product is)
   */
  private static boolean isNSMTriggered(List<RuleEntity> entsBefore,
      List<ModEntity> products, ModelEntityFactory modEntFac) {
    if (entsBefore.isEmpty()) {
      return !anyIsSpatial(products, modEntFac);
    }
    if (anyIsSpatial(entsBefore, modEntFac)) {
      return false;
    }
    if (anyIsSpatial(products, modEntFac)) {
      ApplicationLogger.log(Level.SEVERE,
          "NSM-triggered rule creating spatial entity will not work correctly! "
              + products + " from " + entsBefore);
    }
    return true;
  }

  /**
   * OR of {@link #isSpatial(AbstractPartialEntity)} for all elements of a
   * collection
   * 
   * @param ents
   *          Collection of entities
   * @param modEntFac
   * @return true if any element of the collection is spatial
   * @see {@link #isSpatial(AbstractPartialEntity)}
   */
  private static boolean anyIsSpatial(
      Iterable<? extends AbstractEntity<?>> ents,
      ModelEntityFactory modEntFac) {
    for (AbstractEntity<?> ent : ents) {
      if (modEntFac.isSpatial(ent)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Find how a given entity has to be modified to match another of the same
   * species (the second entity need not define all the attributes present in
   * the first one; only those defined in the second will be considered
   * modified)
   * 
   * @param entBefore
   *          Entity before modification
   * @param prod
   *          Entity after modification
   * @param rand
   * @return List of modifications
   */
  private static List<IAttributeModification> extractAttMods(
      RuleEntity entBefore, ModEntity prod, IRandom rand) {
    if (!entBefore.getSpecies().equals(prod.getSpecies())) {
      throw new IllegalArgumentException("Cannot determine attribute "
          + "modification(s) if entities' species does not match.");
    }

    List<IAttributeModification> rv = new ArrayList<>();
    for (Map.Entry<String, ValueModifier> ae : prod.getAttributes()
        .entrySet()) {
      ValueModifier newValDef = ae.getValue();
      if (newValDef instanceof ValueModifier.IRandomModifier) {
        ((ValueModifier.IRandomModifier) newValDef).setRand(rand);
      }
      rv.add(newValDef.getAttMod(ae.getKey()));
    }
    return rv;
  }

  /**
   * @param key
   *          Rule entity before reaction
   * @param prod
   *          Rule entity after reaction
   * @return Rule entity reflecting before->after binding site changes (returns
   *         first parameter if none)
   */
  private static RuleEntity extractBindingModEnt(RuleEntity key,
      ModEntity prod) {
    assert!(key instanceof RuleEntityWithBindings)
        || ((RuleEntityWithBindings) key).getAffectedBindingSites().isEmpty();
    if (prod.getBindMods() == null || prod.getBindMods().isEmpty()) {
      return key;
    }
    assert key instanceof RuleEntityWithBindings;
    RuleEntityWithBindings before = (RuleEntityWithBindings) key;
    // TODO: checks (released sites occupied before! bind sites free?!)
    return new RuleEntityWithBindings(before, prod.getBindMods());

  }

  private static void logRuleError(String string, RuleSide left,
      AbstractEntity<?> contextAfter,
      List<? extends AbstractEntity<?>> entsAfter, DoubleNode rate) {
    String rightStr = entsAfter.toString();
    if (contextAfter != null) {
      rightStr = contextAfter + "[" + rightStr + "]";
    }
    ApplicationLogger.log(Level.SEVERE, "Rule error: " + string + " -- in\n"
        + left + " -> " + rightStr + " @ " + rate);
  }

  private static MLSpaceRule parseTransferRule(String name, RuleSide left,
      ModEntity contextAfter, List<ModEntity> entsAfter, DoubleNode rate,
      IRandom rand) {
    RuleEntity contextBefore = left.getContext();
    List<RuleEntity> entsBefore = left.getReactants();
    if (contextBefore != null && contextAfter == null) {
      if (entsBefore.size() != 1 || entsAfter.size() != 2) {
        logRuleError(
            "Context on left but not right hand side indicates transfer rule, but number of entities does not match: ",
            left, contextAfter, entsAfter, rate);
        return null;
      }
      return parseTransferRuleOut(name, left, entsAfter.get(0),
          entsAfter.get(1), rate, rand);
    }
    if (entsBefore.size() != 2 || entsAfter.size() != 1) {
      ApplicationLogger.log(Level.SEVERE,
          "Context on right but not left hand side indicates transfer rule.\n"
              + "However, number of entities does not match: " + entsBefore
              + "->" + entsAfter);
      return null;
    }
    return parseTransferRuleIn(name, left, contextAfter, entsAfter.get(0), rate,
        rand);
  }

  private static final String ILLEGAL_TRANSFER_RULE_MSG =
      "Non-transfer rule with transfer rule syntax!";

  private static TransferInRule parseTransferRuleIn(String name, RuleSide left,
      ModEntity target, ModEntity transEntAfter, DoubleNode rate,
      IRandom rand) {
    assert left.getContext() == null;
    assert left.getReactants().size() == 2;
    RuleEntity ent1 = left.getReactants().get(0);
    RuleEntity ent2 = left.getReactants().get(1);

    if (ent1.getSpecies().equals(ent2.getSpecies())) {
      throw new IllegalArgumentException(
          "No nesting of entities of the same species: " + ent1
              + " cannot enter " + ent2 + ", nor vice versa.");
    }
    if (ent1.getSpecies().equals(transEntAfter.getSpecies())) {
      if (!ent2.getSpecies().equals(target.getSpecies())) {
        throw new IllegalArgumentException(ILLEGAL_TRANSFER_RULE_MSG);
      }
      List<IAttributeModification> teMod =
          extractAttMods(ent1, transEntAfter, rand);
      List<IAttributeModification> tarMod = extractAttMods(ent2, target, rand);
      return new TransferInRule(name, ent1, teMod, ent2, tarMod, rate);
    } else if (!ent2.getSpecies().equals(transEntAfter.getSpecies())
        || !ent1.getSpecies().equals(target.getSpecies())) {
      throw new IllegalArgumentException(ILLEGAL_TRANSFER_RULE_MSG);
    } else {
      List<IAttributeModification> teMod =
          extractAttMods(ent2, transEntAfter, rand);
      List<IAttributeModification> tarMod = extractAttMods(ent1, target, rand);
      return new TransferInRule(name, ent2, teMod, ent1, tarMod, rate);
    }
    // checkRateRange(rate);
  }

  private static TransferOutRule parseTransferRuleOut(String name,
      RuleSide left, ModEntity ent3, ModEntity ent4, DoubleNode rate,
      IRandom rand) {
    if (ent3.getSpecies().equals(ent4.getSpecies())) {
      throw new IllegalArgumentException(
          "No nesting of entities of the same species: " + ent3
              + " cannot enter " + ent4 + ", nor vice versa.");
    }
    assert left.getReactants().size() == 1;
    RuleEntity transEntBefore = left.getReactants().get(0);
    RuleEntity source = left.getContext();
    if (ent3.getSpecies().equals(transEntBefore.getSpecies())) {
      if (!ent4.getSpecies().equals(source.getSpecies())) {
        throw new IllegalArgumentException(ILLEGAL_TRANSFER_RULE_MSG);
      }

      List<IAttributeModification> teMod =
          extractAttMods(transEntBefore, ent3, rand);
      List<IAttributeModification> srcMod = extractAttMods(source, ent4, rand);
      return new TransferOutRule(name, transEntBefore, teMod, source, srcMod,
          rate);
    } else if (!ent4.getSpecies().equals(transEntBefore.getSpecies())
        || !ent3.getSpecies().equals(source.getSpecies())) {
      throw new IllegalArgumentException(ILLEGAL_TRANSFER_RULE_MSG);
    } else {
      List<IAttributeModification> teMod =
          extractAttMods(transEntBefore, ent4, rand);
      List<IAttributeModification> srcMod = extractAttMods(source, ent3, rand);
      return new TransferOutRule(name, transEntBefore, teMod, source, srcMod,
          rate);
    }
    // checkRateRange(rate);
  }

}
