/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.variables;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An environment which contains variables and their former assignments.
 * 
 * 
 * @author Jan Himmelspach
 * @param <K>
 *          the type of the keys (usually "names", i.e., strings will be used
 *          here) used to identify variables
 * 
 */
public class HistoryEnvironment<K extends Serializable> extends Environment<K>
    implements IHistoryEnvironment<K> {

  private static final long serialVersionUID = 7007828571814652098L;

  /**
   * History of assignments in the environment. Whenever timeStep is executed,
   * the current values are added to this list.
   */
  private List<Map<K, Object>> history = new ArrayList<>();

  @Override
  public Object getValue(K ident, Integer relTime) {
    if (relTime.compareTo(0) == 0) {
      return getValue(ident);
    }

    return history.get(history.size() - relTime).get(ident);
  }

  @Override
  public void timeStep() {
    history.add(getValues());
    setValues(new HashMap<K, Object>());
  }

  @Override
  public void removeAll() {
    for (Map<K, Object> m : history) {
      m.clear();
    }
    super.removeAll();
  }

  @Override
  public void removeValue(K ident) {
    super.removeValue(ident);
    for (Map<K, Object> m : history) {
      m.remove(ident);
    }
  }

}
