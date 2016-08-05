/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader.antlr;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.antlr.runtime.RecognitionException;
import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.simpletree.binary.AddNode;
import org.jamesii.core.math.simpletree.nullary.VariableNode;
import org.jamesii.core.util.logging.ApplicationLogger;

import model.mlspace.MLSpaceModel;
import model.mlspace.entities.AbstractEntity;
import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.binding.RuleEntityWithBindings;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.reader.antlr.MLSpaceSmallParser.species_def_return;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.TransferRule;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.match.Match;

/**
 * Test parser by parsing a complete model description (asserting only
 * non-emptiness of model)
 * 
 * @author Arne Bittig
 * @date Apr 4, 2012
 */
public class ParserTest extends ChattyTestCase {

  private static final String TPO = "Testing parsing of: ";

  private static final String HARD_CODED_ENDOSOME_MODEL = "one = 1;\r\n"
      + "System(shape:square, size:3,diffusion:0);\r\n"
      + "Cell(shape:circle,size:1,diffusion:0);\r\n"
      + "Endosome(shape:circle, size:0.2, diffusion:0.1,tmp:{'true','false'});\r\n"
      + "Particle(shape:circle, diffusion:0.5,size:.2);\r\n"
      + "NSMPart1(diffusion:0.23);\r\n" + "NSMPart2(diffusion:0.42);\r\n"
      + "\r\n" + "1 System(position:(0,0)) [\r\n"
      + "FOR x={-2,one,2} { FOR y=0:3:3 { 2 Particle(size:(0.01*(x*x+y))) }}\r\n"
      + " + FOR x=1:1 { x Cell(position:(0,0))} ];\r\n" + "\r\n"
      + "Part_creation: Endosome(tmp=='true')[] -> Endosome(tmp='false')[Particle()] @ 100000;\r\n"
      // +
      // "Particle() + Cell() -> Particle() + Cell()[Endosome()] @
      // 100000;\r\n"
      + "Particle() + Cell() -> Cell()[Particle()] @ p=0.9;\r\n"
      + "NSMRule: NSMPart1() + NSMPart2() -> NSMPart1() + NSMPart1() @ r=0.3;\r\n"
      + "Cell()[Particle()] -> Cell()[Endosome(tmp='true')] @ 100000;\r\n";

  private static final String MSG_WARNING =
      "\nThis will produce a \"SEVERE\" warning message that does not indicate test failure.";

  /**
   * @param modelString
   * @return
   */
  private static MLSpaceSmallParser getParserForString(String modelString,
      boolean partialFlag) {
    try {
      return new MLSpaceSmallParser(modelString, partialFlag);
    } catch (IOException ioe) {
      ApplicationLogger.log(Level.SEVERE, "Really unexpected exception: ", ioe);
      return null;
    }
  }

  /**
   * Test parsing of whole model
   */
  public void testModelParsing() {
    MLSpaceSmallParser parser =
        getParserForString(HARD_CODED_ENDOSOME_MODEL, false);
    ApplicationLogger.log(Level.INFO,
        "Testing parsing simple model:\n\n" + HARD_CODED_ENDOSOME_MODEL);
    MLSpaceModel model;
    int numSynErr;
    try {
      model = parser.fullmodel().model;
      numSynErr = parser.getNumberOfSyntaxErrors();
    } catch (RecognitionException e) {
      fail("Error parsing model: " + e);
      return;
    }
    ApplicationLogger.log(Level.INFO, "Model parsed: " + model);
    assertNotNull(model);
    ApplicationLogger.log(Level.INFO, "Comps:" + model.getCompartments());
    assertTrue(
        !model.getCompartments().isEmpty() || !model.getSubvolumes().isEmpty());
    assertEquals(
        "There were syntax errors during ML-Space test model" + " parsing.", 0,
        numSynErr);
  }

  /**
   * Test parsing of species definition with binding site(s) present
   * 
   * @throws RecognitionException
   *           Pass-on from auto-gen {@link MLSpaceSmallParser} method
   */
  public void testSpeciesDefinitionParsing() throws RecognitionException {
    String testString = "A(att1:{'val1','val2'},att2:[0...PI])<bs1:0>";
    ApplicationLogger.log(Level.INFO, TPO + testString);
    MLSpaceSmallParser parser = getParserForString(testString, true);
    species_def_return sdr = parser.species_def();
    ApplicationLogger.log(Level.INFO, sdr.toString());
    assertNoSyntaxErrors("species definition", parser);
  }

  /**
   * Test parsing of transfer rule (in)
   * 
   * @throws RecognitionException
   *           Pass-on from auto-gen {@link MLSpaceSmallParser} method
   */

  public void testTransferRuleParsing() throws RecognitionException {
    String testString = "A() + B() -> B()[A()] @ 0.5";
    ApplicationLogger.log(Level.INFO, TPO + testString);
    MLSpaceSmallParser parser = getParserForString(testString, true);
    MLSpaceRule rule = parser.rule().rv;
    ApplicationLogger.log(Level.INFO, rule.toString());
    assertNoSyntaxErrors("transfer rule (in)", parser);
    assertTrue(rule instanceof TransferRule);
  }

