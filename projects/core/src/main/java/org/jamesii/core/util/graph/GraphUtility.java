/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.core.math.Calc;
import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.NormalDistributionFactory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.trees.SimpleTree;

/**
 * Utility functions for automated and configurable testing of algorithms. E.g.,
 * the generation of random graphs and tree, saving graph objects in dot -
 * format, etc.
 * 
 * Date: 01:34:55 25.01.2005
 * 
 * @author Roland Ewald
 */
public class GraphUtility {

  /** Defining basic colours for the dot files. */
  private static final String[] colors = { "grey", "red", "green", "blue",
      "orange", "black", "yellow", "cyan", "pink", "white" };

  /** Defines a header for DOT file generation. */
  private static final String dotFileHeader =
      "/* This file was generated automatically by JAMES II] */\n/* Date: ";

  /** Defining edge style for dot files. */
  private static final String generalEdgeStyle = "arrowhead=none";

  /** Defining node style for dot files. */
  private static final String generalNodeStyle =
      "shape=circle, style=filled, height=.1";

  /** ArrayList of used colours. */
  private static List<String> usedColors = new ArrayList<>();

  /** Randomiser. */
  private IRandom rand;

  /**
   * Writes a graph partition for partitioning with METIS.
   * 
   * @param fileName
   *          the file name of the file the graph is stored to
   * @param graph
   *          the labelled graph to be stored to the file
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void saveGraphForMETIS(String fileName, ISimpleGraph graph)
      throws IOException {
    try (FileWriter file = new FileWriter(fileName)) {
      int vertexCount = graph.getVertexCount();

      Map<Integer, Double> vertexLabels = graph.getVertexLabels();
      Map<Integer, Map<Integer, Double>> edgeLabels = graph.getEdgeLabels();

      int numOfEdgeLabels = edgeLabels.size();
      int edgeCount = 0;
      for (int i = 0; i < numOfEdgeLabels; i++) {
        edgeCount += edgeLabels.get(i).size();
      }
      edgeCount = edgeCount / 2;

      file.write(vertexCount + " " + edgeCount + " 11\n");

      for (int i = 0; i < vertexCount; i++) {

        file.write(Math.round(vertexLabels.get(i)) + " ");

        Set<Entry<Integer, Double>> labels = edgeLabels.get(i).entrySet();
        for (Entry<Integer, Double> label : labels) {
          file.write((label.getKey() + 1) + " " + Math.round(label.getValue())
              + " ");
        }

        file.write("\n");
      }

    }

  }

  /**
   * Writes a graph partition to DOT format.
   * 
   * @param fileName
   *          the file name
   * @param graph
   *          the graph
   * @param partition
   *          the partition
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void saveGraphToDOT(String fileName, ISimpleGraph graph,
      Map<Integer, Integer> partition) throws IOException {

    IRandom rand = SimSystem.getRNGGenerator().getNextRNG();
    try (FileWriter file = new FileWriter(fileName)) {
      int vertexCount = graph.getVertexCount();

      Map<Integer, Double> vertexLabels = graph.getVertexLabels();
      Map<Integer, Map<Integer, Double>> edgeLabels = graph.getEdgeLabels();

      List<List<Integer>> adjacencyLists = graph.getAdjacencyLists();
      String nodeDefinition;
      String nodeStyle;

      if (partition != null) {
        tidyUpPartition(partition, adjacencyLists, 0, 0);
      }

      // Writing initialisation
      file.write("digraph g {\n\n");
      file.write(dotFileHeader + new Date(System.currentTimeMillis())
          + " */\n\n/* Defining nodes */\n\n");

      file.write("node [" + generalNodeStyle + "]; /* General node style*/ \n");

      // Writing all vertices
      for (int i = 0; i < vertexCount; i++) {
        nodeDefinition = "node_" + i;
        nodeStyle = "";

        if (partition != null && partition.containsKey(i)) {
          int p = partition.get(i);

          if (p >= usedColors.size()) {
            for (int k = usedColors.size(); k <= p; k++) {
              usedColors.add(rand.nextDouble() + " " + rand.nextDouble() + " "
                  + rand.nextDouble());
            }
          }

          file.write("node [color=\"" + usedColors.get(p) + "\"];\n");
        }

        Double vLabel = vertexLabels.get(i);
        double x = 0.0;
        if (vLabel != null) {
          x = vLabel;
        }
        nodeStyle = "label=\"" + i + ":" + Calc.round(x, 2) + "\"";

        file.write(nodeDefinition + " [" + nodeStyle + "];\n");
      }

