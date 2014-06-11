/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jamesii.SimSystem;

/**
 * Static class that provides access to precompiled {@link XPathExpression}s
 * that can be used to get data from a plugintype.xml.
 * 
 * @author Stefan Rybacki
 */
final class PluginTypeXPath {
  /**
   * The Constant instance.
   */
  private static final PluginTypeXPath instance = new PluginTypeXPath();

  /**
   * The xpath.
   */
  private final XPath xpath;

  /**
   * The id expression.
   */
  private final XPathExpression idExpr;

  /**
   * The abstract factory expression.
   */
  private final XPathExpression abstractFactoryExpr;

  /**
   * The base factory expression.
   */
  private final XPathExpression baseFactoryExpr;

  /**
   * The parameter expression.
   */
  private final XPathExpression parameterExpr;

  /**
   * The configuration expression.
   */
  private final XPathExpression configurationExpr;
  
  /**
   * The parameter value expression.
   */
  private final XPathExpression parameterValueExpr;
  
  /**
   * The description expression.
   */
  private final XPathExpression descriptionExpr;

  /**
   * the license expression.
   */
  private XPathExpression iconURIExpr;

  /**
   * Instantiates a new plugin type x path. Hidden constructor.
   */
  private PluginTypeXPath() {
    try {
      xpath = XPathFactory.newInstance().newXPath();
      xpath.setNamespaceContext(JamesNamespaceContext.getInstance());

      idExpr = xpath.compile("/plugintype:plugintype/plugintype:id");

      abstractFactoryExpr =
          xpath
              .compile("/plugintype:plugintype/plugintype:abstractfactory/text()");

      baseFactoryExpr =
          xpath.compile("/plugintype:plugintype/plugintype:basefactory/text()");

      parameterExpr =
          xpath.compile("/plugintype:plugintype/plugintype:parameter");
      
      configurationExpr = xpath.compile("/plugintype:plugintype/plugintype:configuration");
      parameterValueExpr = xpath.compile("/plugintype:plugintype/plugintype:parameterValue");

      iconURIExpr = xpath.compile("/plugin:plugin/plugin:icon/@uri");

      descriptionExpr =
          xpath.compile("/plugintype:plugintype/plugintype:description/text()");
    } catch (XPathExpressionException e) {
      SimSystem.report(e);
      throw new RuntimeException(e);
    }
  }

  /**
   * @return the abstractFactoryExpr
   */
  public static XPathExpression getAbstractFactoryExpr() {
    return instance.abstractFactoryExpr;
  }

  /**
   * @return the baseFactoryExpr
   */
  public static XPathExpression getBaseFactoryExpr() {
    return instance.baseFactoryExpr;
  }

  /**
   * @return the descriptionExpr
   */
  public static XPathExpression getDescriptionExpr() {
    return instance.descriptionExpr;
  }

  /**
   * @return the idExpr
   */
  public static XPathExpression getIdExpr() {
    return instance.idExpr;
  }

  /**
   * @return the parameterExpr
   */
  public static XPathExpression getParameterExpr() {
    return instance.parameterExpr;
  }
  
  /**
   * @return the configurationExpr
   */
  public static XPathExpression getConfigurationExpr() {
    return instance.configurationExpr;
  }

  /**
   * Gets the icon uri expression.
   * 
   * @return the icon uri expression
   */
  public static XPathExpression getIconURIExpr() {
    return instance.iconURIExpr;
  }

}
