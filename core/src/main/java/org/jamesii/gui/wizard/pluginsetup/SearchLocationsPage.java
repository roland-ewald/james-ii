/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard.pluginsetup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jamesii.gui.wizard.DefaultPage;
import org.jamesii.gui.wizard.IWizard;
import org.jamesii.gui.wizard.IWizardHelpProvider;

/**
 * A wizard page to setup (add/remove) search locations (directories).
 * 
 * @author Jan Himmelspach
 */
public class SearchLocationsPage extends DefaultPage {
  /**
   * list of search locations
   */
  private List<String> locations;

  /**
   * Creates a search locations page for a wizard. It supports a welcome message
   * as well as a welcome image.
   * 
   * @param title
   *          page's title
   * @param subTitle
   *          the page's sub title
   * @param list
   *          the list of locations
   */
  public SearchLocationsPage(String title, String subTitle, List<String> list) {
    super(title, subTitle);
    this.locations = list;
  }

  @Override
  public void shown(IWizard wizard) {
  }

  @Override
  public Icon getPageIcon() {
    return null;
  }

  @Override
  public boolean canHelp(IWizard wizard) {
    return false;
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

    JPanel contentPanel = new JPanel(new BorderLayout());

    JLabel label = new JLabel("List of search locations");
    label.setBorder(new EmptyBorder(10, 10, 10, 10));
    label.setVerticalAlignment(SwingConstants.TOP);

    contentPanel.add(label, BorderLayout.NORTH);

    // list of current search locations
    JList searchLocations = new JList(locations.toArray());

    JScrollPane searchLocationsPane = new JScrollPane(searchLocations);

    contentPanel.add(searchLocationsPane, BorderLayout.CENTER);

    // buttons to add/remove search locations

    // add content panel to panel
    panel.add(contentPanel, BorderLayout.CENTER);

    return panel;
  }

  /**
   * Gets a list of search locations.
   * 
   * @return the list of search locations
   */
  protected List<String> getLocations() {
    return locations;
  }

  @Override
  protected void persistData(IWizard wizard) {

  }

  @Override
  protected void prepopulatePage(IWizard wizard) {
  }

}
