/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.HashMap;
import java.util.Map;

/**
 * The Class TrieNode. It represents a prefix tree, where each node is
 * associated with a specific character. Each node represents a string,
 * comprising the sequence of characters of the parent nodes as well as the own
 * character.
 * 
 * @param <N>
 *          type of the nodes
 * 
 * @author Nico Eggert
 */
public class TrieNode<N> {

  /** The character associated with this node. */
  private char character;

  /** The children of the node, each associated with a following character. */
  private Map<Character, TrieNode<N>> children;

  /** The node. */
  private N node;

  /**
   * Instantiates a new TRIE node.
   * 
   * @param c
   *          the character
   * @param n
   *          the node
   */
  public TrieNode(char c, N n) {
    character = c;
    node = n;
    children = new HashMap<>();
  }

  /**
   * Adds a child for a specific character.
   * 
   * @param c
   *          the character
   * @param tN
   *          the child node
   */
  public void addChildAt(char c, TrieNode<N> tN) {
    this.children.put(c, tN);
  }

  /**
   * Sets the attribute 'node' to null.
   */
  public void clearContent() {
    node = null;
  }

  /**
   * Gets the character of this node.
   * 
   * @return the character
   */
  public char getCharacter() {
    return character;
  }

  /**
   * Gets the child at a specific character.
   * 
   * @param c
   *          the character
   * 
   * @return the child node
   */
  public TrieNode<N> getChildAt(char c) {
    return children.get(c);
  }

  /**
   * Gets the node.
   * 
   * @return the node
   */
  public N getNode() {
    return node;
  }

  /**
   * Checks if this node is a leaf.
   * 
   * @return returns true if the node has no children, false otherwise
   */
  public boolean isLeaf() {
    if (children.isEmpty()) {
      return true;
    }
    return false;
  }

  /**
   * Removes the child at a specific character.
   * 
   * @param c
   *          the character
   */
  public void removeChildAt(char c) {
    children.remove(children.get(c));
  }
}
