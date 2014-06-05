/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore;


import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.util.collection.list.ISelector;
import org.jamesii.core.util.misc.Strings;

import model.devscore.couplings.BasicCoupling;
import model.devscore.couplings.ClassCoupling;
import model.devscore.couplings.Coupling;
import model.devscore.couplings.ICouplingSet;
import model.devscore.couplings.InvalidCouplingException;
import model.devscore.couplings.MultiCoupling;
import model.devscore.couplings.MultiCouplingTarget;
import model.devscore.couplings.MultiCouplingTargetList;
import model.devscore.couplings.plugintype.AbstractCouplingSetFactory;
import model.devscore.models.IModelSet;
import model.devscore.models.plugintype.AbstractModelSetFactory;
import model.devscore.ports.IPort;
import model.devscore.ports.InvalidPortException;

/**
 * Base class for a DEVS coupled model. A concrete CoupledModel must inherit
 * from this class (or one of its descendants). The ports are inherited from the
 * BasicDEVSModel ancestor class. In addition to the ports a CoupledModel
 * supports so called couplings - routing information for port to port
 * communication. On creating a concrete coupled model, which consists out of
 * atomic or coupled models, ports and couplings, these have to be added by
 * using. the corresponding methods. A CoupledModel has no internal state nor
 * any transition function. It is - more or less - nothing more than a cover
 * around a group of child models. Because of the closure under coupling coupled
 * models can be considered to be equivalent to an atomic model. The child
 * models are stored in the subModels
 * 
 * @author Jan Himmelspach
 * @author Christian Ober
 * 
 *         history 27.03.2004 Christian Ober added addCoupling() for
 *         MultiCouplings, changed checkForModel(CouplingSet couplings,
 *         IBasicDEVSModel model)
 * @see model.devscore.models.IModelSet
 * @see model.devscore.couplings.ICouplingSet
 */
