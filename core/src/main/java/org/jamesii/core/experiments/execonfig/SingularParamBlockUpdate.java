/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.execonfig;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * Parameter block updates that simply sets a named {@link ParameterBlock} at a
 * specified path and adds specified {@link ParameterBlock}. If a parameter
 * block at this position already exists, its value will be overridden and all
 * children with similar names as well.
 * 
 * TODO: Replace bean-compliant constructor with persistence delegate.
 * 
 * @author Roland Ewald
 */
public class SingularParamBlockUpdate extends BasicParamBlockUpdate {

  /** Serialisation ID. */
  private static final long serialVersionUID = 1603993798758548980L;

  /** Path to new parameter block. */
  private String[] path;

  /** Name of new parameter block. */
  private String name;

  /** The new parameter block to be set. */
  private ParameterBlock content;

  private Object value;

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public SingularParamBlockUpdate() {
  }

  /**
   * Default constructor.
   * 
   * @param targetPath
   *          path to the new parameter block
   * @param targetName
   *          name of the new parameter block
   * @param targetContent
   *          content of the new parameter block
   */
  public SingularParamBlockUpdate(String[] targetPath, String targetName,
      ParameterBlock targetContent) {
    path = targetPath;
    name = targetName;
    content = targetContent;
  }

  /**
   * Extended constructor for setting value at targetName, too.
   * 
   * @param targetPath
   *          path to the new parameter block
   * @param targetName
   *          name of the new parameter block
   * @param value
   *          Value associated with targetName (default is null)
   * @param targetContent
   *          content of the new parameter block
   */
  public SingularParamBlockUpdate(String[] targetPath, String targetName,
      Object val, ParameterBlock targetContent) {
    path = targetPath;
    name = targetName;
    value = val;
    content = targetContent;
  }

  @Override
  void init(ParameterBlock root) {
    ParameterBlock currentBlock = root;

    // Create/traverse path
    for (int i = 0; i < path.length; i++) {
      if (!currentBlock.hasSubBlock(path[i])) {
        for (int j = i; j < path.length; j++) {
          ParameterBlock childBlock = new ParameterBlock();
          currentBlock.addSubBlock(path[j], childBlock);
          currentBlock = childBlock;
        }
        break;
      }
      currentBlock = currentBlock.getSubBlock(path[i]);
    }

    // Create new sub-structure
    if (currentBlock.hasSubBlock(name)) {
      merge(content, currentBlock.getSubBlock(name));
    } else {
      currentBlock.addSubBlock(name, content.getCopy());
    }
    if (value != null) {
      currentBlock.getSubBlock(name).setValue(value);
    }
  }

  @Override
  boolean match(List<Pair<String, ParameterBlock>> p) {
    return false;
  }

  @Override
  void modify(List<Pair<String, ParameterBlock>> p) {
  }

  @Override
  boolean stop() {
    return true;
  }

  /**
   * Gets the path.
   * 
   * @return the path
   */
  public String[] getPath() {
    return path;
  }

  /**
   * Sets the path.
   * 
   * @param path
   *          the new path
   */
  public void setPath(String[] path) {
    this.path = path;
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
  public ParameterBlock getContent() {
    return content;
  }

  /**
   * Sets the content.
   * 
   * @param content
   *          the new content
   */
  public void setContent(ParameterBlock content) {
    this.content = content;
  }

}
