/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.io.Serializable;

import org.jamesii.SimSystem;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.misc.ParameterUtils;

/**
 * Node that represents a selected factory in the selection tree.
 * 
 * @author Roland Ewald
 * 
 */
public class SelectedFactoryNode implements Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(SelectedFactoryNode.class,
        new IConstructorParameterProvider<SelectedFactoryNode>() {
          @Override
          public Object[] getParameters(SelectedFactoryNode selFactoryNode) {
            Object[] params =
                new Object[] { selFactoryNode.getSelectionInformation(),
                    selFactoryNode.getOrder(), selFactoryNode.getParent() };
            return params;
          }
        });
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -3788096151992839635L;

  /** The selection information. */
  private final SelectionInformation<?> selectionInformation;

  /** Order of the node. This is a selection tree. */
  private final int order;

  /** The parent node. */
  private final SelectedFactoryNode parent;

  /**
   * Empty constructor (only to be used by serialization mechanisms).
   */
  public SelectedFactoryNode() {
    this(null, 1, null);
  }

  /**
   * Default constructor.
   * 
   * @param selectionInfo
   *          the selection information
   */
  public SelectedFactoryNode(SelectionInformation<?> selectionInfo) {
    this(selectionInfo, 1, null);
  }

  /**
   * Constructor to set order.
   * 
   * @param selectionInfo
   *          the selection info
   * @param facOrder
   *          the order of the selection
   * @param parentNode
   *          the parent node
   */
  public SelectedFactoryNode(SelectionInformation<?> selectionInfo,
      Integer facOrder, SelectedFactoryNode parentNode) {
    selectionInformation = selectionInfo;
    order = facOrder;
    parent = parentNode;
  }

  @Override
  public String toString() {
    return getOrder()
        + ": "
        + (((getSelectionInformation() != null && getSelectionInformation()
            .getFactoryClass() != null) ? (getSelectionInformation()
            .getFactoryClass().getCanonicalName()
            + " ["
            + getSelectionInformation().getParamString() + "]") : "-"));
  }

  public SelectionInformation<?> getSelectionInformation() {
    return selectionInformation;
  }

  public int getOrder() {
    return order;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SelectedFactoryNode)) {
      return false;
    }
    return this.toString().equals(o.toString())
        && parentsEqual(((SelectedFactoryNode) o).getParent());
  }

  /**
   * Checks whether the parent of another node is equals to the given parent.
   * 
   * @param otherParent
   *          the parent of the other node
   * @return true, if both are equal
   */
  private boolean parentsEqual(SelectedFactoryNode otherParent) {
    return (parent == null && otherParent == null)
        || (parent != null && parent.equals(otherParent));
  }

  @Override
  public int hashCode() {
    return toString().hashCode()
        + (getParent() == null ? 0 : getParent().hashCode());
  }

  public boolean hasSelectionInformation() {
    return getSelectionInformation() != null;
  }

  public Class<? extends AbstractFactory<? extends Factory<?>>> getAbstractFactoryClass() {
    return hasSelectionInformation() ? getSelectionInformation()
        .getAbstractFactory() : null;
  }

  public Class<? extends Factory<?>> getFactoryClass() {
    return hasSelectionInformation() ? getSelectionInformation()
        .getFactoryClass() : null;
  }

  public ParameterBlock getParameter() {
    return hasSelectionInformation() ? getSelectionInformation().getParameter()
        : null;
  }

  public SelectedFactoryNode getParent() {
    return parent;
  }

  /**
   * Gets the unique sub block name. Basically reflects the scheme used in
   * {@link ParameterUtils#getFactorySubBlock(ParameterBlock, Class, int, Class)}
   * .
   * 
   * @return the unique sub block name
   */
  public String getUniqueSubBlockName() {
    return !hasSelectionInformation() ? null : SimSystem.getRegistry()
        .getBaseFactoryForAbstractFactory(getAbstractFactoryClass()).getName()
        + (getOrder() == 1 ? "" : ParameterUtils.CARDINALITY_SEPARATOR
            + getOrder());
  }
}