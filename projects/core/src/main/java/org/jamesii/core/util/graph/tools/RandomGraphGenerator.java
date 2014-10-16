/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.util.graph.AnnotatedEdge;
import org.jamesii.core.util.graph.ISimpleGraph;
import org.jamesii.core.util.graph.SimpleGraph;

/**
 * Generates any kind of random graph with given random distributions.
 * 
 * The edge distribution is used to determine how many neighbours a node should
 * have. It must be really given or uniform distribution will be used. The node
 * label and edge label distributions will be used to set labels. You can set
 * them to null to use constant ones instead of 'real' labels. If not set,
 * uniform distribution will be used.
 * 
 * @author Ragnar Nevries
 */
public class RandomGraphGenerator {

  /** The distribution with which we should generate degrees. */
  private IDistribution degDistri;

  /** The distribution with which we should generate edges. */
  private IDistribution elDistri;

  /** The nl distri. */
  private IDistribution nlDistri;

  /**
   * Uses uniform distributions with degree [1,10], labels [0,1].
   */
  public RandomGraphGenerator() {
    UniformDistribution dis =
        new UniformDistribution(SimSystem.getRNGGenerator().getNextRNG());
    dis.setLowerBorder(1d);
    dis.setUpperBorder(10d);
    degDistri = dis;
    nlDistri = dis.getSimilar();
    elDistri = dis.getSimilar();
  }

  /**
   * Let u set edgeDistribution, others will be uniform.
   * 
   * @param degreeDistribution
   *          the degree distribution
   */
  public RandomGraphGenerator(IDistribution degreeDistribution) {
    IDistribution dis =
        new UniformDistribution(SimSystem.getRNGGenerator().getNextRNG());
    degDistri = degreeDistribution;
    nlDistri = dis;
    elDistri = dis.getSimilar(dis.getRandom());
  }

  /**
   * Let u set edge and node label distribution, edge label will be uniform.
   * 
   * @param degreeDistribution
   *          Custom edge distribution, must give values >= 1 (ceiling is used)
   * @param nodeLabelDistribution
   *          Custom node label distribution or null to set constant one
   */
  public RandomGraphGenerator(IDistribution degreeDistribution,
      IDistribution nodeLabelDistribution) {
    IDistribution dis =
        new UniformDistribution(SimSystem.getRNGGenerator().getNextRNG());
    degDistri = degreeDistribution;
    nlDistri = nodeLabelDistribution;
    elDistri = dis;
  }

  /**
   * Let u set all distributions.
   * 
   * @param degreeDistribution
   *          Custom edge distribution, must give values >= 1 (ceiling is used)
   * @param nodeLabelDistribution
   *          Custom node label distribution or null to set constant one
   * @param edgeLabelDistribution
   *          Custom edge label distribution or null to set constant one
   */
  public RandomGraphGenerator(IDistribution degreeDistribution,
      IDistribution nodeLabelDistribution,
      IDistribution edgeLabelDistribution) {
    degDistri = degreeDistribution;
    nlDistri = nodeLabelDistribution;
    elDistri = edgeLabelDistribution;
  }

  /**
   * Constructs a random SimpleGraph with a given number of vertices.
   * 
   * @param numberOfNodes
   *          the number of nodes
   * 
   * @return the random simple graph
   */
  public ISimpleGraph getRandomSimpleGraph(int numberOfNodes) {
    return getRandomSimpleGraph(new SimpleGraph(numberOfNodes));
  }

