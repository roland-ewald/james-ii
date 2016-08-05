/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic.output;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * @author Arne Bittig
 * @date 27.11.2012
 */
public class ImageToFileWriter implements IImageProcessor, Serializable {

  private static final long serialVersionUID = -2934679617625710116L;

  private final String filePathAndPrefix;

  private final String fileSuffixAndExtension;

  private final Iterator<Double> nextImageTimeItr;

  private double nextImageTime = 0;

  /**
   * Create new Image-to-file-writer writing at given times to file with given
   * suffix and extension.
   * 
   * @param nextImageTimes
   *          Times points when to write images (final image will always be
   *          written)
   * @param filePathAndPrexString
   *          File path and name start
   * @param fileSuffixAndExt
   *          File name end including dot and extension
   */
  ImageToFileWriter(Iterator<Double> nextImageTimes, String filePathAndPrefix,
      String fileSuffixAndExt) {
    this.filePathAndPrefix = filePathAndPrefix;
    this.fileSuffixAndExtension = fileSuffixAndExt;
    this.nextImageTimeItr = nextImageTimes;
  }

  @Override
  public void init(int width, int height) { /* nothing to do */
  }

  @Override
  public boolean needsImageAtTime(Double time) {
    return time >= nextImageTime;
  }

  @Override
  public void processImage(BufferedImage img, Double time) {
    if (time >= nextImageTime) {
      // synchronized (nextImageTimeItr) {
      boolean success = captureImageToFile(img, getFileName(time));
      if (success) {
        while (nextImageTime <= time) {
          nextImageTime =
              nextImageTimeItr.hasNext() ? nextImageTimeItr.next()
                  : Double.POSITIVE_INFINITY;
        }
      }
      // }
    }
  }

  private String getFileName(double time) {
    String timeString;
    if (time > 0 && time < 0.0001) {
      timeString = Double.toString(time);
    } else {
      timeString = String.format("%.4f", time);
    }
    return filePathAndPrefix + "-" + timeString + fileSuffixAndExtension;
  }

  private static boolean captureImageToFile(RenderedImage image, String filename) {
    if (image == null) {
      ApplicationLogger.log(Level.SEVERE,
          "Null image could not be saved to file " + filename);
      return false;
    }
    File file = new File(filename);
    if (!file.exists()) {
      try {
        if (file.createNewFile()) {
          ImageIO.write(image, "png", file);
        } else {
          ApplicationLogger.log(Level.WARNING, "Image file " + filename
              + " exists; not overwritten");
          return false;
        }
      } catch (IOException e) {
        ApplicationLogger.log(Level.WARNING, "Could not write image to file "
            + filename, e);
        return false;
      }
    }
    return true;
  }

  @Override
  public void processFinalImageAndCleanUp(BufferedImage img, Double time) {
    captureImageToFile(img, getFileName(time));
  }

  // /**
  // * Main method listing known image writers
  // *
  // * @param args
  // * Ignored main method arguments
  // */
  // public static void main(String[] args) {
  // List<String> formats = Arrays.asList(ImageIO.getWriterFileSuffixes()
  // // "png", "jpg", "tiff", "tif", "eps"
  // );
  // for (String format : formats) {
  // System.out.print(format + ": ");
  // Iterator<ImageWriter> imWIt = ImageIO
  // .getImageWritersBySuffix(format);
  // while (imWIt.hasNext()) {
  // System.out.println(imWIt.next());
  // }
  // }
  // }
}
