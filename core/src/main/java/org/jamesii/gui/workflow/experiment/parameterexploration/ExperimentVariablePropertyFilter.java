/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.observe.IMediator;
import org.jamesii.gui.utils.objecteditor.property.IPropertyFilter;

/**
 * Simple property filter that hides the mediator property for
 * {@link ExperimentVariable}s.
 * 
 * @author Stefan Rybacki
 */
class ExperimentVariablePropertyFilter implements IPropertyFilter {

  @Override
  public boolean isPropertyVisible(Class<?> parentPropertyClass,
      String propertyName, Class<?> propertyType) {
    if (!ExperimentVariable.class.isAssignableFrom(parentPropertyClass)) {
      return true;
    }

    // hide mediator
    if (propertyName.equals("mediator")
        && IMediator.class.isAssignableFrom(propertyType)) {
      return false;
    }

    return true;
  }

}
