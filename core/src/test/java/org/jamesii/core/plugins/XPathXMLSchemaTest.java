/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Stefan Rybacki
 * 
 */
public class XPathXMLSchemaTest extends TestCase {
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

  /**
   * The builder.
   */
  private DocumentBuilder builder;

  @Override
  protected void setUp() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);
    dbf.setValidating(true);
    try {
      dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
    } catch (IllegalArgumentException x) {
      fail(x.getMessage());
    }
    dbf.setExpandEntityReferences(true);
    try {
      builder = dbf.newDocumentBuilder();
      builder.setEntityResolver(new SchemaResolver());
      builder.setErrorHandler(new ErrorHandler() {

        @Override
        public void error(SAXParseException exception) throws SAXException {
          fail(exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
          fail(exception.getMessage());
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
          fail(exception.getMessage());
        }

      });
    } catch (Exception e) {
      fail(e.getMessage());
    }
  }

  /**
   * Test XPath and generated contents for plugintypes.
   * 
   * @throws SAXException
   * @throws IOException
   */
  public void testXPathPlugintype() throws IOException, SAXException {
    InputSource source = null;

    source =
        new InputSource(getClass().getResourceAsStream(
            "validplugintypefile.xml"));

    assertNotNull(source);

    Document xmlData = builder.parse(source);
    DomPluginTypeData data = new DomPluginTypeData(xmlData);

    assertEquals("abstractFactory", data.getAbstractFactory());
    assertEquals("baseFactory", data.getBaseFactory());
    assertEquals("description", data.getDescription());
    IId ids = data.getId();
    assertEquals("plugintype", ids.getName());
    assertEquals("1.0", ids.getVersion());

    List<IParameter> parameters = data.getParameters();
    assertEquals(2, parameters.size());
    IParameter parameter = parameters.get(0);
    assertEquals("param1", parameter.getName());
    assertEquals("param1description", parameter.getDescription());
    assertEquals("java.lang.String", parameter.getType());
    assertEquals("default", parameter.getDefaultValue());
    assertEquals("", parameter.getPluginType());
    assertFalse(parameter.isRequired());

    parameter = parameters.get(1);
    assertEquals("param2", parameter.getName());
    assertEquals("param2description", parameter.getDescription());
    assertEquals("java.lang.Integer", parameter.getType());
    assertEquals("", parameter.getDefaultValue());
    assertEquals("pt", parameter.getPluginType());
    assertTrue(parameter.isRequired());
  }

  /**
   * Test XPath and generated contents for plugins.
   */
  public void testXPathPlugin() {
    InputSource source = null;
    try {
      source =
          new InputSource(getClass().getResourceAsStream("validpluginfile.xml"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertNotNull(source);

    try {
      Document xmlData = builder.parse(source);
      DomPluginData pluginData =
          new DomPluginData(xmlData, "validpluginfile.xml");

      // check whether the pluginData is as expected
      IId id = pluginData.getId();
      assertEquals("testplugin", id.getName());
      assertEquals("1.0", id.getVersion());

      List<IId> dependencies = pluginData.getDependencies();
      assertEquals(1, dependencies.size());
      id = dependencies.get(0);
      assertEquals("depends1", id.getName());
      assertEquals("2.0", id.getVersion());

      List<IFactoryInfo> factories = pluginData.getFactories();
      assertEquals(factories.size(), 4);

      assertEquals("license:jamesII", pluginData.getLicenseURI().toString());

      // factory 1
      IFactoryInfo factoryInfo = factories.get(0);
      assertEquals("testClass1", factoryInfo.getClassname());
      assertEquals("testClass1description", factoryInfo.getDescription());
      List<IParameter> parameters = factoryInfo.getParameters();
      assertEquals(1, parameters.size());
      IParameter parameter = parameters.get(0);
      assertEquals("param1class1", parameter.getName());
      assertEquals("param1testClass1", parameter.getDescription());
      assertEquals("java.lang.Integer", parameter.getType());
      assertEquals("plugintypetestClass1", parameter.getPluginType());
      assertTrue(parameter.isRequired());

      // factory 2
      factoryInfo = factories.get(1);
      assertEquals("testClass2", factoryInfo.getClassname());
      assertEquals("testClass2description", factoryInfo.getDescription());
      parameters = factoryInfo.getParameters();
      assertEquals(0, parameters.size());

      // factory 3
      factoryInfo = factories.get(2);
      assertEquals("testClass3", factoryInfo.getClassname());
      assertEquals("testClass3description", factoryInfo.getDescription());
      parameters = factoryInfo.getParameters();
      assertEquals(0, parameters.size());

      // factory 4
      factoryInfo = factories.get(3);
      assertEquals("testClass4", factoryInfo.getClassname());
      assertEquals("", factoryInfo.getDescription());
      parameters = factoryInfo.getParameters();
      assertEquals(1, parameters.size());
      parameter = parameters.get(0);
      assertEquals("param1class4", parameter.getName());
      assertEquals("param1testClass4", parameter.getDescription());
      assertEquals("java.lang.String", parameter.getType());
      assertEquals("defaulttestClass4", parameter.getDefaultValue());
      assertEquals("", parameter.getPluginType());
      assertFalse(parameter.isRequired());
    } catch (Exception e) {

      fail(e.getMessage());
    }

  }

  /**
   * The schema invalid found.
   */
  private boolean schemaInvalidFound = false;

  /**
   * Test xml schema for plugin.
   */
  public void testXMLSchemaPlugin() {
    schemaInvalidFound = false;
    builder.setErrorHandler(new ErrorHandler() {

      @Override
      public void error(SAXParseException exception) throws SAXException {
        schemaInvalidFound = true;
      }

      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        schemaInvalidFound = true;
      }

      @Override
      public void warning(SAXParseException exception) throws SAXException {
        schemaInvalidFound = true;
      }
    });

    InputSource source = null;
    try {
      source =
          new InputSource(getClass().getResourceAsStream("validpluginfile.xml"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertNotNull(source);

    Document xmlData;
    try {
      xmlData = builder.parse(source);
      if (xmlData != null) {
        xmlData = null;
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }

    assertFalse(schemaInvalidFound);
    schemaInvalidFound = false;

    source = null;
    try {
      source =
          new InputSource(getClass().getResourceAsStream(
              "invalidpluginfile.xml"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertNotNull(source);

    try {
      xmlData = builder.parse(source);
      if (xmlData != null) {
        xmlData = null;
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }

    assertTrue(schemaInvalidFound);
  }

  /**
   * Test xml schema for plugintype.
   */
  public void testXMLSchemaPlugintype() {
    schemaInvalidFound = false;
    builder.setErrorHandler(new ErrorHandler() {

      @Override
      public void error(SAXParseException exception) throws SAXException {
        schemaInvalidFound = true;
      }

      @Override
      public void fatalError(SAXParseException exception) throws SAXException {
        schemaInvalidFound = true;
      }

      @Override
      public void warning(SAXParseException exception) throws SAXException {
        schemaInvalidFound = true;
      }
    });

    InputSource source = null;
    try {
      source =
          new InputSource(getClass().getResourceAsStream(
              "validplugintypefile.xml"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertNotNull(source);

    Document xmlData;
    try {
      xmlData = builder.parse(source);
      if (xmlData != null) {
        xmlData = null;
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }

    assertFalse(schemaInvalidFound);
    schemaInvalidFound = false;

    source = null;
    try {
      source =
          new InputSource(getClass().getResourceAsStream(
              "invalidplugintypefile.xml"));
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertNotNull(source);

    try {
      xmlData = builder.parse(source);
      if (xmlData != null) {
        xmlData = null;
      }
    } catch (Exception e) {
      fail(e.getMessage());
    }

    assertTrue(schemaInvalidFound);
  }

}
