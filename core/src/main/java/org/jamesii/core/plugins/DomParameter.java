/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implementation of the {@link IParameter} interface for DOM documents.
 * 
 * @author Jan Himmelspach
 */
public class DomParameter implements IParameter {

  /** Serialisation ID. */
  private static final long serialVersionUID = 598135525314934064L;

  /** Parameter description. */
  private String description;

  /** Parameter name. */
  private String name;

  /** Flag: is this required parameter required?. */
  private Boolean required = false;

  /** Parameter type (FQCN). */
  private String type;

  /** Default value. */
  private String defaultValue;

  /** Plug-in type. */
  private String pluginType;

  /**
   * Constructor for bean compliance. Do NOT use manually!
   */
  public DomParameter() {
  }

  /**
   * Construct new parameter from a DOM element.
   * 
   * @param idElement
   *          the element in the document
   */
  public DomParameter(Element idElement) {
    name = idElement.getAttribute("name");
    defaultValue = idElement.getAttribute("defaultValue");
    pluginType = idElement.getAttribute("plugintype");

    // // Sub-ParameterBlocks for other factories should always contain the FQCN
    // of
    // // the factory as their value, String is therefore set as the
    // // 'mandatory'
    // // type
    // if (hasPluginType()
    // && !"java.lang.String".equals(idElement.getAttribute("type"))) {
    // type = "java.lang.String";
    // SimSystem
    // .report(
    // Level.WARNING,
    // "Parameter has plugin type parameter but specified type was not java.lang.String!");
    // }
    // else
    type = idElement.getAttribute("type");

    required =
        (idElement.getAttribute("required").compareToIgnoreCase("true") == 0);
    Node item = idElement.getElementsByTagName("description").item(0);
    description = item != null ? item.getFirstChild().getNodeValue() : null;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Boolean isRequired() {
    return required;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String getDefaultValue() {
    return defaultValue;
  }

  @Override
  public String getPluginType() {
    return pluginType;
  }

  @Override
  public boolean hasPluginType() {
    return pluginType.length() > 0;
  }

  /**
   * Gets the required.
   * 
   * @return the required
   */
  public Boolean getRequired() {
    return required;
  }

  /**
   * Sets the required.
   * 
   * @param required
   *          the new required
   */
  public void setRequired(Boolean required) {
    this.required = required;
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
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the type.
   * 
   * @param type
   *          the new type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Sets the default value.
   * 
   * @param defaultValue
   *          the new default value
   */
  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  /**
   * Sets the plugin type.
   * 
   * @param pluginType
   *          the new plugin type
   */
  public void setPluginType(String pluginType) {
    this.pluginType = pluginType;
  }

}