  /**
   * Constructs a random graph, implementing ISimpleGraph with a given input
   * graph. It should have the wanted number of nodes, all edges will be
   * deleted.
   * 
   * @param graph
   *          the graph
   * @param <T>
   *          the type graph to be randomly created, has to be a sub-class of
   *          {@link ISimpleGraph}
   * 
   * @return the random simple graph
   */
  public <T extends ISimpleGraph> T getRandomSimpleGraph(T graph) {
    // lets give vertex labels
    for (Integer node : graph.getVertices()) {
      graph.setLabel(node, nextDisVal(nlDistri));
    }

    // generate edges

    // the list of nodes. ordering is connected with following two later
    ArrayList<Integer> nodeList = new ArrayList<>(graph.getVertices());
    // hold the degree a node should have at end, index connected with above
    ArrayList<Integer> nodeShouldDeg = new ArrayList<>(graph.getVertexCount());
    // hold the degree a node has so far, index connected with above
    ArrayList<Integer> nodeHasDeg = new ArrayList<>(graph.getVertexCount());

    // fill nodeShouldDeg with random numbers and nodeHasDeg with zeros
    for (int i = 0; i < nodeList.size(); i++) {
      int value = (int) Math.ceil(degDistri.getRandomNumber());
      if (value < 1) {
        throw new RuntimeException(
            "RandomTreeGenerator: edge degree distribution must return values >= 1");
      }
      nodeShouldDeg.add(value);
      nodeHasDeg.add(0);
    }

    // sort shouldDeg list manually to have same order in nodeList
    // stupid linear sorting (item swapping)
    // TODO: improve sort algorithm
    for (int i = 0; i < nodeList.size() - 1; i++) {
      List<Integer> remainingShouldDeg =
          nodeShouldDeg.subList(i, nodeShouldDeg.size());
      int maxIndex =
          i + remainingShouldDeg.indexOf(Collections.max(remainingShouldDeg));
      Collections.swap(nodeList, i, maxIndex);
      Collections.swap(nodeShouldDeg, i, maxIndex);
    }

    // add edges as good as possible (better more than wished to make graph
    // connected)
    // last node must be touched to test if it is connected
    IRandom rnd = SimSystem.getRNGGenerator().getNextRNG();
    for (int i = 0; i < nodeList.size(); i++) {
      // get a list of possible partners by getting all 'right' of i and sort
      // out the ones with max degree
      ArrayList<Integer> possibleNeighbourIndices = new ArrayList<>();
      for (int j = i + 1; j < nodeList.size(); j++) {
        if (nodeHasDeg.get(j) < nodeShouldDeg.get(j)) {
          possibleNeighbourIndices.add(j);
        }
      }

      /*
       * if (nodeList.get(i).equals(18)) { i=i; }
       */

      while (nodeHasDeg.get(i) < nodeShouldDeg.get(i)) {
        if (possibleNeighbourIndices.isEmpty()) {
          // we want more edges but no more partners are there
          if (nodeHasDeg.get(i) == 0) {
            // but we are not connected at all, so we try to force an edge
            // to predecessor or successor
            if (i > 0) {
              graph.addEdge(new AnnotatedEdge<>(nodeList.get(i), nodeList
                  .get(i - 1), nextDisVal(elDistri)));
              System.out.println("Force edge from " + nodeList.get(i) + " to "
                  + nodeList.get(i - 1));
              nodeHasDeg.set(i - 1, nodeHasDeg.get(i - 1) + 1);
              nodeHasDeg.set(i, 1);
            }
            if (i < nodeList.size() - 1) {
              graph.addEdge(new AnnotatedEdge<>(nodeList.get(i), nodeList
                  .get(i + 1), nextDisVal(elDistri)));
              System.out.println("Force edge from " + nodeList.get(i) + " to "
                  + nodeList.get(i + 1));
              nodeHasDeg.set(i + 1, nodeHasDeg.get(i + 1) + 1);
              nodeHasDeg.set(i, 1);
            } else {
              // we are connected, so we give up
              break;
            }
          }
          // no success - we are one node only and therefore also connected by
          // definition
          break;
        }

        // ok, we have possible partners, lets make an edge
        // pick one randomly
        int partnerIndex = rnd.nextInt(possibleNeighbourIndices.size());
        graph.addEdge(new AnnotatedEdge<>(nodeList.get(i), nodeList
            .get(possibleNeighbourIndices.get(partnerIndex)),
            nextDisVal(elDistri)));

        // update degrees
        nodeHasDeg.set(i, nodeHasDeg.get(i) + 1);
        nodeHasDeg.set(possibleNeighbourIndices.get(partnerIndex),
            nodeHasDeg.get(possibleNeighbourIndices.get(partnerIndex)) + 1);
        // delete from possible partners if max degree reached
        if (nodeHasDeg.get(possibleNeighbourIndices.get(partnerIndex)) >= nodeShouldDeg
            .get(possibleNeighbourIndices.get(partnerIndex))) {
          possibleNeighbourIndices.remove(partnerIndex);
        }
      }
    }

    return graph;
  }

  /**
   * Next dis val.
   * 
   * @param d
   *          the d
   * 
   * @return the double
   */
  private double nextDisVal(IDistribution d) {
    if (d == null) {
      return 1d;
    }
    return d.getRandomNumber();
  }
}
