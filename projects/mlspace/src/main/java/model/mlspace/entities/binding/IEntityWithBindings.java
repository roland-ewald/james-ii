/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.binding;

import java.util.Map;

import model.mlspace.entities.IEntity;

/**
 * @author Arne Bittig
 * @param <V>
 *          Value type of E (not used here, for type safety w.r.t. E only)
 * @date 19.07.2012
 */
public interface IEntityWithBindings<V> extends IEntity<V> {

  /**
   * Check whether this entity includes information regarding the binding site
   * of the given name
   * 
   * @param name
   *          Binding site name
   * @return true if binding site state is specified
   */
  boolean hasBindingSite(String name);

  /**
   * Get information on binding site's current occupant (entity actually bound
   * or template entity for matching)
   * 
   * @param name
   *          Binding site name
   * @return Entity bound at given site, or null if free
   * @throws IllegalArgumentException
   *           if nothing about site of this name is specified
   */
  IEntityWithBindings<V> getBoundEntity(String name);

  /**
   * Check whether anything is actually bound to any binding site. Should always
   * have the opposite result as {@link #bindingEntries()}.isEmpty()}.
   * 
   * @return true iff any binding site is occupied
   */
  boolean hasBoundEntities();

  /**
   * @return Binding sites and bound entities (not for modification)
   */
  Map<String, ? extends IEntityWithBindings<?>> bindingEntries();
}