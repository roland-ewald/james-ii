/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor;

import java.util.List;

/**
 * Utility class providing frequently used operations on lexer tokens
 * 
 * @author Stefan Rybacki
 */
public class LexerTokenUtils {

  /**
   * Helper function doing a binary search for the token that ends right before
   * the range p0 and p1 starts or if there is no token in front of p0 it
   * returns the first token index
   * 
   * @param tokens
   *          list of tokens <b>ordered by {@link ILexerToken#getStart()}</b>
   * @param p0
   *          the start of the range we are interested in
   * @return the index of the first token to consider
   */
  public static int findFirstIndexInRange(List<? extends ILexerToken> tokens,
      int p0) {
    // start in the middle
    int startIndex = 0;
    int endIndex = tokens.size();
    int index = (endIndex - startIndex) / 2;

    // binary search until finding a token ending right before p0
    while (endIndex - startIndex > 1) {
      ILexerToken t = tokens.get(index);
      if (t.getEnd() < p0) {
        startIndex = index;
        index = (endIndex + startIndex) / 2;
      } else {
        endIndex = index;
        index = (endIndex + startIndex) / 2;
      }
    }

    return startIndex;
  }

  /**
   * This method helps to shift tokens according to an insert made to the very
   * document the tokens represent. This way a parsing can be made a background
   * task while somebody is typing because tokens move with the insert and are
   * refreshed in the background.
   * 
   * @param tokens
   *          a list of tokens representing the document
   * @param insertionPos
   *          position where the insert happened
   * @param insertionLength
   *          length of inserted text
   */
  public static void moveTokensAfterInsert(List<? extends ILexerToken> tokens,
      int insertionPos, int insertionLength) {
    // basically shift all by insertion affected tokens according to insertion
    // length
    int startIndex = findFirstIndexInRange(tokens, insertionPos);

    for (int i = startIndex; i < tokens.size(); i++) {
      ILexerToken t = tokens.get(i);
      if (t.getStart() < insertionPos && t.getEnd() > insertionPos) {
        t.moveEnd(insertionLength);
        continue;
      }
      if (t.getStart() >= insertionPos) {
        t.moveStart(insertionLength);
      }
    }
  }

  // FIXME sr137: not completely working there are still artifacts in
  // some cases (which means the coloring is off after remove)
  /**
   * This method helps to shift tokens according to an removal made to the very
   * document the tokens represent. This way a parsing can be made a background
   * task while somebody is typing because tokens move with the removal and are
   * refreshed in the background.
   * 
   * @param tokens
   *          a list of tokens representing the document
   * @param removePos
   *          position where the removal
   * @param removeLength
   *          length of removed text
   */
  public static void moveTokensAfterRemove(List<? extends ILexerToken> tokens,
      int removePos, int removeLength) {
    int startIndex = findFirstIndexInRange(tokens, removePos);

    for (int i = tokens.size() - 1; i >= startIndex; i--) {
      ILexerToken t = tokens.get(i);

      // if reaching into removed text
      if (t.getStart() < removePos && t.getEnd() > removePos
          && t.getEnd() <= removePos + removeLength) {
        t.moveEnd(removePos - t.getEnd());
        continue;
      }

      // if overlapping removed text
      if (t.getStart() < removePos && t.getEnd() > removePos
          && t.getEnd() > removePos + removeLength) {
        t.moveEnd(-removeLength);
        continue;
      }

      // if after removed text
      if (t.getStart() >= removePos + removeLength) {
        t.moveStart(-removeLength);
        continue;
      }

      // if starts in removed text
      if (t.getStart() >= removePos && t.getStart() <= removePos + removeLength) {
        t.moveEnd(-removeLength - removePos + t.getStart());
        t.moveStart(removePos - t.getStart());
      }

      // if completely removed
      if (t.getLength() <= 0) {
        tokens.remove(i);
      }

    }

  }

  private LexerTokenUtils() {
  }
}
