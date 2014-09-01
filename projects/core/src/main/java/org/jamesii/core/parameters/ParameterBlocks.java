/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.parameters;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.core.cmdparameters.InvalidParameterException;

/**
 * Static utility methods for dealing with {@link ParameterBlock}s
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * @author Arne Bittig
 * @date 11.09.2012
 * 
 * @see ParameterBlock
 */
public final class ParameterBlocks {

  private ParameterBlocks() {
    /* non-instantiable utility class */
  }

  /**
   * This method returns a sub parameter block of the passed parameter block if
   * this block is not null and if there is a sub parameter block for the passed
   * identifier. This method is completely safe, i.e. neither a null as block
   * parameter nor a not existing or an existing sub parameter of a wrong type
   * will cause a failure. In all these cases null will be returned!
   * 
   * @param block
   *          The parameter block the sub parameter shall be extracted from.
   * @param ident
   *          The identifier of the sub parameter.
   * 
   * @return null if the passed block was null or the sub parameter does not
   *         exist or the sub parameter is of a wrong type, otherwise the sub
   *         parameter is returned
   */
  public static ParameterBlock getSubBlock(ParameterBlock block, String ident) {
    if (block == null) {
      return null;
    }
    return block.getSubBlock(ident);
  }

  /**
   * Returns sub-block of a given block, or an empty new parameter block
   * instance if that is not possible.
   * 
   * @param block
   *          the parent parameter block
   * @param ident
   *          the name of the sub-block
   * 
   * @return either the named sub-block or an empty parameter block
   */
  public static ParameterBlock getSBOrEmpty(ParameterBlock block, String ident) {
    return getSBOrDefault(block, ident, null);
  }

  /**
   * Returns sub-block of a given block, or an new parameter block containing a
   * default value if that is not possible.
   * 
   * @param block
   *          the parent parameter block
   * @param ident
   *          the name of the sub-block
   * @param defaultValue
   *          the default value
   * 
   * @return either the named sub-block or an empty parameter block
   */
  public static ParameterBlock getSBOrDefault(ParameterBlock block,
      String ident, Object defaultValue) {
    if (block == null || !block.hasSubBlock(ident)) {
      return new ParameterBlock(defaultValue);
    }
    return block.getSubBlock(ident);
  }

  /**
   * Get the value of a sub block. Return null if the passed block is null or if
   * there is no sub block for given identifier.
   * 
   * @param <V>
   *          the type of the value to be returned
   * 
   * @param block
   *          the sub block
   * @param ident
   *          the identifier
   * 
   * @return the value of the sub block
   */
  public static <V> V getSubBlockValue(ParameterBlock block, String ident) {
    if (block == null) {
      return null;
    }
    ParameterBlock result = block.getSubBlock(ident);
    if (result == null) {
      return null;
    }
    return result.getValue();
  }

