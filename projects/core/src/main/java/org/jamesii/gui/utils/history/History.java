/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.collection.ListenerSupport;
import org.jamesii.gui.application.preferences.Preferences;

/**
 * The Class History. This class is used to store items (Strings) in the
 * Preferences of JamesGUI. Items are stored/retrieved by certain id.
 * 
 * @author Enrico Seib
 */
public final class History {

  /**
   * The singleton instance.
   */
  private static final History instance = new History();

  /**
   * Constant that indicates that no sorting should be applied
   */
  public static final int UNSORTED = -1;

  /**
   * Constant that indicates if sorting by latest usage should be applied
   */
  public static final int LATEST = 1;

  /**
   * Constant that indicates if sorting by most often usage should be applied
   */
  public static final int MOST_USED = 2;

  /**
   * Constant that indicates if all possible elements should be returned
   */
  public static final int ALL = Integer.MAX_VALUE;

  /**
   * HashMap mapping key(String) to History
   */
  private Map<String, HistoryList> map;

  /**
   * List managing all listener (for all ID's)
   */
  private static final ListenerSupport<IHistoryItemListener> listenerList =
      new ListenerSupport<>();

  /**
   * boolean value, true if values should be ordered by counter (default), if
   * false: order by date of last use
   */
  private static boolean orderByCounter = true;

  /**
   * Separator used to specify level of hierarchies
   */
  public static final String SEPARATOR = ".";

  /**
   * Default constructor
   */
  private History() {
    map = Preferences.get("org.jamesii.history");
    if (map == null) {
      map = new HashMap<>();
      Preferences.put("org.jamesii.history", (Serializable) map);
    }

    // remove null lists
    List<String> keys = new ArrayList<>();

    for (String key : map.keySet()) {
      if (map.get(key) == null) {
        keys.add(key);
      }
    }

    for (String key : keys) {
      map.remove(key);
    }
  }

  /**
   * Clears the history
   */
  public static void clear() {
    instance.map.clear();
    distributeEvent(new HistoryItemEvent(instance, HistoryItemEvent.CLEANED,
        null, null));
  }

  /**
   * Gets values from history with certain properties.
   * 
   * @param id
   *          id of the items
   * @param includeSubKeys
   *          true if the subKeys should also be considered in the return list,
   *          else false
   * @param sortingOption
   *          decides whether the items in the list should be sorted be latest
   *          use or most often use ( {@link #LATEST}, {@link #MOST_USED},
   *          {@link #UNSORTED} )
   * @param number
   *          maximum quantity of return list (e.g., {@link #ALL} )
   * @return Sorted(by latest use or most often used) list of Strings which
   *         contains all values with certain id, list has the maximal quantity
   *         of number elements if number is not {@link #ALL}
   * @see #MOST_USED
   * @see #UNSORTED
   * @see #LATEST
   * @see #ALL
   */
  public static List<String> getValues(String id, boolean includeSubKeys,
      int sortingOption, int number) {
    switch (sortingOption) {
    case UNSORTED:
      return getAllValuesOfKey(id, includeSubKeys);
    case LATEST:
      return getLatestValues(id, includeSubKeys, number);
    case MOST_USED:
    default:
      return getMostUsedValues(id, includeSubKeys, number);
    }
  }

  /**
   * Adds a HistoryItem into the History <br>
   * If a HistoryItem with same key and value already contained, the item is
   * updated, else the item is added to the <br>
   * History HistoryItem contains of a certain id (key) and a value to be stored
   * 
   * @param key
   *          key of HistoryItem
   * @param value
   *          value to be stored (full path and filename)
   */
  public static void putValueIntoHistory(String key, String value) {
    if (key == null || value == null) {
      return;
    }

    // id is not contained in the map
    // --> create HistoryItem and HistoryList, add HistoryItem to
    // HistoryList
    // and HistoryList to History
    if (!instance.map.containsKey(key)) {
      HistoryItem hItem = new HistoryItem(value);
      HistoryList hList = new HistoryList(key);
      hList.putHistoryItem(hItem);
      instance.map.put(key, hList);
      distributeEvent(new HistoryItemEvent(instance,
          HistoryItemEvent.VALUE_ADDED, key, value));
    }

    // id is contained in the map
    else {
      HistoryList hList = instance.map.get(key);
      // check if HistoryItem is already contained in the list
      // item with value not contained in list --> create HistoryItem,
      // add to
      // list
      if (!hList.containsValue(value)) {
        HistoryItem hItem = new HistoryItem(value);
        hList.putHistoryItem(hItem);
        distributeEvent(new HistoryItemEvent(instance,
            HistoryItemEvent.VALUE_ADDED, key, value));
      }
      // item already contained in the list --> update item
      else {
        hList.updateItem(value);
        distributeEvent(new HistoryItemEvent(instance,
            HistoryItemEvent.VALUE_CHANGED, key, value));
      }
    }

  }

