/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jamesii.gui.utils.ImagePanel;

/**
 * Simple welcome page that can be used in a wizard. One can specify the title,
 * the sub title, the welcome message (in HTML if you want line breaks and so
 * on), the wizard page icon as well as the image displayed to the left. It also
 * enables only the next button and no back button, so you can only use it as
 * first page.
 * 
 * @author Stefan Rybacki
 */
public class WelcomePage extends AbstractWizardPage {
  /**
   * icon to display as page icon (top right)
   */
  private Icon pageIcon;

  /**
   * welcome image (displayed on the left)
   */
  private Image welcomeImage;

  /**
   * sub title
   */
  private String subTitle;

  /**
   * title
   */
  private String title;

  /**
   * welcome message
   */
  private String welcomeMessage;

  /**
   * alternative to the welcome label, if not null it is used instead of the
   * welcome label
   */
  private JComponent welcomeComponent = null;

  /**
   * Creates a standard welcome page for a wizard. It supports a welcome message
   * as well as a welcome image.
   * 
   * @param title
   *          page's title
   * @param subTitle
   *          the page's sub title
   * @param welcomeMessage
   *          the welcome message
   * @param welcomeImage
   *          the welcome image
   * @param pageIcon
   *          the page's icon
   */
  public WelcomePage(String title, String subTitle, String welcomeMessage,
      Image welcomeImage, Icon pageIcon) {
    this.welcomeImage = welcomeImage;
    this.pageIcon = pageIcon;
    this.title = title;
    this.subTitle = subTitle;
    this.welcomeMessage = welcomeMessage;
  }

  /**
   * Creates a standard welcome page for a wizard. It supports a welcome
   * component that is displayed where usually a welcome message is displayed as
   * well as a welcome image.
   * 
   * @param title
   *          page's title
   * @param subTitle
   *          the page's sub title
   * @param welcomeComponent
   *          the welcome component
   * @param welcomeImage
   *          the welcome image
   * @param pageIcon
   *          the page's icon
   */
  public WelcomePage(String title, String subTitle,
      JComponent welcomeComponent, Image welcomeImage, Icon pageIcon) {
    this(title, subTitle, (String) null, welcomeImage, pageIcon);
    this.welcomeComponent = welcomeComponent;
  }

  @Override
  public void shown(IWizard wizard) {
  }

  @Override
  public Icon getPageIcon() {
    return pageIcon;
  }

  @Override
  public String getSubTitle() {
    return subTitle;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public boolean canBack(IWizard wizard) {
    return false;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    return false;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return true;
  }

  @Override
  public IWizardHelpProvider getHelp() {
    return null;
  }

  @Override
  public Dimension getPreferredSize() {
    return null;
  }

  @Override
  protected JComponent createPage() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout(15, 0));

    if (welcomeImage != null) {
      ImagePanel imagePanel = new ImagePanel(welcomeImage);
      panel.add(imagePanel, BorderLayout.LINE_START);
    }

    if (welcomeComponent != null) {
      panel.add(welcomeComponent, BorderLayout.CENTER);
    } else {
      JLabel label = new JLabel(welcomeMessage);
      label.setBorder(new EmptyBorder(10, 10, 10, 10));
      label.setVerticalAlignment(SwingConstants.TOP);

      panel.add(label, BorderLayout.CENTER);
    }

    return panel;
  }

  @Override
  protected void persistData(IWizard wizard) {
    deletePage();
  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
  }

}
