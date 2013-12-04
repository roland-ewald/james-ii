/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;

// TODO: Auto-generated Javadoc
/**
 * This class is basically a wrapper around {@link ConfigureFactoryPanel} and is
 * kept for legacy reasons.
 * 
 * @author Stefan Rybacki
 * @param <F>
 *          the base factory factories are selected for
 */
public abstract class AbstractFactorySelectionPanel<F extends Factory<?>>
    extends JPanel {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -5191409491514588360L;

  /**
   * The title.
   */
  private String title;

  /**
   * The allow null.
   */
  private boolean allowNull;

  /**
   * The panel that is handling factory selection and parameter collecting.
   */
  private ConfigureFactoryPanel<F> panel;

  /**
   * Instantiates a new abstract factory selection panel.
   * 
   * @param title
   *          the title
   * @param allowNull
   *          the allow null
   */
  public AbstractFactorySelectionPanel(String title, boolean allowNull) {
    setLayout(new BorderLayout());
    setOpaque(true);

    this.title = title;
    this.allowNull = allowNull;
  }

  /**
   * Set factories.
   * 
   * @param factories
   *          the factories
   * @param selected
   *          the selected
   * @param block
   *          the block
   */
  protected void setFactories(List<F> factories, F selected,
      ParameterBlock block) {
    List<F> facs = factories;
    ParameterBlock bl = block;
    if (facs == null) {
      facs = new ArrayList<>();
    }
    if (bl == null) {
      bl = new ParameterBlock();
    }

    panel = new ConfigureFactoryPanel<>(facs, title, selected, bl, allowNull);

    removeAll();
    add(panel, BorderLayout.CENTER);
    revalidate();
    repaint();
  }

  /**
   * Gets the selected factory.
   * 
   * @return the selected factory
   */
  public F getSelectedFactory() {
    return panel.getSelectedFactoryAndParameter().getFactory();
  }

  /**
   * Gets the selected parameters.
   * 
   * @return the selected parameters
   */
  public ParameterBlock getSelectedParameters() {
    return panel.getSelectedFactoryAndParameter().getParameters();
  }

  /**
   * Gets the selected factory plus its parameters as
   * {@link org.jamesii.core.util.misc.Pair}.
   * 
   * @return the selected factory parameters including the factory itself
   */
  public ParameterizedFactory<F> getSelectedFactoryParameters() {
    return panel.getSelectedFactoryAndParameter();
  }
}
