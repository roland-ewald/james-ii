/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.RecognitionException;
import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.simpletree.nullary.FixedValueNode;

import model.mlspace.entities.InitEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.reader.antlr.MLSpaceSmallParser;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.SpecialAttributeModification;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.populationmatching.PopulationMatchStrategy;
import model.mlspace.rules.populationmatching.NSMMatch;

/**
 * @author Arne Bittig
 * @date 11.06.2012
 */
public class NSMReactionRuleTest extends ChattyTestCase {

  /**
   * Test NSM-2nd-order rule matching for a simple case (no attributes)
   */
  public void test1stOrderSimpleMatch() {
    final RuleEntity ent1 = new RuleEntity(new Species("A"), null);
    final RuleEntity ent2 = new RuleEntity(new Species("B"), null);
    final RuleSide lhsO1 = new RuleSide(null, Arrays.asList(ent1));
    final NSMReactionRule order1Rule = new NSMReactionRule(null, lhsO1,
        Collections.<IAttributeModification> emptyList(),
        Arrays.<List<IAttributeModification>> asList(
            SpecialAttributeModification.CONSUMED),
        Arrays.asList(new InitEntity(ent2.getSpecies(), null, null)),
        new FixedValueNode(.23));
    Map<NSMEntity, Integer> stateVec1 = new LinkedHashMap<>(1);
    NSMEntity nsmEnt1 = new NSMEntity(ent1.getSpecies(), null);
    stateVec1.put(nsmEnt1, 2);

    Collection<NSMMatch<NSMEntity>> matches1a =
        PopulationMatchStrategy.match(order1Rule, null, stateVec1, 1.0);
    Collection<NSMMatch<NSMEntity>> matches1b =
        PopulationMatchStrategy.match(order1Rule, null, stateVec1, 2.0);
    assertEquals(1, matches1a.size());
    assertEquals(1, matches1b.size());
    NSMMatch<NSMEntity> match1a = matches1a.iterator().next();
    NSMMatch<NSMEntity> match1b = matches1b.iterator().next();
    assertEquals(match1a.getRate(), match1b.getRate());
    assertEquals(0.23 * 2, match1a.getRate());
    assertEquals(1, match1a.getMatched().size());
    assertTrue(match1a.getMatched().get(0).containsKey(nsmEnt1));
  }

  /**
   * Test NSM-2nd-order rule matching for a simple case (no attributes)
   */
  public void test2ndOrderSimpleMatch() {
    final RuleEntity ent1 = new RuleEntity(new Species("A"), null);
    final RuleEntity ent2 = new RuleEntity(new Species("B"), null);

    final RuleSide lhsO2 = new RuleSide(null, Arrays.asList(ent1, ent2));

    final NSMReactionRule order2Rule = new NSMReactionRule(null, lhsO2, null,
        Arrays.<List<IAttributeModification>> asList(
            SpecialAttributeModification.CONSUMED,
            SpecialAttributeModification.CONSUMED),
        null, new FixedValueNode(0.42));
    Map<NSMEntity, Integer> stateVec2 = new LinkedHashMap<>(2);
    stateVec2.put(new NSMEntity(ent1.getSpecies(), null), 23);
    stateVec2.put(new NSMEntity(ent2.getSpecies(), null), 5);
    final double volA = 2.0;
    final double volB = 3.0;

    Collection<NSMMatch<NSMEntity>> matches2a =
        PopulationMatchStrategy.match(order2Rule, null, stateVec2, volA);
    Collection<NSMMatch<NSMEntity>> matches2b =
        PopulationMatchStrategy.match(order2Rule, null, stateVec2, volB);
    assertEquals(1, matches2a.size());
    assertEquals(1, matches2b.size());

    Match<NSMEntity> match2a = matches2a.iterator().next();
    assertTrue(0. < match2a.getRate());
    Match<NSMEntity> match2b = matches2b.iterator().next();
    final double delta = 1e-10;
    assertEquals(match2b.getRate() * volB, match2a.getRate() * volA, delta);
    assertEquals(match2b.getRate(), 23 * 5 * 0.42 / 3., delta);
  }

  /**
   * Test NSM-2nd-order rule matching for a rule like S+S->...
   */
  public void test2ndOrderSymmetricMatch() {
    final RuleEntity ent1 = new RuleEntity(new Species("S"), null);

    final RuleSide lhs = new RuleSide(null, Arrays.asList(ent1, ent1));

    final NSMReactionRule order2Rule = new NSMReactionRule(null, lhs, null,
        Arrays.<List<IAttributeModification>> asList(
            SpecialAttributeModification.CONSUMED,
            SpecialAttributeModification.CONSUMED),
        null, new FixedValueNode(1));
    NSMEntity nsmEntity = new NSMEntity(ent1.getSpecies(), null);
    Map<NSMEntity, Integer> stateVec = Collections.singletonMap(nsmEntity, 1);

    Collection<NSMMatch<NSMEntity>> matches =
        PopulationMatchStrategy.match(order2Rule, null, stateVec, 1.);
    assertEquals(0, matches.size());
    Match<NSMEntity> match;// = matches.iterator().next();
    // assertEquals(0., match.getRate());

    stateVec = Collections.singletonMap(nsmEntity, 2);
    matches = PopulationMatchStrategy.match(order2Rule, null, stateVec, 1.);
    assertEquals(1, matches.size());
    match = matches.iterator().next();
    assertEquals(1., match.getRate());

    stateVec = Collections.singletonMap(nsmEntity, 3);
    matches = PopulationMatchStrategy.match(order2Rule, null, stateVec, 1.);
    assertEquals(1, matches.size());
    match = matches.iterator().next();
    assertEquals(3., match.getRate());
  }

