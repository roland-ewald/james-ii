/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jamesii.core.serialization.IEncoderCompatible;
import org.jamesii.core.serialization.XMLEncoderFactory;
import org.jamesii.core.util.Reflect;

/**
 * The Class HistoryList. <b>For internal use only!</b> This class implements a
 * list of HistoryItems used by the class "History"
 * 
 * @author Enrico Seib
 */
final class HistoryList implements IEncoderCompatible {
  {
    PersistenceDelegate delegate = new DefaultPersistenceDelegate() {

      @Override
      protected Expression instantiate(Object oldInstance, Encoder out) {
        HistoryList list = (HistoryList) oldInstance;

        return new Expression(
            oldInstance,
            Reflect.class,
            "instantiate",
            new Object[] {
                list.getClass(),
                new Object[] { list.getId(),
                    list.getHList().toArray(new HistoryItem[getHList().size()]) } });
      }

      @Override
      protected void initialize(Class<?> type, Object oldInstance,
          Object newInstance, Encoder out) {
      }

    };

    // register custom persistence delegate for HistoryList
    XMLEncoderFactory.registerDelegate(HistoryList.class, delegate);
  }

  /**
   * List to manage all HistoryItems of a certain type
   */

  private static final long serialVersionUID = 8476455508872291432L;

  /**
   * List containing HistoryItems
   */
  private List<HistoryItem> hList;

  /**
   * Unique identifier
   */
  private String id;

  /**
   * The hash code.
   */
  private int hashCode;

  /**
   * Default constructor
   * 
   * @param id
   */
  public HistoryList(String id) {
    this(id, new HistoryItem[0]);
  }

  /**
   * Instantiates a new history list.
   * 
   * @param id
   *          the id
   * @param items
   *          the items
   */
  private HistoryList(String id, HistoryItem[] items) {
    this.id = id;
    hList = new ArrayList<>();
    for (HistoryItem i : items) {
      putHistoryItem(i);
    }
    calculateHashCode();
  }

  /**
   * Puts a HistoryItem in the internal list.
   * 
   * @param hItem
   */
  public void putHistoryItem(HistoryItem hItem) {
    if (hItem != null) {
      hList.add(hItem);
      calculateHashCode();
    }
  }

  /**
   * Returns a list of HistoryItems, elements of this list are taken from the
   * internal list and sorted by counter, biggest counter first
   * 
   * @return sorted list, sorted by counter
   */
  public List<HistoryItem> sortByCount() {
    ArrayList<HistoryItem> sList = new ArrayList<>();
    sList.addAll(hList);
    Collections.sort(sList, new HistoryItemComparatorByCounter());
    return sList;
  }

  /**
   * Returns a list of HistoryItems, elements of this list are taken from the
   * internal list and sorted by last access, latest access first
   * 
   * @return sorted list, sorted by access
   */
  public List<HistoryItem> sortByAccess() {
    ArrayList<HistoryItem> sList = new ArrayList<>();
    sList.addAll(hList);
    Collections.sort(sList, new HistoryItemComparatorByDate());
    return sList;
  }

  /**
   * Returns the internal list
   * 
   * @return List of historyItems
   */
  public List<HistoryItem> getHList() {
    return hList;
  }

  /**
   * Checks if an HistoryItem with value value is contained
   * 
   * @param value
   *          value(path) of the HistoryItem
   * @return true if item with value is contained; else false
   */
  public boolean containsValue(String value) {
    if (value == null) {
      return false;
    }

    boolean found = false;
    HistoryItem hi;
    Iterator<HistoryItem> i1 = hList.iterator();
    while (i1.hasNext() && !found) {
      hi = i1.next();
      if (hi == null) {
        return false;
      }
      if (hi.getValue().equals(value)) {
        found = true;
        break;
      }
    }

    return found;

  }

  /**
   * Updates a certain item (increment counter and update lastUsage )
   * 
   * @param value
   *          unique value of the item
   */
  public void updateItem(String value) {
    if (value != null) {
      HistoryItem hi;
      Iterator<HistoryItem> i = hList.iterator();
      while (i.hasNext()) {
        hi = i.next();
        if (hi.getValue().equals(value)) {
          hi.update();
          break;
        }
      }
    }
    calculateHashCode();
  }

  /**
   * Removes an HistoryItem with value from List
   * 
   * @param value
   *          value of HistoryItem which should removed
   */
  public void removeItem(String value) {
    if (value != null) {
      HistoryItem hi;
      Iterator<HistoryItem> i = hList.iterator();
      while (i.hasNext()) {
        hi = i.next();
        if (hi.getValue().equals(value)) {
          hList.remove(hi);
          break;
        }
      }
    }
    calculateHashCode();
  }

  /**
   * Gets the unique id of the HistoryList
   * 
   * @return unique identifier of the HistoryList
   */
  public String getId() {
    return id;
  };

  /**
   * Returns true if HistoryList is empty, else false
   * 
   * @return true if HistoryList is empty, else false
   */
  public boolean isEmpty() {
    return hList.isEmpty();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof HistoryList)) {
      return false;
    }

    HistoryList list = (HistoryList) obj;

    if (!list.getId().equals(id)) {
      return false;
    }

    if (list.getHList().size() != hList.size()) {
      return false;
    }

    for (HistoryItem i : hList) {
      if (!list.getHList().contains(i)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Calculates hash code.
   */
  private void calculateHashCode() {
    hashCode = getClass().hashCode();

    for (HistoryItem i : hList) {
      hashCode = 31 * hashCode + i.hashCode();
    }
  }

  @Override
  public int hashCode() {
    return hashCode;
  }
}