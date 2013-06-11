/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.wizard;

/**
 * A default page supporting titles and navigation
 * 
 * @author Jan Himmelspach
 */
public abstract class DefaultPage extends AbstractWizardPage {

  /**
   * subtitle
   */
  private String subTitle;

  /**
   * title
   */
  private String title;

  /**
   * The can next.
   */
  private boolean canNext;

  /**
   * The can back.
   */
  private boolean canBack;

  /**
   * Instantiates a new default page.
   * 
   * @param title
   *          the title
   * @param subTitle
   *          the sub title
   */
  public DefaultPage(String title, String subTitle) {
    this(title, subTitle, true, false);
  }

  /**
   * Instantiates a new default page.
   * 
   * @param title
   *          the title
   * @param subTitle
   *          the sub title
   * @param canNext
   *          the can next
   * @param canBack
   *          the can back
   */
  public DefaultPage(String title, String subTitle, boolean canNext,
      boolean canBack) {
    this.title = title;
    this.subTitle = subTitle;
    this.canNext = canNext;
    this.canBack = canBack;
  }

  @Override
  public boolean canBack(IWizard wizard) {
    return canBack;
  }

  @Override
  public boolean canNext(IWizard wizard) {
    return canNext;
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
