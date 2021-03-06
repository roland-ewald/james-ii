/*
if(f) * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.asf.registry.AlgoSelectionRegistry;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Triple;

/**
 * Deduct selection tree sets (again defined as trees) from parameter
 * definitions.
 * 
 * @author Roland Ewald
 * 
 */
public class SelectionTreeSet implements Serializable {

  private static final class NoParameterBlockAugmentation implements
      IParameterBlockAugmenter {
    @Override
    public ParameterBlock augment(ParameterBlock originalParamBlock,
        SelectionTreeSet selectionTreeSet, SelTreeSetVertex currentRoot,
        IParameter topLevelParameter) {
      return originalParamBlock;
    }
  }

  static {
    SerialisationUtils.addDelegateForConstructor(SelectionTreeSet.class,
        new IConstructorParameterProvider<SelectionTreeSet>() {
          @Override
          public Object[] getParameters(SelectionTreeSet treeSet) {
            Object[] params =
                new Object[] { treeSet.getTree(), treeSet.getFactorySet(),
                    treeSet.getParameterSet() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = 2449870577714652421L;

  /** Tree defining the set of eligible configurations. */
  private final SelectionTreeSetDefinition tree;

  /** All concrete factories defined in the tree. */
  private final Set<Factory<?>> factorySet = new HashSet<>();

  /** All parameters defined in the tree. */
  private final Set<IParameter> parameterSet = new HashSet<>();

  /**
   * All factory combinations that can be generated by this set. Will be
   * re-created when calling
   * {@link SelectionTreeSet#generateFactoryCombinations()}.
   */
  private transient List<ParameterBlock> factoryCombinations = null;

  /** The default black list. */
  private final List<String> defaultBlackList = new ArrayList<>();

  /** The default 'augmentation' behavior is to not change anything. */
  private transient IParameterBlockAugmenter augmenter =
      new NoParameterBlockAugmentation();

  /**
   * Recursively creates a tree defining a set of selection trees.
   * 
   * @param <F>
   *          type of factory
   * @param <AF>
   *          type of abstract factory
   * @param afClass
   *          abstract factory class
   * @param parameterBlock
   *          parameter block for this setup
   * @throws ClassNotFoundException
   *           if factory class of a plug-in parameter could not be found
   */
  public <F extends Factory<?>, AF extends AbstractFactory<F>> SelectionTreeSet(
      Class<AF> afClass, ParameterBlock parameterBlock)
      throws ClassNotFoundException {
    this(afClass, parameterBlock, null);
  }

  /**
   * Recursively creates a tree defining a set of selection trees.
   * 
   * @param <F>
   *          type of factory
   * @param <AF>
   *          type of abstract factory
   * @param afClass
   *          abstract factory class
   * @param parameterBlock
   *          parameter block for this setup
   * @param augmenter
   *          the parameter block augmenter
   * @throws ClassNotFoundException
   *           if factory class of a plug-in parameter could not be found
   */
  public <F extends Factory<?>, AF extends AbstractFactory<F>> SelectionTreeSet(
      Class<AF> afClass, ParameterBlock parameterBlock,
      IParameterBlockAugmenter augmenter) throws ClassNotFoundException {
    this(new SelectionTreeSetDefinition(), new HashSet<Factory<?>>(),
        new HashSet<IParameter>());
    if (augmenter != null) {
      this.augmenter = augmenter;
    }
    createSelectionTreeSet(afClass, parameterBlock, null, tree.getRoot());
  }

  /**
   * Instantiates a new selection tree set.
   * 
   * @param treeSet
   *          the tree set
   * @param factories
   *          the factories
   * @param parameters
   *          the parameters
   */
  protected SelectionTreeSet(SelectionTreeSetDefinition treeSet,
      Set<Factory<?>> factories, Set<IParameter> parameters) {
    tree = treeSet;
    factorySet.addAll(factories);
    parameterSet.addAll(parameters);
  }

  /**
   * Recursively creates a tree defining a set of selection trees.
   * 
   * @param <F>
   *          type of factory
   * @param <AF>
   *          type of abstract factory
   * @param afClass
   *          abstract factory class
   * @param paramBlock
   *          parameter block for this setup
   * @param topLevelParameter
   *          top-level parameter
   * @param currentRoot
   *          current root
   * @throws ClassNotFoundException
   *           if factory class of a plug-in parameter could not be found
   */
  protected final <F extends Factory<?>, AF extends AbstractFactory<F>> void createSelectionTreeSet(
      Class<AF> afClass, ParameterBlock parameterBlock,
      IParameter topLevelParameter, SelTreeSetVertex currentRoot)
      throws ClassNotFoundException {

    // Retrieve sets of suitable factories and their parameters
    ParameterBlock paramBlock =
        augmenter.augment(parameterBlock, this, currentRoot, topLevelParameter);
    List<F> fList = SimSystem.getRegistry().getFactoryList(afClass, paramBlock);

    List<F> factoryList = filterToAvoidCycle(fList, currentRoot);
    List<IPluginData> pluginData = SimSystem.getRegistry().getPlugins(afClass);
    List<Pair<F, IFactoryInfo>> suitableFactories =
        filterSuitableFactories(factoryList, pluginData);
    List<Pair<Set<F>, Set<IParameter>>> distinctFactorySets =
        retrieveDistinctFactories(suitableFactories);

    // Add these factories to the tree
    for (Pair<Set<F>, Set<IParameter>> distinctFactorySet : distinctFactorySets) {
      addFactoryNodeToTree(paramBlock, topLevelParameter, currentRoot,
          distinctFactorySet);
    }
  }

  /**
   * Filters all factory that are either available in the current root node (of
   * the sub-tree) or any other parent node on the path to the overall root.
   * This is necessary to avoid cycles, e.g. because a plug-in may depend on a
   * plug-in of the same type, etc.
   * 
   * @param factories
   *          the list of factories
   * @param currentRoot
   *          the root node of the current sub-tree
   * @return the filtered list
   */
  private <F extends Factory<?>> List<F> filterToAvoidCycle(List<F> factories,
      SelTreeSetVertex currentRoot) {
    Set<String> includedFactoryClassNames = new HashSet<>();
    for (F factory : factories) {
      includedFactoryClassNames.add(factory.getClass().getName());
    }
    removeDuplicateFactoryClassNames(currentRoot, includedFactoryClassNames);
    List<F> filteredFactories = new ArrayList<>();
    for (F factory : factories) {
      if (includedFactoryClassNames.contains(factory.getClass().getName())) {
        filteredFactories.add(factory);
      }
    }

    return filteredFactories;
  }

  @SuppressWarnings("unchecked")
  // Ensured by prior instance-of check
  private void removeDuplicateFactoryClassNames(SelTreeSetVertex currentRoot,
      Set<String> includedFactoryClassNames) {
    SelTreeSetVertex vertex = currentRoot;
    while (vertex != null) {
      if (vertex instanceof FactoryVertex<?>) {
        Set<Factory<?>> availableFactories =
            ((FactoryVertex<Factory<?>>) vertex).getAvailableFactories();
        for (Factory<?> availableFactory : availableFactories) {
          String factoryName = availableFactory.getClass().getName();
          if (includedFactoryClassNames.contains(factoryName)) {
            includedFactoryClassNames.remove(factoryName);
          }
        }
      }
      vertex = tree.getChildToParentMap().get(vertex);
    }
  }

  /**
   * Filter suitable factories.
   * 
   * @param <F>
   *          the generic type
   * @param factories
   *          the list of factories
   * @param pluginData
   *          the plugin data
   * @return the list
   */
  private <F extends Factory<?>> List<Pair<F, IFactoryInfo>> filterSuitableFactories(
      List<F> factories, List<IPluginData> pluginData) {
    List<Pair<F, IFactoryInfo>> suitableFactories = new ArrayList<>();
    for (IPluginData plugin : pluginData) {
      List<IFactoryInfo> factoryInfos = plugin.getFactories();
      for (IFactoryInfo factoryInfo : factoryInfos) {
        F factory = contains(factories, factoryInfo);
        if (factory != null && checkIfSuitable(factory)) {
          suitableFactories.add(new Pair<>(factory, factoryInfo));
        }
      }
    }
    return suitableFactories;
  }

  /**
   * Checks whether a factory is suitable for selection. It may bee rejected by
   * the {@link AlgoSelectionRegistry}.
   * 
   * @param factory
   *          the factory to be checked
   * @return true, if it is suitable
   */
  private <F extends Factory<?>> boolean checkIfSuitable(F factory) {
    if ((SimSystem.getRegistry() instanceof AlgoSelectionRegistry)) {
      return SimSystem.getRegistry()
          .factoryAvailable(factory.getClass());
    }
    return true;
  }

  /**
   * Adds a factory node to the tree.
   * 
   * @param <F>
   *          the type of the factory
   * @param paramBlock
   *          the parameter block
   * @param topLevelParamBlock
   *          the top level parameter block
   * @param currentRoot
   *          the current root
   * @param distinctFactorySet
   *          the distinct factory set
   * @throws ClassNotFoundException
   */
  private <F extends Factory<?>> void addFactoryNodeToTree(
      ParameterBlock paramBlock, IParameter topLevelParamBlock,
      SelTreeSetVertex currentRoot,
      Pair<Set<F>, Set<IParameter>> distinctFactorySet)
      throws ClassNotFoundException {
    // Add new factory node to the tree
    Set<F> factories = distinctFactorySet.getFirstValue();
    factorySet.addAll(factories);
    FactoryVertex<F> newFacVert =
        new FactoryVertex<>(tree.getVertexCount(), factories,
            topLevelParamBlock);
    tree.addVertex(newFacVert);
    tree.addEdge(new Edge<>(newFacVert, currentRoot));

    Set<IParameter> parameters = distinctFactorySet.getSecondValue();
    Set<IParameter> normalParameters = new HashSet<>();
    for (IParameter parameter : parameters) {
      processParameter(paramBlock, newFacVert, normalParameters, parameter);
    }

    parameterSet.addAll(normalParameters);

    // Normal parameter: add as leaf
    if (normalParameters.size() > 0) {
      ParameterVertex paramVert =
          new ParameterVertex(tree.getVertexCount(), normalParameters);
      tree.addVertex(paramVert);
      tree.addEdge(new Edge<>(paramVert, newFacVert));
    }
  }

  @SuppressWarnings("unchecked")
  // Plug-in definition are supposed to contain FQCNs of factories to signal
  // extension points
  private <F extends Factory<?>> void processParameter(
      ParameterBlock parameterBlock, FactoryVertex<F> newFacVert,
      Set<IParameter> normalParameters, IParameter parameter)
      throws ClassNotFoundException {
    // Plug-in parameter
    if (parameter.hasPluginType()
        && parameter.getPluginType().compareTo("null") != 0) {
      String pluginType = parameter.getPluginType();
      callRecursively(parameterBlock, newFacVert, parameter,
          (Class<? extends Factory<?>>) Class.forName(pluginType));
    } else {
      normalParameters.add(parameter);
    }
  }

  @SuppressWarnings("unchecked")
  // That the abstract factory of the passed factory is returned is ensured by
  // the registry
  private <F extends Factory<?>, F2 extends Factory<?>> void callRecursively(
      ParameterBlock parameterBlock, FactoryVertex<F> newFacVert,
      IParameter parameter, Class<F2> facClass) throws ClassNotFoundException {
    Class<? extends AbstractFactory<F2>> newAbsFacClass =
        (Class<? extends AbstractFactory<F2>>) SimSystem.getRegistry()
            .getAbstractFactoryForBaseFactory(facClass);
    createSelectionTreeSet(newAbsFacClass,
        ParameterBlocks.getSBOrEmpty(parameterBlock, parameter.getName()),
        parameter, newFacVert);
  }

  /**
   * Retrieves distinct factories per level (merges factories with similar
   * parameters).
   * 
   * @param <F>
   *          type of the factory
   * @param suitableFactories
   *          list of pairs (factory, factory information)
   * @return list of pairs (set of factories, set of parameters)
   */
  protected final <F extends Factory<?>> List<Pair<Set<F>, Set<IParameter>>> retrieveDistinctFactories(
      List<Pair<F, IFactoryInfo>> suitableFactories) {

    List<Pair<Set<F>, Set<IParameter>>> results = new ArrayList<>();

    for (Pair<F, IFactoryInfo> suitableFactory : suitableFactories) {

      boolean similarFacFound = false;

      Set<IParameter> newParamSet =
          new HashSet<>(suitableFactory.getSecondValue().getParameters());

      // Check if similar entry already exists; if yes - just add this factory
      // to the set
      for (Pair<Set<F>, Set<IParameter>> result : results) {
        if (setsEqual(newParamSet, result.getSecondValue())) {
          result.getFirstValue().add(suitableFactory.getFirstValue());
          similarFacFound = true;
          break;
        }
      }

      if (similarFacFound) {
        continue;
      }

      // Otherwise: add new entry to the results list
      Set<F> newFacSet = new HashSet<>();
      newFacSet.add(suitableFactory.getFirstValue());
      results.add(new Pair<>(newFacSet, newParamSet));

    }

    return results;
  }

  /**
   * Tests whether two sets of parameters are similar wrt name, type, and
   * plug-in type. Avoids overriding equals (which could cause some problems in
   * code relying on its current (default Object-)implementation).
   * 
   * @param set1
   *          first set
   * @param set2
   *          second set
   * @return true, if both sets of parameters are equal
   */
  protected final boolean setsEqual(Set<IParameter> set1, Set<IParameter> set2) {

    if (set1.size() != set2.size()) {
      return false;
    }

    for (IParameter param : set1) {
      boolean found = false;
      for (IParameter p : set2) {
        if (p.getName().equals(param.getName())
            && p.getType().equals(param.getType())
            && (p.hasPluginType() == param.hasPluginType())) {
          found = true;
          break;
        }
        if (!found) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Retrieves a factory for a given factory information.
   * 
   * @param <F>
   *          type of factories
   * @param list
   *          list of factories
   * @param factoryInfo
   *          the factory information to identified the desired factory
   * @return factory for the factory information, null if nothing found
   */
  protected static <F extends Factory<?>> F contains(List<F> list,
      IFactoryInfo factoryInfo) {
    for (F f : list) {
      if (f.getClass().getName().equals(factoryInfo.getClassname())) {
        return f;
      }
    }
    return null;
  }

  /**
   * Generate set of distinct parameter blocks for all combinations of factories
   * (not the parameters!).
   */
  public void generateFactoryCombinations() {
    factoryCombinations =
        generateFactoryCombinations(tree.getRoot(), defaultBlackList);
  }

  /**
   * Generates all factory combinations, like
   * {@link SelectionTreeSet#generateFactoryCombinations()}, but leaves out
   * those that contain a factory with a name that contains at least one of the
   * sub-strings given by the black list.
   * 
   * TODO: Maybe in future also support a white-list approach
   * 
   * @param blackList
   *          the black list
   */
  public void generateFactoryCombinations(List<String> blackList) {
    List<String> overallBlackList = new ArrayList<>();
    if (blackList != null) {
      overallBlackList.addAll(blackList);
    }
    overallBlackList.addAll(defaultBlackList);
    factoryCombinations =
        generateFactoryCombinations(tree.getRoot(), overallBlackList);
  }

  /**
   * Recursively generates the set of available algorithm configurations.
   * 
   * @param vertex
   *          the current vertex
   * @return set of parameter blocks for this sub-tree
   */
  public List<ParameterBlock> generateFactoryCombinations(
      SelTreeSetVertex vertex, List<String> blackList) {

    List<SelTreeSetVertex> children = tree.getChildren(vertex);

    Pair<Map<IParameter, List<ParameterBlock>>, List<ParameterBlock>> paramsAndPossibilities =
        retrieveParametersAndPossibilities(vertex, blackList, children);
    Map<IParameter, List<ParameterBlock>> usedParameters =
        paramsAndPossibilities.getFirstValue();
    List<ParameterBlock> possibilities =
        paramsAndPossibilities.getSecondValue();

    // Create auxiliary data structure cross-product
    List<Triple<IParameter, List<ParameterBlock>, Integer>> subParams =
        new ArrayList<>();
    for (Entry<IParameter, List<ParameterBlock>> usedParameter : usedParameters
        .entrySet()) {
      subParams.add(new Triple<>(usedParameter.getKey(), usedParameter
          .getValue(), 0));
    }

    return createCrossProduct(possibilities, subParams);
  }

  /**
   * Creates the cross product.
   * 
   * @param possibilities
   *          the possibilities
   * @param subParams
   *          the sub-parameters
   * @return the list
   */
  private List<ParameterBlock> createCrossProduct(
      List<ParameterBlock> possibilities,
      List<Triple<IParameter, List<ParameterBlock>, Integer>> subParams) {
    List<ParameterBlock> results = new ArrayList<>();
    if (subParams.size() > 0) {

      for (ParameterBlock paramBlock : possibilities) {
        boolean firstTime = true;
        while (true) {
          ParameterBlock newBlock = paramBlock.getCopy();
          Map<IParameter, ParameterBlock> subBlocks =
              getNextSetup(subParams, firstTime);
          firstTime = false;
          if (subBlocks == null) {
            break;
          }
          for (Entry<IParameter, ParameterBlock> subBlock : subBlocks
              .entrySet()) {
            newBlock.addSubBlock(
                subBlock.getKey() == null ? ProcessorFactory.class.getName()
                    : subBlock.getKey().getName(), subBlock.getValue()
                    .getCopy());
          }
          results.add(newBlock);
        }
      }
    } else {
      results.addAll(possibilities);
    }
    return results;
  }

  /**
   * Retrieve parameters and possibilities.
   * 
   * @param vertex
   *          the current vertex
   * @param blackList
   *          the black list
   * @param children
   *          the children
   * @return the pair of data structures to iterate of parameters etc.
   */
  private Pair<Map<IParameter, List<ParameterBlock>>, List<ParameterBlock>> retrieveParametersAndPossibilities(
      SelTreeSetVertex vertex, List<String> blackList,
      List<SelTreeSetVertex> children) {
    List<ParameterBlock> possibilities =
        retrievePossibleParamBlocks(vertex, blackList);
    Map<IParameter, List<ParameterBlock>> usedParameters =
        retrieveParameters(blackList, children);
    return new Pair<>(usedParameters, possibilities);
  }

  /**
   * Retrieve possible parameter blocks.
   * 
   * @param vertex
   *          the vertex
   * @param blackList
   *          the black list
   * @return the list of possible parameter blocks
   */
  private List<ParameterBlock> retrievePossibleParamBlocks(
      SelTreeSetVertex vertex, List<String> blackList) {
    List<ParameterBlock> possibilities = new ArrayList<>();
    if (vertex instanceof FactoryVertex<?>) {
      for (Factory<?> f : ((FactoryVertex<?>) vertex).getAvailableFactories()) {
        if (!matched(f.getName(), blackList)) {
          possibilities.add(new ParameterBlock(f.getName()));
        }
      }
    } else if (!(vertex instanceof ParameterVertex)) {
      possibilities.add(new ParameterBlock(""));
    }
    return possibilities;
  }

  /**
   * Retrieve parameters.
   * 
   * @param blackList
   *          the black list
   * @param children
   *          the children
   * @return the map
   */
  private Map<IParameter, List<ParameterBlock>> retrieveParameters(
      List<String> blackList, List<SelTreeSetVertex> children) {
    Map<IParameter, List<ParameterBlock>> usedParameters = new HashMap<>();
    for (SelTreeSetVertex child : children) {
      if (!(child instanceof FactoryVertex<?>)) {
        continue;
      }
      IParameter parameter = ((FactoryVertex<?>) child).getParameter();
      if (usedParameters.containsKey(parameter)) {
        usedParameters.get(parameter).addAll(
            generateFactoryCombinations(child, blackList));
      } else {
        usedParameters.put(parameter,
            generateFactoryCombinations(child, blackList));
      }
    }
    return usedParameters;
  }

  /**
   * Checks whether the given factory name contains any of the items in the
   * black list.
   * 
   * @param factoryName
   *          the factory name
   * @param blackList
   *          the black list
   * @return true, if at least one black list item matched the factory name
   */
  protected boolean matched(String factoryName, List<String> blackList) {
    if (blackList == null) {
      return false;
    }
    for (String ignoreItem : blackList) {
      if (factoryName.contains(ignoreItem)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get next concrete setup.
   * 
   * @param subParams
   *          data structure of sub parameters
   * @param firstTime
   *          flag to signal that the first setup is requested (no cunting is
   *          necessary)
   * @return map with the selected parameter block for each parameter
   */
  protected Map<IParameter, ParameterBlock> getNextSetup(
      List<Triple<IParameter, List<ParameterBlock>, Integer>> subParams,
      boolean firstTime) {

    if (!firstTime && !getNext(subParams)) {
      return null;
    }

    Map<IParameter, ParameterBlock> result = new HashMap<>();
    for (Triple<IParameter, List<ParameterBlock>, Integer> subParam : subParams) {
      result.put(subParam.getA(), subParam.getB().get(subParam.getC()));
    }

    return result;
  }

  /**
   * Counts the indices of the sub parameter blocks to be used up (by one).
   * 
   * @param subParams
   *          sub-parameter list
   * @return true, if a next setup exists, false if everything had been reset to
   *         the original state (like an overflow)
   */
  protected boolean getNext(
      List<Triple<IParameter, List<ParameterBlock>, Integer>> subParams) {

    for (Triple<IParameter, List<ParameterBlock>, Integer> subParam : subParams) {
      if (subParam.getC() < subParam.getB().size() - 1) {
        subParam.setC(subParam.getC() + 1);
        return true;
      }
      subParam.setC(0);
    }

    return false;
  }

  /**
   * Calculate number of available combinations.
   * 
   * @return number of available combinations
   */
  public int calculateFactoryCombinations() {
    return calculateFactoryCombinations(tree.getRoot());
  }

  /**
   * Recursively calculates the number of available algorithm combinations.
   * 
   * @param v
   *          the current vertex
   * @return number of combinations that will be generated with
   *         {@link SelectionTreeSet#generateFactoryCombinations()}
   */
  protected int calculateFactoryCombinations(SelTreeSetVertex v) {

    int possibilities = 1;

    if (v instanceof FactoryVertex<?>) {
      possibilities = ((FactoryVertex<?>) v).getAvailableFactoryCount();
    }

    List<SelTreeSetVertex> children = tree.getChildren(v);
    Map<IParameter, Integer> usedParameters = new HashMap<>();

    for (SelTreeSetVertex child : children) {
      if (!(child instanceof FactoryVertex<?>)) {
        continue;
      }

      IParameter parameter = ((FactoryVertex<?>) child).getParameter();
      if (usedParameters.containsKey(parameter)) {
        usedParameters.put(parameter, usedParameters.get(parameter)
            + calculateFactoryCombinations(child));
      } else {
        usedParameters.put(parameter, calculateFactoryCombinations(child));
      }
    }

    for (Integer p : usedParameters.values()) {
      possibilities *= p;
    }

    return possibilities;
  }

  public SelectionTreeSetDefinition getTree() {
    return tree;
  }

  public Set<Factory<?>> getFactorySet() {
    return factorySet;
  }

  public Set<IParameter> getParameterSet() {
    return parameterSet;
  }

  public List<ParameterBlock> getFactoryCombinations() {
    if (factoryCombinations == null) {
      generateFactoryCombinations();
    }
    return factoryCombinations;
  }

  public List<String> getDefaultBlackList() {
    return defaultBlackList;
  }

  public IParameterBlockAugmenter getAugmenter() {
    return augmenter;
  }

}
