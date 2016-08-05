/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.List;

import org.jamesii.core.math.simpletree.DoubleNode;

import model.mlspace.entities.InitEntity;
import model.mlspace.rules.attributemodification.IAttributeModification;

/**
 * @author Arne Bittig
 * @date 08.06.2012, 2016-01-31 refactored for matching strategy interface
 */
public class NSMReactionRule extends NonTransferRule {

  private static final long serialVersionUID = -45859377296140042L;

  /**
   * Reaction rule for reaction-diffusion simulation applicable only in given
   * context (entity)
   * 
   * @param lhs
   *          Rule left-hand side
   * @param products
   *          Entities that appear only on the right hand side of the rule
   * @param contextMods
   *          Modifications of context
   * @param allOtherMods
   * @param rate
   *          Reaction rate
   * @rate Rate constant or probability expression
   */
  public NSMReactionRule(String name, RuleSide lhs,
      List<IAttributeModification> contextMods,
      List<List<IAttributeModification>> allOtherMods,
      List<InitEntity> products, DoubleNode rate) {
    super(name, lhs, contextMods, allOtherMods, products, rate);
  }

}