      file.write("\n\n/*Defining edges*/\n\n");
      file.write("edge [" + generalEdgeStyle + "]; /* General edge style*/ \n");

      // Writing all edges

      for (int i = 0; i < vertexCount; i++) {

        List<Integer> children = adjacencyLists.get(i);

        for (Integer child : children) {
          if (child > i) {
            file.write("node_" + i + " -> node_" + child + " [label=\""
                + Calc.round(edgeLabels.get(i).get(child), 2) + "\"];\n");
          }
        }
      }

      // Writing end of the file
      file.write("\n\n}");

    }

  }

  /**
   * Writes a graph to DOT format.
   * 
   * @param fileName
   *          the name of the target file
   * @param graph
   *          the graph
   * @param specificNodeStyle
   *          specific node style (default is null)
   * 
   * @param <V>
   *          type of vertices
   * @param <E>
   *          type of edges
   * 
   * @throws IOException
   *           if file output goes wrong
   */
  @SuppressWarnings("unchecked")
  // vertexLabel conversion should be safe, guarded by instanceof
  public static <V, E extends Edge<V>> void saveGraphToDOT(String fileName,
      IGraph<V, E> graph, String specificNodeStyle) throws IOException {
    try (FileWriter file = new FileWriter(fileName)) {
      List<V> vertices = graph.getVertices();
      Map<V, Integer> vertexIndices = new HashMap<>();
      List<List<V>> adjacencyLists = graph.getAdjacencyLists();
      int vertexCount = graph.getVertexCount();

      Map<Integer, Object> vertexLabels = new HashMap<>();
      Map<Integer, Map<Integer, Object>> edgeLabels = new HashMap<>();

      if (graph instanceof ILabeledGraph) {
        Map<V, ?> vLabels =
            ((ILabeledGraph<V, ?, ?, ?>) graph).getVertexLabels();
        if (vLabels.keySet().iterator().next() instanceof Integer) {
          vertexLabels = (HashMap<Integer, Object>) vLabels;
          // edgeLabels = ((ILabeledGraph<Integer, ?, ?, ?>)
          // graph).getEdgeLabels();
        }

      }

      String nodeDefinition;
      String nodeStyle;

      // Writing initialization
      file.write("digraph g {\n\n");
      file.write(dotFileHeader + new Date(System.currentTimeMillis())
          + " */\n\n/* Defining nodes */\n\n");

      file.write("node ["
          + (specificNodeStyle == null ? generalNodeStyle : specificNodeStyle)
          + "]; /* General node style*/ \n");

      // Writing all vertices
      for (int i = 0; i < vertexCount; i++) {
        nodeDefinition = "node_" + i;
        nodeStyle = "";
        vertexIndices.put(vertices.get(i), i);
        Object vLabel = vertexLabels.get(i);
        nodeStyle =
            "label=\""
                + i
                + ":"
                + (vLabel != null ? vLabel.toString() : vertices.get(i)
                    .toString()) + "\"";

        file.write(nodeDefinition + " [" + nodeStyle + "];\n");
      }

      file.write("\n\n/*Defining edges*/\n\n");
      file.write("edge [" + generalEdgeStyle + "]; /* General edge style*/ \n");

      // Writing all edges
      for (int i = 0; i < vertexCount; i++) {
        V vertex = vertices.get(i);
        List<V> neighbours = adjacencyLists.get(i);
        int vertexIndex = vertexIndices.get(vertex);
        for (V neighbour : neighbours) {
          int neighbourIndex = vertexIndices.get(neighbour);
          if (vertexIndex < neighbourIndex) {
            file.write("node_"
                + vertexIndex
                + " -> node_"
                + neighbourIndex
                + ((!edgeLabels.containsKey(vertexIndex) || !edgeLabels.get(
                    vertexIndex).containsKey(neighbourIndex)) ? "" : edgeLabels
                    .get(vertexIndex).containsKey(neighbourIndex)) + ";\n");
          }
        }
      }

      // Writing end of the file
      file.write("\n\n}");
    }

  }

  /**
   * Ensures that every unassigned node inherits the partition block from its
   * nearest assigned ancestor.
   * 
   * @param partition
   *          the partition
   * @param adjacencyLists
   *          the adjacency lists
   * @param rootNode
   *          the root node
   * @param defaultPartition
   *          the default partition
   */
  public static void tidyUpPartition(Map<Integer, Integer> partition,
      List<List<Integer>> adjacencyLists, int rootNode, int defaultPartition) {

    int myPartition;
    if (partition.containsKey(rootNode)) {
      myPartition = partition.get(rootNode);
    } else {
      myPartition = defaultPartition;
      partition.put(rootNode, myPartition);
    }

    // Recursive go through all child nodes
    List<Integer> childNodes = adjacencyLists.get(rootNode);
    for (Integer child : childNodes) {
      if (child > rootNode) {
        tidyUpPartition(partition, adjacencyLists, child, myPartition);
      }
    }
  }

  static {
    Collections.addAll(usedColors, colors);
  }

  /**
   * Standard constructor.
   */
  public GraphUtility() {
    rand = SimSystem.getRNGGenerator().getNextRNG();
  }

  /**
   * Auxiliary function for random tree generation.
   * 
   * @param tree
   *          the tree
   * @param vertexA
   *          the vertex a
   * @param vertexB
   *          the vertex b
   */
  protected void addEdge(SimpleGraph tree, int vertexA, int vertexB) {
    tree.addEdge(new SimpleEdge(vertexA, vertexB, rand.nextDouble() * 10));
    tree.setLabel(vertexB, rand.nextDouble() * 10);
  }

  /**
   * Auxiliary function for random tree generation.
   * 
   * @param tree
   *          the tree
   * @param child
   *          the child
   * @param parent
   *          the parent
   */
  protected void addEdge(SimpleTree tree, int child, int parent) {
    tree.addEdge(new SimpleEdge(child, parent, rand.nextDouble() * 10));
    tree.setLabel(child, rand.nextDouble() * 10);
  }

  /**
   * Generates a random graph in which every has approximately
   * (approxNumberOfNeighbours) neighbours. if after the generation there are
   * more components, random edges will be added until the graph is connected
   * 
   * @param numOfNodes
   *          the number of nodes
   * @param approxNumberOfNeighbours
   *          the approximate number of neighbours
   * @param simpleGraph
   *          if true, multiple edges will be avoided
   * 
   * @return labelled graph
   */
  public SimpleGraph generateRandomGraph(int numOfNodes,
      double approxNumberOfNeighbours, boolean simpleGraph) {

    // Initialising...
    SimpleGraph lg = new SimpleGraph(numOfNodes);
    if (simpleGraph) {
      lg.setSimple(true);
    }

    int minNumberOfEdges =
        (int) Math.floor(numOfNodes * approxNumberOfNeighbours / 2) + 1;

    int edgeCount = 0;
    List<List<Integer>> connectedSets = new ArrayList<>();

    // Initialise component list, set random vertex labels
    for (int i = 0; i < numOfNodes; i++) {
      ArrayList<Integer> component = new ArrayList<>();
      component.add(i);
      connectedSets.add(component);
      lg.setLabel(i, rand.nextDouble() * 10);
    }

    while (edgeCount < minNumberOfEdges || connectedSets.size() > 1) {
      int vertexA = (int) Math.floor(rand.nextFloat() * numOfNodes);
      int vertexB = (int) Math.floor(rand.nextFloat() * numOfNodes);
      if (vertexA == vertexB) {
        continue;
      }

      this.addEdge(lg, vertexA, vertexB);
      edgeCount++;

      updateConnectedSets(connectedSets, vertexA, vertexB);
    }

    return lg;
  }

  /**
   * Generates a random tree.
   * 
   * @param numOfNodes
   *          the number of nodes
   * @param approxNumberOfChildren
   *          the approximate number of children
   * 
   * @return labelled graph that is a tree
   */
  public SimpleTree generateRandomTree(int numOfNodes,
      int approxNumberOfChildren) {

    SimpleTree tree = new SimpleTree(numOfNodes);
    tree.turnTreeCheckOff();

    int actualNodeNum = 1;
    double randomNumber = 0.0;

    ArrayList<Integer> nodesOnLevel = new ArrayList<>();
    nodesOnLevel.add(0);

    while (actualNodeNum < numOfNodes && nodesOnLevel.size() > 0) {

      ArrayList<Integer> oldLevel = nodesOnLevel;
      nodesOnLevel = new ArrayList<>();
      int randomParent =
          ((Double) (Math.floor(rand.nextDouble() * (oldLevel.size()))))
              .intValue();
      addEdge(tree, actualNodeNum, oldLevel.get(randomParent));
      nodesOnLevel.add(actualNodeNum);
      actualNodeNum++;
      AbstractDistribution normDist =
          (new NormalDistributionFactory()).create((new ParameterBlock()
              .addSubBl(NormalDistributionFactory.MEAN, 0).addSubBl(
              NormalDistributionFactory.DEVIATION, 1.0)), rand);

      for (int i : oldLevel) {

        randomNumber =
            approxNumberOfChildren + 0.25 * approxNumberOfChildren
                * normDist.getRandomNumber();

        while (randomNumber > 0.5 && actualNodeNum < numOfNodes) {
          addEdge(tree, actualNodeNum, i);
          nodesOnLevel.add(actualNodeNum);
          actualNodeNum++;
          randomNumber--;
        }
      }

    }

    for (int i = 0; i < numOfNodes; i++) {
      tree.setLabel(i, rand.nextDouble() * 10);
    }

    if (!tree.turnTreeCheckOn()) {
      throw new IllegalStateException("Problem with random tree creation");
    }

    tree.setTreeRoot(0);

    return tree;
  }

  /**
   * Reads a graph in the JOSTLE format. See
   * http://www.gre.ac.uk/~c.walshaw/jostle - basically an adjacency list.
   * Creates corresponding labelled graph object
   * 
   * @param file
   *          the file
   * 
   * @return the simple graph
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  protected SimpleGraph readGraphFromFile(RandomAccessFile file)
      throws IOException {

    String line = file.readLine();
    String[] header = line.split(" ");

    int numOfVertices = Integer.parseInt(header[0]);

    SimpleGraph graph = new SimpleGraph(numOfVertices);

    line = file.readLine();
    int actualVertex = 0;
    while (line != null) {

      graph.setLabel(actualVertex, 1.0);
      String[] neighbours = line.split(" ");

      for (String neighbour : neighbours) {
        if (neighbour.length() > 0) {
          graph.addEdge(new SimpleEdge(actualVertex, Integer
              .parseInt(neighbour) - 1, 1.0));
        }
      }

      line = file.readLine();
      actualVertex++;
    }

    return graph;
  }

  /**
   * Sets RNG seed.
   * 
   * @param randSeed
   *          the RNG seed
   */
  public void setRandSeed(long randSeed) {
    this.rand.setSeed(randSeed);
  }

  /**
   * Auxiliary function that ensures randomly generated graphs to be connected.
   * 
   * @param connectedSets
   *          the connected sets
   * @param vertexA
   *          the vertex a
   * @param vertexB
   *          the vertex b
   */
  private static void updateConnectedSets(List<List<Integer>> connectedSets,
      int vertexA, int vertexB) {

    int setsSize = connectedSets.size();

    // Search for sets of vertices:
    int setA = -1, setB = -1;
    boolean setAFound = false, setBFound = false;
    for (int i = 0; i < setsSize; i++) {
      if (!setAFound && connectedSets.get(i).contains(vertexA)) {
        setA = i;
        setAFound = true;
      }
      if (!setBFound && connectedSets.get(i).contains(vertexB)) {
        setB = i;
        setBFound = true;
      }
      if (setAFound && setBFound) {
        break;
      }
    }

    // Return if wrong attempt or the vertices are already lying in the same
    // component
    if (setA == -1 || setB == -1 || (setA == setB)) {
      return;
    }

    // Add list with vertexB to list with vertexA
    connectedSets.get(setA).addAll(connectedSets.get(setB));
    connectedSets.remove(setB);
  }

}