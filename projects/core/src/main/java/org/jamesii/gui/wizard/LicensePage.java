/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Simple license agreement wizard page that can be used to display and require
 * license agreements.
 * 
 * @author Stefan Rybacki
 */
public class LicensePage extends AbstractWizardPage {

  /**
   * Default margin.
   */
  private static final int MARGIN = 15;

  /**
   * Standard text that can be used for the agree check box
   */
  public static final String AGREE_TEXT =
      "I accept the terms in the License Agreement";

  /**
   * Standard title that can be used
   */
  public static final String TITLE = "License Agreement";

  /**
   * Standard sub title that can be used
   */
  public static final String SUBTITLE =
      "Please read the following License Agreement carefully.";

  /**
   * JTextPane displaying the license text
   */
  private JTextPane licensePane;

  /**
   * JCheckBox that needs to be checked to agree to license
   */
  private JCheckBox agree;

  /**
   * label to display license hint
   */
  private JLabel hintLabel;

  /**
   * the wizard showing this page
   */
  private IWizard wizard;

  /**
   * text displayed next to the check box
   */
  private String agreeText;

  /**
   * wizard page icon to display
   */
  private Icon pageIcon;

  /**
   * subtitle
   */
  private String subTitle;

  /**
   * title
   */
  private String title;

  /**
   * the license text to display
   */
  private String licenseText;

  /**
   * hint text to display
   */
  private String hintText;

  /**
   * Creates a basic license agreement page.
   * 
   * @param title
   *          the title to display
   * @param subTitle
   *          the sub title to display
   * @param pageIcon
   *          the wizard page icon
   * @param hint
   *          the hint text (can be null)
   * @param license
   *          the license text
   * @param agree
   *          the agree text (displayed next to agree check box)
   * @see #AGREE_TEXT
   * @see #TITLE
   * @see #SUBTITLE
   */
  public LicensePage(String title, String subTitle, Icon pageIcon, String hint,
      String license, String agree) {
    this.title = title;
    this.subTitle = subTitle;
    this.pageIcon = pageIcon;
    this.hintText = hint;
    this.agreeText = agree;
    this.licenseText = license;
  }

  @Override
  protected JComponent createPage() {

    JPanel panel = new JPanel(new BorderLayout());

    hintLabel = new JLabel();
    // in the north put hint
    Box hintVBox = Box.createVerticalBox();
    Box hintHBox = Box.createHorizontalBox();

    hintHBox.add(Box.createHorizontalStrut(MARGIN));
    hintHBox.add(hintLabel);
    hintHBox.add(Box.createHorizontalStrut(MARGIN));
    hintHBox.setAlignmentX(Component.LEFT_ALIGNMENT);

    hintVBox.add(Box.createVerticalStrut(MARGIN));
    hintVBox.add(hintHBox);
    hintVBox.add(Box.createVerticalStrut(MARGIN));
    panel.add(hintVBox, BorderLayout.NORTH);

    // in the center put the license pane
    licensePane = new JTextPane();
    licensePane.setEditable(false);
    panel.add(new JScrollPane(licensePane), BorderLayout.CENTER);

    // in the south put the agree check box
    agree = new JCheckBox();
    agree.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        LicensePage.this.actionPerformed(e);
      }
    });
    Box agreeHBox = Box.createHorizontalBox();
    agreeHBox.add(Box.createHorizontalStrut(MARGIN));
    agreeHBox.add(agree);
    agreeHBox.add(Box.createHorizontalStrut(MARGIN));
    agreeHBox.setAlignmentX(Component.LEFT_ALIGNMENT);

    Box agreeVBox = Box.createVerticalBox();
    agreeVBox.add(Box.createVerticalStrut(MARGIN));
    agreeVBox.add(agreeHBox);
    agreeVBox.add(Box.createVerticalStrut(MARGIN));

    panel.add(agreeVBox, BorderLayout.SOUTH);

    panel.add(Box.createHorizontalStrut(15), BorderLayout.WEST);
    panel.add(Box.createHorizontalStrut(15), BorderLayout.EAST);

    hintLabel.setText(hintText);
    licensePane.setText(licenseText);
    licensePane.setCaretPosition(0);
    agree.setText(agreeText);

    return panel;
  }

  /**
   * helper function called by action listeners (here only from the agree check
   * box)
   * 
   * @param e
   *          the action event
   */
  protected void actionPerformed(ActionEvent e) {
    if (e.getSource() == agree) {
      fireStatesChanged();
    }
  }

  @Override
  protected void persistData(IWizard w) {
    w.putValue("agree", Boolean.valueOf(agree.isSelected()));
  }

  @Override
  protected void prepopulatePage(IWizard w) {
    this.wizard = w;
    agree.setSelected(Boolean.TRUE.equals(w.getValue("agree")));
  }

  @Override
  public boolean canBack(IWizard w) {
    return true;
  }

  @Override
  public boolean canHelp(IWizard w) {
    return false;
  }

  @Override
  public boolean canNext(IWizard w) {
    return agree.isSelected();
  }

  @Override
  public IWizardHelpProvider getHelp() {
    return null;
  }

  @Override
  public Icon getPageIcon() {
    return pageIcon;
  }

  @Override
  public Dimension getPreferredSize() {
    return null;
  }

  @Override
  public String getSubTitle() {
    return subTitle;
  }

  @Override
  public String getTitle() {
    return title;
  }

}
