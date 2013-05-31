/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.gui.utils;

import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * This class implements a custom text field which shows elements in the
 * selection-part of the comboBox belonging to the input
 * 
 * @author Enrico Seib
 * @author Stefan Rybacki
 */
public class AutoCompletionTextField extends JTextField implements
    FocusListener, DocumentListener {

  private static final int COLUMNS = 10;

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class FieldKeyListener extends java.awt.event.KeyAdapter {
    @Override
    public void keyPressed(java.awt.event.KeyEvent evt) {

      if (evt.getKeyCode() == KeyEvent.VK_ESCAPE && popup != null) {
        showPopUp = false;
        hidePopup();
        return;
      }

      if ((evt.getKeyCode() == KeyEvent.VK_SPACE && evt.isControlDown())) {
        showPopUp = true;
        showPopup();
        return;
      }

      if (evt.getKeyCode() == KeyEvent.VK_DOWN && popup == null) {
        showPopUp = true;
        showPopup();
        changedUpdate(null);
      }

      if ((evt.getKeyCode() == KeyEvent.VK_DOWN) && popup != null) {
        if (list.getSelectedIndex() == -1) {
          list.setSelectedIndex(0);
        } else {
          int i = list.getSelectedIndex();
          i++;
          if (i < filteredModel.getSize()) {
            list.setSelectedValue(filteredModel.getElementAt(i), true);
          }
        }
        return;
      }

      if ((evt.getKeyCode() == KeyEvent.VK_UP) && popup != null) {
        int i = list.getSelectedIndex();
        if (i > 0) {
          i--;
          list.setSelectedValue(filteredModel.getElementAt(i), true);
        }
        return;
      }

      if (evt.getKeyCode() == KeyEvent.VK_ENTER && popup != null
          && list.getSelectedValue() != null) {
        showPopUp = true;
        selectItem(list.getSelectedIndex());
        return;
      }

    }
  }

  /**
   * 
   * LICENCE: JAMESLIC
   * 
   * @author Stefan Rybacki
   * 
   */
  private final class FieldComponentListener extends ComponentAdapter {
    @Override
    public void componentHidden(ComponentEvent e) {
      hidePopup();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
      hidePopup();
      if (hasFocus()) {
        showPopup();
      }
    }

    @Override
    public void componentResized(ComponentEvent e) {
      hidePopup();
      if (hasFocus()) {
        showPopup();
      }
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
      if (hasFocus()) {
        showPopup();
      }
    }
  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -4498942924940775215L;

  /**
   * True if auto completion popup should be filtered according to value
   * entered, else false
   */
  private boolean autoCompletionPopUp;

  /**
   * The internally used text filter.
   */
  private IFilter<String> filter;

  /**
   * The pop up menu
   */
  private Popup popup;

  /**
   * Internal list of items of type {@link JList}
   */
  private JList list;

  /**
   * Flag indicating if popUp should be closed permanently
   */
  private boolean showPopUp;

  /**
   * The filtered model that is used in the list of proposed values.
   */
  private FilteredListModel<String> filteredModel;

  /**
   * Constructor.
   * 
   * @param columns
   *          number of columns of the popup
   * @param cbModel
   *          the list model providing alternatives for input values
   * @param autoCompletePopUp
   *          {@code true} if auto completion popup should be filtered according
   *          to entered values
   */
  public AutoCompletionTextField(int columns, ListModel cbModel,
      boolean autoCompletePopUp) {
    super(columns);

    this.showPopUp = true;
    this.autoCompletionPopUp = autoCompletePopUp;

    filter = new TextFilter(null);
    filteredModel = new FilteredListModel<>(cbModel, filter);
    list = new JList(filteredModel);

    list.addFocusListener(this);
    list.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 1) {
          selectItem(list.getSelectedIndex());
        }
      }
    });

    getDocument().addDocumentListener(this);

    addFocusListener(this);

    addAncestorListener(new AncestorListener() {

      @Override
      public void ancestorAdded(AncestorEvent event) {
      }

      @Override
      public void ancestorMoved(AncestorEvent event) {
        hidePopup();
        if (hasFocus()) {
          showPopup();
        }
      }

      @Override
      public void ancestorRemoved(AncestorEvent event) {
      }

    });

    addComponentListener(new FieldComponentListener());

    addKeyListener(new FieldKeyListener());

  }

  /**
   * Helper method that selects item from list and puts it into text field.
   * 
   * @param selectedIndex
   *          the selected index
   */
  private void selectItem(int selectedIndex) {
    if (selectedIndex >= 0 && selectedIndex < filteredModel.getSize()) {
      list.clearSelection();
      setText(filteredModel.getElementAt(selectedIndex).toString());
      setCaretPosition(getText().length());
    }
  }

  /**
   * Default Constructor. Number of columns of the popUp is set to 10.
   * 
   * @param cbModel
   *          the list model providing alternatives for input values
   * @param autoCompletePopUp
   *          {@code true} if auto completion popup should be filtered according
   *          to entered values
   */

  public AutoCompletionTextField(ListModel cbModel, boolean autoCompletePopUp) {
    this(COLUMNS, cbModel, autoCompletePopUp);
  }

  /**
   * Specifies whether the suggested values in the popup are filtered according
   * to specified filter set e.g., in {@link #setFilter(IFilter)}.
   * 
   * @param filterPopUp
   *          {@code true} if the pop up should be filtered automatically with
   *          suitable entries (entries contain the text of the input line) of
   *          the list, else {@code false} in which case the popup shows all
   *          possible values without filtering according to entered text
   * @see #setFilter(IFilter)
   * @see #getFilter()
   */
  public void setFilterPopUp(boolean filterPopUp) {

    this.autoCompletionPopUp = filterPopUp;
    if (!this.autoCompletionPopUp) {
      filter = new TextFilter(null);
    }
    showPopup();
  }

  @Override
  public void focusGained(FocusEvent e) {
    showPopup();
  }

  @Override
  public void focusLost(FocusEvent e) {
    if (popup != null
        && (e == null || e.getOppositeComponent() != list || e
            .getOppositeComponent() == this)) {
      hidePopup();
    }
  }

  @Override
  public void changedUpdate(DocumentEvent e) {
    showPopup();

    filter.setFilterValue(autoCompletionPopUp ? getText() : null);

    checkPopUpToClose();
  }

  @Override
  public void insertUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  @Override
  public void removeUpdate(DocumentEvent e) {
    changedUpdate(e);
  }

  /**
   * Checks if {@link #list} contains only one element left, if so the popup is
   * not shown
   */
  private void checkPopUpToClose() {
    if (filteredModel.getSize() < 1
        || (filteredModel.getSize() == 1 && getText().toLowerCase(
            Locale.getDefault()).equals(
            filteredModel.getElementAt(0).toString()
                .toLowerCase(Locale.getDefault())))) {
      hidePopup();
    }
  }

  /**
   * Helper method that shows the popup if not already visible and
   * {@link #showPopUp} is {@code true}.
   */
  private void showPopup() {
    if (isDisplayable() && showPopUp && popup == null) {
      Point locationOnScreen = getLocationOnScreen();

      popup = null;
      popup =
          PopupFactory.getSharedInstance().getPopup(this,
              new JScrollPane(list), locationOnScreen.x,
              locationOnScreen.y + getHeight());
      popup.show();
    }
  }

  /**
   * Helper method to hide the popup if visible.
   */
  private void hidePopup() {
    if (popup != null) {
      popup.hide();
    }
    popup = null;
    list.clearSelection();
  }

  /**
   * Changes the default used filter to the one specified.
   * 
   * @param filter
   *          the filter to set
   */
  public void setFilter(IFilter<String> filter) {
    this.filter = filter;
    filteredModel.setFilter(filter);
    if (hasFocus()) {
      showPopup();
    }
  }

  /**
   * Returns the specified filter.
   * 
   * @return the filter
   */
  public IFilter<String> getFilter() {
    return filter;
  }

}
