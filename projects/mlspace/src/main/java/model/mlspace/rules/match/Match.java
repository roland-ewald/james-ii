/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.rules.match;

import java.util.Collection;
import java.util.Map;

import model.mlspace.entities.AbstractModelEntity;
import model.mlspace.rules.MLSpaceRule;

/**
 * Designated result of rule matching. If successful, the match should contain
 * enough information about the changes specified by the rule and the actual
 * entities to which it applies that only a call to {@link #apply()} to ... well
 * ... apply the attribute changes to the respective entities. Note that for
 * entities in population-based processing, changes to one entity should not be
 * applied directly (see {@link #applyReplacing(IReplacer)}).
 * 
 * @author Arne Bittig
 * @param <E>
 *          Model entity type
 * @date 07.11.2012
 */
public interface Match<E extends AbstractModelEntity> {

  /**
   * Match success (value returned by previous, less flexible matching methods).
   * If false, calling the other methods is not meaningful (except
   * {@link #getRate()}, which must return 0).
   * 
   * @return success value
   */
  boolean isSuccess();

  /**
   * Apply successfully matched rule to entities that were matched
   * 
   * @return Record of previous attribute values for potential rollback
   */
  SuccessfulMatch.ModRecord<E> apply();

  /**
   * Apply successfully matched rule to entities that were matched
   * 
   * @param rep
   *          Replacement generator
   * 
   * @return Record of previous attribute values for potential rollback
   */
  SuccessfulMatch.ModRecord<E> applyReplacing(IReplacer<? super E> rep);

  /**
   * Replacement generator. If an entity should not be modified directly (e.g.
   * in population-based simulation, where the same instance may be used to keep
   * track of the state of different spatial subunits), implementations of this
   * interface should handle the "cloning"/creation of copies which to use
   * instead.
   * 
   * @author Arne Bittig
   * @date 02.07.2014
   * 
   * @param <E>
   */
  interface IReplacer<E extends AbstractModelEntity> {

    /**
     * 
     * @param ent
     *          Entity
     * @return Replacement (copy, or original parameter if no replacement
     *         necessary)
     */
    E replace(E ent);
  }

  /**
   * Get local variables (a.k.a. environment)
   * 
   * @return Names and values of local variables (empty if none)
   */
  Map<String, Object> getEnv();

  /**
   * Get reaction probability or rate expression value
   * 
   * @return reaction probability or rate expression value
   */
  double getRate();

  /**
   * @return Matched rule, e.g. to get to-be produced entities
   */
  MLSpaceRule getRule();

  /**
   * @return to-be destroyed entities (destruction may require special handling)
   */
  Collection<E> getConsumed();
}