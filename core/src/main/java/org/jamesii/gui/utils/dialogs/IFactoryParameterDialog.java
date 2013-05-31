/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.dialogs;

import java.awt.Window;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * This dialog is intended to be the super class of all dialogs that provide
 * custom factories with input from the user (e.g., the model and experiment
 * reader/writers).
 * 
 * @author Roland Ewald
 * @param <X>
 *          the factory the dialog supports
 */
public interface IFactoryParameterDialog<X extends Factory> {

  /**
   * Get factory parameter from this dialog.
   * 
   * @param parentWindow
   *          the parent component, so that dialogs can be displayed properly
   * @return pair of factory parameter object and generic parameter block, null
   *         if user cancelled the action
   */
  Pair<ParameterBlock, X> getFactoryParameter(Window parentWindow);

  /**
   * Get string description for menu.
   * 
   * @return string description (e.g., for dialog's menu)
   */
  String getMenuDescription();
}
