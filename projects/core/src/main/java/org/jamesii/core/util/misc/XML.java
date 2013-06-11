/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// TODO: Auto-generated Javadoc
/**
 * Class for XML-specific utility functions.
 * 
 * Created: 23.05.2004
 * 
 * @author Roland Ewald
 */

public final class XML {

  /**
   * Hidden constructor.
   */
  private XML() {
  }

  /**
   * Gets an attribute of a node (that is an xml-tag).
   * 
   * @param n
   *          the node
   * @param attr
   *          the attribute
   * 
   * @return String value of attribute, if it's not there: null
   */
  public static String getAttributeOfNode(Node n, String attr) {
    NamedNodeMap attribs = n.getAttributes();
    if (attribs != null) {
      for (int i = 0; i < attribs.getLength(); i++) {
        if (attribs.item(i).getNodeName().equals(attr)) {
          return attribs.item(i).getNodeValue();
        }
      }
    }
    return null;
  }

  /**
   * Returns all sub-nodes of a node object that are element nodes.
   * 
   * @param node
   *          the node
   * 
   * @return list of nodes
   */
  public static List<Node> getChildElements(Node node) {

    List<Node> elementList = new ArrayList<>();

    NodeList nodeList = node.getChildNodes();

    for (int i = 0; i < nodeList.getLength(); i++) {
      if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
        elementList.add(nodeList.item(i));
      }
    }

    return elementList;
  }

  /**
   * Returns the name of an XML element name without namespace prefix.
   * 
   * @param elementName
   *          the element name
   * 
   * @return the local name
   */
  public static String getLocalName(String elementName) {
    if (elementName.indexOf(':') >= 0) {
      return elementName.substring(elementName.indexOf(':') + 1);
    }

    return elementName;
  }

  /**
   * The value of an element node in a DOM tree is saved in a single sub-node.
   * 
   * @param node
   *          the node
   * 
   * @return value of the node
   */
  public static String getValueOfNode(Node node) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      return node.getFirstChild().getNodeValue();
    }
    return node.getNodeValue();
  }
}
