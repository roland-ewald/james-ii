/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

// TODO: Auto-generated Javadoc
/**
 * The Class SpecialHashMap.
 * 
 * @param <K>
 * @param <V>
 */
@SuppressWarnings({ "unused" })
public abstract class SpecialHashMap<K, V> {

  /**
   * The Class Entry.
   * 
   * @param <K>
   *          the key of the entry
   * @param <V>
   *          the value of the entry
   * @author Jan Himmelspach
   */
  static class Entry<K, V> {

    /** The key. */
    private K key;

    /** The value. */
    private V value;

    /**
     * Instantiates a new entry.
     */
    Entry() {
      super();
      key = null;
      value = null;
    }

    /**
     * Instantiates a new entry.
     * 
     * @param key
     *          the key
     * @param value
     *          the value
     */
    Entry(K key, V value) {
      super();
      this.key = key;
      this.value = value;
    }

    /**
     * Gets the key.
     * 
     * @return the key
     */
    K getKey() {
      return key;
    }

    /**
     * Gets the value.
     * 
     * @return the value
     */
    V getValue() {
      return value;
    }
  }

  /** The load factor. */
  private double loadFactor = 0.75;

  /** The lower bound. */
  private K lowerBound;

  /** The range. */
  private double range;

  /** The alpha. */
  private double alpha;

  /** The range treshold. */
  private double rangeTreshold;

  /** The reorder factor. */
  private double reorderFactor = 0.5;

  /** The size. */
  private int size = 0;

  /** The space. */
  private int space;

  /** The treshold. */
  private int treshold;

  /** The table. */
  private transient Entry<K, V>[] table;

  /** The upper bound. */
  private K upperBound;

  /**
   * Creates a new ordered hash map.
   * 
   * @param initialSize
   *          Half of the initital size of the
   * @param lBound
   *          Lowest value to be added to the map
   * @param uBound
   *          Highest value to be added to the map
   */
  @SuppressWarnings("unchecked")
  public SpecialHashMap(int initialSize, K lBound, K uBound) {
    super();
    table = new Entry[2 * initialSize];
    space = table.length;
    treshold = (int) (space * loadFactor);
    /*
     * for (int i = 0; i < table.length; i++) { table[i] = null; }
     */
    setLowerBound(lBound);
    setUpperBound(uBound);
    range = getRange(getLowerBound(), getUpperBound());

    alpha = (space - 1) / range;

  }

  /**
   * Contains key.
   * 
   * @param key
   *          the key
   * 
   * @return true, if contains key
   */
  public boolean containsKey(K key) {
    return findEntry(key) != null;
  }

  /**
   * Find entry.
   * 
   * @param key
   *          the key
   * 
   * @return the entry< k, v>
   */
  protected abstract Entry<K, V> findEntry(K key);

  /**
   * Gets the alpha.
   * 
   * @return the alpha
   */
  protected double getAlpha() {
    return alpha;
  }

  /**
   * Returns the hash address of the given key.
   * 
   * @param key
   *          the key
   * 
   * @return the hash address
   */

  public abstract int getHashAddress(K key);

  /**
   * Gets the range.
   * 
   * @param key1
   *          the key1
   * @param key2
   *          the key2
   * 
   * @return the range
   */
  public abstract double getRange(K key1, K key2);

  /**
   * Gets the storage size.
   * 
   * @return the storage size
   */
  public int getStorageSize() {
    return space;
  }

  /**
   * Gets the value.
   * 
   * @param key
   *          the key
   * 
   * @return the value
   */
  public V getValue(K key) {
    Entry<K, V> found = findEntry(key);
    if (found != null) {
      return found.getValue();
    }
    return null;

  }

  /**
   * Internal put.
   * 
   * @param set
   *          the set
   * @param keyEntry
   *          the key entry
   */
  protected abstract void internalPut(Entry<K, V>[] set, Entry<K, V> keyEntry);

  /**
   * print the complete table.
   */
  public void printTable() {
    for (int i = 0; i < table.length; i++) {
      if (table[i] != null) {
        System.out.println(i + "   " + table[i].getKey());
      } else {
        System.out.println(i + "   " + null);
      }

    }
  }

  /**
   * Insert the given in the hash map, thereby keeping taking the order of the
   * keys into account.
   * 
   * @param key
   *          the key
   * @param value
   *          the value
   */
  public void put(K key, V value) {

    if (size > treshold) {
      resize();
    }

    Entry<K, V> keyEntry = new Entry<>(key, value);

    internalPut(table, keyEntry);
  }

  /**
   * Creates a second hashmap of the same size and inserts all elements from
   * this map into the new one, thereby using the adapted range values.
   */
  public void reorder() {
    reorganize(table.length);
  }

  /**
   * Create a new array of the given size and reinsert all items (not the best
   * solution, but working).
   * 
   * @param newSize
   *          the new size
   */
  @SuppressWarnings("unchecked")
  protected void reorganize(int newSize) {
    Entry<K, V>[] help = new Entry[newSize];

    space = help.length;

    treshold = (int) (space * loadFactor);

    range = getRange(getLowerBound(), getUpperBound());

    alpha = (space - 1) / range;

    size = 0;

    for (int i = 0; i < table.length; i++) {
      if (table[i] != null) {
        internalPut(help, table[i]);
      }
    }
    table = help;
  }

  /**
   * Resize the array and reinsert all items (not the best solution, but
   * working).
   */
  public void resize() {
    reorganize(table.length * 2);
  }

  /**
   * Sets the new bounds.
   * 
   * @param lBound
   *          the l bound
   * @param uBound
   *          the u bound
   */
  public void setNewBounds(K lBound, K uBound) {
    setLowerBound(lBound);
    setUpperBound(uBound);
    reorder();
  }

  /**
   * Size.
   * 
   * @return the int
   */
  public int size() {
    return size;
  }

  /**
   * @return the lowerBound
   */
  public final K getLowerBound() {
    return lowerBound;
  }

  /**
   * @param lowerBound
   *          the lowerBound to set
   */
  public final void setLowerBound(K lowerBound) {
    this.lowerBound = lowerBound;
  }

  /**
   * @return the upperBound
   */
  public final K getUpperBound() {
    return upperBound;
  }

  /**
   * @param upperBound
   *          the upperBound to set
   */
  public final void setUpperBound(K upperBound) {
    this.upperBound = upperBound;
  }

  /**
   * @return the table
   */
  protected final Entry<K, V>[] getTable() {
    return table;
  }

  /**
   * @param table
   *          the table to set
   */
  protected final void setTable(Entry<K, V>[] table) {
    this.table = table;
  }

  /**
   * @return the size
   */
  protected final int getSize() {
    return size;
  }

  /**
   * @param size
   *          the size to set
   */
  protected final void setSize(int size) {
    this.size = size;
  }

}
