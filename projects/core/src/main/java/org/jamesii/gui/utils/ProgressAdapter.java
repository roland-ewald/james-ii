/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import org.jamesii.gui.application.IProgressListener;

/**
 * Simple progress listener adapter you can use for quick inner class creation
 * where you only need to override the methods you are interested in.
 * 
 * @author Stefan Rybacki
 * 
 */
public class ProgressAdapter implements IProgressListener {

  @Override
  public void finished(Object source) {
    // should be overridden as needed
  }

  @Override
  public void progress(Object source, float progress) {
    // should be overridden as needed
  }

  @Override
  public void started(Object source) {
    // should be overridden as needed
  }

  @Override
  public void taskInfo(Object source, String info) {
    // should be overridden as needed
  }

}
