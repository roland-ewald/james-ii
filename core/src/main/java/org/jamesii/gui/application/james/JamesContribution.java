/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.beans.DefaultPersistenceDelegate;
import java.beans.PersistenceDelegate;
import java.util.ArrayList;
import java.util.List;
import org.jamesii.core.serialization.IEncoderCompatible;
import org.jamesii.core.serialization.XMLEncoderFactory;

/**
 * Enumeration of possible contribution values. JamesContributions are used by the
 * {@link IWindowManager} and {@link IWindow} to determine where to put the
 * {@link IWindow}.
 * 
 * @author Stefan Rybacki
 */
public class JamesContribution implements IEncoderCompatible {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8605414140948333962L;
  {
    PersistenceDelegate delegate =
        new DefaultPersistenceDelegate(new String[] { "position" });

    XMLEncoderFactory.registerDelegate(JamesContribution.class, delegate);
  }

  /**
   * left view area
   */
  public static final JamesContribution LEFT_VIEW = new JamesContribution("tbl");

  /**
   * right view area
   */
  public static final JamesContribution RIGHT_VIEW = new JamesContribution("tbrr");

  /**
   * bottom view area
   */
  public static final JamesContribution BOTTOM_VIEW = new JamesContribution("b");

  /**
   * editor view area
   */
  public static final JamesContribution EDITOR = new JamesContribution("tbrl");

  /**
   * detached view
   */
  public static final JamesContribution DIALOG = new JamesContribution("d");

  /**
   * top view.
   */
  public static final JamesContribution TOP_VIEW = new JamesContribution("tt");

  /**
   * The position.
   */
  private final String pos;

  /**
   * Creates a contribution with the specified label
   * 
   * @param pos
   *          the position
   */
  public JamesContribution(String pos) {
    assert (pos != null);
    this.pos = pos;
  }

  /**
   * Gets the position
   * 
   * @return the position
   */
  public String getPosition() {
    return pos;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof JamesContribution) {
      return ((JamesContribution) obj).getPosition().equals(pos);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return pos.hashCode();
  }

  private static List<JamesContributionNode> parseLocation(String pos, int offset) {
    List<JamesContributionNode> list = new ArrayList<>();

    if (offset >= pos.length()) {
      return list;
    }

    char ch = pos.charAt(offset);
    switch (Character.toLowerCase(ch)) {
    case 'd':
      // if dialog check for dialog identifier
      return list;
    case 'l':
      // TODO create vertical split node
      list.add(new JamesContributionNode(false, true));
      list.addAll(parseLocation(pos, offset + 1));
      return list;
    case 'r':
      list.add(new JamesContributionNode(false, false));
      list.addAll(parseLocation(pos, offset + 1));
      return list;
    case 't':
      list.add(new JamesContributionNode(true, true));
      list.addAll(parseLocation(pos, offset + 1));
      return list;
    case 'b':
      list.add(new JamesContributionNode(true, false));
      list.addAll(parseLocation(pos, offset + 1));
      return list;

    default:
      throw new IllegalArgumentException(ch + " is not supported!");
    }
  }

  /**
   * Creates a tree like structure representing the hierarchy of the given
   * position string
   * 
   * @param pos
   *          the position string to parse
   * @return
   */
  public static final List<JamesContributionNode> parsePosition(String pos) {
    // check highest level
    return parseLocation(pos, 0);
  }

}