public abstract class BasicCoupledModel extends BasicDEVSModel implements
    IBasicCoupledModel {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1821412661318246525L;

  /**
   * Set of external in couplings (from this model, port d to sub model A, port
   * a).
   */
  private ICouplingSet externalInCouplings = null;

  /**
   * Set of external out couplings (from sub model A, port a to this model port
   * c).
   */
  private ICouplingSet externalOutCouplings = null;

  /**
   * Set of internal couplings (from sub model A, port a to sub model B port b).
   */
  private ICouplingSet internalCouplings = null;

  /** A set of sub models (or child models). */
  private IModelSet subModels = null;

  /**
   * Creates a new instance of a coupled model. This model is not named and a
   * name must be set afterwards (e.g. in an init method, BEFORE children are
   * added).
   */
  public BasicCoupledModel() {
    super();
    createSubStructures();
  }

  /**
   * Creates a new instance of a coupled model. This instance is named by the
   * given parameter name.
   * 
   * @param name
   *          the name
   */
  public BasicCoupledModel(String name) {
    super(name);
    createSubStructures();
  }

  /**
   * Adds a coupling between one of the coupled model's in ports or between the
   * outport of one sub model and the inport of any other sub model. This method
   * auto detected the correct coupling set this coupling must be placed into.
   * However it is not possible to create a coupling with an external output
   * port of this coupled model.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * @param selector
   *          the selector
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2,
      ISelector<IBasicDEVSModel> selector) {
    ICouplingSet couplings;
    if (model1 == this) {
      couplings = externalInCouplings;
    } else {
      couplings = internalCouplings;
    }
    // call the private addCoupling method with the appropriate couplings set
    addCoupling(couplings, model1, port1, model2, port2, selector);
  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // +++++++++++++++++ sub model handling

  /**
   * This addCoupling method adds a coupling between model1, port1 and model2,
   * port2, thereby auto detecting the type of coupling (external in, external
   * out, internal).
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {
    ICouplingSet couplings;
    // if model1 is this model (self) then its an external in coupling
    if (model1 == this) {
      couplings = externalInCouplings;
    } else {
      // if model2 is this model (self) then its an external out coupling
      if (model2 == this) {
        couplings = externalOutCouplings;
      } else {
        // if none of the above is true it must be an internal coupling
        couplings = internalCouplings;
      }
    }
    addCoupling(couplings, model1, port1, model2, port2);
  }

  /**
   * Adds a coupling having one of the coupled model's in ports as its source,
   * or between the outport of one sub model and the inport of any other sub
   * model. This method auto detected the correct coupling set this coupling
   * must be placed into. However it is not possible to create a coupling with
   * an external output port of this coupled model.
   * 
   * @param model1
   *          the source model
   * @param port1
   *          the source port
   * @param model2
   *          the targetlist of models and ports
   * @param name
   *          the identifier of the multicoupling to create
   * @param selector
   *          the associated selector
   * 
   * @throws InvalidCouplingException
   *           if anything is wrong
   */
  public void addCoupling(IBasicDEVSModel model1, IPort port1,
      MultiCouplingTargetList model2, String name,
      ISelector<IBasicDEVSModel> selector) {
    ICouplingSet couplings;
    if (model1 == this) {
      couplings = externalInCouplings;
    } else {
      couplings = internalCouplings;
    }
    // call the private addCoupling method with the approbiate couplings set
    addCoupling(couplings, model1, port1, model2, name, selector);
  }

  /**
   * Adds a coupling between the given models and ports, thereby autodetecting
   * the type of the coupling (external in, external out or internal). The ports
   * are specified by their unique names.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addCoupling(IBasicDEVSModel model1, String port1,
      IBasicDEVSModel model2, String port2) {
    // if model1 == this -> external input coupling

    if (model1 == this) {
      addCoupling(model1, getInPort(port1), model2, model2.getInPort(port2));
    } else {
      // if model2 == this -> external output coupling
      if (model2 == this) {
        addCoupling(model1, model1.getOutPort(port1), model2, getOutPort(port2));
      } else {
        // internal coupling
        addCoupling(model1, model1.getOutPort(port1), model2,
            model2.getInPort(port2));
      }
    }

  }

  /**
   * This method creates a coupling between the given models and ports and
   * inserts this coupling into the given couplings set - no check is done
   * whether the coupling is added to the correct list!!!.
   * 
   * @param couplings
   *          The list of Couplings, where the new Coupling is to be added to.
   * @param model1
   *          The source model.
   * @param port1
   *          The source port.
   * @param model2
   *          The class type for the ClassCoupling to be added.
   * @param port2
   *          The target port.
   * @param selector
   *          the selector
   */
  private void addCoupling(ICouplingSet couplings, IBasicDEVSModel model1,
      IPort port1, Class<IBasicDEVSModel> model2, String port2,
      ISelector<IBasicDEVSModel> selector) {

    ClassCoupling coupling =
        new ClassCoupling(model1, port1, model2, port2, selector);
    // only allow addition if models and ports are existent!
    if ((!subModels.contains(model1) && (this != model1))
        || !model1.hasPort(port1)) {
      throw new InvalidCouplingException(
          "Add coupling: Not existing model or port! " + model1.getName() + ":"
              + port1.getName());
    }
    couplings.addCoupling(coupling);
    changed();

  }

  /**
   * This method creates a coupling between the given models and ports and
   * inserts this coupling into the given couplings set - no check is done
   * whether the coupling is added to the correct list!!!.
   * 
   * @param couplings
   *          the couplings
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   */
  private void addCoupling(ICouplingSet couplings, IBasicDEVSModel model1,
      IPort port1, IBasicDEVSModel model2, IPort port2) {

    Coupling coupling = new Coupling(model1, port1, model2, port2);

    // only allow addition if models and ports are existent!
    if ((!subModels.contains(model1) && (this != model1))
        || (!subModels.contains(model2) && (this != model2))
        || !model1.hasPort(port1) || !model2.hasPort(port2)) {
      String notExist = "";
      if (!subModels.contains(model1) && (this != model1)) {
        notExist = " model " + model1.getName();
      }
      if (!subModels.contains(model2) && (this != model2)) {
        notExist = notExist + " model " + model2.getName();
      }
      if (!model1.hasPort(port1)) {
        notExist =
            notExist + " port " + model1.getName() + ":" + port1.getName();
      }
      if (!model2.hasPort(port2)) {
        notExist =
            notExist + " port " + model2.getName() + ":" + port2.getName();
      }
      throw new InvalidCouplingException(
          "Add coupling: Not existing model or port in " + this.getFullName()
              + "!\n Coupling (" + model1.getName() + ":" + port1.getName()
              + " [In: " + model1.hasInPort(port1) + " Out: "
              + model1.hasOutPort(port1) + "]" + " - " + model2.getName() + ":"
              + port2.getName() + " [In: " + model2.hasInPort(port2) + " Out: "
              + model2.hasOutPort(port2) + "])\n"
              + " This/these element(s) do not exist: [" + notExist + "]");
    }
    if (model1 == model2) {
      throw new InvalidCouplingException(
          "A model cannot be connected to itself!\n(" + model1.getName() + ":"
              + port1.getName() + " - " + model2.getName() + ":"
              + port2.getName() + ")");
    }
    couplings.addCoupling(coupling);
    changed();

  }

  /**
   * This method creates a coupling between the given models and ports and
   * inserts this coupling into the given couplings set - no check is done
   * whether the coupling is added to the correct list!!!.
   * 
   * @param couplings
   *          The list of Couplings, where the new Coupling is to be added to.
   * @param model1
   *          The source model.
   * @param port1
   *          The source port.
   * @param model2
   *          The class type for the ClassCoupling to be added.
   * @param selector
   *          the selector
   * @param name
   *          the name
   */
  private void addCoupling(ICouplingSet couplings, IBasicDEVSModel model1,
      IPort port1, MultiCouplingTargetList model2, String name,
      ISelector<IBasicDEVSModel> selector) {

    if (port1 == null) {
      throw new InvalidCouplingException(
          "Cannot use null as starting port of a coupling (" + name
              + ") starting model " + model1.getName() + "");
    }
    MultiCoupling coupling =
        new MultiCoupling(model1, port1, model2, name, selector);
    // only allow addition if models and ports are existent!
    if ((!subModels.contains(model1) && (this != model1))
        || !model1.hasPort(port1)) {
      throw new InvalidCouplingException(
          "Add coupling: Not existing model or port! " + model1.getName() + ":"
              + port1.getName());
    }
    couplings.addCoupling(coupling);
    changed();

  }

  /**
   * Add a coupling between the given models and ports. Thereby the model as
   * well as the port names must be unique - but not only the submodels names
   * must be unique - the name of this model must be unique in regards to the
   * sub models, too!!
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addCoupling(String model1, String port1, String model2,
      String port2) {
    // if model1 == this -> external input coupling
    if (model1.equals(getName())) {
      addCoupling(this, port1, getModel(model2), port2);
    } else {
      // if model2 == this -> external output coupling
      if (model2.equals(getName())) {
        addCoupling(getModel(model1), port1, this, port2);
      } else {
        // internal coupling
        addCoupling(getModel(model1), port1, getModel(model2), port2);
      }
    }

  }

  // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
  // +++++++++++++++++ couplings

  /**
   * Directly adds a new coupling between the specified nodes one and two to the
   * set if external in couplings. If any invalid node is given the method will
   * throw an InvalidCouplingException.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addExternalInCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {
    addCoupling(externalInCouplings, model1, port1, model2, port2);
  }

  /**
   * Directly adds a new coupling between the specified nodes one and two to the
   * set if external out couplings. If any invalid node is given the method will
   * throw an InvalidCouplingException.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addExternalOutCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {
    addCoupling(externalOutCouplings, model1, port1, model2, port2);
  }

  // /////////////////////////////////////////////////////////////////////////////////////////
  // start: MultiCouplings
  // /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Directly adds a new coupling between the specified nodes one and two to the
   * set if internal couplings. If any invalid node is given the method will
   * throw an InvalidCouplingException.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   * 
   * @throws InvalidCouplingException
   *           the invalid coupling exception
   */
  public void addInternalCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {
    addCoupling(internalCouplings, model1, port1, model2, port2);
  }

  // /////////////////////////////////////////////////////////////////////////////////////////
  // end: MultiCouplings
  // /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * This method adds a new model instance of the given modelClass type to the
   * list of submodels naming it with the given name. This method calls the
   * default constructor of a model, i.e. the one without parameters. So the
   * initialization of the model must be done either in the default constructor
   * or manually after the creation. This models returns a pointer to the newly
   * created model.
   * 
   * @param modelClass
   *          the model class
   * @param name
   *          the name
   * 
   * @return the i basic devs model
   */
  public IBasicDEVSModel addModel(Class<? extends BasicDEVSModel> modelClass,
      String name) {
    try {
      BasicDEVSModel model = modelClass.newInstance();
      model.setName(name);
      addModel(model);
      return model;
    } catch (Exception e) {
      SimSystem.report(e);
      throw new InvalidModelException("Adding model " + name
          + " of type class " + modelClass.getName() + " failed!!", e);
    }
  }

  /**
   * Adds the given model to the list of submodels of this coupled model. This
   * model should not be added to another coupled model at the same time -
   * however this is not checked!
   * 
   * @param model
   *          the model
   */
  public void addModel(IBasicDEVSModel model) {

    model.setParent(this);
    subModels.addModel(model);

  }

  /**
   * adds single new target to the list of multicouplings of the by ident
   * specified coupling.
   * 
   * @param ident
   *          the ident
   * @param target
   *          the target
   */
  public void addToCoupling(String ident, MultiCouplingTarget target) {
    MultiCouplingTargetList mctl = new MultiCouplingTargetList();
    mctl.addTarget(target);
    addToCoupling(ident, mctl);
  }

  /**
   * Add the targets specified in the targets parameter to the coupling
   * specified by the ident parameter.
   * 
   * @param ident
   *          of the coupling
   * @param targets
   *          the targets to be added
   */
  public void addToCoupling(String ident, MultiCouplingTargetList targets) {
    MultiCoupling mc = (MultiCoupling) getCoupling(ident);

    if (mc != null) {
      mc.addTargetList(targets);
    } else {
      throw new InvalidModelException(
          "BasicCoupledModel, addToCoupling: unknown coupling " + ident);
      // System.out.println();
    }
  }

  /**
   * Checks whether there is a coupling in given set of couplings which contains
   * the given model (true will be returned). Note: Only Couplings are
   * checked!!! (no other types of couplings)
   * 
   * @param couplings
   *          the couplings
   * @param model
   *          the model
   * 
   * @return true, if check for model
   */
  public boolean checkForModel(ICouplingSet couplings, IBasicDEVSModel model) {
    Iterator<BasicCoupling> itc = couplings.iterator();

    while (itc.hasNext()) {
      Object c = itc.next();
      if (c instanceof Coupling) {
        if (((Coupling) c).isModel1(model) || ((Coupling) c).isModel2(model)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Couplings to string.
   * 
   * @return the string
   */
  public String couplingsToString() {
    return "Internal couplings: \n" + internalCouplings.toString() + "\n"
        + "External in couplings: \n" + externalInCouplings.toString() + "\n"
        + "External out couplings: \n" + externalOutCouplings.toString();
  }

  /**
   * create the coupling and model set data structures.
   */
  private void createSubStructures() {
    subModels =
        SimSystem.getRegistry().getFactory(AbstractModelSetFactory.class, null)
            .createDirect();

    internalCouplings =
        SimSystem.getRegistry()
            .getFactory(AbstractCouplingSetFactory.class, null).createDirect();

    externalOutCouplings =
        SimSystem.getRegistry()
            .getFactory(AbstractCouplingSetFactory.class, null).createDirect();

    externalInCouplings =
        SimSystem.getRegistry()
            .getFactory(AbstractCouplingSetFactory.class, null).createDirect();
  }

  /**
   * Get all information encapsulated into this object.
   * 
   * @return the complete info string
   */
  @Override
  public String getCompleteInfoString() {

    StringBuilder res = new StringBuilder();

    res.append(super.getCompleteInfoString());
    
    res.append("\t [COUPLINGS ");
    
    addCouplingsToInfoString (res, "internal", getICIterator());    
    
    addCouplingsToInfoString (res, "extin", getEICIterator());    
    
    addCouplingsToInfoString (res, "extout", getEOCIterator());
        
    res.append("]");

    Iterator<IBasicDEVSModel> itM = getSubModelIterator();

    while (itM.hasNext()) {
      IBasicDEVSModel m = itM.next();
      res.append("\n");
      res.append(Strings.indent(m.getCompleteInfoString(), " "));
    }

    return res.toString();
  }

  /**
   * Add the couplings as list.
   * @param res
   * @param it
   */
  private void addCouplingsToInfoString(StringBuilder res, String ident,
      Iterator<BasicCoupling> it) {    
    
    if (it.hasNext()) {
      res.append("\t"+ident+":\t");
    }
    
    while (it.hasNext()) {
      res.append(it.next());
      res.append(";");
    }
  }

  /**
   * Gets the coupling.
   * 
   * @param ident
   *          the ident
   * 
   * @return the coupling
   */
  public BasicCoupling getCoupling(String ident) {
    if (internalCouplings.contains(ident)) {
      return internalCouplings.getCoupling(ident);
    }
    if (externalInCouplings.contains(ident)) {
      return externalInCouplings.getCoupling(ident);
    }
    if (externalOutCouplings.contains(ident)) {
      return externalOutCouplings.getCoupling(ident);
    }
    return null;
  }

  /**
   * Returns an iterator for iterating through the list of external in
   * couplings. The return value is not a private iterator so there should be
   * only one process using this iterator per time. A subsequent call will just
   * reset the existing iterator!!
   * 
   * @return the EIC iterator
   */
  @Override
  public Iterator<BasicCoupling> getEICIterator() {
    return externalInCouplings.iterator();
  }

  @Override
  public Iterator<BasicCoupling> getEICIterator(IBasicDEVSModel model) {
    return externalInCouplings.getCouplingsIterator(model);
  }

  /**
   * Returns an iterator for iterating through the list of external out
   * couplings. The return value is not a private iterator so there should be
   * only one process using this iterator per time. A subsequent call will just
   * reset the existing iterator!!
   * 
   * @return the EOC iterator
   */
  @Override
  public Iterator<BasicCoupling> getEOCIterator() {
    return externalOutCouplings.iterator();
  }

  /**
   * Returns an iterator for iterating through the list of external out
   * couplings of the given model. The return value is not a private iterator so
   * there should be only one process using this iterator per time. A subsequent
   * call will just reset the existing iterator!!
   * 
   * @param model
   *          the model
   * 
   * @return the EOC iterator
   */
  @Override
  public Iterator<BasicCoupling> getEOCIterator(IBasicDEVSModel model) {
    return externalOutCouplings.getCouplingsIterator(model);
  }

  /**
   * Returns an iterator for iterating through the list of internal couplings.
   * The return value is not a private iterator so there should be only one
   * process using this iterator per time. A subsequent call will just reset the
   * existing iterator!!
   * 
   * @return the IC iterator
   */
  @Override
  public Iterator<BasicCoupling> getICIterator() {
    return internalCouplings.iterator();
  }

  /**
   * Returns an iterator for iterating through the list of internal couplings of
   * the given model. The return value is not a private iterator so there should
   * be only one process using this iterator per time. A subsequent call will
   * just reset the existing iterator!!
   * 
   * @param model
   *          the model
   * 
   * @return the IC iterator
   */
  @Override
  public Iterator<BasicCoupling> getICIterator(IBasicDEVSModel model) {
    return internalCouplings.getCouplingsIterator(model);
  }

  /**
   * The getModel function returns a reference to the model which name is equal
   * to the given name. So this name must be the name of an existing submodel of
   * this coupled model.
   * 
   * @param name
   *          (a model's short name)
   * 
   * @return a reference to the model with the given name or null if it does no
   *         exist
   */
  @Override
  public IBasicDEVSModel getModel(String name) {
    return subModels.getModel(name);
  }

  /**
   * Returns a reference to the instance of the in port with the given port name
   * in the given model and throws an InvalidPortException if the port does not
   * exist.
   * 
   * @param model
   *          the model
   * @param port
   *          the port
   * 
   * @return the model in port
   */
  public IPort getModelInPort(String model, String port) {

    IPort iport = getModel(model).getInPort(port);
    if (iport == null) {
      throw new InvalidPortException("A port with the given name " + port
          + " does not exist in " + model + "!");
    }
    return iport;

  }

  /**
   * Returns a reference to the instance of the out port with the given port
   * name in the given model and throws an InvalidPortException if the port does
   * not exist.
   * 
   * @param model
   *          the model
   * @param port
   *          the port
   * 
   * @return the model out port
   */
  public IPort getModelOutPort(String model, String port) {

    IPort iport = getModel(model).getOutPort(port);
    if (iport == null) {
      throw new InvalidPortException("A port with the given name " + port
          + " does not exist in " + model + "!");
    }
    return iport;

  }

  /**
   * Returns the number of sub models.
   * 
   * @return the sub model count
   */
  @Override
  public int getSubModelCount() {
    return subModels.size();
  }

  @Override
  public IBasicDEVSModel getSubModel(int index) {
    return subModels.get(index);
  }

  /**
   * Returns an iterator for the set of sub models of this coupled model, a sub
   * model can either be an AtomicModel or another CoupledModel. This is a
   * non-private iterator - i.e. you cannot fetch a second iterator (if you do
   * the existing iterator will just be restarted!).
   * 
   * @return an iterator for iterating over all sub models
   */
  @Override
  public Iterator<IBasicDEVSModel> getSubModelIterator() {
    return subModels.iterator();
  }

  /**
   * Gets the submodels by class.
   * 
   * @param classOfModelsToGet
   *          the class of models to get
   * 
   * @return the submodels by class
   */
  @Override
  public List<IBasicDEVSModel> getSubmodelsByClass(Class<?> classOfModelsToGet) {
    return this.subModels.getModels(classOfModelsToGet);
  }

  /**
   * The removeCompleteModel method removes the given model from the set of
   * submodels and all couplings this model is the source of the only target of.
   * 
   * @param model
   *          the model to be removed
   */
  public void removeCompleteModel(IBasicDEVSModel model) {
    // remove all couplings containing this model
    removeCouplings(internalCouplings, model);
    removeCouplings(externalOutCouplings, model);
    removeCouplings(externalInCouplings, model);
    // remove the model
    subModels.removeModel(model);
  }

  /**
   * Removes the coupling.
   * 
   * @param c
   *          the c
   */
  private void removeCoupling(BasicCoupling c) {
    if (internalCouplings.contains(c)) {
      removeCoupling(internalCouplings, c);
    }
    if (externalOutCouplings.contains(c)) {
      removeCoupling(externalOutCouplings, c);
    }
    if (externalInCouplings.contains(c)) {
      removeCoupling(externalInCouplings, c);
    }
  }

  /**
   * Remove the given class coupling from the model.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   */
  public void removeCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2) {
    ClassCoupling c = new ClassCoupling(model1, port1, model2, port2, null);
    removeCoupling(c);
  }

  /**
   * Removes a coupling between the given node one (model1, port1) and node two
   * (model2, port2). Thereby this method autodetects the set of couplings this
   * coupling must be removed from. With this coupling only couplings of type
   * Coupling can be removed - ClassCouplings or MultiCouplings must be removed
   * by using other methods.
   * 
   * @param model1
   *          the model1
   * @param port1
   *          the port1
   * @param model2
   *          the model2
   * @param port2
   *          the port2
   */
  public void removeCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {
    ICouplingSet couplingSet;
    if (model1 == this) {
      couplingSet = externalInCouplings;
    } else {
      if (model2 == this) {
        couplingSet = externalOutCouplings;
      } else {
        couplingSet = internalCouplings;
      }
    }
    couplingSet.removeCoupling(model1, port1, model2, port2);
  }

  /**
   * Remove the given coupling from the specified set.
   * 
   * @param couplings
   *          the couplings
   * @param c
   *          Coupling to be removed*
   */
  private void removeCoupling(ICouplingSet couplings, BasicCoupling c) {
    try {
      couplings.removeCoupling(c);
    } catch (NoSuchElementException nsee) {
      throw new InvalidModelException("The coupling " + c
          + " cannot be removed from the model " + getFullName()
          + " as it is not existent.", nsee);
    }
  }

  /**
   * Removes the coupling specified by the ident. The ident must be unique over
   * all coupling lists!! Otherwise the first coupling found (with that name)
   * will be removed!.
   * 
   * @param ident
   *          the identification of the coupling
   */
  public void removeCoupling(String ident) {
    int removedCouplings =
        countRemovedCouplings(ident, internalCouplings, externalInCouplings,
            externalOutCouplings);

    if ((removedCouplings == 0) || (removedCouplings > 1)) {
      if (removedCouplings == 0) {
        throw new ModelChangeException("The coupling " + ident
            + " was not found, thus it was not possible to remove it ...");
      }
      throw new ModelChangeException("The coupling " + ident
          + " was removed from more than one coupling list!!");
    }
  }

  /**
   * Counts the number of removed couplings.
   * 
   * @param ident
   *          the identification of the coupling
   * @param couplingSets
   *          the coupling sets from which it might be removable
   * @return the number of {@link ICouplingSet} instances from which it could be
   *         removed
   */
  private int countRemovedCouplings(String ident, ICouplingSet... couplingSets) {
    int result = 0;
    for (ICouplingSet couplingSet : couplingSets) {
      result += removeCouplingFromCouplingSet(ident, couplingSet) ? 1 : 0;
    }
    return result;
  }

  /**
   * Removes coupling specified by the ident from the given {@link ICouplingSet}
   * .
   * 
   * @param ident
   *          the identification of the coupling
   * 
   * @param couplingSet
   *          the coupling set from which the coupling shall be removed
   * @return true, if coupling could be removed
   */
  private boolean removeCouplingFromCouplingSet(String ident,
      ICouplingSet couplingSet) {
    if (couplingSet.contains(ident)) {
      couplingSet.removeCoupling(couplingSet.getCoupling(ident));
      return true;
    }
    return false;
  }

  /**
   * This method will remove all couplings in which the given model (@param
   * model) is either the source or the drain.
   * 
   * @param couplings
   *          the couplings
   * @param model
   *          the model
   */
  public void removeCouplings(ICouplingSet couplings, IBasicDEVSModel model) {
    couplings.removeCouplings(model);
  }

  /**
   * Removes the from coupling.
   * 
   * @param ident
   *          the ident
   * @param target
   *          the target
   */
  public void removeFromCoupling(String ident, MultiCouplingTarget target) {
    MultiCouplingTargetList mctl = new MultiCouplingTargetList();
    mctl.addTarget(target);
    removeFromCoupling(ident, mctl);
  }

  /**
   * Remove the targets specified in the targets parameter from the coupling
   * specified by the ident parameter.
   * 
   * @param ident
   *          of the coupling
   * @param targets
   *          the targets to be removed
   */
  public void removeFromCoupling(String ident, MultiCouplingTargetList targets) {

    MultiCoupling mc = (MultiCoupling) getCoupling(ident);

    if (mc == null) {
      SimSystem.report(Level.WARNING,
          "BasicCoupledModel, removeFromCoupling: unknown coupling " + ident);
    } else {
      mc.removeTargetList(targets);
    }

  }

  /**
   * This method removes a model if it has no more dependecies, i.e. none of the
   * remaining coupling definitions contains a reference to it.
   * 
   * If there are still dependencies a ModelChangeException will be thrown.
   * 
   * @param model
   *          the model to be removed
   */
  public void removeModel(IBasicDEVSModel model) {
    // check for couplings
    if (checkForModel(internalCouplings, model)
        || checkForModel(externalOutCouplings, model)
        || checkForModel(externalInCouplings, model)) {
      throw new ModelChangeException("Model cannot be removed! "
          + "There is at least one coupling which " + "contains this model!!");
    }
    subModels.removeModel(model);
  }

  /**
   * The behaviour of this model is equivalent to the removeModel
   * (IBasicDEVSModel model) method (it internally uses this method) - but
   * instead of a model pointer it takes a model name as input parameter - this
   * model name must be the name of an existing submodel of this coupled model.
   * 
   * @param name
   *          the name
   * 
   * @throws InvalidModelException
   *           the invalid model exception
   */
  public void removeModel(String name) {
    removeModel(getModel(name));
  }

  /**
   * A call to this update method will invoke the inherited update mechansims as
   * well as it will invoke the update method of all sub models.
   */
  @Override
  public void update() {
    super.update();

    Iterator<IBasicDEVSModel> itm = getSubModelIterator();
    IBasicDEVSModel model;
    while (itm.hasNext()) {
      model = itm.next();
      model.update();
    }

  }

  /*
   * public String toString() { return super.toString() + "\n" +
   * couplingsToString(); }
   */

  /**
   * Update model.
   * 
   * @param present
   *          the present
   * @param replace
   *          the replace
   */
  @Override
  public void updateModel(IBasicDEVSModel present, IBasicDEVSModel replace) {

    subModels.replace(present, replace);
    // now we have to run through the list of couplings and replace the
    // reference
    // occurences of the model "present";

    externalInCouplings.replace(present, replace);
    externalOutCouplings.replace(present, replace);
    internalCouplings.replace(present, replace);

  }

  /**
   * Checks for model.
   * 
   * @param model
   *          the model
   * 
   * @return true, if checks for model
   */
  public boolean hasModel(IBasicDEVSModel model) {
    return subModels.contains(model);
  }

}
