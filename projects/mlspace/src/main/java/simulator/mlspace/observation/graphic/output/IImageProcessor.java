/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic.output;

import java.awt.image.BufferedImage;

/**
 * (Somewhat) Generic interface for processing images produced during a
 * simulation run / computation task, e.g. for displaying them on screen,
 * writing them to file, or creating a video from them.
 * 
 * @author Arne Bittig
 * @date 27.11.2012
 */
public interface IImageProcessor {

  /**
   * Initialize image processing if needed
   * 
   * @param width
   *          Expected width of image
   * @param height
   *          Expected height of image
   */
  void init(int width, int height);

  /**
   * Signal to image provider whether an image corresponding to given time is
   * needed. If only at certain time points an image is needed, the provider may
   * skip generating it.
   * 
   * @param time
   *          Time
   * @return false if an image corresponding to given time will not be processed
   *         further by the implementation
   */
  boolean needsImageAtTime(Double time);

  /**
   * Process image for given time point.
   * 
   * @param img
   *          Image
   * @param time
   *          Time index
   */
  void processImage(BufferedImage img, Double time);

  /**
   * Process image for given time point (as in
   * {@link #processImage(BufferedImage, Double)}), but with the knowledge that
   * no further images will follow. Do clean up if required.
   * 
   * @param img
   *          Image
   * @param time
   *          Time index
   */
  void processFinalImageAndCleanUp(BufferedImage img, Double time);

}
