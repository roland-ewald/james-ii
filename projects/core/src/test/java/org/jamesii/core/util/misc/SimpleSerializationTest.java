/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.TemporaryFileManager;
import org.jamesii.core.util.misc.debug.remote.ISerializableTest;
import org.jamesii.core.util.misc.debug.remote.SerializableTest;

/**
 * Simple test for ensuring that an entity is really {@link Serializable}.
 * 
 * @param <S>
 *          the type to be tested
 * 
 * @author Roland Ewald
 * @author Simon Bartels
 */
public abstract class SimpleSerializationTest<S extends Serializable> extends
    ChattyTestCase {

  /** The Constant file name for testing serialisation. */
  public static final String TEST_FILE = "testfile.dat";

  /**
   * Data structure for {@link #testRuntimeSerialization(Object)}.
   */
  private Set<Pair<Object, Class<?>>> seenObjects = new HashSet<>();

  /**
   * Instantiates a new simple serialization test.
   */
  public SimpleSerializationTest() {
    ApplicationLogger.enableConsoleLog();
  }

  /**
   * Get the object considered serialisable.
   * 
   * @return the test object
   * 
   * @throws Exception
   *           if object creation failed
   */
  public abstract S getTestObject() throws Exception;

  /**
   * Test serialisation.
   * 
   * @param original
   *          the original
   * @param deserialisedVersion
   *          the de-serialised version
   */
  public abstract void assertEquality(S original, S deserialisedVersion);

  /**
   * Tests (de-)serialisation via object I/O streams.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  // this is ensured by prior assertions
  public void testSerialization() throws Exception {

    S s = getTestObject();

    Object copyOfS =
        SerialisationUtils.deserializeFromB64String(SerialisationUtils
            .serializeToB64String(s));
    assertNotNull(copyOfS);
    assertEquals(s.getClass(), copyOfS.getClass());
    assertEquality(s, (S) copyOfS);
    assertNull(testRuntimeSerialization(s));
  }

  /**
   * Test serialization as an XML bean.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  // this is ensured by prior assertions
  public void testSerializationXMLBean() throws Exception {
    S s = getTestObject();
    Object copyOfS =
        SerialisationUtils.deserialize(SerialisationUtils.serialize(s));
    assertNotNull(copyOfS);
    assertEquals(s.getClass(), copyOfS.getClass());
    assertEquality(s, (S) copyOfS);

    File tempFile = File.createTempFile("serialization", ".test");
    addParameter("Test file", tempFile.getAbsoluteFile());
    tempFile.deleteOnExit();

    Files.save(s, tempFile.getAbsolutePath());

    S myNewS = (S) Files.load(tempFile.getAbsolutePath());
    assertEquality(s, myNewS);
  }

  /**
   * Tests serialization via RMI.
   * 
   * @throws Exception
   */
  public void testSerializationViaRMI() throws Exception {
    ISerializableTest t = new SerializableTest();
    Serializable s = getTestObject();
    assertTrue(t.isSerializable(s));
  }

  /**
   * Tries to serialize the object. If it fails the method is called recursively
   * (with declared fields and supper class) until the cause is found.
   * 
   * @param o
   *          the object under test
   * @return null if the oject is serializable<br>
   *         a string of the form
   *         "FieldName (ClassName) in FieldName (ClassName) in ...", else<br>
   *         ClassName may refer to a supper class!
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  public String testRuntimeSerialization(Object o)
      throws IllegalArgumentException, IOException, IllegalAccessException {
    if (o == null) {
      return null;
    }
    return testRuntimeSerialization(o, o.getClass());
  }

  /**
   * Iterates over all fields of the object and tries to find the serialization
   * problem. If no field is responsible then the same is tried with the supper
   * class. If this is also not the cause then it returns the name of the class.
   * 
   * @param o
   *          the object
   * @param c
   *          the objects class or a supper class, depending on which fields
   *          shall be examined
   * @return null if the oject is serializable<br>
   *         a string of the form
   *         "FieldName (ClassName) in FieldName (ClassName) in ...", else<br>
   *         ClassName may refer to a supper class!
   * @throws IOException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   */
  private String testRuntimeSerialization(Object o, Class<?> c)
      throws IOException, IllegalArgumentException, IllegalAccessException {
    if (o == null) {
      return null;
    }
    if (seenObjects.contains(new Pair<Object, Class<?>>(o, c))) {
      return null;
    }
    if (!Serializable.class.isAssignableFrom(c)) {
      return c.getName();
    }
    seenObjects.add(new Pair<Object, Class<?>>(o, c));

    try {
      serialize(o);
    } catch (NotSerializableException e) {
      for (Method m : c.getDeclaredMethods()) {
        m.setAccessible(true);
        if ("writeObject".equals(m.getName())) {
          /*
           * the writeObject method is called from the serializable API and can
           * change the behaviour completely
           */
          return "Problem is probably within " + c.getName();
        }
      }
      String ret = null;
      if (o.getClass().getName().startsWith("[")) {
        ret = checkArrayEntries(o);
      } else {
        ret = checkObject(o, c);
      }
      /*
       * Maybe it was one of the fields in the SupperClass...
       */
      if (ret == null) {
        Class<?> supperClass = c.getSuperclass();
        /*
         * We check the supperclass only if it is supposed to be serializable,
         * otherwise the supperclass can't be made responsible
         */
        if (supperClass != null
            && Serializable.class.isAssignableFrom(supperClass)) {
          ret = testRuntimeSerialization(o, c.getSuperclass());
        }
        if (supperClass == null) {
          System.out.println(o);
        }
      }
      return ret;
    }
    return null;
  }

  /**
   * Iterates over all declared fiels of the object and calls
   * {@link #testRuntimeSerialization(Object)} with the values of the field.
   * 
   * @param o
   *          the object that is to be tested
   * @return same as {@link #testRuntimeSerialization(Object)}
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws IOException
   */
  private String checkObject(Object o, Class<?> c)
      throws IllegalArgumentException, IllegalAccessException, IOException {
    for (Field field : c.getDeclaredFields()) {
      field.setAccessible(true);
      if (Modifier.isTransient(field.getModifiers())) {
        // we're not interested in transient fields
        continue;
      }
      Object ob = field.get(o);
      String temp = testRuntimeSerialization(ob);
      if (temp != null) {
        return temp + " in " + field.getName() + " (" + c.getSimpleName() + ")";
      }
    }
    return null;
  }

  /**
   * Iterates through the array an calls {@link #testSerialization(Object)} for
   * every entry.
   * 
   * @param o
   *          the object
   * @return the return of {@link #testSerialization(Object)} (if not null) or
   *         null
   * @throws IllegalArgumentException
   * @throws IOException
   * @throws IllegalAccessException
   */
  private String checkArrayEntries(Object o) throws IllegalArgumentException,
      IOException, IllegalAccessException {
    Object[] array = (Object[]) o;
    for (Object entry : array) {
      String ret = testRuntimeSerialization(entry);
      if (ret != null) {
        return ret;
      }
    }
    return null;
  }

  /**
   * Serializes the object. TODO: Very slow!
   * 
   * @param o
   *          the object to serialize
   * @throws IOException
   */
  private static void serialize(Object o) throws IOException {
    File f = TemporaryFileManager.createTempFile("serial", "object");
    try (FileOutputStream ostream = new FileOutputStream(f)) {
      ObjectOutputStream p = new ObjectOutputStream(ostream);
      p.writeObject(o);
      p.flush();
    }
  }
}
