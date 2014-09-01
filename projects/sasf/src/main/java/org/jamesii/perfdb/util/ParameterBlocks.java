/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.collection.BidirectionalHashMap;
import org.jamesii.core.util.collection.IBidirectionalMap;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;

/**
 * Auxiliary methods to handle {@link ParameterBlock} instances.
 * 
 * @see ParameterBlock
 * 
 * @author Roland Ewald
 * 
 */
public final class ParameterBlocks {

  /** The error message in case of matching problems. */
  private static final String MATCHING_PROBLEM =
      "Parameter Block <=> RT Config matching encountered some problems.";

  /**
   * Should not be instantiated.
   */
  private ParameterBlocks() {
  }

  /**
   * Tests for 'structural' equality of parameter blocks. It ignores differences
   * in factory instances (only classes have to be equal in that case), and
   * similar differences in {@link Collection} and {@link Pair} instances. This
   * is required to check the similarity of composite simulation stop time
   * policies by their parameter blocks.
   * 
   * @param p1
   *          the first parameter block
   * @param p2
   *          the second parameter block
   * 
   * @return true, if both blocks are equal following this definition
   */
  public static boolean structuralEqualityParamBlocks(ParameterBlock p1,
      ParameterBlock p2) {

    if (!equalValues(p1, p2)) {
      return false;
    }

    if (p1.getSubBlocks().size() != p2.getSubBlocks().size()) {
      return false;
    }

    for (Entry<String, ParameterBlock> subBlock : p1.getSubBlocks().entrySet()) {
      ParameterBlock compSB = p2.getSubBlock(subBlock.getKey());
      if (compSB == null
          || !structuralEqualityParamBlocks(compSB, subBlock.getValue())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Structural equality. Checks all kinds of distinguished classes/interfaces.
   * 
   * @param val1
   *          the first value
   * @param val2
   *          the second value
   * 
   * @return true, if structurally equal
   */
  private static boolean structuralEquality(Object val1, Object val2) {
    if (val1.equals(val2)) {
      return true;
    }
    if (val1 instanceof ParameterBlock && val2 instanceof ParameterBlock) {
      return structuralEqualityParamBlocks((ParameterBlock) val1,
          (ParameterBlock) val2);
    }
    if (val1 instanceof Factory && val1.getClass().equals(val2.getClass())) {
      return true;
    }
    if (val1 instanceof Collection<?> && val2 instanceof Collection) {
      return structuralEqualityCollections((Collection<?>) val1,
          (Collection<?>) val2);
    }
    if (val1 instanceof Pair<?, ?> && val2 instanceof Pair) {
      return structuralEquality(((Pair<?, ?>) val1).getFirstValue(),
          ((Pair<?, ?>) val2).getFirstValue())
          && structuralEquality(((Pair<?, ?>) val1).getSecondValue(),
              ((Pair<?, ?>) val2).getSecondValue());
    }
    return false;
  }

  /**
   * Collections equal.
   * 
   * @param val1
   *          the val1
   * @param val2
   *          the val2
   * 
   * @return true, if successful
   */
  private static boolean structuralEqualityCollections(Collection<?> val1,
      Collection<?> val2) {
    Object[] c1 = val1.toArray();
    Object[] c2 = val2.toArray();
    if (c1.length != c2.length) {
      return false;
    }
    for (int i = 0; i < c1.length; i++) {
      if (!structuralEquality(c1[i], c2[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns a simple string representation of a parameter block that is unique
   * in that two equal parameter blocks will have the same one. Use
   * {@link ParameterBlock#toString()} for just getting one possible string
   * representation, or this method if the string representation shall always be
   * the same. Internally, this is done by sorting the sub blocks by name before
   * recursively concatenating their content (two sub-blocks of equivalent name
   * or not possible).
   * 
   * @param block
   *          the block to be represented
   * 
   * @return the string representation
   */
  public static String toUniqueString(ParameterBlock block) {

    if (block == null) {
      return "null";
    }

    Map<String, ParameterBlock> subBlocks = block.getSubBlocks();
    StringBuilder result = new StringBuilder("(");
    List<String> sbNames = new ArrayList<>(subBlocks.keySet());
    Collections.sort(sbNames);

    for (String sbName : sbNames) {
      result.append("(" + sbName + " " + toUniqueString(subBlocks.get(sbName))
          + ")");
    }

    return result.append(valueToUniqueString(block.getValue()) + ")")
        .toString();
  }

  /**
   * Maps some value to a unique string representation.
   * 
   * @param value
   *          the value
   * @return the string representation
   */
  private static String valueToUniqueString(Object value) {
    String result = "";
    if (value instanceof ParameterBlock) {
      result = ParameterBlocks.toUniqueString((ParameterBlock) value);
    } else if (value instanceof Factory) {
      result = ((Factory<?>) value).getClass().getCanonicalName();
    } else if (value instanceof Collection) {

      List<String> representationList = new ArrayList<>();
      for (Object element : (Collection<?>) value) {
        representationList.add(valueToUniqueString(element));
      }
      Collections.sort(representationList);

      StringBuffer elemRepresentations = new StringBuffer("");
      for (String elementRepresentation : representationList) {
        elemRepresentations.append(elementRepresentation);
        elemRepresentations.append(',');
      }
      result += elemRepresentations;

      if (!result.isEmpty()) {
        result = "[" + result.substring(0, result.length() - 1) + "]";
      }
    } else if (value instanceof Pair) {
      result =
          "[" + valueToUniqueString(((Pair<?, ?>) value).getFirstValue()) + ","
              + valueToUniqueString(((Pair<?, ?>) value).getSecondValue())
              + "]";
    } else {
      result = value == null ? "null" : value.toString();
    }
    return result;
  }

  /**
   * Matches a list of parameter setups to a list of runtime configurations.
   * Default structure of setups is assumed (i.e., they should contain a
   * processor factory, setup of random number generation is ignored).
   * 
   * Uses
   * {@link ParameterBlocks#matchSubBlock(String, ParameterBlock, ParameterBlock, String...)}
   * for the matching.
   * 
   * @param paramBlockSetups
   *          the list of parameter block setups to be matched
   * @param runtimeConfigurations
   *          the list of runtime configurations to be matched
   * @return the map that represents the mapping
   */
  public static IBidirectionalMap<Integer, IRuntimeConfiguration> matchParamBlocksToRuntimeConfigs(
      List<ParameterBlock> paramBlockSetups,
      List<IRuntimeConfiguration> runtimeConfigurations) {

    IBidirectionalMap<Integer, IRuntimeConfiguration> configMapping =
        new BidirectionalHashMap<>();

    Set<Integer> unmatchedSetups = new HashSet<>();
    for (int i = 0; i < paramBlockSetups.size(); i++) {
      boolean foundMatch = false;
      for (IRuntimeConfiguration runtimeConfig : runtimeConfigurations) {
        if (ParameterBlocks.matchSubBlock(
            ProcessorFactory.class.getCanonicalName(), paramBlockSetups.get(i),
            runtimeConfig.getSelectionTree().toParamBlock(),
            RandomGeneratorFactory.class.getName())) {
          checkDuplicateMatches(configMapping, i, runtimeConfig);
          configMapping.put(i, runtimeConfig);
          foundMatch = true;
          break;
        }
      }
      if (!foundMatch) {
        unmatchedSetups.add(i);
      }
    }

    checkForUnmatchedSetups(runtimeConfigurations, configMapping,
        unmatchedSetups);
    return configMapping;
  }

  /**
   * Check duplicate matches.
   * 
   * @param configMapping
   *          the configuration mapping
   * @param matchedSetupIndex
   *          the index of the matched setup
   * @param matchedRuntimeConfig
   *          the matched runtime configuration
   */
  private static void checkDuplicateMatches(
      IBidirectionalMap<Integer, IRuntimeConfiguration> configMapping,
      int matchedSetupIndex, IRuntimeConfiguration matchedRuntimeConfig) {
    if (configMapping.getKey(matchedRuntimeConfig) != null) {
      SimSystem.report(
          Level.WARNING,
          MATCHING_PROBLEM,
          new IllegalArgumentException(
              "Duplicate match of setups detected: both setup "
                  + configMapping.get(Long
                      .valueOf(matchedRuntimeConfig.getID()).intValue())
                  + " and " + matchedSetupIndex
                  + " match with runtime configuration "
                  + matchedRuntimeConfig.getID() + " ["
                  + matchedRuntimeConfig.getSelectionTree().toParamBlock()
                  + "]"));
    }
  }

  /**
   * Checks for unmatched setups. This should lead to a graceful degradation: in
   * case not all parameter setups could be matched, we warn the user and assign
   * the left indices randomly.
   * 
   * @param runtimeConfigurations
   *          the runtime configurations
   * @param configurationMapping
   *          the mapping from setups to runtime configurations
   * @param unmatchedSetups
   *          the unmatched setups
   */
  private static void checkForUnmatchedSetups(
      List<IRuntimeConfiguration> runtimeConfigurations,
      Map<Integer, IRuntimeConfiguration> configurationMapping,
      Set<Integer> unmatchedSetups) {
    if (!unmatchedSetups.isEmpty()) {
      SimSystem.report(
          Level.WARNING,
          MATCHING_PROBLEM,
          new IllegalArgumentException("The setups with indices "
              + Strings.dispIterable(unmatchedSetups)
              + " could not be matched -- will used random order."));
      Set<IRuntimeConfiguration> values =
          new HashSet<>(configurationMapping.values());
      Iterator<Integer> unmatchedIndicesIt = unmatchedSetups.iterator();
      for (IRuntimeConfiguration runtimeConfig : runtimeConfigurations) {
        if (!values.contains(runtimeConfig)) {
          configurationMapping.put(unmatchedIndicesIt.next(), runtimeConfig);
        }
      }
    }
  }

  /**
   * Matches the parameter blocks OR their sub-blocks (with a given name).
   * 
   * @param subBlockName
   *          the sub block name
   * @param p1
   *          the first parameter block
   * @param p2
   *          the second parameter block
   * @param subBlocksToIgnore
   *          the sub-blocks to ignore
   * @return true, if successful
   */
  public static boolean matchSubBlock(String subBlockName, ParameterBlock p1,
      ParameterBlock p2, String... subBlocksToIgnore) {
    ParameterBlock firstMatchParamBlock =
        p1.hasSubBlock(subBlockName) ? p1.getSubBlock(subBlockName) : p1;
    ParameterBlock secondMatchParamBlock =
        p2.hasSubBlock(subBlockName) ? p2.getSubBlock(subBlockName) : p2;
    return matchParamBlocks(firstMatchParamBlock, secondMatchParamBlock,
        subBlocksToIgnore);
  }

  /**
   * Matches two parameter blocks while ignoring the given 'black list' of
   * irrelevant sub-blocks.
   * 
   * @param p1
   *          the first parameter block
   * @param p2
   *          the second parameter block
   * @param subBlocksToIgnore
   *          the sub-blocks to ignore
   * @return true, if matching is successful
   */
  public static boolean matchParamBlocks(ParameterBlock p1, ParameterBlock p2,
      String... subBlocksToIgnore) {

    if (!equalValues(p1, p2)) {
      return false;
    }

    Set<String> subBlocks = new HashSet<>();
    subBlocks.addAll(p1.getSubBlocks().keySet());
    subBlocks.addAll(p2.getSubBlocks().keySet());
    subBlocks.removeAll(Arrays.asList(subBlocksToIgnore));

    for (String subBlock : subBlocks) {
      if (!p1.hasSubBlock(subBlock) || !p2.hasSubBlock(subBlock)) {
        return false;
      }
      if (!matchParamBlocks(p1.getSubBlock(subBlock), p2.getSubBlock(subBlock),
          subBlocksToIgnore)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks whether to parameter blocks have (structurally) equal values.
   * 
   * @param p1
   *          the first parameter block
   * @param p2
   *          the second parameter block
   * @return true, if values are (structurally) equal
   */
  public static boolean equalValues(ParameterBlock p1, ParameterBlock p2) {

    Object p1Val = p1.getValue();
    Object p2Val = p2.getValue();

    if (p1Val == null && p2Val != null) {
      return false;
    }

    if (p1Val != null && !structuralEquality(p1Val, p2Val)) {
      return false;
    }
    return true;
  }

  /**
   * Checks for equality (via string representation, i.e. this is rather slow).
   * 
   * @param p1
   *          the first parameter block
   * @param p2
   *          the second parameter block
   * @return true, if equal
   */
  public static boolean equal(ParameterBlock p1, ParameterBlock p2) {
    return toUniqueString(p1).equals(toUniqueString(p2));
  }

}
