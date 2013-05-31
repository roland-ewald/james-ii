/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class OptionalParameter.
 */
public class OptionalParameter {

  /** The default value. */
  private Object defaultValue;

  /** The parameter ident. */
  private String parameterIdent;

  /** The paras. */
  private List<Parameter> paras = new ArrayList<>();

  /**
   * The Constructor.
   * 
   * @param parameterIdent
   *          the parameter ident
   * @param defaultValue
   *          the default value
   */
  public OptionalParameter(String parameterIdent, Object defaultValue) {
    super();
    this.defaultValue = defaultValue;
    this.parameterIdent = parameterIdent;
  }

  /**
   * Adds the.
   * 
   * @param handler
   *          the handler
   * @param comment
   *          the comment
   * @param paraIdent
   *          the para ident
   */
  public void add(String paraIdent, ParamHandler handler, String comment) {
    String[] p = new String[1];
    p[0] = paraIdent;
    add(p, handler, comment);
  }

  /**
   * Adds the.
   * 
   * @param parameterIdents
   *          the parameter idents
   * @param handler
   *          the handler
   * @param comment
   *          the comment
   */
  public void add(String[] parameterIdents, ParamHandler handler, String comment) {
    Parameter p =
        new Parameter(parameterIdents, handler, defaultValue, comment);
    paras.add(p);
  }

  /**
   * Gets the parameters.
   * 
   * @return the parameters
   */
  public List<Parameter> getParameters() {
    return paras;
  }

}
