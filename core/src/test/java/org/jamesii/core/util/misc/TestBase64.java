/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.misc.Base64;
import org.jamesii.core.util.misc.Strings;

/**
 * Test case for {@link Base64}.
 * 
 * @author Stefan Rybacki
 */
public class TestBase64 extends ChattyTestCase {
  /**
   * The test strings.
   */
  private final Map<String, String> testStrings = new HashMap<>();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    testStrings.put("Aladdin:open sesame", "QWxhZGRpbjpvcGVuIHNlc2FtZQ==");
    testStrings.put("http://www.jamesii.org",
        "aHR0cDovL3d3dy5qYW1lc2lpLm9yZw==");
    testStrings.put("University of Rostock", "VW5pdmVyc2l0eSBvZiBSb3N0b2Nr");
    testStrings
        .put(
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789",
            "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0NTY3ODk=");
  }

  /**
   * Test method for {@link org.jamesii.core.util.misc.Base64#encode(byte[])}.
   * 
   * @throws UnsupportedEncodingException
   */
  public final void testEncode() throws UnsupportedEncodingException {
    for (Entry<String, String> v : testStrings.entrySet()) {
      assertEquals(v.getValue(), Base64.encode(v.getKey().getBytes("UTF8")));
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Base64#decode(java.lang.String)}.
   * 
   * @throws UnsupportedEncodingException
   */
  public final void testDecode() throws UnsupportedEncodingException {
    for (Entry<String, String> v : testStrings.entrySet()) {
      assertEquals(v.getKey(), new String(Base64.decode(v.getValue()), "UTF8"));
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Base64#decode(java.lang.String)} and
   * {@link org.jamesii.core.util.misc.Base64#encode(byte[])}.
   * 
   * @throws UnsupportedEncodingException
   */
  public final void testCodec() throws UnsupportedEncodingException {
    long seed = System.currentTimeMillis() * System.nanoTime();
    addParameter("seed", seed);

    Random rnd = new Random(seed);

    for (int i = 0; i < 10000; i++) {
      String random = Strings.generateRandomString(rnd.nextInt(10000), rnd);
      assertEquals(random,
          new String(Base64.decode(Base64.encode(random.getBytes("UTF8"))),
              "UTF8"));
    }
  }

}
