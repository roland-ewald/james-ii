/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.observation.graphic.output;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Arne Bittig
 * @date 27.11.2012
 */
public class ImageDisplay extends JPanel implements IImageProcessor {

  private static final long serialVersionUID = -2762929703776758616L;

  private final String frameTitle;

  private JFrame frame;

  private JPanel panel;

  private final long targetRefreshInterval; // milliseconds

  private long lastFrameTime;

  private BufferedImage currentImage;

  /** Window border width bonus */
  private static final int WIDTH_BORDER_ADD = 16;

  /** Window border height bonus */
  private static final int HEIGHT_BORDER_ADD = 38;

  /** Number of previously opened windows (for placement of next one) */
  private static int windowCount = 0;

  ImageDisplay(String frameTitle, long targetRefreshInterval) {
    this.frameTitle = frameTitle;
    this.targetRefreshInterval = targetRefreshInterval;
  }

  @Override
  public void init(int width, int height) {
    frame = new JFrame();
    frame.setSize(width + WIDTH_BORDER_ADD, height + HEIGHT_BORDER_ADD);
    initFramePosition(width, height);
    frame.setVisible(true);
    frame.setTitle(frameTitle);

    panel = new JPanel() {

      private static final long serialVersionUID = 3978819499027254796L;

      @Override
      public void paint(Graphics g) {
        g.drawImage(currentImage, 0, 0, null);
      }

    };
    frame.getContentPane().add(panel);
    lastFrameTime = System.currentTimeMillis();
  }

  private synchronized void initFramePosition(int width, int height) {
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    int xFit = (int) (dim.getWidth() / width);
    int winPosIndex = xFit == 0 ? 0 : windowCount % (xFit * 2);
    if (winPosIndex < xFit) {
      // align at top of screen
      frame.setLocation(winPosIndex * (width + WIDTH_BORDER_ADD), 0);
    } else {
      frame.setLocation((int) dim.getWidth() - (winPosIndex - xFit + 1)
          * (width + WIDTH_BORDER_ADD),
          (int) (dim.getHeight() - height - HEIGHT_BORDER_ADD));
    }
    windowCount++;
  }

  @Override
  public boolean needsImageAtTime(Double time) {
    if (frame.isShowing()) {
      long currentTimeMillis = System.currentTimeMillis();
      if (currentTimeMillis - lastFrameTime >= targetRefreshInterval) {
        lastFrameTime = currentTimeMillis;
        return true;
      }
    }
    return false;
  }

  @Override
  public void processImage(BufferedImage img, Double time) {
    this.currentImage = img;
    frame.setTitle("@" + time + ": " + frameTitle);
    // frame.repaint();
    panel.repaint();
  }

  @Override
  public void processFinalImageAndCleanUp(BufferedImage img, Double time) {
    processImage(img, time);
  }
}