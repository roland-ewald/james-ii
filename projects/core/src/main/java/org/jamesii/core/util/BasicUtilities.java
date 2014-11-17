package org.jamesii.core.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;

import org.jamesii.SimSystem;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Basic utility functions.
 * 
 * Originally part of {@link org.jamesii.gui.utils.BasicUtilities}, but moved
 * out of gui package as they were used elsewhere in the core.
 * 
 * TODO: several methods may fit better in different places and/or in a more
 * appropriately named class
 * 
 * @author Stefan Rybacki
 */
public class BasicUtilities {

  /**
   * Make factory class name readable.
   * 
   * @param fName
   *          the factory name
   * @return the readable version of the factory name
   */
  public static String makeFactoryClassNameReadable(String fName) {
    if (fName == null) {
      return null;
    }
  
    String result = fName.replaceAll("Factory", "");
    result = result.replaceAll("_", " ");
  
    // now only use the simple class name version in case its fully
    // qualified
    int a = result.lastIndexOf('.');
    if (a >= 0) {
      result = result.substring(a + 1);
    }
  
    return makeCamelCaseReadable(result);
  }

  /**
   * Make factory class name readable.
   * 
   * @param f
   *          the factory
   * @return the readable version of the factory name
   */
  public static String makeFactoryClassNameReadable(Factory f) {
    return makeFactoryClassNameReadable(f.getClass().getName());
  }

  /**
   * Make camel case readable.
   * 
   * @param camelCase
   *          the camel case string
   * @return the readable version of the camel case string
   */
  public static String makeCamelCaseReadable(String camelCase) {
    if (camelCase == null) {
      return null;
    }
    // acronyms
    String result =
        camelCase.replaceAll("([A-Z0-9]+)([A-Z0-9]|\\z|\\s)", "$1 $2");
    // camelcase splitting
    result = result.replaceAll("([a-z])([A-Z])", "$1 $2");
  
    return result.trim();
  }

  /**
   * Auxiliary function for displaying log messages related to experiment I/O.
   * 
   * @param param
   *          parameter block of experiment reader/writer factory
   * @return the location of the experiment
   */
  public static String getExpLocation(ParameterBlock param) {
    if (!param.hasSubBlock(AbstractExperimentReaderFactory.EXPERIMENT_INFO)
        || !(param
            .getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO) instanceof ExperimentInfo)) {
      return "UNKNOWN";
    }
    return BasicUtilities.displayURI(((ExperimentInfo) param
        .getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO))
        .getIdent());
  }

  /**
   * Displays a {@link URI} using {@link URLDecoder} with encoding from
   * {@link SimSystem#getEncoding()}. Useful for displaying save/load location
   * in the status bar.
   * 
   * @param uri
   *          the URI to be decoded
   * @return decoded String
   */
  public static String displayURI(URI uri) {
    String decodedURL = "";
    try {
      decodedURL = URLDecoder.decode(uri.toString(), SimSystem.getEncoding());
    } catch (UnsupportedEncodingException e) {
      SimSystem.report(e);
    }
    return decodedURL;
  }

  /**
   * Helper method that should be used in a finally block when closing
   * {@link AutoCloseable} objects such as {@link InputStream},
   * {@link java.io.OutputStream} and so on. It checks for <code>null</code> and
   * catches exceptions that might be thrown when closing silently.
   * 
   * @param closeable
   *          the object to close
   */
  public static void close(AutoCloseable closeable) {
    if (closeable == null) {
      return;
    }
  
    try {
      closeable.close();
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  /**
   * Helper method that checks the java version.
   * 
   * @param requiredVersion
   *          specifies the required version e.g. "1.6"
   * 
   * @return true, if successful
   */
  public static boolean checkJavaVersion(final String requiredVersion) {
    // check java version first
    String usedVersion = System.getProperty("java.specification.version");
    boolean sufficient = false;
    try {
      sufficient =
          !(usedVersion == null || Double.valueOf(usedVersion).doubleValue() < Double
              .valueOf(requiredVersion).doubleValue());
    } catch (Exception e) {
    }
    return sufficient;
  }

}
