/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.factories.list;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.gui.utils.list.IViewableItem;
import org.jamesii.gui.utils.list.JItemList;
import org.jamesii.gui.utils.list.view.item.FactoryItem;

public class TestCompleteFactoryList {

  /**
   * @param args
   */
  public static void main(String[] args) {

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    List<Factory<?>> infos = SimSystem.getRegistry().getAllFactories();

    IViewableItem[] items = new IViewableItem[infos.size()];
    int c = 0;
    for (Factory pdata : infos) {
      items[c] =
          new FactoryItem(SimSystem.getRegistry().getFactoryInfo(
              pdata.getClass().getName()));
      c++;
    }

    String[] properties =
        new String[] { "Name", "Description", "Location", "Parameters" };

    JItemList myList = new JItemList(items, properties);

    myList.setPreferredSize(new Dimension(300, 300));
    frame.setLayout(new BorderLayout());
    frame.add(myList, BorderLayout.CENTER);

    // frame.setSize(300, 300);
    frame.pack();
    frame.setVisible(true);

  }

}
