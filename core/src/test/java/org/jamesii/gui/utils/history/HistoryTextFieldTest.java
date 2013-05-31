/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jamesii.gui.utils.FilteredListModel;
import org.jamesii.gui.utils.history.History;
import org.jamesii.gui.utils.history.HistoryComboBoxModel;
import org.jamesii.gui.utils.history.HistoryTextField;

/**
 * The class HistoryTextFieldTest.
 * 
 * This class is used to test {@link HistoryTextField} with different
 * ComboBoxModels ({@link FilteredListModel} and {@link HistoryComboBoxModel})
 * 
 * @author Enrico Seib
 * 
 */
public class HistoryTextFieldTest extends JPanel {

  private static final long serialVersionUID = 1869819572942792933L;

  /**
   * JTextfield
   */
  private JTextField tf1;

  /**
   * HistoryTextField
   */
  private HistoryTextField htf;

  /**
   * Constructor
   */
  public HistoryTextFieldTest() {
    super();

    tf1 = new JTextField("", 40);
    htf = new HistoryTextField("historyTest", true, 10, History.LATEST);

    JButton button = new JButton("OK");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {

        htf.commit();
        tf1.setText(tf1.getText()
            + " "
            + History.getValues("historyTest", false, History.LATEST, 10)
                .get(0));

      }

    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.add(button);

    tf1.setEditable(false);

    this.add(tf1);
    this.add(buttonPanel);
    htf.setEditable(true);
    this.add(htf);
    this.setVisible(true);
  }

}
