/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.parameter.write.plugintype;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.jamesii.core.data.IFileHandling;
import org.jamesii.core.util.misc.Files;

/**
 * Super class of all model parameter file writer factories.
 * 
 * @author Stefan Rybacki
 */
public abstract class ModelParameterFileWriterFactory extends
    ModelParameterWriterFactory implements IFileHandling {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -9070589108539376252L;

  /**
   * Specifies how to encode the file information into a URI.
   * 
   * @param modelFile
   *          the model parameter file
   * @return the URI from file
   * @throws URISyntaxException
   *           the URI syntax exception
   */
  public static URI getURIFromFile(File modelFile) throws URISyntaxException {
    URI uri = modelFile.toURI();

    return new URI("file-" + Files.getFileEnding(modelFile),
        uri.getAuthority(), uri.getPath(), null, null);
  }

  /**
   * Returns file name from URI. URI might define a relative path, ie. instead
   * of 'file-stopi:/C:/cosa/bin/examples/stopi/Benchmark-3000.stopi' one could
   * write 'file-stopi:/./examples/stopi/Benchmark-3000.stopi' (useful for
   * distributed setups).
   * 
   * @param fileURI
   *          the file uri
   * @return the file from uri
   */
  public static File getFileFromURI(URI fileURI) {
    return new File(getPathStringFromURI(fileURI));
  }

  /**
   * Returns file name from URI. URI might define a relative path, ie. instead
   * of 'file-stopi:/C:/cosa/bin/examples/stopi/Benchmark-3000.stopi' one could
   * write 'file-stopi:/./examples/stopi/Benchmark-3000.stopi' (useful for
   * distributed setups).
   * 
   * @param fileURI
   *          the file uri
   * @return the file from uri as string
   */
  public static String getPathStringFromURI(URI fileURI) {
    boolean isRelativePath = false;
    if (fileURI.getPath().length() > 0) {
      isRelativePath =
          fileURI.getSchemeSpecificPart().substring(0, 3).compareTo("/./") == 0;
    }

    // If a relative path is given,
    if (isRelativePath) {
      return fileURI.getSchemeSpecificPart().substring(2);
    }

    return fileURI.getSchemeSpecificPart().substring(1);
  }

  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme().equals("file-" + getFileEnding())) {
      return true;
    }
    return false;
  }

}
