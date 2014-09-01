/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.Map.Entry;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.graph.EdgeInCut;
import org.jamesii.core.util.graph.ILabeledGraph;
import org.jamesii.core.util.graph.ISimpleGraph;
import org.jamesii.core.util.graph.LabeledEdge;
import org.jamesii.core.util.graph.LabeledGraph;
import org.jamesii.core.util.graph.SimpleEdge;
import org.jamesii.core.util.graph.SimpleGraph;
import org.jamesii.core.util.graph.trees.SimpleTree;
import org.jamesii.core.util.misc.Pair;

/**
 * A class that stores arbitrary auxiliary functions for partitioning/load
 * balancing.
 * 
 * Date: 14.12.2006
 * 
 * @author Roland Ewald
 */
public class PartitioningUtilities {

  /**
   * The maximal weight to be assigned randomly to a node in a randomly
   * generated tree.
   */
  private static final int UPPER_BOUND_RANDOM_NODE_WEIGHT = 10;

  /** The probability to add a new child vertex for a randomly generated tree. */
  private static final double NEW_CHILD_ADDITION_PROBABILITY = 0.5;

  /** Defining basic colors for the dot files. */
  static final String[] COLORS = { "grey", "red", "green", "blue", "orange",
      "black", "yellow", "cyan", "pink", "white" };

  /** Defining edge style for dot files. */
  public static final String GENERALEDGESTYLE = "arrowhead=none";

  /** Defining node style for dot files. */
  public static final String GENERAL_NODE_STYLE =
      "shape=circle, style=filled, height=.1";

  /** ArrayList of used colors. */
  private static List<String> usedColors = new ArrayList<>();

  /**
   * Returns the equivalence class of a node, i.e. the topmost node in the
   * subtree that belongs to the same partition block
   * 
   * @param node
   *          the node
   * @param childRelation
   *          the child relation
   * @param partition
   *          the partition
   * 
   * @param <PB>
   *          the type of the partition block
   * 
   * @return equivalence class of a node
   */
  protected static <PB> Integer getEquivalenceClass(Integer node,
      Map<Integer, Integer> childRelation, Map<Integer, PB> partition) {

    Integer myParent = childRelation.get(node);
    if (myParent == null) {
      return -1;
    }

    PB myPartitionBlock = partition.get(node);
    PB parentPartitionBlock = partition.get(myParent);

    if (!myPartitionBlock.equals(parentPartitionBlock)) {
      return node;
    }

    return getEquivalenceClass(myParent, childRelation, partition);
  }

  /**
   * Generates a simplified graph that contains lists of original node indices.
   * The graph has to be a tree and has to be connected. Node 0 contains the
   * list with the root node.
   * 
   * The edges denote the index pair that define the corresponding edge in the
   * original graph.
   * 
   * @param partition
   *          partition that indicates the simplification (vertex index =>
   *          partition block index)
   * @param tree
   *          the tree
   * 
   * @return a simplified graph that contains graphs as nodes
   */
  public static LabeledGraph<Integer, LabeledEdge<Integer, EdgeInCut>, List<Integer>, EdgeInCut> getSimplifiedGraph(
      SimpleTree tree, Map<Integer, Integer> partition) {

    Map<Integer, List<Integer>> decomposition = new HashMap<>();

    List<Integer> vertices = tree.getVertices();

    if (vertices.size() == 0) {
      return new LabeledGraph<>(new Integer[0]);
    }

    Map<Integer, Integer> childRelations = tree.getChildToParentMap();

    // Add nodes to equivalence classes
    for (Integer v : vertices) {
      Integer eqClass = getEquivalenceClass(v, childRelations, partition);
      if (!decomposition.containsKey(eqClass)) {
        ArrayList<Integer> set = new ArrayList<>();
        set.add(v);
        decomposition.put(eqClass, set);
      } else {
        decomposition.get(eqClass).add(v);
      }
    }

    // Create new graph
    int numOfPartitionFragments = decomposition.size();
    Integer[] newVertices = new Integer[numOfPartitionFragments];
    for (int i = 0; i < newVertices.length; i++) {
      newVertices[i] = i;
    }

    LabeledGraph<Integer, LabeledEdge<Integer, EdgeInCut>, List<Integer>, EdgeInCut> simplifiedGraph =
        new LabeledGraph<>(newVertices, true);

    Pair<List<Integer>, Map<Integer, Integer>> eqClassToNodeIndexMapping =
        retrieveEquivClassToNodeIndexMapping(simplifiedGraph, decomposition);

    // Add edges
    addEdgesToSimplifiedGraph(partition, childRelations, simplifiedGraph,
        eqClassToNodeIndexMapping.getSecondValue(),
        eqClassToNodeIndexMapping.getFirstValue());

    return simplifiedGraph;
  }

