/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.infoprovider;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.Token;
import org.jamesii.SimSystem;
import org.jamesii.gui.model.base.AbstractModelInfoProvider;
import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.model.carules.reader.antlr.parser.CaruleLexer;

/**
 * @author Stefan Rybacki
 */
public class CAStateHighlightProvider extends AbstractModelInfoProvider {

  private final List<CAStateToken> tokens = new ArrayList<>();

  private final ILexerTokenStylizer stylizer = new CAStateHighlightStylizer();

  @Override
  protected void run(Reader input, int cPos) {
    try {
      synchronized (tokens) {
        tokens.clear();

        try {
          CaruleLexer lexer = new CaruleLexer(new ANTLRReaderStream(input));

          int pos = 0;
          Token s;
          s = lexer.nextToken();
          String sState = null;
          while (s != null && s.getType() != -1) {
            String state = s.getText();

            pos += state.length();
            if (s.getType() == CaruleLexer.ID
                && (sState == null || sState.equals(state))) {
              if (pos - state.length() <= cPos && pos >= cPos) {
                sState = state;
              }
              CAStateToken t = new CAStateToken(pos - state.length(), state);
              tokens.add(t);
            }
            s = lexer.nextToken();
          }

          // now delete all CAStateTokens not representing sState
          for (int i = tokens.size() - 1; i >= 0; i--) {
            if (sState == null || !sState.equals(tokens.get(i).getState())) {
              tokens.remove(i);
            }
          }
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }
      fireTokensChanged();
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  public ILexerTokenStylizer getStylizer() {
    return stylizer;
  }

  @Override
  public ILexerToken getToken(int index) {
    return tokens.get(index);
  }

  @Override
  public int getTokenCount() {
    return tokens.size();
  }

  @Override
  public boolean isOnlyStyleToken(ILexerToken token) {
    return true;
  }

  @Override
  protected boolean isCursorChangeSensitive() {
    return true;
  }
}
