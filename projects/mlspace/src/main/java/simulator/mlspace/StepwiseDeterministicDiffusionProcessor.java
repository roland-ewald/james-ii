/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.mlspace.IMLSpaceModel;
import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.eventset.Entry;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.misc.Quadruple;

import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.SubvolEventRecord.SubvolChange;

/**
 * Subvols-/population based processor handling diffusion diffusion between
 * {@link Subvol}s in fixed distance time steps with a certain fraction of all
 * entities (depending on the respective diffusion constants) diffusing at once.
 * 
 * This can be interpreted as a rather crude approximation of PDE behaviour
 * (using only integer number values, pending adaptations of the subvol class
 * for a generic number type to be used in the state vector).
 * 
 * Limitations:
 * <ul>
 * <li>no reactions
 * <li>no diffusion over compartment boundaries (implementation pending,
 * probably not be too difficult)
 * <li>no customization or adaptation of step length (so far)
 * <li>rounding: always down so far (double to int conversion)
 * </ul>
 * 
 * @author Arne Bittig
 * @date 12.10.2012
 */
public class StepwiseDeterministicDiffusionProcessor extends
    AbstractMLSpaceProcessor<IMLSpaceEvent<Void>, ISubvolEventRecord> implements
    IPopulationProcessor<IMLSpaceEvent<Void>> {

  private static final long serialVersionUID = 5945385856839106682L;

  /** The subvolumes (processed in every step) */
  private final Collection<Subvol> subvols;

  /** The time step length */
  private final double stepInterval;

  /**
   * @param model
   *          Model to simulate
   */
  public StepwiseDeterministicDiffusionProcessor(IMLSpaceModel model) {
    super(model, new SingleEntryEventQueue<IMLSpaceEvent<Void>>(), null,
        new Timer());
    this.subvols = model.getSubvolumes();
    this.stepInterval = getStepInterval(model);
    IMLSpaceEvent<Void> dummyEvent = new IMLSpaceEvent<Void>() {

      @Override
      public Void getTriggeringComponent() {
        return null;
      }
    };
    enqueueEvent(dummyEvent, stepInterval);
  }

  private static double getStepInterval(IMLSpaceModel model) {
    double maxDiff = 0.;
    double halfSvSlMin = Double.MAX_VALUE;
    for (Subvol sv : model.getSubvolumes()) {
      for (Map.Entry<NSMEntity, Integer> e : sv.getState().entrySet()) {
        Double diff = e.getKey().getDiffusionConstant();
        if (diff > maxDiff) {
          maxDiff = diff;
        }
      }
      double minHalfSvExt =
          Vectors.vecNormMin(sv.getShape().getMaxExtVector().toArray());
      if (minHalfSvExt < halfSvSlMin) {
        halfSvSlMin = minHalfSvExt;
      }
    }
    return halfSvSlMin * halfSvSlMin / maxDiff / 2;
  }

  @Override
  public ISubvolEventRecord handleEvent(IMLSpaceEvent<Void> event) {
    // assert event instanceof NSMEvent;
    // assert ((NSMEvent) event).getType() == NSMEventType.NSMDIFFUSION;
    Map<Subvol, IUpdateableMap<NSMEntity, Integer>> dropIn =
        new LinkedHashMap<>();
    Map<Subvol, IUpdateableMap<NSMEntity, Integer>> takeOut =
        new LinkedHashMap<>();
    for (Subvol sv : getSubvols()) {
      if (sv.getState().isEmpty()) {
        continue;
      }
      Map<NSMEntity, Integer> takeOut1 = new LinkedHashMap<>();
      Iterator<Map.Entry<NSMEntity, Integer>> stateIt =
          sv.getState().entrySet().iterator();
      do {
        Map.Entry<NSMEntity, Integer> entry = stateIt.next();
        NSMEntity ent = entry.getKey();
        Integer amount = entry.getValue();
        int takeOutAmount = 0;
        for (Map.Entry<Subvol, Double> sve : sv.getNeighborhoodMap().entrySet()) {
          Subvol neigh = sve.getKey();
          if (neigh.getEnclosingEntity() == sv.getEnclosingEntity()) { // NOSONAR:
            // object identity intentional
            int transferAmount =
                (int) (ent.getDiffusionConstant() * stepInterval
                    * sve.getValue() * amount);
            if (!dropIn.containsKey(neigh)) {
              dropIn.put(neigh, new UpdateableAmountMap<NSMEntity>());
            }
            dropIn.get(neigh).update(ent, transferAmount);
            takeOutAmount += transferAmount;
          }
        }
        takeOut1.put(ent, -takeOutAmount);
      } while (stateIt.hasNext());
      takeOut.put(sv, UpdateableAmountMap.wrap(takeOut1, Integer.MIN_VALUE));
      sv.updateState(takeOut1);
    }
    for (Map.Entry<Subvol, IUpdateableMap<NSMEntity, Integer>> sve : dropIn
        .entrySet()) {
      Subvol sv = sve.getKey();
      IUpdateableMap<NSMEntity, Integer> updVec = sve.getValue();
      sv.updateState(updVec);
      if (!takeOut.containsKey(sv)) {
        takeOut.put(sv, updVec);
      } else {
        takeOut.get(sv).updateAll(updVec);
      }
    }
    return new StepRecord(takeOut);
  }

  @Override
  public Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>> handleExternalCollision(
      ISubvol source, ISubvol target, NSMEntity ent, int amount) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  @Override
  public void updateEventQueue(IMLSpaceEvent<Void> event,
      ISubvolEventRecord effect) {
    enqueueEvent(event, getTime() + stepInterval);
  }

  @Override
  public Collection<Subvol> getSubvols() {
    return subvols;
  }

  /**
   * Event queue that accepts only one single entry and does not support any
   * operations beyond those of
   * {@link org.jamesii.core.util.eventset.IBasicEventQueue}. Can be used, for
   * example, for discrete step-wise simulation in a framework that is usually
   * doing discrete-event simulation.
   * 
   * @author Arne Bittig
   * @date 14.10.2012
   * @param <E>
   *          Event type
   */
  static class SingleEntryEventQueue<E> implements IEventQueue<E, Double> {

    private static final long serialVersionUID = -6576618650667727210L;

    private Entry<E, Double> entry = null;

    @Override
    public Entry<E, Double> dequeue() {
      Entry<E, Double> retVal = entry;
      entry = null;
      return retVal;
    }

    @Override
    public void enqueue(E event, Double time) {
      if (entry != null) {
        throw new IllegalStateException();
      }
      entry = new Entry<>(event, time);
    }

    @Override
    public Double getMin() {
      return entry == null ? null : entry.getTime();
    }

    @Override
    public boolean isEmpty() {
      return entry == null;
    }

    @Override
    public int size() {
      return isEmpty() ? 0 : 1;
    }

    @Override
    public Double dequeue(E event) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<E> dequeueAll() {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<E> dequeueAll(Double time) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Map<E, Object> dequeueAllHashed() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Double getTime(E event) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void requeue(E event, Double newTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void requeue(E event, Double oldTime, Double newTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setSize(long size) {
      throw new UnsupportedOperationException();
    }

  }

  /**
   * Custom implementation of {@link ISubvolEventRecord} for changes to many
   * subvols at once
   * 
   * @author Arne Bittig
   * @date 14.10.2012
   */
  private static class StepRecord implements ISubvolEventRecord {

    private final Map<Subvol, ISubvolChange> subvolChanges;

    /**
     * @param subvolContentChanges
     *          State change "vector" for each changed sv
     */
    public StepRecord(
        Map<Subvol, IUpdateableMap<NSMEntity, Integer>> subvolContentChanges) {
      this.subvolChanges = new LinkedHashMap<>(subvolContentChanges.size());
      for (Map.Entry<Subvol, IUpdateableMap<NSMEntity, Integer>> e : subvolContentChanges
          .entrySet()) {
        subvolChanges.put(e.getKey(), new SubvolChange(e.getValue()));
      }
    }

    @Override
    public IShapedComponent getTriggeringComponent() {
      return null;
    }

    @Override
    public Collection<MLSpaceRule> getRules() {
      return Collections.emptyList();

    }

    @Override
    public Map<Subvol, ISubvolChange> getSubvolChanges() {
      return subvolChanges;
    }

    @Override
    public Map<SpatialEntity, ICompChange> getEnclosingEntityChange() {
      return Collections.EMPTY_MAP;
    }

    @Override
    public boolean isSuccess() {
      return true;
    }

  }
}
