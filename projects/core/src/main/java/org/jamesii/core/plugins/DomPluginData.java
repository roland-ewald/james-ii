/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import org.jamesii.SimSystem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The Class DomPluginData.
 * 
 * @author Jan Himmelspach
 * */
public class DomPluginData implements IPluginData, Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7159598209257128637L;

  /** The dependencies. */
  private List<IId> dependencies = new ArrayList<>();

  /** fully qualified class name of a factory. */
  private List<IFactoryInfo> factories = new ArrayList<>();

  /** The id. */
  private IId id;

  /**
   * The license uri.
   */
  private URI licenseURI;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public void setId(IId id) {
    this.id = id;
  }

  public void setLicenseURI(URI licenseURI) {
    this.licenseURI = licenseURI;
  }

  /**
   * The location of the plug-in.
   */
  private String location;

  /**
   * Bean compatibility constructor.
   */
  public DomPluginData() {
  }

  /**
   * Instantiates a new dom plugin data.
   * 
   * @param domData
   *          the dom data
   */
  public DomPluginData(Document domData, String location) {
    this.location = location;
    Element pluginId;
    try {
      pluginId =
          (Element) PluginXPath.getIdExpr().evaluate(domData,
              XPathConstants.NODE);
      id = new DomId(pluginId);

      String uri =
          (String) PluginXPath.getLicenseURIExpr().evaluate(domData,
              XPathConstants.STRING);

      if (uri != null) {
        licenseURI = new URI(uri);
      }

      NodeList factoryList =
          (NodeList) PluginXPath.getFactoryExpr().evaluate(domData,
              XPathConstants.NODESET);
      for (int i = 0; i < factoryList.getLength(); i++) {
        // System.out.println("Factory: "+factoryList.item(i));
        Element curFactory = (Element) factoryList.item(i);
        // String curClassName = curFactory.getNodeValue();
        // System.out.println("Factory: "+curFactory);
        IFactoryInfo curFac = new DomFactory(curFactory, location);
        factories.add(curFac);
      }

      NodeList depList =
          (NodeList) PluginXPath.getDependsExpr().evaluate(domData,
              XPathConstants.NODESET);
      for (int i = 0; i < depList.getLength(); i++) {
        Node curDep = depList.item(i);
        IId curId = new DomId((Element) curDep);
        dependencies.add(curId);
      }
    } catch (Exception e) {
      SimSystem.report(e);
      // TODO better error handling
    }

    sortFactories();
  }

  @Override
  public List<IId> getDependencies() {
    return dependencies;
  }

  @Override
  public List<IFactoryInfo> getFactories() {
    return Collections.unmodifiableList(factories);
  }

  @Override
  public IId getId() {
    return id;
  }

  /**
   * Sets the dependencies.
   * 
   * @param dependencies
   *          the new dependencies
   */
  public void setDependencies(List<IId> dependencies) {
    this.dependencies = dependencies;
  }

  /**
   * Sets the factories.
   * 
   * @param factories
   *          the new factories
   */
  public void setFactories(List<IFactoryInfo> factories) {
    this.factories = new ArrayList<>(factories);
    sortFactories();
  }

  private void sortFactories() {
    Collections.sort(factories, new Comparator<IFactoryInfo>() {
      @Override
      public int compare(IFactoryInfo o1, IFactoryInfo o2) {
        if (o1==null)
          return -1;
        if (o2==null)
          return 1;
        return o1.getClassname().compareTo(o2.getClassname());
      }
    });
  }

  @Override
  public String toString() {
    return id.toString();
  }

  @Override
  public URI getLicenseURI() {
    return licenseURI;
  }

  @Override
  public String getPluginLocation() {
    return location;
  }

}