  public void test2ndOrderMixedMatch()
      throws RecognitionException, IOException {

    final NSMReactionRule mixedRule = (NSMReactionRule) new MLSpaceSmallParser(
        "S(a==1,x=b) + S(y=a,b==1) -> S+S @ r=(3*x)+y", true).rule().rv;
    final Map<NSMEntity, Integer> mixedState = new LinkedHashMap<>();
    {
      Map<InitEntity, Integer> pop = new MLSpaceSmallParser(
          "1 Sys[7 S(a:2,b:1) + 11 S(a:1,b:5) + 13 S(a:1,b:1)]", true)
              .init(false).map;
      ModelEntityFactory mef = new ModelEntityFactory(null, null);
      Map<String, AbstractValueRange<?>> attMap = new LinkedHashMap<>();
      attMap.put("a", AbstractValueRange.newSingleValue(-3));
      attMap.put("b", AbstractValueRange.newSingleValue(-5));
      mef.registerSpeciesDefinition("S", attMap, null);
      for (Map.Entry<InitEntity, Integer> e : pop.keySet().iterator().next()
          .getSubEntities().entrySet()) {
        mixedState
            .putAll(mef.createNSMEntities(e.getKey(), e.getValue(), null));
      }

      Collection<NSMMatch<NSMEntity>> matches =
          PopulationMatchStrategy.match(mixedRule, null, mixedState, 1.);
      System.out.println(matches);
      assertEquals(4, matches.size());

      Map<Map<String, Object>, Double> envRateMap = new LinkedHashMap<>();
      for (NSMMatch<NSMEntity> m : matches) {
        envRateMap.put(m.getEnv(), m.getRate());
      }
      Map<String, Object> env = new HashMap<>();
      env.put("y", 1.);
      env.put("x", 5.);
      assertTrue(envRateMap.containsKey(env));
      assertEquals((5. * 3 + 1) * 11 * 13, envRateMap.get(env));
      env.put("y", 2.);
      assertTrue(envRateMap.containsKey(env));
      assertEquals((5. * 3 + 2) * 11 * 7, envRateMap.get(env));
      env.put("x", 1.);
      assertTrue(envRateMap.containsKey(env));
      assertEquals((1. * 3 + 2) * 13 * 7, envRateMap.get(env));
      env.put("y", 1.);
      assertTrue(envRateMap.containsKey(env));
      assertEquals((1. * 3 + 1) * 13 * 12 / 2., envRateMap.get(env));
    }
  }

  /**
   * Test NSM-3rd-order rule matching for a rule like S+S+S->...
   */
  public void test3rdOrderSymmetricMatch() {
    final RuleEntity ent1 = new RuleEntity(new Species("S"), null);

    final RuleSide lhs = new RuleSide(null, Arrays.asList(ent1, ent1, ent1));

    final NSMReactionRule order3Rule = new NSMReactionRule(null, lhs, null,
        Arrays.<List<IAttributeModification>> asList(
            SpecialAttributeModification.CONSUMED,
            SpecialAttributeModification.CONSUMED,
            SpecialAttributeModification.CONSUMED),
        null, new FixedValueNode(1));
    NSMEntity nsmEntity = new NSMEntity(ent1.getSpecies(), null);

    for (double volume : new double[] { 1., 0.5, 3. }) {

      Map<NSMEntity, Integer> stateVec = Collections.singletonMap(nsmEntity, 1);
      Collection<NSMMatch<NSMEntity>> matches =
          PopulationMatchStrategy.match(order3Rule, null, stateVec, volume);
      assertEquals(0, matches.size());
      Match<NSMEntity> match;// = matches.iterator().next();
      // assertEquals(0., match.getRate());

      stateVec = Collections.singletonMap(nsmEntity, 2);
      matches =
          PopulationMatchStrategy.match(order3Rule, null, stateVec, volume);
      assertEquals(0, matches.size());

      stateVec = Collections.singletonMap(nsmEntity, 3);
      matches =
          PopulationMatchStrategy.match(order3Rule, null, stateVec, volume);
      assertEquals(1, matches.size());
      match = matches.iterator().next();
      assertEquals(1. / volume / volume, match.getRate());

      stateVec = Collections.singletonMap(nsmEntity, 4);
      matches =
          PopulationMatchStrategy.match(order3Rule, null, stateVec, volume);
      assertEquals(1, matches.size());
      match = matches.iterator().next();
      assertEquals(4. / volume / volume, match.getRate());

      stateVec = Collections.singletonMap(nsmEntity, 5);
      matches =
          PopulationMatchStrategy.match(order3Rule, null, stateVec, volume);
      assertEquals(1, matches.size());
      match = matches.iterator().next();
      assertEquals(5. * 4 * 3 / (1 * 2 * 3) / volume / volume, match.getRate());
    }
  }
}