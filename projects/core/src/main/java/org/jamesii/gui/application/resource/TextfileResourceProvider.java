/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.BasicUtilities;

/**
 * Resource provider providing a string representation of a specified text file.
 * The text file is identified by a location and an encoding can be specified as
 * parameter e.g. {@code textfile:location?encoding=UTF8}. If no encoding is
 * specified the systems standard encoding is used. It is recommended to specify
 * an encoding at all times.
 * 
 * @author Stefan Rybacki
 */
final class TextfileResourceProvider implements IResourceProvider {

  private static final int BUFFER = 1024;

  /**
   * singleton instance of this provider
   */
  private static final IResourceProvider INSTANCE =
      new TextfileResourceProvider();

  @Override
  public Object getResourceFor(String location, Map<String, String> params,
      Class<?> requestingClass) {
    Reader reader = null;
    try {
      InputStream stream = null;
      if (requestingClass == null) {
        stream = getClass().getResourceAsStream(location);
      } else {
        stream = requestingClass.getResourceAsStream(location);
      }

      if (stream == null) {
        throw new FileNotFoundException("Could not open: " + location);
      }

      StringBuilder buffer = new StringBuilder();

      // see if there is a special encoding given as parameter
      String encoding = params.get("encoding");

      if (encoding == null) {
        reader = new BufferedReader(new InputStreamReader(stream));
      } else {
        reader = new BufferedReader(new InputStreamReader(stream, encoding));
      }

      char[] cBuf = new char[BUFFER];
      int readCount;
      while ((readCount = reader.read(cBuf)) >= 0) {
        buffer.append(cBuf, 0, readCount);
      }

      return buffer.toString();
    } catch (IOException e) {
      SimSystem.report(e);
    } finally {
      org.jamesii.core.util.BasicUtilities.close(reader);
    }

    return null;
  }

  /**
   * @return an instance of this provider
   */
  public static IResourceProvider getInstance() {
    return INSTANCE;
  }

  /**
   * omitted constructor
   */
  private TextfileResourceProvider() {
  }

  @Override
  public boolean canHandleDomain(String domain) {
    return domain.equals("textfile");
  }

  @Override
  public String[] getSupportedDomains() {
    return new String[] { "textfile" };
  }
}
