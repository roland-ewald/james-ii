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
 * Move of A into B
 * 
 * @author Arne Bittig
 * @date 04.04.2014
 */
public class TransferInRule extends TransferRule {

  private static final long serialVersionUID = -8190330966998061273L;

  public TransferInRule(String name, RuleEntity transferredEntity,
      List<IAttributeModification> entMods, RuleEntity targetComp,
      List<IAttributeModification> targetMods, DoubleNode rate) {
    super(name, transferredEntity, entMods, targetComp, targetMods, rate);
  }

  /**
   * Match "A into B" transfer rule
   * 
   * @param entToMatch
   *          Entity A
   * @param targetCompToMatch
   *          Entity B
   * @return Match
   */
  public Match<SpatialEntity> match(SpatialEntity entToMatch,
      SpatialEntity targetCompToMatch) {
    return super.match(entToMatch, targetCompToMatch);
  }

  @Override
  public String toString() {
    Map.Entry<String, String> entStrings = getTransferredEntityStrings();
    Map.Entry<String, String> targetStrings = getSurroundingEntityStrings();
    return entStrings.getKey() + ENT_SEP + targetStrings.getKey() + REACTS_TO
        + targetStrings.getValue() + '[' + entStrings.getValue() + ']'
        + RATE_SEP + rateToString();
  }
}