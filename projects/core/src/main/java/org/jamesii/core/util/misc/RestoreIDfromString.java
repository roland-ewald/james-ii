/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.util.id.IUniqueID;

/**
 * Generates an id from a string.
 * 
 * @author Simon Bartels
 * 
 */
public final class RestoreIDfromString {

  /**
   * Temporary class to solve the id problem above. (See the fixme tag)
   * 
   * @author Simon Bartels
   * 
   */
  private static class IDwrapper extends ComputationTaskIDObject {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 8922773336491351263L;

    /**
     * The id as string.
     */
    private final String id;

    public IDwrapper(String id) {
      this.id = id;
    }

    @Override
    public int compareTo(IUniqueID uID) {
      return id.compareTo(uID.asString());
    }

    @Override
    public String asString() {
      return id;
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof IUniqueID) {
        if (id.compareTo(((IUniqueID) o).asString()) == 0) {
          return true;
        }
      }
      return false;
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }

  /**
   * Hidden constructor.
   */
  private RestoreIDfromString() {
  }

  /**
   * Creates a new {@link IUniqueID} from a string.
   * 
   * @param id
   *          the id
   * @return the id as object
   */
  public static IUniqueID createFromString(String id) {
    // FIXME: This can only be a temporary solution
    return new IDwrapper(id);
  }

}