  /**
   * Removes a certain an item with a certain value and a certain key from
   * History
   * 
   * @param key
   *          id of the item
   * @param value
   *          value of the item to remove
   */
  public static void removeValueFromHistory(String key, String value) {
    if (key == null || value == null) {
      return;
    }

    // id is not contained in the historyMap
    if (!instance.map.containsKey(key)) {
      return;
    }
    // id is contained in the History; check if list is not empty and
    // contains value, if so: remove HistoryItem
    HistoryList list = instance.map.get(key);
    if (list != null && !list.isEmpty() && list.containsValue(value)) {
      list.removeItem(value);
      distributeEvent(new HistoryItemEvent(instance,
          HistoryItemEvent.VALUE_REMOVED, key, value));
    }
  }

  /**
   * Removes all items of a certain key
   * 
   * @param key
   *          id of the items to be removed
   */
  public static void removeIDfromHistory(String key) {
    if (key == null) {
      return;
    }
    if (!instance.map.containsKey(key)) {
      return;
    }

    instance.map.put(key, new HistoryList(key));
    distributeEvent(new HistoryItemEvent(instance, HistoryItemEvent.ID_REMOVED,
        key, null));
  }

  /**
   * Adds a listener to the set (list) of all listener; <br>
   * if an event occurs, the listener decide itself if this event is useful for
   * them
   * 
   * @param listener
   *          listener to add
   */
  public static void addListener(IHistoryItemListener listener) {

    // return if listener is null or already contained in the
    // listenerList
    if (listener == null || listenerList.contains(listener)) {
      return;
    }

    listenerList.add(listener);
  }

  /**
   * Removes listener from list of all listener
   * 
   * @param listener
   *          listener to be removed
   */
  public static void removeListener(IHistoryItemListener listener) {
    if (listener == null) {
      return;
    }

    if (listenerList.contains(listener)) {
      listenerList.remove(listener);
    }
  }

  /**
   * Returns a sorted list of HistoryItem's with a certain key, <br>
   * this method provides sorting by recently usage or sorting by most often
   * usage
   * 
   * @param key
   *          key of HistoryItems to look at
   * @return sorted list of history items belonging to the specified key, sorted
   *         by most often usage or by last recently usage
   */
  private static List<HistoryItem> getAndSort(String key) {
    ArrayList<HistoryItem> itemList;

    if (orderByCounter) {
      itemList = (ArrayList<HistoryItem>) instance.map.get(key).sortByCount();
    } else {
      itemList = (ArrayList<HistoryItem>) instance.map.get(key).sortByAccess();
    }

    return itemList;
  }

  /**
   * Gets list of values out of the History belonging to a certain key
   * 
   * @param key
   *          id if HistoryItems
   * @param includeSubKeys
   *          true, if sub elements should be included to the output list
   * @return List of values belonging to certain key, empty list if no values
   *         with key are contained in the history
   */
  private static List<String> getAllValuesOfKey(String key,
      boolean includeSubKeys) {

    if (!includeSubKeys) {
      // Key does not exist, return empty ArrayList
      if (!instance.map.containsKey(key)) {
        return new ArrayList<>();
      }

      List<HistoryItem> sList = new ArrayList<>();

      // get and order items
      sList.addAll(getAndSort(key));

      // transfer item values into separate list
      List<String> list = new ArrayList<>();
      list.addAll(transferItemsToString(sList));

      return list;
    }

    // includeSubKeys == true
    // stores keys which have to be processed
    List<String> keyList = new ArrayList<>();
    List<String> subKeyList = new ArrayList<>();
    // List of all items
    List<HistoryItem> sList = new ArrayList<>();

    // create a list of subkeys
    subKeyList.add(key);

    keyList.addAll(instance.map.keySet());
    for (String k : instance.map.keySet()) {
      if (isSubkey(k, key)) {
        subKeyList.add(k);
      }
    }

    // add items with corresponding keys to item list
    for (String currentKey : subKeyList) {
      if (instance.map.containsKey(currentKey)) {
        sList.addAll(instance.map.get(currentKey).getHList());
      }
    }

    // remove redundant items, sum up counter if values are equal and
    // take
    // biggest time stamp
    sList = mergeItemList(sList);
    sList = removeRedundantItems(sList);

    // order items
    if (sList != null) {
      sList = sortList(sList, orderByCounter);
    }

    // transfer item values into separate list
    List<String> list = new ArrayList<>();
    list.addAll(transferItemsToString(sList));

    return list;
  }

  /**
   * Sorts a given list of HistoryItems by last recently usage or by most often
   * usage
   * 
   * @param list
   *          Input list of unsorted HistoryItems
   * @param orderByCount
   *          if true, order by counter (most often used item first), else order
   *          by latest usage (latest first)
   * @return sorted list of HistoryItems
   */
  private static List<HistoryItem> sortList(List<HistoryItem> list,
      boolean orderByCount) {
    if (orderByCount) {
      Collections.sort(list, new HistoryItemComparatorByCounter());
    } else {
      Collections.sort(list, new HistoryItemComparatorByDate());
    }
    return list;
  }

