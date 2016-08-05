/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.eventrecord;

import java.util.Collection;

import model.mlspace.rules.MLSpaceRule;

import org.jamesii.core.math.geometry.IShapedComponent;

/**
 * Common interface for recording changes to spatial components of an ML-Space
 * simulation (compartments & subvolumes)
 * 
 * @author Arne Bittig
 */
public interface IEventRecord {

  /**
   * @return whether record is of success or failed attempt
   */
  boolean isSuccess();

  /**
   * @return Model component that triggered the event
   */
  IShapedComponent getTriggeringComponent();

  /**
   * @return Applied rules related to the changes
   */
  Collection<? extends MLSpaceRule> getRules();

}