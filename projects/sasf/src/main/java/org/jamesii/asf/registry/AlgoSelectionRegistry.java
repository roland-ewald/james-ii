/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.registry.failuredetection.FailureDescription;
import org.jamesii.asf.registry.failuredetection.FailureDetector;
import org.jamesii.asf.registry.failuredetection.FailureReport;
import org.jamesii.asf.registry.selection.SelectorEnsemble;
import org.jamesii.asf.registry.selection.SelectorManager;
import org.jamesii.core.Registry;
import org.jamesii.core.algoselect.AlgorithmSelection;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.metadata.AbstractFactoryRuntimeData;
import org.jamesii.core.plugins.metadata.ComponentAction;
import org.jamesii.core.plugins.metadata.FactoryDataSynchronizer;
import org.jamesii.core.plugins.metadata.FactoryRuntimeData;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * This is a test class to try out prototypical enhancements of the original
 * registry in a save environment.
 * 
 * @author Roland Ewald
 */
public class AlgoSelectionRegistry extends Registry {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5122609601516611929L;

  /**
   * If this flag is switched off, the registry will just hand over all requests
   * to the original one.
   */
  private boolean active = true;

  /** The failure detector. */
  private FailureDetector failureDetector = new FailureDetector();

  /** The flag that activates the failure detection system. */
  private boolean detectFailures = true;

  /**
   * Reports failure.
   * 
   * @param fd
   *          the failure description
   */
  public void reportFailure(FailureDescription fd) {

    if (!active || !detectFailures) {
      return;
    }

    for (FailureReport report : failureDetector.failureOccurred(fd)) {
      FactoryRuntimeData<?> rtData =
          getDataStorage().getFactoryData(report.getFactory());
      if (rtData != null) {
        rtData.changeState(ComponentAction.DECLARE_BROKEN);
        SimSystem.report(Level.INFO, "The component generated by factory '"
        + rtData.getFactory()
        + "' has been declared broken and will not be used again until further notice.");
      }
    }
  }

  @Override
  public <F extends Factory<?>> F getFactory(
      Class<? extends AbstractFactory<F>> afc, ParameterBlock afp) {

    if (!active) {
      return super.getFactory(afc, afp);
    }

    F factory = getFactoryWithoutSelection(afc, afp);
    if (factory == null) {
      factory = selectAutomatically(afc, afp);
    }

    if (getFactorySelectionHook() != null) {
      getFactorySelectionHook().execute(
          new SelectionInformation<>(afc, afp, factory));
    }

    return factory;
  }

  /**
   * Gets a factory without selection, if possible.
   * 
   * @param afc
   *          the class of the abstract factory
   * @param afp
   *          the abstract factory parameters
   * 
   * @return the factory, null if automatic selection shall be conducted
   */
  protected <F extends Factory<?>> F getFactoryWithoutSelection(
      Class<? extends AbstractFactory<F>> afc, ParameterBlock afp) {

    String predefinedFactoryName = retrievePotentialFactoryName(afp);
    Factory<?> factory =
        afp == null ? null : getFactoryNameMap().get(predefinedFactoryName);

    AlgorithmSelection algoSelection =
        afc.getAnnotation(AlgorithmSelection.class);

    // If no specific factory is selected and this type shall be selected
    // automatically, return
    if (factory == null && algoSelection != null) {
      return null;
    }

    // Otherwise, choose factory by tolerance if none prescribed
    if (factory == null) {
      return getFactoryWithTolerance(afc, afp);
    }

    checkFactoryState(factory);
    List<F> factories = getInitializedAbstractFactory(afc).getFactoryList(afp);
    for (F f : factories) {
      if (f.getName().equals(predefinedFactoryName)) {
        return f;
      }
    }

    return resolvePrematureDecisionProblem(factories, afp);
  }

  /**
   * Retrieve potential factory name.
   * 
   * @param parameterBlock
   *          the parameter block
   * @return the name that may be a factory name, or null if no value is set
   */
  protected String retrievePotentialFactoryName(ParameterBlock parameterBlock) {
    return parameterBlock == null ? null : (String) parameterBlock.getValue();
  }

