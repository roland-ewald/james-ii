/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package examples.devs.forestfire;


import java.util.Collection;
import java.util.Iterator;

import model.devs.CoupledModel;
import model.devscore.BasicAtomicModel;
import model.devscore.BasicDEVSModel;
import model.devscore.IBasicDEVSModel;

import org.jamesii.core.util.misc.Pair;

/*
 * The hole grid. All grid-elements are arranged within this.
 */
/**
 * The Class ForestFire.
 */
public class ForestFire extends CoupledModel {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -4321923149271339649L;

  /** The height. */
  public int height = 10;

  /** The match at. */
  public Pair<Integer, Integer> matchAt;

  @SuppressWarnings("unused")
  private Map map;

  // end model properties

  /** The vis_ascii. */
  public boolean vis_ascii = true;

  // model properties
  /** The width. */
  public int width = 10;

  /**
   * Default constructor.
   */
  public ForestFire() {
    super("ForestFire");
    System.out
        .println("This here is no longer valid ... init cannot be called from within the default constructor!!!");
    this.init();
  }

  /**
   * Constructor for compatibility with model reading.
   * 
   * @param p
   *          the p
   */
  public ForestFire(java.util.Map<String, ?> p) {
    this(getParam(p, "instanceName", "ForestFire"), getParam(p, "height", 10),
        getParam(p, "width", 10), getParam(p, "vis_ascii", true));
  }

  /**
   * The Constructor.
   * 
   * @param instanceName
   *          the instance name
   * @param width
   *          the width
   * @param height
   *          the height
   * @param vis_ascii
   *          the vis_ascii
   */
  public ForestFire(String instanceName, int height, int width,
      boolean vis_ascii) {
    super(instanceName);
    this.width = width;
    this.height = height;
    matchAt = new Pair<>(width / 2, height / 2);
    this.vis_ascii = vis_ascii;
    init();
  }

  /**
   * Initializes the ForestFire model.
   */
  @Override
  public void init() {

    // installing the map
    addModel(map = new Map("Map", height, width, vis_ascii));

    // getModel("Map").registerObserver(portObserver);

    Match m = new Match("Match", matchAt);
    addModel(m);

    int c = 0;

    // generate the grid-elements
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        FireModule g =
            new FireModule(new String(i + "/" + j), new Pair<>(
                j, i));
        addModel(g);

        g.init();

        if ((j == matchAt.getFirstValue()) && (i == matchAt.getSecondValue())) {
          // IPort p = g.addInPort(FireModule.InAgent, FireParcel.class);
          this.addCoupling(m, m.getOutPort(Match.OutPort), g,
              g.getInPort(FireModule.InAgent));
        }

        ++c;
        if (c % 10000 == 0) {
          System.out.println(c);
        }
      }
    }

    // couple the neighboured grid-elements
    c = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        String modelname = new String(i + "/" + j);

        IBasicDEVSModel model = getModel(modelname);

        // couple every element with the map
        addInternalCoupling(model,
            getModelOutPort(modelname, FireModule.OutMap), getModel("Map"),
            getModelInPort("Map", "In"));

        c += 4;
        // couple south-north and north-south
        if (i < (height - 1)) {
          addCoupling(model, getModelOutPort(modelname, FireModule.OutSouth),
              getModel(new String((i + 1) + "/" + j)),
              getModelInPort(new String((i + 1) + "/" + j), FireModule.InNorth));
        }

        if (i > 0) {
          addCoupling(model, getModelOutPort(modelname, FireModule.OutNorth),
              getModel(new String((i - 1) + "/" + j)),
              getModelInPort(new String((i - 1) + "/" + j), FireModule.InSouth));
        }

        // couple east-west and west-east
        if (j < (width - 1)) {
          addCoupling(model, getModelOutPort(modelname, FireModule.OutEast),
              getModel(new String(i + "/" + (j + 1))),
              getModelInPort(new String(i + "/" + (j + 1)), FireModule.InWest));
        }

        if (j > 0) {
          addCoupling(model, getModelOutPort(modelname, FireModule.OutWest),
              getModel(new String(i + "/" + (j - 1))),
              getModelInPort(new String(i + "/" + (j - 1)), FireModule.InEast));
        }

        if (c % 10000 == 0) {
          System.out.println(c);
        }
      }
    }
    System.out.println("Created models and couplings");
  }

  /**
   * Prints the model tree.
   * 
   * @param indent
   *          the indent
   * @param model
   *          the model
   */
  public void printModelTree(String indent, CoupledModel model) {
    Iterator<IBasicDEVSModel> it = model.getSubModelIterator();
    BasicDEVSModel bmodel;

    while (it.hasNext()) {
      bmodel = (BasicDEVSModel) it.next();
      System.out.println(indent + bmodel.getName());
      if (bmodel instanceof BasicAtomicModel) {
      } else {
        printModelTree(indent + "  ", (CoupledModel) bmodel);
      }
    }
  }

  @Override
  public IBasicDEVSModel select(Collection<IBasicDEVSModel> setOfModels) {
    return setOfModels.iterator().next();
  }

} // end of file
