/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.infoprovider;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.gui.model.base.AbstractModelInfoProvider;
import org.jamesii.gui.model.base.IModelInfoProvider;
import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.ITokenAction;
import org.jamesii.gui.syntaxeditor.ReplaceTokenAction;
import org.jamesii.model.carules.reader.antlr.parser.CAProblemToken;
import org.jamesii.model.carules.reader.antlr.parser.CaruleLexer;
import org.jamesii.model.carules.reader.antlr.parser.CaruleParser;
import org.jamesii.model.carules.reader.antlr.parser.CaruleParser.camodel_return;

/**
 * Simple implementation of an {@link IModelInfoProvider} that is used to
 * provide problem information of currently edited CA model.
 * 
 * @author Stefan Rybacki
 */
public class CAProblemInfoProvider extends AbstractModelInfoProvider {

  /**
   * The tokens.
   */
  private final List<CAProblemToken> tokens = new ArrayList<>();

  private final Map<ILexerToken, List<ITokenAction>> tokenActionsCache =
      new HashMap<>();

  /**
   * The stylizer.
   */
  private final ILexerTokenStylizer stylizer = new CAProblemStylizer();

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
  protected void run(Reader input, int cPos) {
    try {
      CaruleLexer lexer;
      TokenStream stream =
          new CommonTokenStream(lexer =
              new CaruleLexer(new ANTLRReaderStream(input)));
      CaruleParser p = new CaruleParser(stream);
      camodel_return ca = p.camodel();
      synchronized (tokens) {
        tokens.clear();
        tokenActionsCache.clear();
        for (CAProblemToken t : lexer.getProblemTokens()) {
          tokens.add(t);
        }
        tokens.addAll(ca.problems);
        Collections.sort(tokens);
        for (final CAProblemToken t : tokens) {
          // create fixing actions for problem tokens
          List<ITokenAction> list = new ArrayList<>();

          if (t.getType().equals(CAProblemToken.STATE_NOT_DEFINED)) {
            // sort by Levenshtein distance to have the best matching
            // state on top
            Collections.sort(ca.states, new Comparator<String>() {

              @Override
              public int compare(String o1, String o2) {
                return Strings.getLevenshteinDistance(t.getValue(), o1)
                    - Strings.getLevenshteinDistance(t.getValue(), o2);
              }

            });
            for (String state : ca.states) {
              list.add(new ReplaceTokenAction(String.format(
                  "Replace with \"%s\"", state), t.getStart(), t.getEnd(),
                  state));
            }
          }

          tokenActionsCache.put(t, list);
        }
      }
      fireTokensChanged();
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  public List<ITokenAction> getActionsForToken(ILexerToken token) {
    return tokenActionsCache.get(token);
  }

}
