/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.instrumenter;

import java.net.URI;

import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.plugintype.AbstractModelInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;

/**
 * CAModelInstrumenterFactory
 * 
 * Factory for the CA Model instrumenter.
 * 
 * @author Jan Himmelspach
 */
@Plugin
public class CAModelInstrumenterFactory extends ModelInstrumenterFactory {

  private static final long serialVersionUID = -3937703967659679718L;

  @Override
  public IModelInstrumenter create(ParameterBlock parameter, Context context) {
    return new CAModelInstrumenter();
  }

  @Override
  public int supportsParameters(ParameterBlock parameter) {
    if (!parameter.hasSubBlock(AbstractModelInstrumenterFactory.MODELURI)) {
      return 0;
    }
    URI uri =
        parameter.getSubBlockValue(AbstractModelInstrumenterFactory.MODELURI);
    if (uri == null) {
      return 0;
    }
    String filename = uri.getScheme();
    if (filename.indexOf(".ca") == filename.length() - 3) {
      return 1;
    }
    return 0;
  }
}