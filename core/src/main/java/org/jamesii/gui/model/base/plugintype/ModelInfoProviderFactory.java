/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.base.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.gui.model.base.IModelInfoProvider;

/**
 * Factory for info providers for model source.
 * 
 * @author Stefan Rybacki
 */
public abstract class ModelInfoProviderFactory extends
    Factory<IModelInfoProvider> implements IParameterFilterFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -8558242481683467736L;

}
