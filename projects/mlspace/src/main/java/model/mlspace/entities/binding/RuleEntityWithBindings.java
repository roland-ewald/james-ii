/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.binding;

import java.util.Collections;
import java.util.Map;

import org.jamesii.core.math.match.ValueMatch;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Pair;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.IEntity;
import model.mlspace.entities.RuleEntity;
import model.mlspace.entities.Species;

/**
 * Extension of entity definition for rule with additional definition of binding
 * site and their required state, ALSO INCLUDING the modifications to the
 * binding state as part of the rule in which this entity pattern occurs
 * 
 * @author Arne Bittig
 * @date 19.07.2012
 */
public class RuleEntityWithBindings extends RuleEntity
    implements IEntityWithBindings<Pair<? extends ValueMatch, String>> {

  private static final long serialVersionUID = 6862562958148415659L;

  private final Map<String, RuleEntityWithBindings> bindings;

  private final Map<String, BindingAction> bindingsMod;

  /**
   * @param spec
   * @param attributes
   * @param bs
   */
  public RuleEntityWithBindings(Species spec,
      Map<String, Pair<? extends ValueMatch, String>> attributes,
      Map<String, RuleEntityWithBindings> bs) {
    super(spec, attributes);
    this.bindings = bs != null ? bs : Collections.EMPTY_MAP;
    this.bindingsMod = Collections.EMPTY_MAP;
  }

  /**
   * Rule entity from left-hand side only & binding changes
   * 
   * @param before
   *          Left-hand-side rule entity
   * @param bindingsMod
   *          Modifications to binding sites
   */
  public RuleEntityWithBindings(RuleEntityWithBindings before,
      Map<String, BindingAction> bindingsMod) {
    super(before);
    this.bindings = before.bindings;
    assert before.bindingsMod.isEmpty();
    this.bindingsMod = bindingsMod;
  }

  /** dummy constructor for all-matching entity */
  protected RuleEntityWithBindings() {
    super(null, null, Collections.EMPTY_MAP);
    this.bindings = Collections.<String, RuleEntityWithBindings> emptyMap();
    this.bindingsMod = Collections.<String, BindingAction> emptyMap();
  }

  @Override
  public boolean hasBindingSite(String name) {
    return bindings.containsKey(name);
  }

  @Override
  public RuleEntityWithBindings getBoundEntity(String name) {
    if (!bindings.containsKey(name)) {
      throw new IllegalArgumentException(
          "No binding site " + name + " in " + this);
    }
    return bindings.get(name);
  }

  @Override
  public boolean hasBoundEntities() {
    return bindings.size() > 0;
  }

  @Override
  public Map<String, ? extends IEntityWithBindings<?>> bindingEntries() {
    return Collections.unmodifiableMap(bindings);
  }

  /**
   * Binding sites whose state is changed (true -> something binds, false ->
   * something is released)
   * 
   * @return Binding site name or empty map if none
   */
  public Map<String, BindingAction> getAffectedBindingSites() {
    return bindingsMod;
  }

  @Override
  public boolean matches(AbstractModelEntity mEnt, Map<String, Object> vars) {
    if (!super.matches(mEnt, vars)) {
      return false;
    }
    if (this.bindings == null || this.bindings.isEmpty()) {
      return true;
    }
    if (!(mEnt instanceof IEntityWithBindings<?>)) {
      return false;
    }
    return matchBindingSites(mEnt, vars);
  }

  private boolean matchBindingSites(AbstractModelEntity mEnt,
      Map<String, Object> vars) {
    try {
      IEntityWithBindings<?> mewbs = (IEntityWithBindings<?>) mEnt;
      for (Map.Entry<String, RuleEntityWithBindings> e : bindings.entrySet()) {
        String bsName = e.getKey();
        if (!mewbs.hasBindingSite(bsName)) {
          return false;
        }
        RuleEntity boundRuleEnt = e.getValue();
        IEntity<?> boundModelEnt = mewbs.getBoundEntity(bsName);
        if (boundRuleEnt == null) {
          if (boundModelEnt != null) {
            return false;
          }
        } else if (!boundRuleEnt.matches((AbstractModelEntity) boundModelEnt,
            vars)) {
          return false;
        }
      }
      return true;
    } catch (ClassCastException ex) {
      ApplicationLogger.log(ex);
      return false;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (bindings == null ? 0 : bindings.hashCode());
    result =
        prime * result + (bindingsMod == null ? 0 : bindingsMod.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RuleEntityWithBindings other = (RuleEntityWithBindings) obj;
    if (!super.equalsSameClass(other)) {
      return false;
    }
    return bindingsEquals(other);
  }

  private boolean bindingsEquals(RuleEntityWithBindings other) {
    if (bindings == null && other.bindings != null) {
      return false;
    }
    if (bindings != null && !bindings.equals(other.bindings)) {
      return false;
    }
    if (bindingsMod == null) {
      return other.bindingsMod == null;
    }
    return !bindingsMod.equals(other.bindingsMod);
  }

  @Override
  public String toString() {
    if (bindings.isEmpty() && bindingsMod.isEmpty()) {
      return super.toString();
    }
    StringBuilder str = new StringBuilder(super.toString());
    str.append('<');
    for (Map.Entry<String, RuleEntityWithBindings> e : bindings.entrySet()) {
      String siteName = e.getKey();
      str.append(siteName);
      str.append(':');
      str.append(e.getValue());
      if (bindingsMod.containsKey(siteName)) {
        str.append("->");
        str.append(bindingsMod.get(siteName));
      }
      str.append(',');
    }
    for (Map.Entry<String, BindingAction> e : bindingsMod.entrySet()) {
      String siteName = e.getKey();
      if (!bindings.containsKey(siteName)) {
        str.append(siteName);
        str.append(':');
        str.append(e.getValue());
        str.append(',');
      }
    }
    if (str.charAt(str.length() - 1) == ',') {
      str.deleteCharAt(str.length() - 1);
    }
    str.append(">");
    return str.toString();
  }

  /** Types of changes to a binding site within a rule */
  public static enum BindingAction {
    /** Bind something at free binding site */
    BIND(true, false), /** Release what is bound at occupied binding site */
    RELEASE(false, true), /**
                           * Release what is bound at occupied binding site,
                           * replace with something else
                           */
    REPLACE(true, true);

    private final boolean release;

    private final boolean bind;

    /**
     * @param bind
     * @param release
     */
    private BindingAction(boolean bind, boolean release) {
      this.release = release;
      this.bind = bind;
    }

    /**
     * @return true iff action involves binding an entity
     */
    public boolean binds() {
      return bind;
    }

    /**
     * @return true iff action involves releasing a bound entity
     */
    public boolean releases() {
      return release;
    }
  }

}
