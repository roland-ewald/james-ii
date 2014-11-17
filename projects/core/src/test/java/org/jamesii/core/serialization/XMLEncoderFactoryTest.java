/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.serialization;

import java.beans.DefaultPersistenceDelegate;
import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;

import org.jamesii.ChattyTestCase;

/**
 * The Class XMLEncoderFactoryTest.
 * 
 * @author Stefan Rybacki
 */
public class XMLEncoderFactoryTest extends ChattyTestCase {

  /**
   * The delegate.
   */
  private PersistenceDelegate delegate;

  /**
   * Sets the up.
   * 
   * @throws Exception
   *           the exception
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    delegate = new DefaultPersistenceDelegate();
  }

  /**
   * Test method for
   * {@link org.jamesii.core.serialization.XMLEncoderFactory#registerDelegate(java.lang.Class, java.beans.PersistenceDelegate)}
   * .
   */
  public final void testRegisterDelegate() {
    XMLEncoderFactory.registerDelegate(XMLEncoderFactory.class, delegate);

    XMLEncoder encoder = XMLEncoderFactory.createXMLEncoder(new OutputStream() {

      @Override
      public void write(int b) throws IOException {
      }
    });

    XMLEncoder encoderNotUpdated =
        XMLEncoderFactory.createXMLEncoder(new OutputStream() {

          @Override
          public void write(int b) throws IOException {
          }
        }, false);

    // check whether previously registered delegate was attached
    // automatically
    assertSame(delegate,
        encoder.getPersistenceDelegate(XMLEncoderFactory.class));
    assertSame(delegate,
        encoderNotUpdated.getPersistenceDelegate(XMLEncoderFactory.class));

    // now register another delegate and check whether the already
    // created encoder still retrieves this information and the one
    // create with non update option will not
    assertNotSame(delegate, encoder.getPersistenceDelegate(String.class));
    assertNotSame(delegate,
        encoderNotUpdated.getPersistenceDelegate(String.class));

    XMLEncoderFactory.registerDelegate(String.class, delegate);

    assertSame(delegate, encoder.getPersistenceDelegate(String.class));

    // This only seems to work on Java 7 (at least Beta, not sure about the
    // official release)
    // Java 6 seems to register delegates globally where as Java 7 does it
    // locally (this test test for locally registration because this is more
    // intuitive and seems to be fixed in Java 7)
    if (org.jamesii.core.util.BasicUtilities.checkJavaVersion("1.7")) {
      assertNotSame(delegate,
          encoderNotUpdated.getPersistenceDelegate(String.class));
    }

  }

}
