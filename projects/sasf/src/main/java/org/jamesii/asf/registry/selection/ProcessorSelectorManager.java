/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.selection;


import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.ISelector;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.ModelFeatureExtractor;
import org.jamesii.perfdb.recording.features.plugintype.ModelFeatureExtractorFactory;


/**
 * Super class for managers that maintain a selector of processors.
 * Applicability is defined by supported model interfaces. Will extract all
 * model features.
 * 
 * @see ModelFeatureExtractor
 * 
 * @author Roland Ewald
 * 
 */
public class ProcessorSelectorManager extends SelectorManager {
  static {
    SerialisationUtils.addDelegateForConstructor(
        ProcessorSelectorManager.class,
        new IConstructorParameterProvider<ProcessorSelectorManager>() {
          @Override
          public Object[] getParameters(ProcessorSelectorManager oldInstance) {
            return new Object[] { oldInstance.getSelector(),
                oldInstance.getModelInterfaces() };
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -471903477306528850L;

  /** The model interfaces. */
  private final List<Class<? extends IModel>> modelInterfaces;

  /**
   * Instantiates a new processor selector manager.
   * 
   * @param managedSelector
   *          the managed selector
   * @param supportedModelInterfaces
   *          the supported model interfaces
   */
  public ProcessorSelectorManager(ISelector managedSelector,
      List<Class<? extends IModel>> supportedModelInterfaces) {
    super(managedSelector);
    modelInterfaces = supportedModelInterfaces;
  }

  @Override
  public boolean appliesTo(ParameterBlock parameters) {
    IModel model = ModelFeatureExtractor.getModelRuntime(parameters);
    for (Class<? extends IModel> supportedInterface : modelInterfaces) {
      if (supportedInterface.isAssignableFrom(model.getClass())) {
        return true;
      }
    }
    return false;
  }

  public List<Class<? extends IModel>> getModelInterfaces() {
    return modelInterfaces;
  }

  @Override
  protected Features extractFeatures(ParameterBlock parameters) {

    IModel model = ModelFeatureExtractor.getModelRuntime(parameters);
    Features features = new Features();

    // Look up registry for all model feature extractors
    List<FeatureExtractorFactory> featExtractFactories =
        SimSystem.getRegistry().getFactories(FeatureExtractorFactory.class);

    for (FeatureExtractorFactory featExtractFactory : featExtractFactories) {
      // Test each if applicable to this model
      if (featExtractFactory instanceof ModelFeatureExtractorFactory<?>
          && featExtractFactory.supportsParametersRuntime(parameters)) {
        ModelFeatureExtractor<?> featureExtractor =
            ((ModelFeatureExtractorFactory<?>) featExtractFactory).create(null);
        // If applicable, use it to generate features
        features.putAll(featureExtractor.extractModelFeaturesRuntime(model));
      }
    }
    return features;
  }
}