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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic2DVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic3DVectorFactory;
import org.jamesii.core.math.match.ValueMatch;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.simpletree.DoubleNode;
import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.hierarchy.LinkedHierarchy;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;

import model.mlspace.MLSpaceModel;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.ModelEntityFactory;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.binding.BindingSites;
import model.mlspace.entities.binding.InitEntityWithBindings;
import model.mlspace.entities.spatial.SpatialAttribute;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.entities.values.AbstractValueRange;
import model.mlspace.entities.values.VariableValue;
import model.mlspace.reader.ValueModifier.RangeValueModifier;
import model.mlspace.reader.ValueModifier.TreeValueModifier;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.RuleCollection;
import model.mlspace.rules.RuleSide;
import model.mlspace.rules.TransferInRule;

/**
 * Helper class between {@link model.mlspace.reader.MLSpaceModelReader} and the
 * (antlr auto-generated) parser, doing the actual work of creating the model
 * out of model component classes and definitions.
 * 
 * @author Arne Bittig
 */
public class MLSpaceParserHelper {

  /**
   * name of the setting used in the constants definition section to indicate
   * whether to use periodic boundaries in this model (read in the
   * antlr-generated parser)
   */
  public static final String PERIODIC_BOUNDARIES_SETTING = "periodicBoundaries";

  /**
   * name of the setting used in the constants definition section to indicate
   * whether to use experimental postponed init of regions (with direct transfer
   * rule application)
   */
  public static final Collection<String> INSTANT_TRANSFER_ON_INIT =
      Arrays.asList("postponedRegionInit".toLowerCase(),
          "instantTransferOnInit".toLowerCase());

  /** Model entity factory */
  private final ModelEntityFactory modEntFac;

  /**
   * Random number generator (to pass to {@link #modEntFac} and for shape
   * placement)
   */
  private final IRandom rand;

  private final boolean onTheFlySpeciesDef;

  /**
   * @param vecFac
   *          Vector factory
   * @param rand
   *          Random number generator
   * @param onTheFlySpeciesDef
   *          Flag whether to allow entities of species without separate species
   *          definitions (for unit testing only!)
   */
  private MLSpaceParserHelper(IVectorFactory vecFac, IRandom rand,
      boolean onTheFlySpeciesDef) {
    this.rand = rand;
    this.modEntFac = new ModelEntityFactory(rand, vecFac);
    this.onTheFlySpeciesDef = onTheFlySpeciesDef;
  }

  /**
   * Default constructor, lazy vector factory initialization, Java random number
   * generator
   * 
   * @param onTheFlySpeciesDef
   *          Flag for strict checking of species definition (it true) or
   *          accepting undefined species (if false; for testing of model parts
   *          parsing)
   */
  public MLSpaceParserHelper(boolean onTheFlySpeciesDef) {
    this(null, SimSystem.getRNGGenerator().getNextRNG(), onTheFlySpeciesDef);
  }

  /**
   * Set periodic boundary conditions
   * 
   * @param val
   *          4 or 6 values for boundaries
   * @return true if successful, false if non-periodic boundaries (dummy value
   *         passed), null if unsuccessful
   */
  public Boolean setPeriodicBoundaries(AbstractValueRange<?> val) {
    Object[] array = null;
    if (val.size() == 4 || val.size() == 6) { // NOSONAR: 4 bounds needed for 2D
                                              // periodic b., 6 for 3D
      array = val.toList().toArray();
    } else {
      if (val.size() == 1) {
        Object actualVal = val.iterator().next();
        if (actualVal.equals(0.)) {
          return false;
        } else if (actualVal.equals(1.)) {
          modEntFac.setPeriodicBoundaries();
          return true;
        } else if (actualVal instanceof double[]) {
          double[] actArray = (double[]) actualVal;
          array = new Number[actArray.length];
          for (int i = 0; i < actArray.length; i++) {
            array[i] = actArray[i];
          }
        }
      }
    }
    if (array == null) {
      ApplicationLogger.log(Level.SEVERE, "No vector factory known for periodic"
          + " boundaries and dimension " + val.size() / 2);
      return null; // NOSONAR: capital-B-Boolean used for ternary logic
    }
    modEntFac.setVectorFactory(createPeriodicVecFac(array));
    return true;
  }