  /**
   * Select automatically: use the {@link AbstractFactoryRuntimeData} to fill in
   * the parameter block accordingly, then process as if this setting comes from
   * user.
   * 
   * @param abstractFactoryClass
   *          the class of the abstract factory
   * @param parameters
   *          the parameters
   * 
   * @return the selected factory
   */
  protected <F extends Factory<?>, AF extends AbstractFactory<F>> F selectAutomatically(
      Class<AF> abstractFactoryClass, ParameterBlock parameters) {

    AbstractFactoryRuntimeData<F, AF> absFactoryRTD =
        getDataStorage().getAbstractFactoryData(abstractFactoryClass);

    if (!absFactoryRTD.hasAdditionalData(SelectorEnsemble.DATA_ID)) {
      return getFactoryWithTolerance(abstractFactoryClass, parameters);
    }

    SelectorEnsemble ensemble =
        absFactoryRTD.getAdditionalData(SelectorEnsemble.DATA_ID);
    ParameterBlock selectedParameters =
        ensemble.select(parameters, getDataStorage().getFailureTolerance());

    // If no selection could be made, return to default behaviour
    if (selectedParameters == null) {
      return getFactoryWithTolerance(abstractFactoryClass, parameters);
    }

    // Only select sub-block that corresponds to given task
    String baseFactoryName =
        getBaseFactoryForAbstractFactory(abstractFactoryClass).getName();
    if (selectedParameters.hasSubBlock(baseFactoryName)) {
      // Setting the value to null is important in case the user messed up a
      // factory name, i.e. it is non-null but does not result in a selection
      // Otherwise the value of selectedParameters will not be copied (infinite
      // loop)
      parameters.setValue(null);
      copyValues(parameters, selectedParameters.getSubBlock(baseFactoryName));
    }

    return getFactory(abstractFactoryClass, parameters);
  }

  /**
   * Resolve premature decision problem.
   * 
   * @param viableOptions
   *          the viable options
   * @param parameters
   *          the parameters
   * 
   * @return the f
   */
  protected <F extends Factory<?>> F resolvePrematureDecisionProblem(
      List<F> viableOptions, ParameterBlock parameters) {
    // TODO: think of better handling strategy
    return viableOptions.size() > 0 ? viableOptions.get(0) : null;
  }

  /**
   * Reports successful execution.
   * 
   * @param setup
   *          the selection tree that was used
   * @param simConfig
   *          the set-up that was processed successfully
   */
  public void reportSuccess(SelectionTree setup, TaskConfiguration simConfig) {
    if (!active) {
      return;
    }
    for (SelectedFactoryNode factoryNode : setup.getVertices()) {
      FactoryRuntimeData<?> frd =
          getDataStorage().getFactoryData(factoryNode.getSelectionInformation()
              .getFactoryClass());
      frd.success();
    }
  }

  /**
   * Looks up factories in a {@link ParameterBlock}.
   * 
   * @param parameterBlock
   *          the parameter block
   * 
   * @return the set< class<? extends factory>>
   */
  @SuppressWarnings("unchecked")
  // cast of getClass to wildcard type
  public Set<Class<? extends Factory<?>>> lookupFactories(
      ParameterBlock parameterBlock) {
    Set<Class<? extends Factory<?>>> result =
        new HashSet<>();

    if (parameterBlock == null) {
      return result;
    }

    if (parameterBlock.getValue() != null) {
      String strVal = parameterBlock.getValue().toString();
      if (getFactoryNameMap().containsKey(strVal)) {
        result.add((Class<? extends Factory<?>>) getFactoryNameMap().get(strVal)
            .getClass());
      }
    }

    for (ParameterBlock subBlock : parameterBlock.getSubBlocks().values()) {
      result.addAll(lookupFactories(subBlock));
    }

    return result;
  }

