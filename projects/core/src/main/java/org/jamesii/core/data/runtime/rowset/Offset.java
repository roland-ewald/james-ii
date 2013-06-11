/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime.rowset;

/**
 * Represents a line index and an offset in a file. Intended for use as an entry
 * in conjunction with {@link IOffsetCache} implementations that might store
 * additional information about an offset, such as frequency or time of use.
 * <p>
 * This class implements value equality with {@link #equals(Object)} but
 * considers only the {@code index} and {@code offset} properties for equality.
 * 
 * @author Johannes Rössel
 */
public class Offset implements Comparable<Offset> {
  /**
   * Placeholder value for {@link #getLastUseTime()} if this cache entry was
   * unused previously.
   */
  public static final long UNUSED = Long.MIN_VALUE;

  /** The line index represented by this cache entry. */
  private int index;

  /** The file offset represented by this cache entry. */
  private long offset;

  /**
   * The number of accesses to this cache entry. Must be incremented by the
   * implementation of {@link IOffsetCache}.
   */
  private int hits;

  /** The time this cache entry was created. Read-only from the outside. */
  private long creationTime;

  /**
   * The time when this cache entry was last requested. This has to be updated
   * by the implementation of {@link IOffsetCache}.
   */
  private long lastUseTime;

  /**
   * Initialises a new instance of the {@link Offset} class with the given index
   * and offset. The creation time is set to the current time.
   * 
   * @param index
   *          The line index.
   * @param offset
   *          The file offset.
   */
  public Offset(int index, long offset) {
    this.index = index;
    this.offset = offset;
    this.creationTime = System.currentTimeMillis();
    this.hits = 0;
    this.lastUseTime = UNUSED;
  }

  /**
   * Retrieves the index from this cache entry.
   * 
   * @return The index stored in this entry.
   */
  public int getIndex() {
    return index;
  }

  /**
   * Retrieves the offset from this cache entry.
   * 
   * @return The offset stored in this entry.
   */
  public long getOffset() {
    return offset;
  }

  /**
   * Increments the access counter by one.
   * <p>
   * This method should be called by {@link IOffsetCache} implementations that
   * use the frequency of access to a cache entry for their strategy which
   * values to keep and which do discard.
   */
  public void incrementHits() {
    hits++;
  }

  /**
   * Retrieves the number of accesses to this cache entry.
   * 
   * @return The number of accesses to this entry.
   * 
   * @see #incrementHits()
   */
  public int getHits() {
    return hits;
  }

  /**
   * Retrieves the time when this cache entry was created.
   * 
   * @return The time this entry was created in milliseconds since 1970-01-01
   *         00:00.
   * 
   * @see System#currentTimeMillis()
   */
  public long getCreationTime() {
    return creationTime;
  }

  /**
   * Updates the time of last access to the current time.
   * <p>
   * This method should be called by {@link IOffsetCache} implementations that
   * use the time of the last access to a cache entry for their strategy which
   * values to keep and which do discard.
   */
  public void updateLastUseTime() {
    lastUseTime = System.currentTimeMillis();
  }

  /**
   * Retrieves the time when this cache entry was last accessed.
   * 
   * @return The time this entry was accessed in milliseconds since 1970-01-01
   *         00:00.
   * 
   * @see System#currentTimeMillis()
   */
  public long getLastUseTime() {
    return lastUseTime;
  }

  @Override
  public int compareTo(Offset o) {
    if (o == null) {
      throw new NullPointerException();
    }

    return o.getIndex() - this.getIndex();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Offset)) {
      return false;
    }
    Offset o = (Offset) obj;
    return o.getIndex() == index && o.getOffset() == offset;
  }

  @Override
  public int hashCode() {
    return Long.valueOf(offset).hashCode() ^ Integer.valueOf(index);
  }

}
