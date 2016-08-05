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
import java.util.LinkedHashMap;
import java.util.Map;

import model.mlspace.rules.MLSpaceRule;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.util.misc.Pair;

import simulator.mlspace.eventrecord.AbstractEventRecord.IChange;

/**
 * Base class for continuous-space and NSM event records
 * 
 * @author Arne Bittig
 * @date 28.09.2012
 * 
 * @param <T>
 *          Type of component
 * @param <C>
 *          Change record type
 */
abstract class AbstractEventRecord<T extends IShapedComponent, C extends IChange>
    implements IEventRecord {

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (changes != null) {
      builder.append("changes=");
      builder.append(changes);
      if (rules != null) {
        builder.append(", ");
      }
    }
    if (rules != null) {
      builder.append("rules=");
      builder.append(rules);
    }
    return builder.toString();
  }

  /** Internal map containing the changes */
  private final Map<T, C> changes = new LinkedHashMap<>(1);

  /** Unmodifiable view of changes to be returned by {@link #getChanges()} */
  private final Map<T, C> cachedUnmodViewOfChanges = Collections
      .unmodifiableMap(changes);

  private final Collection<? extends MLSpaceRule> rules;

  protected AbstractEventRecord(Collection<? extends MLSpaceRule> rules) {
    this.rules = rules;
  }

  @Override
  public final T getTriggeringComponent() {
    return changes.keySet().iterator().next();
  }

  protected final Map<T, C> getChanges() {
    return cachedUnmodViewOfChanges;
  }

  /**
   * Add change, throw exception if one was already present for same entity
   * 
   * @param entity
   *          Entity that changed
   * @param change
   *          Change
   */
  protected final void putChange(T entity, C change) {
    C oldVal = changes.put(entity, change);
    if (oldVal != null && change != ContSpaceEventRecord.DESTROYED) {
      // TODO: if comp is destroyed, effect for comp is created (with
      // empty change) and destruction is marked later. This may be a
      // little inelegant as now here identity with that marker change
      // instance needs to be checked.
      throw new IllegalStateException(entity
          + " already has a recorded change: " + oldVal + "; is " + change
          + " part of another effect?");
    }
  }

  @Override
  public Collection<? extends MLSpaceRule> getRules() {
    return rules;
  }

  /**
   * Base interface changes to one spatial component
   * 
   * @author Arne Bittig
   * @date 01.10.2012
   */
  protected interface IChange {
    /**
     * @return previous and new surrounding spatial component (null if no
     *         change)
     * @see IShapedComponent#getEnclosingEntity()
     */
    Pair<IShapedComponent, IShapedComponent> getEnclosingEntityChange();
  }

  /**
   * Base class for recording changes, handling changes in the enclosing spatial
   * component (see {@link IShapedComponent#getEnclosingEntity()})
   * 
   * @author Arne Bittig
   */
  protected abstract static class Change implements IChange {

    private Pair<IShapedComponent, IShapedComponent> enclosingEntityBeforeAndAfter;

    @Override
    public final Pair<IShapedComponent, IShapedComponent> getEnclosingEntityChange() {
      return enclosingEntityBeforeAndAfter;
    }

    protected final void setEnclosingEntityChange(IShapedComponent before,
        IShapedComponent after) {
      if (this.enclosingEntityBeforeAndAfter != null) {
        throw new IllegalArgumentException(
            "Enclosing comp can only change once per effect!");
      }
      if (before == null) {
        throw new IllegalArgumentException("Enclosing entity change"
            + " needs previous  value.");
      } // after == null legal for destruction of entities
      if (before.equals(after)) {
        throw new IllegalArgumentException("Enclosing entity did not change!\n"
            + before + " in " + this);
      }
      this.enclosingEntityBeforeAndAfter = new Pair<>(before, after);
    }

    @Override
    public String toString() {
      if (enclosingEntityBeforeAndAfter == null) {
        return "";
      }
      return ", surroundChange=" + enclosingEntityBeforeAndAfter;
    }

  }

}