  public static IVectorFactory createPeriodicVecFac(Object[] array) {
    if (array.length == 4) {
      return new Periodic2DVectorFactory(((Number) array[0]).doubleValue(),
          ((Number) array[1]).doubleValue(), ((Number) array[2]).doubleValue(),
          ((Number) array[3]).doubleValue()); // NOSONAR: 4-el.
    } else if (array.length == 6) {
      return new Periodic3DVectorFactory(((Number) array[0]).doubleValue(),
          ((Number) array[1]).doubleValue(), ((Number) array[2]).doubleValue(),
          ((Number) array[3]).doubleValue(), ((Number) array[4]).doubleValue(),
          ((Number) array[5]).doubleValue()); // NOSONAR: 4-el.
    } else {
      throw new IllegalArgumentException(
          "Periodic boundaries must be defined with 4 (2d) or 6 (3d) values");
    }
  }

  public static <K, V1> Map<K, V1> multiMapsFirstHalf(
      final Map<K, ? extends Pair<? extends V1, ?>>... maps) {
    Map<K, V1> rv = new LinkedHashMap<>();
    for (Map<K, ? extends Pair<? extends V1, ?>> map : maps) {
      if (map == null) {
        continue;
      }
      for (Map.Entry<K, ? extends Pair<? extends V1, ?>> e : map.entrySet()) {
        V1 val = e.getValue().getFirstValue();
        if (val != null) {
          rv.put(e.getKey(), val);
        }
      }
    }
    return rv;
  }

  /**
   * Add species definition to (internal) {@link ModelEntityFactory}
   * 
   * @param name
   * @param attributes
   * @param bindingSites
   */
  public void registerSpeciesDef(String name,
      Map<String, ? extends AbstractValueRange<?>> attributes,
      BindingSites bindingSites) {

    Map<String, AbstractValueRange<?>> attRangeMap = new LinkedHashMap<>();
    if (attributes != null) {
      for (Map.Entry<String, ? extends AbstractValueRange<?>> e : attributes
          .entrySet()) {
        AbstractValueRange<?> val = e.getValue();
        if (val != null) {
          attRangeMap.put(e.getKey(), val);
        }
      }
    }

    // TODO: attribute plausibility check (here or in modEntFac)?
    // e.g. diffusion & size >=0

    modEntFac.registerSpeciesDefinition(name, attRangeMap, bindingSites);
  }

  /**
   * @param specName
   * @return
   * @see model.mlspace.entities.ModelEntityFactory#getSpeciesForName(java.lang.String)
   */
  public Species getSpeciesForName(String specName) {
    return modEntFac.getSpeciesForName(specName);
  }

  /**
   * @param specName
   *          Species name
   * @return true iff species has been declared previously or on-the-fly
   *         definitions are allowed (test flag)
   */
  public boolean isValidSpecies(String specName) {
    return onTheFlySpeciesDef || modEntFac.getSpeciesForName(specName) != null;
  }

  /**
   * Parse entity definition and match with species definition from (internal)
   * {@link ModelEntityFactory}
   * 
   * @param specName
   * @param allAtts
   * @return parsed entity as rule entity pattern
   */
  public boolean checkEntityDefPlausibility(String specName,
      Map<String, ?> allAtts) {
    Species spec = modEntFac.getSpeciesForName(specName);
    if (spec == null) {
      if (onTheFlySpeciesDef) {
        registerSpeciesDefOnTheFly(specName, allAtts);
        spec = modEntFac.getSpeciesForName(specName);
      } else {
        throw new IllegalStateException(
            "Entity " + specName + " is of a not previously defined species.");
      }
    }
    boolean allPlausible = true;
    if (allAtts != null) {
      correctRelPosShort(allAtts);
      for (Map.Entry<String, ?> att : allAtts.entrySet()) {
        Object value = att.getValue();
        if (value instanceof Pair<?, ?>) {
          Pair<? extends AbstractValueRange<?>, String> pair =
              (Pair<? extends AbstractValueRange<?>, String>) att.getValue();
          if (pair.getSecondValue() == null) {
            if (pair.getFirstValue() == null) {
              throw new IllegalStateException("conceptual error by programmer");
            } else {
              continue;
            }
          }
          value = pair.getFirstValue();
        }
        if (value == null) {
          continue;
        }
        if (value instanceof ValueModifier) {
          if (value instanceof RangeValueModifier) {
            allPlausible &= checkAttValPlausibility(specName, att.getKey(),
                ((RangeValueModifier) value).getRange());
          } else if (value instanceof TreeValueModifier) {
            try {
              double val = ((TreeValueModifier) value).getTree()
                  .calculateValue(Collections.EMPTY_MAP);
              allPlausible &= checkAttValPlausibility(specName, att.getKey(),
                  AbstractValueRange.newSingleValue(val));
            } catch (UndefinedVariableException e) {
              /* check not possible (run-time only) */
            }
          }
        } else if (value instanceof ValueMatch) {
          // TODO? check match range plausibility?
        } else {
          allPlausible &= checkAttValPlausibility(specName, att.getKey(),
              (AbstractValueRange<?>) value);
        }
      }
    }
    return allPlausible;
  }

