/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.factories.converter.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Selects a converter that is suitable for converting a String encoded object
 * to an actual java object.
 * 
 * @author Stefan Rybacki
 */
public class AbstractValueConverterFactory extends
    AbstractFilteringFactory<ValueConverterFactory<?>> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5361308565374446562L;

  /**
   * The Constant TYPE.
   */
  public static final String TYPE = "TYPE_TO_CONVERT_TO";

}
