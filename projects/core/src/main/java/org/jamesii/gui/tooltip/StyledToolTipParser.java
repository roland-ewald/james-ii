/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

import java.awt.Font;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import org.jamesii.gui.syntaxeditor.ILexerToken;
import org.jamesii.gui.tooltip.StyledToolTipSyntaxToken.Type;

/**
 * Parser that extends the actual lexer {@link StyledToolTipLexer} for styled
 * tooltips. Additionally to the syntax tokens provided by the lexer it provides
 * additional problem tokens for problem highlighting in a
 * {@link org.jamesii.gui.syntaxeditor.SyntaxEditor}. Problem tokens can
 * indicate badly nested tags, missing start and end tags, obsolete tags and
 * wrong values for color and font size.<br/>
 * It also provides render tokens that are used by the actual
 * {@link StyledToolTip} for displaying purposes.
 * 
 * @author Stefan Rybacki
 */
public class StyledToolTipParser extends StyledToolTipLexer {
  /**
   * problem tokens
   */
  private List<ILexerToken> problemTokens = new ArrayList<>();

  /**
   * syntax tokens
   */
  private List<ILexerToken> syntaxTokens = new ArrayList<>();

  /**
   * helper array that holds start and end tags for know tags
   */
  private static final CombinedTag tags[] = new CombinedTag[] {
      new CombinedTag(Type.BOLDON, Type.BOLDOFF),
      new CombinedTag(Type.ITALICON, Type.ITALICOFF),
      new CombinedTag(Type.UNDERLINEON, Type.UNDERLINEOFF),
      new CombinedTag(Type.CROSSOUTON, Type.CROSSOUTOFF),
      new CombinedTag(Type.PREON, Type.PREOFF),
      new CombinedTag(Type.PUSHSTATE, Type.POPSTATE) };

  /**
   * helper function determining whether the passed tag is a start tag
   * 
   * @param tag
   *          the tag to examine
   * @return true if tag is a start tag false else
   * @see StyledToolTipParser#isEndTag(Type)
   */
  private static boolean isStartTag(Type tag) {
    for (CombinedTag tag2 : tags) {
      if (tag == tag2.getStartTag()) {
        return true;
      }
    }
    return false;
  }

