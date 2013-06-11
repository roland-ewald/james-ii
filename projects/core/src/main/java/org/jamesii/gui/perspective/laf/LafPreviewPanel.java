/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.AbstractComboBoxModel;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.CheckBoxGroup;
import org.jamesii.gui.utils.ICheckBoxGroupModel;

/**
 * Provides a preview panel for installed and via {@link LafManager} registered
 * look and feels
 * 
 * @author Stefan Rybacki
 */
final class LafPreviewPanel extends JPanel {
  /**
   * Sample list model used in list for preview purposes
   * 
   * @author Stefan Rybacki
   */
  private static class ListModel extends AbstractComboBoxModel<String>
      implements ICheckBoxGroupModel<String> {
    /**
     * Serialization ID
     */
    private static final long serialVersionUID = 454139087742370497L;

    /**
     * Creates a sample list model
     */
    public ListModel() {
      addElement("Item 1");
      addElement("Item 2");
      addElement("Item 3");
      addElement("Item 4");
      addElement("Item 5");
    }

    @Override
    public String getItemAt(int i) {
      return (String) getElementAt(i);
    }

    @Override
    public JComponent getComponentAt(int index) {
      return new JLabel((String) getElementAt(index));
    }

    @Override
    public boolean isEditable(int index) {
      return true;
    }
  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -8279912673265229124L;

  /**
   * the images representing the ui of the look and feel identified by the class
   * name provided as key for the map
   */
  private final Map<String, BufferedImage> images = new HashMap<>();

  /**
   * lock for {@link #initialized}
   */
  private final Lock initializedLock = new ReentrantLock();

  /**
   * flag whether initialization is done
   */
  private boolean initialized = false;

  /**
   * the class name of the current laf that is shown as preview
   */
  private String currentLaf;

  /**
   * flag indicating whether initialization is currently running
   */
  private boolean initializing = false;

  /**
   * Creates a new look and feel preview panel
   */
  public LafPreviewPanel() {
    super();

    // TODO sr137: rework this class by using CellRendererPane for
    // painting

    setLayout(new FlowLayout());
    ListModel listModel = new ListModel();

    JList list = new JList(listModel);
    JComboBox combo = new JComboBox(listModel);
    JTabbedPane tabbed = new JTabbedPane(SwingConstants.TOP);
    JTextArea textArea = new JTextArea("Text Area");
    JButton button = new JButton("Button");
    JTextField textField = new JTextField("Text Field");
    JPasswordField passwordField = new JPasswordField("Password Field");
    JSlider slider = new JSlider();
    ButtonGroup buttonGroup = new ButtonGroup();
    JProgressBar progress = new JProgressBar();

    add(new JScrollPane(list));
    list.setSelectedIndex(0);
    add(button);

    Box vBox = Box.createVerticalBox();
    for (int i = 0; i < listModel.getSize(); i++) {
      JRadioButton radioButton;
      buttonGroup.add(radioButton =
          new JRadioButton((String) listModel.getElementAt(i)));

      vBox.add(radioButton);
      radioButton.setSelected(true);
    }

    add(vBox);
    CheckBoxGroup<String> checkboxGroup = new CheckBoxGroup<>(listModel);
    checkboxGroup.getSelectionModel().setSelectionInterval(0, 0);

    JPanel tabbedPanel = new JPanel(new BorderLayout());
    tabbedPanel.add(checkboxGroup, BorderLayout.CENTER);
    tabbed.addTab("Tabbed Pane", tabbedPanel);

    add(tabbed);

    textArea.setPreferredSize(new Dimension(300, 300));

    JScrollPane pane = new JScrollPane(textArea);
    pane.setPreferredSize(new Dimension(100, 100));
    add(pane);

    vBox = Box.createVerticalBox();

    vBox.add(combo);
    combo.setSelectedIndex(0);

    vBox.add(textField);

    vBox.add(passwordField);

    vBox.add(progress);
    progress.setValue(50);

    add(vBox);

    add(slider);

    setMaximumSize(new Dimension(320, 350));
    setPreferredSize(getMaximumSize());
    setSize(getPreferredSize());
  }

  @Override
  protected synchronized void paintComponent(Graphics g) {
    super.paintComponent(g);

    initializedLock.lock();
    try {
      if (initialized && !initializing) {
        // draw screenshot
        BufferedImage image = images.get(currentLaf);
        if (image != null) {
          g.drawImage(image, 0, 0, null);
        } else {
          g.drawString("No preview", 0, g.getFontMetrics().getHeight());
        }
      } else {
        if (!initializing) {
          init();
        }
      }
    } finally {
      initializedLock.unlock();
    }
  }

  /**
   * Helper method initializing the preview panel
   */
  private void init() {
    // create screenshots of this component using all installed look and
    // feels
    initialized = true;
    initializing = true;
    try {
      currentLaf = UIManager.getLookAndFeel().getClass().getName();
      List<LookAndFeelInfo> lafs = LafManager.getLookAndFeels();

      for (LookAndFeelInfo lafInfo : lafs) {
        try {
          UIManager.setLookAndFeel(lafInfo.getClassName());
          SwingUtilities.updateComponentTreeUI(this);
          revalidate();
          doLayout();
          BufferedImage image =
              BasicUtilities.createCompatibleImage(getWidth(), getHeight(),
                  Transparency.TRANSLUCENT);
          Graphics g = image.getGraphics();
          printAll(g);
          g.dispose();
          images.put(lafInfo.getClassName(), image);
        } catch (Exception t) {
          SimSystem.report(t);
        }
      }

      // switch back to original Look and Feel
      UIManager.setLookAndFeel(currentLaf);

      // remove GUI elements
      removeAll();
      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      SimSystem.report(e);
    }
    initializing = false;
  }

  @Override
  public synchronized Dimension getPreferredSize() {
    Dimension dim = super.getPreferredSize();
    initializedLock.lock();
    try {
      if (initialized) {
        BufferedImage image = images.get(currentLaf);
        if (image != null) {
          dim.width = image.getWidth();
          dim.height = image.getHeight();
        }
      }
    } finally {
      initializedLock.unlock();
    }

    return dim;
  }

  /**
   * Sets the look and feel a preview should be shown for
   * 
   * @param className
   *          the look and feel's class name
   */
  public synchronized void setLookAndFeel(String className) {
    currentLaf = className;
    doLayout();
    repaint();
  }
}
