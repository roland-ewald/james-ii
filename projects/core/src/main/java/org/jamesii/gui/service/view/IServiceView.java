/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.service.view;

import org.jamesii.gui.application.IWindow;

/**
 * Basic interface for all views that display information about a
 * {@link org.jamesii.core.services.IService}.
 * 
 * @author Stefan Leye
 * 
 */
public interface IServiceView extends IWindow {

  /**
   * Registers observers at the service to allow dynamic display of
   * informations.
   */

  void setupObservers();
}
