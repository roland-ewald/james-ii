/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Use this annotation to specify {@link PluginType#parameters()} or
 * {@link Plugin#parameters()}.
 * 
 * @author Stefan Rybacki
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Parameter {
  String description() default "";
  String name();
  boolean required() default false;
  Class<?> type();
  String defaultValue() default "";

  Class<?> pluginType() default Void.class;
}
