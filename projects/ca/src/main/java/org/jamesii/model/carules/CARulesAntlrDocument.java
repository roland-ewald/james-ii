/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.core.model.symbolic.convert.SimpleDocument;

/**
 * The Class CARulesDocument.
 */
public class CARulesAntlrDocument extends SimpleDocument implements
    ICARulesDocument<String> {

  /**
   * Instantiates a new cA rules document.
   * 
   * @param newContent
   *          the new content
   */
  public CARulesAntlrDocument(String newContent) {
    super(newContent);
  }

}