  private void registerSpeciesDefOnTheFly(String specName,
      Map<String, ?> allAtts) {
    Map<String, ? extends AbstractValueRange<?>> atts = Collections.EMPTY_MAP;
    if (allAtts != null && !allAtts.isEmpty()) {
      Object next = allAtts.values().iterator().next();
      if (next instanceof Pair<?, ?>) {
        allAtts = multiMapsFirstHalf(
            (Map<String, ? extends Pair<? extends AbstractValueRange<?>, String>>) allAtts);
        next = allAtts.isEmpty() ? null : allAtts.values().iterator().next();
      }
      if (next instanceof AbstractValueRange<?>) {
        atts = (Map<String, ? extends AbstractValueRange<?>>) allAtts;
      }
      // else if (next instanceof ValueMatch) {
      // // for on-the-fly registration of species on a left hand rule side
      // Map<String, AbstractValueRange<?>> atts2 = new LinkedHashMap<>();
      // for (Map.Entry<String, ?> e : allAtts.entrySet()) {
      // Object value = e.getValue();
      // if (value instanceof ValueMatchRange) {
      // atts2.put(e.getKey(), ((ValueMatchRange) value).getRange());
      // } else {
      // atts2.put(e.getKey(), new AVRMatch((ValueMatch) value));
      // }
      // }}
      else if (next instanceof RangeValueModifier) {
        // for on-the-fly registration of species on a right hand rule side (or
        // an init block (?))
        Map<String, AbstractValueRange<?>> atts2 = new LinkedHashMap<>();
        for (Map.Entry<String, ?> e : allAtts.entrySet()) {
          Object value = e.getValue();
          atts2.put(e.getKey(), ((RangeValueModifier) value).getRange());
        }
        atts = atts2;
        // } else if (next != null) {
        // ApplicationLogger.log(Level.SEVERE, next + " may cause problems");
      }
    }
    registerSpeciesDef(specName, atts, null);
  }

  private static final String REL_POS_SHORT = "relpos";

  /**
   * Convenience method to allow {@link REL_POS_SHORT} to be used in place of
   * {@link ModelEntityFactory#REL_POSITION} -- replaces the former in the map
   * with the latter
   * 
   * @param allAtts
   *          Attribute map
   */
  private static <T> void correctRelPosShort(Map<String, T> allAtts) {
    T relPos = allAtts.remove(REL_POS_SHORT);
    if (relPos != null) {
      allAtts.put(ModelEntityFactory.REL_POSITION, relPos);
    }
  }

  /**
   * Helper method for validating binding site definitions
   * 
   * @param spec
   * @param siteNames
   * @return true if binding sites are consistent with respective species
   *         definition
   */
  public boolean validateBindingSites(Species spec,
      Collection<String> siteNames) {
    BindingSites bs = modEntFac.getBindingSites(spec);
    List<String> invalid = new ArrayList<>();
    if (bs == null) {
      invalid.addAll(siteNames);
    } else {
      for (String site : siteNames) {
        if (!bs.contains(site)) {
          invalid.add(site);
        }
      }
    }
    if (invalid.isEmpty()) {
      return true;
    }
    if (invalid.size() == 1) {
      ApplicationLogger.log(Level.SEVERE,
          spec + " has no binding site named " + invalid.get(0));
    } else {
      ApplicationLogger.log(Level.SEVERE,
          spec + " has no binding sites named " + invalid);
    }
    return false;
  }

  private static final Set<String> VECTOR_VALUED_ATTS =
      new LinkedHashSet<>(Arrays.asList(SpatialAttribute.POSITION.toString(),
          ModelEntityFactory.ASPECT_RATIO, ModelEntityFactory.REL_POSITION));

