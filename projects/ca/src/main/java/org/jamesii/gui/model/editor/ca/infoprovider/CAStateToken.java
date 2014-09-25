/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.infoprovider;

import org.jamesii.gui.syntaxeditor.DefaultLexerToken;
import org.jamesii.model.carules.reader.antlr.parser.CaruleLexer;

/**
 * @author Stefan Rybacki
 * 
 */
class CAStateToken extends DefaultLexerToken<Integer> {

  /**
   * The state.
   */
  private String state;

  /**
   * @param start
   * @param state
   */
  public CAStateToken(int start, String state) {
    super(CaruleLexer.ID, start, state.length());
    this.state = state;
  }

  /**
   * @return the state
   */
  public String getState() {
    return state;
  }
}
