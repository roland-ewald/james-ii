/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * A simple description mechanism for {@link IRuntimeConfiguration}, to be used
 * by sub-classes of the {@link AbstractPerformanceExtractor}. Lets the user
 * define a list of base factories, runtime descriptions will then be described
 * by their concrete factories, in the order of the given base factories.
 * 
 * @see AbstractPerformanceExtractor
 * 
 * @author Roland Ewald
 */
public class DefaultConfigurationDescriptor implements IRTCDescriptor {

  /** The factory classes. */
  private final List<Class<? extends Factory<?>>> baseFactoryClasses;

  /** Boolean flag to decide whether other factories shall be filtered. */
  private final boolean filterOthers;

  /** The map from factories to their parameters. */
  private final Map<Class<? extends Factory<?>>, List<String>> factoryParametersMap;

  /**
   * Instantiates a new default configuration descriptor.
   * 
   * @param bFactoryClasses
   *          the base factory classes
   * @param filter
   *          the flag for filtering other algorithms
   */
  public DefaultConfigurationDescriptor(
      List<Class<? extends Factory<?>>> bFactoryClasses, boolean filter) {
    this(bFactoryClasses, null, filter);
  }

  /**
   * Instantiates a new default configuration descriptor.
   * 
   * @param bFactoryClasses
   *          the b factory classes
   * @param factoryParameters
   *          the factory parameters
   * @param filter
   *          the filter
   */
  public DefaultConfigurationDescriptor(
      List<Class<? extends Factory<?>>> bFactoryClasses,
      Map<Class<? extends Factory<?>>, List<String>> factoryParameters,
      boolean filter) {
    baseFactoryClasses = bFactoryClasses;
    filterOthers = filter;
    factoryParametersMap =
        factoryParameters == null ? new HashMap<Class<? extends Factory<?>>, List<String>>()
            : factoryParameters;
  }

  /**
   * Instantiates a new default configuration descriptor.
   * 
   * @param bFactoryClasses
   *          the b factory classes
   * @param filter
   *          the flag for filtering other algorithms
   */
  public DefaultConfigurationDescriptor(Class<?>[] bFactoryClasses,
      boolean filter) {
    this(bFactoryClasses,
        new HashMap<Class<? extends Factory<?>>, List<String>>(), filter);
  }

  /**
   * Instantiates a new default configuration descriptor.
   * 
   * @param bFactoryClasses
   *          the b factory classes
   * @param factoryParameters
   *          factory parameters to be listed as well
   * @param filter
   *          the flag for filtering other algorithms
   */
  @SuppressWarnings("unchecked")
  public DefaultConfigurationDescriptor(Class<?>[] bFactoryClasses,
      Map<Class<? extends Factory<?>>, List<String>> factoryParameters,
      boolean filter) {
    filterOthers = filter;
    factoryParametersMap = factoryParameters;
    baseFactoryClasses = new ArrayList<>();
    for (Class<?> factoryClass : bFactoryClasses) {
      baseFactoryClasses.add((Class<? extends Factory<?>>) factoryClass);
    }
  }

  @Override
  public String[] describe(IRuntimeConfiguration config) {

    List<Class<? extends Factory<?>>> facs =
        config.getSelectionTree().getUniqueFactories();

    Collections.sort(facs, new Comparator<Class<?>>() {
      @Override
      public int compare(Class<?> o1, Class<?> o2) {
        for (Class<?> basefactoryClass : baseFactoryClasses) {
          if (basefactoryClass.isAssignableFrom(o1)) {
            return -1;
          }
          if (basefactoryClass.isAssignableFrom(o2)) {
            return 1;
          }
        }
        return 0;
      }
    });

    List<String> result = createDescription(config, facs);
    return result.toArray(new String[result.size()]);
  }

  /**
   * Creates the description.
   * 
   * @param config
   *          the config
   * @param facs
   *          the facs
   * 
   * @return the list< string>
   */
  List<String> createDescription(IRuntimeConfiguration config,
      List<Class<? extends Factory<?>>> facs) {
    filter(facs);

    List<String> result = new ArrayList<>();
    for (Class<? extends Factory<?>> fac : facs) {
      result.add(Strings.dispClassName(fac));
      result.addAll(getParametersForFactory(config.getSelectionTree(), fac));

    }
    return result;
  }

