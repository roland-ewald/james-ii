/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods, which shall be called by a wrapper knowing their
 * name.
 * 
 * @author Stefan Leye
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(value = { ElementType.METHOD })
public @interface TriggerableByName {

  /**
   * List of parameter descriptions for the annotated method. Descriptions
   * should occur in the same order as the parameters.
   * 
   * @return list of descriptions
   */
  String[] parameterDescription() default {};

}
