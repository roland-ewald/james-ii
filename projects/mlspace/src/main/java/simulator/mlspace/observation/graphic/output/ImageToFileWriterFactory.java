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
public class ImageToFileWriterFactory implements IImageProcessorFactory {

  private final Iterable<Double> imageTimes;

  private final String fileSuffixAndExt;

  /**
   * Factory for image-to-file writers writing png files at given times
   * 
   * @param imageTimes
   */
  public ImageToFileWriterFactory(Iterable<Double> imageTimes) {
    this(imageTimes, ".png");
  }

  /**
   * Factory for image-to-file writers writing at given times to files with
   * given extension
   * 
   * @param imageTimes
   * @param fileSuffixAndExt
   */
  public ImageToFileWriterFactory(Iterable<Double> imageTimes,
      String fileSuffixAndExt) {
    this.imageTimes = imageTimes;
    this.fileSuffixAndExt = fileSuffixAndExt;
  }

  @Override
  public IImageProcessor create(INamedEntity imageContentProvider,
      String outputSpec) {
    return new ImageToFileWriter(imageTimes.iterator(), outputSpec,
        fileSuffixAndExt);
  }

}
