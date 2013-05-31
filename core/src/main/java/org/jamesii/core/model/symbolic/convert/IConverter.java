/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic.convert;

import org.jamesii.core.model.symbolic.ISymbolicModel;

/**
 * The Interface IConverter.
 */
public interface IConverter {

  /**
   * To document.
   * 
   * @param data
   *          the data
   * 
   * @return the i document
   */
  IDocument<?> toDocument(ISymbolicModel<?> data);

  /**
   * From document.
   * 
   * @param document
   *          the document
   * 
   * @return the d
   */
  ISymbolicModel<?> fromDocument(IDocument<?> document);

}
