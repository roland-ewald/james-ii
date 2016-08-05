/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 *
 */
package simulator.mlspace.eventrecord;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;

import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;
import simulator.mlspace.eventrecord.ISubvolEventRecord.ISubvolChange;

/**
 * Container for changes resulting from a {@link Subvol} event
 * 
 * @author Arne Bittig
 * @date 28.09.2012
 */
public class SubvolEventRecord extends
    AbstractEventRecord<Subvol, ISubvolChange> implements ISubvolEventRecord {

  private Map<SpatialEntity, ICompChange> enclosingEntChange =
      Collections.EMPTY_MAP;

  /**
   * Single-subvol event, i.e. reaction
   * 
   * @param affected
   *          Affected subvol
   * @param stateChange
   *          State change "vector"
   * @param rule
   *          Applied rules
   */
  public SubvolEventRecord(Subvol affected,
      Map<NSMEntity, Integer> stateChange, MLSpaceRule rule) {
    super(Collections.singleton(rule));
    this.putChange(affected, new SubvolChange(stateChange));
  }

  /**
   * Diffusion from one subvol to another with same parent
   * 
   * @param source
   *          Source subvol
   * @param sourceUpdate
   *          State update
   * @param target
   *          Target subvol (changed by opposite of given update)
   */
  public SubvolEventRecord(Subvol source, Map<NSMEntity, Integer> sourceUpdate,
      Subvol target) {
    super(Collections.<MLSpaceRule> emptySet());
    this.putChange(source, new SubvolChange(sourceUpdate));
    this.putChange(target, null);
  }

  /**
   * Diffusion from one subvol to another with in different parent
   * 
   * @param source
   *          Source subvol
   * @param sourceUpdate
   *          State update for source
   * @param target
   *          Target subvol
   * @param targetUpdate
   *          State update for target (entities may change attributes with
   *          crossing)
   * @param rules
   *          Applied rules for crossing
   */
  public SubvolEventRecord(Subvol source, Map<NSMEntity, Integer> sourceUpdate,
      Subvol target, Map<NSMEntity, Integer> targetUpdate,
      Collection<? extends MLSpaceRule> rules) {
    super(rules);
    this.putChange(source, new SubvolChange(sourceUpdate));
    if (!targetUpdate.isEmpty()) {
      this.putChange(target, new SubvolChange(targetUpdate));
    } // CHECK: else putChange(target,null) ?
  }

  /**
   * Production of entities in one or more subvols
   * 
   * @param subvols
   *          Subvols in which entities were produced
   * @param updEnts
   *          Entities that were produced, in order of subvols where they were
   *          produced
   */
  public SubvolEventRecord(List<Subvol> subvols, List<NSMEntity> updEnts
  /* , MLSpaceRule rule */) {
    super(Collections.<MLSpaceRule> emptyList() /* singleton(rule) */);
    if (subvols.size() != updEnts.size()) {
      throw new IllegalArgumentException("One update per sv!");
    }
    Iterator<Subvol> svIt = subvols.iterator();
    Iterator<NSMEntity> updIt = updEnts.iterator();
    Map<Subvol, IUpdateableMap<NSMEntity, Integer>> svUpdates =
        new LinkedHashMap<>();
    while (svIt.hasNext()) {
      Subvol subvol = svIt.next();
      NSMEntity updEnt = updIt.next();
      IUpdateableMap<NSMEntity, Integer> svUpdate = svUpdates.get(subvol);
      if (svUpdate == null) {
        svUpdate =
            UpdateableAmountMap.wrap(new LinkedHashMap<NSMEntity, Integer>(1));
        svUpdates.put(subvol, svUpdate);
      }
      svUpdate.update(updEnt, 1);
    }
    for (Map.Entry<Subvol, IUpdateableMap<NSMEntity, Integer>> e : svUpdates
        .entrySet()) {
      this.putChange(e.getKey(), new SubvolChange(e.getValue()));
    }
  }

  /**
   * Single subvol state update in hybrid simulation resulting from move of
   * nearby or enclosing compartment with change of parent entity
   * 
   * @param subvol
   *          Subvol that changed
   * @param state
   *          Change vector
   * @param prevEnclComp
   *          Enclosing entity before change
   */
  public SubvolEventRecord(Subvol subvol, Map<NSMEntity, Integer> state,
      SpatialEntity prevEnclComp) {
    super(Collections.<MLSpaceRule> emptySet());
    SubvolChange change = new SubvolChange(state);
    change.setEnclosingEntityChange(prevEnclComp, subvol.getEnclosingEntity());
    this.putChange(subvol, change);
  }

  /**
   * Single subvol state update in hybrid simulation resulting from move of
   * nearby or enclosing compartment without change of parent entity
   * 
   * @param subvol
   *          Subvol that changed
   * @param state
   *          Change vector
   */
  public SubvolEventRecord(Subvol subvol, Map<NSMEntity, Integer> state) {
    super(Collections.<MLSpaceRule> emptySet());
    ISubvolChange change = new SubvolChange(state);
    this.putChange(subvol, change);
  }

  @Override
  public boolean isSuccess() {
    return !this.getChanges().isEmpty();
  }

  /**
   * @return Subvols that changed
   */
  @Override
  public Map<Subvol, ISubvolChange> getSubvolChanges() {
    return super.getChanges();
  }

  @Override
  public Map<SpatialEntity, ICompChange> getEnclosingEntityChange() {
    return enclosingEntChange;
  }

  /**
   * @param ent
   *          Entity
   * @param change
   *          Change
   */
  public final void setEnclosingEntityChange(SpatialEntity ent,
      ICompChange change) {
    if (!this.enclosingEntChange.isEmpty()) {
      throw new IllegalStateException();
    }
    this.enclosingEntChange = Collections.singletonMap(ent, change);
  }

  /**
   * Container for changes to a single {@link Subvol}
   * 
   * @author Arne Bittig
   * @date 28.09.2012
   */
  public static class SubvolChange extends AbstractEventRecord.Change implements
      ISubvolChange {

    private final Map<NSMEntity, Integer> stateChange;

    /**
     * @param stateChange
     *          state update "vector"
     */
    public SubvolChange(Map<NSMEntity, Integer> stateChange) {
      this.stateChange = stateChange;
    }

    /**
     * @return Subvol state change "vector"
     */
    @Override
    public Map<NSMEntity, Integer> getStateChange() {
      return stateChange;
    }

    @Override
    public String toString() {
      return stateChange + super.toString();
    }
  }
}
