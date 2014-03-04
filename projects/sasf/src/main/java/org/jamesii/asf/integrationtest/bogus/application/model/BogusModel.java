/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.model;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.model.Model;
import org.jamesii.core.model.symbolic.convert.IDocument;

/**
 * The one and only BogusModel. Used in for integration testing the whole
 * algorithm selection procedure.
 * 
 * @author Roland Ewald
 */
public class BogusModel extends Model implements IBogusModel {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -629187859281417662L;

  /** The content of the bogus model. */
  private final Map<String, Serializable> content;

  /**
   * Instantiates a new bogus model.
   * 
   * @param parameters
   *          the parameters
   */
  public BogusModel(Map<String, Serializable> parameters) {
    content = parameters;
  }

  @Override
  public IBogusModel getAsDataStructure() {
    return this;
  }

  @Override
  public IDocument<?> getAsDocument(Class<? extends IDocument<?>> targetFormat) {
    return null;
  }

  @Override
  public boolean setFromDataStructure(IBogusModel model) {
    return false;
  }

  @Override
  public boolean setFromDocument(IDocument<?> model) {
    return false;
  }

  @Override
  public Map<String, Serializable> getContent() {
    return content;
  }

  @Override
  public void removeSource() {
  }

  @Override
  public void setSource(String src, IMIMEType mime) {
  }

  @Override
  public boolean isSourceAvailable() {
    return false;
  }

  @Override
  public String getSource() {
    return null;
  }

  @Override
  public IMIMEType getSourceMimeType() {
    return null;
  }

}
