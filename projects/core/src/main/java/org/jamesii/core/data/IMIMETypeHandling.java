/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import org.jamesii.core.data.model.read.plugintype.IMIMEType;

/**
 * Interface specifying whether implemented classes support mime handling
 * checks.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IMIMETypeHandling {
  /**
   * Returns true if the factory is able to handle such mime types
   * 
   * @param mime
   *          mime type to handle
   * @return true, if mime type is supported
   */
  boolean supportsMIMEType(IMIMEType mime);
}