  /**
   * Retrieve mapping from the equivalence class to the node index.
   * 
   * @param simplifiedGraph
   *          the simplified graph
   * @param decomposition
   *          the decomposition
   * @return the pair
   */
  private static Pair<List<Integer>, Map<Integer, Integer>> retrieveEquivClassToNodeIndexMapping(
      LabeledGraph<Integer, LabeledEdge<Integer, EdgeInCut>, List<Integer>, EdgeInCut> simplifiedGraph,
      Map<Integer, List<Integer>> decomposition) {
    Map<Integer, Integer> eqClassToNodeIndexMapping = new HashMap<>();
    List<Integer> eqClassElems = new ArrayList<>();

    // Adding fragment of root node
    simplifiedGraph.setLabel(0, decomposition.get(-1));
    eqClassToNodeIndexMapping.put(-1, 0);

    // Adding other fragments
    Set<Entry<Integer, List<Integer>>> decompositionEntries =
        decomposition.entrySet();

    int currentNode = 1;
    for (Entry<Integer, List<Integer>> decompositionEntry : decompositionEntries) {

      int eqClassElem = decompositionEntry.getKey();

      if (eqClassElem == -1) {
        continue;
      }

      eqClassElems.add(eqClassElem);

      eqClassToNodeIndexMapping.put(eqClassElem, currentNode);
      simplifiedGraph.setLabel(currentNode, decompositionEntry.getValue());
      currentNode++;
    }
    return new Pair<>(eqClassElems, eqClassToNodeIndexMapping);
  }

  /**
   * Adds the edges to simplified graph.
   * 
   * @param partition
   *          the partition
   * @param childRelations
   *          the child relations
   * @param result
   *          the result
   * @param eqClassToNodeIndexMapping
   *          the eq class to node index mapping
   * @param eqClassElems
   *          the eq class elems
   */
  private static void addEdgesToSimplifiedGraph(
      Map<Integer, Integer> partition,
      Map<Integer, Integer> childRelations,
      LabeledGraph<Integer, LabeledEdge<Integer, EdgeInCut>, List<Integer>, EdgeInCut> result,
      Map<Integer, Integer> eqClassToNodeIndexMapping,
      List<Integer> eqClassElems) {
    int numOfEqClassElems = eqClassElems.size();
    for (int i = 0; i < numOfEqClassElems; i++) {
      int eqClassElem = eqClassElems.get(i);
      int parent = childRelations.get(eqClassElem);

      if (parent == -1) {
        continue;
      }

      EdgeInCut cutEdge =
          new EdgeInCut(eqClassElem, parent, partition.get(eqClassElem),
              partition.get(parent));
      result.addEdge(new LabeledEdge<>(eqClassToNodeIndexMapping
          .get(eqClassElem), eqClassToNodeIndexMapping.get(getEquivalenceClass(
          parent, childRelations, partition)), cutEdge));
    }
  }

