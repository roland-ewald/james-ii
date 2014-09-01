/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.history.History;

/**
 * Class to test HistoryTextField and it's document listener
 * HistoryTFListenerComboBox and HistoryTFListenerTextField
 * 
 * @author Enrico Seib
 * 
 */
public class SimpleAutoText {

  /**
   * TextField for text input
   */
  private JTextField tf;

  /**
   * TextField to check function
   */
  private JTextField tf1;

  /**
   * TextField to check function
   */
  private JTextField tf2;

  /**
   * Default constructor
   */
  public SimpleAutoText() {
    // super();
  }

  /**
   * 
   * @param panel
   * 
   * @return a panel with 2 textfields and an OK-button
   */
  public JPanel formLayout(JPanel panel) {

    panel.setLayout(new GridLayout(4, 1));

    // This textField sends TextListnerEvents
    tf = new JTextField("", 40);

    tf1 = new JTextField("", 40);

    tf2 = new JTextField("", 40);

    tf.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        tf = (JTextField) e.getSource();
        // System.out.println("tf: character: " + e.getKeyChar());
      }

      @Override
      public void keyReleased(KeyEvent e) {
        // System.out.println("tf: textValueChanged: " + tf.getText());
      }

    });

    panel.add(tf);

    JButton button = new JButton("OK");
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent event) {
        History.putValueIntoHistory("testtext", tf.getText());
        tf1.setText(tf.getText());
        tf.setText("");
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
    buttonPanel.add(button);
    panel.add(buttonPanel);

    tf1.setEditable(false);
    panel.add(tf1);

    tf2.setEditable(true);
    tf2.getDocument().addDocumentListener(new MyListener());
    tf2.getDocument().putProperty("tf2", "Text Field");
    tf2.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {

        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
        }
      }

      @Override
      public void keyPressed(KeyEvent e) {
        tf = (JTextField) e.getSource();
      }

      @Override
      public void keyReleased(KeyEvent e) {

        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
        }
      }

    });

    panel.add(tf2);

    return panel;
  }

  /**
   * 
   * @param id
   * @return list of Strings
   */
  private List<String> getListFromHistory(String id) {
    List<String> list = new ArrayList<>();
    list.addAll(History.getValues(id, false, History.UNSORTED, History.ALL));
    return list;
  }

  /**
   * @author es074
   * 
   */
  private class MyListener implements DocumentListener {

    /**
     * null if no text was selected to add
     */
    String selectedText = "";

    /**
     * position of the cursor before the selected text was added
     */
    int caretPos = 0;

    @Override
    public void removeUpdate(DocumentEvent event) {
      // System.out.println("removeUpdate = ");

      tf2.addKeyListener(new KeyListener() {
        @Override
        public void keyTyped(KeyEvent e) {
          if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            // System.out.println("removeUpdate with Back space key pressed");

          }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(final KeyEvent e) {

          if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            System.out.println("removeUpdate with Back space key released");
            System.out.println(selectedText);
            // System.out.println(tf2.getSelectionStart());
            // System.out.println(tf2.getSelectionEnd());
            // System.out.println(tf2.getSelectedText());
            // tf2.setCaretPosition(tf2.getSelectionStart());
            if (caretPos >= 1 && selectedText != null) {
              BasicUtilities.invokeLaterOnEDT(new Runnable() {
                @Override
                public void run() {
                  tf2.dispatchEvent(e);
                  e.consume();
                }
              });
            }
          }
        }
      });
    }

    @Override
    public void insertUpdate(DocumentEvent event) {

      final Document doc = event.getDocument();
      try {
        // System.out.println("TF2: textValueChanged: " + doc.getText(0,
        // doc.getLength()));

        String text = doc.getText(0, doc.getLength());
        List<String> keyList = getListFromHistory("testtext");
        for (String item : keyList) {
          if (item.equals(text)) {
            break;
          }
          if (text != "" && item.startsWith(text)) {
            Runnable setAndSelect = new Runnable() {
              @Override
              public void run() {
                try {
                  int docLength = doc.getLength();
                  caretPos = tf2.getCaretPosition();

                  tf2.setText(item);
                  tf2.select(docLength, item.length());
                  selectedText = tf2.getSelectedText();

                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
            };
            SwingUtilities.invokeLater(setAndSelect);
            break;
          }
        }
      } catch (BadLocationException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
      // System.out.println("changedUpdate = ");
    }
  }

}
