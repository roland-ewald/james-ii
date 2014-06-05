/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;


import java.util.ArrayList;
import java.util.Iterator;

import org.jamesii.core.util.collection.ElementSet;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

import model.devscore.IBasicDEVSModel;
import model.devscore.ports.IPort;

/**
 * The Class SimpleCouplingSet.
 */
public class SimpleCouplingSet extends ElementSet<BasicCoupling> implements
    ICouplingSet {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1966922701744722795L;

  @Override
  public void addCoupling(BasicCoupling coupling) {
    super.add(coupling);
  }

  @Override
  public boolean contains(BasicCoupling coupling) {
    return super.getVelements().contains(coupling);
  }

  @Override
  public boolean contains(String ident) {
    Iterator<BasicCoupling> cit = super.iterator();
    while (cit.hasNext()) {
      BasicCoupling c = cit.next();
      if (c.getName().compareTo(ident) == 0) {
        return true;
      }
    }
    return false;
  }

  @Override
  public BasicCoupling getCoupling(String name) {
    Iterator<BasicCoupling> cit = super.iterator();
    while (cit.hasNext()) {
      BasicCoupling c = cit.next();
      if (c.getName().compareTo(name) == 0) {
        return c;
      }
    }
    return null;
  }

  @Override
  public Iterator<BasicCoupling> getCouplingsIterator(IBasicDEVSModel model) {
    ArrayList<BasicCoupling> result = new ArrayList<>();
    for (int i = 0; i < getVelements().size(); i++) {
      BasicCoupling c = getVelements().get(i);
      if (c.isModel1(model)) {
        result.add(c);
      }
    }
    return result.iterator();
  }

  @Override
  public void removeCoupling(BasicCoupling coupling) {
    getVelements().remove(coupling);
  }

  private void fail() {
    throw new OperationNotSupportedException(
        "Removing couplings is not supported by the simple coupling set implementation!");
  }

  @Override
  public void removeCoupling(IBasicDEVSModel model1, IPort port1,
      Class<IBasicDEVSModel> model2, IPort port2) {
    fail();
  }

  @Override
  public void removeCoupling(IBasicDEVSModel model1, IPort port1,
      IBasicDEVSModel model2, IPort port2) {
    // Iterator<BasicCoupling> cit = velements.iterator();
    fail();
  }

  @Override
  public void removeCoupling(String couplingName) {
    fail();
  }

  @Override
  public void removeCouplings(IBasicDEVSModel model) {
    fail();
  }

  @Override
  public void replace(IBasicDEVSModel currentModel, IBasicDEVSModel newModel) {
    fail();
  }

}
