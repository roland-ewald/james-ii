/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.util.collection.ArrayMap;
import org.jamesii.core.util.collection.IUpdateableMap;
import org.jamesii.core.util.collection.UpdateableAmountMap;
import org.jamesii.core.util.eventset.IEventQueue;
import org.jamesii.core.util.hierarchy.IHierarchy;
import org.jamesii.core.util.misc.Quadruple;

import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.eventrecord.HybridEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;
import simulator.mlspace.eventrecord.ISubvolEventRecord;
import simulator.mlspace.eventrecord.SubvolEventRecord;

/**
 * Base class for hybrid simulator, composed of a continuous-space and a
 * (compartment-aware) next-subvolume-method processor, and implementations of
 * {@link AbstractMLSpaceProcessor#handleEvent(IMLSpaceEvent)} and
 * {@link AbstractMLSpaceProcessor#updateEventQueue(IMLSpaceEvent, simulator.mlspace.eventrecord.IEventRecord)
 * )} that call the respective methods of the contained processors and in the
 * former case, if required, the (here abstract) method
 * {@link #handleHybridEffect(simulator.mlspace.eventrecord.IContSpaceEventRecord)}
 * for the actual operations related to hybrid simulation.
 * 
 * Also provides a method for trying to transfer the content of an entire Subvol
 * over a boundary into a neighboring subvol (using
 * {@link NCMProcessor#handleExternalCollision(ISubvol, ISubvol, NSMEntity, int)}
 * ), which is the only place where a hybrid processor needs access to the
 * contained {@link NCMProcessor}.
 * 
 * @author Arne Bittig
 * @param <SE>
 *          Spatial event class
 * @param <PE>
 *          Population event class
 */
