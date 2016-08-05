/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.List;
import java.util.Map;

import org.jamesii.core.math.simpletree.DoubleNode;

import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.match.Match;

/**
 * Rule for entities that collide with boundary of their surrounding and shall
 * end up outside of it
 * 
 * @author Arne Bittig
 */
public class TransferOutRule extends TransferRule {

  private static final long serialVersionUID = -7870620111774640014L;

  public TransferOutRule(String name, RuleEntity transferredEntity,
      List<IAttributeModification> entMods, RuleEntity sourceComp,
      List<IAttributeModification> sourceMods, DoubleNode rate) {
    super(name, transferredEntity, entMods, sourceComp, sourceMods, rate);
  }

  /**
   * Match "A out of B" transfer rule
   * 
   * @param entToMatch
   *          Entity A
   * @param sourceCompToMatch
   *          Entity B
   * @return Match
   */
  public Match<SpatialEntity> match(SpatialEntity entToMatch,
      SpatialEntity sourceCompToMatch) {
    return super.match(entToMatch, sourceCompToMatch);
  }

  @Override
  public String toString() {
    Map.Entry<String, String> entStrings = getTransferredEntityStrings();
    Map.Entry<String, String> sourceStrings = getSurroundingEntityStrings();
    return sourceStrings.getKey() + '[' + entStrings.getKey() + ']' + REACTS_TO
        + sourceStrings.getValue() + ENT_SEP + entStrings.getValue() + RATE_SEP
        + rateToString();
  }

}
