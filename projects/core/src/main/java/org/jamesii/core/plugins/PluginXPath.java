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
 * that can be used to get data from a plugin.xml.
 * 
 * @author Stefan Rybacki, Tobias Helms
 */
final class PluginXPath {

  /**
   * The Constant instance.
   */
  private static final PluginXPath instance = new PluginXPath();

  /**
   * The xpath.
   */
  private final XPath xpath;

  /**
   * The id expression.
   */
  private final XPathExpression idExpr;

  /**
   * The factory expression.
   */
  private final XPathExpression factoryExpr;

  /**
   * The depends expression.
   */
  private XPathExpression dependsExpr;

  /**
   * The parameter expression.
   */
  private XPathExpression parameterExpr;
  
  /**
   * The configuration expression.
   */
  private XPathExpression configurationExpr;
  
  /**
   * The parameter value expression (for default configurations).
   */
  private XPathExpression parameterValueExpr;

  /**
   * The factory description expression.
   */
  private XPathExpression factoryDescriptionExpr;

  /**
   * the license expression.
   */
  private XPathExpression licenseURIExpr;

  /**
   * the license expression.
   */
  private XPathExpression iconURIExpr;

  /**
   * The license text expr.
   */
  private XPathExpression licenseTextExpr;

  /**
   * Hidden constructor.
   */
  private PluginXPath() {
    try {
      xpath = XPathFactory.newInstance().newXPath();
      xpath.setNamespaceContext(JamesNamespaceContext.getInstance());

      idExpr = xpath.compile("/plugin:plugin/plugin:id");

      factoryExpr = xpath.compile("/plugin:plugin/plugin:factory");

      dependsExpr = xpath.compile("/plugin:plugin/plugin:depends");
      parameterExpr = xpath.compile("./plugin:parameter");
      
      configurationExpr = xpath.compile("./plugin:configuration");
      parameterValueExpr = xpath.compile("./plugin:parameterValue");

      factoryDescriptionExpr = xpath.compile("./plugin:description/text()");

      licenseURIExpr = xpath.compile("/plugin:plugin/plugin:license/@uri");

      iconURIExpr = xpath.compile("/plugin:plugin/plugin:icon/@uri");

      licenseTextExpr = xpath.compile("/plugin:plugin/plugin:license");

    } catch (XPathExpressionException e) {
      SimSystem.report(e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Gets the factory expr.
   * 
   * @return the factoryExpr
   */
  public static XPathExpression getFactoryExpr() {
    return instance.factoryExpr;
  }

  /**
   * Gets the id expr.
   * 
   * @return the idExpr
   */
  public static XPathExpression getIdExpr() {
    return instance.idExpr;
  }

  /**
   * Gets the depends expression.
   * 
   * @return the depends expr
   */
  public static XPathExpression getDependsExpr() {
    return instance.dependsExpr;
  }

  /**
   * Gets the parameter expression.
   * 
   * @return the parameter expr
   */
  public static XPathExpression getFactoryParameterExpr() {
    return instance.parameterExpr;
  }

  /**
   * Gets the configuration expression.
   */
  public static XPathExpression getFactoryConfigurationExpr() {
    return instance.configurationExpr;
  }
  
  /**
   * Gets the parameter value expression.
   */
  public static XPathExpression getParameterValueExpr() {
    return instance.parameterValueExpr;
  }
  
  /**
   * Gets the factory description expression.
   * 
   * @return the factory description expr
   */
  public static XPathExpression getFactoryDescriptionExpr() {
    return instance.factoryDescriptionExpr;
  }

  /**
   * Gets the license uri expression.
   * 
   * @return the license uri expression
   */
  public static XPathExpression getLicenseURIExpr() {
    return instance.licenseURIExpr;
  }

  /**
   * Gets the icon uri expression.
   * 
   * @return the icon uri expression
   */
  public static XPathExpression getIconURIExpr() {
    return instance.iconURIExpr;
  }

  /**
   * Gets the license text expression.
   * 
   * @return the license text expression
   */
  public static XPathExpression getLicenseTextExpr() {
    return instance.licenseTextExpr;
  }
}
