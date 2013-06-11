/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.IConstraint;
import org.jamesii.core.util.misc.XML;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Checker for enumeration restrictions
 * 
 * Created on June 2, 2004.
 * 
 * @author Roland Ewald
 */
public class EnumerationConstraint implements IConstraint<String> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1079376000467738149L;

  /** The enumeration. */
  private List<String> enumeration = null;

  /**
   * Lazy constructor.
   */
  public EnumerationConstraint() {
    this(new ArrayList<String>());
  }

  /**
   * Default constructor.
   * 
   * @param en
   *          enumeration
   */
  public EnumerationConstraint(List<String> en) {
    this.enumeration = en;
  }

  /**
   * Configure.
   * 
   * @param node
   *          the node
   * 
   * @return true, if configure
   */
  public boolean configure(Node node) {

    boolean isNeeded = false;

    NodeList nl = node.getChildNodes();

    for (int i = 0; i < nl.getLength(); i++) {

      Node n = nl.item(i);

      if (XML.getLocalName(n.getNodeName()).equalsIgnoreCase("enumeration")) {

        String value = XML.getAttributeOfNode(n, "value");

        if (value != null) {
          enumeration.add(value);
          isNeeded = true;
        }

      }
    }

    return isNeeded;
  }

  /**
   * Gets the copy.
   * 
   * @return the copy
   * 
   * @see org.jamesii.core.util.IConstraint#getCopy()
   */
  @Override
  public IConstraint<String> getCopy() {
    return new EnumerationConstraint(new ArrayList<>(enumeration));
  }

  /**
   * Needed to display this constraint as a combobox.
   * 
   * @return enumeration ArrayList
   */
  public List<String> getEnumeration() {
    return this.enumeration;
  }

  /**
   * Checks if is fulfilled.
   * 
   * @param value
   *          the value
   * 
   * @return true, if checks if is fulfilled
   */
  @Override
  public boolean isFulfilled(String value) {

    if (enumeration.contains(value)) {
      return true;
    }

    return false;
  }

  /**
   * Sets the enumeration.
   * 
   * @param enumeration
   *          the new enumeration
   */
  public void setEnumeration(List<String> enumeration) {
    this.enumeration = enumeration;
  }
}