  /**
   * Transfers values from ItemList into list of values (String)
   * 
   * @param itemList
   *          list of HistoryItems
   * @return List of values of the input HistoryItem-List
   */
  private static List<String> transferItemsToString(List<HistoryItem> itemList) {

    List<String> valueList = new ArrayList<>();
    for (HistoryItem hi : itemList) {
      valueList.add(hi.getValue());
    }

    return valueList;
  }

  /**
   * Merges a list of HistoryItems if each value occurs more than once<br>
   * If items with same value occur more than once, items are merged into one
   * item by sum up counter and set last access to last time stamp
   * 
   * @param itemList
   *          List of HistoryItems, maybe containing multiple items
   * @return List of items with no multiple items
   */
  private static List<HistoryItem> mergeItemList(List<HistoryItem> itemList) {
    Collections.sort(itemList, new HistoryItemComparatorByName());
    return itemList;
  }

  /**
   * Detects duplicated items, removes duplicate items, updates remained item
   * (sum up counter, gets latest time stamp)
   * 
   * @param itemList
   *          List of HistoryItems that maybe contains duplicate items
   * @return List of items without duplicated but updated items
   */
  private static List<HistoryItem> removeRedundantItems(
      List<HistoryItem> itemList) {
    int index = 0;
    while (index < itemList.size() - 1) {
      HistoryItem item1 = itemList.get(index);
      HistoryItem item2 = itemList.get(index + 1);

      if (item1.getValue().equals(item2.getValue())) {
        item1.setCounter(item1.getCounter() + item2.getCounter());

        if (item1.getLastUsage().before(item2.getLastUsage())) {
          item1.setLastUsage(item2.getLastUsage());
        }

        itemList.remove(index + 1);
      } else {
        index++;
      }
    }
    return itemList;
  }

  /**
   * Returns a list of Strings of the latest values belonging to a certain key,
   * list contains only a certain quantity/number of entries
   * 
   * @param id
   *          key of items
   * @param includeSubKeys
   *          true if sub elements should be included
   * @param number
   *          quantity of values which should be returned
   * @return List with max. number X of elements, sorted by "latest element
   *         first"
   */
  private static List<String> getLatestValues(String id,
      boolean includeSubKeys, int number) {
    // System.out.println("getLatestUsedValues");
    orderByCounter = false;

    List<String> list;

    list = getAllValuesOfKey(id, includeSubKeys);

    if (number == History.ALL) {
      return list;
    }

    List<String> rList = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      if (i >= number) {
        break;
      }
      rList.add(list.get(i));
    }

    return rList;

  }

  /**
   * Returns a list of Strings of the most used values belonging to a certain
   * key, list contains only a certain quantity/number of entries
   * 
   * @param id
   *          key of items
   * @param includeSubKeys
   *          true if subElements should be included
   * @param number
   *          quantity of values which should be returned or use {@link #ALL} to
   *          retrieve all values
   * @return List with max. number X elements, sorted by "most often used
   *         element first"
   */

  private static List<String> getMostUsedValues(String id,
      boolean includeSubKeys, int number) {
    // System.out.println("getMostUsedValues");
    orderByCounter = true;

    List<String> list;
    List<String> rList = new ArrayList<>();

    list = getAllValuesOfKey(id, includeSubKeys);

    for (int i = 0; i < list.size(); i++) {
      if (i >= number) {
        break;
      }
      rList.add(list.get(i));
    }

    return rList;
  }

  /**
   * Distributes a HistoryItemEvent to all listener in the listenerList
   * 
   * @param e
   *          HistoryItemEvent
   */
  private static void distributeEvent(HistoryItemEvent e) {
    for (IHistoryItemListener listener : listenerList) {
      switch (e.getType()) {
      case HistoryItemEvent.VALUE_ADDED:
        listener.valueAdded(e);
        break;
      case HistoryItemEvent.VALUE_CHANGED:
        listener.valueChanged(e);
        break;
      case HistoryItemEvent.VALUE_REMOVED:
        listener.valueRemoved(e);
        break;
      case HistoryItemEvent.ID_REMOVED:
        listener.idRemoved(e);
        break;
      case HistoryItemEvent.CLEANED:
        listener.cleaned(e);
        break;
      default:
      }
    }

  }

  /**
   * Checks if the specified key is a subkey of another one
   * 
   * @param subKey
   *          the sub key
   * @param ofKey
   *          the parent key
   * @return true, if subkey is a sub key of the parent key
   */
  public static boolean isSubkey(String subKey, String ofKey) {
    return subKey.startsWith(ofKey + SEPARATOR);
  }

}
