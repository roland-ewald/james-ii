/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.BasicDirectedGraph;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IDirectedGraph;

/**
 * Static methods for dealing with hierarchies: cycle detection, position of a
 * node in a hierarchy, immutable empty hierarchy (in analogy to empty
 * collections), wrapping as a directed graph (immutable due to different
 * semantics of the remove functions).
 * 
 * @author Arne Bittig
 * 
 */
public final class Hierarchies {

  private Hierarchies() {
  }

  /**
   * Find cycles in the graph implied by the hierarchy. Cycles are returned as
   * lists where each node is child of the subsequent node and the final node is
   * child of the first.
   * 
   * {@link org.jamesii.core.util.graph.traverse.CycleDetection#detectCycle(org.jamesii.core.util.graph.IGraph)}
   * in contrast, works on graphs and detects only one cycle, not all of them.
   * Hierarchies allow for easier detection of all cycles as each node can be
   * part of only one (directed) cycle as no node has more than one outgoing
   * edge. (Since CycleDetection works on undirected simple graphs, it should
   * not be able to detect cycles of length two (which require a multi-edge),
   * but should detect the feed-forward "loop" as a cycle).
   * 
   * @param hierarchy
   *          Hierarchy to find cycles in
   * @return Collection of cycles (empty if none)
   */
  public static <T> Collection<List<T>> detectCycles(IHierarchy<T> hierarchy) {
    Collection<T> nodes = hierarchy.getAllNodes();
    Collection<T> roots = hierarchy.getRoots();
    removeNodesAndChildrenRec(nodes, roots, hierarchy);
    if (nodes.isEmpty()) { // shortcut
      return Collections.emptyList();
    }
    Collection<List<T>> cycles = new ArrayList<>(2);
    while (!nodes.isEmpty()) {
      T node = nodes.iterator().next();
      List<T> potentialCycle = new ArrayList<>();
      int index = -1;
      while (index < 0) {
        potentialCycle.add(node);
        node = hierarchy.getParent(node);
        if (!nodes.contains(node)) {
          break; // parent already removed, i.e. in a recorded cycle
        }
        index = potentialCycle.indexOf(node);
      }
      if (index >= 0) {
        cycles.add(potentialCycle.subList(index, potentialCycle.size()));
      }
      nodes.removeAll(potentialCycle);
    }
    return cycles;
  }

  /**
   * From given collection of nodes, remove given other nodes, their children,
   * their children's children, ..., without altering the hierarchy describing
   * the child-parent relation. Goes into infinite recursion if any of the nodes
   * to remove is part of a cycle.
   * 
   * @param nodes
   * @param toRemove
   * @param hierarchy
   */
  private static <T> void removeNodesAndChildrenRec(Collection<T> nodes,
      Collection<T> toRemove, IHierarchy<T> hierarchy) {
    nodes.removeAll(toRemove);
    for (T rem : toRemove) {
      removeNodesAndChildrenRec(nodes, hierarchy.getChildren(rem), hierarchy);
    }
  }

  /**
   * Get presence status of node in a hierarchy
   * 
   * @param hierarchy
   *          Hierarchy
   * @param node
   *          Node (potentially) in hierarchy
   * @return node's position in hierarchy
   */
  public static <T> NodeStatus getNodeStatus(IHierarchy<T> hierarchy, T node) {
    Collection<T> orphans = hierarchy.getOrphans();
    if (orphans != null && orphans.contains(node)) {
      return NodeStatus.ORPHAN;
    }
    if (hierarchy.getParent(node) == null) {
      if (hierarchy.getChildren(node).isEmpty()) {
        return NodeStatus.ABSENT;
      } else {
        return NodeStatus.ROOT;
      }
    } else {
      if (hierarchy.getChildren(node).isEmpty()) {
        return NodeStatus.LEAF;
      } else {
        return NodeStatus.INNER;
      }
    }
  }

  /**
   * Position / status of an element in a hierarchy
   * 
   * @author Arne Bittig
   */
  public static enum NodeStatus {
    /** parent, but no children */
    LEAF,
    /** children, but no parent */
    ROOT,
    /** parent and children */
    INNER,
    /** no parent or children, but present (optional) */
    ORPHAN,
    /** not present at all */
    ABSENT
  }

  /**
   * 
   * @return The empty hierarchy (immutable & serializable)
   */
  @SuppressWarnings("unchecked")
  public static <T> IHierarchy<T> emptyHierarchy() {
    return EMPTY_HIERARCHY;
  }

  @SuppressWarnings("rawtypes")
  private static final IHierarchy EMPTY_HIERARCHY = new EmptyHierarchy();

