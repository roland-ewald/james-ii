/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.logging;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

import org.jamesii.core.serialization.XMLEncoderFactory;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.gui.application.Contribution;

/**
 * View implementing {@link org.jamesii.gui.application.IWindow} that provides
 * access to the log records logged by the {@link ApplicationLogger}.
 * 
 * @author Stefan Rybacki
 */

public final class LogView extends BasicLogView {
  static {
    PersistenceDelegate delegate = new DefaultPersistenceDelegate() {
      @Override
      protected Expression instantiate(Object oldInstance, Encoder out) {
        return new Expression(oldInstance, LogView.class, "getInstance", null);
      }

    };

    XMLEncoderFactory.registerDelegate(LogView.class, delegate);
  }

  /** singleton instance. */
  private static final LogView INSTANCE = new LogView();

  /**
   * Hidden constructor due to usage of singleton pattern for this view.
   */
  private LogView() {
    super("Log Viewer", Contribution.BOTTOM_VIEW, null);

    ApplicationLogger.addLogListener(this, true);
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.view.log";
  }

  /**
   * Gets the instance.
   * 
   * @return the instance
   */
  public static LogView getInstance() {
    return INSTANCE;
  }

  @Override
  public boolean canClose() {
    return true;
  }

}
