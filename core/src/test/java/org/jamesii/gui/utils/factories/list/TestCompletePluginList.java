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
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.gui.utils.list.IViewableItem;
import org.jamesii.gui.utils.list.JItemList;
import org.jamesii.gui.utils.list.view.item.PluginItem;

public class TestCompletePluginList {

  /**
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    List<IPluginData> infos = SimSystem.getRegistry().getPlugins();

    IViewableItem[] items = new IViewableItem[infos.size()];
    int c = 0;
    for (IPluginData pdata : infos) {
      items[c] = new PluginItem(pdata);
      c++;
    }

    String[] properties =
        new String[] { "Name", "Version", "License", "Location", "Factories" };

    JItemList myList = new JItemList(items, properties);

    myList.setPreferredSize(new Dimension(300, 300));
    frame.setLayout(new BorderLayout());
    frame.add(myList, BorderLayout.CENTER);

    // frame.setSize(300, 300);
    frame.pack();
    frame.setVisible(true);

  }

}
