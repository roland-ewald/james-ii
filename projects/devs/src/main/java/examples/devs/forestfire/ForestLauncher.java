/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;

import model.devscore.IBasicDEVSModel;

import java.util.Iterator;

import org.jamesii.SimSystem;
import org.jamesii.core.simulation.launch.DirectLauncher;

import examples.devs.forestfire.observe.FFInstrumenter;

/**
 * This class wraps model creation and model execution. If it is executed it
 * will automatically connect to a simulation server and create a simulation on
 * this server which it executes afterwards. The model execution can be
 * parameterized by a set of std, non model specific parameters.
 * 
 * @author Jan Himmelspach
 */
public class ForestLauncher extends DirectLauncher {

  /**
   * Create and launch the simulation
   * 
   * @param args
   *          command line parameters
   */
  @SuppressWarnings({ "static-access" })
  public static void main(String args[]) {
    // default argument parsing
    ForestLauncher launcher = new ForestLauncher();

    // std parameter parsing, any non std parameters have to be parsed before
    // a call to parseArgs (and have to be thereby removed from or set to an
    // empty string in the list of args)
    launcher.stdParseArgs(args);

    // create an instance of the model
    ForestFire forestFire = null;

    try {
      forestFire =
          new ForestFire("FOREST FIRE", launcher.width, launcher.height,
              launcher.vis_ascii);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // example: how to write a model structure to a file by using the filer
    // Filer filer = new XMLFiler("model.xml");
    // filer.write(forestFire);

    // create the simulation
    System.out.println("creating simulation");
    launcher.createSimulation(forestFire);

    new FFInstrumenter().instrumentModel(forestFire, null);

    System.out.println("running the simulation");
    // run the model
    launcher.executeModel();

    Iterator<IBasicDEVSModel> it = forestFire.getSubModelIterator();
    IBasicDEVSModel m = null;

    // while (!(m instanceof GridElement))
    // m = it.next();
    //
    // it = ((CoupledModel) m).getSubModelIterator();
    //
    // m = null;

    while (!(m instanceof FireModule)) {
      m = it.next();
    }

    FireModule f = (FireModule) m;

    // note: ext + int must be equal to width*height*6

    System.out.println("ext: " + f.theExtCounter + " --- int: "
        + f.theIntCounter + " --- lambda: " + f.theLambdaCounter);

    it = forestFire.getSubModelIterator();
    m = null;

    while (!(m instanceof Map)) {
      m = it.next();
    }

    // note: that's the number of different times used in the sim / simulation
    // pulses

    System.out.println("Map, ext: " + ((Map) m).myCount);

    SimSystem.shutDown(0);
  }

  int height = 10;

  boolean vis_ascii = true;

  int width = 10;

  /** Creates a new instance of ForestLauncher */
  public ForestLauncher() {
    super();
  }

  /**
   * Print the default arguments plus the model specific ones!
   */
  @Override
  public String extArgsToString() {
    return "\nForest Fire specific parameters:\n"
        + "-width=        [4] width of the grid (integer)\n"
        + "-height=       [4] height of the grid (integer)\n"
        + "-vis               print map (ascii)\n";
  }

  /**
   * Checks wether the given parameter is known, if known the parameter is
   * interpreted and the function returns true, otherwise it will return the
   * inherited function's implementation return code
   * 
   * @return true if the parameter was handled, false otherwise
   */
  @Override
  public boolean handleParameter(String param, String value) {
    if (param.compareTo("width") == 0) {
      width = Integer.valueOf(value).intValue();
      // width parameter found => set argument to an emtpy string
      return true;
    }
    if (param.compareTo("height") == 0) {
      height = Integer.valueOf(value).intValue();
      // height parameter found => set argument to an emtpy string
      return true;
    }
    if (param.compareTo("vis") == 0) {
      vis_ascii = true;
      // height parameter found => set argument to an emtpy string
      return true;
    }
    return super.handleParameter(param, value);
  }

}
