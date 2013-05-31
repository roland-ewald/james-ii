/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that represents a URL used by {@link URLTreeNode}, ... to define a tree
 * representation using URLs, similar to file system URLs. URLs syntax is as
 * follows:
 * <p>
 * 
 * <code>node/subnode/subsubnode/subsubsubnode[?after=id]</code>
 * <p>
 * 
 * the first part till the question mark defines a path within a tree that is
 * constructed using multiple URLs. The part after the question mark is the so
 * called placement modifier which can be omitted but must be something like
 * this if present:
 * <p>
 * <code>
 * <table border="1">
 * <tr>
 * <td>after=&lt;ID></td><td>places the specified node after the node identified by &lt;ID></td>
 * </tr>
 * <tr>
 * <td>before=&lt;ID></td><td>places the specified node before the node identified by &lt;ID></td>
 * </tr>
 * <tr>
 * <td>start</td><td>places the specified node at the beginning of the parent node (Note: following URLs with the same modifier might
 *                    occur before this node)</td>
 * </tr>
 * <tr>
 * <td>end</td><td>places the specified node at the end of the parent node (Note: following URLs with the same modifier might occur
 *                    after this node)</td>
 * </tr>
 * <tr>
 * <td>first</td><td>places the specified node at the beginning before all nodes marked with start (Node: following URLs with the same modifier
 *                    might occur before this node)</td>
 * </tr>
 * <tr>
 * <td>last</td><td>places the specified node at the end after all nodes marked with end (Node: following URLs with the same modifier
 *                    might occur after this node)</td>
 * </tr>
 * </table>
 * </code>
 * <p>
 * <b>Usage:</b> <code>
 *  <pre>
 *  URLTreeNodeURL save = new URLTreeNodeURL("menu.main/file?after=open");
 *  String[] path = r.getPath();
 *  URLTreeNodePlacement p = r.getPlacement();
 *  </pre>
 * </code>
 * 
 * @author Stefan Rybacki
 * 
 */
public class URLTreeNodeURL {
  /**
   * the url as string
   */
  private String url;

  /**
   * the path as string
   */
  private String path;

  /**
   * the placement
   */
  private URLTreeNodePlacement placement;

  /**
   * Creates a new instance using the given url.
   * 
   * @param url
   *          the url in supported format
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   * @see #getLocation()
   * @see #getPath()
   * @see #getPlacement()
   */
  public URLTreeNodeURL(String url) throws MalformedURLException,
      UnsupportedEncodingException {
    this.url = url;

    // try to parse url
    String urlPattern = "^([^?]*)[?]?(.*)?$";

    Pattern pattern = Pattern.compile(urlPattern);

    Matcher matcher = pattern.matcher(url);

    if (!matcher.find()) {
      throw new MalformedURLException("Provided resource URL is not valid!");
    }

    String encoding = System.getProperty("file.encoding");

    path = URLDecoder.decode(matcher.group(1), encoding);

    String parameters = matcher.group(2);

    // now match the parameters
    pattern = Pattern.compile("([^=]+)[=]?([^&]*)[&]?");
    matcher = pattern.matcher(parameters);

    while (matcher.find()) {
      String name = matcher.group(1);
      String value = URLDecoder.decode(matcher.group(2), encoding);

      // only use the first placement option that is found
      if (URLTreeNodePlacement.isPlacement(name)) {
        placement = new URLTreeNodePlacement(name, value);
        break;
      }
    }
  }

  /**
   * @return the path in the tree as array of nodes
   */
  public final String[] getPath() {
    StringTokenizer tokenizer = new StringTokenizer(path, "/");
    String[] result = new String[tokenizer.countTokens()];

    int p = 0;
    while (tokenizer.hasMoreTokens()) {
      result[p] = tokenizer.nextToken();
      p++;
    }

    return result;
  }

  /**
   * @return the placement modifier encoding in a {@link URLTreeNodePlacement}
   *         object
   */
  public final URLTreeNodePlacement getPlacement() {
    return placement;
  }

  /**
   * @return the url as String representation
   */
  public final String getURL() {
    return url;
  }

  /**
   * @return the location/path of the specified url (basically the url without
   *         placement modifier)
   */
  public final String getLocation() {
    return path;
  }

  @Override
  public String toString() {
    return url;
  }
}
