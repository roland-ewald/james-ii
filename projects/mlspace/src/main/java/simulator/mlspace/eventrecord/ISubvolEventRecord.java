/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.eventrecord;

import java.util.Map;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;
import model.mlspace.subvols.Subvol;
import simulator.mlspace.eventrecord.AbstractEventRecord.IChange;
import simulator.mlspace.eventrecord.IContSpaceEventRecord.ICompChange;

/**
 * Changes in continuous space simulation. This interface contains the querying
 * methods (i.e. getters) for information from continuous space event records,
 * which is organized in chain form: The scheduled event triggers one event
 * (e.g. a move), which then contains references to events resulting from the
 * first (e.g. transfers, collision reactions,...).
 *
 * @author Arne Bittig
 * @date 05.10.2012
 */
public interface ISubvolEventRecord extends IEventRecord {

  /**
   * @return Subvols that changed
   */
  Map<Subvol, ISubvolChange> getSubvolChanges();

  /**
   * @return Changes to enclosing comp of subvol (empty or singleton map)
   */
  Map<SpatialEntity, ICompChange> getEnclosingEntityChange();

  /**
   * Interface for record of changes to a single Subvol
   * 
   * @author Arne Bittig
   * @date 12.10.2012
   */
  interface ISubvolChange extends IChange {

    /**
     * @return Subvol state change "vector"
     */
    Map<NSMEntity, Integer> getStateChange();

  }
}