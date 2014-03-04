/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.selection;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.registry.AlgoSelectionRegistry;
import org.jamesii.asf.spdm.meta.IMetaSelector;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.metadata.FailureTolerance;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * Class to manage the algorithm selection process, the algorithm selector
 * managers and also potential meta learners. Also identifies the problems to
 * which this learner is applicable.
 * 
 * @see SelectorManager
 * 
 * @author Roland Ewald
 * 
 */
public class SelectorEnsemble implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8616592827752258259L;

  /** The data ID for storage in the factory runtime data store. */
  public static final String DATA_ID = "SelectorEnsemble";

  /** The meta selector for the selectors. */
  private IMetaSelector metaSelector = null;

  /** The selector managers. */
  private final List<SelectorManager> selectorManagers =
      new ArrayList<>();

  /**
   * Select an algorithm by asking meta selector and selection managers.
   * 
   * @param parameters
   *          the parameter block
   * @param failureTolerance
   *          the failure tolerance
   * 
   * @return the parameter block
   */
  public ParameterBlock select(ParameterBlock parameters,
      FailureTolerance failureTolerance) {

    List<SelectorManager> applicableSelectorManagers =
        filterManagers(parameters);

    if (metaSelector != null) {
      applicableSelectorManagers =
          metaSelector.chooseSelectorManager(applicableSelectorManagers,
              parameters);
    }

    return selectFromManagers(parameters, applicableSelectorManagers);
  }

  /**
   * Selects most suitable parameter block from selector managers.
   * 
   * @param parameters
   *          the parameters
   * @param applSelManagers
   *          the applicable selector managers
   * 
   * @return the parameter block
   */
  protected ParameterBlock selectFromManagers(ParameterBlock parameters,
      List<SelectorManager> applSelManagers) {
    for (SelectorManager selectorManager : applSelManagers) {
      ParameterBlock result = selectFromManager(parameters, selectorManager);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * Select best configuration from a given selector manager.
   * 
   * @param parameters
   *          the parameters
   * @param selectorManager
   *          the selector manager
   * 
   * @return the parameter block
   */
  protected ParameterBlock selectFromManager(ParameterBlock parameters,
      SelectorManager selectorManager) {

    List<SelectionTree> configurations =
        selectorManager.useSelector(parameters);

    for (SelectionTree configuration : configurations) {
      if (isAvailable(configuration)) {
        return configuration.toParamBlock();
      }
    }

    return null;
  }

  /**
   * Checks back if all factories specified in a selection tree are available
   * via the registry and are eligible for selection.
   * 
   * @param configuration
   *          the potential configuration
   * 
   * @return true if all factories contained in this configuration are available
   */
  protected boolean isAvailable(SelectionTree configuration) {
    List<Class<? extends Factory<?>>> factories =
        configuration.getUniqueFactories();
    for (Class<? extends Factory<?>> factory : factories) {
      if (!AlgoSelectionRegistry.getRegistry().factoryAvailable(factory)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Filter out all managers that are applicable to the given problem, as
   * specified by the parameter block.
   * 
   * @param parameters
   *          the parameter block
   * 
   * @return the list of applicable selector managers
   */
  protected List<SelectorManager> filterManagers(ParameterBlock parameters) {
    List<SelectorManager> filteredManagers = new ArrayList<>();
    for (SelectorManager selectorManager : selectorManagers) {
      if (selectorManager.appliesTo(parameters)) {
        filteredManagers.add(selectorManager);
      }
    }
    return filteredManagers;
  }

  /**
   * Checks for meta-selector.
   * 
   * @return true, if meta selector available
   */
  public boolean hasMetaSelector() {
    return metaSelector != null;
  }

  public IMetaSelector getMetaSelector() {
    return metaSelector;
  }

  public void setMetaSelector(IMetaSelector metaSelector) {
    this.metaSelector = metaSelector;
  }

  public List<SelectorManager> getSelectorManagers() {
    return new ArrayList<>(selectorManagers);
  }

  public void setSelectorManagers(List<SelectorManager> selManagers) {
    this.selectorManagers.clear();
    this.selectorManagers.addAll(selManagers);
  }

  public void addSelectorManager(SelectorManager selectorManager) {
    selectorManagers.add(selectorManager);
  }

  public boolean removeSelectorManager(SelectorManager selectorManager) {
    return selectorManagers.remove(selectorManager);
  }

  public void clearSelectorManagers() {
    selectorManagers.clear();
  }
}
