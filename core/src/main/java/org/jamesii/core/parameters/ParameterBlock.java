/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A parameter block. This is the central class for handling algorithm
 * parameters. It is nested with sub-block of the same type, each of which is
 * associated with a name (that has to be unique for the given parent block).
 * Think of it as a tree with edge labels, and the edges of a parent to its
 * child nodes all have to have distinct labels (names). The value of a
 * parameter block can be any {@link Object}, but for distributed setups and
 * storage it will be necessary to assign {@link Serializable} values only.
 * 
 * Several get... methods have a static analogue in {@link ParameterBlocks} that
 * takes an additional {@link ParameterBlock} argument and is null-safe, i.e.
 * returns something (usually null) if the block itself is null instead of
 * throwing an exception.
 * 
 * @author Jan Himmelspach
 * @author Roland Ewald
 * @see ParameterBlocks
 */
public class ParameterBlock implements Serializable, Comparable<ParameterBlock> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4101966687827103043L;

  /** The value of the current parameter block. */
  private Object value;

  /** References to sub-blocks. */
  private Map<String, ParameterBlock> subBlocks;

  /**
   * Create a new parameter block and use the passed values as parameters, the
   * passed value as value of the block.
   * 
   * @param val
   *          the value
   * @param vals
   *          the sub blocks
   */
  public ParameterBlock(Object val, Map<String, ParameterBlock> vals) {
    subBlocks = vals;
    value = val;
  }

  /**
   * Create a parameter block without sub-blocks.
   * 
   * @param val
   *          the value of the parameter block
   */
  public ParameterBlock(Object val) {
    this(val, new HashMap<String, ParameterBlock>());
  }

  /**
   * Creates parameter with empty value but another value added to a named
   * sub-block.
   * 
   * @param val
   *          the value
   * @param ident
   *          the identifier
   */
  public ParameterBlock(Object val, String ident) {
    this();
    addSubBl(ident, val);
  }

  /**
   * Create a new and empty parameter block.
   */
  public ParameterBlock() {
    this(null, new HashMap<String, ParameterBlock>());
  }

  /**
   * Add the passed ParameterBlock as sub block.
   * 
   * @param ident
   *          the identifier
   * @param block
   *          the sub-block
   */
  public final void addSubBlock(String ident, ParameterBlock block) {
    subBlocks.put(ident, block);
  }

  /**
   * Add new parameter block with given value and return it.
   * 
   * @param ident
   *          identifier of the new sub-block
   * @param o
   *          the value of the sub-block
   * 
   * @return reference to the newly created sub-block
   */
  public final ParameterBlock addSubBlock(String ident, Object o) {
    ParameterBlock block = new ParameterBlock(o);
    subBlocks.put(ident, block);
    return block;
  }

  /**
   * The same as {@link ParameterBlock#addSubBlock(String, Object)}, but returns
   * reference to original parameter block. This allows statements like
   * 
   * <code>(new ParameterBlock()).addSubBl(name1,val1).addSubBl(name2,val2);</code>
   * 
   * @param ident
   *          identifier of the new sub block
   * @param o
   *          value to be set
   * 
   * @return reference to object itself
   */
  public final ParameterBlock addSubBl(String ident, Object o) {
    addSubBlock(ident, o);
    return this;
  }

  /**
   * The same as {@link ParameterBlock#addSubBlock(String, ParameterBlock)}, but
   * returns reference to original parameter block. This allows concatenated
   * statements like
   * 
   * <code>(new ParameterBlock()).addSubBl(name1,block1).addSubBl(name2,block2);</code>
   * 
   * @param ident
   *          identifier of new sub-block
   * @param paramBlock
   *          the new sub-block
   * 
   * @return reference to parent parameter block
   */
  public final ParameterBlock addSubBl(String ident, ParameterBlock paramBlock) {
    addSubBlock(ident, paramBlock);
    return this;
  }

  /**
   * Get the sub block with the given identifier.
   * 
   * @param ident
   *          the identification
   * 
   * @return null if the sub block does not exist
   * @see ParameterBlocks#getSubBlock(ParameterBlock, String)
   * @see ParameterBlocks#getSBOrEmpty(ParameterBlock, String)
   * @see ParameterBlocks#getSBOrDefault(ParameterBlock, String, Object)
   */
  public final ParameterBlock getSubBlock(String ident) {
    return subBlocks.get(ident);
  }

  /**
   * Checks whether there is a value with the given identifier.
   * 
   * @param ident
   *          the identifier
   * 
   * @return true, if sub block with this ID exists
   * @see ParameterBlocks#hasSubBlock(ParameterBlock, String)
   */
  public final boolean hasSubBlock(String ident) {
    return subBlocks.containsKey(ident);
  }

  /**
   * Removes sub-block with given identifier.
   * 
   * @param ident
   *          the identifier
   * 
   * @return the removed parameter block, null if none was deleted
   */
  public final ParameterBlock removeSubBlock(String ident) {
    return subBlocks.remove(ident);
  }

  /**
   * Get the value of this parameter block's level.
   * 
   * @param <V>
   *          the type of the value to be returned
   * 
   * @return the value
   */
  @SuppressWarnings("unchecked")
  public final <V> V getValue() {
    return (V) value;
  }

  /**
   * Set the value of this parameter block (level).
   * 
   * @param val
   *          the val
   */
  public final void setValue(Object val) {
    value = val;
  }

  /**
   * Set value, return reference to this block (e.g. for parameter block
   * segments to be merged into another parameter block)
   * 
   * @param val
   *          new value
   * @return this block
   */
  public final ParameterBlock setVal(Object val) {
    value = val;
    return this;
  }

  /**
   * Get the map of sub blocks defined in this parameter block.
   * 
   * @return the sub blocks
   */
  public final Map<String, ParameterBlock> getSubBlocks() {
    return subBlocks;
  }

  /**
   * Get the value of a sub block (null if there is no sub block for given
   * identifier).
   * 
   * @param <V>
   *          the type of the value to be returned
   * 
   * @param ident
   *          the identifier
   * 
   * @return the value of the sub block
   * @see ParameterBlocks#getSubBlockValue(ParameterBlock, String)
   */
  public final <V> V getSubBlockValue(String ident) {
    ParameterBlock result = getSubBlock(ident);
    if (result == null) {
      return null;
    }
    return result.<V> getValue();
  }

  /**
   * Get the value of the identified sub block or the default value if not
   * existent.
   * 
   * @param <V>
   *          the type of the value to be returned
   * 
   * @param ident
   *          the name of the sub-block
   * @param defaultVal
   *          the default value, to be used if sub-block does not exist
   * 
   * @return either the value of the named sub-block, or the default value
   * @see ParameterBlocks#getSubBlockValueOrDefault(ParameterBlock, String,
   *      Object)
   */
  @SuppressWarnings("unchecked")
  public final <V> V getSubBlockValue(String ident, V defaultVal) {
    V val = null;
    if (hasSubBlock(ident)) {
      val = (V) getSubBlockValue(ident);
    }
    if (val == null) {
      val = defaultVal;
    }
    return val;
  }

  /**
   * Set the sub blocks.
   * 
   * @param subBlocks
   *          the sub blocks
   */
  public final void setSubBlocks(Map<String, ParameterBlock> subBlocks) {
    this.subBlocks = subBlocks;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (Map.Entry<String, ParameterBlock> e : subBlocks.entrySet()) {
      if (result.length() > 1) {
        result.append("; ");
      }
      result.append(e.getKey());
      result.append("( ");
      result.append(String.valueOf(e.getValue()));
      result.append(" )");
    }
    String help = "val = " + value + " sub content: ";
    if (result.length() == 0) {
      return help + "none";
    }
    return help + result;
  }

  @Override
  public int compareTo(ParameterBlock o) {

    // if o us null get out here as the blocks cannot be equal
    if (o == null) {
      return 1;
    }

    // if this value is null but the other's block not return -1
    if (getValue() == null && o.getValue() != null) {
      return -1;
    }

    // if this value is non null but the other's block is null return 1
    if (getValue() != null && o.getValue() == null) {
      return 1;
    }

    // if the value is a comparable compare to the other's block value
    // get out here if not identical
    if (getValue() instanceof Comparable) {
      int test = ((Comparable<Object>) getValue()).compareTo(o.getValue());
      if (test != 0) {
        return test;
      }
    } else {
      // we cannot compare as the objects are not comparable
      if (getValue() != null && !getValue().equals(o.getValue())) {
        // we use the hascode to achieve a consistent ordering in this case
        return getValue().hashCode() - o.getValue().hashCode() < 0 ? -1 : 1;
      }
    }

    // if the number of subblocks differs we cannot be equal
    if (subBlocks.size() != o.getSubBlocks().size()) {
      return subBlocks.size() - o.getSubBlocks().size() < 0 ? -1 : 1;
    }

    // build sorted list of the subblocks defined in here
    List<String> idents = new ArrayList<>();
    for (String ident : subBlocks.keySet()) {
      idents.add(ident);
    }
    Collections.sort(idents);

    // build sorted list of the subblocks defined in o
    List<String> identsO = new ArrayList<>();
    for (String ident : o.getSubBlocks().keySet()) {
      identsO.add(ident);
    }
    Collections.sort(identsO);

    // idents and identsO has the same number of elements
    for (int i = 0; i < idents.size(); i++) {

      int temp = idents.get(i).compareTo(identsO.get(i));
      if (temp != 0) {
        return temp;
      }

      ParameterBlock iThis = subBlocks.get(idents.get(i));
      ParameterBlock iO = o.getSubBlocks().get(idents.get(i));

      if (iThis == null) {
        if (iO != null) {
          return -1;
        } else // if (iThis == null && iO == null)
        {
          // we should never get here as this means that the parameter block has
          // been modified while we compare it
          return 0;
        }
      } else if (iO == null) {
        return 1;
      }

      temp = iThis.compareTo(iO);
      if (temp != 0) {
        return temp;
      }
    }

    return 0;
  }

  /**
   * Creates a shallow copy of a parameter block. The sub-blocks are newly
   * instanced as well, but none of the values are.
   * 
   * @return copy of parameter block
   * @see ParameterBlocks#getCopy(ParameterBlock)
   */
  public ParameterBlock getCopy() {
    return ParameterBlocks.getCopy(this);
  }

}
