/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.plugintype;

/**
 * Helper class, that allows to distinguish between internal and external
 * processor based on the factory. Internal processors (which means that don't
 * use a customized TaskSetupFactory), must inherit this class.
 * <p>
 * Beware: this class might change or vanish due to refactoring
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class JamesProcessorFactory extends ProcessorFactory implements
    IJamesProcessorFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2482688672601846112L;

}
