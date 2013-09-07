/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.algoselect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Simple annotation for sub-classes of
 * {@link org.jamesii.core.factories.AbstractFactory}.
 * 
 * @author Roland Ewald
 * 
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AlgorithmSelection {

  /**
   * Gets the selection type.
   * 
   * @return the selection type
   */
  SelectionType value() default SelectionType.DISABLED;

}
