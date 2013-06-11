/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model;

import java.net.URI;
import java.net.URISyntaxException;

import org.jamesii.core.data.model.parameter.write.plugintype.ModelParameterFileWriterFactory;

import junit.framework.TestCase;

/**
 * Tests the {@link ModelParameterFileWriterFactory}.
 * 
 * @author Simon Bartels
 * 
 */
public class ModelParameterFileWriterFactoryTest extends TestCase {

  /**
   * Tests {@link ModelParameterFileWriterFactory#getFileFromURI(URI)} with
   * windows and unix like paths.
   * 
   * @throws URISyntaxException
   */
  public void testGetPathStringFromURI() throws URISyntaxException {
    URI u = null;
    String scheme = "file-formalism:/";
    // windows like absolute path
    String windowsLikeAbsolutePath = "C:/dir/model.ending";
    u = new URI(scheme + windowsLikeAbsolutePath);
    assertEquals(windowsLikeAbsolutePath,
        ModelParameterFileWriterFactory.getPathStringFromURI(u));
    // unix like absolute path
    String unixLikeAbsolutePath = "/dir/model.ending";
    u = new URI(scheme + unixLikeAbsolutePath);
    assertEquals(unixLikeAbsolutePath,
        ModelParameterFileWriterFactory.getPathStringFromURI(u));
    // relative path
    String relativePath = "./dir/model.ending";
    u = new URI(scheme + relativePath);
    assertEquals(relativePath.substring(1),
        ModelParameterFileWriterFactory.getPathStringFromURI(u));
  }

}
