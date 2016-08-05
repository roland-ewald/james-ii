/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace;

import java.util.Collection;
import java.util.Map;

import model.mlspace.entities.InitEntity;

import org.jamesii.core.math.geometry.IShapedComponent;

/**
 * In a hybrid simulation, one part (e.g. the continuous one) sometimes needs to
 * to access the other (e.g. when a compartment reaction produces non-spatial
 * entities). Relevant methods for notifying another part of something that
 * concerns it are encapsulated in this interface.
 * 
 * A {@link java.lang.NullPointerException} of a {@link IHybridReactionHandler}
 * variable may points to
 * <ul>
 * <li>a not properly initialized hybrid simulation, or
 * <li>an improper rule in a non-hybrid simulation (e.g. creation of non-spatial
 * entities in continuous-space-only simulation)
 * </ul>
 * 
 * @author Arne Bittig
 * @param <C>
 *          Type of spatial component triggering hybrid processing (compartment
 *          or subvolume)
 * @date Apr 12, 2012
 */
public interface IHybridReactionHandler<C extends IShapedComponent> {

  /**
   * Record pending production of entity of a different kind (i.e. not the kind
   * that triggered the actual event that lead to the production)
   * 
   * @param nonSpatial
   *          Template for entities to produce
   * @param inComp
   *          Spatial component triggering production
   * @param nearComp
   *          Near spatial component (optional; null if not relevant)
   * @param env
   *          Local variables
   * @return Record wrapping the parameters
   */
  HybridRecord<C> recordHybridEntityProduction(
      Collection<InitEntity> nonSpatial, C inComp, C nearComp,
      Map<String, Object> env);

  // /** TODO: what was this method for? Shouldn't there be the possiblity of
  // rollback? (commented out when found to never be called)
  // * Forget previous call to
  // * {@link #recordHybridEntityProduction(Collection, IShapedComponent,
  // IShapedComponent)}
  // * and its effect on the internal state (e.g. in case of a rollback)
  // *
  // * @param record
  // * {@link HybridRecord} to forget
  // * @throws IllegalStateException
  // * if record does not exist (e.g. because the specified changes
  // * where already applied and hence deleted from the pending
  // * records)
  // */
  // void forgetRecord(HybridRecord<C> record);

  /**
   * Record to be used internally and for identification
   * 
   * @author Arne Bittig
   * @date 29.06.2012
   * @param <C>
   *          Type of spatial component
   */
  class HybridRecord<C extends IShapedComponent> {

    /** Entities to be produced */
    private final Collection<InitEntity> pEnts;

    /** Spatial component in which to produce */
    private final C inComp;

    private final C nearComp;

    private final Map<String, Object> env;

    /**
     * @param pEnts
     *          Entities to be produced
     * @param inComp
     *          Spatial entity in which to place pEnts
     * @param nearComp
     *          Spatial enitiy near which to place pEnts
     * @param env
     *          Local variables
     */
    public HybridRecord(Collection<InitEntity> pEnts, C inComp, C nearComp,
        Map<String, Object> env
    /* , MLSpaceRule react */) {
      this.pEnts = pEnts;
      this.inComp = inComp;
      this.nearComp = nearComp;
      this.env = env;
    }

    /**
     * @return Entities to be produced
     */
    public final Collection<InitEntity> getEntiesToProduce() {
      return pEnts;
    }

    /**
     * @return Local variables potentially relevant for produced entities
     *         attributes
     */
    public Map<String, Object> getEnv() {
      return env;
    }

    /**
     * @return Spatial component in which to produce
     */
    public final C getInComp() {
      return inComp;
    }

    /**
     * @return the nearComp
     */
    public final C getNearComp() {
      return nearComp;
    }

  }

}
