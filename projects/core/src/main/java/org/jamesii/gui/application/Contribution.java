/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

/**
 * Enumeration of possible contribution values. Contributions are used by the
 * {@link IWindowManager} and {@link IWindow} to determine where to put the
 * {@link IWindow}.
 *
 * @author Stefan Rybacki
 */
public enum Contribution {
  LEFT_VIEW , RIGHT_VIEW, BOTTOM_VIEW, EDITOR, DIALOG, TOP_VIEW
}
