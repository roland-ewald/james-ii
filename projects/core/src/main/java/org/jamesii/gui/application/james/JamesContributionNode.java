/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

/**
 * @author Stefan Rybacki
 * 
 */
final class JamesContributionNode {
  private final boolean horiz;

  private final boolean left;

  public JamesContributionNode(boolean horizontal, boolean left) {
    horiz = horizontal;
    this.left = left;
  }

  public final boolean isHorizontal() {
    return horiz;
  }

  public final boolean isVertical() {
    return !horiz;
  }

  public final boolean isLeft() {
    return left;
  }

  public final boolean isRight() {
    return !left;
  }

  @Override
  public String toString() {
    return left ? (horiz ? "HORIZONTAL (TOP)" : "VERTICAL (LEFT)")
        : (horiz ? "HORIZONTAL (BOTTOM)" : "VERTICAL (RIGHT)");
  }
}
