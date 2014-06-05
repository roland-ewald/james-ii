/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

import model.devscore.IBasicDEVSModel;

/**
 * The class {@link ClassLooseCoupling}.
 * 
 * Extends the class {@link LooseCoupling} by references to the classes of the
 * source and target models between which this type of coupling is defined.<br>
 * <br>
 * 
 * Analogous to {@link LooseCoupling}s, the existence of this type of couplings
 * has to be assured by the simulator.<br>
 * <br>
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Alexander Steiniger
 * 
 */
public class ClassLooseCoupling extends LooseCoupling {

  private static final int PRIME_FOR_HASH = 31;

  /** the class of source models of this coupling */
  private final Class<? extends IBasicDEVSModel> model1;

  /** the class of target models of this coupling */
  private final Class<? extends IBasicDEVSModel> model2;

  /**
   * Creates a new instance of {@link ClassLooseCoupling} and assigns the given
   * parameter arguments to it.
   * 
   * @param sourcePort
   *          the name of the source port
   * @param sourceModel
   *          the class of the source model
   * @param targetPort
   *          the name of the target port
   * @param targetModel
   *          the class of the target model
   * @throws IllegalArgumentException
   *           if one of the given arguments is <code>null</code>
   */
  public ClassLooseCoupling(String sourcePort,
      Class<? extends IBasicDEVSModel> sourceModel, String targetPort,
      Class<? extends IBasicDEVSModel> targetModel) {
    super(sourcePort, targetPort);

    // check given classes for source and target model against null
    if ((sourceModel == null) || (targetModel == null)) {
      throw new IllegalArgumentException(
          "ClassLooseCoupling cannot be instanciated as class of source or target model is null.");
    }
    model1 = sourceModel;
    model2 = targetModel;
  }

  /**
   * The copy constructor of {@link ClassLooseCoupling}.
   * 
   * @param coupling
   *          the class loose coupling to copy
   */
  public ClassLooseCoupling(ClassLooseCoupling coupling) {
    super(coupling.getSourcePort(), coupling.getTargetPort());
    model1 = coupling.getModel1();
    model2 = coupling.getModel2();
  }

  @Override
  public boolean equals(Object obj) {
    // check for object identity
    if (this == obj) {
      return true;
    } else {
      // check object type
      if (obj instanceof ClassLooseCoupling) {
        ClassLooseCoupling coupling = (ClassLooseCoupling) obj;
        // check if all attributes are equal
        if ((getSourcePort().equals(coupling.getSourcePort()))
            && (getTargetPort().equals(coupling.getTargetPort()))
            && (model1 == coupling.getModel1())
            && (model1 == coupling.getModel2())) {

          return true;
        }
      }
    }
    // not equal
    return false;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode() * PRIME_FOR_HASH;
    result += model1.hashCode();
    result *= PRIME_FOR_HASH;
    result += model2.hashCode();
    result *= PRIME_FOR_HASH;
    return result;
  }

  /**
   * @return the model1
   */
  public Class<? extends IBasicDEVSModel> getModel1() {
    return model1;
  }

  /**
   * @return the model2
   */
  public Class<? extends IBasicDEVSModel> getModel2() {
    return model2;
  }

}
