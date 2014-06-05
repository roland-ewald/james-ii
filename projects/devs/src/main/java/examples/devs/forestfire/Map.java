/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;

import java.util.Iterator;
import java.util.List;

import model.devscore.ports.IPort;

/**
 * The Map of the Grid.
 */
public class Map extends model.devs.AtomicModel<MapState> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 5778914138696815065L;

  /** The my count. */
  public int myCount = 0;

  /** The vis. */
  boolean vis = false;

  /** The vis_ascii. */
  boolean vis_ascii = true;

  /**
   * Instantiates a new map.
   * 
   * @param instanceName
   *          the instance name
   * @param height
   *          the height
   * @param width
   *          the width
   * @param vis_ascii
   *          the vis_ascii
   */
  Map(String instanceName, int height, int width, boolean vis_ascii) {
    super(instanceName);

    getState().setPhase("receiving");
    getState().setMapSize(height, width);
    this.vis_ascii = vis_ascii;
    init();
  }

  @Override
  protected MapState createState() {
    return new MapState();
  }

  @Override
  public void deltaExternal(double elapsedTime) {
    myCount++;

    try {
      Thread.sleep(200);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // System.out.println("delta ext of the map");

    Iterator<IPort> inPortIt = getInPortIterator();
    IPort port;
    FireParcel obj;

    while (inPortIt.hasNext()) {
      port = inPortIt.next();
      List<Object> values = port.readAll();
      for (int i = 0; i < values.size(); i++) {

        obj = (FireParcel) values.get(i);

        int y = obj.sender.getSecondValue();
        int x = obj.sender.getFirstValue();

        System.out.println(x + " - " + y + " : " + obj.value);

        getState().setMapPos(x, y, obj.value);

      }
    }

    getState().isChangedRR();

  }

  @Override
  public void deltaInternal() {
    // trace("\n\nThe map at the end:\n"+mapToString());
    getState().setPhase("printed");
    // System.out.println(mapToString());
  }

  @Override
  public void init() {
    addInPort("In", FireParcel.class);

    // initializing the map
    if (vis) {

      // show is depreceated ...!!!!
      // mapvis.show();
    }
  }

  @Override
  public void lambda() {

  }

  @Override
  public double timeAdvance() {
    // if everything keeps quiet for 20.0 ticks we're printing the map
    /*
     * return (getState().getPhase().equals("receiving")) ? 20.0 :
     * Double.POSITIVE_INFINITY;
     */
    return Double.POSITIVE_INFINITY;
  }

}
