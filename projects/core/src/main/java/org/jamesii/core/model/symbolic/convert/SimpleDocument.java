/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.symbolic.convert;

/**
 * The Class SimpleDocument. Internally uses a string as representation.
 * 
 */
public class SimpleDocument implements IDocument<String> {

  /** The content. */
  private String content;

  /**
   * Instantiates a new simple document.
   * 
   * @param newContent
   *          the new content
   */
  public SimpleDocument(String newContent) {
    super();
    updateContent(newContent);
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public final void updateContent(String newContent) {
    content = newContent;
  }

}
