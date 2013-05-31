/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property;

/**
 * Abstract implementation of the IProperty interface. All IProperty
 * implementations should subclass this class to have proper {@link #hashCode()}
 * and {@link #equals(Object)} implementations.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractProperty implements IProperty {

  /**
   * The type.
   */
  private Class<?> type;

  /**
   * The name.
   */
  private String name;

  /**
   * The description.
   */
  private String description;

  /**
   * Instantiates a new abstract property.
   * 
   * @param name
   *          the name
   * @param type
   *          the type
   */
  public AbstractProperty(String name, Class<?> type) {
    this(name, type, null);
  }

  /**
   * Instantiates a new abstract property.
   * 
   * @param name
   *          the name
   * @param type
   *          the type
   * @param description
   *          the description
   */
  public AbstractProperty(String name, Class<?> type, String description) {
    this.type = type;
    this.name = name;
    this.description = description;
  }

  @Override
  public int hashCode() {
    return getName().hashCode() * 31 + getType().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !obj.getClass().equals(getClass())) {
      return false;
    }

    IProperty p = (IProperty) obj;

    return getName().equals(p.getName()) && getType().equals(p.getType());
  }

  @Override
  public Class<?> getType() {
    return type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }
}