public abstract class AbstractHybridProcessor<SE extends IMLSpaceEvent<?>, PE extends IMLSpaceEvent<?>>
    extends AbstractMLSpaceProcessor<IMLSpaceEvent<?>, HybridEventRecord> {

  private static final long serialVersionUID = -1260069496508811008L;

  private final AbstractMLSpaceProcessor<SE, IContSpaceEventRecord> contProc;

  private final IPopulationProcessor<PE> nsmProc;

  private final Class<SE> seClass;

  private final Class<PE> popEvClass;

  protected AbstractHybridProcessor(
      AbstractMLSpaceProcessor<SE, IContSpaceEventRecord> contProc,
      Class<SE> spatialEventClass, IPopulationProcessor<PE> nsmProc,
      Class<PE> populationEventClass,
      IEventQueue<IMLSpaceEvent<?>, Double> eventQueue, Timer timer) {
    super(contProc.getModel(), eventQueue, contProc.getRand(), timer);
    this.seClass = spatialEventClass;
    this.popEvClass = populationEventClass;
    this.contProc = contProc;
    this.nsmProc = nsmProc;
  }

  @Override
  public HybridEventRecord handleEvent(IMLSpaceEvent<?> event) {
    Class<? extends IMLSpaceEvent<?>> eventClass =
        (Class<? extends IMLSpaceEvent<?>>) event.getClass();
    if (seClass.isAssignableFrom(eventClass)) {
      @SuppressWarnings("unchecked")
      IContSpaceEventRecord contEffect = contProc.handleEvent((SE) event);
      if (contEffect.getState().isSuccess()) {
        return handleHybridEffect(contEffect);
      } else {
        return new HybridEventRecord(contEffect);
      }
    } else if (popEvClass.isAssignableFrom(eventClass)) {
      @SuppressWarnings("unchecked")
      ISubvolEventRecord nsmEffect = nsmProc.handleEvent((PE) event);
      // TODO: NSM event triggering spatial entity creation?!
      return new HybridEventRecord(nsmEffect);
    }
    // default:
    else {
      unknownEventWarning(event);
      return null;
    }
  }

  /**
   * Hybrid-simulation-related post-processing of a processed continuous space
   * event, i.e. handling of a compartment's move's effect on the NSM level
   * 
   * @param contEffect
   *          Continuous-space effect (may involve several comp moves)
   * @return Hbyrid effect (wrapped contEffect plus affected Subvols and
   *         possibly more applied rules)
   */
  protected abstract HybridEventRecord handleHybridEffect(
      IContSpaceEventRecord contEffect);

  @SuppressWarnings("unchecked")
  @Override
  public void updateEventQueue(IMLSpaceEvent<?> event, HybridEventRecord effect) {
    if (effect.isWrappedSubvolRecord()) {
      nsmProc.updateEventQueue((PE) event, effect);
      Map<SpatialEntity, ICompChange> entityChange =
          effect.getEnclosingEntityChange();
      if (!entityChange.isEmpty()) {
        Map.Entry<SpatialEntity, ICompChange> entry =
            entityChange.entrySet().iterator().next();
        // inelegant but necessary cast; maybe the generification of the whole
        // class is not as elegant anymore as originally planned
        ((AbstractContinuousSpaceProcessor) contProc).updateChangeEvent(event,
            entry.getKey(), entry.getValue());
      }
    } else {
      contProc.updateEventQueue((SE) event, effect);
      nsmProc.updateEventQueue(null, effect);
    }
  }

  /**
   * Try to move all entities in one Subvol into a neighboring one that belongs
   * to a different enclosing compartment by invoking
   * {@link NCMProcessor#handleBoundaryCollision(ISubvol, ISubvol, NSMEntity, int, TransferDirection)}
   * on every entry of the source subvol's state vector. (Accesses the NCM
   * processor, which is not used otherwise for handling of hybrid events, and
   * is in the AbstractHybridProcessor because of that.)
   * 
   * The participating Subvol's enclosing comps are unchanged.
   * 
   * @param source
   *          Source subvol
   * @param target
   *          Target subvol
   * @return Applied transfer rules (e.g. for use in an
   *         {@link simulator.mlspace.eventrecord.IEventRecord})
   */
  protected SubvolEventRecord moveSubvolContentInto(Subvol source,
      ISubvol target) {
    Map<NSMEntity, Integer> sourceState = source.getState();
    List<MLSpaceRule> allAppliedRules = new ArrayList<>();
    Map<NSMEntity, Integer> oldSourceState = new ArrayMap<>(sourceState.size());
    // copy to avoid ConcurrentModificationException
    oldSourceState.putAll(sourceState);
    IUpdateableMap<NSMEntity, Integer> sourceChange =
        new UpdateableAmountMap<>(1, Integer.MIN_VALUE, Integer.MAX_VALUE);
    for (Map.Entry<NSMEntity, Integer> sse : oldSourceState.entrySet()) {
      Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, ?, List<? extends MLSpaceRule>> quadruple =
          nsmProc.handleExternalCollision(source, target, sse.getKey(),
              sse.getValue());
      sourceChange.updateAll(quadruple.getA());
      allAppliedRules.addAll(quadruple.getD());
      // if (quadruple.getC() != null) {
      // for (AbstractModelEntity ent : quadruple.getC().getAttMods().keySet())
      // {
      // affectedComps.add((SpatialEntity) ent);
      // } // TODO: proper incorporation of changes into
      // // SubvolEventRecord
      // }
    }
    return new SubvolEventRecord(source, sourceChange, null,
        Collections.<NSMEntity, Integer> emptyMap(), allAppliedRules);
    // TODO: comps crossed? (i.e. replace null between map and rules)
  }

  @Override
  public IHierarchy<SpatialEntity> getSpatialEntities() {
    return contProc.getSpatialEntities();
  }

  @Override
  public Collection<Subvol> getSubvols() {
    return nsmProc.getSubvols();
  }

  @Override
  public String toString() {
    return super.toString() + " using " + contProc + " and " + nsmProc;
  }

}
