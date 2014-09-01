/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire.observe;


import java.net.URI;

import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.plugintype.AbstractModelInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The Class FFStateInstrFactory.
 * 
 * @author Jan Himmelspach
 */
public class FFStateInstrFactory extends ModelInstrumenterFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = -8666395112456630719L;

  @Override
  public IModelInstrumenter create(ParameterBlock parameter) {
    return new FFInstrumenter();
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    if (params
            .getSubBlockValue(AbstractModelInstrumenterFactory.MODELURI)
            .toString().contains("ForestFire")) {
      return 1;
    }
    return 0;
  }

}
