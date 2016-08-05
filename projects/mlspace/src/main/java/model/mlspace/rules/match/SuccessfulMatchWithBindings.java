package model.mlspace.rules.match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.entities.binding.IEntityWithBindings;
import model.mlspace.entities.binding.RuleEntityWithBindings.BindingAction;
import model.mlspace.entities.spatial.Compartment;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.rules.attributemodification.IAttributeModification;

/**
 * Match of applicable entities to rule that modifies binding sites
 * 
 * @author Arne Bittig
 * @param <E>
 *          Entity type
 */
public class SuccessfulMatchWithBindings<E extends AbstractModelEntity> extends
    SuccessfulMatch<E> {

  private final Compartment comp0;

  private final Compartment comp1;

  private final Map<String, BindingAction> bindingActions0;

  private final Map<String, BindingAction> bindingActions1;

  /**
   * @param rule
   *          Matched rule (e.g. for accessing to-be-produced entities)
   * @param rate
   *          Rate expression value (rate constant or probability)
   * @param environment
   *          Extracted local variables
   * @param mods
   *          Modifications to be performed on matched entities
   * @param ent0
   *          One matched entity
   * @param bindingActions0
   *          Binding site changes for matched entity
   * @param ent1
   *          Another matched entity
   * @param bindingActions1
   *          Binding site changes for other matched entity
   */
  public SuccessfulMatchWithBindings(MLSpaceRule rule, double rate,
      Map<String, Object> environment,
      Map<E, List<IAttributeModification>> mods, E ent0,
      Map<String, BindingAction> bindingActions0, E ent1,
      Map<String, BindingAction> bindingActions1) {
    super(rule, rate, environment, mods);
    this.bindingActions0 = bindingActions0;
    this.bindingActions1 = bindingActions1;
    this.comp0 = ent0 instanceof Compartment ? (Compartment) ent0 : null;
    this.comp1 = ent1 instanceof Compartment ? (Compartment) ent1 : null;
  }

  @Override
  public SuccessfulMatch.ModRecord<E> apply() {
    SuccessfulMatch.ModRecord<E> superAppl = super.apply();
    return applyBindings(superAppl);
  }

  private SuccessfulMatch.ModRecord<E> applyBindings(
      SuccessfulMatch.ModRecord<E> superAppl) {
    List<BindingMod<Compartment>> bm;
    if (bindingActions0 != null) {
      bm =
          SuccessfulMatchWithBindings.applyBindingActions(bindingActions0,
              comp0, comp1);
      if (bindingActions1 != null) {
        bm.addAll(SuccessfulMatchWithBindings.applyBindingActions(
            bindingActions1, comp1, comp0));
      }
    } else if (bindingActions1 != null) {
      bm =
          SuccessfulMatchWithBindings.applyBindingActions(bindingActions1,
              comp1, comp0);
    } else {
      throw new IllegalStateException(
          "This method must not be called if there is no binding action");
    }
    return new SuccessfulMatchWithBindings.ModRecordWithBindings<>(
        superAppl.getAttMods(), bm);
  }

  /**
   * Apply binding actions (usually from rule) to given entities
   * 
   * @param bindingActions
   *          Map of binding site name to change specification
   * @param compToChange
   *          Entity to which binding actions apply
   * @param compToBind
   *          Entity to bind if bindingActions contain binding (ignored if
   *          "release" only)
   * @return Map of before-change binding site state for each affected entity
   * @throws NullPointerException
   *           if the bindingActions implied binding but either entity does not
   *           allow it (non-binding-enabled entities may trigger release of
   *           unrelated bound partners in binding-enabled entities)
   */
  private static List<BindingMod<Compartment>> applyBindingActions(
      Map<String, BindingAction> bindingActions, Compartment compToChange,
      Compartment compToBind) {

    List<BindingMod<Compartment>> rv = new ArrayList<>();
    for (Map.Entry<String, BindingAction> e : bindingActions.entrySet()) {
      String bindingSite = e.getKey();
      if (e.getValue().releases()) {
        Compartment prevBoundComp = compToChange.release(bindingSite);
        // release action also allowed for sites of unknown binding
        // state, i.e. there may not actually be something bound:
        if (prevBoundComp != null) {
          String bsOnReleasedComp = prevBoundComp.getBindingSite(compToChange);
          prevBoundComp.release(bsOnReleasedComp);
          rv.add(new BindingMod<>(compToChange, bindingSite, prevBoundComp));
          rv.add(new BindingMod<>(prevBoundComp, bsOnReleasedComp, compToChange));
        }
      }
      if (e.getValue().binds()) {
        compToChange.bind(bindingSite, compToBind);
        rv.add(new BindingMod<>(compToChange, bindingSite, null));
      }
    }
    return rv;
  }

  /**
   * Container for attribute and binding state changes for several entities
   * 
   * @author Arne Bittig
   * @date 02.10.2012
   * @param <E>
   *          Entity type for attribute change map
   * @param <EB>
   *          Entity type for binding state change map (some entities may have
   *          attributes but no binding sites, hence the different type)
   */
  public static class ModRecordWithBindings<E extends AbstractModelEntity, EB extends AbstractModelEntity & IEntityWithBindings<Object>>
      extends SuccessfulMatch.ModRecord<E> {

    private final List<BindingMod<EB>> bindMods;

    /**
     * @param attMods
     *          Entity -> Attribute names and previous states map
     * @param bindMods
     *          Entity -> Binding site names and previously bound entity
     */
    public ModRecordWithBindings(Map<E, Map<String, Object>> attMods,
        List<BindingMod<EB>> bindMods) {
      super(attMods);
      this.bindMods = bindMods;
    }

    /**
     * @return Entity -> Binding site names and previously bound entity map
     */
    public final List<BindingMod<EB>> getBindMods() {
      return bindMods;
    }

    @Override
    public boolean isEmpty() {
      return super.isEmpty() && bindMods.isEmpty();
    }
  }

  public static class BindingMod<EB extends AbstractModelEntity & IEntityWithBindings<Object>> {
    private final EB changedEntity;

    private final String changedSite;

    private final EB previousEntity;

    public BindingMod(EB changedEntity, String changedSite, EB previousEntity) {
      this.changedEntity = changedEntity;
      this.changedSite = changedSite;
      this.previousEntity = previousEntity;
    }

    public final EB getChangedEntity() {
      return changedEntity;
    }

    public final String getChangedSite() {
      return changedSite;
    }

    public final EB getPreviousEntity() {
      return previousEntity;
    }
  }
}