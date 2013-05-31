/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jamesii.SimSystem;
import org.jamesii.gui.decoration.Decorator;
import org.jamesii.gui.decoration.MirrorDecoration;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.ImagePanel;
import org.jamesii.gui.utils.animator.IAnimatorListener;
import org.jamesii.gui.utils.animator.LinearInterpolator;
import org.jamesii.gui.utils.animator.SwingAnimator;

/**
 * Simple splash screen used during application startup. It is able to display a
 * background image and puts if desired the application title on the lower right
 * corner of that background image. Additionally a progress bar and progress
 * label is added for displaying progress information.
 * 
 * @author Stefan Rybacki
 * 
 */
public class SplashScreen extends JFrame implements IProgressListener,
    IAnimatorListener, WindowListener {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -6309461677440423355L;

  /**
   * progress bar showing the current progress
   */
  private final JProgressBar progressBar;

  /**
   * label displaying current task information or progress information
   */
  private final JLabel progressLabel;

  /**
   * variable for fading animation
   */
  private Color oldColor;

  /**
   * variable for fading animation
   */
  private Color newColor;

  /**
   * the animator used for fading (could also have used TransitionDecoration for
   * fading)
   */
  private SwingAnimator animator;

  /**
   * label showing the splash screen title
   */
  private JLabel titleLabel;

  /**
   * background panel showing the specified splash screen image
   */
  private final ImagePanel imagePanel;

  /**
   * Creates a splash screen using the specified information.
   * 
   * @param image
   *          the image to display as splash screen background
   * @param title
   *          the title
   * @param version
   *          the version
   * @param vendor
   *          the vendor
   * @param showTitle
   *          flag whether to show title or not (this is because the image might
   *          already contain the title)
   */
  public SplashScreen(Image image, String title, String version, String vendor,
      boolean showTitle) {
    super();
    setUndecorated(true);
    setBackground(new Color(0, 0, 0, 0));
    setResizable(false);
    setAlwaysOnTop(false);
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setMinimumSize(new Dimension(480, 320));
    setLocationRelativeTo(null);

    addWindowListener(this);

    getContentPane().setLayout(new BorderLayout());

    JPanel splashPanel = new JPanel(new BorderLayout());

    progressLabel = new JLabel();
    progressLabel.setBorder(new EmptyBorder(2, 5, 2, 5));
    imagePanel = new ImagePanel(image);
    imagePanel.setLayout(new BorderLayout());
    imagePanel.setBackground(new Color(0, 0, 0, 0));
    imagePanel.add(progressLabel, BorderLayout.PAGE_END);

    JPanel appInfoPanel = new JPanel(new BorderLayout());
    appInfoPanel.setOpaque(false);
    appInfoPanel.setBorder(new EmptyBorder(2, 5, 2, 5));
    appInfoPanel.setBackground(new Color(0, 0, 0, 0));

    JLabel vendorLabel = new JLabel(vendor);
    JLabel versionLabel = new JLabel(version);

    appInfoPanel.add(vendorLabel, BorderLayout.LINE_START);
    appInfoPanel.add(versionLabel, BorderLayout.LINE_END);

    imagePanel.add(appInfoPanel, BorderLayout.PAGE_START);
    imagePanel.setImageAlpha(0f);

    if (image == null || showTitle) {
      titleLabel = new JLabel(title);
      titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      titleLabel.setVerticalAlignment(SwingConstants.BOTTOM);
      titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 42f));
      titleLabel.setBorder(new EmptyBorder(0, 25, 0, 10));
      titleLabel.setForeground(new Color(0f, 0f, 0f, 0f));

      // animate titleLabel
      animator = new SwingAnimator(2500, new LinearInterpolator());
      animator.addAnimatorListener(this);

      imagePanel.add(titleLabel, BorderLayout.CENTER);
    }

    splashPanel.add(imagePanel, BorderLayout.CENTER);

    progressBar = new JProgressBar(0, 100);
    progressBar.setIndeterminate(true);
    splashPanel.add(progressBar, BorderLayout.PAGE_END);
    splashPanel.setBorder(new LineBorder(Color.DARK_GRAY, 1, true));

    Decorator deco = new Decorator(splashPanel, new MirrorDecoration(75, 0));
    getContentPane().add(deco, BorderLayout.CENTER);
  }

  @Override
  public final void progress(Object sender, final float progress) {
    // with the first call to this function we switch from indeterminate mode to
    // determinate mode
    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          progressBar.setIndeterminate(false);
          progressBar.setValue((int) (progress * 100));
        }
      });
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    }
  }

  @Override
  public final void taskInfo(Object source, final String info) {
    try {
      BasicUtilities.invokeAndWaitOnEDT(new Runnable() {
        @Override
        public void run() {
          progressLabel.setText(info);
        }
      });
    } catch (InterruptedException | InvocationTargetException e) {
      SimSystem.report(e);
    }
  }

  @Override
  public final void animate(double frac) {
    Color f =
        new Color(
            (int) ((newColor.getRed() - oldColor.getRed()) * frac + oldColor
                .getRed()),
            (int) ((newColor.getGreen() - oldColor.getGreen()) * frac + oldColor
                .getGreen()),
            (int) ((newColor.getBlue() - oldColor.getBlue()) * frac + oldColor
                .getBlue()),
            (int) ((newColor.getAlpha() - oldColor.getAlpha()) * frac + oldColor
                .getAlpha()));
    titleLabel.setForeground(f);
    imagePanel.setImageAlpha((float) frac);
  }

  @Override
  public final void started(Object source) {
    oldColor = titleLabel.getForeground();
    newColor = new Color(0f, 0f, 0f, 1f);
  }

  @Override
  public final void stopped() {
    titleLabel.setForeground(newColor);
  }

  @Override
  public final void windowActivated(WindowEvent e) {
  }

  @Override
  public final void windowClosed(WindowEvent e) {
    if (animator != null) {
      animator.abort();
    }
  }

  @Override
  public final void windowClosing(WindowEvent e) {
  }

  @Override
  public final void windowDeactivated(WindowEvent e) {
  }

  @Override
  public final void windowDeiconified(WindowEvent e) {
  }

  @Override
  public final void windowIconified(WindowEvent e) {
  }

  @Override
  public final void windowOpened(WindowEvent e) {
    if (animator != null) {
      animator.start();
    }
  }

  @Override
  public void finished(Object source) {
  }

  @Override
  public void started() {
    started(null);
  }

}
