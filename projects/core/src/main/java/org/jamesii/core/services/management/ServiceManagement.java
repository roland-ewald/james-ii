/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.management;

import org.jamesii.core.services.IService;

/**
 * Service management class. Ensures that elements are of type "IService".
 * 
 * @author Jan Himmelspach
 */
public class ServiceManagement extends Management<IService> implements
    IServiceManagement {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5085668883030195766L;

}
