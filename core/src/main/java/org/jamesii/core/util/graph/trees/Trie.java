/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.Map;

/**
 * Implements a TRIE with nodes of type {@link TrieNode}. It represents a prefix
 * tree, where each node is associated with a specific character. Each node
 * represents a string, comprising the sequence of characters of the parent
 * nodes as well as the own character.
 * 
 * @author Nico Eggert
 * 
 * @see TrieNode
 * 
 * @param <N>
 *          the type associated with the {@link TrieNode}
 */
public class Trie<N> {

  /** Nodes of the TRIE, each of them is associated with another character. */
  private Map<Character, TrieNode<N>> rChildren;

  /**
   * Default constructor.
   */
  public Trie() {
  }

  /**
   * Creates a new TrieNode and adds it to the Trie at the correct position.
   * Only the last character of "k" is stored in the node
   * 
   * @param k
   *          the node's key
   * @param node
   */
  public void addNode(String k, N node) {
    int i;
    TrieNode<N> currentNode = rChildren.get(k.charAt(1));

    // process k character by character
    for (i = 2; i <= k.length() - 1; i++) {
      // if a necessary child does not exist it will be created
      if (currentNode.getChildAt(k.charAt(i)) == null) {
        currentNode.addChildAt(k.charAt(i), new TrieNode<N>(k.charAt(i), null));
      }

      currentNode = currentNode.getChildAt(k.charAt(i));
    }
    // TODO: what happens if the TrieNode already exists?
    // create a new node
    TrieNode<N> tN = new TrieNode<>(k.charAt(k.length()), node);
    currentNode.addChildAt(k.charAt(k.length()), tN);
  }

  /**
   * Retrieves the information stored with the key "k"
   * 
   * @param k
   *          the node's key
   * @return the content of the node stored under "k"
   */
  public N getNode(String k) {
    int i;
    TrieNode<N> currentNode = rChildren.get(k.charAt(1));
    // process k character by character
    for (i = 2; i <= k.length(); i++) {
      currentNode = currentNode.getChildAt(k.charAt(i));
    }
    return currentNode.getNode();
  }

  /**
   * If the node is a leaf the method removes it completely else just the
   * content is deleted (the attribute 'node', not 'character') and marks it as
   * blank
   * 
   * @param k
   *          the string that identifies the node to be removed
   */
  public void removeNode(String k) {
    int i;
    TrieNode<N> currentNode = rChildren.get(k.charAt(1));

    for (i = 2; i < k.length() - 1; i++) {
      currentNode = currentNode.getChildAt(k.charAt(i));
    }

    // if the node is a leaf, completely remove it
    if (currentNode.getChildAt(k.charAt(k.length())).isLeaf()) {
      currentNode.removeChildAt(k.charAt(k.length()));
      // if it is not a leaf just clear its content
    } else {
      currentNode.getChildAt(k.charAt(k.length())).clearContent();
    }
  }
}
