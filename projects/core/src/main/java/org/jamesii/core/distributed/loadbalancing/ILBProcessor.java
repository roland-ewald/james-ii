package org.jamesii.core.distributed.loadbalancing;

import java.io.Serializable;
import java.util.Set;

import org.jamesii.core.base.IEntity;

/**
 * Interface for a processor that supports load balancing. The methods in here
 * are either called by the local {@link ILoadBalancer}, or by a remote
 * {@link ILBProcessor}.
 * 
 * @author Roland Ewald
 * @author Simon Bartels
 * 
 */
public interface ILBProcessor extends IEntity {

  /**
   * Triggers the migration of model entities from one processor to another.
   * 
   * @param modelEntities
   *          set of model entities to be migrated
   * @param dest
   *          the destination processor
   * @return true if migration was accepted by the processor
   */
  boolean migrate(Set<Serializable> modelEntities, ILBProcessor dest);

  /**
   * Adds the received model entities to the processor.
   * 
   * @param source
   *          the source processor
   * @param entities
   * @return true if receival was accepted by this processor, if false, the
   *         entities need to remain on the source processor
   */
  boolean receive(ILBProcessor source, Set<Serializable> entities);

  /**
   * Two choice : first : Updates the position info regarding the given model
   * entities to the new host(JVM) at this processor. second:(the current
   * solution see the @param migratedModelEntities) Updates the position info of
   * the given simulator(processor) where the model embedded to the new
   * host(JVM,could be on the same physical machine or a different one )
   * 
   * This method needs top be invoked at all neighbours that need to update
   * their information after a migration was done.
   * 
   * @param migratedModelEntities
   *          the entities for which the information needs to be updated
   * @param newHost
   *          the delegation of the newHost the delegation of new host of the
   *          entities
   */
  void update(Set<Serializable> migratedModelEntities, ILBProcessor newHost);

  /**
   * Get all neighbour {@link ILBProcessor} instances. Only these are eligible
   * as migration targets.
   * 
   * @return set of neighbour processors.
   */
  Set<ILBProcessor> getNeighbours();

  /**
   * Sets a load balancer for this processor. Registers it as an observer.
   * 
   * @param loadBalancer
   *          the load balancer to be set
   */
  void setLoadBalancer(ILoadBalancer<?, ?> loadBalancer);

  /**
   * Gets the {@link ILoadBalancer} instance associated with this processor
   * 
   * @return the processor's load recorder
   */
  ILoadBalancer<?, ?> getLoadBalancer();

}
