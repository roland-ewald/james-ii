/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

/**
 * The Class NamedEntity.
 * 
 * <br/>
 * In addition to being observable (inherited from Entity) a named entity
 * carries a "name", and is comparable. The comparison is done on base of the
 * compareTo() method of the String class. Thereby a name with value "null" is
 * considered to be unequal to an empty string.
 * 
 * @author Jan Himmelspach
 */
public class NamedEntity extends Entity implements INamedEntity {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -567577796000830095L;

  /**
   * Holds the name of this entity.
   */
  private String name;

  /**
   * Constructs an unnamed entity.
   */
  public NamedEntity() {
    super();
  }

  /**
   * Constructs a named entity and sets the given name.
   * 
   * @param name
   *          the name
   */
  public NamedEntity(String name) {
    super();
    this.name = name;
  }

  /**
   * Compares two named entities. If they are equal an 0 will be compared. The
   * default implementation should simply use the compareTo methods of the names
   * of entity. The return value of this method shall be returned then.
   * 
   * @param o
   *          the entity to compare with
   * 
   * @return an integer, see {@link java.lang.String#compareTo(String)} method
   *         for more details.
   */
  @Override
  public int compareTo(INamedEntity o) {
    // if a name is null we need some extra checks here
    if (name == null) {
      if (o.getName() == null) {
        return 0;
      }
      return -1;
    }
    if (o.getName() == null) {
      return -1;
    }
    return name.compareTo(o.getName());
  }

  @Override
  public String getCompleteInfoString() {
    return name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

}
