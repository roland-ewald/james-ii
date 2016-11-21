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
 * 
 * Use this annotation to specify license information for
 * {@link Plugin#license()}
 * 
 * @author Stefan Rybacki
 * 
 */
@Retention(RetentionPolicy.SOURCE)
public @interface License {
  String uri();
}
