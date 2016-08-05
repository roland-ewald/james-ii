/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * 
 */
package model.mlspace.rules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.InitEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.rules.attributemodification.IAttributeModification;

/**
 * @author Arne Bittig
 * @date 06.03.2014
 * 
 */
public abstract class NonTransferRule extends MLSpaceRule {

  private static final long serialVersionUID = 6801982076080346453L;

  private final RuleSide lhs;

  /** entities that appear only on the right hand side of the rule */
  private final List<InitEntity> produced;

  private final int order;

  private final RuleEntity context;

  private final List<IAttributeModification> contextMod;

  private final List<List<IAttributeModification>> entityMods;

  protected NonTransferRule(String name, RuleSide lhs,
      List<IAttributeModification> contextMods,
      List<List<IAttributeModification>> allOtherMods,
      List<InitEntity> products, DoubleNode rate) {
    super(name, rate);
    this.produced =
        products != null ? products : Collections.<InitEntity> emptyList();
    this.lhs = lhs;
    this.entityMods = allOtherMods;
    this.context = lhs.getContext();
    this.contextMod = contextMods != null ? contextMods
        : Collections.<IAttributeModification> emptyList();
    List<RuleEntity> reactants = lhs.getReactants();
    this.order = reactants.size();
  }

  /**
   * Zeroth-, first-, second-, higher-order reaction?
   * 
   * @return Number of reactants on l.h.s. of rule
   */
  public final int getOrder() {
    return order;
  }

  public final RuleSide getLeftHandSide() {
    return lhs;
  }

  /**
   * @return the context
   */
  public final RuleEntity getContext() {
    return context;
  }

  /**
   * @return Modifications to the context, empty list if none
   */
  public final List<IAttributeModification> getContextMod() {
    return contextMod;
  }

  /**
   * Get modifications to be applied to each entity on the left hand side of the
   * rule
   * 
   * @param index
   *          Entity index (as in list returned by
   *          {@link RuleSide#getReactants()}
   * @return Modifications (possibly consumption marker)
   */
  public final List<IAttributeModification> getEntityMod(int index) {
    return entityMods.get(index);
  }

  /**
   * Test if this rule can only be applied in a certain context and if the given
   * context matches; extract variables if necessary.
   * 
   * Note: The actual match methods for the different rules are not part of this
   * superclass as they have different signatures (e.g. different number of
   * parameters depending on the number of entities they (can) apply to).
   * 
   * @param actualContext
   * @return null if no match, Map of extracted variable values (possibly empty;
   *         modifiable) if successful
   */
  public final Map<String, Object> matchContext(
      AbstractModelEntity actualContext) {
    LinkedHashMap<String, Object> rv = new LinkedHashMap<>();
    if (context == null || context.matches(actualContext, rv)) {
      return rv;
    }
    return null;
  }

  /**
   * @return produced entities (production pattern)
   */
  public final List<InitEntity> getProduced() {
    return produced;
  }

  @Override
  public String toString() {

    StringBuilder str = new StringBuilder();
    StringBuilder rhs = new StringBuilder();

    Map.Entry<String, String> contextStrings =
        entBeforeAfterToString(context, contextMod);
    if (!contextStrings.getKey().isEmpty()) {
      str.append(contextStrings.getKey());
      str.append('[');
      rhs.append(contextStrings.getValue());
      rhs.append('[');
    }
    int rIdx = 0;
    for (RuleEntity ent : lhs.getReactants()) {
      Map.Entry<String, String> beforeAfterString =
          entBeforeAfterToString(ent, entityMods.get(rIdx));
      str.append(beforeAfterString.getKey());
      str.append(ENT_SEP);
      if (!beforeAfterString.getValue().isEmpty()) {
        rhs.append(beforeAfterString.getValue());
        rhs.append(ENT_SEP);
      }
      rIdx++;
    }
    for (InitEntity ent : getProduced()) {
      rhs.append(ent.toString() + ENT_SEP);
    }

    if (str.length() > 0 && str.charAt(str.length() - 1) != '[') {
      str.delete(str.length() - ENT_SEP.length(), str.length());
    }
    if (rhs.length() > 0 && rhs.charAt(rhs.length() - 1) != '[') {
      rhs.delete(rhs.length() - ENT_SEP.length(), rhs.length());
    }

    if (!contextStrings.getKey().isEmpty()) {
      str.append(']');
      rhs.append(']');
    }

    str.append(REACTS_TO);
    str.append(rhs);
    str.append(RATE_SEP);
    str.append(rateToString());
    return str.toString();
  }
}
