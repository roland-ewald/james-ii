/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.rules.CollisionReactionRule;
import model.mlspace.rules.match.Match;
import model.mlspace.rules.match.SuccessfulMatch;
import model.mlspace.rules.match.SuccessfulMatch.ModRecord;
import model.mlspace.rules.match.SuccessfulMatchWithBindings;
import model.mlspace.rules.match.SuccessfulMatchWithBindings.BindingMod;

/**
 * Collection of static methods rollback attribute modifications. See also
 * {@link Match#apply()} {@link ModRecord} (formerly part of this class in some
 * form).
 * 
 * @author Arne Bittig
 */
public final class AttModUtils {

  private AttModUtils() {
  }

  /**
   * Roll back modifications specified in a transfer rule to given entities.
   * Matching is not performed due to the different semantics and given order of
   * the entities.
   * 
   * @param modRecord
   *          Return value of
   *          {@link #applyRuleModifications(CollisionReactionRule, AbstractModelEntity, AbstractModelEntity, Map)}
   */
  public static <E extends AbstractModelEntity> void rollbackRuleModifications(
      SuccessfulMatch.ModRecord<E> modRecord) {
    Map<E, Map<String, Object>> prevVals = modRecord.getAttMods();
    rollbackNormalModifications(prevVals);
    if (modRecord instanceof SuccessfulMatchWithBindings.ModRecordWithBindings<?, ?>) {
      rollbackBindingActions(((SuccessfulMatchWithBindings.ModRecordWithBindings<E, Compartment>) modRecord)
          .getBindMods());
    }

  }

  private static <E extends AbstractModelEntity> void rollbackNormalModifications(
      Map<E, Map<String, Object>> prevVals) {
    Iterator<Map.Entry<E, Map<String, Object>>> prevValsIt =
        prevVals.entrySet().iterator();
    while (prevValsIt.hasNext()) {
      Map.Entry<E, Map<String, Object>> e = prevValsIt.next();
      E ent = e.getKey();
      Map<String, Object> mods = e.getValue();
      rollbackAttMods(ent, mods);
      prevValsIt.remove();
    }
  }

  /**
   * Roll back entity modifications
   * 
   * @param ent
   *          Entity to modify
   * @param map
   *          Attribute modifications
   * 
   * @see SuccessfulMatch#applyAttMods(AbstractModelEntity, List, Map)
   */
  private static void rollbackAttMods(AbstractModelEntity ent,
      Map<String, Object> map) {
    for (Map.Entry<String, Object> e : map.entrySet()) {
      ent.setAttribute(e.getKey(), e.getValue());
    }
  }

  private static void rollbackBindingActions(List<BindingMod<Compartment>> mods) {
    for (BindingMod<Compartment> mod : mods) {
      Compartment changedEnt = mod.getChangedEntity();
      Compartment prevEnt = mod.getPreviousEntity();
      if (prevEnt == null) {
        changedEnt.release(mod.getChangedSite());
      } else {
        changedEnt.bind(mod.getChangedSite(), prevEnt);
      }
    }
  }
}
