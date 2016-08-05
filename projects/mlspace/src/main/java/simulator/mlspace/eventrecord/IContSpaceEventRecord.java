/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.eventrecord;

import java.util.Collection;
import java.util.Map;

import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.rules.MLSpaceRule;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;

import simulator.mlspace.eventrecord.AbstractEventRecord.IChange;

/**
 * Changes in continous space simulation. This interface contains the querying
 * methods (i.e. getters) for information from continous space event records,
 * which is organized in chain form: The scheduled event triggers one event
 * (e.g. a move), which then contains references to events resulting from the
 * first (e.g. transfers, collision reactions,...).
 *
 * TODO: move CompChange here or extract interface
 *
 * @author Arne Bittig
 * @date 04.10.2012
 */
public interface IContSpaceEventRecord extends IEventRecord {

  /**
   * State of the effects in the record (performed successfully, or attempted
   * and failed, potentially including rollback)
   * 
   * @author Arne Bittig
   * @date 01.10.2012
   */
  public static enum State {
    /**
     * Event did not actually lead to any effect, but failed due to some
     * contraints
     */
    FAILED(false),
    /**
     * Event lead to recorded effect
     */
    SUCCESS(true);

    private final boolean success;

    State(boolean success) {
      this.success = success;
    }

    /**
     * @return boolean value corresponding to success state
     */
    public boolean isSuccess() {
      return success;
    }
  }

  /**
   * Does this record belong to a failed attempt or an actual effect?
   * 
   * @return {@link State#FAILED} of {@link State#SUCCESS}
   */
  State getState();

  /**
   * @return Spatial entities that changed and summary of changes
   */
  Map<SpatialEntity, ICompChange> getCompChanges();

  /**
   * @return Numeric information (usually move attempts)
   */
  int getNumInfo();

  /**
   * @return Effects triggered by this one (empty list if none)
   */
  Collection<IContSpaceEventRecord> getTriggeredEffects();

  /**
   * Get moves of spatial entities in this record. The result may be extracted
   * from all changes present and then cached, i.e. calculations need not be
   * repeated for repeated calls to this method. Thus, the method must not be
   * called when triggered effects or any other changes may still be added (to
   * this one or one directly or indirectly triggered).
   * 
   * @return Moved entities and the respective position update
   */
  Map<SpatialEntity, IDisplacementVector> getCompMoves();

  /**
   * Get newly created spatial entities. The result may be extracted from all
   * changes present and then cached, i.e. calculations need not be repeated for
   * repeated calls to this method. Thus, the method must not be called when
   * triggered effects or any other changes may still be added (to this one or
   * one directly or indirectly triggered).
   * 
   * @return Collection of newly created spatial entities in this record.
   */
  Collection<SpatialEntity> getCompCreations();

  /**
   * Get newly created spatial entities. The result may be extracted from all
   * changes present and then cached, i.e. calculations need not be repeated for
   * repeated calls to this method. Thus, the method must not be called when
   * triggered effects or any other changes may still be added (to this one or
   * one directly or indirectly triggered).
   * 
   * @return Collection of dissolved spatial entities in this record.
   */
  Collection<SpatialEntity> getCompDestructions();

  /**
   * Interface for record of changes to a single spatial entity
   * 
   * @author Arne Bittig
   * @date 12.10.2012
   */
  interface ICompChange extends IChange {

    boolean spatialAttsChanged();

    Type getType();

    Map<String, Object> getNewAtts();

    Map<String, Object> getOldAtts();

    IDisplacementVector getPosUpd();

    public static enum Type {
      UNCHANGED(0), MOVED_ONLY(1), ATTS_CHANGED_OR_CREATED(2), DESTROYED(3);

      private final int priority;

      Type(int priority) {
        this.priority = priority;
      }

      /**
       * @return Priority / severity of this type of change
       */
      public int getPriority() {
        return priority;
      }

    }

  }

  /**
   * @return Aggregated rules of effect and all triggered effects
   */
  Collection<? extends MLSpaceRule> getAllRules();

  /**
   * @return Aggregated entity changes of effect and all triggered effects
   */
  Map<SpatialEntity, ICompChange> getAllCompChanges();

}