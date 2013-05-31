/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Support for different languages.
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Jan Himmelspach
 * 
 */
public final class Language {

  private static final Language INSTANCE = new Language();

  /**
   * List of messages, may be loaded from a file and thus be substituted by
   * language dependent stuff.
   */
  private Map<String, String> messageTable = new HashMap<>();

  // TODO:This method should be adapted
  /**
   * Get a translated version of the message to be printed.
   * 
   * @param id
   *          unique ident of the text
   * @param params
   *          some parametersF
   * @return translated msg
   */
  public String getMessageInstance(String id, Object[] params) {
    // try to fetch language dependent message
    String s = messageTable.get(id);

    if (s == null) {
      s = "";
    }

    return String.format(s, params);
  }

  public static String getMessage(String id, Object[] params) {
    return INSTANCE.getMessageInstance(id, params);
  }

  // TODO:This method should be adapted
  /**
   * Get a translated version of the message to be printed.
   * 
   * @param id
   *          unique ident of the text
   * @param text
   *          default text (usually in english)
   * @return translated msg
   */
  public String getMessageInstance(String id, String text) {
    // try to fetch language dependent message
    String s = messageTable.get(id);
    // fallback
    if (s == null) {
      s = text;
    }

    return s;
  }

  public static String getMessage(String id, String text) {
    return INSTANCE.getMessageInstance(id, text);
  }

  /**
   * Get a translated version of the message to be printed.
   * 
   * @param id
   *          unique ident of the text
   * @param text
   *          default text (usually in english)
   * @param params
   *          some parameters
   * @return translated msg
   */
  public String getMessageInstance(String id, String text, Object[] params) {
    // try to fetch language dependent message
    String s = messageTable.get(id);
    // fallback
    if (s == null) {
      s = text;
    }
    if (params != null) {
      s = String.format(s, params);
    }
    return s;
  }

  public static String getMessage(String id, String text, Object[] params) {
    return INSTANCE.getMessageInstance(id, text, params);
  }

}
