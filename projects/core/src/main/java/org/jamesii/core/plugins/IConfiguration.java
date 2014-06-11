package org.jamesii.core.plugins;

import java.io.Serializable;
import java.util.Map;

/**
 * A configuration defines one valid setting of primitive parameters of a factory.
 * 
 * @author Tobias Helms
 *
 */
public interface IConfiguration extends Serializable {

  /**
   * Get description.
   * 
   * @return the description of the parameter
   */
  String getDescription();
  
  /**
   * Get name.
   * 
   * @return the name of the parameter
   */
  String getName();
  
  /**
   * Get the mapping of parameters and values of this configuration.
   */
  Map<String, Object> getMapping();
  
}