  /**
   * helper function similar to {@link StyledToolTipParser#isStartTag(Type)}
   * that determines whether the passed tag is an end tag
   * 
   * @param tag
   *          the tag to examine
   * @return true if passed tag is an end tag false else
   * @see StyledToolTipParser#isStartTag(Type)
   */
  private static boolean isEndTag(Type tag) {
    for (CombinedTag tag2 : tags) {
      if (tag == tag2.getEndTag()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void stopParsing() {
    super.stopParsing();
  }

  @Override
  public final void parse(Reader input) {
    // first tokenize the input
    super.parse(input);

    if (!stop) {
      problemTokens.clear();
      syntaxTokens = getSyntaxTokens();

      // run thru all syntax tokens and check for errors not
      // found by lexer for instance obsolete tags, nested tags or
      // wrong color values
      StyleState state = new StyleState();
      Deque<StyledToolTipSyntaxToken> stack = new ArrayDeque<>();
      for (ILexerToken to : syntaxTokens) {
        StyledToolTipSyntaxToken t = (StyledToolTipSyntaxToken) to;

        // if already bold mark as obsolete
        if (t.getType() == Type.BOLDON && state.isBold()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }
        if (t.getType() == Type.BOLDOFF && !state.isBold()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }
        if (t.getType() == Type.UNDERLINEON && state.isUnderline()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }
        if (t.getType() == Type.UNDERLINEOFF && !state.isUnderline()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }
        if (t.getType() == Type.ITALICON && state.isItalic()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }
        if (t.getType() == Type.ITALICOFF && !state.isItalic()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }

        if (t.getType() == Type.CROSSOUTOFF && !state.isCrossout()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }

        if (t.getType() == Type.CROSSOUTON && state.isCrossout()) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.OBSOLETE, t.getStart(), t
                  .getLength()));
        }

        // push start tags on stack (this is used to determine whether
        // tags are
        // nested and whether end tags are missing)
        if (isStartTag(t.getType())) {
          stack.push(t);
        }

        if (stack.peek() != null) {
          for (CombinedTag tag : tags) {
            if (t.getType() == tag.getEndTag()
                && stack.peek().getType() != tag.getStartTag()) {
              problemTokens.add(new StyledToolTipProblemToken(
                  StyledToolTipProblemToken.Type.INVALIDNESTED, t.getStart(), t
                      .getLength()));
            }
          }
        } else if (isEndTag(t.getType())) {
          problemTokens.add(new StyledToolTipProblemToken(
              StyledToolTipProblemToken.Type.NOENDTAG, t.getStart(), t
                  .getLength()));
        }

        if (stack.peek() != null) {
          for (CombinedTag tag : tags) {
            if (t.getType() == tag.getEndTag()
                && stack.peek().getType() == tag.getStartTag()) {
              stack.pop();
            }
          }
        }

        // check for value
        if (t.getType() == Type.COLORVALUE) {
          if (StyledToolTipUtils.getColorFromValue(t.getValue(), null) == null) {
            problemTokens.add(new StyledToolTipProblemToken(
                StyledToolTipProblemToken.Type.INVALIDVALUE, t.getStart(), t
                    .getLength()));
          }
        }

        // check for value
        if (t.getType() == Type.BGCOLORVALUE) {
          if (StyledToolTipUtils.getColorFromValue(t.getValue(), null) == null) {
            problemTokens.add(new StyledToolTipProblemToken(
                StyledToolTipProblemToken.Type.INVALIDVALUE, t.getStart(), t
                    .getLength()));
          }
        }

        // check for value
        if (t.getType() == Type.FONTVALUE) {
          if (!new Font(t.getValue(), Font.PLAIN, 12).getName().equals(
              t.getValue())) {
            problemTokens.add(new StyledToolTipProblemToken(
                StyledToolTipProblemToken.Type.INVALIDVALUE, t.getStart(), t
                    .getLength()));
          }
        }

        // check for value
        if (t.getType() == Type.FONTSIZEVALUE || t.getType() == Type.WIDTHVALUE
            || t.getType() == Type.HEIGHTVALUE) {
          try {
            Integer.parseInt(t.getValue());
          } catch (NumberFormatException n) {
            problemTokens.add(new StyledToolTipProblemToken(
                StyledToolTipProblemToken.Type.INVALIDVALUE, t.getStart(), t
                    .getLength()));
          }
        }

        // keep state up to date
        if (t.getType() == Type.BOLDON) {
          state.setBold(true);
        }
        if (t.getType() == Type.CROSSOUTON) {
          state.setCrossout(true);
        }
        if (t.getType() == Type.ITALICON) {
          state.setItalic(true);
        }
        if (t.getType() == Type.UNDERLINEON) {
          state.setUnderline(true);
        }
        if (t.getType() == Type.BOLDOFF) {
          state.setBold(false);
        }
        if (t.getType() == Type.ITALICOFF) {
          state.setItalic(false);
        }
        if (t.getType() == Type.UNDERLINEOFF) {
          state.setUnderline(false);
        }
        if (t.getType() == Type.CROSSOUTOFF) {
          state.setCrossout(false);
        }

      }

      // check whether there are still elements on the stack
      for (StyledToolTipSyntaxToken ts : stack) {
        // mark as no end tag or nested
        problemTokens.add(new StyledToolTipProblemToken(
            isStartTag(ts.getType()) ? StyledToolTipProblemToken.Type.NOENDTAG
                : StyledToolTipProblemToken.Type.NOSTARTTAG, ts.getStart(), ts
                .getLength()));
      }

      Collections.sort(problemTokens);
    }
  }

