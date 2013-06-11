package org.jamesii.core.util.misc;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.jamesii.SimSystem;
import org.jamesii.core.util.misc.Files;

import junit.framework.TestCase;

/**
 * Tests the utility class {@link Files}.
 * 
 * @author Stefan Rybacki
 * @author Johannes Rössel
 */
public class TestFiles extends TestCase {

  /** Test method for {@link Files#appendExtension(File, String)}. */
  public void testAppendExtension() {
    assertEquals(Files.appendExtension(new File("foo"), "bar")
        .getAbsoluteFile(), new File("foo.bar").getAbsoluteFile());
    assertEquals(Files.appendExtension(new File("foo.bar"), "bar")
        .getAbsoluteFile(), new File("foo.bar").getAbsoluteFile());
    assertEquals(Files.appendExtension(new File("foo.txt"), "bar")
        .getAbsoluteFile(), new File("foo.txt.bar").getAbsoluteFile());

    assertEquals(Files.appendExtension(new File(new File("a"), "foo"), "bar")
        .getAbsoluteFile(),
        new File(new File("a"), "foo.bar").getAbsoluteFile());
    assertEquals(
        Files.appendExtension(new File(new File("a"), "foo.bar"), "bar")
            .getAbsoluteFile(),
        new File(new File("a"), "foo.bar").getAbsoluteFile());
    assertEquals(
        Files.appendExtension(new File(new File("a"), "foo.txt"), "bar")
            .getAbsoluteFile(),
        new File(new File("a"), "foo.txt.bar").getAbsoluteFile());
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileEnding(java.io.File)}.
   */
  public void testGetFileEnding() {
    File f = new File("foo.bar");
    assertEquals(Files.getFileEnding(f), "bar");

    f = new File(new File("foo"), "bar");
    assertEquals(Files.getFileEnding(f), "");

    f = new File(new File("foo"), "bar.baz");
    assertEquals(Files.getFileEnding(f), "baz");

    f = new File(new File("foo"), "bar.baz.gak");
    assertEquals(Files.getFileEnding(f), "gak");

    f = new File("");
    assertEquals(Files.getFileEnding(f), "");
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileFromURI(java.net.URI)}.
   * 
   * @throws URISyntaxException
   */
  public void testGetFileFromURI() throws URISyntaxException {
    URI uri = new URI("file-txt:/bla.txt");
    File file = Files.getFileFromURI(uri);
    assertEquals("bla.txt", file.getName());

    File f = new File("bla.txt");
    uri = Files.getURIFromFile(f);
    assertNotNull(uri);
    assertEquals(f.getAbsoluteFile(), Files.getFileFromURI(uri));
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileNameWithoutEnding(java.io.File, java.lang.String)}
   * .
   */
  public void testGetFileNameWithoutEndingFileString() {
    File f = new File("foo.bar");
    assertEquals(Files.getFileNameWithoutEnding(f, "bar"), "foo");
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileNameWithoutEnding(java.io.File)}
   * .
   */
  public void testGetFileNameWithoutEndingFile() {
    File f = new File("foo.bar");
    assertEquals(Files.getFileNameWithoutEnding(f), "foo");
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileName(java.net.URI)}.
   */
  public void testGetFileNameURI() {

    File f =
        new File(org.jamesii.core.util.misc.Files.composeFilename(
            SimSystem.getTempDirectory(), "foo.bar"));
    String name = Files.getFileName(f.toURI());

    assertEquals("foo.bar", name);
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileWithEnding(java.io.File, java.lang.String)}
   * .
   */
  public void testGetFileWithEnding() {
    assertEquals(new File("bla.txt"),
        Files.getFileWithEnding(new File("bla.txt"), "txt"));
    assertEquals(new File("bla.txt"),
        Files.getFileWithEnding(new File("bla"), "txt"));
    assertEquals(new File("bla.txt"),
        Files.getFileWithEnding(new File("bla.jpg"), "txt"));
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.misc.Files#getFileWithEnding(java.lang.String, java.lang.String)}
   * .
   */
  public void testGetFileWithEndingString() {
    assertEquals("bla.txt", Files.getFileWithEnding("bla.txt", "txt"));

    assertEquals("bla.tx1", Files.getFileWithEnding("bla.txt", "tx1"));
  }

}