  /**
   * was: test whether attribute value is in range/set/interval given in species
   * definition. is: some remains of this test (for correct vector-valued
   * attributes, for example), but with deactivated messages for ranges etc, as
   * species definition ranges were found to be more practically interpreted as
   * default values, i.e. they do not need to cover all possible values later
   * assigned
   * 
   * @param specName
   * @param attName
   * @param attVal
   * @return true if attVal is in default value range
   * @throws IllegalStateException
   *           if attribute value is not of a required type (e.g. when given
   *           size is non-numeric)
   */
  boolean checkAttValPlausibility(String specName, String attName,
      AbstractValueRange<?> attVal) {
    if (attName == null) {
      return attVal == null; // for antlr validating semantic predicate in
      // absence of anything to validate
    }
    if (VECTOR_VALUED_ATTS.contains(attName)) {
      return checkVector(specName, attName, attVal);
    }

    if (attName.equals(SpatialAttribute.SIZE.toString())
        || attName.equals(SpatialAttribute.DIFFUSION.toString())) {
      if (!AbstractValueRange.isNumeric(attVal)) {
        throw new IllegalStateException(
            attVal + " is not numeric and thus not a valid value for " + attName
                + " of " + specName + ".");
      }
    }

    AbstractValueRange<?> valueRange = modEntFac.getAttRange(specName, attName);
    if (valueRange == null) {
      if (isDriftPart(specName, attName)) {
        return true;
      }
      ApplicationLogger.log(Level.SEVERE,
          "Species " + specName + " does not have an attribute " + attName
              + ", only " + modEntFac.getAttNames(specName));
      return false;
    }

    if (valueRange.containsAll(attVal)) {
      return true;
    }
    // // TODO: type check!
    // ApplicationLogger.log(Level.WARNING, specName + "' attribute '" + attName
    // + "'values " + attVal
    // + " not all contained in species definition values " + valueRange);
    return false;
  }

  private boolean isDriftPart(String specName, String attName) {
    SpatialAttribute spatialAttribute = SpatialAttribute.forName(attName);
    if (spatialAttribute == null) {
      return false;
    }
    if (spatialAttribute == SpatialAttribute.VELOCITY
        || spatialAttribute == SpatialAttribute.DIRECTION && modEntFac
            .getAttRange(specName, SpatialAttribute.DRIFT.toString()) != null) {
      return true;
    }
    if (spatialAttribute == SpatialAttribute.DRIFT && modEntFac
        .getAttRange(specName, SpatialAttribute.VELOCITY.toString()) != null) {
      return true;
    }
    return false;
  }

  private boolean checkVector(String specName, String attName,
      AbstractValueRange<?> value) {
    if (value.size() != 1) {
      ApplicationLogger.log(Level.SEVERE, "Not a valid vector " + value);
      return false;
    }
    Object singleValue = value.iterator().next();
    if (!(singleValue instanceof double[])) {
      ApplicationLogger.log(Level.SEVERE,
          invalidVectorMessage(specName, attName, value));
      return false;
    }
    int vLen = ((double[]) singleValue).length;
    IVectorFactory vecFac = modEntFac.getVectorFactory();
    if (vecFac == null) {
      modEntFac.setVectorFactory(new AVectorFactory(vLen));
    } else if (vecFac.getDimension() != vLen) {
      try { // may be the first vector, try to set dim
        vecFac.setDimension(vLen);
      } catch (Exception e) {
        ApplicationLogger.log(Level.SEVERE,
            invalidVectorMessage(specName, attName, value), e);
        return false; // number of dims does not match previous
        // vectors or vector factory default
      }
    }
    return true;
  }

  private String invalidVectorMessage(String specName, String attName,
      AbstractValueRange<?> value) {
    return specName + "'s " + attName + " attribute " + value
        + " is not a valid vector. (\"null\" values "
        + "may occur if the defined value relies on an "
        + "undefined variable (spelling mistake?))";
  }

  /**
   * Convert right-hand side rule entity to previous general rule entity format
   * (which is still used for describing entities that are produced in a rule,
   * although there are probably better ways)
   * 
   * @param ent
   *          ModEntity (used in init-with-bindings)
   * @return Rule entity corresponding to this one
   */
  public static InitEntity modToInitEntity(ModEntity ent) {
    Map<String, AbstractValueRange<?>> atts =
        new LinkedHashMap<>(ent.getAttributes().size());
    for (Map.Entry<String, ValueModifier> e : ent.getAttributes().entrySet()) {
      ValueModifier valmod = e.getValue();
      if (valmod instanceof ValueModifier.RangeValueModifier) {
        atts.put(e.getKey(),
            ((ValueModifier.RangeValueModifier) valmod).getRange());
      } else if (valmod instanceof ValueModifier.TreeValueModifier) {
        DoubleNode node = ((ValueModifier.TreeValueModifier) valmod).getTree();
        try {
          double value =
              node.calculateValue(Collections.<String, Number> emptyMap());
          atts.put(e.getKey(), AbstractValueRange.newSingleValue(value));
        } catch (UndefinedVariableException ex) {
          atts.put(e.getKey(), new VariableValue(node));
        }
      } else if (valmod instanceof ValueModifier.StringAssignmentModifier) {
        atts.put(e.getKey(), AbstractValueRange.newSingleValue(
            ((ValueModifier.StringAssignmentModifier) valmod).getValue()));
      } else {
        throw new IllegalStateException("Cannot deal with value " + valmod
            + " for to-be-created entity " + ent);
      }
    }
    if (ent.getBindMods().isEmpty())

    {
      return new InitEntity(ent.getSpecies(), atts);
    }
    return new InitEntityWithBindings(ent.getSpecies(), atts,
        ent.getBindMods());

  }

