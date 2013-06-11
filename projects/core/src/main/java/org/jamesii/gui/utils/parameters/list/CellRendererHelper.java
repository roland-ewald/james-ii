/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Helper class that simplifies implementation of some more commonly used cell
 * renderers for this plugin.
 * 
 * @author Johannes Rössel
 * 
 * @see ParameterListCellRenderer
 */
public final class CellRendererHelper {

  /** Private default constructor to prevent instantiation. */
  private CellRendererHelper() {
  }

  /**
   * Returns a component that displays instances of the
   * {@link org.jamesii.gui.utils.parameters.list.Entry} class. This utilises a
   * template {@link Component} from which all important visual properties are
   * copied to the returned component.
   * 
   * @param template
   *          The template component. This is used to copy all necessary
   *          properties from, such as background and foreground colour, font
   *          and border. This ensures visual consistency. Usually this template
   *          {@link Component} is retrieved from a default implementation of
   *          the respective renderer.
   * @param value
   *          The value
   * @return A {@link Component} for displaying the
   *         {@link org.jamesii.gui.utils.parameters.list.Entry}.
   */
  public static Component getEvaluationConfigurationEntryRendererComponent(
      Component template, org.jamesii.gui.utils.parameters.list.Entry value) {
    // some properties
    // Note that we need to use getRGB trick for the colours here since Nimbus
    // otherwise won't play well with this renderer and ignore background
    // colours.
    Color foreground = new Color(template.getForeground().getRGB(), true);
    Color background = new Color(template.getBackground().getRGB(), true);
    Font font = template.getFont();

    if (value == null) {
      SimSystem.report(Level.WARNING, "No renderer found for null!");
      return new JLabel("No renderer found for null entry");
    }

    // Border border = null;

    Factory factory = null;
    try {
      factory = (Factory) Class.forName(value.getFactoryName()).newInstance();
    } catch (Exception e) {
      SimSystem.report(e);
      return null;
    }

    JPanel outerPanel = new JPanel(new BorderLayout());

    applyTemplate(template, outerPanel);

    // if (border != null) {
    // outerPanel.setBorder(border);
    // }

    JLabel nameLabel = new JLabel(factory.getReadableName());
    nameLabel.setFont(font);
    nameLabel.setForeground(foreground);
    nameLabel.setBackground(background);
    nameLabel.setOpaque(false);
    outerPanel.add(nameLabel, BorderLayout.PAGE_START);

    // retrieve parameters
    Map<String, ParameterBlock> parameters =
        value.getParameters().getSubBlocks();

    // the panel that holds the parameter summary
    Box innerPanel = new Box(BoxLayout.PAGE_AXIS);
    innerPanel.setBackground(background);
    innerPanel.setOpaque(false);
    innerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    innerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    for (Entry<String, ParameterBlock> param : parameters.entrySet()) {
      String paramName = param.getKey();
      String paramText;
      Object paramVal = param.getValue().getValue();
      paramText = paramVal != null ? paramVal.toString() : "(null)";
      if (paramVal instanceof String) {
        try {
          Factory fac =
              (Factory) Class.forName((String) paramVal).newInstance();
          paramText = fac.getReadableName();
        } catch (Exception e) {
          SimSystem.report(e);
        }
      }

      JLabel label =
          new JLabel(String.format("%1$s: %2$s", paramName, paramText));

      label.setForeground(foreground);
      label.setBackground(background);
      label.setOpaque(false);
      label.setFont(font.deriveFont(font.getSize2D() * 0.95f));

      innerPanel.add(label);
    }

    outerPanel.add(innerPanel, BorderLayout.CENTER);

    return outerPanel;
  }

  /**
   * Applies the necessary visual properties from a template {@link Component}
   * to a target {@link JComponent}, such as foreground and background colour,
   * font and border. The border is only set if {@code template} is also an
   * instance of {@link JComponent}.
   * 
   * @param template
   *          The template {@link Component} from which the properties are
   *          copied.
   * @param target
   *          The target {@link JComponent} to which the properties are copied.
   */
  public static void applyTemplate(Component template, JComponent target) {
    Color foreground = new Color(template.getForeground().getRGB(), true);
    Color background = new Color(template.getBackground().getRGB(), true);
    Font font = template.getFont();

    target.setBackground(background);
    target.setForeground(foreground);
    target.setFont(font);
    target.setName(template.getName());
    target.setOpaque(template.isOpaque());
    target.setEnabled(template.isEnabled());

    if (template instanceof JComponent) {
      target.setBorder(((JComponent) template).getBorder());
    }
  }

}
