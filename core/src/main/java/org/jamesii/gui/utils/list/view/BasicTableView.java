/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class BasicTableView extends JTable implements IItemView {

  private static final long serialVersionUID = -7216795003816484133L;

  /**
   * List of selection listeners.
   */
  private List<ListSelectionListener> listeners = new ArrayList<>();

  public BasicTableView() {
    super();
    getSelectionModel().addListSelectionListener(
        new ViewListSelectionListener(this));
  }

  @Override
  public void addListSelectionListener(ListSelectionListener x) {
    listeners.add(x);
  }

  @Override
  public int[] getSelectedIndices() {
    return getSelectedRows();
  }

  private class ViewListSelectionListener implements ListSelectionListener {

    private Object view;

    public ViewListSelectionListener(Object view) {
      this.view = view;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
      ListSelectionEvent selEv =
          new ListSelectionEvent(view, e.getFirstIndex(), e.getLastIndex(),
              e.getValueIsAdjusting());
      for (ListSelectionListener listener : listeners) {
        listener.valueChanged(selEv);
      }
    }
  }

}