  /**
   * Gets the parameters for factory.
   * 
   * TODO: This simple linear search does not work when two or more algorithms
   * of the same type have been selected with different parameters.
   * 
   * @param selectionTree
   *          the selection tree
   * @param factory
   *          the factory
   * @param paramNames
   *          the parameter names
   * 
   * @return the parameters for the factory
   */
  protected List<String> getParametersForFactory(SelectionTree selectionTree,
      Class<? extends Factory<?>> factory) {

    SelectedFactoryNode selFacNode =
        searchSelectedFactoryNode(selectionTree, factory);

    if (selFacNode == null) {
      throw new IllegalArgumentException("Factory '" + factory
          + "' was not found!");
    }

    ParameterBlock paramBlock = selFacNode.getParameter();
    if (paramBlock == null) {
      paramBlock = new ParameterBlock();
    }

    return compileResults(
        factoryParametersMap.get(SimSystem.getRegistry()
            .getBaseFactoryForAbstractFactory(
                selFacNode.getAbstractFactoryClass())), paramBlock);
  }

  /**
   * Search selected factory node.
   * 
   * @param selectionTree
   *          the selection tree
   * @param factory
   *          the factory
   * 
   * @return the selected factory node
   */
  private SelectedFactoryNode searchSelectedFactoryNode(
      SelectionTree selectionTree, Class<? extends Factory<?>> factory) {
    SelectedFactoryNode selFacNode = null;
    for (SelectedFactoryNode node : selectionTree.getVertices()) {
      if (node.getFactoryClass() != null
          && node.getFactoryClass().equals(factory)) {
        selFacNode = node;
        break;
      }
    }
    return selFacNode;
  }

  /**
   * Compile results.
   * 
   * @param paramNames
   *          the parameter names
   * @param paramBlock
   *          the parameter block
   * 
   * @return the list of parameter values
   */
  private List<String> compileResults(List<String> paramNames,
      ParameterBlock paramBlock) {
    List<String> result = new ArrayList<>();
    if (paramNames == null) {
      return result;
    }

    for (String paramName : paramNames) {
      Object paramValue = paramBlock.getSubBlockValue(paramName);
      if (paramValue == null) {
        result.add("");
      } else {
        result.add(paramValue.toString());
      }
    }
    return result;
  }

  /**
   * Filters factory list so that only user-defined factories remain.
   * 
   * @param factoryClasses
   *          the factory list
   */
  private void filter(List<Class<? extends Factory<?>>> factoryClasses) {

    if (!filterOthers) {
      return;
    }

    Set<Class<? extends Factory<?>>> filtered =
        new HashSet<>();
    for (Class<? extends Factory<?>> factoryClass : factoryClasses) {
      if (filtered.contains(factoryClass)) {
        continue;
      }
      boolean filter = true;
      for (Class<? extends Factory<?>> baseFactoryClass : baseFactoryClasses) {
        if (baseFactoryClass.isAssignableFrom(factoryClass)) {
          filter = false;
          break;
        }
      }
      if (filter) {
        filtered.add(factoryClass);
      }
    }

    for (Class<?> facToBeFiltered : filtered) {
      factoryClasses.remove(facToBeFiltered);
    }
  }

  @Override
  public String[] getHeader() {
    List<String> header = new ArrayList<>();
    for (Class<? extends Factory<?>> factory : baseFactoryClasses) {
      header.add(Strings.dispClassName(factory));
      addHeaderElementsForSubFactories(header, factory);
    }
    return header.toArray(new String[header.size()]);
  }

  /**
   * Adds the header elements for sub factories.
   * 
   * @param header
   *          the header
   * @param baseFactory
   *          the base factory
   */
  private void addHeaderElementsForSubFactories(List<String> header,
      Class<? extends Factory<?>> baseFactory) {
    if (factoryParametersMap.containsKey(baseFactory)) {
      for (String paramName : factoryParametersMap.get(baseFactory)) {
        header.add(paramName);
      }
    }
  }
}
