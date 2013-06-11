/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */

package org.jamesii.gui.utils.history;

import java.util.EventObject;

/**
 * The class HistoryItemEvent.
 * 
 * @author Enrico Seib
 * 
 */
public class HistoryItemEvent extends EventObject {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2422303155707581033L;

  /**
   * value added to History
   */
  public static final int VALUE_ADDED = 1;

  /**
   * Value has been changed
   */
  public static final int VALUE_CHANGED = 2;

  /**
   * Value has been removed
   */
  public static final int VALUE_REMOVED = 3;

  /**
   * All items with certain id have been removed
   */
  public static final int ID_REMOVED = 4;

  /**
   * History is deleted completely
   */
  public static final int CLEANED = 5;

  /**
   * Type of the event (added/changed/removed HistoryItem)
   * 
   * VALUE_ADDED = 1; VALUE_CHANGED = 2;VALUE_REMOVED = 3;<br>
   * ID_REMOVED = 4;CLEANED = 5;
   */
  private int type;

  /**
   * value of the of the added/changed/removed HistoryItem of this event
   */
  private String itemValue;

  /**
   * id of the HistoryItem belonging to this event.
   */
  private String itemId;

  /**
   * Constructor.
   * 
   * @param source
   *          link to the object where action was performed
   * 
   * @param id
   *          id of the added/changed/removed HistoryItem of this event
   * 
   * @param value
   *          value of the added/changed/removed HistoryItem of this event
   */
  public HistoryItemEvent(Object source, int type, String id, String value) {
    super(source);
    this.itemId = id;
    this.itemValue = value;
    this.type = type;
  }

  /**
   * Returns the type of event. VALUE_ADDED = 1; VALUE_CHANGED = 2;VALUE_REMOVED
   * = 3;<br>
   * ID_REMOVED = 4;CLEANED = 5;
   * 
   * @return the type of the added/changed/removed HistoryItem of this event
   */
  public int getType() {
    return this.type;
  }

  /**
   * Returns the value of the corresponding HistoryItem of this event.
   * 
   * @return value of the added/changed/removed HistoryItem of this event
   */
  public String getValue() {
    return this.itemValue;
  }

  /**
   * Returns the id of the corresponding HistoryItem of this event.
   * 
   * @return id of the added/changed/removed HistoryItem of this event
   */
  public String getId() {
    return this.itemId;
  }

}