  /**
   * Test parsing of collision-triggered tule with binding site occupancy
   * relevant for matching
   * 
   * @throws RecognitionException
   *           Pass-on from auto-gen {@link MLSpaceSmallParser} method
   */
  public void testCollisionRuleWithBindingMatch() throws RecognitionException {

    String testString = "someName: A(att in [0...1],size==0.1)<s1:free,s2:occ>"
        + " + B()<sb:C()> -> A(att=1) + B() @ (1/4)^0.5";
    ApplicationLogger.log(Level.INFO, TPO + testString + MSG_WARNING);
    MLSpaceSmallParser parser = getParserForString(testString, true);
    MLSpaceRule rule = parser.rule().rv;
    ApplicationLogger.log(Level.INFO, rule.toString());
    assertNoSyntaxErrors("collision rule with binding", parser);
    assertEquals("someName", rule.getName());
    assertTrue(rule instanceof CollisionReactionRule);
    // assertEquals(0.5, // would require increase in method visibility
    // ((CollisionReactionRule) rule).getRate(Collections.EMPTY_MAP));
  }

  /**
   * Test parsing of binding site definition for matching
   * 
   * @throws RecognitionException
   *           Pass-on from auto-gen {@link MLSpaceSmallParser} method
   */
  public void testBindingMatch() throws RecognitionException {
    String testString = "<b1:free,b2:A()<site:free>>";
    ApplicationLogger.log(Level.INFO, TPO + testString + MSG_WARNING);
    MLSpaceSmallParser parser = getParserForString(testString, true);
    Map<String, RuleEntityWithBindings> bs = parser.bindingsites().map;
    ApplicationLogger.log(Level.INFO, bs.toString());
    assertNoSyntaxErrors("enities with binding actions", parser);
  }

  /**
   * Test parsing of binding actions definition
   * 
   * @throws RecognitionException
   *           Pass-on from auto-gen {@link MLSpaceSmallParser} method
   */
  public void testBindingAction() throws RecognitionException {
    String testString = "A(att:[0...1])<s1:release,s2:bind> + B()<sb:bind>";
    ApplicationLogger.log(Level.INFO, TPO + testString + MSG_WARNING);
    MLSpaceSmallParser parser = getParserForString(testString, true);
    List<? extends AbstractEntity<?>> ents = parser.entities_result(false).list;
    ApplicationLogger.log(Level.INFO, ents.toString());
    assertNoSyntaxErrors("binding actions", parser);
  }

  /**
   * Test parsing of binding actions definition
   * 
   * @throws RecognitionException
   *           Pass-on from auto-gen {@link MLSpaceSmallParser} method
   */
  public void testValueExtraction() throws RecognitionException {
    String testString = "A(e1=att1 in [0...1],e2=att2) + B(e3=attOfB) ->";
    ApplicationLogger.log(Level.INFO, TPO + testString + MSG_WARNING);
    MLSpaceSmallParser parser = getParserForString(testString, true);
    RuleSide lhs = parser.rule_left_hand_side().lhs;
    ApplicationLogger.log(Level.INFO, lhs.toString());
    assertNoSyntaxErrors(
        "value extraction (i.e. variable binding to attribute value)", parser);

    @SuppressWarnings("unchecked")
    CollisionReactionRule rule = new CollisionReactionRule(null, lhs, null,
        Arrays.<List<IAttributeModification>> asList(Collections.EMPTY_LIST,
            Collections.EMPTY_LIST),
        Collections.EMPTY_LIST,
        new AddNode(new VariableNode("e1"), new VariableNode("e2")));

    Map<String, Object> attA = new HashMap<>();
    attA.put("att2", 2);
    NSMEntity dummyContext = new NSMEntity(null, null);
    NSMEntity dummyEntB = new NSMEntity(new Species("B"),
        Collections.singletonMap("attOfB", (Object) 1));
    Compartment dummyEntA =
        new Compartment(new Species("A"), null, attA, null, null);

    dummyEntA.setAttribute("att1", 1.5);
    List<Match<AbstractModelEntity>> match =
        rule.match(dummyContext, dummyEntB, dummyEntA);
    assertTrue(match.isEmpty());
    dummyEntA.setAttribute("att1", 0.5);
    List<Match<AbstractModelEntity>> matchCorrect =
        rule.match(dummyContext, dummyEntB, dummyEntA);
    assertFalse(matchCorrect.isEmpty());
    assertNotNull(matchCorrect.get(0).getEnv());
    assertFalse(matchCorrect.get(0).getEnv().isEmpty());
    assertEquals(2.5, matchCorrect.get(0).getRate());
  }

  /**
   * Assert that parser indicates no syntax errors, display SEVERE message if
   * backtracking level is >0
   * 
   * @param what
   *          Description of what was parsed
   * @param parser
   *          Parser
   */
  public static void assertNoSyntaxErrors(String what,
      MLSpaceSmallParser parser) {
    assertEquals("Syntax errors during parsing of " + what, 0,
        parser.getNumberOfSyntaxErrors());
    if (parser.getBacktrackingLevel() != 0) {
      ApplicationLogger.log(Level.SEVERE, "Parser backtracking level "
          + parser.getBacktrackingLevel() + "parsing " + what);
    }
  }

  /**
   * Main method to use this also for manual testing of new models and/or model
   * parser/reader features
   * 
   * @param args
   */
  public static void main(String[] args) {
    MLSpaceSmallParser parser;
    try {
      parser = new MLSpaceSmallParser(
          "../../src/main/" + "resources/ActinModelBMCupdated.mls", null,
          false);
      MLSpaceModel model = parser.fullmodel().model;
      ApplicationLogger.log(Level.INFO,
          "Model " + model + " parsed with " + parser.getNumberOfSyntaxErrors()
              + " syntax errors. Info: " + model.getInfoMap());
    } catch (Exception e) {
      ApplicationLogger.log(e);
    }
  }
}
