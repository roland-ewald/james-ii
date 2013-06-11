/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 * This class implements the {@link TreeModel} interface and uses
 * {@link URLTreeNode}s to represent the tree's nodes. Nodes are added using
 * {@link URLTreeNodeURL}s respectively their string representations.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          specifies the type of the objects that are assigned to each
 *          {@link URLTreeNode}
 * 
 */
public class URLTreeModel<E> extends DefaultTreeModel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -5991578580395519040L;

  /**
   * Creates a new instance using the specified node as root
   * 
   * @param root
   *          the root node
   */
  public URLTreeModel(URLTreeNode<E> root) {
    super(root, true);
  }

  /**
   * Helper method that adds a node to the given parent node according to the
   * placement specified as {@link URLTreeNodePlacement}
   * 
   * @param parent
   *          the parent to add the node to
   * @param node
   *          the node to add
   * @param p
   *          the placement of the node
   */
  private void addNode(URLTreeNode<E> parent, URLTreeNode<E> node,
      URLTreeNodePlacement p) {
    node = parent.addNode(node, p);
    fireTreeNodesInserted(parent, getPathToRoot(parent),
        new int[] { parent.getIndex(node) }, new URLTreeNode[] { node });
  }

  /**
   * Adds a node to the tree at specified position. The position must be a
   * format {@link URLTreeNodeURL} understands and can contain placement
   * information. Nodes that don't exist on the given path are created
   * automatically.
   * 
   * @param path
   *          the path of the given node within the tree (the path must be
   *          conform to {@link URLTreeNodeURL} format)
   * @param node
   *          the node to add
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  public void addNode(String path, URLTreeNode<E> node)
      throws MalformedURLException, UnsupportedEncodingException {
    URLTreeNodeURL url = new URLTreeNodeURL(path);
    URLTreeNode<E> parent = createPath(url.getLocation());

    addNode(parent, node, url.getPlacement());
  }

  /**
   * Returns the path in {@link URLTreeNodeURL} format for a given node
   * 
   * @param node
   *          the node in question
   * @return the path to the node or null if node is not in tree
   */
  public String getPath(URLTreeNode<E> node) {
    TreeNode[] pathToRoot = getPathToRoot(node);
    StringBuffer path = new StringBuffer();
    for (TreeNode n : pathToRoot) {
      if (n != getRoot()) {
        path.append("/");
        path.append(((URLTreeNode<?>) n).getId());
      }
    }

    if (path.length() > 0) {
      return path.substring(1);
    }
    return null;
  }

  /**
   * Returns the node to a given path in {@link URLTreeNodeURL} format.
   * 
   * @param path
   *          the path to look at
   * @return the node at given path or null if path doesn't exist in tree
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings("unchecked")
  public URLTreeNode<E> getNode(String path) throws MalformedURLException,
      UnsupportedEncodingException {
    // extract path parts from path
    URLTreeNodeURL url = new URLTreeNodeURL(path);
    // start at root node
    URLTreeNode<E> node = (URLTreeNode<E>) getRoot();
    boolean nodeFound = false;

    // try to walk thru tree part by part
    for (String p : url.getPath()) {
      // search for part in current node
      nodeFound = false;
      for (int i = 0; i < node.getChildCount(); i++) {
        // if node is found continue
        if (((URLTreeNode<E>) node.getChildAt(i)).getId().equals(p)) {
          node = (URLTreeNode<E>) node.getChildAt(i);
          nodeFound = true;
          break;
        }
      }
      // path not found
      if (!nodeFound) {
        return null;
      }
    }
    return node;
  }

  /**
   * Creates an empty path in the tree. That means the specified path in
   * {@link URLTreeNodeURL} format creates nodes as necessary so that the given
   * path exists in the tree. Created nodes are empty though.
   * 
   * @param path
   *          the path to create in the tree
   * @return the node that is created last in hierarchy
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings("unchecked")
  public URLTreeNode<E> createPath(String path) throws MalformedURLException,
      UnsupportedEncodingException {
    // extract path parts from path
    URLTreeNodeURL url = new URLTreeNodeURL(path);
    // start at root node
    URLTreeNode<E> node = (URLTreeNode<E>) getRoot();
    // walk the tree using path parts
    String[] paths = url.getPath();
    for (int j = 0; j < paths.length; j++) {
      String p = paths[j];
      boolean nodeFound = false;
      // search for part in current node
      for (int i = 0; i < node.getChildCount(); i++) {
        nodeFound = false;
        // if node is found continue
        if (((URLTreeNode<E>) node.getChildAt(i)).getId().equals(p)) {
          node = (URLTreeNode<E>) node.getChildAt(i);
          nodeFound = true;
          break;
        }
      }
      // path not found so create it
      if (!nodeFound) {
        addNode(node, node = new URLTreeNode<>(p, node),
            j == paths.length - 1 ? url.getPlacement() : null);
      }

    }
    return node;
  }

  /**
   * Removes a node from the given parent.
   * 
   * @param parent
   *          the parent the node is to be removed from
   * @param node
   *          the node to remove
   */
  protected void removeNode(URLTreeNode<E> parent, URLTreeNode<E> node) {
    int i = parent.getIndex(node);
    if (i >= 0) {
      parent.remove(node);
      nodesWereRemoved(parent, new int[] { i }, new Object[] { node });
    }
  }

  /**
   * Removes a specified node at a given path in {@link URLTreeNodeURL} format.
   * It is also possible to specify the path relative to another path in
   * {@link URLTreeNodeURL} format.
   * 
   * @param path
   *          the path to remove
   * @param relativeTo
   *          the path the given path is relative to
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  @SuppressWarnings("unchecked")
  public void removeNodeRelative(String path, String relativeTo)
      throws MalformedURLException, UnsupportedEncodingException {
    URLTreeNode<E> node =
        getNode(relativeTo + (relativeTo.length() > 0 ? "/" : "") + path);
    if (node != null && node.getParent() != null
        && (node.getParent() instanceof URLTreeNode)) {
      removeNode((URLTreeNode<E>) node.getParent(), node);
    }
  }

  /**
   * Removes a node at the given path in {@link URLTreeNodeURL}. If you want to
   * remove a node at a path relative to another path use
   * {@link #removeNodeRelative(String, String)}.
   * 
   * @param path
   *          the path to the node to remove
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   */
  public void removeNode(String path) throws MalformedURLException,
      UnsupportedEncodingException {
    removeNodeRelative(path, "");
  }

}
