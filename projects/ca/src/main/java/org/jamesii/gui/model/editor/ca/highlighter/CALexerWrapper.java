/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.highlighter;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.Token;
import org.jamesii.SimSystem;
import org.jamesii.gui.syntaxeditor.ILexer;
import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.model.carules.reader.antlr.parser.CALexerToken;
import org.jamesii.model.carules.reader.antlr.parser.CaruleLexer;

/**
 * Wraps around the {@link CaruleLexer} and implements the {@link ILexer}
 * interface.
 */
public class CALexerWrapper implements ILexer {

  /**
   * The lexer.
   */
  private CaruleLexer lexer = new CaruleLexer();

  /**
   * The syntax tokens.
   */
  private List<ILexerToken> syntaxTokens = new ArrayList<>();

  /**
   * The pos.
   */
  private int pos;

  /**
   * The parsing lock.
   */
  private Lock parsingLock = new ReentrantLock();

  /**
   * The stop flag.
   */
  private boolean stop = false;

  @Override
  public List<ILexerToken> getSyntaxTokens() {
    return syntaxTokens;
  }

  /**
   * Helper method that creates the tokens.
   */
  private void createTokens() {
    syntaxTokens.clear();

    try {
      Token s;
      s = lexer.nextToken();
      while (s != null && s.getType() != -1 && !stop) {
        ILexerToken t =
            new CALexerToken(s.getType(), pos, s.getText().length());
        pos += s.getText().length();
        if (s.getType() != CaruleLexer.WHITESPACE) {
          syntaxTokens.add(t);
        }
        s = lexer.nextToken();
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  public void parse(Reader input) {
    parsingLock.lock();
    try {

      stop = false;
      pos = 0;

      lexer.setCharStream(new ANTLRReaderStream(input));
      createTokens();
    } catch (IOException e) {
      SimSystem.report(e);
    } finally {
      parsingLock.unlock();
    }

  }

  @Override
  public void stopParsing() {
    stop = true;
  }
}
