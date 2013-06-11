/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resolves an unique schema location to a local path.
 * 
 * @author Mathias Roehl
 */
public class SchemaResolver implements EntityResolver {

  /** The Constant ALL_NAMESPACES. */
  public static final String[] ALL_NAMESPACES = {
      "http://www.jamesii.org/plugin", "http://www.jamesii.org/plugintype" };

  /** NAMESPACE */
  private static final String NAMESPACE = "http://www.jamesii.org";

  /** Prefix for path */
  private static String PATH = "/org/jamesii/core/plugins";

  /**
   * Looks up XML entity references on the current classpath. We assume the
   * entity to be resolved is located in the same JAR-file or in the same
   * directory sub-tree as the caller.
   * 
   * @param systemId
   *          relative path (w.r.t. the caller) to the source
   * @return the input source
   */
  private static InputSource resolveEntity(final String systemId) {

    InputSource inputSource = null;

    try {
      InputStream inputStream =
          SchemaResolver.class.getResourceAsStream(systemId);
      if (inputStream == null) {
        throw new PluginLoadException("ERROR: " + systemId + " not found");
      }
      inputSource = new InputSource(inputStream);
    } catch (NullPointerException e) {
      throw new PluginLoadException("ERROR: <" + systemId + "> not found"
          + "\n maybe a wrong namespace has been specified in a XML file", e);
    }

    return inputSource;
  }

  /**
   * resolves an XML entity based on its unique ID.
   * 
   * @param publicID
   *          the public id
   * @param systemID
   *          the system id
   * @return the input source
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws SAXException
   *           the SAX exception
   */
  @Override
  public final InputSource resolveEntity(String publicID, String systemID)
      throws IOException, SAXException {
    // TODO (why is this class loading xsds for namespace declarations
    // without schema definitions?)
    // There should also be a better way to redirect xsd declarations
    // than harcoding it (also why should http:// xsd or namespace
    // requests be redirected if they are available online?)
    // System.out.println(systemID + ":" + publicID);
    String path = systemID;
    if (systemID.contains("plugin.xsd")) {
      path = PATH + "/plugin.xsd";
    } else if (systemID.contains("plugintype.xsd")) {
      path = PATH + "/plugintype.xsd";
    } else if (systemID.contains("base.xsd")) {
      path = PATH + "/base.xsd";
    } else if (systemID.contains("parameter.xsd")) {
      path = PATH + "/parameter.xsd";
    } else if (systemID.startsWith("http://")) {
      // TODO is this needed? We could return null or empty
      // InputSource
      path = systemID.replace(NAMESPACE, PATH) + ".xsd";
    }

    return resolveEntity(path);
  }
}
