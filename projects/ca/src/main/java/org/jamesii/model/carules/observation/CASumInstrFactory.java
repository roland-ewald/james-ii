/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.observation;

import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.plugintype.AbstractModelInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;

import java.net.URI;

/**
 * The Class CASumInstrFactory.
 * 
 * @author Roland Ewald
 */
@Plugin
public class CASumInstrFactory extends ModelInstrumenterFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = -8666395112456630719L;

  @Override
  public IModelInstrumenter create(ParameterBlock parameter, Context context) {
    return new CAStateSumInstrumenter();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    URI uri =
        params.getSubBlockValue(AbstractModelInstrumenterFactory.MODELURI);
    if (uri != null) {
      if (uri.toString().contains("tutorial.Bogus")) {
        return 1;
      }
    }
    return 0;
  }

}
