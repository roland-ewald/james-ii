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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.Matches;
import model.mlspace.rules.match.SuccessfulMatch;

/**
 * Base class for Rules where an entity A is transferred across the (hard or
 * soft) boundary of an entity B. Whether A is outside B initially and inside
 * afterwards or the other way around is determined in each subclass.
 * 
 * @author Arne Bittig
 */
public abstract class TransferRule extends MLSpaceRule {

  private static final long serialVersionUID = -5217915705386122478L;

  private final RuleEntity transferredEntity;

  private final List<IAttributeModification> entMods;

  private final RuleEntity surroundingEnt;

  private final List<IAttributeModification> surroundingMods;

  protected TransferRule(String name, RuleEntity transferredEntity,
      List<IAttributeModification> entMods, RuleEntity surroundingComp,
      List<IAttributeModification> surroundingMods, DoubleNode rate) {
    super(name, rate);
    this.transferredEntity = transferredEntity;
    this.entMods = entMods;
    this.surroundingEnt = surroundingComp;
    this.surroundingMods = surroundingMods;
  }

  protected List<IAttributeModification> getEntMods() {
    return entMods;
  }

  /**
   * General match method applicable to both transfer-in and -out rules
   * 
   * @param entToMatch
   *          To-be-transferred entity
   * @param surroundingEntToMatch
   *          surrounding entity (before or after, depending on subclass)
   * @return {@link Match}
   */
  public <E extends AbstractModelEntity> Match<E> match(E entToMatch,
      E surroundingEntToMatch) {
    Map<String, Object> env = new LinkedHashMap<>();
    if (!transferredEntity.matches(entToMatch, env)) {
      return Matches.failure();
    }
    if (!surroundingEnt.matches(surroundingEntToMatch, env)) {
      return Matches.failure();
    }
    Map<E, List<IAttributeModification>> mods = new LinkedHashMap<>(2);
    if (!getEntMods().isEmpty()) {
      mods.put(entToMatch, getEntMods());
    }
    if (!surroundingMods.isEmpty()) {
      mods.put(surroundingEntToMatch, surroundingMods);
    }
    return new SuccessfulMatch<>(this, getRate((Map) env), env, mods);
  }

  protected Map.Entry<String, String> getTransferredEntityStrings() {
    return entBeforeAfterToString(transferredEntity, entMods);
  }

  protected Map.Entry<String, String> getSurroundingEntityStrings() {
    return entBeforeAfterToString(surroundingEnt, surroundingMods);
  }

}
