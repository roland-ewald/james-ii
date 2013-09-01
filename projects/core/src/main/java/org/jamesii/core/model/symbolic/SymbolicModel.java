/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic;

import org.jamesii.SimSystem;
import org.jamesii.core.base.NamedEntity;
import org.jamesii.core.data.model.read.plugintype.IMIMEType;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.model.symbolic.convert.ConversionException;
import org.jamesii.core.model.symbolic.convert.IConverter;
import org.jamesii.core.model.symbolic.convert.IDocument;
import org.jamesii.core.model.symbolic.convert.SimpleDocument;
import org.jamesii.core.model.symbolic.convert.plugintype.AbstractConverterFactory;
import org.jamesii.core.model.symbolic.convert.plugintype.ConverterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The Class SymbolicModel is a simple implementation of the ISymbolicModel
 * interface. Only two methods are implemented, the remaining interface
 * functionality is not supported, and the corresponding default values are
 * returned. This basic implementation only supports a textual representation of
 * the symbolic model. <br>
 * This class can, but does not have to be used as ancestor class for your own
 * symbolic model implementations. By extending NamedEntity it can automatically
 * be observed.
 * 
 * @param <D>
 *          the internal data structure of the symbolic model
 */
public class SymbolicModel<D> extends NamedEntity implements ISymbolicModel<D> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 475676718282539498L;

  /** The model */
  private D model;

  /** The mime type of the source code present. */
  private IMIMEType mime;

  /** The textual representation of the symbolic model. */
  private String src;

  /**
   * Instantiates a new symbolic model.
   */
  public SymbolicModel() {
    super();
  }

  /**
   * Instantiates a new symbolic model.
   * 
   * @param name
   *          the name
   */
  public SymbolicModel(String name) {
    super(name);
  }

  @Override
  public D getAsDataStructure() {
    return model;
  }

  @Override
  public IDocument<?> getAsDocument(Class<? extends IDocument<?>> targetFormat) {
    try {
      IConverter converter = getConverter(targetFormat);
      return converter.toDocument(this);
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return null;
  }

  /**
   * Gets the converter.
   * 
   * @param targetFormat
   *          the target format
   * 
   * @return the converter
   */
  private IConverter getConverter(Class<?> targetFormat) {
    ParameterBlock pb = new ParameterBlock();
    pb.addSubBl(AbstractConverterFactory.TYPE, targetFormat);
    ConverterFactory cF = null;
    try {
      cF =
          SimSystem.getRegistry()
              .getFactory(AbstractConverterFactory.class, pb);
    } catch (NoFactoryFoundException nothingFound) {
      if (targetFormat.equals(SimpleDocument.class)) {
        return new DefaultConverter();
      }

      throw new ConversionException("Configuration problem: No converter for "
          + this.getClass().getCanonicalName() + " to a document of type "
          + targetFormat + " exists!", nothingFound);
    }
    pb.setValue(this);
    return cF.create(pb);
  }

  @Override
  public boolean setFromDataStructure(D model) {
    this.model = model;
    changed();
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean setFromDocument(IDocument<?> model) {
    IConverter converter = getConverter(model.getClass());
    return setFromDataStructure(((ISymbolicModel<D>) converter
        .fromDocument(model)).getAsDataStructure());
  }

  /**
   * The Class DefaultConverter.
   */
  private class DefaultConverter implements IConverter {

    @Override
    public ISymbolicModel<?> fromDocument(IDocument<?> document) {
      return null;
    }

    @Override
    public IDocument<?> toDocument(ISymbolicModel<?> data) {
      return new SimpleDocument(data.toString());
    }

  }

  @Override
  public void removeSource() {
    src = null;
    mime = null;
  }

  @Override
  public void setSource(String src, IMIMEType mime) {
    if (src == null || mime == null) {
      throw new SymbolicModelException("Source and Mime Type must be non null!");
    }
    this.src = src;
    this.mime = mime;
  }

  @Override
  public boolean isSourceAvailable() {
    return src != null;
  }

  @Override
  public String getSource() {
    return src;
  }

  @Override
  public IMIMEType getSourceMimeType() {
    return mime;
  }

}
