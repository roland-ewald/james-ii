/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.syntaxeditor.highlighting.java;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;

import javax.swing.Icon;

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.syntaxeditor.ILexerTokenStylizer;
import org.jamesii.gui.syntaxeditor.StrokePosition;

/**
 * Stylizer problem tokens for Java syntax highlighting. Use
 * {@link #getInstance()} to retrieve the actual stylizer.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class JavaProblemTokenStylizer implements
    ILexerTokenStylizer<JavaProblemToken> {
  /**
   * singleton instance
   */
  private static final JavaProblemTokenStylizer INSTANCE =
      new JavaProblemTokenStylizer();

  /**
   * default color
   */
  private static final Color DEFAULT_COLOR = Color.red;

  /**
   * default style
   */
  private static final int DEFAULT_STYLE = Font.PLAIN;

  /**
   * error image
   */
  private Icon errorImage;

  /**
   * warning image
   */
  private Icon warningImage;

  /**
   * Hidden constructor due to usage of singleton pattern
   */
  private JavaProblemTokenStylizer() {
    // String url =
    // this.getClass().getPackage().getName().replace('.', '/');
    warningImage = IconManager.getIcon(IconIdentifier.WARNING_SMALL);
    errorImage = IconManager.getIcon(IconIdentifier.ERROR_SMALL);
  }

  @Override
  public Color getColorFor(JavaProblemToken to) {

    switch (to.getType()) {
    case WARNING:
      return Color.YELLOW;
    case ERROR:
    default:
      return DEFAULT_COLOR;
    }

  }

  @Override
  public int getFontStyleFor(JavaProblemToken t) {
    return DEFAULT_STYLE;
  }

  @Override
  public Stroke getStrokeFor(JavaProblemToken to) {

    switch (to.getType()) {
    case ERROR:
      return new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
          10f, new float[] { 3f, 4f }, 0f);
    case WARNING:
    default:
      return new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
          10f, new float[] { 3f, 4f }, 0f);
    }
  }

  @Override
  public StrokePosition getStrokePositionFor(JavaProblemToken to) {

    switch (to.getType()) {
    case WARNING:
    case ERROR:
    default:
      return StrokePosition.UNDERLINE;
    }
  }

  @Override
  public Color getBgColorFor(JavaProblemToken to) {
    return null;
  }

  @Override
  public int getDefaultStyle() {
    return DEFAULT_STYLE;
  }

  @Override
  public Color getDefaultBgColor() {
    return null;
  }

  @Override
  public Color getDefaultColor() {
    return DEFAULT_COLOR;
  }

  @Override
  public Icon getIconFor(JavaProblemToken to) {

    switch (to.getType()) {
    case WARNING:
      return warningImage;
    case ERROR:
      return errorImage;
    default:
      return null;
    }
  }

  @Override
  public String getTooltipFor(JavaProblemToken to) {

    switch (to.getType()) {
    case WARNING:
      return String.format("This is a warning from position %d to %d",
          to.getStart(), to.getEnd());
    case ERROR:
      return String.format("This is an error from position %d to %d",
          to.getStart(), to.getEnd());
    default:
      return null;
    }
  }

  @Override
  public String getDescriptionFor(JavaProblemToken to) {

    switch (to.getType()) {
    case WARNING:
      return String.format(
          "This is a warning description from position %d to %d",
          to.getStart(), to.getEnd());
    case ERROR:
      return String.format(
          "This is an error description from position %d to %d", to.getStart(),
          to.getEnd());
    default:
      return null;
    }
  }

  @Override
  public boolean showAnnotationFor(JavaProblemToken t) {
    return true;
  }

  /**
   * @return the singleton instance of this stylizer
   */
  public static JavaProblemTokenStylizer getInstance() {
    return INSTANCE;
  }
}
