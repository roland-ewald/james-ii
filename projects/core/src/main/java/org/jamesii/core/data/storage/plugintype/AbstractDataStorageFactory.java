/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.storage.plugintype;

import org.jamesii.core.factories.AbstractFactory;

/**
 * Class to choose factories creating data storages.
 * 
 * @author Thomas Noesinger
 */
public class AbstractDataStorageFactory extends
    AbstractFactory<DataStorageFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2696798896064187466L;

  /**
   * The parameter name for a directory manager.
   * 
   * TODO: to have this in a core class is currently required for the parameter
   * black list in {@link org.jamesii.core.algoselect.SelectionInformation}.
   */
  public static final String PARAM_MANAGER = "manager";

}
