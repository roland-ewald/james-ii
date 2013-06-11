/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.tooltip;

/**
 * Helper class used to define tag combination that are related. A
 * {@link CombinedTag} represents a start and end tag so that for instance the
 * parser can use this to find a specific end tag for a given start tag.
 * 
 * @author Stefan Rybacki
 * 
 */
class CombinedTag {
  /**
   * the start tag
   */
  private StyledToolTipSyntaxToken.Type startTag;

  /**
   * the end tag
   */
  private StyledToolTipSyntaxToken.Type endTag;

  /**
   * Convenience constructor to set the public fields of the class
   * 
   * @param startTag
   *          the start tag to set
   * @param endTag
   *          the end tag to set
   */
  public CombinedTag(StyledToolTipSyntaxToken.Type startTag,
      StyledToolTipSyntaxToken.Type endTag) {
    this.setStartTag(startTag);
    this.setEndTag(endTag);
  }

  /**
   * @return the startTag
   */
  public StyledToolTipSyntaxToken.Type getStartTag() {
    return startTag;
  }

  /**
   * @param startTag
   *          the startTag to set
   */
  public void setStartTag(StyledToolTipSyntaxToken.Type startTag) {
    this.startTag = startTag;
  }

  /**
   * @return the endTag
   */
  public StyledToolTipSyntaxToken.Type getEndTag() {
    return endTag;
  }

  /**
   * @param endTag
   *          the endTag to set
   */
  public void setEndTag(StyledToolTipSyntaxToken.Type endTag) {
    this.endTag = endTag;
  }
}
