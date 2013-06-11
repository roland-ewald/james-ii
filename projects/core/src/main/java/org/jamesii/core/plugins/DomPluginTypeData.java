/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.jamesii.SimSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The Class DomPluginTypeData.
 */
public class DomPluginTypeData implements IPluginTypeData, Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3201593359527522435L;

  /** The abstract factory. See {@link #getAbstractFactory()}. */
  private String abstractFactory = "";

  /** The base factory. See {@link #getBaseFactory()}. */
  private String baseFactory = "";

  /** The description. See {@link #getDescription()}. */
  private String description = "";

  /** The id. */
  private IId id;

  /** The parameters. */
  private List<IParameter> parameters = new ArrayList<>();

  /**
   * Instantiates a new dom plugin type data.
   */
  public DomPluginTypeData() {
    super();
  }

  /**
   * Instantiates a new dom plugin type data.
   * 
   * @param domData
   *          the dom data
   */
  public DomPluginTypeData(Document domData) {
    this();
    // read plugintype data using xpath expressions
    try {
      id =
          new DomId((Element) PluginTypeXPath.getIdExpr().evaluate(domData,
              XPathConstants.NODE));

      abstractFactory =
          (String) PluginTypeXPath.getAbstractFactoryExpr().evaluate(domData,
              XPathConstants.STRING);

      if (abstractFactory == null) {
        throw new RuntimeException("no abstract factory given " + id);
      }

      baseFactory =
          (String) PluginTypeXPath.getBaseFactoryExpr().evaluate(domData,
              XPathConstants.STRING);

      if (baseFactory == null) {
        throw new RuntimeException("no base factory given " + id);
      }

      NodeList parameterList =
          (NodeList) PluginTypeXPath.getParameterExpr().evaluate(domData,
              XPathConstants.NODESET);

      for (int i = 0; i < parameterList.getLength(); i++) {
        Node curParameter = parameterList.item(i);
        // String curClassName = curFactory.getNodeValue();
        IParameter curPara = new DomParameter((Element) curParameter);
        parameters.add(curPara);
      }

      description =
          (String) PluginTypeXPath.getDescriptionExpr().evaluate(domData,
              XPathConstants.STRING);

    } catch (XPathExpressionException e) {
      SimSystem.report(e);
      // TODO better error handling
    }
  }

  @Override
  public String getAbstractFactory() {
    return abstractFactory;
  }

  @Override
  public String getBaseFactory() {
    return baseFactory;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public IId getId() {
    return id;
  }

  @Override
  public List<IParameter> getParameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return id + " \n " + abstractFactory + "  \n " + baseFactory + " \n "
        + description;
  }

  /**
   * Sets the abstract factory.
   * 
   * @param abstractFactory
   *          the new abstract factory
   */
  public void setAbstractFactory(String abstractFactory) {
    this.abstractFactory = abstractFactory;
  }

  /**
   * Sets the base factory.
   * 
   * @param baseFactory
   *          the new base factory
   */
  public void setBaseFactory(String baseFactory) {
    this.baseFactory = baseFactory;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the parameters.
   * 
   * @param parameters
   *          the new parameters
   */
  public void setParameters(List<IParameter> parameters) {
    this.parameters = parameters;
  }

  /**
   * Set the id of the plug-in type
   * 
   * @param id
   */
  public void setId(IId id) {
    this.id = id;
  }

}
