package org.jamesii.core.distributed.loadbalancing;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.plugintype.ProcessorFactory;

/**
 * Interface for methods to set up {@link ILoadBalancer} instances. This needs
 * to be called in the {@link ProcessorFactory} of the given
 * {@link ILBProcessor}.
 * 
 * @author Roland Ewald
 * 
 */
public interface ILoadBalancingSetup {

  /**
   * Initialises root processor.
   * 
   * @param root
   *          the root processor
   */
  void initRootProcessor(IRunnable root);

  /**
   * Initialises any other processor.
   * 
   * @param processor
   *          the processor to be initialised
   */
  void initProcessor(IProcessor processor);

}
