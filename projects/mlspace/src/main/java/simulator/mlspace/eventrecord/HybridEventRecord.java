/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.eventrecord;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.util.collection.CombinedIterator;

/**
 * Container for event effects in hybrid simulation
 * 
 * @author Arne Bittig
 * @date 03.10.2012
 */
public class HybridEventRecord implements IContSpaceEventRecord,
    ISubvolEventRecord {

  private final IContSpaceEventRecord csRecord;

  private final Collection<ISubvolEventRecord> svRecords;

  /**
   * If effect is nsm-only, this field is used to avoid multiple calls to get
   * single element from {@link #svRecords}
   */
  private final ISubvolEventRecord singleSvRecord;

  private Set<Subvol> cachedAllAffectedSubvols = null;

  private final Collection<Subvol> parentChanges;

  /**
   * FAILED continuous-space event (no effect on subvol level)
   * 
   * @param csRecord
   *          Continuous space event record
   */
  public HybridEventRecord(IContSpaceEventRecord csRecord) {
    assert !csRecord.getState().isSuccess();
    this.csRecord = csRecord;
    this.svRecords = Collections.emptySet();
    this.singleSvRecord = null;
    this.parentChanges = Collections.emptySet();
  }

  /**
   * Wrapped Subvol-related only event
   * 
   * @param svEventRecord
   *          Subvol event record
   */
  public HybridEventRecord(ISubvolEventRecord svEventRecord) {
    this.csRecord = null;
    this.svRecords = Collections.singleton(svEventRecord);
    this.singleSvRecord = svEventRecord;
    this.parentChanges = Collections.emptySet(); // TODO: comp change effect
  }

  /**
   * Continuous-space event and aftermath on subvol level
   * 
   * @param csRecord
   *          Continuous space event record
   * @param svRecords
   *          Record of triggered effects on subvols
   * @param reassignedSvs
   *          Subvols with changed enclosing entities
   */
  public HybridEventRecord(IContSpaceEventRecord csRecord,
      Collection<ISubvolEventRecord> svRecords, Collection<Subvol> reassignedSvs) {
    this.csRecord = csRecord;
    this.svRecords = svRecords;
    this.parentChanges = reassignedSvs;
    this.singleSvRecord = null;
  }

  /**
   * Check whether hybrid event consists only of a single subvol event or
   * whether belongs to an event originating in continuous space with effects on
   * the subvol level.
   * 
   * Note: If true, {@link #getTriggeringComponent()} returns a
   * {@link model.mlspace.subvols.Subvol subvolume}, otherwise a
   * {@link model.mlspace.entities.spatial.SpatialEntity spatial model entity} .
   * 
   * @return true iff only single subvol event is present
   */
  public final boolean isWrappedSubvolRecord() {
    return singleSvRecord != null;
  }

  @Override
  public boolean isSuccess() {
    return getState().isSuccess();
  }

  /**
   * {@inheritDoc}. Hybrid event record returns the rules of the first wrapped
   * continuous space event record, or if it only wraps a subvol event record,
   * the rules of the latter.
   */
  @Override
  public Collection<? extends MLSpaceRule> getRules() {
    if (this.isWrappedSubvolRecord()) {
      return singleSvRecord.getRules();
    }
    return csRecord.getRules();
  }

  @Override
  public Map<Subvol, ISubvolChange> getSubvolChanges() {
    if (this.isWrappedSubvolRecord()) {
      return singleSvRecord.getSubvolChanges();
    }
    if (cachedAllAffectedSubvols == null) {
      cachedAllAffectedSubvols = aggregateAffectedSubvols(svRecords);
    }
    return new AbstractMap<Subvol, ISubvolChange>() {

      @Override
      public Set<Map.Entry<Subvol, ISubvolChange>> entrySet() {
        throw new UnsupportedOperationException("Not to be used on aggregation");
      }

      @Override
      public Set<Subvol> keySet() {
        return cachedAllAffectedSubvols;
      }
    };
  }

  @Override
  public Map<SpatialEntity, ICompChange> getEnclosingEntityChange() {
    if (this.isWrappedSubvolRecord()) {
      return singleSvRecord.getEnclosingEntityChange();
    }
    return Collections.emptyMap();

  }

  private Set<Subvol> aggregateAffectedSubvols(
      Collection<ISubvolEventRecord> eventRecords) {
    Set<Subvol> affected = new LinkedHashSet<>(parentChanges);
    for (ISubvolEventRecord eventRecord : eventRecords) {
      affected.addAll(eventRecord.getSubvolChanges().keySet());
    }
    return affected;
  }

  /**
   * @return Subvolume changes as individual records
   */
  public final Collection<ISubvolEventRecord> getSvRecords() {
    return svRecords;
  }

  @Override
  public IShapedComponent getTriggeringComponent() {
    if (csRecord == null) {
      return singleSvRecord.getTriggeringComponent();
    }
    return csRecord.getTriggeringComponent();
  }

  @Override
  public State getState() {
    return csRecord == null ? State.SUCCESS : csRecord.getState();
  }

  @Override
  public Map<SpatialEntity, ICompChange> getCompChanges() {
    if (this.singleSvRecord != null) {
      return singleSvRecord.getEnclosingEntityChange();
    }
    return csRecord.getCompChanges();
  }

  @Override
  public final int getNumInfo() {
    return csRecord.getNumInfo();
  }

  // @Override
  // public IContSpaceEventRecord getTriggeringEffect() {
  // return csRecord.getTriggeringEffect();
  // }

  @Override
  public Collection<IContSpaceEventRecord> getTriggeredEffects() {
    return csRecord.getTriggeredEffects();
  }

  // @Override
  // public IContSpaceEventRecord getAggregateCSERecord() {
  // return csRecord == null ? null : csRecord.getAggregateCSERecord();
  // }

  @Override
  public Map<SpatialEntity, IDisplacementVector> getCompMoves() {
    return csRecord.getCompMoves();
  }

  @Override
  public Collection<SpatialEntity> getCompCreations() {
    return csRecord.getCompCreations();
  }

  @Override
  public Collection<SpatialEntity> getCompDestructions() {
    return csRecord.getCompDestructions();
  }

  private Collection<? extends MLSpaceRule> cachedAllRules = null;

  @Override
  public Collection<? extends MLSpaceRule> getAllRules() {
    if (cachedAllRules == null) {
      if (svRecords.isEmpty()) {
        cachedAllRules = csRecord.getAllRules();
      } else {
        Collection<MLSpaceRule> svRules = new ArrayList<>();
        for (ISubvolEventRecord svRecord : svRecords) {
          svRules.addAll(svRecord.getRules());
        }
        if (csRecord == null) {
          cachedAllRules = svRules;
        } else {
          cachedAllRules =
              new CombinedCollection<>(csRecord.getAllRules(), svRules);
        }
      }
    }
    return cachedAllRules;
  }

  private Map<SpatialEntity, ICompChange> cachedAllCompChanges = null;

  @Override
  public Map<SpatialEntity, ICompChange> getAllCompChanges() {
    if (cachedAllCompChanges != null) {
      return cachedAllCompChanges;
    }
    if (csRecord == null) {
      assert singleSvRecord != null;
      return singleSvRecord.getEnclosingEntityChange();
    } else {
      cachedAllCompChanges = csRecord.getAllCompChanges();
      boolean copied = false;
      for (ISubvolEventRecord svRecord : svRecords) {
        Map<SpatialEntity, ICompChange> change =
            svRecord.getEnclosingEntityChange();
        if (change.isEmpty()) {
          continue;
        }
        if (!copied) {
          cachedAllCompChanges = new LinkedHashMap<>(cachedAllCompChanges);
          copied = true;
        }
        Map.Entry<SpatialEntity, ICompChange> e =
            change.entrySet().iterator().next();
        if (!cachedAllCompChanges.containsKey(e.getKey())) {
          cachedAllCompChanges.put(e.getKey(), e.getValue());
        }
      }
    }
    return cachedAllCompChanges;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Hybrid[");
    if (csRecord != null) {
      builder.append("cont=");
      builder.append(csRecord);
      builder.append(", ");
    }
    if (!svRecords.isEmpty()) {
      builder.append("nsm=");
      builder.append(svRecords);
    }
    builder.append("]");
    return builder.toString();
  }

  /**
   * View of several collections as one
   * 
   * Note: {@link #size()} implementation assumes the size of the underlying
   * collections does not change.
   * 
   * @author Arne Bittig
   * @date 03.10.2012
   * @param <E>
   *          Element type
   */
  public static class CombinedCollection<E> extends AbstractCollection<E> {

    private int totalSize = 0;

    private final Collection<Iterable<? extends E>> colls;

    /**
     * Initially empty collection combining those subsequently
     * {@link #addCollection(Collection) added}.
     * 
     * @param expectedSize
     *          Expected number of collections to combine later
     */
    public CombinedCollection(int expectedSize) {
      this.colls = new ArrayList<>(expectedSize);
    }

    /**
     * @param coll1
     *          One collection
     * @param coll2
     *          Another collection
     */
    public CombinedCollection(Collection<? extends E> coll1,
        Collection<? extends E> coll2) {
      this(2);
      this.addCollection(coll1);
      this.addCollection(coll2);
    }

    /**
     * @param coll
     *          Collection to add to combined view
     * @return same combined collection view (i.e. <code>this</code> object)
     */
    public final CombinedCollection<E> addCollection(
        Collection<? extends E> coll) {
      if (!coll.isEmpty()) {
        this.totalSize += coll.size();
        this.colls.add(coll);
      }
      return this;
    }

    @Override
    public Iterator<E> iterator() {
      return new CombinedIterator<>(colls.toArray(new Iterable[colls.size()]));
    }

    @Override
    public int size() {
      return totalSize;
    }

  }
}
