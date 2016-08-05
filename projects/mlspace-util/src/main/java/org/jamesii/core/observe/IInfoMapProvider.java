/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import java.util.Collection;
import java.util.Map;

/**
 * * Interface for classes providing any information where fixed keys are
 * associated with different values during different states of the respective
 * object, i.e. information that can be usefully stored in a table (where the
 * maps' keys correspond to column headings and the values to the entries in
 * subsequent rows).
 *
 * Note: After existing for > 2 years, this serves mainly two purposes: adding
 * info to the model that needs to be passed on along with it (overridden params
 * while parsing, for later output; some sim setting) and compiling the final
 * information from observers. Separate methods for these two functionalities
 * may be implemented in some future refactoring.
 *
 * @author Arne Bittig
 *
 * @param <S>
 *          The type of the actual pieces of information
 */
public interface IInfoMapProvider<S> {

  /**
   * Get the information. If the information is not yet available (e.g. if a
   * steady state is to be returned, but has not been reached yet), a map with
   * null values may be returned, or a {@link RuntimeException} may be thrown.
   *
   * @return Information map
   */
  Map<String, ? extends S> getInfoMap();

  /**
   * Get the identifiers for the pieces of information (should be equal to
   * {@link #getInfoMap()}.keySet() whenever {@link #getInfoMap()} completes
   * normally).
   *
   * @return Indentifiers for the pieces of information
   */
  Collection<String> getInfoIDs();
}