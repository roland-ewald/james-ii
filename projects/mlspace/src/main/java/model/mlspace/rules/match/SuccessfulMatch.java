package model.mlspace.rules.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.attributemodification.IAttributeModification;
import model.mlspace.rules.attributemodification.SpecialAttributeModification;

import org.jamesii.core.util.collection.ArrayMap;

/**
 * Generic successful match for use by most rules
 * 
 * @author Arne Bittig
 * 
 * @param <E>
 *          Related entity type
 */
public class SuccessfulMatch<E extends AbstractModelEntity> implements Match<E> {

  private final double rate;

  private final MLSpaceRule rule;

  private final Map<String, Object> env;

  private final Map<E, List<IAttributeModification>> mods;

  private List<E> consumed;

  /**
   * @param rule
   *          Matched rule (e.g. for accessing to-be-produced entities)
   * @param rate
   *          Rate expression value (rate constant or probability)
   * @param environment
   *          Extracted local variables
   * @param mods
   *          Modifications to be performed on matched entities
   */
  public SuccessfulMatch(MLSpaceRule rule, double rate,
      Map<String, Object> environment, Map<E, List<IAttributeModification>> mods) {
    this.rate = rate;
    this.rule = rule;
    this.env = environment;
    this.mods = mods;
  }

  @Override
  public boolean isSuccess() {
    return true;
  }

  @Override
  public Map<String, Object> getEnv() {
    return env;
  }

  @Override
  public double getRate() {
    return rate;
  }

  @Override
  public MLSpaceRule getRule() {
    return rule;
  }

  @Override
  public SuccessfulMatch.ModRecord<E> apply() {
    Map<E, Map<String, Object>> attMods = new ArrayMap<>(mods.size());
    for (Map.Entry<E, List<IAttributeModification>> e : mods.entrySet()) {
      List<IAttributeModification> mod = e.getValue();
      E ent = e.getKey();
      if (SpecialAttributeModification.CONSUMED.equals(mod)) {
        if (consumed == null) {
          this.consumed = new ArrayList<>();
        }
        consumed.add(ent);
      } else {
        attMods.put(ent, SuccessfulMatch.applyAttMods(ent, mod, env));
      }
    }
    return new SuccessfulMatch.ModRecord<>(attMods);
  }

  @Override
  public SuccessfulMatch.ModRecord<E> applyReplacing(
      model.mlspace.rules.match.Match.IReplacer<? super E> rep) {
    Map<E, Map<String, Object>> attMods = new ArrayMap<>(mods.size());
    for (Map.Entry<E, List<IAttributeModification>> e : mods.entrySet()) {
      List<IAttributeModification> mod = e.getValue();
      E ent = e.getKey();
      if (SpecialAttributeModification.CONSUMED.equals(mod)) {
        if (consumed == null) {
          this.consumed = new ArrayList<>();
        }
        consumed.add(ent);
      } else {
        E repEnt = (E) rep.replace(ent);
        if (repEnt == ent) {
          attMods.put(repEnt, SuccessfulMatch.applyAttMods(repEnt, mod, env));
        } else {
          SuccessfulMatch.applyAttMods(repEnt, mod, env);
          // ... and leave finding out changes to caller
        }
      }
    }
    return new SuccessfulMatch.ModRecord<>(attMods);
  }

  @Override
  public List<E> getConsumed() {
    return consumed == null ? Collections.<E> emptyList() : consumed;
  }

  /**
   * Apply given attribute modifications to single entity
   * 
   * @param ent
   *          Entity to modify
   * @param entMods
   *          Modifications
   * @param env
   *          Environment, i.e. local variables
   * @return Map with modified attributes and previous values
   */
  public static <E extends AbstractModelEntity> Map<String, Object> applyAttMods(
      E ent, List<IAttributeModification> entMods, Map<String, Object> env) {
    Map<String, Object> prevVals = new LinkedHashMap<>();
    for (IAttributeModification em : entMods) {
      Object prevVal = em.modifyAttVal(ent, env);
      prevVals.put(em.getAttribute(), prevVal);
    }
    return prevVals;
  }

  /**
   * Container for attribute changes for several entities
   * 
   * @author Arne Bittig
   * 
   * @param <E>
   *          Model entity type
   */
  public static class ModRecord<E extends AbstractModelEntity> {
    private final Map<E, Map<String, Object>> attMods;

    /**
     * @param attMods
     *          Entity -> Attribute names and previous states map
     */
    public ModRecord(Map<E, Map<String, Object>> attMods) {
      this.attMods = attMods;
    }

    /**
     * @return Attribute names and previous states
     */
    public final Map<E, Map<String, Object>> getAttMods() {
      return attMods;
    }

    /**
     * @return true if record contains no modifications
     */
    public boolean isEmpty() {
      return attMods.isEmpty();
    }
  }
}