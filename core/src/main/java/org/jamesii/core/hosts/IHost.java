/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.hosts;

import org.jamesii.core.services.IService;

/**
 * The Interface IHost. Any resource host providing a resource for the m&s
 * framework has to implement this interface. Every host is considered to be a
 * service, e.g., a computation service, database service, etc ....
 * 
 * @author Jan Himmelspach
 */
public interface IHost extends IService {

}
