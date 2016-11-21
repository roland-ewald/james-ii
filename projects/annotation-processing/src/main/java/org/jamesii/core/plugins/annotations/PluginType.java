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
 * Use the annotation to specify a JAMES II plugintype, without providing an
 * explicit plugintype file. This file will automatically created for you when
 * using this annotation. This annotation is to be used on the abstract factory
 * specifying the plugintype. Usage example:
 * 
 * <pre>
 *  {@literal @}PluginType(version="1.2", parameters={
 *    {@literal @}Parameter(name="param1", type=Integer.class),
 *    {@literal @}Parameter(name="param2", type=Double.class, required=true, description="param2 for something", defaultValue="1.0") 
 *  })
 *  public class AbstractPluginTypeFactory extends AbstractFactory&lt;PluginTypeBaseFactory&gt; {...}
 * </pre>
 * 
 * @author Stefan Rybacki
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface PluginType {
  String description() default "";

  String name() default "";

  String icon() default "";

  String version() default "1.0";
  Parameter[] parameters() default {};
}