  /**
   * Switches self-adaptive registry off.
   * 
   * @param reason
   *          the reason
   */
  protected void switchOff(Throwable reason) {
    SimSystem.report(reason);
    SimSystem.report(Level.SEVERE, "Deactivating AS-Registry, using default Registry instead.");
    active = false;
  }

  /**
   * Allows to add a new factory manually.
   * 
   * @param loadedFactory
   *          the loaded factory
   * @param facInfo
   *          the factory info
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected void addNewFactoryManually(Factory<?> loadedFactory,
      IFactoryInfo facInfo) throws IOException {
    registerFactory(loadedFactory, facInfo);
    List<Factory<?>> factories = new ArrayList<>();
    factories.add(loadedFactory);
    FactoryDataSynchronizer.addNewFactories(factories, getDataStorage());
  }

  /**
   * Merge all values to first parameter block by calling
   * {@link ParameterBlocks#merge(ParameterBlock, ParameterBlock)} on all common
   * sub-blocks. Afterwards the first parameter block contains copies of all
   * sub-blocks of the second parameter block. In case of conflict, the first
   * block has precedence.
   * 
   * @param pb
   *          the parameter block which shall contain the merged structures
   * @param merge
   *          the parameter block to be merged
   */
  static void copyValues(ParameterBlock pb, ParameterBlock merge) {

    if (pb.getValue() == null) {
      pb.setValue(merge.getValue());
    }

    // Merge all common sub-blocks
    for (String sb : pb.getSubBlocks().keySet()) {
      if (merge.hasSubBlock(sb)) {
        pb.addSubBl(sb,
            ParameterBlocks.merge(pb.getSubBlock(sb), merge.getSubBlock(sb)));
      }
    }

    // Add sub-blocks that are exclusive to the parameter block for merging
    for (String sb : merge.getSubBlocks().keySet()) {
      if (!pb.hasSubBlock(sb)) {
        pb.addSubBl(sb, merge.getSubBlock(sb).getCopy());
      }
    }
  }

  /**
   * Gets the casted registry instance from {@link SimSystem}.
   * 
   * @return the algorithm selection registry
   */
  public static AlgoSelectionRegistry getRegistry() {
    return (AlgoSelectionRegistry) SimSystem.getRegistry();
  }

  /**
   * Deploys a selector (contained in a selector manager) to the registry.
   * 
   * @param <F>
   *          the generic type
   * @param <AF>
   *          the generic type
   * @param newSelectorManager
   *          the manager of the new selector
   * @param abstractFactoryClass
   *          the abstract factory class
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public <F extends Factory<?>, AF extends AbstractFactory<F>> void deploySelector(
      SelectorManager newSelectorManager, Class<AF> abstractFactoryClass)
      throws IOException {

    AbstractFactoryRuntimeData<F, AF> absFactoryRTD =
        getDataStorage().getAbstractFactoryData(abstractFactoryClass);

    // Create ensemble if necessary
    SelectorEnsemble ensemble =
        absFactoryRTD.getAdditionalData(SelectorEnsemble.DATA_ID);
    if (!absFactoryRTD.hasAdditionalData(SelectorEnsemble.DATA_ID)) {
      ensemble = new SelectorEnsemble();
      absFactoryRTD.setAdditionalData(SelectorEnsemble.DATA_ID, ensemble);
    }

    ensemble.addSelectorManager(newSelectorManager);
    getDataStorage().flush();
  }

  /**
   * Gets the selector ensemble for a certain abstract factory.
   * 
   * @param abstractFactoryClass
   *          the abstract factory class
   * 
   * @return the selector ensemble, null if non-existent
   */
  public <F extends Factory<?>, AF extends AbstractFactory<F>> SelectorEnsemble getSelectorEnsemble(
      Class<AF> abstractFactoryClass) {
    AbstractFactoryRuntimeData<F, AF> absFactoryRTD =
        getDataStorage().getAbstractFactoryData(abstractFactoryClass);
    return absFactoryRTD != null ? absFactoryRTD
        .<SelectorEnsemble> getAdditionalData(SelectorEnsemble.DATA_ID) : null;
  }

}