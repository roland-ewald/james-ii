/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities;

import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Species of Entities that have ID and diffusion constant in common (but not
 * necessarily attributes)
 *
 * @author Arne Bittig
 */
public class Species implements java.io.Serializable {

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(Species.class,
        new IConstructorParameterProvider<Species>() {
          @Override
          public Object[] getParameters(Species spec) {
            return new Object[] { spec.getName() };
          }
        });
  }

  private static final long serialVersionUID = -8921398160063585600L;

  /** The actual content: the species name */
  private final String name;

  /**
   * New Species identified by given name
   * 
   * @param name
   *          Name of the species
   */
  public Species(String name) {
    this.name = name;
  }

  /**
   * @return Name of the species (same as {@link #toString()})
   */
  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      // if (obj instanceof String)
      // return this.name.equals(obj);
      // else
      return false;
    }
    return this.name.equals(((Species) obj).name);
  }

  @Override
  public String toString() {
    return name;
  }

}