package org.jamesii.core.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of the {@link IConfiguration} interface for DOM documents.
 * 
 * @author Tobias Helms
 * 
 */
public class DomConfiguration implements IConfiguration {

  /** Serialization ID. */
  private static final long serialVersionUID = -7020238312656981388L;

  /** The description of this configuration. */
  private String description;

  /** The name of this configuration. */
  private String name;

  /** The values of this configuration. */
  private Map<String, Object> mapping = new HashMap<>();

  /**
   * Check if the given parameter exist and if the value has the correct type.
   */
  private Object checkParameterValue(String name, String value,
      List<IParameter> parameters) throws Exception {

    for (IParameter parameter : parameters) {
      if (parameter.getName().equals(name)) {
        if (parameter.getType().equals("java.lang.Boolean")) {
          return value.equals("true") ? true : (value.equals("false") ? false
              : null);
        }
        if (parameter.getType().equals("java.lang.Character")) {
          return value.length() == 1 ? value.charAt(0) : null;
        }
        return Class.forName(parameter.getType())
            .getMethod("valueOf", String.class).invoke(null, value);
      }
    }

    return null;
  }

  /**
   * Construct new configuration from a DOM element.
   * 
   * @param idElement
   *          the element in the document
   */
  public DomConfiguration(Element idElement, List<IParameter> parameters)
      throws XPathExpressionException {
    name = idElement.getAttribute("name");
    Node item = idElement.getElementsByTagName("description").item(0);
    description = item != null ? item.getFirstChild().getNodeValue() : null;

    // compute the parameter setting
    NodeList list =
        (NodeList) PluginXPath.getParameterValueExpr().evaluate(idElement,
            XPathConstants.NODESET);
    for (int i = 0; i < list.getLength(); i++) {
      Node curValue = list.item(i);
      String name =
          curValue.getAttributes().getNamedItem("name").getNodeValue();
      String value =
          curValue.getAttributes().getNamedItem("value").getNodeValue();

      try {
        if (mapping.containsKey(name)) {
          throw new XPathExpressionException("The parameter " + name
              + " is defined multiple times!");
        }
        Object convertedValue = checkParameterValue(name, value, parameters);
        if (convertedValue == null) {
          throw new XPathExpressionException("The parameter " + name
              + " is not defined!");
        }
        mapping.put(name, convertedValue);
      } catch (Exception e) {
        throw new XPathExpressionException("The value of the parameter " + name
            + " does not match the parameter type!" + e);
      }
    }

    // add default values of missing primitive parameters
    for (IParameter parameter : parameters) {
      if (parameter.isRequired() && parameter.getPluginType().isEmpty()
          && !mapping.containsKey(parameter.getName())) {
        if (parameter.getDefaultValue().isEmpty()) {
          throw new XPathExpressionException(
              "Parameter "
                  + parameter.getName()
                  + " is not specified within the configuration and has no default value!");
        }
        mapping.put(parameter.getName(), parameter.getDefaultValue());
      }
    }
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
  public Map<String, Object> getMapping() {
    return mapping;
  }

}
