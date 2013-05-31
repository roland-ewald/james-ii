/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.gui.utils.history.HistoryItem;
import org.jamesii.gui.utils.history.HistoryList;

/**
 * @author Stefan Rybacki
 * 
 */
public class HistoryListSerializationTest extends
    SimpleSerializationTest<HistoryList> {

  @Override
  public void assertEquality(HistoryList original,
      HistoryList deserialisedVersion) {
    assertEquals(original, deserialisedVersion);
  }

  @Override
  public HistoryList getTestObject() throws Exception {
    HistoryList l = new HistoryList("HelloWorld");
    l.putHistoryItem(new HistoryItem("Value1"));
    return l;
  }

}
