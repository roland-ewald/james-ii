/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic.output;

import java.util.Map;

import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.observe.IInfoMapProvider;

/**
 * @author Arne Bittig
 * @date 28.11.2012
 */
public class ImageDisplayFactory implements IImageProcessorFactory {

  private static final String DEFAULT_FRAME_TITLE = "";

  private static final double DEFAULT_FRAME_RATE = 25.;

  @Override
  public IImageProcessor create(INamedEntity imageContentProvider,
      String outputSpec) {
    String frameTitle = getNameForTitle(imageContentProvider);
    if (frameTitle.isEmpty()) {
      frameTitle = outputSpec;
    }
    return new ImageDisplay(frameTitle, getRefreshInterval(DEFAULT_FRAME_RATE));
  }

  private static long getRefreshInterval(double defaultFrameRate) {
    final double milliConversionFactor = 1000.;
    return (long) (milliConversionFactor / defaultFrameRate);
  }

  /**
   * Get name whatever provides the content of the image (e.g. the model that is
   * simulated) if specified by the given object for frame title; default value
   * {@value #DEFAULT_FRAME_TITLE} otherwise
   * 
   * @param object
   *          model or other named entity
   * @return frame title (from model name & properties)
   */
  private static String getNameForTitle(Object contentProvider) {
    String frameTitle = DEFAULT_FRAME_TITLE;
    if (contentProvider instanceof INamedEntity) {
      frameTitle = ((INamedEntity) contentProvider).getName();
    }
    if (contentProvider instanceof IInfoMapProvider<?>) {
      Map<String, ?> infoMap =
          ((IInfoMapProvider<?>) contentProvider).getInfoMap();
      if (infoMap != null) {
        frameTitle += " " + infoMap.toString();
      }
    }
    return frameTitle;
  }

}
