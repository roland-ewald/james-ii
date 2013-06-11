/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.io.Reader;
import java.io.Writer;

/**
 * Interface for implementations that are able to perform actions based on a
 * special {@link ILexerToken}. Implementations may change the content of an
 * input to e.g., omit the special token. One use case might be an
 * {@link IInfoProvider} implementations implementing a spell checker which
 * returns {@link ILexerToken}s that identify supposably misspelled words. An
 * action might be the preposition to correct the misspelled word by replacing
 * the token in the given input by the correct word.
 * 
 * @author Stefan Rybacki
 */
public interface ITokenAction {

  /**
   * Performs actions on the given input.
   * 
   * @param input
   *          the original input the special {@link ILexerToken} was part of
   * @param output
   *          the output the "changed" output
   * @return true, if action was performed successfully
   */
  boolean run(Reader input, Writer output);

  /**
   * Gets the actions description.
   * 
   * @return the description
   */
  String getDescription();
}
