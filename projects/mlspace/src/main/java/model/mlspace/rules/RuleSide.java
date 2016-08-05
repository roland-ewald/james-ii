/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import model.mlspace.entities.RuleEntity;

/**
 * Container for everything on one side of a rule
 * 
 * @author Arne Bittig
 * @date 20.11.2012
 */
public class RuleSide {

  private final RuleEntity context;

  private final List<RuleEntity> entities;

  /**
   * Constructor to be used only by {@link Builder}.
   * 
   * @param context
   * @param entities
   */
  RuleSide(RuleEntity context, List<RuleEntity> entities) {
    this.context = context;
    this.entities = entities;
  }

  /**
   * @return Context in which reaction should happen (null if any)
   */
  public final RuleEntity getContext() {
    return context;
  }

  /**
   * @return Entities triggering the reaction
   */
  public final List<RuleEntity> getReactants() {
    return entities;
  }

  /**
   * @return All variable names
   */
  private Set<String> getAllVarNames() {
    LinkedHashSet<String> allVarNames = new LinkedHashSet<>();
    if (context != null) {
      allVarNames.addAll(context.getVarNames());
    }
    for (RuleEntity ent : entities) {
      allVarNames.addAll(ent.getVarNames());
    }
    return allVarNames;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    if (context != null) {
      stringBuilder.append(context);
      stringBuilder.append('[');
    }
    for (RuleEntity ent : entities) {
      stringBuilder.append(ent);
      stringBuilder.append(" + ");
    }
    if (!entities.isEmpty()) {
      stringBuilder.delete(stringBuilder.length() - 3, stringBuilder.length());
    }
    if (context != null) {
      stringBuilder.append(']');
    }
    Set<String> allVarNames = getAllVarNames();
    if (!allVarNames.isEmpty()) {
      stringBuilder.append(" /*vars: ");
      stringBuilder.append(allVarNames);
      stringBuilder.append("*/\n");
    }

    return stringBuilder.toString();
  }

  /**
   * Helper class for building rule left hand sides & right hand sides
   * 
   * @author Arne Bittig
   * @date 20.11.2012
   */
  public static final class Builder {

    private RuleEntity context = null;

    private final List<RuleEntity> entities = new ArrayList<>();

    private final Set<String> alreadyDefinedVariableNames =
        new LinkedHashSet<>();

    /**
     * Assert that exactly one entity has been added so far, and make it the
     * context entity (incl. removing it from list of reacting entities)
     * 
     * @return this object
     */
    public Builder makeLastContext() {
      if (context != null) {
        throw new IllegalStateException("Context already set");
      }
      if (entities.size() != 1) {
        throw new IllegalStateException(entities.size()
            + " entities already set");
      }
      this.context = entities.remove(0);
      return this;
    }

    private static void assertDisjoint(Collection<String> before,
        Collection<String> toAdd) {
      if (!Collections.disjoint(before, toAdd)) {
        throw new IllegalStateException("Some variables of " + toAdd
            + "already defined in " + before);
      }
    }

    /**
     * @param ent
     *          Entity to add
     * @return this Builder object for chaining
     */
    public Builder addEntity(RuleEntity ent) {
      entities.add(ent);
      Collection<String> varNames = ent.getVarNames();
      if (varNames.isEmpty()) {
        return this;
      }
      assertDisjoint(alreadyDefinedVariableNames, varNames);
      alreadyDefinedVariableNames.addAll(varNames);
      return this;
    }

    /**
     * @return true iff context is already set
     */
    public boolean isContextSet() {
      return context != null;
    }

    /**
     * Get rule left hand side build by this Builder
     * 
     * @return rule left hand side
     */
    public RuleSide build() {
      return new RuleSide(context, entities);
    }

  }
}
