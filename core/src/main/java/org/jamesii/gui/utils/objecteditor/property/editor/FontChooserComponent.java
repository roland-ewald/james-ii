/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.objecteditor.property.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.gui.utils.BasicUtilities;

// TODO: Auto-generated Javadoc
/**
 * A component that can be used to choose and setup a {@link Font}. It provides
 * options to set the font type, the font style and the size. It also provides a
 * preview of the currently setup font.
 * 
 * @author Stefan Rybacki
 */
public class FontChooserComponent extends JComponent implements
    ListSelectionListener, ChangeListener {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -3210131568214629670L;

  /**
   * The selected font.
   */
  private Font selectedFont;

  /**
   * The font list.
   */
  private JList fontList;

  /**
   * The size spinner.
   */
  private JSpinner sizeSpinner;

  /**
   * The bold style.
   */
  private JCheckBox boldStyle;

  /**
   * The italic style.
   */
  private JCheckBox italicStyle;

  /**
   * The preview label.
   */
  private JLabel previewLabel;

  /**
   * Instantiates a new font chooser component.
   * 
   * @param selected
   *          the selected
   */
  public FontChooserComponent(Font selected) {
    super();
    setLayout(new BorderLayout());

    JPanel fontSelectionPanel = new JPanel(new BorderLayout());
    fontSelectionPanel.setBorder(BorderFactory.createTitledBorder(
        fontSelectionPanel.getBorder(), "Font"));

    fontList = new JList(new FontListModel());
    fontList.setCellRenderer(new FontListCellRenderer());
    fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // add selection listener
    fontList.addListSelectionListener(this);

    fontSelectionPanel.add(new JScrollPane(fontList), BorderLayout.CENTER);

    add(fontSelectionPanel, BorderLayout.CENTER);

    sizeSpinner =
        new JSpinner(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));

    // add change listener
    sizeSpinner.addChangeListener(this);

    JPanel sizePanel = new JPanel(new BorderLayout());
    sizePanel.setBorder(BorderFactory.createTitledBorder(sizePanel.getBorder(),
        "Font Size"));

    sizePanel.add(sizeSpinner, BorderLayout.CENTER);

    Box hBox = Box.createHorizontalBox();
    hBox.add(sizePanel);

    // style box
    JPanel fontStyleBox = new JPanel(new GridLayout(1, 2));
    fontStyleBox.setBorder(BorderFactory.createTitledBorder(
        fontStyleBox.getBorder(), "Font Style"));

    boldStyle = new JCheckBox("Bold");
    italicStyle = new JCheckBox("Italic");

    boldStyle.addChangeListener(this);
    italicStyle.addChangeListener(this);

    fontStyleBox.add(boldStyle);
    fontStyleBox.add(italicStyle);

    hBox.add(fontStyleBox);

    Box vBox = Box.createVerticalBox();
    vBox.add(hBox);

    // preview box
    JPanel previewBox = new JPanel(new FlowLayout());
    previewBox.setBorder(BorderFactory.createTitledBorder(
        previewBox.getBorder(), "Preview"));

    previewLabel =
        new JLabel("This is a preview of the selected font and font style!");
    previewBox.add(previewLabel);

    vBox.add(previewBox);

    add(vBox, BorderLayout.PAGE_END);

    setSelectedFont(selected);
  }

  /**
   * Value changed.
   * 
   * @param e
   *          the e
   */
  @Override
  public void valueChanged(ListSelectionEvent e) {
    updateSelectedFont();
  }

  /**
   * Update preview.
   */
  private void updatePreview() {
    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        previewLabel.setFont(selectedFont);
        previewLabel.revalidate();
        previewLabel.repaint();
      }

    });
  }

  /**
   * State changed.
   * 
   * @param e
   *          the e
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    updateSelectedFont();
  }

  /**
   * Helper method that updates selected font.
   */
  private void updateSelectedFont() {
    // get list selection
    Object selectedValue = fontList.getSelectedValue();
    if (selectedValue instanceof Font) {
      // derive font with style and size
      selectedFont =
          ((Font) selectedValue).deriveFont((boldStyle.isSelected() ? Font.BOLD
              : Font.PLAIN)
              | (italicStyle.isSelected() ? Font.ITALIC : Font.PLAIN),
              ((Integer) sizeSpinner.getValue()).floatValue());

      updatePreview();
    }
  }

  /**
   * Sets the selected font.
   * 
   * @param font
   *          the new selected font
   */
  public void setSelectedFont(Font font) {
    if (font == null) {
      font = new Font(Font.DIALOG, Font.PLAIN, 10);
    }

    // set size
    sizeSpinner.setValue(font.getSize());

    // set style
    boldStyle.setSelected(font.isBold());
    italicStyle.setSelected(font.isItalic());

    // now find font in list
    for (int i = 0; i < fontList.getModel().getSize(); i++) {
      Font f = (Font) fontList.getModel().getElementAt(i);
      if (f.getFontName().equals(font.getFontName())) {
        fontList.setSelectedIndex(i);
        break;
      }
    }

    selectedFont = font.deriveFont(font.getStyle());
    updatePreview();
  }

  /**
   * Gets the selected and setup font.
   * 
   * @return the selected font
   */
  public Font getSelectedFont() {
    return selectedFont;
  }
}
