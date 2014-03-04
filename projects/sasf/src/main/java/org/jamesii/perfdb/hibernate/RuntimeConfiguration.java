/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import java.util.Date;

import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Hibernate implementation of runtime configuration.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
// Hibernate uses private methods
public class RuntimeConfiguration extends IDEntity implements
    IRuntimeConfiguration {

  private static final long serialVersionUID = -8688744475328696845L;

  /** Hash sum of the current selection tree. */
  private long selectionTreeHash;

  /** The selection tree that defines the configuration data. */
  private SelectionTree selectionTree;

  /**
   * The date at which this runtime configuration was introduced to the
   * performance database.
   */
  private Date introductionDate;

  /** Simple version information, will be managed automatically by the base. */
  private int version;

  /**
   * Flag that signals if this runtime configuration is still up to date. If
   * true, no algorithm in the selection tree has been changed significantly.
   */
  private boolean upToDate;

  /**
   * Empty constructor for beans compliance.
   */
  public RuntimeConfiguration() {
  }

  /**
   * Instantiates a new runtime configuration.
   * 
   * @param selTree
   *          the corresponding selection tree
   * @param introDate
   *          the date at which this configuration was introduced (i.e., used
   *          first)
   * @param ver
   *          the version of the configuration
   */
  public RuntimeConfiguration(SelectionTree selTree, Date introDate, int ver) {
    setSelectionTree(selTree);
    introductionDate = new Date(introDate.getTime());
    version = ver;
    upToDate = true;
  }

  @Override
  public SelectionTree getSelectionTree() {
    return selectionTree;
  }

  @Override
  public long getSelectionTreeHash() {
    return selectionTreeHash;
  }

  @Override
  public final void setSelectionTree(SelectionTree tree) {
    selectionTree = tree;
    selectionTreeHash = tree.getHash();
  }

  @Override
  public Date getIntroductionDate() {
    return new Date(introductionDate.getTime());
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public boolean isUpToDate() {
    return upToDate;
  }

  /**
   * Marks the configuration as outdated. Afterwards it will no longer be up to
   * date.
   */
  public void markOutdated() {
    upToDate = false;
  }

  // Hibernate support

  private void setSelectionTreeHash(long hash) {
    selectionTreeHash = hash; // NOSONAR:{used_by_hibernate}
  }

  private void setIntroductionDate(Date introDate) {
    introductionDate = introDate; // NOSONAR:{used_by_hibernate}
  }

  private void setVersion(int ver) {
    version = ver; // NOSONAR:{used_by_hibernate}
  }

  private void setUpToDate(boolean utd) {
    upToDate = utd; // NOSONAR:{used_by_hibernate}
  }

}
