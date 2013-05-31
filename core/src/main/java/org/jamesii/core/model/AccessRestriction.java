/**
 * Title:        CoSA: AccessRestriction
 * Description:  This class can be set to a model for restricting certain 
 *               method calls
 * Copyright:    Copyright (c) 2003
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * @author       Jan Himmelspach
 * @version 1.0
 */

package org.jamesii.core.model;

import java.io.Serializable;

/**
 * The Class AccessRestriction.
 * 
 * Base class for access restrictions. Can be used on implementing executable
 * model classes and simulation algorithms to restrict certain operations, e.g.,
 * that you are not allowed to modify a model's state during a non-state
 * transition.
 * 
 * This might be helpful in the case that modelers are condammned to use plain
 * Java to create their models.
 * 
 * @author Jan Himmelspach
 */
public abstract class AccessRestriction implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 1260272491131631414L;

  /**
   * Creates a new instance of AccessRestriction.
   */
  public AccessRestriction() {
  }

}
