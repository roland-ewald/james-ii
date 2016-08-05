/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;

import model.mlspace.entities.RuleEntity;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.SpecialAttributeModification;

/**
 * Base class for reaction and transfer rules
 * 
 * @author Arne Bittig
 */
public abstract class MLSpaceRule implements java.io.Serializable {

  private static final long serialVersionUID = 7504026019173357970L;

  private String name;

  /** the rate constant (subject to change to rate expression) */
  private final DoubleNode rate;

  /**
   * MLSpaceRule applicable in any context
   * 
   * @param name
   *          Name or default string representation
   * @param rate
   *          Reaction rate or propensity
   */
  protected MLSpaceRule(String name, DoubleNode rate) {
    this.name = name;
    this.rate = rate;
  }

  /**
   * @return Name of the rule, or string representation without rate if name
   *         undefined
   */
  public final String getName() {
    if (name == null) {
      name = this.toString();
      int idxRateStart = name.lastIndexOf('@');
      if (idxRateStart == -1) {
        idxRateStart = name.lastIndexOf('%');
      }
      name = name.substring(0, idxRateStart);
    }
    return name;
  }

  /**
   * @param env
   *          Variable values extracted from involved entities' attributes
   * @return Value of the reaction rate (or probability) expression
   */
  public final double getRate(Map<String, Number> env) {
    return rate.calculateValue(env);
  }

  protected final String rateToString() {
    return rate.toString();
  }

  protected static final String ENT_SEP = " + ";

  protected static final String REACTS_TO = " -> ";

  protected static final String RATE_SEP = " @ ";

  /**
   * Get String representation of RuleEntity to be matched and modified entity.
   * Returns empty strings if both are null (e.g. for non-existing context).
   * 
   * @param ent
   *          RuleEntity
   * @param mods
   *          Modifications
   * @return single map entry of before and after state (if consumed, empty
   *         string for the latter)
   */
  protected Map.Entry<String, String> entBeforeAfterToString(RuleEntity ent,
      List<IAttributeModification> mods) {
    if (ent == null) {
      assert mods == null || mods.isEmpty();
      return new AbstractMap.SimpleImmutableEntry<>("", "");
    }
    if (mods == null) {
      return new AbstractMap.SimpleImmutableEntry<>(ent.toString(),
          ent.getSpecies().toString() + "()");
    }
    if (mods.equals(SpecialAttributeModification.CONSUMED)) {
      return new AbstractMap.SimpleImmutableEntry<>(ent.toString(), "");
    }
    String modstr = mods.toString();
    return new AbstractMap.SimpleImmutableEntry<>(ent.toString(),
        ent.getSpecies().toString() + '('
            + modstr.substring(1, modstr.length() - 1) + ')');
  }

}