/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import org.jamesii.core.util.collection.list.ISelector;
import org.jamesii.core.util.collection.list.StandardSelector;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class ClassCoupling. This class contains the functionality of
 * ClassCouplings: This type of Coupling connects a source-model and one of its
 * outputports to all submodels of the specified class and the given inputport
 * of it. The associated Selector specifies the number of items to select and
 * selects the receivers of a message send over this ClassCoupling.
 * 
 * 
 * @author Jan Himmelspach
 * @author Christian Ober
 * @version 1.0
 * 
 *          history 26.01.2004 Christian Ober added JavaDoc-comments. history
 *          14.02.2004 Christian Ober added Selector.
 */
public class ClassCoupling extends BasicCoupling {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 703719501538505650L;

  /** The target type of this ClassCoupling. */
  private Class<IBasicDEVSModel> model2;

  /** The target port of the target type of this ClassCoupling. */
  private String port2;

  /** The associated {@link ISelector} to select several Items. */
  private ISelector<IBasicDEVSModel> selector;

  /**
   * Creates a new coupling object - from the given.
   * 
   * @param model1
   *          The given BasicDEVSModel used as source.
   * @param port1
   *          The corresponding outgoing port of the model.
   * @param model2
   *          The target of this ClassCoupling specified as Class.
   * @param port2
   *          The targetting Port.
   * 
   * @model1,
   * @port1 to
   * @model2,
   * @port2. Note that a {@link ISelector} has to be associated and in this case
   *         the {@link StandardSelector} is selected.
   */
  public ClassCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2) {
    this(model1, port1, model2, port2, null);
  }

  /**
   * Creates a new coupling object - from the given.
   * 
   * @param model1
   *          The given BasicDEVSModel used as source.
   * @param port1
   *          The corresponding outgoing port of the model.
   * @param model2
   *          The target of this ClassCoupling specified as Class.
   * @param port2
   *          The targetport.
   * @param selector
   *          The {@link ISelector} containing special information about how to
   *          select and how many to return.
   * 
   * @model1,
   * @port1 to
   * @model2,
   * @port2.
   */
  public ClassCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, String port2,
      ISelector<IBasicDEVSModel> selector) {
    super(model1, port1);
    this.model2 = model2;
    this.port2 = port2;
    if (selector != null) {
      this.selector = selector;
    } else {
      this.selector = new StandardSelector<>();
    }
    this.setName(this.toString());
  }

  /**
   * Compares two couplings.
   * 
   * @param o
   *          The source-coupling to be compared with.
   * 
   * @return the int
   * 
   *         0 if the source and target models and ports match<br>
   *         1 if model1 > model2 or (model1 = model) and port1 > port2<br>
   *         -1 if model1 < model2 or (model1 = model) and port1 < port2<br>
   */
  @Override
  public int compareTo(BasicCoupling o) {
    if (o instanceof Coupling) {
      int i = getModel1().compareTo(o.getModel1());
      // int j = ((Comparable)getModel2()).compareTo(((Coupling)o).getModel2());
      int j = 0;
      int k = getPort1().compareTo(o.getPort1());

      int l =
          ((Comparable<String>) getPort2()).compareTo(((Coupling) o).getPort2()
              .getName());
      if ((i == j) && (i == k) && (i == l) && (i == 0)) {
        return 0;
      } else if ((i > j) || (i == j) && (k > l)) {
        return 1;
      } else {
        return -1;
      }
    }
    return 0;
  }

  /**
   * Returns a pointer to the drain/target model.
   * 
   * @return A pointer to the target model.
   */
  public Class<?> getModel2() {
    return model2;
  }

  /**
   * Returns a pointer to the drain/target port.
   * 
   * @return A pointer to the target port.
   */
  public String getPort2() {
    return port2;
  }

  /**
   * Retrieves the {@link ISelector} of this ClassCoupling.
   * 
   * @return The {@link ISelector} of this ClassCoupling.
   */
  public ISelector<IBasicDEVSModel> getSelector() {
    return this.selector;
  }

  /**
   * Checks whether the target-model is of the type that is required.
   * 
   * @param model
   *          The type the model2 is to be checked to be of.
   * 
   * @return true if the
   * 
   * @model is the model2 (that's the drain) of the coupling
   */
  public boolean isModel2(Class<IBasicDEVSModel> model) {
    return (model2 == model);
    // return (model2.REMOTEgetFullName().compareTo(model.REMOTEgetFullName())
    // == 0);
  }

  /**
   * Sets the {@link ISelector} for this ClassCoupling.
   * 
   * @param selector
   *          The new {@link ISelector}.
   */
  public void setSelector(ISelector<IBasicDEVSModel> selector) {
    this.selector = selector;
  }

  @Override
  public String toString() {

    StringBuffer buff = new StringBuffer();
    buff.append(getModel1().getName());
    buff.append(":");
    buff.append(getPort1().getName());
    buff.append(" - class(");
    buff.append(getModel2().getName());
    buff.append("):");
    buff.append(getPort2());
    return buff.toString();

  }
}
