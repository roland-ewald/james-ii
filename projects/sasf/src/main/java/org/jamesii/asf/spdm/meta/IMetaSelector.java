/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.meta;


import java.io.Serializable;
import java.util.List;

import org.jamesii.asf.registry.selection.SelectorManager;
import org.jamesii.core.parameters.ParameterBlock;


/**
 * Interface for meta-selectors. They operate on a list of selector managers
 * they may choose from, all of which are applicable to the problem (as assured
 * by the {@link org.jamesii.asf.registry.selection.SelectorEnsemble}).
 * 
 * @see org.jamesii.asf.registry.selection.SelectorEnsemble
 * @see org.jamesii.asf.spdm.generators.ISelector
 * @see SelectorManager
 * 
 * @author Roland Ewald
 */
public interface IMetaSelector extends Serializable {

  /**
   * Re-orders the set of selector managers so that the preferred ones are at
   * the beginning.
   * 
   * @param selectorManagers
   *          the unsorted list of selector managers
   * @param parameters
   *          the parameters describing the selection problem
   * 
   * @return the ordered list of selector managers
   */
  List<SelectorManager> chooseSelectorManager(
      List<SelectorManager> selectorManagers, ParameterBlock parameters);

}
