/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.selection;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.ISelector;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * Component that manages a selector and defines its scope. In other words, the
 * manager wraps a selector for the algorithm selection registry and defines to
 * which problems it is applicable.
 * 
 * @see SelectorEnsemble
 * 
 * @author Roland Ewald
 */
public abstract class SelectorManager implements Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(SelectorManager.class,
        new IConstructorParameterProvider<SelectorManager>() {
          @Override
          public Object[] getParameters(SelectorManager oldInstance) {
            return new Object[] { oldInstance.getSelector() };
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2668152755308252815L;

  /** The selector to be managed. */
  private final ISelector selector;

  /**
   * Instantiates a new selector manager.
   * 
   * @param managedSelector
   *          the managed selector
   */
  public SelectorManager(ISelector managedSelector) {
    selector = managedSelector;
  }

  /**
   * Uses selector to generate list of configurations for given parameter block.
   * 
   * @param parameters
   *          the parameters
   * 
   * @return the list< configuration>
   */
  public List<SelectionTree> useSelector(ParameterBlock parameters) {
    Features features = extractFeatures(parameters);
    List<Configuration> configurations =
        selector.selectConfigurations(features);
    return retrieveTrees(configurations);
  }

  /**
   * Checks whether selector applies to given parameters.
   * 
   * @param parameters
   *          the parameters
   * 
   * @return true, if applicable
   */
  public abstract boolean appliesTo(ParameterBlock parameters);

  /**
   * Retrieve selection trees from configurations.
   * 
   * @param configurations
   *          the configurations
   * 
   * @return the list< selection tree>
   */
  protected List<SelectionTree> retrieveTrees(List<Configuration> configurations) {
    List<SelectionTree> selectionTrees = new ArrayList<>();
    for (Configuration configuration : configurations) {
      if (configuration.getSelectionTree() != null) {
        selectionTrees.add(configuration.getSelectionTree());
      }
    }
    return selectionTrees;
  }

  /**
   * Extract features from current problem, as defined by the parameter block.
   * 
   * @param parameters
   *          the parameter block
   * 
   * @return the features
   */
  protected abstract Features extractFeatures(ParameterBlock parameters);

  /**
   * Gets the selector.
   * 
   * @return the selector
   */
  public ISelector getSelector() {
    return selector;
  }

}
