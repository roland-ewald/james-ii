/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Clone;
import org.jamesii.core.util.misc.Pair;

/**
 * In this update, the whole tree gets traversed and each time a parent with a
 * certain name is encountered, a sub-block with a certain name and its content
 * will be added. If an instance of {@link Serializable} shall be handed over,
 * it will be cloned each time it is set.
 * 
 * @author Roland Ewald
 */
public class OverallParamBlockUpdate extends BasicParamBlockUpdate {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1744606289158843510L;

  /**
   * The name of the parent to which a sub-block with given name and content
   * shall be added. Will be used for matching.
   */
  private String parentName;

  /** The name of the sub-block to be created. */
  private String name;

  /** The content of the sub-block to be created. */
  private Object content;

  /** This is either the same as content, or null. */
  private Serializable sContent;

  /**
   * Default constructor.
   * 
   * @param targetParentName
   *          name of the target parent, will be used for matching
   * @param paramName
   *          name of the parameter that will be added
   * @param paramContent
   *          content of the parameter that will be added
   */
  public OverallParamBlockUpdate(String targetParentName, String paramName,
      Object paramContent) {
    parentName = targetParentName;
    name = paramName;
    content = paramContent;
    sContent =
        (content instanceof Serializable) ? (Serializable) content : null;
  }

  @Override
  void init(ParameterBlock root) {
  }

  @Override
  boolean match(List<Pair<String, ParameterBlock>> path) {
    return getCurrentNode(path).getFirstValue().equals(parentName);
  }

  @Override
  void modify(List<Pair<String, ParameterBlock>> path) {
    ParameterBlock parent = getCurrentNode(path).getSecondValue();

    Object contentToBeSet =
        (sContent != null) ? Clone.riskyCloneSerializable(sContent) : content;

    if (parent.hasSubBlock(name)) {
      parent.getSubBlock(name).setValue(contentToBeSet);
    } else {
      parent.addSubBlock(name, contentToBeSet);
    }
  }

  @Override
  boolean stop() {
    return false;
  }

  /**
   * Gets the parent name.
   * 
   * @return the parent name
   */
  public String getParentName() {
    return parentName;
  }

  /**
   * Sets the parent name.
   * 
   * @param parentName
   *          the new parent name
   */
  public void setParentName(String parentName) {
    this.parentName = parentName;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the content.
   * 
   * @return the content
   */
  public Object getContent() {
    return content;
  }

  /**
   * Sets the content.
   * 
   * @param content
   *          the new content
   */
  public void setContent(Object content) {
    this.content = content;
  }

  /**
   * Gets the s content.
   * 
   * @return the s content
   */
  public Serializable getSContent() {
    return sContent;
  }

  /**
   * Sets the s content.
   * 
   * @param content
   *          the new s content
   */
  public void setSContent(Serializable content) {
    sContent = content;
  }
}
