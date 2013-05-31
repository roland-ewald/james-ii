/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

/**
 * Constants class that can be used to retrieve a standard set of urls to images
 * and icons ready to use with the {@link ApplicationResourceManager}.
 * 
 * @author Stefan Rybacki
 */
public class BasicResources {

  /**
   * License text as string resource file.
   */
  public static final String TEXTFILE_LICENSE_TEXT =
      "textfile:/org/jamesii/LIZENZ.txt?encoding=UTF8";

  /**
   * the actual path to the logo
   */
  public static final String IMAGE_COSA_LOGO_PATH =
      "/org/jamesii/gui/application/logo.png";

  /**
   * Cosa logo as image.
   */
  public static final String IMAGE_COSA_LOGO = "image:" + IMAGE_COSA_LOGO_PATH;

  private BasicResources() {
  }

}