  /**
   * This function returns the render tokens that can be used by
   * {@link StyledToolTip} for rendering purposes. It basically creates a list
   * of tokens with states attached. Those states specify current font, size,
   * color, background and so on.
   * 
   * @return a list of render tokens
   */
  public final List<StyledToolTipToken> getRenderTokens() {
    List<StyledToolTipToken> tokens = new ArrayList<>();

    StyleState state = new StyleState();
    Deque<StyleState> styleStack = new ArrayDeque<>();

    // go thru all syntax tokens and generate StyledToolTipTokens from
    // them
    for (ILexerToken to : getSyntaxTokens()) {
      StyledToolTipSyntaxToken t = (StyledToolTipSyntaxToken) to;

      /*
       * set text and color state
       */
      if (t.getType() == Type.UNDERLINEON) {
        state.setUnderline(true);
      }
      if (t.getType() == Type.UNDERLINEOFF) {
        state.setUnderline(false);
      }
      if (t.getType() == Type.BOLDON) {
        state.setBold(true);
      }
      if (t.getType() == Type.BOLDOFF) {
        state.setBold(false);
      }
      if (t.getType() == Type.ITALICON) {
        state.setItalic(true);
      }
      if (t.getType() == Type.ITALICOFF) {
        state.setItalic(false);
      }
      if (t.getType() == Type.COLORVALUE) {
        state.setColor(StyledToolTipUtils.getColorFromValue(t.getValue(),
            state.getColor()));
      }
      if (t.getType() == Type.BGCOLORVALUE) {
        state.setBgColor(StyledToolTipUtils.getColorFromValue(t.getValue(),
            state.getBgColor()));
      }
      if (t.getType() == Type.FONTVALUE) {
        state.setFontName(t.getValue());
      }
      if (t.getType() == Type.FONTSIZEVALUE) {
        int size = state.getFontSize();
        try {
          size = Integer.parseInt(t.getValue());
        } catch (NumberFormatException n) {
        }
        state.setFontSize(size);
      }
      if (t.getType() == Type.CROSSOUTON) {
        state.setCrossout(true);
      }
      if (t.getType() == Type.CROSSOUTOFF) {
        state.setCrossout(false);
      }

      if (t.getType() == Type.PUSHSTATE) {
        styleStack.push(new StyleState(state));
      }
      if (t.getType() == Type.POPSTATE && styleStack.size() > 0) {
        state = styleStack.pop();
      }

      // create tokens according to syntax tokens
      if (t.getType() == Type.WIDTHVALUE) {
        try {
          int value = Integer.parseInt(t.getValue());
          // hide value in start value of token
          tokens.add(0, new StyledToolTipToken(StyledToolTipToken.Type.WIDTH,
              value, 0, new StyleState()));
        } catch (Exception e) {
        }
      }
      if (t.getType() == Type.HEIGHTVALUE) {
        try {
          int value = Integer.parseInt(t.getValue());
          // hide value in start value of token
          tokens.add(0, new StyledToolTipToken(StyledToolTipToken.Type.HEIGHT,
              value, 0, new StyleState()));
        } catch (Exception e) {
        }
      }
      if (t.getType() == Type.LESSTHAN) {
        tokens.add(new StyledToolTipToken(StyledToolTipToken.Type.TEXT, t
            .getStart(), 1, state));
      }
      if (t.getType() == Type.TEXT) {
        tokens.add(new StyledToolTipToken(StyledToolTipToken.Type.TEXT, t
            .getStart(), t.getLength(), state));
      }
      if (t.getType() == Type.LINEBREAK) {
        tokens.add(new StyledToolTipToken(StyledToolTipToken.Type.LINEBREAK, t
            .getStart(), t.getLength(), state));
      }
      if (t.getType() == Type.LINE) {
        tokens.add(new StyledToolTipToken(StyledToolTipToken.Type.LINE, t
            .getStart(), t.getLength(), state));
      }
    }

    return tokens;
  }

  /**
   * @return the problem tokens
   */
  public List<ILexerToken> getProblemTokens() {
    return problemTokens;
  }
}
