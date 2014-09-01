/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.ParameterUtils;

/**
 * Basic class to manage command-line parameters.
 * 
 * @author Jan Himmelspach
 */
public abstract class AbstractParameters implements Serializable, IParameters {

  /** Serialisation ID. */
  private static final long serialVersionUID = 356807161343757150L;

  /** All valid arguments. */
  private Map<String, Parameter> argumentList = new HashMap<>();

  /**
   * List for parameters which do not have a corresponding attribute in the
   * parameters class.
   */
  private Map<String, Object> simpleValues = new HashMap<>();

  /** Overall simulation parameters. */
  private ParameterBlock parameterBlock = ParameterUtils
      .getDefaultExecParamBlock();

  /**
   * Instantiates a new abstract parameters.
   */
  public AbstractParameters() {
    super();
    initParameters();
  }

  /**
   * Gets the help.
   * 
   * @return the help
   */
  public String getHelp() {
    StringBuilder result = new StringBuilder();
    Map<Parameter, Object> l = new HashMap<>();
    for (Parameter p : getArgumentList().values()) {
      if (!l.containsKey(p)) {
        result.append(p.toString());
        l.put(p, null);
      }

    }
    return result.toString();
  }

  @Override
  public List<Parameter> getOptionalParameters() {
    List<Parameter> al = new ArrayList<>();
    for (Parameter p : getArgumentList().values()) {
      if (!p.isRequired()) {
        al.add(p);
      }
    }
    return null;
  }

  /**
   * Gets the parameter.
   * 
   * @param ident
   *          the ident
   * 
   * @return the parameter
   */
  public Parameter getParameter(String ident) {
    return getArgumentList().get(ident);
  }

  @Override
  public List<Parameter> getParameters() {
    return new ArrayList<>(getArgumentList().values());
  }

  /**
   * Returns the value of the given parameter. This parameter may either be an
   * attribute of this object or stored in the simple values list. The parameter
   * is automatically converted to the appropriate return value.
   * 
   * This method will throw an InvalidParameterException if the given ident is
   * not a field and if there is no valid entry in the simpleValues list.
   * 
   * @param <R>
   *          the type of the returned parameter value
   * 
   * @param ident
   *          the name of the attribute (field of the class) or any other string
   * 
   * @return value
   */
  @SuppressWarnings("unchecked")
  public <R> R getParameterValue(String ident) {
    try {
      return (R) getClass().getField(ident).get(this);
    } catch (Exception e) {
      // field not available, thus lookup in simple value list
      if (getSimpleValues().containsKey(ident)) {
        return (R) getSimpleValues().get(ident);
      }
      throw new InvalidParameterException(
          "Trying to read unknown/unset parameter: " + ident, e);

    }
  }

  @Override
  public List<Parameter> getRequiredParameters() {
    List<Parameter> al = new ArrayList<>();
    for (Parameter p : getArgumentList().values()) {
      if (p.isRequired()) {
        al.add(p);
      }
    }
    return al;
  }

  @Override
  public boolean hasParameters() {
    return (getArgumentList().size() == 0);
  }

  /**
   * Inits the parameters.
   */
  protected abstract void initParameters();

  /**
   * Register parameter.
   * 
   * @param opt
   *          the opt
   */
  protected void registerParameter(OptionalParameter opt) {
    List<Parameter> paras = opt.getParameters();
    for (Parameter p : paras) {
      registerParameter(p.getIdents(), p.getHandler(), p.getDefault(),
          p.getComment());
    }
  }

  /**
   * Register parameter.
   * 
   * @param parameterIdent
   *          the parameter ident
   * @param handler
   *          the handler
   * @param comment
   *          the comment
   * @param defaultValue
   *          the default value
   */
  protected void registerParameter(String parameterIdent, ParamHandler handler,
      Object defaultValue, String comment) {
    String[] p = new String[1];
    p[0] = parameterIdent;
    registerParameter(p, handler, defaultValue, comment);
  }

  /**
   * Register parameter.
   * 
   * @param parameterIdents
   *          the parameter idents
   * @param handler
   *          the handler
   * @param comment
   *          the comment
   * @param defaultValue
   *          the default value
   */
  protected void registerParameter(String[] parameterIdents,
      ParamHandler handler, Object defaultValue, String comment) {
    Parameter p =
        new Parameter(parameterIdents, handler, defaultValue, comment);
    for (String s : parameterIdents) {
      if (getArgumentList().containsKey(s)) {
        throw new InvalidParameterException("A parameter with the ident " + s
            + " has already been defined.");
      }
      getArgumentList().put(s, p);
    }
  }

  public Map<String, Parameter> getArgumentList() {
    return argumentList;
  }

  public void setArgumentList(Map<String, Parameter> argumentList) {
    this.argumentList = argumentList;
  }

  public ParameterBlock getParameterBlock() {
    return parameterBlock;
  }

  public void setParameterBlock(ParameterBlock parameterBlock) {
    this.parameterBlock = parameterBlock;
  }

  public Map<String, Object> getSimpleValues() {
    return simpleValues;
  }

  public void setSimpleValues(Map<String, Object> simpleValues) {
    this.simpleValues = simpleValues;
  }

}
