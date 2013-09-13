/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.install;

import java.io.IOException;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jamesii.SimSystem;
import org.jamesii.core.plugins.DomPluginData;
import org.jamesii.core.plugins.DomPluginTypeData;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.plugins.IPluginTypeData;
import org.jamesii.core.plugins.PluginLoadException;
import org.jamesii.core.plugins.SchemaResolver;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Methods to read plug-in type and plug-in XML description files.
 * 
 * 
 * @author Jan Himmelspach
 * 
 */
public class XMLReader {

  /**
   * 
   * Error handler.
   */
  private static final class ErrorHandlerImplementation implements ErrorHandler {
    @Override
    public void error(SAXParseException exception) throws SAXException {
      SimSystem.report(Level.SEVERE, exception.getMessage(), exception);
      throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
      SimSystem.report(Level.SEVERE, exception.getMessage(), exception);
      throw exception;
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
      SimSystem.report(Level.WARNING, exception.getMessage(), exception);
    }
  }

  /** Constants used for JAXP */
  private static final String JAXP_SCHEMA_LANGUAGE =
      "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

  /** The Constant JAXP_SCHEMA_SOURCE. */
  @SuppressWarnings("unused")
  private static final String JAXP_SCHEMA_SOURCE =
      "http://java.sun.com/xml/jaxp/properties/schemaSource";

  /** The Constant W3C_XML_SCHEMA. */
  private static final String W3C_XML_SCHEMA =
      "http://www.w3.org/2001/XMLSchema";

  /** The builder. */
  private DocumentBuilder builder;

  public XMLReader() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(true);
    try {
      dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    } catch (IllegalArgumentException x) {
      // This can happen if the parser does not support JAXP 1.2
      SimSystem.report(x);
    }
    dbf.setExpandEntityReferences(true);
    try {
      builder = dbf.newDocumentBuilder();
      builder.setEntityResolver(new SchemaResolver());
      builder.setErrorHandler(new ErrorHandlerImplementation());
    } catch (Exception e) {
      SimSystem.report(e);
    }

  }
  
  /**
   * Read the passed xml file. Has to be a plug-in description file.
   * 
   * @param source
   *          the source of the plugin description
   * @param fileName
   *          the file name
   * @param debugInfo
   *          the debug info
   */
  public IPluginData readPluginXMLFile(InputSource source, String fileName,
      String debugInfo) {

    Document xmlData = null;    
    
    // try {
    try {
      xmlData = builder.parse(source);
      return new DomPluginData(xmlData, fileName);
    } catch (SAXParseException e) {
      SimSystem
          .report(
              Level.WARNING,
              "Error reading plugin XML (might be ok if the loaded XML file is not a James II Plugin - XML File) --> "
                  + fileName + "/" + debugInfo, e);
      throw new PluginLoadException(e);
    } catch (SAXException | IOException e) {
      SimSystem.report(Level.SEVERE, "Error reading plugin XML --> " + fileName
          + "/" + debugInfo, e);
      throw new PluginLoadException(e);
    }
  }

  /**
   * Read the passed xml plug-in type description file. Every extension point /
   * plug-in type has to be defined by such a plug-in type description file.
   * 
   * @param source
   *          the source the type definition shall be read from
   * @param fileName
   *          the file name is used as additional information if a parsing error
   *          occurs
   * 
   * @return true, if successful
   */
  public IPluginTypeData readPluginTypeXMLFile(InputSource source,
      String fileName) {
    Document xmlData = null;

    try {
      xmlData = builder.parse(source);
      return new DomPluginTypeData(xmlData);
      // System.out.println(pluginTypeData);

    } catch (SAXParseException e) {
      SimSystem
          .report(
              Level.WARNING,
              "Error reading plugin XML (might be ok if the loaded XML file is not a James II Plugin - XML File) --> "
                  + fileName, e);
      throw new PluginLoadException(e);
    } catch (SAXException | IOException e) {
      SimSystem.report(Level.SEVERE, "Error (" + e.getMessage()
          + ") reading plug-in type description file: " + fileName
          + " !Ignoring the error!", e);
    }

    // if valid plugin type XML file return true, otherwise false
    return null;
  }

}
