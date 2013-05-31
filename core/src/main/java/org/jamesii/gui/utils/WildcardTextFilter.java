/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * A filter that implements {@link IFilter} using text as filter value. Here the
 * filter value can contain wildcards such as <b>*</b> and <b>?</b> for
 * arbitrary text parts.<br/>
 * <b>*</b> specifies an arbitrary string while<br/>
 * <b>?</b> specifies an arbitrary character
 * <p/>
 * Example: <b>H?llo</b> matches Hello, Hallo, Hollo, ...<br/>
 * <b>H*llo</b> on the other also matches Hllo, Hlllllo, Haallo and so on
 * 
 * @author Stefan Rybacki
 * 
 */
public class WildcardTextFilter extends AbstractFilter<String> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8633405984908551440L;

  /**
   * Creates a new wildcard text filter using text for filtering
   */
  public WildcardTextFilter() {
    this(null);
  }

  /**
   * Creates a new wildcard filter using text for filtering and setting the
   * filter value to the specified text.
   * 
   * @param filterText
   *          the filter text to set
   */
  public WildcardTextFilter(String filterText) {
    super(filterText);
  }

  @Override
  public boolean filteredWithValue(String value, Object object) {
    if (value == null || value.length() == 0) {
      return false;
    }

    // convert value into regexp escaping everything with special treatment of
    // *
    // and ?
    StringBuilder builder = new StringBuilder();
    StringTokenizer tokenizer = new StringTokenizer(value, "*?", true);

    builder.append("^");
    while (tokenizer.hasMoreTokens()) {
      String nextToken = tokenizer.nextToken();
      if (nextToken.equals("*")) {
        builder.append(".*?");
        continue;
      }
      if (nextToken.equals("?")) {
        builder.append(".+");
        continue;
      }

      builder.append(Pattern.quote(nextToken));
    }
    builder.append(".*?$");

    return !object.toString().toLowerCase().matches(builder.toString());
  }

}
