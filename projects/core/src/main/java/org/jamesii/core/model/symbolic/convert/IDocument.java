/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic.convert;

/**
 * The Interface IDocument.
 * 
 * @param <C>
 */
public interface IDocument<C> {

  /**
   * Gets the content of the document (e.g., a string).
   * 
   * @return the content
   */
  C getContent();

  /**
   * Update content. Should update the list of problems as well.
   * 
   * @param newContent
   *          the new content
   */
  void updateContent(C newContent);

}
