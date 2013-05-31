/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.james;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.action.ActionManager;

/**
 * Internal used component that can handle {@link IWindow}s and its actions by
 * providing a {@link JToolBar} that provides access to the window's actions
 * 
 * @author Stefan Rybacki
 */
public class JamesWindow extends JPanel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7284593403462161304L;

  /**
   * the managed {@link IWindow}
   */
  private final IWindow content;

  /**
   * the relative action path
   */
  private String rootURL;

  /**
   * the panel containing toolbar and window content
   */
  private JPanel contentPanel;

  /**
   * Creates a new instance using the given {@link IWindow} and relative action
   * path
   * 
   * @param content
   *          the window to manage
   * @param actionPath
   *          the relative action url
   */
  public JamesWindow(IWindow content, String actionPath) {
    super(new BorderLayout());
    this.content = content;

    setFocusable(true);
    rootURL = actionPath;
    if (rootURL == null) {
      rootURL = "";
    }

    if (this.content == null) {
      throw new IllegalArgumentException("Content can't be null!");
    }

    init();
  }

  /**
   * Helper method that initializes the component
   */
  private void init() {
    contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBorder(BorderFactory
        .createLineBorder(SystemColor.controlDkShadow));
    JToolBar toolbar;
    try {
      toolbar = ActionManager.createJToolBarFor(rootURL);
      if (toolbar != null) {
        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        // toolbar.add(Box.createHorizontalGlue(),0);
        contentPanel.add(toolbar, BorderLayout.NORTH);
      }
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      SimSystem.report(e);
    }

    try {
      contentPanel.add(content.getContent(), BorderLayout.CENTER);
    } catch (Throwable e) {
      SimSystem.report(e);
    }

    add(contentPanel, BorderLayout.CENTER);

    SwingUtilities.updateComponentTreeUI(this);
  }

  @Override
  public Dimension getPreferredSize() {
    if (content != null && content.getPreferredSize() != null) {
      return content.getPreferredSize();
    }

    return super.getPreferredSize();
  }

  /**
   * @return the managed {@link IWindow}
   */
  public IWindow getWindow() {
    return content;
  }

}