/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.editor.ca.parametersetup;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.jamesii.gui.model.editor.ca.cellrenderer.ICACellRenderer;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.ICAValueMapper;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.IMappingChangedListener;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.CAValueMapperEditorManager;
import org.jamesii.gui.model.editor.ca.cellrenderer.valuemapper.editor.ICAValueMapperEditor;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.ListenerSupport;
import org.jamesii.model.carules.symbolic.ISymbolicCAModel;
import org.jamesii.model.carules.symbolic.ISymbolicCAModelInformation;

/**
 * A list of CA states, based on a CA Model. The list automatically provides a
 * context menu for items to edit value mappings.
 * 
 * @author Johannes Rössel
 */
public class JStateList extends JList implements IMappingChangedListener {

  /**
   * A {@link ListModel} based on a CA Model. This class does not observe the CA
   * Model and won't send any changes in the model to the {@link JList} as the
   * CA Model is supposed to be static and won't change anymore after it has
   * been created.
   * 
   * @author Johannes Rössel
   */
  private class StateListModel implements ListModel {

    /** Registered {@link ListDataListener}s. */
    private ListenerSupport<ListDataListener> listeners =
        new ListenerSupport<>();

    private List<String> states;

    /**
     * Initializes a new instance of the {@link StateListModel} class with the
     * given CA Model.
     * 
     * @param caModel
     *          The CA model.
     */
    public StateListModel(List<String> states) {
      this.states = states;
    }

    @Override
    public Object getElementAt(int index) {
      return states.get(index);
    }

    @Override
    public int getSize() {
      return states.size();
    }

    @Override
    public void addListDataListener(ListDataListener l) {
      listeners.addListener(l);
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
      listeners.removeListener(l);
    }
  }

  /** Serial version ID. */
  private static final long serialVersionUID = 1L;

  /** The CA cell renderer. */
  private ICACellRenderer caCellRenderer;

  private ISymbolicCAModel caModel;

  /**
   * Initializes a new instance of this class. Private to prevent instantiation.
   */
  private JStateList() {
    super();
    this.setFixedCellHeight(25);
  }

  /**
   * Instantiates a new {@link JStateList} from the given
   * {@link ICACellRenderer} and {@link ISymbolicCAModel}.
   * 
   * @param cr
   *          The CA cell renderer to use. This will determine how the state
   *          previews are rendered in the list.
   * @param model
   *          The symbolic CA model to use. This contains information about what
   *          states need to be displayed in this list.
   */
  public JStateList(ICACellRenderer cr, ISymbolicCAModel model) {
    this();
    if (cr != null) {
      this.setCACellRenderer(cr);
    }
    this.setCAModel(model);

    // not nice, but since isPopupTrigger doesn't care about the keyboard ...
    this.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
          int index = JStateList.this.getSelectedIndex();
          Rectangle r = JStateList.this.getCellBounds(index, index);
          setUpPopupMenu(index).show(JStateList.this, r.x, r.y);
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }

      @Override
      public void keyTyped(KeyEvent e) {
      }
    });

    this.addMouseListener(new MouseAdapter() {
      private void handle(MouseEvent e) {
        if (e.isPopupTrigger()) {
          int index = locationToIndex(e.getPoint());
          JStateList.this.setSelectedIndex(index);
          setUpPopupMenu(index).show(JStateList.this, e.getX(), e.getY());
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
        handle(e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        handle(e);
      }
    });
  }

  /**
   * Sets a new CA cell renderer for this list.
   * 
   * @param cr
   *          The new CA cell renderer.
   */
  public void setCACellRenderer(ICACellRenderer cr) {
    if (caCellRenderer != null && caCellRenderer.getMappers() != null) {
      for (ICAValueMapper m : caCellRenderer.getMappers()) {
        m.removeMappingChangedListener(this);
      }
    }
    caCellRenderer = cr;
    this.setCellRenderer(new StateListCellRenderer(cr));
    for (ICAValueMapper m : caCellRenderer.getMappers()) {
      m.addMappingChangedListener(this);
    }
  }

  /**
   * Returns the currently used CA cell renderer for this list.
   * 
   * @return The CA cell renderer currently set.
   */
  public ICACellRenderer getCACellRenderer() {
    return ((StateListCellRenderer) this.getCellRenderer()).getCACellRenderer();
  }

  /**
   * Sets a new CA model for this list.
   * 
   * @param caModel
   *          The new model.
   */
  public void setCAModel(ISymbolicCAModel caModel) {
    this.caModel = caModel;
    ISymbolicCAModelInformation modelInfo = caModel.getAsDataStructure();
    this.setModel(new StateListModel(modelInfo.getStates()));
  }

  /**
   * Retrieves the currently set CA model for this list.
   * 
   * @return The CA model currently in use.
   */
  public ISymbolicCAModel getCAModel() {
    return caModel;
  }

  /**
   * Helper method to construct a popup menu for a given item. This menu allows
   * to change the mappings for inputs of the currently active
   * {@link ICACellRenderer}.
   * 
   * @param itemIndex
   *          The index of the item for which the popup menu is created.
   * @return A {@link JPopupMenu} that allows for changing value mappings.
   */
  private JPopupMenu setUpPopupMenu(final int itemIndex) {
    JPopupMenu m = new JPopupMenu();

    final ICACellRenderer cr = getCACellRenderer();
    final List<ICAValueMapper> mappers = cr.getMappers();

    for (ICAValueMapper vm : mappers) {
      vm.addMappingChangedListener(this);
    }

    for (int i = 0; i < cr.getInputs().size(); i++) {
      Class<?> c = cr.getInputs().get(i);
      final int j = i;

      final ICAValueMapperEditor editor =
          CAValueMapperEditorManager.getValueMapperEditorFor(c);

      JMenuItem mi =
          new JMenuItem("Edit " + cr.getInputName(i) + " for "
              + getModel().getElementAt(itemIndex) + " ...");

      if (editor == null) {
        mi.setEnabled(false);
      } else {
        mi.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            editor.editMappingFor(cr.getInputName(j), itemIndex, mappers.get(j));
          }
        });
      }

      m.add(mi);
    }

    return m;
  }

  @Override
  public void mappingChanged() {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {
      @Override
      public void run() {
        repaint();
      }
    });
  }
}
