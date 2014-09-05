/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.model.implemented;

import java.net.URI;

import org.jamesii.core.data.model.IModelReader;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.symbolic.ISymbolicModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Model reader factory to support 'reading' implemented models.
 * 
 * @author Roland Ewald
 * 
 *         04.06.2007
 * 
 */
public class ImplementedModelReaderFactory extends ModelReaderFactory {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1L;

  @Override
  public IModelReader create(ParameterBlock params, Context context) {
    return new ImplementedModelReader();
  }

  @Override
  public boolean supportsModel(IModel model) {
    return true;
  }

  /**
   * Implemented models are given as qualified java class names and with the
   * scheme 'java'.
   * 
   * @param uri
   *          URI to be checked
   * @return true if supported
   * @see org.jamesii.core.data.model.read.plugintype.ModelReaderFactory#supportsURI(java.net.URI)
   */
  @Override
  public boolean supportsURI(URI uri) {
    if (uri.getScheme() == null) {
      return false;
    }
    return uri.getScheme().equals("java");
  }

  @Override
  public boolean supportsModel(ISymbolicModel<?> model) {
    return true;
  }

  @Override
  public boolean supportsMIMEType(IMIMEType mime) {
    // TODO Auto-generated method stub
    return false;
  }

}
