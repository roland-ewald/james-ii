/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Level;

import org.jamesii.core.math.simpletree.UndefinedVariableException;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * @author Arne Bittig
 * @date 08.06.2012
 */
public class RuleCollection {

  private final Collection<NSMReactionRule> nsmRules;

  private final Collection<TimedReactionRule> timedRules;

  private final Collection<CollisionReactionRule> collRules;

  private final Collection<TransferInRule> transInRules;

  private final Collection<TransferOutRule> transOutRules;

  /** Empty rule collection to which new rules can be added */
  public RuleCollection() {
    this.timedRules = new LinkedList<>();
    this.collRules = new LinkedList<>();
    this.transInRules = new LinkedList<>();
    this.transOutRules = new LinkedList<>();
    this.nsmRules = new LinkedList<>();
  }

  /**
   * Add rule to collection
   * 
   * @param rule
   *          Rule to add (type determined here)
   * @return true if collection changed
   * @throws IllegalArgumentException
   *           if rule is not of the known (4) {@link MLSpaceRule} subclasses
   * @throws RuntimeException
   *           if this collection has not been initialized with the no-args
   *           c'tor and a sub-collection is not modifiable
   */
  public boolean add(MLSpaceRule rule) {
    try {
      if (rule.getRate(Collections.<String, Number> emptyMap()) == 0) {
        ApplicationLogger.log(Level.WARNING, "Not adding 0-rate rule " + rule);
        return false;
      }
    } catch (UndefinedVariableException e) {
      /* no test for always-0 rate possible; do nothing */
    }
    if (rule instanceof TimedReactionRule) {
      return timedRules.add((TimedReactionRule) rule);
    }
    if (rule instanceof CollisionReactionRule) {
      return collRules.add((CollisionReactionRule) rule);
    }
    if (rule instanceof TransferInRule) {
      return transInRules.add((TransferInRule) rule);
    }
    if (rule instanceof TransferOutRule) {
      return transOutRules.add((TransferOutRule) rule);
    }
    if (rule instanceof NSMReactionRule) {
      return nsmRules.add((NSMReactionRule) rule);
    }
    throw new IllegalArgumentException("Unknown rule class " + rule.getClass());

  }

  /**
   * @return Time-triggered reaction rules
   */
  public final Collection<TimedReactionRule> getTimedRules() {
    return timedRules;
  }

  /**
   * @return Collision-triggered rules
   */
  public final Collection<CollisionReactionRule> getCollRules() {
    return collRules;
  }

  /**
   * @return Transfer rules
   */
  public final Collection<TransferInRule> getTransInRules() {
    return transInRules;
  }

  /**
   * @return Transfer rules
   */
  public final Collection<TransferOutRule> getTransOutRules() {
    return transOutRules;
  }

  /**
   * @return NSM reaction rules
   */
  public final Collection<NSMReactionRule> getNsmRules() {
    return nsmRules;
  }

  /**
   * @return Number of rules (total)
   */
  public int size() {
    return nsmRules.size() + timedRules.size() + collRules.size()
        + transInRules.size() + transOutRules.size();
  }

  @Override
  public String toString() {
    return size() + " rules: timed: " + timedRules + ", collision-triggered: "
        + collRules + ", transfer in: " + transInRules + ", transfer out: "
        + transOutRules + ", NSM/reaction-diffusion: " + nsmRules;
  }
}