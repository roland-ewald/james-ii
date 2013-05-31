/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import org.jamesii.core.math.interpolation.ExponentialSplineInterpolator;
import org.jamesii.core.util.misc.Pair;

/**
 * The Class ShowExpontialSplineInterpolation.
 */
public class ShowExpontialSplineInterpolation {

  /**
   * This class plots an exponential spline interpolation with different tension
   * parameters p.
   * 
   * @param args
   *          the args
   * 
   * @author Thomas Flisgen
   */

  public static void main(String[] args) {
    final int N_conti = 300;

    List<Double> xValues = new ArrayList<>();
    List<Double> yValues = new ArrayList<>();

    List<Pair<? extends Number, ? extends Number>> conti1 = new ArrayList<>();

    List<Pair<? extends Number, ? extends Number>> conti2 = new ArrayList<>();

    List<Pair<? extends Number, ? extends Number>> conti3 = new ArrayList<>();

    xValues.add(0.0);
    xValues.add(1.0);
    xValues.add(1.5);
    xValues.add(2.5);
    xValues.add(4.0);
    xValues.add(4.5);
    xValues.add(5.5);
    xValues.add(6.0);
    xValues.add(8.0);
    xValues.add(10.0);

    yValues.add(10.0);
    yValues.add(8.0);
    yValues.add(5.0);
    yValues.add(4.0);
    yValues.add(3.5);
    yValues.add(3.4);
    yValues.add(6.0);
    yValues.add(7.1);
    yValues.add(8.0);
    yValues.add(8.5);

    ExponentialSplineInterpolator expin = new ExponentialSplineInterpolator();

    expin.setXValues(xValues);
    expin.setYValues(yValues);

    double xdiscrete;
    double ydiscrete;

    int N = yValues.size();

    for (int i = 0; i <= N_conti; i++) {
      xdiscrete =
          (xValues.get(N - 1) - xValues.get(0)) * i / N_conti + xValues.get(0);
      ydiscrete = expin.getOrdinateAtPosition(xdiscrete);
      conti1.add(new Pair<>(xdiscrete, ydiscrete));
    }

    expin.setWeight(0.001);
    for (int i = 0; i <= N_conti; i++) {
      xdiscrete =
          (xValues.get(N - 1) - xValues.get(0)) * i / N_conti + xValues.get(0);
      ydiscrete = expin.getOrdinateAtPosition(xdiscrete);
      conti2.add(new Pair<>(xdiscrete, ydiscrete));
    }

    expin.setWeight(100);
    for (int i = 0; i <= N_conti; i++) {
      xdiscrete =
          (xValues.get(N - 1) - xValues.get(0)) * i / N_conti + xValues.get(0);
      ydiscrete = expin.getOrdinateAtPosition(xdiscrete);
      conti3.add(new Pair<>(xdiscrete, ydiscrete));
    }

    JFrame frame = new JFrame();

    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.setSize(1200, 700);

    // BasicChart2D testChart = new BasicChart2D(0.0, 0.0, frame.getSize(),
    // "x-values", "", "y-values", "", TypeOfPresentationObject.POLYLINE, true);
    //
    // frame.getContentPane().add(testChart, BorderLayout.CENTER);
    // //
    // testChart.addVariable("Automatic Tension Control", conti1);
    // testChart.addVariable("p=0.001 (cubic splines)", conti2);
    // testChart.addVariable("p=100 (polygon lines)", conti3);
    // testChart.build();
    // //
    // frame.setVisible(true);
  }

}
