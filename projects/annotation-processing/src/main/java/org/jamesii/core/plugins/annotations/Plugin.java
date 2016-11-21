/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use the annotation to specify a JAMES II plugin, without providing an
 * explicit plugin file. This file will automatically created for you when using
 * this annotation. This annotation is to be used on the factory providing the
 * implementation of a plugin. Usage example:
 * 
 * <pre>
 *  {@literal @}Plugin(version="1.2", parameters={
 *    {@literal @}Parameter(name="param1", type=Integer.class),
 *    {@literal @}Parameter(name="param2", type=Double.class, required=true, description="param2 for something", defaultValue="1.0") 
 *  })
 *  public class PluginFactory extends PluginTypeBaseFactory {...}
 * </pre>
 * 
 * @author Stefan Rybacki
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Plugin {
  String description() default "";

  License license() default @License(uri = "");

  Parameter[] parameters() default {};

  String version() default "1.0";

  String name() default "";

  String icon() default "";

  // depends is missing for now
}