  private static class EmptyHierarchy<T> implements IHierarchy<T>,
      java.io.Serializable {
    private static final long serialVersionUID = 3745243894110945566L;

    @Override
    public T addChildParentRelation(T child, T parent) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean addOrphan(T orphan) {
      return false;
    }

    @Override
    public Collection<T> getAllNodes() {
      return Collections.emptyList();
    }

    @Override
    public Collection<T> getChildren(T parent) {
      return Collections.emptyList();
    }

    @Override
    public Map<T, T> getChildToParentMap() {
      return Collections.emptyMap();
    }

    @Override
    public Collection<T> getOrphans() {
      return null;
    }

    @Override
    public T getParent(T child) {
      return null;
    }

    @Override
    public Collection<T> getRoots() {
      return Collections.emptyList();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public T removeChildParentRelation(T child) {
      return null;
    }

    @Override
    public Collection<T> removeNode(T node) {
      throw new UnsupportedOperationException();
    }
  }

  /**
   * Return representation of a hierarchy as directed graph (non-modifiable)
   * 
   * @param hierarchy
   *          Hierarchy to wrap
   * @return Graph wrapping the hierarchy
   */
  public static <T> IDirectedGraph<T, Edge<T>> asGraph(IHierarchy<T> hierarchy) {
    return new HierarchyGraph<>(hierarchy);
  }

  /**
   * {@link IHierarchy} wrapper implementing the {@link IGraph} interface so
   * {@link org.jamesii.core.util.graph.traverse.CycleDetection#detectCycle(IGraph)}
   * can be used on it. The graph itself is immutable, i.e. all mutating methods
   * throws {@link UnsupportedOperationException}, but changes to the underlying
   * hierarchy are reflected directly. {@link Edge} collections for methods
   * returning those are created on-the-fly (as a hierarchy has no explicit
   * edges), making these methods rather computationally expensive, especially
   * {@link #getEdges()}.
   * 
   * Note: as of Mar 08 2012,
   * {@link org.jamesii.core.util.graph.traverse.CycleDetection} will not detect
   * two-element cycles (i.e. nodes that are both child and parent of each
   * other) as it works on simple graphs only, and the order of the cycle need
   * correspond to the direction of the relationship (either way) as it was
   * written for undirected graphs.
   * 
   * @author Arne Bittig
   * 
   * @param <T>
   *          Type of the nodes in the hierarchy
   */
  private static final class HierarchyGraph<T> extends
      BasicDirectedGraph<T, Edge<T>> {

    private static final long serialVersionUID = 1656597135677914971L;

    private final IHierarchy<T> hierarchy;

    /**
     * Create graph (incomplete implementation) from hierarchy
     * 
     * @param hierarchy
     *          Hierarchy to wrap
     */
    HierarchyGraph(IHierarchy<T> hierarchy) {
      super();
      this.hierarchy = hierarchy;
    }

    @Override
    public boolean addEdge(Edge<T> edge) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void addVertex(T vertex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Edge<T>> getEdges(T vertex) {
      return new EdgeList<>(vertex, getSources(vertex), getTargets(vertex));
    }

    @Override
    public List<T> getNeighboursOfNode(T vertex) {
      Collection<T> children = hierarchy.getChildren(vertex);
      List<T> neighbours = new ArrayList<>(children);
      T parent = hierarchy.getParent(vertex);
      if (parent != null && !children.contains(parent)) {
        neighbours.add(parent);
      }
      return neighbours;
    }

    @Override
    public int getVertexCount() {
      return hierarchy.getAllNodes().size();
    }

    @Override
    public List<T> getVertices() {
      return new ArrayList<>(hierarchy.getAllNodes());
    }

    @Override
    public boolean removeEdge(Edge<T> edge) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeVertex(T vertex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Edge<T>> getEdges() {
      if (hierarchy instanceof AbstractHierarchy) {
        List<Edge<T>> allEdges = new ArrayList<>();
        for (T parent : ((AbstractHierarchy<T>) hierarchy).getAllParents()) {
          allEdges.addAll(new EdgeList<>(parent, hierarchy.getChildren(parent),
              Collections.<T> emptyList()));
        }
        return allEdges;
      } else {
        Collection<Edge<T>> allEdges = new HashSet<>();
        Collection<T> allConnectedNodes = hierarchy.getAllNodes();
        Collection<T> orphans = hierarchy.getOrphans();
        if (orphans != null) {
          allConnectedNodes.removeAll(orphans);
        }
        for (T node : allConnectedNodes) {
          allEdges.addAll(getEdges(node));
        }
        return new ArrayList<>(allEdges);
      }
    }

    @Override
    public boolean hasEdge(T vertex1, T vertex2) {
      return hierarchy.getParent(vertex1) == vertex2
          || hierarchy.getParent(vertex2) == vertex1;
    }

    @Override
    public boolean hasVertex(T vertex) {
      return hierarchy.getParent(vertex) != null
          || !hierarchy.getChildren(vertex).isEmpty()
          || hierarchy.getOrphans() != null
          && hierarchy.getOrphans().contains(vertex);
    }

    @Override
    public boolean isSimple() {
      return false; // a node may be parent of its own parent
    }

    @Override
    public boolean isDirected() {
      return true;
    }

    @Override
    public Collection<Edge<T>> getIncomingEdges(T vertex) {
      return new EdgeList<>(vertex, getSources(vertex),
          Collections.<T> emptyList());
    }

    @Override
    public Collection<Edge<T>> getOutgoingEdges(T vertex) {
      return new EdgeList<>(vertex, Collections.<T> emptyList(),
          getTargets(vertex));
    }

    @Override
    public Collection<T> getSources(T vertex) {
      return hierarchy.getChildren(vertex);
    }

    @Override
    public Collection<T> getTargets(T vertex) {
      T parent = hierarchy.getParent(vertex);
      if (parent == null) {
        return Collections.emptyList();
      }
      return Arrays.asList(parent);
    }

    /**
     * List view of graph edges involving one specific vertex. Actual
     * {@link Edge} instances are views on this structure and created
     * on-the-fly.
     * 
     * @author Arne Bittig
     * @date Mar 12, 2012
     * 
     * @param <V>
     *          Vertex type
     */
    public static class EdgeList<V> extends AbstractList<Edge<V>> implements
        java.io.Serializable {

      private static final long serialVersionUID = 6170602537920058935L;

      private final V vertex;

      private final List<V> sources;

      private final List<V> targets;

      private final int numSources;

      private final int numTotal;

      /**
       * Create new List view of edges from given (source) vertices to given
       * vertex, and from this vertex to given (target) vertices
       * 
       * @param vertex
       *          Vertex common to all edges in "list"
       * @param sources
       *          Vertices with edges to vertex
       * @param targets
       *          Vertices with edges from vertex
       */
      public EdgeList(V vertex, Collection<V> sources, Collection<V> targets) {
        this.vertex = vertex;
        if (sources instanceof List) {
          this.sources = (List<V>) sources;
        } else {
          this.sources = new ArrayList<>(sources);
        }
        this.numSources = sources.size();
        if (targets instanceof List) {
          this.targets = (List<V>) targets;
        } else {
          this.targets = new ArrayList<>(targets);
        }
        numTotal = numSources + targets.size();
      }

      @Override
      public Edge<V> get(int index) {
        if (index < numSources) {
          return new SourceEdge(index);
        } else {
          return new TargetEdge(index - numSources);
        }
      }

      @Override
      public int size() {
        return numTotal;
      }

      class DirectedWrappedEdge extends Edge<V> {
        private static final long serialVersionUID = -3210956115717633489L;

        DirectedWrappedEdge() { // NOSONAR: serializable because super
          // class is; serialization of outer class unavoidable
          super(null, null);
        }

        @Override
        public int hashCode() {
          return 2 * getFirstVertex().hashCode() + getSecondVertex().hashCode();
        }

        @Override
        public boolean equals(Object obj) {
          if (this == obj) {
            return true;
          }
          if (!(obj instanceof EdgeList.DirectedWrappedEdge)) {
            return false;
          }
          Edge<?> other = (Edge<?>) obj;
          return this.getFirstVertex().equals(other.getFirstVertex())
              && this.getSecondVertex().equals(other.getSecondVertex());
        }

        @Override
        public String toString() {
          return getFirstVertex() + "-" + getSecondVertex();
        }
      }

      final class SourceEdge extends DirectedWrappedEdge {
        private static final long serialVersionUID = 7586993772437534452L;

        private final int index;

        /**
         * Edge from source vertex at given index to vertex
         * 
         * @param index
         *          Index of source node
         */
        SourceEdge(int index) { // NOSONAR: intentionally uses super's equals()
          this.index = index;
        }

        @Override
        public V getFirstVertex() {
          return sources.get(index);
        }

        @Override
        public V getSecondVertex() {
          return vertex;
        }
      }

      final class TargetEdge extends DirectedWrappedEdge {
        private static final long serialVersionUID = 8036361359302766844L;

        private final int index;

        /**
         * Edge from vertex to target vertex at given index
         * 
         * @param index
         *          Index of target node
         */
        TargetEdge(int index) { // NOSONAR: intentionally uses super's equals()
          this.index = index;
        }

        @Override
        public V getFirstVertex() {
          return vertex;
        }

        @Override
        public V getSecondVertex() {
          return targets.get(index);
        }
      }
    }

  }

}