  /**
   * Get the value of a sub block. Return default value if the passed block is
   * null, if there is no sub block for given identifier, or if the respective
   * value itself is null.
   * 
   * @param <V>
   *          the type of the value to be returned
   * 
   * @param block
   *          sub-block
   * @param ident
   *          identifier
   * @param defaultValue
   *          value to return if identifier is not in block
   * 
   * @return the value of the sub block
   */
  public static <V> V getSubBlockValueOrDefault(ParameterBlock block,
      String ident, V defaultValue) {
    if (block == null) {
      return defaultValue;
    }
    ParameterBlock result = block.getSubBlock(ident);
    if (result == null) {
      return defaultValue;
    }
    V value = result.getValue();
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Return the value of the passed parameter block. May return null if the
   * block is empty.
   * 
   * @param <V>
   *          the type of the value to be returned
   * 
   * @param block
   *          the block
   * 
   * @return the value
   */
  public static <V> V getValue(ParameterBlock block) {
    if (block == null) {
      return null;
    }
    return block.getValue();
  }

  /**
   * Checks whether the given block has a sub block with given identifier.
   * 
   * @param block
   *          the sub block
   * @param ident
   *          the identifier
   * 
   * @return true if block is not null and if there is a sub block with given
   *         identifier
   */
  public static boolean hasSubBlock(ParameterBlock block, String ident) {
    return ((block != null) && (block.hasSubBlock(ident)));
  }

  /**
   * Internal method used by the {@link #fromObject(Object...)} method to create
   * a hierarchy of parameter blocks based on the array passed.
   * 
   * @param block
   * @param values
   * @return
   */
  private static ParameterBlock fromObject(ParameterBlock block,
      Object... values) {
    if (values.length % 2 == 0) {
      throw new InvalidParameterException(
          "An odd number of parameters is required!");
    }
    block.setValue(values[0]);
    for (int i = 1; i < values.length; i += 2) {
      ParameterBlock subBlock =
          block.addSubBlock(values[i] + "", values[i + 1]);
      // will replace the value just set in case that we are creating a
      // hierarchy
      if (values[i + 1] instanceof Object[]) {
        fromObject(subBlock, values[i + 1]);
      }
    }
    return block;
  }

  /**
   * Create a parameter block based on the content of the array passed. Assumes
   * that the array passed contains in the first position the value of the block
   * and in all subsequent pairs ident and value of the sub blocks. Thereby it
   * is allowed that the values are arrays of objects as well which means that
   * they will be interpreted as sub blocks to be parsed. I.e., it is not
   * possible to set an array of objects as value using this method.
   * <p>
   * Example:<br/>
   * <code>
   * new Object[] { "valueOfMainBlock", "identOfFirstSubBlock",
   * "valueOfFirstSubBlock", "identOfSecondSubBlock", new Object[] {
   * "valueOfSecondSubBlock", "identOfFirstSubBlockInSecondSubBlock", "1"} }
   * </code>
   * </p>
   * 
   * @param values
   * @return Parameter block made up of array values
   */
  public static ParameterBlock fromObject(Object... values) {
    ParameterBlock result = new ParameterBlock(values[0]);
    fromObject(result, values);
    return result;
  }

  /**
   * Recursively copies a given parameter block.
   * 
   * @param pb
   *          the parameter block to be copied
   * 
   * @return a copy of the parameter block
   */
  public static ParameterBlock getCopy(ParameterBlock pb) {
    if (pb == null) {
      return null;
    }
    ParameterBlock copy = new ParameterBlock(pb.getValue());
    for (Entry<String, ParameterBlock> entry : pb.getSubBlocks().entrySet()) {
      copy.addSubBlock(entry.getKey(), getCopy(entry.getValue()));
    }
    return copy;
  }

  /**
   * Prints parameter block as a tree (useful for debugging purposes).
   * 
   * @param block
   *          the block to be printed
   * 
   * @return the string
   */
  public static String printParamTree(ParameterBlock block) {
    return printParamTree(0, block);
  }

  /**
   * Recursively constructs a tree-representation of the parameter block.
   * 
   * @param indentLevel
   *          level of recursion, reflected by amount of indentation
   * @param block
   *          current block
   * 
   * @return string describing current block
   */
  private static String printParamTree(int indentLevel, ParameterBlock block) {

    if (block == null) {
      return "";
    }

    int newIndent = indentLevel + 1;
    StringBuilder indentation = new StringBuilder();
    for (int i = 0; i < newIndent; i++) {
      indentation.append(" ");
    }

    StringBuilder result =
        new StringBuilder((block.getValue() == null ? "" : String.valueOf(block
            .getValue())));
    Map<String, ParameterBlock> subBlocks = block.getSubBlocks();

    for (Entry<String, ParameterBlock> subBlock : subBlocks.entrySet()) {
      result.append("\n" + indentation + subBlock.getKey() + ":"
          + printParamTree(newIndent, subBlock.getValue()));
    }
    if (!subBlocks.isEmpty()) {
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Merges two parameter blocks into a new copy. The values in the first block
   * have precedence over the values in the second block.
   * 
   * @param firstBlock
   *          first block to be merged
   * @param secondBlock
   *          second block to be merged
   * 
   * @return merged block
   */
  public static ParameterBlock merge(ParameterBlock firstBlock,
      ParameterBlock secondBlock) {

    ParameterBlock result =
        new ParameterBlock(
            firstBlock.getValue() != null ? firstBlock.getValue()
                : secondBlock.getValue());

    Set<String> subBlockNames = new HashSet<>();
    subBlockNames.addAll(firstBlock.getSubBlocks().keySet());
    subBlockNames.addAll(secondBlock.getSubBlocks().keySet());
    for (String subBlockName : subBlockNames) {
      result.addSubBlock(subBlockName,
          merge(subBlockName, firstBlock, secondBlock));
    }
    return result;
  }

  /**
   * Merges two parameter blocks into a new copy on a top level, but avoids
   * merging parameter blocks from different hierarchies. The values in the
   * first block have precedence over the values in the second block. For
   * example, if both parameter blocks have a sub-block 'a', the whole hierarchy
   * of the first parameter block is used, while the second blocks' hierarchy of
   * parameters below 'a' will be dismissed entirely. Hence, this method
   * basically merges both parameter block on the first level, with precedence
   * for the first one.
   * 
   * @param firstBlock
   *          first block to be merged
   * @param secondBlock
   *          second block to be merged
   * 
   * @return merged block
   */
  public static ParameterBlock mergeOnTopLevel(ParameterBlock firstBlock,
      ParameterBlock secondBlock) {

    ParameterBlock result =
        new ParameterBlock(
            firstBlock.getValue() != null ? firstBlock.getValue()
                : secondBlock.getValue());

    for (String subBlockName : firstBlock.getSubBlocks().keySet()) {
      result.addSubBlock(subBlockName, firstBlock.getSubBlock(subBlockName)
          .getCopy());
    }

    for (String subBlockName : secondBlock.getSubBlocks().keySet()) {
      if (!result.hasSubBlock(subBlockName)) {
        result.addSubBlock(subBlockName, secondBlock.getSubBlock(subBlockName)
            .getCopy());
      }
    }

    return result;
  }

  /**
   * Merges the sub block with the given name from two parameter block which may
   * or may not have such a sub-block. The values in the first block have
   * precedence over the values in the second block.
   * 
   * @param subBlock
   *          the name of the sub-block
   * @param firstBlock
   *          the first parameter block
   * @param secondBlock
   *          the second parameter block
   * 
   * @return a merged parameter block
   */
  private static ParameterBlock merge(String subBlock,
      ParameterBlock firstBlock, ParameterBlock secondBlock) {

    boolean first = firstBlock.hasSubBlock(subBlock);
    boolean second = secondBlock.hasSubBlock(subBlock);

    // If both blocks have a sub block with this name, retrieve its value from
    // the first one and run merge recursively on all sub-block names
    if (first && second) {
      ParameterBlock firstSubBlock = firstBlock.getSubBlock(subBlock);
      ParameterBlock secondSubBlock = secondBlock.getSubBlock(subBlock);
      ParameterBlock toBeUsed = new ParameterBlock(firstSubBlock.getValue());
      Set<String> subBlockNames = new HashSet<>();
      subBlockNames.addAll(firstSubBlock.getSubBlocks().keySet());
      subBlockNames.addAll(secondSubBlock.getSubBlocks().keySet());
      for (String subBlockName : subBlockNames) {
        toBeUsed.addSubBlock(subBlockName,
            merge(subBlockName, firstSubBlock, secondSubBlock));
      }
      return toBeUsed;
    }

    // Otherwise, just get a copy of the sub block that has been defined
    ParameterBlock toBeUsed =
        first ? firstBlock.getSubBlock(subBlock) : secondBlock
            .getSubBlock(subBlock);
    return toBeUsed.getCopy();
  }

  /**
   * Searches recursively for sub block with given name. Search is implemented
   * as simple depth-first search.
   * 
   * @param searchBlock
   *          the block in which shall be searched
   * @param name
   *          the name of the sub-block that is searched
   * 
   * @return the first parameter block encountered with this name (depth-first
   *         search), or null if none was found
   */
  public static ParameterBlock searchSubBlock(ParameterBlock searchBlock,
      String name) {
    return searchSubBlock(searchBlock, name, null);
  }

  /**
   * Searches recursively for sub block with given name and value. Search is
   * implemented as simple depth-first search
   * 
   * @param searchBlock
   *          the block in which shall be searched
   * @param name
   *          the name of the sub-block that is search
   * @param value
   *          the value of the sub-block that is searched
   * 
   * @return the first parameter block encountered with this name (depth-first
   *         search), or null if none was found
   */
  public static ParameterBlock searchSubBlock(ParameterBlock searchBlock,
      String name, Object value) {
    if (searchBlock.hasSubBlock(name)
        && (value == null || searchBlock.getSubBlockValue(name).equals(value))) {
      return searchBlock.getSubBlock(name);
    }
    for (ParameterBlock sb : searchBlock.getSubBlocks().values()) {
      ParameterBlock result = searchSubBlock(sb, name, value);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * Copies given parameter block, or, if this is null, creates new one.
   * 
   * @param pBlock
   *          the parameter block to be copied if existent
   * 
   * @return the new or copied parameter block
   */
  public static ParameterBlock newOrCopy(ParameterBlock pBlock) {
    return pBlock != null ? pBlock.getCopy() : new ParameterBlock();
  }

  /**
   * Gets the list of values set for the parameter block and all its sub-blocks.
   * 
   * @param paramBlock
   *          the parameter block
   * @return the list of values
   */
  public static List<Object> getValueList(ParameterBlock paramBlock) {
    List<Object> result = new ArrayList<>();
    result.add(paramBlock.getValue());
    for (ParameterBlock subBlock : paramBlock.getSubBlocks().values()) {
      result.addAll(getValueList(subBlock));
    }
    return result;
  }

  /**
   * Alternative {@link String} representation of parameter block with one line
   * for each identifier and indented sub-blocks
   * 
   * @param pb
   *          Parameter block
   * @return String representation probably spanning multiple lines
   */
  public static String toMultilineString(ParameterBlock pb) {
    if (pb == null) {
      return "null";
    }
    return appendToMultilineString(pb, new StringBuilder(), "=", "--", 0)
        .toString();
  }

  private static StringBuilder appendToMultilineString(ParameterBlock pb,
      StringBuilder str, CharSequence valIntro, CharSequence indent, int level) {
    boolean firstNull = level == 0 && pb.getValue() == null;
    if (!firstNull) {
      str.append(valIntro);
      str.append(pb.getValue() == null ? "null" : pb.getValue().toString());
    }
    for (Map.Entry<String, ParameterBlock> e : pb.getSubBlocks().entrySet()) {
      if (firstNull) {
        firstNull = false;
      } else {
        str.append('\n');
      }
      for (int i = 0; i < level; i++) {
        str.append(indent);
      }
      str.append(e.getKey());
      appendToMultilineString(e.getValue(), str, valIntro, indent, level + 1);
    }
    return str;
  }
}
