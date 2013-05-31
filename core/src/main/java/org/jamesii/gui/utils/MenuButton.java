/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.jamesii.SimSystem;

/**
 * Drop down Button with the possibility to spawn {@link JPopupMenu};
 * 
 * @author Valerius Weigandt
 */
public class MenuButton extends JButton implements PropertyChangeListener {

  /** The serialization */
  private static final long serialVersionUID = -6808754503119202691L;

  /** The active color for the arrow */
  private static final Color ACTIVE = new Color(250, 0, 0);

  /** The inactive color for the arrow */
  private static final Color INACTIVE = new Color(0, 0, 0);

  /** The current arrow color */
  private Color arrowColor = INACTIVE;

  /** The PopUp Menu */
  private IMenuButtonModel popUpMenu;

  /** The Arrow */
  private transient Icon arrow = new Icon() {
    private int w = 10;

    private int h = (int) (w / 2.0 * 1.7);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Graphics2D gr = (Graphics2D) g.create();
      gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
          RenderingHints.VALUE_ANTIALIAS_ON);

      gr.setColor(arrowColor);
      int[] a = { x, x + w, x + w / 2 };
      int[] b = { y, y, y + h };
      gr.fillPolygon(a, b, 3);
    }

    @Override
    public int getIconWidth() {
      return w;
    }

    @Override
    public int getIconHeight() {
      return h;
    }
  };

  /** The label for the arrow */
  private JLabel arrowLabel = new JLabel(findIcon());
  {
    arrowLabel.addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        arrowColor = ACTIVE;
      }

      @Override
      public void mouseExited(MouseEvent e) {
        arrowColor = INACTIVE;
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        e.consume();
        popUpMenu.getPopUpMenu().show(MenuButton.this,
            e.getComponent().getParent().getLocation().x,
            MenuButton.this.getHeight());
      }
    });

    // lab.setBorder(new LineBorder(new Color(50, 50, 50)));
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (IMenuButtonModel.ACTION.equals(evt.getPropertyName())) {
      this.setAction((Action) evt.getNewValue());
    }

    if (IMenuButtonModel.MENU.equals(evt.getPropertyName())) {
      popUpMenu = (IMenuButtonModel) evt.getNewValue();
    }
  }

  private Icon findIcon() {
    UIDefaults defaults = UIManager.getDefaults();
    Enumeration<Object> iterator = defaults.keys();

    while (iterator.hasMoreElements()) {
      Object key = iterator.nextElement();

      if (key.toString().equals("Table.descendingSortIcon")) {
        // System.out.println("found");
        return UIManager.getIcon(key);
      }
    }
    return arrow;

  }

  /**
   * Sets new {@link IMenuButtonModel}.
   * 
   * @param menuButtonModel
   *          The model to set.
   */
  final void setMenuButtonModel(IMenuButtonModel menuButtonModel) {
    popUpMenu = menuButtonModel;
    popUpMenu.addPropertyChangeListener(this);
    this.setAction(popUpMenu.getDefaultAction());
  }

  /**
   * Creates a menu button.
   * 
   * @param menuButtonModel
   *          The model to set.
   */
  public MenuButton(IMenuButtonModel menuButtonModel) {
    super();
    setMenuButtonModel(menuButtonModel);
    this.setAction(menuButtonModel.getDefaultAction());

    Dimension size = this.getPreferredSize();

    // setting layout
    setLayout(new BorderLayout());
    JPanel right = new JPanel();
    right.setOpaque(false);
    right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
    right.add(Box.createVerticalGlue());
    right.add(arrowLabel);
    right.add(Box.createVerticalGlue());
    add(right, BorderLayout.EAST);

    // size.width += right.getPreferredSize().width+10;
    size.width += arrow.getIconWidth() + 10;
    this.setPreferredSize(size);

    setHorizontalAlignment(LEFT);
    Insets ins = this.getMargin();
    ins.right = 5;
    this.setMargin(ins);

  }

  @SuppressWarnings("all")
  /**
   * The main
   * 
   * @param args
   *          the args
   * @throws UnsupportedLookAndFeelException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws ClassNotFoundException
   */
  public static void main(String[] args) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException,
      UnsupportedLookAndFeelException {

    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

    AbstractMenuButtonModel popUp = new AbstractMenuButtonModel() {

      Action a1 = new AbstractAction("a1") {
        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println("a1");
          defaultAction = a2;
          fireDefaultActionChanged();
        }
      };

      Action a2 = new AbstractAction("a2") {

        @Override
        public void actionPerformed(ActionEvent e) {
          System.out.println("a2");
          defaultAction = a1;
          fireDefaultActionChanged();
        }
      };

      @Override
      public JPopupMenu getPopUpMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.add(a1);
        menu.add(a2);

        return menu;
      }

      Action defaultAction = a1;

      @Override
      public Action getDefaultAction() {

        return defaultAction;
      }
    };
    final MenuButton b = new MenuButton(popUp);

    final JFrame f = new JFrame("Test MenuButton");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setLayout(new BorderLayout());
    // b.setPreferredSize(new Dimension(100,1000));
    f.add(b, BorderLayout.PAGE_END);

    final JComboBox box = new JComboBox(UIManager.getInstalledLookAndFeels());
    box.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          LookAndFeelInfo info = (LookAndFeelInfo) box.getSelectedItem();
          UIManager.setLookAndFeel(info.getClassName());
          SwingUtilities.updateComponentTreeUI(f);
          f.invalidate();
          f.repaint();
        } catch (Exception e1) {
          SimSystem.report(e1);
        }
      }
    });

    f.add(box, BorderLayout.PAGE_START);

    f.pack();

    f.setVisible(true);
  }
}
