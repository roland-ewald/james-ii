/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.binding;

import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;

/**
 * Dummy rule entity that matches every model entity, e.g. to be used in rules
 * where binding sites are specified to be occupied, but no restrictions are
 * made about the occupying entity
 * 
 * @author Arne Bittig
 * @date 23.07.2012
 */
public class AllMatchingRuleEntity extends RuleEntityWithBindings {

  private static final long serialVersionUID = 2550662490160288570L;

  private final String string;

  /**
   * Only constructor
   * 
   * @param inRule
   *          Flag whether created entity is used in rule (true) or observation
   *          matching (false) -- the only difference this makes is for the
   *          string representation
   */
  public AllMatchingRuleEntity(boolean inRule) {
    super();
    this.string = inRule ? "OCCUPIED" : "*";
  }

  @Override
  public boolean matches(AbstractModelEntity mEnt, Map<String, Object> env) {
    return mEnt != null;
  }

  @Override
  public String toString() {
    return string;
  }

  @Override
  public int hashCode() {
    return -1;
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj.getClass() == this.getClass();
  }
}
