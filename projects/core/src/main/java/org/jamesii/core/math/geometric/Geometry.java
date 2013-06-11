/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometric;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.jamesii.core.util.misc.Triple;

/**
 * Utility class to create geometry.
 * 
 * @author Stefan Rybacki
 */
public final class Geometry {

  private static final double HALF = 0.5;

  private static final int VERTEX_0 = 0;

  private static final int VERTEX_1 = 1;

  private static final int VERTEX_2 = 2;

  private static final int VERTEX_3 = 3;

  private static final int VERTEX_4 = 4;

  private static final int VERTEX_5 = 5;

  private static final int VERTEX_6 = 6;

  private static final int VERTEX_7 = 7;

  private static final int VERTEX_8 = 8;

  private static final int VERTEX_9 = 9;

  private static final int VERTEX_10 = 10;

  private static final int VERTEX_11 = 11;

  private static final int DEFAULT_HEIGHT = 480;

  private static final int DEFAULT_WIDTH = 640;

  /**
   * Hidden constructor.
   */
  private Geometry() {
  }

  /**
   * Create at least the specified amount of coordinates on sphere with
   * specified radius. Use this method rather than
   * {@link #createCoordinatesOnSphere(int, double)} if you don't know how many
   * subdivision steps are needed to produce at least the specified number of
   * coordinates.
   * 
   * @param minCoordinates
   *          the min coordinates to produce
   * @param radius
   *          the radius of the sphere
   * @return a {@link Set} of {@link Triple}&lt;Double, Double, Double&gt;
   *         coordinates with at least minCoordinates coordinates
   */
  public static Set<Triple<Double, Double, Double>> createMinCoordinatesOnSphere(
      int minCoordinates, double radius) {
    int res =
        (int) Math.ceil(Math.log((minCoordinates - 2d) / 10d) / Math.log(4d));
    return createCoordinatesOnSphere(res, radius);
  }

