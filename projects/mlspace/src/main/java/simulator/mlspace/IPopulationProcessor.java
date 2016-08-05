/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import model.mlspace.entities.NSMEntity;
import model.mlspace.rules.MLSpaceRule;
import model.mlspace.subvols.ISubvol;
import model.mlspace.subvols.Subvol;

import org.jamesii.core.util.misc.Quadruple;

import simulator.mlspace.event.IMLSpaceEvent;
import simulator.mlspace.eventrecord.ISubvolEventRecord;

/**
 * Common interface for simulators handling populations of model entities in
 * spatial subunits. Contains all methods that a {@link AbstractHybridProcessor
 * hybrid processor} uses, which makes some overlap with methods already defined
 * by {@link AbstractMLSpaceProcessor} unavoidable.
 * 
 * @author Arne Bittig
 * @param <E>
 *          Type of processed event
 * @date 14.10.2012
 */
public interface IPopulationProcessor<E extends IMLSpaceEvent<?>> {

  /**
   * Handle event (if possible; usually dispatch to appropriate separate method)
   * 
   * @param event
   *          IMLSpaceEvent<?> to handle
   * @return Effect of event (may be null if no handling possible)
   */
  ISubvolEventRecord handleEvent(E event);

  /**
   * Update the event queue entries for model components affected by the recent
   * event handling (see {@link #handleEvent(IMLSpaceEvent)})
   * 
   * @param event
   *          Recently handled event
   * @param effect
   *          Effect of recently handled event
   */
  void updateEventQueue(E event, ISubvolEventRecord effect);

  /**
   * Handle collision of one or several dimensionless entities (of same type)
   * with a spatial entity to which a neighboring subvol belongs (resulting from
   * a regular diffusion attempt -- amount will the usually be 1 -- or, in
   * hybrid simulation, a move of a compartment over one or both of the involved
   * subvols). This can mean moving the entities over the boundary between the
   * two subvols or applying an entity-consuming second-order reaction rule
   * triggered by the collision with the respective spatial entity.
   * 
   * The second argument is of type {@link ISubvol} for applicability to state
   * container dummies in the hybrid simulator, and this is the reason for not
   * returning an {@link ISubvolEventRecord} directly.
   * 
   * @param source
   *          Source subvol
   * @param target
   *          Target subvol
   * @param ent
   *          Entity to transfer over boundary
   * @param amount
   *          Amount of ent to transfer
   * @return Source change, target change, change to surrounding entities (empty
   *         map if none/transfer only), actually applied transfer rules
   */
  Quadruple<Map<NSMEntity, Integer>, Map<NSMEntity, Integer>, Map<String, Object>, List<? extends MLSpaceRule>> handleExternalCollision(
      ISubvol source, ISubvol target, NSMEntity ent, int amount);

  /**
   * helper method for observers (not every subclass needs to have an
   * appropriate field)
   * 
   * @return Subvols in the current system state
   */
  Collection<Subvol> getSubvols();
}