  /**
   * Reads another file format for storing graphs (supported by METIS).
   * 
   * @param file
   *          the file
   * 
   * @return the simple graph
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static SimpleGraph readGraphFromFile(RandomAccessFile file)
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
   * Rounds a double value to numOfPos digits after the comma.
   * 
   * @param x
   *          the x
   * @param numOfPos
   *          the num of pos
   * 
   * @return the double
   */
  protected static double round(double x, int numOfPos) {
    int z = (int) Math.pow(10, numOfPos);
    return (Math.floor(x * z) / z);
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
  public static void saveGraphToDOT(String fileName,
      ILabeledGraph<Integer, ?, Double, Double> graph,
      Map<Integer, Integer> partition) throws IOException {
    IRandom rand = SimSystem.getRNGGenerator().getNextRNG();
    try (FileWriter file = new FileWriter(fileName)) {
      int vertexCount = graph.getVertexCount();
      Map<Integer, Double> vertexLabels = graph.getVertexLabels();
      Map<Integer, Map<Integer, Double>> edgeLabels = graph.getEdgeLabels();
      List<List<Integer>> adjacencyLists = graph.getAdjacencyLists();

      if (partition != null) {
        tidyUpPartition(partition, adjacencyLists, 0, 0);
      }

      // Writing initialisation
      file.write("digraph g {\n\n");
      file.write("/* This file was generated automatically by MassiveTestingUtility.saveGraphToDOT(...) [part of JAMES II] */\n/* Date: "
          + new Date(System.currentTimeMillis())
          + " */\n\n/* Defining nodes */\n\n");

      file.write("node [" + GENERAL_NODE_STYLE
          + "]; /* General node style*/ \n");

      writeVertexDefinitionsInDOT(partition, rand, file, vertexCount,
          vertexLabels);

      file.write("\n\n/*Defining edges*/\n\n");
      file.write("edge [" + GENERALEDGESTYLE + "]; /* General edge style*/ \n");

      writeEdgeDefinitionsInDOT(file, vertexCount, edgeLabels, adjacencyLists);

      // Writing end of the file
      file.write("\n\n}");
    }

  }

  /**
   * Write edge definitions in dot format.
   * 
   * @param file
   *          the file
   * @param vertexCount
   *          the vertex count
   * @param edgeLabels
   *          the edge labels
   * @param adjacencyLists
   *          the adjacency lists
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void writeEdgeDefinitionsInDOT(FileWriter file,
      int vertexCount, Map<Integer, Map<Integer, Double>> edgeLabels,
      List<List<Integer>> adjacencyLists) throws IOException {
    for (int i = 0; i < vertexCount; i++) {

      Collection<Integer> children = adjacencyLists.get(i);

      for (Integer child : children) {
        if (child > i) {
          file.write("node_" + i + " -> node_" + child + " [label=\""
              + round(edgeLabels.get(i).get(child), 2) + "\"];\n");
        }
      }
    }
  }

  /**
   * Write vertex definitions in dot format.
   * 
   * @param partition
   *          the partition
   * @param rand
   *          the random number generator
   * @param file
   *          the file
   * @param vertexCount
   *          the vertex count
   * @param vertexLabels
   *          the vertex labels
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void writeVertexDefinitionsInDOT(
      Map<Integer, Integer> partition, IRandom rand, FileWriter file,
      int vertexCount, Map<Integer, Double> vertexLabels) throws IOException {
    String nodeDefinition;
    String nodeStyle;
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

      Object vLabel = vertexLabels.get(i);
      double x = 0.0;
      if (vLabel instanceof Double) {
        x = (Double) vLabel;
      }
      nodeStyle = "label=\"" + i + ":" + round(x, 2) + "\"";

      file.write(nodeDefinition + " [" + nodeStyle + "];\n");
    }
  }

  /**
   * Save graph to GraphML.
   * 
   * @param stream
   *          the stream
   * @param graph
   *          the graph
   * @param partition
   *          the partition
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void saveGraphToGraphML(OutputStream stream,
      ISimpleGraph graph, Map<Integer, Integer> partition) throws IOException {

    OutputStreamWriter file = new OutputStreamWriter(stream, "UTF-8");
    int vertexCount = graph.getVertexCount();
    Map<Integer, Double> vertexLabels =
        graph.getVertexLabels();
    Map<Integer, Map<Integer, Double>> edgeLabels =
        graph.getEdgeLabels();
    List<List<Integer>> adjacencyLists = graph.getAdjacencyLists();

    if (partition != null) {
      tidyUpPartition(partition, adjacencyLists, 0, 0);
    }

    // Writing initialization
    file.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    file.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n"
        + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
        + "  xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n"
        + "  http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n\n");

    file.write("<graph id=\"G\" edgedefault=\"undirected\">\n");
    file.write("<key id=\"color\" for=\"node\" attr.name=\"color\" attr.type=\"string\" />\n");
    file.write("<key id=\"label\" for=\"node\" attr.name=\"label\" attr.type=\"string\" />\n");
    file.write("<key id=\"weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\" />\n\n");

    writeVertexDefinitionsToGraphML(partition, file, vertexCount, vertexLabels);

    file.write("\n");

    writeEdgeDefinitionsToGraphML(file, vertexCount, edgeLabels, adjacencyLists);

    // Writing end of the file
    file.write("</graph></graphml>");
    file.flush();
  }

  /**
   * Write edge definitions in GraphML.
   * 
   * @param file
   *          the file
   * @param vertexCount
   *          the vertex count
   * @param edgeLabels
   *          the edge labels
   * @param adjacencyLists
   *          the adjacency lists
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void writeEdgeDefinitionsToGraphML(OutputStreamWriter file,
      int vertexCount, Map<Integer, Map<Integer, Double>> edgeLabels,
      List<List<Integer>> adjacencyLists) throws IOException {
    for (int i = 0; i < vertexCount; i++) {
      Collection<Integer> children = adjacencyLists.get(i);
      for (Integer child : children) {
        if (child > i) {
          file.write("  <edge id=\"e" + i + "_" + child + "\" source=\"n" + i
              + "\" target=\"n" + child + "\">\n");
          file.write("    <data key=\"weight\">"
              + round(edgeLabels.get(i).get(child), 2) + "</data>\n");
          file.write("  </edge>\n");
        }
      }
    }
  }

  /**
   * Write vertex definitions to GraphML.
   * 
   * @param partition
   *          the partition
   * @param file
   *          the file
   * @param vertexCount
   *          the vertex count
   * @param vertexLabels
   *          the vertex labels
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private static void writeVertexDefinitionsToGraphML(
      Map<Integer, Integer> partition, OutputStreamWriter file,
      int vertexCount, Map<Integer, Double> vertexLabels) throws IOException {
    for (int i = 0; i < vertexCount; i++) {
      file.write("  <node id=\"n" + i + "\">\n");

      if (partition != null && partition.containsKey(i)) {
        int p = partition.get(i);

        file.write("    <data key=\"color\">" + p + "</data>\n");
      }

      Object vLabel = vertexLabels.get(i);
      double x = 0.0;
      if (vLabel instanceof Double) {
        x = (Double) vLabel;
      }
      file.write("    <data key=\"label\">" + i + ":" + round(x, 2)
          + "</data>\n");

      file.write("  </node>\n");
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

  /** Randomiser. */
  private final IRandom rand;

  /** Randomiser seed. */
  private long randSeed = 0;

  static {
    Collections.addAll(usedColors, COLORS);
  }

  /**
   * Standard constructor.
   */
  public PartitioningUtilities() {
    rand = SimSystem.getRNGGenerator().getNextRNG();
  }

  /**
   * Constructor with specified random seed.
   * 
   * @param randSeed
   *          the rand seed
   */
  public PartitioningUtilities(long randSeed) {
    this();
    rand.setSeed(randSeed);
  }

  /**
   * Auxiliary function for random graph generation.
   * 
   * @param graph
   *          the graph
   * @param vertexA
   *          the vertex a
   * @param vertexB
   *          the vertex b
   */
  protected void addEdge(SimpleGraph graph, int vertexA, int vertexB) {
    graph.addEdge(new SimpleEdge(vertexA, vertexB, rand.nextDouble()
        * UPPER_BOUND_RANDOM_NODE_WEIGHT));
    graph.setLabel(vertexB, rand.nextDouble() * UPPER_BOUND_RANDOM_NODE_WEIGHT);
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
  protected void addEdge(SimpleTree tree, int vertexA, int vertexB) {
    tree.addEdge(new SimpleEdge(vertexA, vertexB, rand.nextDouble()
        * UPPER_BOUND_RANDOM_NODE_WEIGHT));
    tree.setLabel(vertexB, rand.nextDouble() * UPPER_BOUND_RANDOM_NODE_WEIGHT);
  }

  /**
   * Generates a random tree with parent label = sum of child labels.
   * 
   * @param numOfNodes
   *          the overall number of nodes
   * @param approxNumberOfChildren
   *          the approximate number of children per node
   * @param objectCreator
   *          handles creation of annotated objects, if != null
   * @param fixedLabel
   *          if true, use 1 instead of random labels for leaves
   * 
   * @return labeled graph that is a tree
   * 
   * 
   */
  public SimpleTree generateRandomAccumulatedTree(int numOfNodes,
      int approxNumberOfChildren, IObjectCreator objectCreator,
      boolean fixedLabel) {

    // create tree structure while holding the nodes associated to their depth
    // level
    List<List<Integer>> nodesOnLevel = new ArrayList<>();
    SimpleTree tree =
        createRandomTreeStructure(numOfNodes, approxNumberOfChildren,
            nodesOnLevel);

    assignRandomWeightsToLeaves(fixedLabel, nodesOnLevel, tree);

    if (objectCreator != null) {
      objectCreator.createObjects(tree);
    }

    return tree;
  }

  /**
   * Assign random weights to leave nodes and sum this up for the others,
   * working bottom-up (leaves -> root).
   * 
   * @param weightFixedToOne
   *          the flag to set all leave weights fixed to one (otherwise chosen
   *          randomly)
   * @param nodesOnLevel
   *          the lists of nodes per level
   * @param tree
   *          the tree
   */
  private void assignRandomWeightsToLeaves(boolean weightFixedToOne,
      List<List<Integer>> nodesOnLevel, SimpleTree tree) {
    for (int level = nodesOnLevel.size() - 1; level >= 0; level--) {
      for (int node : nodesOnLevel.get(level)) {
        if (tree.getChildren(node) == null || tree.getChildren(node).isEmpty()) {
          tree.setLabel(node, weightFixedToOne ? 1d : rand.nextDouble()
              * UPPER_BOUND_RANDOM_NODE_WEIGHT);
        } else {
          double label = 0;
          for (int child : tree.getChildren(node)) {
            label += tree.getLabel(child);
          }
          tree.setLabel(node, label);
        }
      }
    }
  }

  /**
   * Creates a random tree structure.
   * 
   * @param numOfNodes
   *          the overall number of nodes
   * @param approxNumberOfChildren
   *          the desired approximate number of children
   * @param nodesOnLevel
   *          the nodes on level, will be filled by this method
   * @return the simple tree
   */
  private SimpleTree createRandomTreeStructure(int numOfNodes,
      int approxNumberOfChildren, List<List<Integer>> nodesOnLevel) {

    SimpleTree tree = new SimpleTree(numOfNodes);
    tree.setTreeRoot(0);
    int currentNodeNumber = 0;
    int numOfNodesToGenerate = numOfNodes - 1;

    nodesOnLevel.add(new ArrayList<Integer>());
    nodesOnLevel.get(0).add(0);

    tree.turnTreeCheckOff();

    while (currentNodeNumber < numOfNodesToGenerate
        && nodesOnLevel.get(nodesOnLevel.size() - 1).size() > 0) {
      currentNodeNumber =
          createAdditionalTreeLevel(tree, nodesOnLevel, approxNumberOfChildren,
              currentNodeNumber, numOfNodesToGenerate);
    }
    if (!tree.turnTreeCheckOn()) {
      throw new IllegalStateException("Random tree generation failed!");
    }
    return tree;
  }

  /**
   * Creates the additional tree level. A level is the set of nodes with the
   * same distance to the root.
   * 
   * @param tree
   *          the tree
   * @param nodesOnLevel
   *          the nodes on each level
   * @param approxNumberOfChildren
   *          the desired approximate number of children
   * @param currentNodeNumber
   *          the current node number
   * @param numOfNodesToGenerate
   *          the number of nodes to generate
   * @return the current number of nodes in the tree
   */
  private int createAdditionalTreeLevel(SimpleTree tree,
      List<List<Integer>> nodesOnLevel, int approxNumberOfChildren,
      int currentNodeNumber, int numOfNodesToGenerate) {
    List<Integer> oldLevel = nodesOnLevel.get(nodesOnLevel.size() - 1);
    nodesOnLevel.add(new ArrayList<Integer>());
    List<Integer> newLevel = nodesOnLevel.get(nodesOnLevel.size() - 1);
    int randomParent =
        ((Double) (Math.floor(rand.nextDouble() * (oldLevel.size()))))
            .intValue();
    currentNodeNumber++;
    addEdge(tree, currentNodeNumber, oldLevel.get(randomParent));
    newLevel.add(currentNodeNumber);

    for (int i : oldLevel) {
      int currentNumberOfChildren = 0;
      while (checkNewChildAddition(approxNumberOfChildren,
          currentNumberOfChildren) && currentNodeNumber < numOfNodesToGenerate) {
        currentNodeNumber++;
        currentNumberOfChildren++;
        addEdge(tree, currentNodeNumber, i);
        newLevel.add(currentNodeNumber);
      }
    }
    return currentNodeNumber;
  }

  /**
   * Checks (randomly) whether a new child shall be added.
   * 
   * @param approxNumberOfChildren
   *          the desired approximate number of children per node
   * @param currentNumberOfChildren
   *          the current number of children at the node
   * @return true, if additional child shall be added
   */
  private boolean checkNewChildAddition(int approxNumberOfChildren,
      int currentNumberOfChildren) {
    double invertedCurrentChildRatio =
        approxNumberOfChildren / (currentNumberOfChildren + 1.);
    return invertedCurrentChildRatio * rand.nextDouble() > NEW_CHILD_ADDITION_PROBABILITY;
  }

  /**
   * Generates a random graph in wich every has approximately
   * (approxNumberOfNeighbours) neighbours. if after the generation there are
   * more components, random edges will be added until the graph is connected
   * 
   * @param numOfNodes
   *          the num of nodes
   * @param approxNumberOfNeighbours
   *          the approx number of neighbours
   * @param simpleGraph
   *          if true, multiple edges will be avoided
   * @param objectCreator
   *          handles creation of annotated objects, if != null
   * 
   * @return labeled graph
   */
  public SimpleGraph generateRandomGraph(int numOfNodes,
      double approxNumberOfNeighbours, boolean simpleGraph,
      IObjectCreator objectCreator) {

    // Initializing...
    SimpleGraph graph = new SimpleGraph(numOfNodes);

    if (simpleGraph) {
      graph.setSimple(true);
    }

    int minNumberOfEdges =
        (int) Math.floor(numOfNodes * approxNumberOfNeighbours / 2) + 1;

    int edgeCount = 0;
    List<List<Integer>> connectedSets = new ArrayList<>();

    // Initialize component list, set random vertex labels
    for (int i = 0; i < numOfNodes; i++) {
      ArrayList<Integer> component = new ArrayList<>();
      component.add(i);
      connectedSets.add(component);
      graph.setLabel(i, rand.nextDouble() * UPPER_BOUND_RANDOM_NODE_WEIGHT);
    }

    while (edgeCount < minNumberOfEdges || connectedSets.size() > 1) {
      int vertexA = (int) Math.floor(rand.nextFloat() * numOfNodes);
      int vertexB = (int) Math.floor(rand.nextFloat() * numOfNodes);
      if (vertexA == vertexB) {
        continue;
      }

      this.addEdge(graph, vertexA, vertexB);
      edgeCount++;

      updateConnectedSets(connectedSets, vertexA, vertexB);
    }

    if (objectCreator != null) {
      objectCreator.createObjects(graph);
    }

    return graph;
  }

  /**
   * Generates a random tree.
   * 
   * @param numOfNodes
   *          the num of nodes
   * @param approxNumberOfChildren
   *          the approx number of children
   * @param objectCreator
   *          handles creation of annotated objects, if != null
   * 
   * @return labeled graph that is a tree
   */
  public SimpleTree generateRandomTree(int numOfNodes,
      int approxNumberOfChildren, IObjectCreator objectCreator) {

    List<List<Integer>> nodesOnLevel = new ArrayList<>();
    SimpleTree tree =
        createRandomTreeStructure(numOfNodes, approxNumberOfChildren,
            nodesOnLevel);

    for (int i = 0; i < numOfNodes + 1; i++) {
      tree.setLabel(i, rand.nextDouble() * UPPER_BOUND_RANDOM_NODE_WEIGHT);
    }

    if (objectCreator != null) {
      objectCreator.createObjects(tree);
    }

    return tree;
  }

  /**
   * Returns rand seed.
   * 
   * @return initial randomizer value
   */
  public long getRandSeed() {
    return randSeed;
  }

  /**
   * Sets rand seed.
   * 
   * @param randSeed
   *          the rand seed
   */
  public void setRandSeed(long randSeed) {
    this.randSeed = randSeed;
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
  private void updateConnectedSets(List<List<Integer>> connectedSets,
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
