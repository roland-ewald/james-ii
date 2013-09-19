/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jamesii.gui.application.ApplicationDialog;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.utils.CheckBoxGroup;
import org.jamesii.gui.utils.list.IViewableItem;
import org.jamesii.gui.utils.list.view.item.IProperty;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class PropertySelectionDialog extends ApplicationDialog implements
    ActionListener {

  private static final long serialVersionUID = -8507145024848240217L;

  private CheckBoxGroup group;

  private boolean apply = false;

  private final JButton setButton = new JButton("Apply");

  private final JButton cancelButton = new JButton("Cancel");

  public PropertySelectionDialog(Window owner, String title,
      List<IProperty<?>> properties, String[] propertiesSelected) {
    super(owner, title);

    setButton.setActionCommand("Apply");
    cancelButton.setActionCommand("Cancel");

    setButton.addActionListener(this);
    cancelButton.addActionListener(this);
    getRootPane().setDefaultButton(setButton);

    PropertiesCheckBoxGroupModel pcgm;

    group =
        new CheckBoxGroup<>(pcgm = new PropertiesCheckBoxGroupModel(properties));

    // do magic
    group.setSelectionModel(pcgm.getSelectionModel());

    if (propertiesSelected != null) {
      List<IProperty<?>> selProps = new ArrayList<>();
      for (String pN : propertiesSelected) {
        for (IProperty<?> p : properties) {
          if (p.getName().equals(pN)) {
            selProps.add(p);
            break;
          }
        }
      }

      pcgm.setSelectedProperties(selProps);

    }

    JScrollPane groupScroller = new JScrollPane(group);
    groupScroller.setPreferredSize(new Dimension(250, 80));
    groupScroller.setAlignmentX(LEFT_ALIGNMENT);

    setModal(true);

    // Put everything together, using the content pane's BorderLayout.
    Container contentPane = getContentPane();
    contentPane.add(groupScroller, BorderLayout.CENTER);
    // contentPane.add(buttonPane, BorderLayout.PAGE_END);

    JPanel buttonPanel = new JPanel();
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
    buttonPanel.add(setButton);
    buttonPanel.add(cancelButton);

    // Initialize values.
    // setValue(initialValue);
    pack();
    // setLocationRelativeTo(locationComp);

  }

  /**
   * Return the selected/checked items.
   * 
   * @return a list of selected items
   */
  public List<IProperty<? extends IViewableItem>> getSelectedItems() {
    return group.getSelectedItems();
  }

  /**
   * {@link javax.swing.JDialog#JDialog()}
   */
  public PropertySelectionDialog() {
    this(WindowManagerManager.getWindowManager() != null ? WindowManagerManager
        .getWindowManager().getMainWindow() : null, null, null, null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == setButton) {
      apply = true;
      setVisible(false);
    }
    if (e.getSource() == cancelButton) {
      setVisible(false);
    }

  }

  public boolean getApply() {
    return apply;
  }

}