  public static List<InitEntity> modToInitEntities(List<ModEntity> ents) {
    List<InitEntity> rv = new ArrayList<>(ents.size());
    for (ModEntity ent : ents) {
      rv.add(modToInitEntity(ent));
    }
    return rv;
  }

  /**
   * method dispatching to specialized rule-parsing methods
   * 
   * @param name
   *          Rule name
   * @param left
   *          Left-hand side
   * @param contextAfter
   *          Right-hand side context
   * @param entsAfter
   *          Right-hand side entities
   * @param rate
   *          Rate expression
   * @param rateOrProb
   * @return rule
   */
  public MLSpaceRule parseRule(String name, RuleSide left,
      ModEntity contextAfter, List<ModEntity> entsAfter, DoubleNode rate,
      String rateOrProb) {
    return MLSpaceRuleCreator.parseRule(name, left, contextAfter,
        entsAfter == null ? Collections.<ModEntity> emptyList() : entsAfter,
        rate.simplify(), rateOrProb, modEntFac, rand);
  }

  /**
   * Create model from given init map and rules
   * 
   * @param init
   *          Initial state: entities (tree) and amounts
   * @param ruleCollection
   *          Rules
   * @param instantTransferIn
   *          Transfer rules to apply directly (a.k.a. postponed region init;
   *          empty collection for none)
   * @param variables
   *          Variables for incorporation in potential error message)
   * @return ML-Space model
   */
  public MLSpaceModel getModel(Map<InitEntity, Integer> init,
      RuleCollection ruleCollection, boolean instantTransferIn,
      Map<String, AbstractValueRange<?>> variables) {
    IHierarchy<SpatialEntity> compTree = new LinkedHierarchy<>();
    MLSpaceModelInitializer.NSMEntMap nsmEntMap;
    try {
      nsmEntMap =
          MLSpaceModelInitializer
              .fillCompTree(compTree, init,
                  instantTransferIn ? ruleCollection.getTransInRules()
                      : Collections.<TransferInRule> emptySet(),
                  modEntFac, rand);
    } catch (Exception ex) {
      ApplicationLogger.log(Level.SEVERE,
          "Error initializing model with these parameters:\n" + variables);
      throw ex;
    }
    MLSpaceModel model =
        new MLSpaceModel("", compTree, ruleCollection, nsmEntMap, modEntFac);
    return model;
  }

  /**
   * Get random value from range (implemented here because parser does not hold
   * ref to RNG)
   * 
   * @param vr
   *          value range
   * @return
   */
  public Double getRandomValue(AbstractValueRange<?> vr) {
    return (Double) vr.getRandomValue(rand); // ClassCastEx allowed to happen,
                                             // points to model error
  }

  /**
   * Construct object from with later to extract observation targets for
   * observing specific attributes
   * 
   * @param specName
   *          Species name
   * @param attributes
   *          attributes to observe, null for all (taken from model entity
   *          factory)
   * @return
   */
  public Collection<RuleEntity> getRuleEntsForAttributeObservations(
      String specName, Collection<String> attributes) {
    Species species = getSpeciesForName(specName);
    if (species == null) {
      ApplicationLogger.log(Level.SEVERE,
          "Unknown species " + specName
              + "; ignoring corresponding observation target (with attributes "
              + attributes + ")");
      return Collections.emptyList();
    }
    Collection<String> actualAtts = attributes;
    if (actualAtts == null) {
      actualAtts = modEntFac.getAttNames(specName);
    }

    Collection<RuleEntity> rv = new ArrayList<>(actualAtts.size());
    for (String att : actualAtts) {
      rv.add(new RuleEntity(species,
          Collections.<String, Pair<? extends ValueMatch, String>> singletonMap(
              att, null)));
    }
    return rv;
  }
}
