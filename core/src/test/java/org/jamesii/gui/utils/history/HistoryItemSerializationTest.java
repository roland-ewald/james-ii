/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.gui.utils.history.HistoryItem;

/**
 * @author Stefan Rybacki
 * 
 */
public class HistoryItemSerializationTest extends
    SimpleSerializationTest<HistoryItem> {

  @Override
  public void assertEquality(HistoryItem original,
      HistoryItem deserialisedVersion) {
    assertEquals(original, deserialisedVersion);
  }

  @Override
  public HistoryItem getTestObject() throws Exception {
    HistoryItem item = new HistoryItem("HelloWorld");
    item.update();
    return item;
  }

}
