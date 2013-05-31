/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.io.Serializable;

import org.jamesii.core.util.misc.Strings;

/**
 * The Class Parameter.
 */
public class Parameter implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 9115972881721920115L;

  /** The comment. */
  private String comment;

  /** The default value. */
  private Object defaultValue;

  /** The handler. */
  private ParamHandler handler;

  /** The idents. */
  private String[] idents;

  /** The required. */
  private boolean required = false;

  /**
   * Instantiates a new parameter.
   * 
   * @param idents
   *          the idents
   * @param handler
   *          the handler
   * @param defaultValue
   *          the default value
   * @param comment
   *          the comment
   */
  public Parameter(String[] idents, ParamHandler handler, Object defaultValue,
      String comment) {
    super();
    this.comment = comment;
    this.handler = handler;
    this.idents = idents;
    this.defaultValue = defaultValue;
  }

  /**
   * Gets the comment.
   * 
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Gets the default.
   * 
   * @return the default
   */
  public Object getDefault() {
    return defaultValue;
  }

  /**
   * Gets the handler.
   * 
   * @return the handler
   */
  public ParamHandler getHandler() {
    return handler;
  }

  /**
   * Gets the idents.
   * 
   * @return the idents
   */
  public String[] getIdents() {
    return idents;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (String s : idents) {
      result.append("-");
      result.append(s);
      result.append(" ");
    }
    Object dV = defaultValue;
    /*
     * if ((dV == null) || ((dV instanceof String) &&
     * (((String)dV).compareTo("") == 0))) { dV = "automatic"; }
     */
    result.append("\n");
    result.append("[Default:" + dV + "] \n");
    result.append(Strings.indent(comment, "  "));
    result.append("\n");
    String s = handler.toString();
    if (s.compareTo("") != 0) {
      result.append(s);
      result.append("\n");
    }
    return result.toString();
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }
}
