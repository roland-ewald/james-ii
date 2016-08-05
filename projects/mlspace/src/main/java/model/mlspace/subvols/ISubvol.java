/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.subvols;

import java.util.Map;

import model.mlspace.entities.NSMEntity;
import model.mlspace.entities.spatial.SpatialEntity;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.util.collection.IUpdateableMap;

/**
 * @author Arne Bittig
 * @date Mar 20, 2012
 */
public interface ISubvol extends IShapedComponent {

  @Override
  SpatialEntity getEnclosingEntity();

  /** @return Map of entities -> total amount */
  Map<NSMEntity, Integer> getState();

  /**
   * clear internal state, i.e. all contained NSM-entities
   * 
   * @return Previous state
   */
  IUpdateableMap<NSMEntity, Integer> clearState();

  /**
   * Update amount of single entity (delegate)
   * 
   * @param ent
   *          Entity whose amount to change
   * @param uval
   *          How much to change it
   * @return new amount
   * 
   * @see org.jamesii.core.util.collection.UpdateableAmountMap#update(Object,
   *      Integer)
   */
  Integer updateState(NSMEntity ent, Integer uval);

  /**
   * Update state (delegate)
   * 
   * @param updVec
   *          Map of entities -> amount of change
   * @see org.jamesii.core.util.collection.UpdateableAmountMap#updateAll(Map)
   */
  void updateState(Map<NSMEntity, Integer> updVec);

}