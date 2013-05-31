/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset;

import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.eventset.IBasicEventQueue;
import org.jamesii.core.util.eventset.TwoListWithBucketsSimpleThreshold;

// TODO: Auto-generated Javadoc
/**
 * The Class EventSetTest.
 */
public class EventSetTest {

  /**
   * Gets the obj.
   * 
   * @return the obj
   */
  public static Object getObj() {
    return new Object();
  }

  /**
   * The main method.
   * 
   * @param args
   *          the args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    IBasicEventQueue<Object, Double> q;// = new CalendarQueue<Object>();

    q = new TwoListWithBucketsSimpleThreshold<>();

    /*
     * q.enqueue(new Object(), 2.0); q.enqueue(new Object(), 1.0); q.enqueue(new
     * Object(), 2.2); q.enqueue(new Object(), 1.3); q.enqueue(new Object(),
     * 1.3); q.enqueue(new Object(), 3.3); q.enqueue(new Object(), 5.3);
     * q.enqueue(new Object(), 8.3); q.enqueue(new Object(), 22.3);
     */

    int distrib = 3;

    StopWatch sw = new StopWatch();

    sw.start();

    for (int i = 0; i < 100000; i++) {

      double rand = Math.random();

      switch (distrib) {

      case 0:
        // Exponential
        q.enqueue(getObj(), -Math.log(rand));
        break;

      case 1:
        // Uniform 0.0 - 2.0
        q.enqueue(getObj(), 2 * rand);
        break;

      case 2:
        // Biased 0.9 - 1.1
        q.enqueue(getObj(), 0.9 + 0.2 * rand);
        break;

      case 3: {
        // Biomodal

        if (rand < 0.1) {
          q.enqueue(getObj(), 0.95238 + 9.5238 * rand);
        } else {
          q.enqueue(getObj(), 0.95238 + rand);
        }
        break;
      }

      case 4:
        // Triangular
        q.enqueue(getObj(), 1.5 * Math.pow(rand, 0.5));
      }
    }

    sw.stop();
    System.out.println("Enqueing took " + sw.elapsedSeconds() + " seconds ");

    /*
     * System.out.println("The event queue (intially filled)");
     * q.print(System.out);
     */

    // sw.reset();
    // sw.start();

    // int i = 0;
    //
    // while (!q.isEmpty()) {
    //
    // i++;
    // System.out.print("Get least "+i+": ");
    // Entry<Object> e = q.dequeue();
    // System.out.println(e.getTime()+" --- "+e.getEvent());

    /*
     * System.out.println("The event queue (after get least)");
     * q.print(System.out);
     */
    // }

    /*
     * System.out.println("The event queue (after get least)");
     * q.print(System.out);
     */

    // sw.stop();
    // System.out.println("Dequeing took " + sw.elapsedSeconds() + " seconds ");
  }

  /**
   * Instantiates a new event set test.
   */
  public EventSetTest() {
    super();
    // TODO Auto-generated constructor stub
  }

}
