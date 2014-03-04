/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util.sampling;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.model.variables.QuantitativeBaseVariable;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.trees.Tree;
import org.jamesii.core.util.misc.Pair;

/**
 * A tree that defines a hierarchy on parameter vertices.
 * 
 * Note that not all corner cases defined in
 * {@link org.jamesii.asf.spdm.SelectionTreeFlattener} are covered yet (e.g. regarding
 * multiple plug-ins of the same kind on the same level).
 * 
 * @see FactoryParameterizationVertex
 * 
 * @author Roland Ewald
 * 
 */
public class ParameterizationTree extends
    Tree<FactoryParameterizationVertex, Edge<FactoryParameterizationVertex>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9215357626730409734L;

  /** Stores all factories for which parameters are defined in this tree. */
  private final Set<Class<? extends Factory<?>>> affectedFactories =
      new HashSet<>();

  /**
   * Stores the factories => parameters map to re-create similar trees. See
   * {@link ParameterizationTree#createTreeForParameters(ParameterBlock)}.
   */
  private final Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> factoryParameterMap =
      new HashMap<>();

  /** Holds the top-most parameter block. */
  private final ParameterBlock configuration;

  /**
   * Creates a parameter tree from a configuration.
   * 
   * @param factoryParametersMap
   *          the map from each factory to its parameters that shall be sampled
   * @param configuration
   *          the configuration
   * @return the parameterization tree
   */
  public ParameterizationTree(
      ParameterBlock configuration,
      Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> factoryParametersMap) {
    super(getInitialVertexList());
    setTreeRoot(this.getVertices().get(0));
    this.configuration = configuration;
    this.factoryParameterMap.putAll(factoryParametersMap);
    createTreeRecursively(configuration, createLookupMap(factoryParametersMap),
        getRoot());
  }

  /**
   * Creates the parameterization tree recursively.
   * 
   * @param configuration
   *          the configuration
   * @param factoryParametersMap
   *          the map from each factory to its parameters that shall be sampled
   * @param parent
   *          the parent in the tree
   */
  private void createTreeRecursively(
      ParameterBlock configuration,
      Map<String, Pair<Class<? extends Factory<?>>, Map<String, QuantitativeBaseVariable<?>>>> factoryParametersMap,
      FactoryParameterizationVertex parent) {

    List<String> subBlockNames =
        new ArrayList<>(configuration.getSubBlocks().keySet());
    Collections.sort(subBlockNames);

    for (String subBlockName : subBlockNames) {
      ParameterBlock subBlock = configuration.getSubBlock(subBlockName);
      Object subBlockValue = subBlock.getValue();
      if (factoryParametersMap.containsKey(subBlockValue)) {
        addParameterVertexForFactory(parent, subBlock, (String) subBlockValue,
            factoryParametersMap);
      } else {
        createTreeRecursively(subBlock, factoryParametersMap, parent);
      }
    }
  }

  /**
   * Adds the parameter vertex for factory.
   * 
   * @param parent
   *          the parent vertex
   * @param factoryParamBlock
   *          the factory parameter block
   * @param factoryClass
   *          the factory class
   * @param factoryParametersMap
   *          the factory parameters map
   */
  private void addParameterVertexForFactory(
      FactoryParameterizationVertex parent,
      ParameterBlock factoryParamBlock,
      String factoryClassName,
      Map<String, Pair<Class<? extends Factory<?>>, Map<String, QuantitativeBaseVariable<?>>>> factoryParametersMap) {

    Pair<Class<? extends Factory<?>>, Map<String, QuantitativeBaseVariable<?>>> parametersForFactory =
        factoryParametersMap.get(factoryClassName);

    FactoryParameterizationVertex newVertex =
        new FactoryParameterizationVertex(parametersForFactory.getFirstValue());
    affectedFactories.add(parametersForFactory.getFirstValue());
    addVertex(newVertex);
    addEdge(new Edge<>(newVertex, parent));

    createTreeRecursively(factoryParamBlock, factoryParametersMap, newVertex);

    registerParameters(newVertex, factoryParamBlock,
        parametersForFactory.getFirstValue(),
        parametersForFactory.getSecondValue());
  }

  /**
   * Register parameters for a given vertex. If declared parameter is not
   * specified, add new sub-block.
   * 
   * @param vertex
   *          the vertex to which the parameters belong
   * @param factoryParamBlock
   *          the factory parameter block that contains the parameters
   * @param factoryClass
   *          the factory class
   * @param parametersForFactory
   *          the parameters to be sampled for the factory
   */
  private void registerParameters(FactoryParameterizationVertex vertex,
      ParameterBlock factoryParamBlock,
      Class<? extends Factory<?>> factoryClass,
      Map<String, QuantitativeBaseVariable<?>> parametersForFactory) {

    for (Entry<String, QuantitativeBaseVariable<?>> parameterEntry : parametersForFactory
        .entrySet()) {

      String parameterName = parameterEntry.getKey();
      QuantitativeBaseVariable<?> parameter = parameterEntry.getValue();

      if (factoryParamBlock.hasSubBlock(parameterName)) {
        vertex.addParameter(parameter,
            factoryParamBlock.getSubBlock(parameterName));
      } else {
        SimSystem.report(Level.WARNING, "In parameter block '"
            + factoryParamBlock + "' for factory '" + factoryClass.getName()
            + "' there is no parameter '" + parameterName
            + "' defined --- adding it with default value of the variable: "
            + parameter.getValue());
        ParameterBlock newParameterSubBlock =
            factoryParamBlock.addSubBlock(parameterName, parameter.getValue());
        vertex.addParameter(parameter, newParameterSubBlock);
      }
    }
  }

  /**
   * Gets the initial list of tree vertices (only the root is defined).
   * 
   * @return the vertex list for initialization
   */
  private static List<FactoryParameterizationVertex> getInitialVertexList() {
    List<FactoryParameterizationVertex> vertices =
        new ArrayList<>();
    vertices.add(new FactoryParameterizationVertex());
    return vertices;
  }

  /**
   * Gets the factories affected by the available parameters.
   * 
   * @return the affected factories
   */
  public Set<Class<? extends Factory<?>>> getAffectedFactories() {
    return Collections.unmodifiableSet(affectedFactories);
  }

  /**
   * Counts parameters defined in this tree.
   * 
   * @return the number of parameters
   */
  public int countParameters() {
    int parameterCount = 0;
    for (FactoryParameterizationVertex vertex : getVertices()) {
      if (vertex.equals(getRoot())) {
        continue;
      }
      parameterCount += vertex.getParameterCount();
    }
    return parameterCount;
  }

  /**
   * Gets the parameter with the given index.
   * 
   * @param index
   *          the index
   * @return the parameter
   */
  public Pair<BaseVariable<?>, ParameterBlock> getParameter(int index) {
    int paramsInPreviousVertices = 0;
    for (FactoryParameterizationVertex vertex : getVertices()) {
      int indexForVertex = index - paramsInPreviousVertices;
      if (vertex.getParameterCount() <= indexForVertex) {
        paramsInPreviousVertices += vertex.getParameterCount();
      } else {
        return vertex.getParameter(indexForVertex);
      }
    }
    throw new IllegalArgumentException("Index '" + index
        + "' not defined, this tree only contains " + countParameters()
        + " parameters.");
  }

  /**
   * Creates a map for looking up factories and variables by name.
   * 
   * @param factoryParametersMap
   *          the mapping from factories to their parameters
   * @return the lookup map
   */
  private Map<String, Pair<Class<? extends Factory<?>>, Map<String, QuantitativeBaseVariable<?>>>> createLookupMap(
      Map<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> factoryParametersMap) {

    Map<String, Pair<Class<? extends Factory<?>>, Map<String, QuantitativeBaseVariable<?>>>> lookupTable =
        new HashMap<>();

    for (Entry<Class<? extends Factory<?>>, List<QuantitativeBaseVariable<?>>> factoryParameters : factoryParametersMap
        .entrySet()) {
      lookupTable
          .put(
              factoryParameters.getKey().getName(),
              new Pair<Class<? extends Factory<?>>, Map<String, QuantitativeBaseVariable<?>>>(
                  factoryParameters.getKey(),
                  createParameterLookupMap(factoryParameters.getValue())));
    }
    return lookupTable;
  }

  /**
   * Creates map for looking up factory parameters by name.
   * 
   * @param parameters
   *          the list of parameters
   * @return the lookup map
   */
  private Map<String, QuantitativeBaseVariable<?>> createParameterLookupMap(
      List<QuantitativeBaseVariable<?>> parameters) {
    Map<String, QuantitativeBaseVariable<?>> parameterMap =
        new HashMap<>();
    for (QuantitativeBaseVariable<?> variable : parameters) {
      parameterMap.put(variable.getName(), variable);
    }
    return parameterMap;
  }

  /**
   * Creates the tree for given parameters. The created tree will have the
   * structure of the current tree, but will the values defined in the given
   * configuration will override the default values.
   * 
   * @param configuration
   *          the configuration
   * @return the parameterization tree
   */
  public ParameterizationTree createTreeForParameters(
      ParameterBlock configuration) {
    return new ParameterizationTree(configuration, factoryParameterMap);
  }

  public ParameterBlock getConfiguration() {
    return configuration;
  }

}
