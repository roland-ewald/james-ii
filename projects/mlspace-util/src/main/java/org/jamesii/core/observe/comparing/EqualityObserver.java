/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.comparing;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.logging.Level;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Observer to be used with two (or more, with caveats) {@link IObservable
 * observables} that compares (and checks for equality of) the sequence of calls
 * to their respective {@link IObservable#changed() changed()} and
 * {@link IObservable#changed(Object) changed(Object)} methods. A use for this
 * is, for example, to assert reproducibility, i.e. that the same model
 * simulated with the same simulator initialized with the same random number
 * generator and seeds results in the same output.
 *
 * Since the observables may be objects that change (e.g. processors) and cannot
 * be expected to be at the same point of computation at the same time, i.e.
 * when this observer would compare them, the comparison-relevant information
 * has to be extracted after each changed method call via a to-be-provided class
 * implementing the {@link InformationExtractor} interface.
 *
 * May be used with processes running in parallel or sequentially. In the latter
 * case, (strong references to) all pieces of information produced by one
 * process are kept until the other has produced the respective hint to compare
 * each to. Corresponding pieces of information from two different observables
 * are discarded once they have been compared. Three or more observables
 * changing in parallel can be compared provided that each has called
 * {@link IObservable#changed()} or {@link IObservable#changed(Object)} at least
 * once before all others that previously did so did so twice.
 *
 * @author Arne Bittig
 * @param <E>
 *          Type of entity to be observed
 * @date 21.09.2012
 */
public class EqualityObserver<E extends IObservable> implements IObserver<E> {

  private static final Map<IUniqueID, EqualityObserver<?>> INSTANCES =
      new WeakHashMap<>();

  /**
   * Factory method for {@link EqualityObserver}s. Entities/observables to be
   * compared are identified by a common property, e.g. for comparing
   * computation tasks of one experiment, the experiment id can be used (or the
   * configuration id, if comparisons are to be performed in one experiment).
   * 
   * @param id
   *          Identifier associated with entities/observables to compare
   * @param infoExtractor
   *          {@link InformationExtractor} to use (must be the same for all
   *          entities/observables to compare with same id)
   * @return Comparing observer for classes associated with given id (may be
   *         newly or previously created)
   * @throws ClassCastException
   *           if a {@link EqualityObserver} has been created expecting another
   *           class of observables than given (may be thrown later)
   */
  public static synchronized <E extends IObservable> EqualityObserver<E> getInstanceFor(
      IUniqueID id, InformationExtractor<E, ?> infoExtractor) {
    EqualityObserver<E> obs = (EqualityObserver<E>) INSTANCES.get(id);
    if (obs != null) {
      if (!obs.infoExtractor.equals(infoExtractor)) {
        throw new IllegalArgumentException("Cannot use different "
            + "information extractors for same ID: " + id + " "
            + obs.infoExtractor + " vs. " + infoExtractor);
      }
      return obs;
    }
    obs = new EqualityObserver<>(infoExtractor);
    INSTANCES.put(id, obs);
    return obs;
  }

  /**
   * Helper interface for {@link EqualityObserver} to extract pieces of
   * information to be compared. The pieces of information must override
   * {@link Object#equals(Object)} for useful assessment of equality of the
   * information from different observables. Note that the performed comparison
   * is null-safe, i.e. null is a valid piece of information (and equal to any
   * other "null information").
   * 
   * Implementations should override {@link Object#equals(Object) equals} (and
   * {@link Object#hashCode() hashCode}): They will be used in
   * {@link EqualityObserver}, of which there should be one instance for several
   * observables, and it is checked in its factory method that these all use the
   * same InformationExtractor. However, JAMES' experiment initialization may
   * involve making copies of passed on parameter classes (especially if they
   * are {@link java.io.Serializable serializable}), thus object identity is not
   * a necessary condition here.
   * 
   * The class returned by the two method must also override
   * {@link Object#equals(Object) equals}, and will be used for displayed
   * messages and thus should have a useful {@link Object#toString() toString}
   * implementation. (Other than that, its type is not relevant to the
   * {@link EqualityObserver}.)
   * 
   * @author Arne Bittig
   * @date 24.09.2012
   * @param <O>
   *          Observable type to which extractor is applicable
   * @param <I>
   *          Type of information produced
   */
  public interface InformationExtractor<O, I> {

    /**
     * Extract information to compare / assess similarity of observables
     * 
     * @param observable
     *          Observable to extract information from
     * @return Information
     * @see IObserver#update(IObservable)
     */
    I extractInformation(O observable);

    /**
     * Extract information to compare / assess similarity of observables
     * considering the given hint (If {@link IObservable#changed(Object)} is not
     * expected to be called on the respective type of observable or it does not
     * matter for similarity assessment, calls to this method should at least
     * return the result of the method without hint.)
     * 
     * @param observable
     *          Observable to extract information from
     * @param hint
     *          Hint provided to {@link IObservable#changed(Object)}
     * @return Information
     * @see IObserver#update(IObservable,Object)
     */
    I extractInformation(O observable, Object hint);
  }

  private final Map<E, Queue<Object>> infoMap = new IdentityHashMap<>(2);

  private final Map<E, Integer> totalInfoAmounts = new IdentityHashMap<>(2);

  private final InformationExtractor<E, ?> infoExtractor;

  private int commonInfoAmount = 0;

  private int firstDifferenceIndex = -1;

  /* logging properties */
  private boolean logAllDifferences;

  private int sameMessageInterval;

  /**
   * Default constructor. Note that instantiation is supposed to be done via a
   * factory method to better achieve that the same observer instance is
   * associated with several observables to be compared.
   * 
   * @param infoExtractor
   *          Information extraction method
   * @see #getInstanceFor(IUniqueID, InformationExtractor)
   */
  protected EqualityObserver(InformationExtractor<E, ?> infoExtractor) {
    this.infoExtractor = infoExtractor;
  }

  /**
   * Set flags and parameter related to message output. Each value may be null
   * left unchanged if so.
   * 
   * @param logAllDifferences
   *          Flag whether to display a message for every step in which
   *          different observables came with different infoMap (if false the
   *          message is shown only for the first)
   * @param sameMessageInterval
   *          Display message if infoMap match up to current point, but only
   *          every ... steps (0 for never)
   */
  public void setLoggingProperties(Boolean logAllDifferences,
      Integer sameMessageInterval) {

    if (logAllDifferences != null) {
      this.logAllDifferences = logAllDifferences;
    }
    if (sameMessageInterval != null) {
      this.sameMessageInterval = sameMessageInterval;
    }
  }

  /**
   * Get number of calls to {@link #update(IObservable, Object)} with different
   * observables but equal hint objects in same order
   * 
   * @return "index" of first difference in infoMap, -1 if n/a
   */
  public final int getFirstDifferenceIndex() {
    return firstDifferenceIndex;
  }

  @Override
  public void update(E entity) {
    updateWithInfo(entity, infoExtractor.extractInformation(entity));
    // if (!checkNewEntityAndRegister(entity) && logAllNoHintCalls) {
    // ApplicationLogger.log(Level.WARNING, "update method called"
    // + " without hint but known entity: " + entity);
    // }
  }

  @Override
  public void update(E entity, Object hint) {
    updateWithInfo(entity, infoExtractor.extractInformation(entity, hint));
  }

  private void updateWithInfo(E entity, Object info) {
    synchronized (infoMap) {
      checkNewEntityAndRegister(entity);
      Integer infosForE = totalInfoAmounts.get(entity) + 1;
      totalInfoAmounts.put(entity, infosForE);
      infoMap.get(entity).add(info);
      Integer minTotalInfoAmount = Collections.min(totalInfoAmounts.values());
      if (minTotalInfoAmount > commonInfoAmount && infoMap.size() > 1) {
        commonInfoAmount = minTotalInfoAmount;
        checkQueueHeadsEquality();
      }
    }
  }

  /**
   * @param entity
   * @return true if entity is new to this observer
   */
  private boolean checkNewEntityAndRegister(E entity) {
    synchronized (infoMap) {
      if (!infoMap.containsKey(entity)) {
        if (commonInfoAmount > 0) {
          throw new IllegalArgumentException("New entity registered"
              + " too late: Previous infoMap of other entities "
              + "were already discarded.");
        }
        infoMap.put(entity, new LinkedList<>());
        totalInfoAmounts.put(entity, 0);
        return true;
      }
    }
    return false;
  }

  /**
   *
   */
  private boolean checkQueueHeadsEquality() {
    boolean allEqualSoFar = true;
    synchronized (infoMap) {
      Iterator<Map.Entry<E, Queue<Object>>> infoIter =
          infoMap.entrySet().iterator();
      Map.Entry<E, Queue<Object>> e = infoIter.next();
      E firstEntity = e.getKey();
      Object firstInfo = e.getValue().remove();
      while (infoIter.hasNext()) {
        Map.Entry<E, Queue<Object>> e2 = infoIter.next();
        E currentEntity = e2.getKey();
        Object currentInfo = e2.getValue().remove();
        if (!equalIncludingNull(firstInfo, currentInfo)) {
          if (firstDifferenceIndex < 0) {
            firstDifferenceIndex = commonInfoAmount;
          }
          logDifference(firstEntity, firstInfo, currentEntity, currentInfo,
              allEqualSoFar);
          allEqualSoFar = false;
        }
      }
      if (allEqualSoFar) {
        logEquality(firstInfo);
      }
    }
    return allEqualSoFar;
  }

  private static boolean equalIncludingNull(Object one, Object other) {
    if (one == null) {
      return other == null;
    }
    return one.equals(other);
  }

  /**
   * @param firstEntity
   * @param firstHint
   * @param diffEntity
   * @param diffHint
   * @param isFirstDifference
   */
  private void logDifference(E firstEntity, Object firstHint, E diffEntity,
      Object diffHint, boolean isFirstDifference) {
    if (logAllDifferences || isFirstDifference) {
      StringBuilder msg = new StringBuilder();
      msg.append("Difference at hint #");
      msg.append(Integer.toString(commonInfoAmount));
      if (firstDifferenceIndex == commonInfoAmount) {
        msg.append(" -- first difference!\n");
      } else {
        msg.append(" (first was at #");
        msg.append(Integer.toString(firstDifferenceIndex));
        msg.append(")\n");
      }
      msg.append(firstHint.toString());
      msg.append(" for ");
      msg.append(firstEntity.toString());
      msg.append(" ...vs.\n");
      msg.append(diffHint.toString());
      msg.append(" for ");
      msg.append(diffEntity.toString());
      ApplicationLogger.log(Level.SEVERE, msg.toString());
    }
  }

  /**
   * @param hint
   */
  private void logEquality(Object hint) {
    if (firstDifferenceIndex > 0) {
      return; // equality violated earlier
    }
    if (sameMessageInterval > 0 && commonInfoAmount % sameMessageInterval == 0) {
      ApplicationLogger.log(Level.INFO, "Behavior equal up to observation #"
          + commonInfoAmount + ". This hint: " + hint);
    }
  }
}