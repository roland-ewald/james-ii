/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.list.view.IItemView;
import org.jamesii.gui.utils.list.view.plugintype.ViewFactory;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class JItemList extends JPanel implements ItemListener {

  private static final long serialVersionUID = 4922624145274822197L;

  /**
   * The current view.
   */
  private JComponent view;

  /**
   * The data to be shown in the view.
   */
  private IViewableItem[] data;

  /**
   * The pane around the view (to make the view scrollable).
   */
  private JScrollPane pane;

  /**
   * The label shown on the information panel (lower border).
   */
  private JLabel infoLabel;

  /**
   * The list selection model shared among the views.
   */
  private ListSelectionModel selectionModel;

  private boolean dragEnabled = false;

  private String[] properties;

  public static final DataFlavor ITEM_FLAVOR = new DataFlavor(
      IViewableItem.class, "An item");

  public JItemList() {
    super();
  }

  // /**
  // *
  // * @param abstractFactory
  // */
  // public <F extends Factory> JItemList(Class<F> abstractFactory) {
  // super();
  // // List<F> factories =
  // // SimSystem.getRegistry().getFactories(abstractFactory);
  //
  // List<IPluginData> infos =
  // SimSystem.getRegistry().getPlugins(
  // SimSystem.getRegistry().getAbstractFactoryForBaseFactory(
  // abstractFactory));
  //
  // this.add(list);
  //
  //
  // list.setListData(infos.toArray());
  // list.setPreferredSize(new Dimension(675,100));
  // list.setCellRenderer(new SmallIconCellRenderer());
  // list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
  // list.setVisibleRowCount(-1);
  // }

  /**
   * 
   * @param abstractFactory
   */
  public <F extends Factory> JItemList(IViewableItem[] data, String[] properties) {
    super();

    JPanel controlBar = new JPanel();
    controlBar.setLayout(new BorderLayout());

    JPanel infoBar = new JPanel();
    infoBar.setLayout(new BorderLayout());
    infoLabel = new JLabel(data.length + " entries");
    infoBar.add(infoLabel, BorderLayout.WEST);

    this.properties = new String[properties.length];
    System.arraycopy(properties, 0, this.properties, 0, properties.length);

    List<ViewFactory> views =
        SimSystem.getRegistry().getFactories(ViewFactory.class);

    JComboBox select = new JComboBox(views.toArray());
    select.setSelectedIndex(0);
    select.addItemListener(this);
    controlBar.add(select, BorderLayout.EAST);

    pane = new JScrollPane();
    setLayout(new BorderLayout());

    add(controlBar, BorderLayout.NORTH);
    add(infoBar, BorderLayout.SOUTH);
    this.add(pane, BorderLayout.CENTER);

    this.data = data;

    createView((ViewFactory) select.getSelectedItem());

    // for (ViewFactory view : views) {
    // JButton btn = new JButton (view.getName().substring(2));
    // btn.setSize(20, 20);
    // controlBar.add(btn);
    // }

  }

  /**
   * Add a listener to the list that's notified each time a change to the
   * selection occurs.
   * 
   */
  public void addListSelectionListener(ListSelectionListener x) {
    ((IItemView) view).addListSelectionListener(x);
  }

  /**
   * Get the selected indices.
   * 
   * @return
   */
  public int[] getSelectedIndices() {
    return ((IItemView) view).getSelectedIndices();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getStateChange() == ItemEvent.SELECTED) {

      pane.remove(view);
      createView(((ViewFactory) e.getItem()));
    }
  }

  /**
   * Set the selection mode. See the ListSelectionModel documentation on further
   * details.
   * 
   * @param selectionMode
   */
  public void setSelectionMode(int selectionMode) {
    ((IItemView) view).setSelectionMode(selectionMode);
  }

  private void createView(ViewFactory viewFactory) {

    ParameterBlock pb = new ParameterBlock();
    pb.addSubBl(ViewFactory.DATA, data);
    pb.addSubBl(ViewFactory.PROPERTIES, properties);

    view = viewFactory.create(pb);
    pane.setViewportView(view);

    // ((IItemView) view).setTransferHandler(new ItemListTransferHandler());

    if (selectionModel == null) {
      selectionModel = ((IItemView) view).getSelectionModel();
    }

    ((IItemView) view).setSelectionModel(selectionModel);
    ((IItemView) view).setDragEnabled(dragEnabled);
    ((IItemView) view).addListSelectionListener(new SelectionListener());
    validate();

  }

  public void setDragEnabled(boolean b) {
    dragEnabled = b;
    ((IItemView) view).setDragEnabled(dragEnabled);
  }

  /**
   * Internal class for supporting drag and drop operations.
   * 
   * @author Jan Himmelspach
   * 
   */
  private static class ItemListTransferHandler extends TransferHandler {

    private static final long serialVersionUID = 2477458372598682514L;

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

      boolean result = super.canImport(comp, transferFlavors);

      if (!result) {

        for (int i = 0; i < transferFlavors.length; i++) {
          if (transferFlavors[i].equals(JItemList.ITEM_FLAVOR)) {
            return true;
          }
        }
      }

      return result;
    }

  }

  /**
   * 
   * @author Jan Himmelspach
   * 
   */
  private final class SelectionListener implements ListSelectionListener {
    @Override
    public void valueChanged(ListSelectionEvent e) {

      int[] selection = ((IItemView) e.getSource()).getSelectedIndices();

      if (selection != null) {
        if (selection.length == 1) {
          infoLabel.setText(data[selection[0]].getLabel());
        } else {
          infoLabel.setText(selection.length + " selected");
        }

      } else {
        infoLabel.setText(data.length + " entries");
      }

    }
  }

}