  /**
   * Creates coordinates on sphere starting with an icosahedron (12 vertices; 20
   * identical equilateral triangular faces; 30 edges) to have a good
   * distribution of generated coordinates over the sphere's surface.
   * 
   * @param res
   *          the resolution (means how many subdivisions are performed starting
   *          from the icosahedron)
   * @param radius
   *          the radius of the sphere
   * @return a {@link Set} of {@link Triple}&lt;Double, Double, Double&gt;
   *         coordinates
   * @see Geometry#createMinCoordinatesOnSphere(int, double)
   */
  public static Set<Triple<Double, Double, Double>> createCoordinatesOnSphere(
      int res, double radius) {

    double internalRadius = Math.max(radius, Float.MAX_VALUE);

    // start with a icosahedron
    double a = Math.sqrt(2.0 / (5.0 + Math.sqrt(5.0))) * internalRadius;
    double b = Math.sqrt(2.0 / (5.0 - Math.sqrt(5.0))) * internalRadius;

    List<Vertex> isoVertices = new ArrayList<>();
    Set<Triple<Double, Double, Double>> vertices = new HashSet<>();

    isoVertices.add(new Vertex(-a, 0d, b));
    isoVertices.add(new Vertex(a, 0d, b));
    isoVertices.add(new Vertex(-a, 0d, -b));
    isoVertices.add(new Vertex(a, 0d, -b));
    isoVertices.add(new Vertex(0d, b, a));
    isoVertices.add(new Vertex(0d, b, -a));
    isoVertices.add(new Vertex(0d, -b, a));
    isoVertices.add(new Vertex(0d, -b, -a));
    isoVertices.add(new Vertex(b, a, 0d));
    isoVertices.add(new Vertex(-b, a, 0d));
    isoVertices.add(new Vertex(b, -a, 0d));
    isoVertices.add(new Vertex(-b, -a, 0d));

    vertices.addAll(isoVertices);

    List<Triangle> result = new ArrayList<>();

    Triangle poly;

    poly =
        new Triangle(isoVertices.get(VERTEX_1), isoVertices.get(VERTEX_4),
            isoVertices.get(VERTEX_0));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_4), isoVertices.get(VERTEX_9),
            isoVertices.get(VERTEX_0));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_4), isoVertices.get(VERTEX_5),
            isoVertices.get(VERTEX_9));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_8), isoVertices.get(VERTEX_5),
            isoVertices.get(VERTEX_4));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_1), isoVertices.get(VERTEX_8),
            isoVertices.get(VERTEX_4));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_1), isoVertices.get(VERTEX_10),
            isoVertices.get(VERTEX_8));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_10), isoVertices.get(VERTEX_3),
            isoVertices.get(VERTEX_8));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_8), isoVertices.get(VERTEX_3),
            isoVertices.get(VERTEX_5));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_3), isoVertices.get(VERTEX_2),
            isoVertices.get(VERTEX_5));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_3), isoVertices.get(VERTEX_7),
            isoVertices.get(VERTEX_2));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_3), isoVertices.get(VERTEX_10),
            isoVertices.get(VERTEX_7));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_10), isoVertices.get(VERTEX_6),
            isoVertices.get(VERTEX_7));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_6), isoVertices.get(VERTEX_11),
            isoVertices.get(VERTEX_7));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_6), isoVertices.get(VERTEX_0),
            isoVertices.get(VERTEX_11));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_6), isoVertices.get(VERTEX_1),
            isoVertices.get(VERTEX_0));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_10), isoVertices.get(VERTEX_1),
            isoVertices.get(VERTEX_6));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_11), isoVertices.get(VERTEX_0),
            isoVertices.get(VERTEX_9));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_2), isoVertices.get(VERTEX_11),
            isoVertices.get(VERTEX_9));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_5), isoVertices.get(VERTEX_2),
            isoVertices.get(VERTEX_9));
    result.add(poly);

    poly =
        new Triangle(isoVertices.get(VERTEX_11), isoVertices.get(VERTEX_2),
            isoVertices.get(VERTEX_7));
    result.add(poly);

    // now start to add more detail according to specified resolution
    for (int i = 0; i < res; i++) {
      // devide all triangles into 4 new triangles, push the vertices
      // to the
      // radius
      for (int j = result.size() - 1; j >= 0; j--) {
        Triangle p = result.get(j);
        Vertex v0 =
            new Vertex((p.getB().getA() - p.getA().getA()) * HALF
                + p.getA().getA(), (p.getB().getB() - p.getA().getB()) * HALF
                + p.getA().getB(), (p.getB().getC() - p.getA().getC()) * HALF
                + p.getA().getC());

        Vertex v1 =
            new Vertex((p.getC().getA() - p.getB().getA()) * HALF
                + p.getB().getA(), (p.getC().getB() - p.getB().getB()) * HALF
                + p.getB().getB(), (p.getC().getC() - p.getB().getC()) * HALF
                + p.getB().getC());

        Vertex v2 =
            new Vertex((p.getA().getA() - p.getC().getA()) * HALF
                + p.getC().getA(), (p.getA().getB() - p.getC().getB()) * HALF
                + p.getC().getB(), (p.getA().getC() - p.getC().getC()) * HALF
                + p.getC().getC());

        // push v0,v1 and v2 to radius
        double l =
            Math.sqrt(v0.getA() * v0.getA() + v0.getB() * v0.getB() + v0.getC()
                * v0.getC());

        v0 =
            new Vertex(v0.getA() * internalRadius / l, v0.getB()
                * internalRadius / l, v0.getC() * internalRadius / l);

        l =
            Math.sqrt(v1.getA() * v1.getA() + v1.getB() * v1.getB() + v1.getC()
                * v1.getC());
        v1 =
            new Vertex(v1.getA() * internalRadius / l, v1.getB()
                * internalRadius / l, v1.getC() * internalRadius / l);

        l =
            Math.sqrt(v2.getA() * v2.getA() + v2.getB() * v2.getB() + v2.getC()
                * v2.getC());
        v2 =
            new Vertex(v2.getA() * internalRadius / l, v2.getB()
                * internalRadius / l, v2.getC() * internalRadius / l);

        vertices.add(v0);
        vertices.add(v1);
        vertices.add(v2);

        poly = new Triangle(p.getA(), v0, v2);
        result.add(poly);

        poly = new Triangle(v0, p.getB(), v1);
        result.add(poly);

        poly = new Triangle(p.getA(), v0, v2);
        result.add(poly);

        poly = new Triangle(v0, v1, v2);
        result.add(poly);

        // shrink original polygon
        p.setA(v2);
        p.setB(v1);
      }
    }

    // now scale down each vertex to specified radius
    for (Triple<Double, Double, Double> v : vertices) {
      // scale components of v by radius/internalRadius
      v.setA(v.getA() * radius / internalRadius);
      v.setB(v.getB() * radius / internalRadius);
      v.setC(v.getC() * radius / internalRadius);
    }

    return vertices;
  }

  /**
   * The main method to test the created coordinates. Can be removed later.
   * 
   * @param args
   *          the arguments
   */
  @Deprecated
  public static void main(String[] args) {
    final Set<Triple<Double, Double, Double>> coordinates =
        createMinCoordinatesOnSphere(163, 1d);

    JFrame frame = new JFrame();

    JComponent c = new JComponent() {
      /**
       * Serialization ID
       */
      private static final long serialVersionUID = 5100131822191853457L;

      private Set<Triple<Double, Double, Double>> rotCoordinates =
          new HashSet<>(coordinates.size());

      private double alpha = 0;
      {
        new Timer(true).scheduleAtFixedRate(new TimerTask() {

          @Override
          public void run() {
            synchronized (rotCoordinates) {
              alpha += 0.1d / 2d / Math.PI;
              rotCoordinates.clear();

              // rotate coordinates along Y-Axis
              for (Triple<Double, Double, Double> t : coordinates) {
                double x = t.getA();
                double y = t.getB();
                double z = t.getC();

                double z1 = z * Math.cos(alpha) - x * Math.sin(alpha);
                double x1 = z * Math.sin(alpha) + x * Math.cos(alpha);

                rotCoordinates.add(new Triple<>(x1, y, z1));
              }

              SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                  repaint();
                }
              });
            }
          }

        }, 500, 50);
      }

      @Override
      protected void paintComponent(Graphics g) {
        synchronized (rotCoordinates) {
          g.setColor(Color.black);
          g.fillRect(0, 0, getWidth(), getHeight());
          g.setColor(Color.white);
          for (Triple<Double, Double, Double> t : rotCoordinates) {
            int x =
                (int) (t.getA() / (t.getC() + 3) * getWidth()) + getWidth() / 2;
            int y =
                (int) (t.getB() / (t.getC() + 3) * getWidth()) + getHeight()
                    / 2;

            g.fillRect(x, y, (int) (3 - t.getC() * 3), (int) (3 - t.getC() * 3));

            // g.drawLine(x, y, x, y);
          }
        }
      }
    };

    c.setDoubleBuffered(true);
    frame.setLayout(new BorderLayout());
    frame.add(c, BorderLayout.CENTER);

    frame.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
