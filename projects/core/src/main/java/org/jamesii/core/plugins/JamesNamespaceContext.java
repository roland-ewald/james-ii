/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Class that provides a {@link NamespaceContext} implementation that is used
 * with {@link PluginTypeXPath} and {@link PluginXPath} providing access to
 * defined prefixes for the plugin and plugintype namespaces. The prefix for the
 * plugintype would be <b>plugintype</b> and the prefix for plugin namespace
 * would be <b>plugin</b>.
 * 
 * @author Stefan Rybacki
 */
final class JamesNamespaceContext implements NamespaceContext {
  /**
   * The Constant instance.
   */
  private static final NamespaceContext instance = new JamesNamespaceContext();

  /**
   * The Constant PLUGIN_NAMESPACE.
   */
  public static final String PLUGIN_NAMESPACE = "http://www.jamesii.org/plugin";

  /**
   * The Constant PLUGINTYPE_NAMESPACE.
   */
  public static final String PLUGINTYPE_NAMESPACE =
      "http://www.jamesii.org/plugintype";

  /**
   * hidden constructor
   */
  private JamesNamespaceContext() {
  }

  /**
   * Gets the namespace uri.
   * 
   * @param prefix
   *          the prefix
   * @return the namespace uri
   */
  @Override
  public String getNamespaceURI(String prefix) {
    if (prefix == null) {
      throw new NullPointerException("Null prefix");
    } else if ("plugin".equals(prefix)) {
      return PLUGIN_NAMESPACE;
    } else if ("plugintype".equals(prefix)) {
      return PLUGINTYPE_NAMESPACE;
    } else if ("xml".equals(prefix)) {
      return XMLConstants.XML_NS_URI;
    }
    return XMLConstants.NULL_NS_URI;
  }

  /**
   * Gets the prefix.
   * 
   * @param namespaceURI
   *          the namespace uri
   * @return the prefix
   */
  @Override
  public String getPrefix(String namespaceURI) {
    if (PLUGIN_NAMESPACE.equals(namespaceURI)) {
      return "plugin";
    }
    if (PLUGINTYPE_NAMESPACE.equals(namespaceURI)) {
      return "plugintype";
    }
    return null;
  }

  /**
   * Gets the prefixes.
   * 
   * @param namespaceURI
   *          the namespace uri
   * @return the prefixes
   */
  @Override
  public Iterator<?> getPrefixes(String namespaceURI) {
    throw new UnsupportedOperationException("not supported!");
  }

  /**
   * Gets the singleton instance of {@link JamesNamespaceContext}.
   * 
   * @return singleton instance of {@link JamesNamespaceContext}
   */
  public static NamespaceContext getInstance() {
    return instance;
  }

}
