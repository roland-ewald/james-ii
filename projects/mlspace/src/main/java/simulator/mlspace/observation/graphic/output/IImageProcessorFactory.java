/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic.output;

import org.jamesii.core.base.INamedEntity;

/**
 * @author Arne Bittig
 * @date 28.11.2012
 */
public interface IImageProcessorFactory
// extends IFactory<IImageProcessor>
{
  /**
   * Create image processor from given parameters
   * 
   * @param imageContentProvider
   *          Entity providing image content, e.g. for extracting display window
   *          title
   * @param outputSpec
   *          String specifying output, e.g. path and filename (without
   *          extension)
   * @return Image processor
   */
  IImageProcessor create(INamedEntity imageContentProvider, String outputSpec);

}
