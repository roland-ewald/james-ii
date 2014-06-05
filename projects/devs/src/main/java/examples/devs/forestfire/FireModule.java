/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;


import java.util.Iterator;

import org.jamesii.core.util.misc.Pair;

import model.devscore.ports.IPort;

/**
 * The Class FireModule.
 */
public class FireModule extends model.devs.AtomicModel<FireState> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4452631303474612464L;

  /** The observe. */
  static boolean observe = true;

  /** The ext counter. */
  public static int theExtCounter = 0;

  /** The int counter. */
  public static int theIntCounter = 0;

  /** The lambda counter. */
  public static int theLambdaCounter = 0;

  /** The Constant InAgent. */
  public static final String InAgent = "IA";

  /** The Constant InNorth. */
  public static final String InNorth = "IN";

  /** The Constant InSouth. */
  public static final String InSouth = "IS";

  /** The Constant InEast. */
  public static final String InEast = "IE";

  /** The Constant InWest. */
  public static final String InWest = "IW";

  /** The Constant OutNorth. */
  public static final String OutNorth = "ON";

  /** The Constant OutSouth. */
  public static final String OutSouth = "OS";

  /** The Constant OutEast. */
  public static final String OutEast = "OE";

  /** The Constant OutWest. */
  public static final String OutWest = "OW";

  /** The Constant OutMap. */
  public static final String OutMap = "OM";

  /** The BURNIN g_ time. */
  private double BURNING_TIME = 10.0;

  /** The INFERN o_ time. */
  private double INFERNO_TIME = 15.0;

  /** The prepare to smoulder time. */
  private double PREPARE_TO_SMOULDER_TIME = 0.0;

  /** The SMOULDERIN g_ time. */
  private double SMOULDERING_TIME = 2.0;

  /** The pos. */
  Pair<Integer, Integer> pos;

  FireParcel fpToMap;

  private IPort outMap;

  /**
   * Instantiates a new fire module.
   * 
   * @param instanceName
   *          the instance name
   */
  public FireModule(String instanceName, Pair<Integer, Integer> pos) {
    super(instanceName);
    this.pos = pos;
    fpToMap = new FireParcel(pos, 0);
    init();
  }

  /**
   * Creates the state of this model (a FireState instance).
   * 
   * @return instance of a {@link FireState}
   */
  @Override
  protected FireState createState() {
    return new FireState(FireState.INITIALIZING);
  }

  @Override
  public void deltaExternal(double elapsedTime) {
    theExtCounter++;
    // checking the port allocations
    Iterator<IPort> inPortIt = getInPortIterator();
    IPort port;
    // FireParcel obj;

    // System.out.println("I'm on this host "+getFullName());

    // System.out.print(getFullName()+" -- Number of incoming messages ");

    // let's remove all those models from the list of models we have to spread
    // the fire to from which we already received a message
    while (inPortIt.hasNext()) {
      port = inPortIt.next();
      while (port.hasValue()) {
        // we are not interest in what, but from where ;-)
        port.read();

        // System.out.println(getName()+" .. "+port.getName());

        if (port.getName() == InNorth) {
          getState().spreadFireTo -= FireState.North;
        }
        if (port.getName() == InSouth) {
          getState().spreadFireTo -= FireState.South;
        }
        if (port.getName() == InEast) {
          getState().spreadFireTo -= FireState.East;
        }
        if (port.getName() == InWest) {
          getState().spreadFireTo -= FireState.West;
        }

      }
    }
    // if we had been inactive so far we'll now prepare to smoulder
    if ((getState().getPhase()) == FireState.INACTIVE) {
      getState().setPhase(FireState.PREPARE_TO_SMOULDER);
    }
  }

  /**
   * Each time we get active due to an internal event we simply have to get to
   * the next state phase.
   */
  @Override
  public void deltaInternal() {
    theIntCounter++;
    getState().setPhase(determineNewStatePhase());
  }

  /**
   * Determines the next state phase. State phase order is INACTIVE ->
   * PREPARE_TO_SMOULDER -> SMOULDERING -> BURNING -> INFERNO -> BURNT_OUT ->
   * INACTIVE
   * 
   * @return the int
   */
  private int determineNewStatePhase() {
    // System.out.println(getFullName()+" hast the current status:
    // "+getState().getPhase()+" which will be left now");

    int currentPhase = getState().getPhase();
    // return ++currentPhase;

    if (currentPhase == FireState.PREPARE_TO_SMOULDER) {
      return FireState.SMOULDERING;
    }
    if (currentPhase == FireState.SMOULDERING) {
      return FireState.BURNING;
    }
    if (currentPhase == FireState.BURNING) {
      return FireState.INFERNO;
    }
    if (currentPhase == FireState.INFERNO) {
      return FireState.BURNT_OUT;
    }

    return FireState.INACTIVE;
  }

  @Override
  public void init() {
    getState().setPhase(FireState.INITIALIZING);

    addInPort(InNorth, FireParcel.class);
    addInPort(InSouth, FireParcel.class);
    addInPort(InWest, FireParcel.class);
    addInPort(InEast, FireParcel.class);

    addOutPort(OutNorth, FireParcel.class);
    addOutPort(OutSouth, FireParcel.class);
    addOutPort(OutWest, FireParcel.class);
    addOutPort(OutEast, FireParcel.class);

    outMap = addOutPort(OutMap, FireParcel.class);

    // the Connection to the Agents
    addInPort(InAgent, FireParcel.class);
    // reseting
    getState().spreadFireTo =
        FireState.North + FireState.South + FireState.East + FireState.West;
    // for (int i = 0; i < getState().allDirections.length; i++)
    // getState().spreadFireTo.add(getState().allDirections[i]);
  }

  @Override
  public void lambda() {
    theLambdaCounter++;
    // internal event => state will change => inform map module
    // ensure, that this is the same as in deltaInt
    //
    System.out.println("out " + getState().getPhase());
    //
    // getOutPort(OutMap).write(
    // new FireParcel(pos, determineNewStatePhase()));
    fpToMap.value = determineNewStatePhase();
    outMap.write(fpToMap);
    // new FireParcel(pos, determineNewStatePhase()));

    // if necessary, spread the fire
    if (getState().getPhase() == FireState.SMOULDERING) {

      final int to = getState().spreadFireTo;

      final FireParcel startBurning = new FireParcel(pos, FireState.BURN);

      if ((to & FireState.North) == FireState.North) {
        getOutPort(OutNorth).write(startBurning);
      }
      if ((to & FireState.South) == FireState.South) {
        getOutPort(OutSouth).write(startBurning);
      }
      if ((to & FireState.East) == FireState.East) {
        getOutPort(OutEast).write(startBurning);
      }
      if ((to & FireState.West) == FireState.West) {
        getOutPort(OutWest).write(startBurning);
      }

      // System.out.println (getFullName()+" writes on "+"Out" +
      // (String)spreadFireTo.elementAt(i));

    }
    // reseting the spreading directions
    // getState().spreadFireTo = new ArrayList<String>();
    // for (int i = 0; i < getState().allDirections.length; i++) {
    // getState().spreadFireTo.add(getState().allDirections[i]);
    // }
    // }// end of if
  }

  /**
   * Return the time the next internal event shall occur according to the state
   * phase. During initialization we want to get active at once preparation to
   * smoulder: start smouldering after PREPARE_TO_SMOULDER_TIME has elapsed
   * start burning after SMOULDERING_TIME has been elapsed the inferno starts
   * after the model has been burning for BURNING_TIME the inferno lasts for
   * INFERNO_TIME
   * 
   * @return the double
   */
  @Override
  public double timeAdvance( /* State state */) {
    // System.out.println ("("+getFullName()+")"+ getState().getPhase());

    int currentPhase = getState().getPhase();

    if (currentPhase == FireState.INITIALIZING) {
      return 0;
    }
    if (currentPhase == FireState.PREPARE_TO_SMOULDER) {
      return PREPARE_TO_SMOULDER_TIME;
    }
    if (currentPhase == FireState.SMOULDERING) {
      return SMOULDERING_TIME;
    }
    if (currentPhase == FireState.BURNING) {
      return BURNING_TIME;
    }
    if (currentPhase == FireState.INFERNO) {
      return INFERNO_TIME;
    }
    return Double.POSITIVE_INFINITY;
  }
}
