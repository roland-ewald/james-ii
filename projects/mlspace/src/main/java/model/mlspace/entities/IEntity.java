/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

/**
 * ML-Space model entity with attributes (names and values)
 *
 * (interface extracted primarily to be extended by
 * {@link model.mlspace.entities.binding.IEntityWithBindings}, otherwise the
 * abstract classes could well be used instead)
 *
 * @author Arne Bittig
 * @date 22.10.2012
 * @param <V>
 *          Attribute value type
 */
public interface IEntity<V> {

  /**
   * Check whether entity has given attribute
   * 
   * @param name
   *          Name of the attribute
   * @return true if attribute map contains name as key
   */
  boolean hasAttribute(String name);

  /**
   * Get attribute value (as Object, will need casting)
   * 
   * @param name
   *          Attribute name
   * @return Attribute value (null if no attribute of this name)
   */
  V getAttribute(String name);

  /**
   * 
   * @return Species this entity belongs to
   */
  Species getSpecies();

}