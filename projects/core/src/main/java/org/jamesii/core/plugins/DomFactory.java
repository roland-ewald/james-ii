/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.xpath.XPathConstants;

import org.jamesii.SimSystem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * General factory info class for all factories defined within plugin.xml files.
 * Typically, instances of this class are created automatically by the
 * {@link Registry}.
 * 
 * @author Jan Himmelspach, Tobias Helms
 */
public class DomFactory implements IFactoryInfo, Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2893944669851682355L;

  /** The classname. */
  private String classname;

  /** The name. */
  private String name;

  /** The parameters. */
  private List<IParameter> parameters = new ArrayList<>();

  /** The configurations. */
  private List<IConfiguration> configurations = new ArrayList<>();

  /**
   * The description.
   */
  private String description;

  /**
   * The location the factory was defined/announced.
   */
  private String location;

  /**
   * The icon uri.
   */
  private URI iconURI;

  /**
   * Instantiates a new dom factory.
   */
  public DomFactory() {
    super();
  }

  /**
   * Construct new Factory from a DOM element.
   * 
   * @param idElement
   *          the id element
   */
  public DomFactory(Element idElement, String location) {
    try {
      this.location = location;
      classname = idElement.getAttribute("classname");

      name = idElement.getAttribute("name");

      NodeList parameterList =
          (NodeList) PluginXPath.getFactoryParameterExpr().evaluate(idElement,
              XPathConstants.NODESET);
      for (int i = 0; i < parameterList.getLength(); i++) {
        Node curParameter = parameterList.item(i);
        // String curClassName = curFactory.getNodeValue();
        IParameter curPara = new DomParameter((Element) curParameter);
        parameters.add(curPara);
      }

      description =
          (String) PluginXPath.getFactoryDescriptionExpr().evaluate(idElement,
              XPathConstants.STRING);

      NodeList configurationList =
          (NodeList) PluginXPath.getFactoryConfigurationExpr().evaluate(idElement,
              XPathConstants.NODESET);
      for (int i = 0; i < configurationList.getLength(); i++) {
        Node curParameter = configurationList.item(i);
        IConfiguration curConfig = new DomConfiguration((Element) curParameter, parameters);
        configurations.add(curConfig);
      }
      
      
      try {
        // fetch the icon URI, if set
        String uri = idElement.getAttribute("icon");

        if ((uri != null) && (uri.compareTo("") != 0)) {
          URL url = getClass().getResource("/" + uri);

          if (url != null) {
            iconURI = url.toURI();
          }

          if (url == null) {
            SimSystem.report(Level.WARNING,
                "Was not able to locate the icon file: " + uri);
          }
        }

      } catch (URISyntaxException e) {
        SimSystem.report(Level.WARNING, "The icon path for the plug-in "
            + classname + " is invalid.", e);
      }

    } catch (Exception e) {
      SimSystem.report(e);
      // TODO better error handling
    }
  }

  @Override
  public String getClassname() {
    return classname;
  }

  @Override
  public List<IParameter> getParameters() {
    return parameters;
  }
  
  @Override
  public List<IConfiguration> getConfigurations() {
    return configurations;
  }

  /**
   * Sets the classname.
   * 
   * @param classname
   *          the new classname
   */
  public void setClassname(String classname) {
    this.classname = classname;
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

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description.
   * 
   * @param description
   *          the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getPluginDefLocation() {
    return location;
  }

  /**
   * Sets the plugin definition loaction.
   * 
   * @param location
   *          the location
   */
  public void setPluginDefLoaction(String location) {
    this.location = location;
  }

  @Override
  public URI getIconURI() {
    return iconURI;
  }

  /**
   * Set the URI where the icon for the factory can be read from.
   * 
   * @param icon
   */
  public void setIconURI(URI icon) {
    iconURI = icon;
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * Set the friendly name of this factory.
   * 
   * @param name
   */
  public void setName(String name) {
    this.name = name;
  }

}
