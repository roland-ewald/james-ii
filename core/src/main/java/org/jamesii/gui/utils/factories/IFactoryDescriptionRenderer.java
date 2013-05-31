/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.factories;

import org.jamesii.core.factories.Factory;

/**
 * Determines the way a factory and additional information are displayed.
 * 
 * TODO: Should be generalised to components at some point.
 * 
 * @param <F>
 *          the type of the factory
 * @author Roland Ewald
 */
public interface IFactoryDescriptionRenderer<F extends Factory> {

  /**
   * Gets the description of the factory for the user.
   * 
   * @param factory
   *          the factory
   * 
   * @return the description
   */
  String getDescription(F factory);

}
