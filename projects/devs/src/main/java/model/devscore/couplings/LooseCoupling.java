/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.couplings;

/**
 * The class {@link LooseCoupling}.
 * 
 * This simple class defines a dynamic and loose coupling. This type of coupling
 * is only specified by the name of a source port and a target port. The
 * existence of this ports implies the existence of the coupling and thus has to
 * be checked during simulation. Model references are not required to define the
 * coupling.
 * 
 * @author Alexander Steiniger
 */
public class LooseCoupling {

  private static final int PRIME_FOR_HASH = 31;
  
  /** port name of source port */
  private final String sourcePort;

  /** port name of target port */
  private final String targetPort;

  /**
   * Creates a new instance of {@link LooseCoupling} and assigns the given
   * arguments to it.
   * 
   * @param source
   *          the name of the source port of the loose coupling to instantiate
   * @param target
   *          the name of the target port of the loose coupling to instantiate
   * @throws IllegalArgumentException
   *           if at least one of the given arguments is <code>null</code>
   */
  public LooseCoupling(String source, String target) {
    // check if at least one of the arguments is null
    if ((source == null) || (target == null)) {
      throw new IllegalArgumentException(
          "LooseCoupling cannot be instanciated as at least one of the arguments is null.");
    }

    this.sourcePort = source;
    this.targetPort = target;
  }

  /**
   * The copy constructor for {@link LooseCoupling}.
   * 
   * @param coupling
   *          the loose coupling to copy
   */
  public LooseCoupling(LooseCoupling coupling) {
    // check if given loose coupling is null
    if (coupling == null) {
      throw new IllegalArgumentException(
          "LooseCoupling cannot be instanciated as the given argument to copy is null.");
    }

    sourcePort = coupling.getSourcePort();
    targetPort = coupling.getTargetPort();
  }

  /**
   * @return the name of the source port
   */
  public String getSourcePort() {
    return sourcePort;
  }

  /**
   * @return the name of the target port
   */
  public String getTargetPort() {
    return targetPort;
  }

  @Override
  public boolean equals(Object obj) {
    // check for object identity
    if (this == obj) {
      return true;
    } else {
      // check if classes equals
      if (obj instanceof LooseCoupling) {
        LooseCoupling looseCoupling = (LooseCoupling) obj;
        // check if port names equal
        if (sourcePort.equals(looseCoupling.getSourcePort())
            && targetPort.equals(looseCoupling.getTargetPort())) {
          return true;
        }
      }
    }

    // objects not equal
    return false;
  }

  @Override
  public int hashCode() {
    int result = sourcePort.hashCode() * PRIME_FOR_HASH;
    result += targetPort.hashCode();
    result *= PRIME_FOR_HASH;
    return result;
  }
}
