/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.binding;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.util.collection.UpdateableAmountMap;

import model.mlspace.entities.InitEntity;
import model.mlspace.entities.Species;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;
import model.mlspace.entities.values.AbstractValueRange;

/**
 * @author Arne Bittig
 * @date 21.08.2012
 */
public class InitEntityWithBindings extends InitEntity
    implements IEntityWithBindings<AbstractValueRange<?>> {

  private static final long serialVersionUID = 6764935817099352407L;

  private final Map<String, InitEntityWithBindings> bindings =
      new LinkedHashMap<>();

  private Map<String, BindingAction> bindingsMod;

  /**
   * @param list
   */
  public InitEntityWithBindings(List<InitEntity> list) {
    super(validateAndGetFirst(list), true);
    replaceBindingActionsWithPartners(list);
  }

  private InitEntityWithBindings(InitEntity ent) {
    super(ent, true);
  }

  /**
   * Temp constructor for parsed to-be-created entities with bindings (to be
   * collected in a list that is later processed by
   * {@link InitEntityWithBindings#InitEntityWithBindings(List)}.
   * 
   * @param species
   * @param atts
   * @param bindMods
   */
  public InitEntityWithBindings(Species species,
      Map<String, AbstractValueRange<?>> atts,
      Map<String, BindingAction> bindMods) {
    super(species, atts, UpdateableAmountMap.<InitEntity> emptyMap());
    this.bindingsMod = bindMods;
  }

  private static InitEntity validateAndGetFirst(List<InitEntity> list) {
    if (list.size() < 2) {
      throw new IllegalArgumentException("Intial entity with bindings"
          + " defined without binding partner: " + list);
    }
    for (InitEntity ent : list) {
      if (!(ent instanceof InitEntityWithBindings)) {
        throw new IllegalStateException("Intial enity with bindings"
            + " (complex definition) mixed with entity without "
            + "binding site: " + ent + " in " + list);
      }
      InitEntityWithBindings rewb = (InitEntityWithBindings) ent;
      Set<BindingAction> modSet =
          new LinkedHashSet<>(rewb.getAffectedBindingSites().values());
      if (modSet.size() != 1 || !modSet.contains(BindingAction.BIND)) {
        throw new IllegalArgumentException("Binding actions in inital"
            + " entity definition may only be to the bind " + "operation, not "
            + modSet + " as in " + rewb);
      }
    }
    return list.get(0);
  }

  /**
   * @param list
   */
  private void replaceBindingActionsWithPartners(List<InitEntity> list) {
    if (list.size() != 2) {
      throw new IllegalArgumentException("Can only deal with dimers, "
          + "not more complex binding in initialization (so far)");
    }
    String firstSite =
        checkAndGetBindingSite((InitEntityWithBindings) list.get(0));
    InitEntityWithBindings secondInitEnt = (InitEntityWithBindings) list.get(1);
    String secondSite = checkAndGetBindingSite(secondInitEnt);
    InitEntityWithBindings secondEnt =
        new InitEntityWithBindings(secondInitEnt);
    secondEnt.addBinding(secondSite, this);
    this.addBinding(firstSite, secondEnt);
  }

  private void addBinding(String site, InitEntityWithBindings ent) {
    bindings.put(site, ent);
  }

  private static String checkAndGetBindingSite(InitEntityWithBindings iewb) {
    Map<String, BindingAction> actions = iewb.getAffectedBindingSites();
    if (actions.size() != 1) {
      throw new IllegalArgumentException("Exactly one binding site must "
          + "be given for dimer initialization, not " + actions);
    }
    return actions.keySet().iterator().next();
  }

  @Override
  public boolean hasBindingSite(String name) {
    return bindings.containsKey(name);
  }

  @Override
  public InitEntityWithBindings getBoundEntity(String name) {
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
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    return prime * result // CHECK: better hashCode without recursion
        + (bindings == null ? 0 : bindings.keySet().hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    InitEntityWithBindings other = (InitEntityWithBindings) obj;
    if (bindings == null) {
      return other.bindings == null;
    } else {
      return bindings.equals(other.bindings);
    }
  }

}
