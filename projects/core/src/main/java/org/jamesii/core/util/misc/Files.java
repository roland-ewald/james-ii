/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.serialization.XMLEncoderFactory;
import org.jamesii.core.util.encoding.IEncoding;
import org.jamesii.core.util.encoding.plugintype.EncodingFactory;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * Set of auxiliary file functions.
 * 
 * @author Roland Ewald Date: 29.05.2007
 */
public final class Files {

  /**
   * Hidden constructor.
   */
  private Files() {
  }

  /**
   * Replace all backslashes in a path with slashes.
   * 
   * @param path
   *          the path
   * @return path with slashes
   */
  public static String bs2s(String path) {
    return path.replace('\\', '/');
  }

  /**
   * Retrieves file ending. Without "."
   * 
   * @param file
   *          the file name
   * @return file ending
   */
  public static String getFileEnding(File file) {

    String fileName = file.getName();
    int endingIndex = fileName.lastIndexOf('.');

    // If no ending found
    if (endingIndex < 0 || endingIndex >= file.getName().length() - 1) {
      return "";
    }

    return file.getName().substring(endingIndex + 1);
  }

  /**
   * Returns file name from URI. URI might define a relative path, ie. instead
   * of 'file-stopi:/C:/cosa/bin/examples/stopi/Benchmark-3000.stopi' one could
   * write 'file-stopi:/./examples/stopi/Benchmark-3000.stopi' (useful for
   * distributed setups).
   * 
   * @param fileURI
   *          the file uri
   * @return the file from uri
   */
  public static File getFileFromURI(URI fileURI) {
    boolean isRelativePath = false;
    if (fileURI.getPath().length() > 0) {
      isRelativePath = fileURI.getPath().startsWith("/./");
    }

    // If a relative path is given,
    if (isRelativePath) {
      return new File(fileURI.getPath().substring(1));
    }

    return new File(fileURI.getPath());
  }

  /**
   * Specifies how to encode the file information into a URI.
   * 
   * @param modelFile
   *          the model file
   * @return the URI from file
   * @throws URISyntaxException
   *           the URI syntax exception
   */
  public static URI getURIFromFile(File modelFile) throws URISyntaxException {
    URI uri = modelFile.toURI();

    // FIXME: there is a problem with unsupported characters that CAN
    // occur in file endings but are not allowed in a URI schema!

    return new URI("file-" + Files.getFileEnding(modelFile),
        uri.getAuthority(), uri.getPath(), null, null);
  }

  /**
   * Appends the specified extension to the file name if it's not already
   * present. This will not replace an extension already present, e.g.:
   * 
   * <pre>
   * appendExtension(new File("foo"), "bar")       ->   foo.bar
   * appendExtension(new File("foo.bar"), "bar")   ->   foo.bar
   * appendExtension(new File("foo.bar"), "txt")   ->   foo.bar.txt
   * </pre>
   * 
   * @param file
   *          The file to append a new extension to.
   * @param extension
   *          The extension to append if it doesn't exist yet.
   * @return If the given extension was already present the original file is
   *         returned, otherwise the desired extension is appended at the end of
   *         the file name.
   */
  public static File appendExtension(File file, String extension) {
    if (getFileEnding(file).equals(extension)) {
      return file;
    }

    return new File(file.getAbsolutePath() + "." + extension);
  }

  /**
   * Retrieves name of a file without ending. Assumes that a '.' separates file
   * name and ending, e.g. getFileNameWithoutEnding("index.html", "html") will
   * return "index".
   * 
   * @param file
   *          the file
   * @param fileEnding
   *          the file ending
   * @return the file's name, without the ending
   */
  public static String getFileNameWithoutEnding(File file, String fileEnding) {
    String fileName = file.getName();
    int nameLength = fileName.length();
    int endLength = fileEnding.length();
    if (endLength >= nameLength) {
      return "";
    }
    if (endLength == 0) {
      return fileName;
    }
    return fileName.substring(0, nameLength - (endLength + 1));
  }

  /**
   * Combines {@link Files#getFileNameWithoutEnding(File, String)} and
   * {@link Files#getFileEnding(File)}.
   * 
   * @param file
   *          the file
   * @return the file's name, without the ending
   */
  public static String getFileNameWithoutEnding(File file) {
    return getFileNameWithoutEnding(file, getFileEnding(file));
  }

  /**
   * Will return the pure filename. This is the name of the file without path
   * information.
   * 
   * @param file
   *          the file name shall be returned from
   * @return name of the file without path information
   */
  public static String getFileName(URI file) {
    return getFileFromURI(file).getName();
  }

  /**
   * Returns file filter for a given ending (including character that separates
   * file ending from file name).
   * 
   * @param ending
   *          e.g. ".xml"
   * @param caseSensitive
   *          if fals, cases are ignored
   * @return filename filter that accepts all files with given ending
   */
  public static FilenameFilter getFilenameFilter(final String ending,
      final boolean caseSensitive) {
    return new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        String nameEnding = name.substring(name.length() - ending.length());
        if ((!caseSensitive && nameEnding.compareToIgnoreCase(ending) == 0)
            || nameEnding.compareTo(ending) == 0) {
          return true;
        }
        return false;
      }
    };
  }

  /**
   * Loads JavaBean from XML file.
   * 
   * @param file
   *          path to file with the object to be loaded
   * @return object the object that has been loaded
   * @throws FileNotFoundException
   *           if file was not found, etc.
   */
  public static Object load(String file) throws FileNotFoundException {
    Object object;
    try (XMLDecoder xmlDecoder =
        new XMLDecoder(new BufferedInputStream(new FileInputStream(file)),
            null, null, SimSystem.getRegistry().getClassLoader())) {
      object = xmlDecoder.readObject();
    }

    return object;
  }

  /**
   * Saves an object.
   * 
   * @param object
   *          the object
   * @param file
   *          the file
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see Files#save(Object, String, boolean)
   */
  public static void save(Object object, File file)
      throws FileNotFoundException {
    save(object, file.getAbsolutePath());
  }

  /**
   * Saves an object.
   * 
   * @param object
   *          the object
   * @param file
   *          the file
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see Files#save(Object, String, boolean)
   */
  public static void save(Object object, String file)
      throws FileNotFoundException {
    save(object, file, false);
  }

  /**
   * Saves an object and adds ending if file has no ending.
   * 
   * @param object
   *          the object
   * @param file
   *          the file
   * @param fileEnding
   *          the fileEnding
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see Files#save(Object, String, boolean)
   */
  public static void save(Object object, File file, String fileEnding)
      throws FileNotFoundException {
    save(object, file.getAbsolutePath(), fileEnding);
  }

  /**
   * Saves an object and adds ending if file has no ending.
   * 
   * @param object
   *          the object
   * @param file
   *          the file
   * @param fileEnding
   *          the fileEnding
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @see Files#save(Object, String, boolean)
   */
  public static void save(Object object, String file, String fileEnding)
      throws FileNotFoundException {

    int endingIndex = file.lastIndexOf('.');

    // Prepare Ending (remove ".")
    String ending = fileEnding.substring(fileEnding.lastIndexOf('.') + 1);

    // No ending found --> add ending
    if (endingIndex < 0 || endingIndex >= file.length() - 1) {
      if (endingIndex < 0) {
        file = file + "." + ending;
      } else {
        file = file + ending;
      }
    }

    save(object, file, false);
  }

  /**
   * Saves JavaBean to XML file.
   * 
   * @param object
   *          the object to be saved in the file
   * @param file
   *          the file
   * @param createDirectories
   *          flag to control whether a new directory should be created
   * @throws FileNotFoundException
   *           if parameter file was not found, etc.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void save(Object object, File file, boolean createDirectories)
      throws FileNotFoundException {
    save(object, file.getAbsolutePath(), true);
  }

  /**
   * Saves JavaBean to XML file.
   * 
   * @param object
   *          the object to be saved in the file
   * @param file
   *          the file
   * @param createDirectories
   *          flag to control whether a new directory should be created
   * @throws FileNotFoundException
   *           if parameter file was not found, etc.
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws IllegalArgumentException
   *           in case directory could not be created
   */
  public static void save(Object object, String file, boolean createDirectories)
      throws FileNotFoundException {

    // Test whether directory exists
    if (createDirectories) {
      File f = new File(file);
      File directory = f.getParentFile();
      while (directory != null && !directory.exists()) {
        if (!directory.mkdir()) {
          throw new IllegalArgumentException("Directory cannot be created.");
        }
        directory = f.getParentFile();
      }
    }
    try (XMLEncoder xmlEncoder =
        XMLEncoderFactory.createXMLEncoder(new BufferedOutputStream(
            new FileOutputStream(file)))) {
      xmlEncoder.writeObject(object);
    }
  }

  /**
   * Append string to file.
   * 
   * @param fileName
   *          the file name
   * @param addition
   *          the addition to the file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void appendToFile(String fileName, String addition)
      throws IOException {
    try (FileWriter fw = new FileWriter(fileName, true)) {
      fw.append(addition);
    }
  }

  /**
   * Copy a file. All directories have to exist, i.e. these are not created
   * automatically.
   * 
   * @param source
   *          the source file
   * @param destination
   *          the destination file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void copy(File source, File destination) throws IOException {
    FileChannel src = null;
    FileChannel dest = null;
    try {
      src = new FileInputStream(source.getAbsoluteFile()).getChannel();
      dest = new FileOutputStream(destination.getAbsoluteFile()).getChannel();
      src.transferTo(0, src.size(), dest);
    } finally {
      if (src != null) {
        src.close();
      }
      if (dest != null) {
        dest.close();
      }
    }
  }

  /**
   * Copy a file. All directories have to exist, i.e. these are not created
   * automatically.
   * 
   * @param sourcePath
   *          the path to the source file
   * @param destinationPath
   *          the path to the destination file
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static void copy(String sourcePath, String destinationPath)
      throws IOException {
    copy(new File(sourcePath), new File(destinationPath));
  }

  /**
   * Renames the ending of an existing file if there is an ending present. If
   * not the specified ending will be appended.
   * <p/>
   * E.g.
   * 
   * <pre>
   * bla.txt with jpg -&gt; bla.jpg
   * bla     with jpg -&gt; bla.jpg
   * bla.jpg with jpg -&gt; bla.jpg
   * </pre>
   * 
   * @param file
   *          The file which ending should be changed
   * @param fileEnding
   *          The new file ending (without preceding .)
   * @return The file with new file ending
   */
  public static File getFileWithEnding(File file, String fileEnding) {
    // file ending did not changed --> return file
    if (getFileEnding(file).equals(fileEnding)) {
      return file;
    }

    String fileName;
    String parent = file.getParent();

    fileName = getFileNameWithoutEnding(file);
    fileName = fileName + "." + fileEnding;

    return new File(parent, fileName);
  }

  /**
   * Returns the complete path and file name with another file ending as String
   * 
   * @param fileName
   *          The complete file name incl. ending
   * @param fileEnding
   *          The new ending of the file (without preceding .)
   * @return Complete filename as String
   */
  public static String getFileWithEnding(String fileName, String fileEnding) {
    return getFileWithEnding(new File(fileName), fileEnding).toString();
  }

  /**
   * Writes the actual SVG document to a file.
   * 
   * @param fileName
   *          the file name
   * @param document
   *          the document
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws TransformerException
   *           the transformer exception
   */
  public static void writeDocumentToFile(String fileName, Document document)
      throws FileNotFoundException, TransformerException {

    File f = new File(fileName);

    FileOutputStream fop = new FileOutputStream(f);

    // Writing document...
    TransformerFactory tff = TransformerFactory.newInstance();
    Transformer tf = tff.newTransformer();

    tf.setOutputProperty(OutputKeys.METHOD, "xml");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");

    DOMSource source = new DOMSource(document);
    StreamResult result = new StreamResult(fop);

    tf.transform(source, result);
  }

  /**
   * Reads a file to a string (attention: this is not fast and should only be
   * done with sufficiently small files!).
   * 
   * @param file
   *          the file
   * @return the string content of the file, in standard encoding
   * @throws FileNotFoundException
   *           the file not found exception
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public static String getFileAsString(File file) throws IOException {
    ArrayList<Object> content = new ArrayList<>();
    int size = 0;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      while (br.ready()) {
        byte[] bytes = br.readLine().getBytes();
        content.add(bytes);
        size += bytes.length;
      }
    }

    return new String(Arrays.dataChunksToArray(content, size));
  }

  /**
   * Counts the number of lines that contain a certain string in the given file.
   * 
   * @param file
   *          the file to be searched
   * @param searchString
   *          the string to be searched for
   * @return list of line indices (beginning with 0) for all lines of the file
   *         that contain the given string
   */
  public static List<Integer> occurrencesInFile(File file, String searchString)
      throws IOException {
    List<Integer> occurrences = new ArrayList<>();
    int counter = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String currentLine = reader.readLine();
      while (currentLine != null) {
        if (currentLine.contains(searchString)) {
          occurrences.add(counter);
        }
        counter++;
        currentLine = reader.readLine();
      }
    }
    return occurrences;
  }

  /**
   * Retrieves files with a given ending (without leading ".") recursively.
   * 
   * @param directory
   *          the top-most directory
   * @param ending
   *          the desired file ending
   * @return list of files within the directory (or its sub-directories) with
   *         the desired file ending
   */
  public static List<File> getFilesRecursively(File directory, String ending) {
    List<File> files = new ArrayList<>();
    if (!directory.isDirectory()) {
      return files;
    }

    File[] subFiles = directory.listFiles();
    for (File subFile : subFiles) {
      if (subFile.isDirectory()) {
        files.addAll(getFilesRecursively(subFile, ending));
        continue;
      }
      if (Files.getFileEnding(subFile).equals(ending)) {
        files.add(subFile);
      }
    }

    return files;
  }

  /**
   * Method that deletes a file or a directory, in case of an directory all sub
   * directories and files will also be deleted.
   * 
   * @param f
   *          the file/directory to delete
   * @return true, if successfully deleted
   */
  public static boolean deleteRecursively(File f) {
    boolean success = true;
    if (f.isDirectory()) {
      File[] files = f.listFiles();
      for (File file : files) {
        if (file.isDirectory()) {
          success &= deleteRecursively(file);
        } else {
          success &= file.delete();
        }
      }
    }

    if (f.exists()) {
      success &= f.delete();
    }

    return success;
  }

  /**
   * Converts to a human readable size. For instance: 6830 bytes will become,
   * 6,83 Kbytes 7248395 bytes will become 7,25 Mbytes etc.
   * 
   * @param size
   *          the size
   * @param unit
   *          the unit
   * @return the string
   */
  public static String convertToHumanReadableSize(long size, String unit) {
    return convertToHumanReadableSize(size, unit, 2);
  }

  /**
   * Convert to a human readable size. For instance: 6830 bytes will become,
   * 6,83 Kbytes 7248395 bytes will become 7,25 Mbytes etc. (if fractions is set
   * to two and unit will be "bytes")
   * 
   * @param size
   *          the size
   * @param unit
   *          the unit
   * @param fractions
   *          the fractions
   * @return the string
   */
  public static String convertToHumanReadableSize(long size, String unit,
      int fractions) {
    long[] steps = new long[] { 5000L, 5000000L, 5000000000L, 0L };
    double[] convert =
        new double[] { 1, 1024, 1024 * 1024, 1024 * 1024 * 1024 };
    String[] units = new String[] { unit, "K" + unit, "M" + unit, "G" + unit };

    int index = 0;
    for (int i = 0; i < steps.length; i++) {
      index = i;
      if (size < steps[i]) {
        break;
      }
    }

    return String.format("%." + fractions + "f %s", size / convert[index],
        units[index]);
  }

  /**
   * Creates directory, checks whether it exists and if permissions are denied
   * (this is reported as an error).
   * 
   * @param directory
   *          the directory to be created
   * @return true if directory creation was successful OR the directory already
   *         existed
   */
  public static boolean makeDirectory(File directory) {
    if (directory.exists()) {
      return true;
    }
    boolean result = false;

    try {
      result = directory.mkdir();
    } catch (SecurityException ex) {
      SimSystem.report(Level.SEVERE, "Permission denied to create directory: "
          + directory.getAbsolutePath(), ex);
    }

    return result;
  }

  /**
   * Guess the encoding of the file passed. The encoding used to write a file
   * needs to be given to read a file properly afterwards. For this purpose any
   * "encoding guessers" can be installed via the corresponding encoding guesser
   * extension point. Encoding guessing might be expensive (as bytes might be
   * read from the file). Thus it might be advisable to use this method in a
   * cached manner and only if the encoding is not known in advance. And even if
   * guessers are there they might be wrong (check the documentation of the
   * guessers installed to find out in which cases this might happen). All
   * guessers available will be used, in the order they are returned by the
   * registry. If a guesser determines an encoding this procedure will be
   * cancelled and the encoding will be returned.
   * 
   * @return encoding of the file or null if no encoding was determined
   */
  public static String guessEncoding(java.io.File file) {

    List<EncodingFactory> encodingFacs =
        SimSystem.getRegistry().getFactories(EncodingFactory.class);

    if (encodingFacs == null) {
      return null;
    }

    for (EncodingFactory ef : encodingFacs) {
      IEncoding encoding = ef.create(new ParameterBlock(file), SimSystem.getRegistry().createContext());
      String result = encoding.getEncoding(file);
      if ((result != null) && (!result.isEmpty())) {
        return result;
      }
    }

    return null;

  }

  /**
   * Read the given file and return the content as string. The filename should
   * be valid otherwise there will be a null pointer exception during the
   * creation of the string.
   * 
   * @param filename
   *          the URI of the file
   * @return content of the file as string representation
   */
  public static String readASCIIFile(URI filename) {
    return readASCIIFile(new java.io.File(filename));
  }

  /**
   * Read the given file and return the content as string. The filename should
   * be valid otherwise there will be a null pointer exception during the
   * creation of the string. This method used the
   * {@link #guessEncoding(java.io.File)} method to determine the encoding of
   * the file to be read. Please not that this method will only be able to
   * detect a decoding if at least single guesser is installed! As a fallback
   * the java vm system encoding property is used.
   * 
   * @param file
   *          the file to be read
   * @return content of the file as string representation
   */
  public static String readASCIIFile(java.io.File file) {
    String encoding = guessEncoding(file);
    if (encoding == null) {
      encoding = System.getProperty("file.encoding");
    }
    return readASCIIFile(file, encoding);
  }

  /**
   * Read the given file and return the content as string. The filename should
   * be valid otherwise there will be a null pointer exception during the
   * creation of the string.
   * 
   * @param filename
   *          the name of the file
   * @return content of the file as string representation
   */
  public static String readASCIIFile(String filename) {
    return readASCIIFile(new java.io.File(filename));
  }

  /**
   * Gets the list of paths. The pathsString has to be a list of system
   * dependent paths separated by the paths delimiter character (as defined in
   * java.io.File.pathSeparatorChar).
   * 
   * @param pathsString
   *          the string containing the paths, e.g., /xyz:/abc/def:/
   * @return the list of paths
   */
  public static List<String> getListOfPaths(String pathsString) {

    ArrayList<String> result = new ArrayList<>();

    if (pathsString != null) {
      char c[] = new char[1];
      c[0] = java.io.File.pathSeparatorChar;
      StringTokenizer st = new StringTokenizer(pathsString, new String(c));
      while (st.hasMoreTokens()) {
        String p = st.nextToken();
        result.add(p);
      }

    }

    return result;

  }

  /**
   * Retrieves the list of sub directory names from the given path and returns
   * them in the same order in the list.
   * 
   * @param path
   *          the directory names shall be retrieved from
   * @return list of directory names
   */
  public static List<String> getDirectoryNames(String path) {

    if (java.io.File.separatorChar == '\\') {
      return new ArrayList<>(java.util.Arrays.asList(path.split("\\\\")));
    }

    return new ArrayList<>(java.util.Arrays.asList(path.split(""
        + java.io.File.separatorChar)));
  }

  /**
   * Read the given file and return the content as string. The filename should
   * be valid otherwise there will be a null pointer exception during the
   * creation of the string.
   * 
   * @param file
   *          the file to be read
   * @param charset
   *          the encoding to use when reading ascii file
   * @return content of the file as string representation
   */
  public static String readASCIIFile(java.io.File file, String charset) {

    char[] thechars = null;
    int index = 0;

    try (InputStreamReader reader =
        new InputStreamReader(
            new BufferedInputStream(new FileInputStream(file)), charset)) {
      int size = (int) file.length();
      thechars = new char[size];
      int count = 0;
      // read in the bytes from the input stream
      while ((count = reader.read(thechars, index, size)) > 0) {
        size -= count;
        index += count;
      }

    } catch (IOException e) {
      SimSystem.report(e);
    }
    return String.valueOf(thechars, 0, index);
  }

  /**
   * Read the given file and return the content as string. The filename should
   * be valid otherwise there will be a null pointer exception during the
   * creation of the string.
   * 
   * @param filename
   *          the name of the file
   * @param charset
   *          the encoding to use when reading ascii file
   * @return content of the file as string representation
   */
  public static String readASCIIFile(String filename, String charset) {
    return readASCIIFile(new java.io.File(filename), charset);
  }

  /**
   * Read the given file and return the content as array of strings (a line per
   * string). The file should exist.
   * 
   * Will only work if the max number of characters is < Integer.MAX_VALUE per
   * line and if the number of lines in the files is smaller Integer.MAX_VALUE
   * as well.
   * 
   * @param file
   *          the file to be read
   * @param charset
   *          the encoding to use when reading ascii file
   * @return content of the file as array of lines
   */
  public static List<String> readLargeASCIIFile(java.io.File file,
      String charset) {

    InputStreamReader fr = null;

    List<String> result = new ArrayList<>();

    try {
      fr =
          new InputStreamReader(new BufferedInputStream(new FileInputStream(
              file), 8192 * 2 * 2 * 2 * 2 * 2 * 2), charset);
      long size = file.length();
      boolean last = true;
      while (size > 0) {

        int chunk = 0;

        char[] thechars = null;
        int index = 0;

        if (size > Long.valueOf(Integer.MAX_VALUE - 10).longValue()) {
          chunk = Integer.MAX_VALUE - 10;
        } else {
          chunk = (int) size;
        }

        thechars = new char[chunk];
        int count = 0;

        // read in the bytes from the input stream
        while ((count = fr.read(thechars, index, chunk)) > 0) {
          size -= count;
          index += count;
          if (count == chunk) {
            break;
          }
        }

        String test = new String(thechars);

        String[] lines = test.split("\n");
        int start = 0;
        if (!last) {
          lines[lines.length - 1] += lines[0];
          start = 1;
        }
        if (test.endsWith("\n")) {
          last = true;
        } else {
          last = false;
        }
        test = "";
        result.addAll(java.util.Arrays.asList(lines).subList(start, lines.length));

      }

    } catch (IOException e) {
      SimSystem.report(e);
    } finally {
      try {
        if (fr != null) {
          fr.close();
        }
      } catch (IOException e2) {
        SimSystem.report(e2);
      }
    }
    return result;
  }

  /**
   * Writes content to ascii file using the specified charset for encoding.
   * 
   * @param filename
   *          the filename to write to
   * @param content
   *          the content to write to file
   * @param charset
   *          the charset to use
   * @return true, if successful
   */
  public static boolean writeASCIIFile(java.io.File filename, String content,
      String charset) {
    BufferedWriter writer = null;
    try {
      writer =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
              filename), charset));

      writer.write(content);
    } catch (IOException e) {
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
        return true;
      } catch (IOException e) {
      }
    }
    return false;
  }

  /**
   * Writes content to ascii file using the specified charset for encoding.
   * 
   * @param filename
   *          the filename to write to
   * @param content
   *          the content to write to file
   * @param charset
   *          the charset to use
   * @return true, if successful
   */
  public static boolean writeASCIIFile(String filename, String content,
      String charset) {
    return writeASCIIFile(new java.io.File(filename), content, charset);
  }

  /**
   * Read the given file and return the content as string. The filename should
   * be valid otherwise there will be a null pointer exception during the
   * creation of the string.
   * 
   * @param filename
   *          the name of the file
   * @param charset
   *          the encoding to use when reading ascii file
   * @return content of the file as string representation
   */
  public static String readASCIIFile(URI filename, String charset) {
    return readASCIIFile(new java.io.File(filename), charset);
  }

  /**
   * Gets a unique name for the file, i.e., if a file with the given name
   * already exists we increment a counter until we get a not yet used filename.
   * This method is synchronized to prevent identical file names for the case of
   * concurrent calls to the method with the same filename.
   * 
   * @param name
   *          the name of the file
   * @return the unique name (old name or old "nameNumber", e.g., myFile211,
   *         which means that myFile and myFile1 to myFile210 exist)
   */
  public static synchronized String getUniqueName(String name) {

    Pair<String, String> fileNE = getFileNameAndEnding(name);
    String fileName = fileNE.getFirstValue();
    String extension = fileNE.getSecondValue();

    java.io.File file = new java.io.File(fileName + extension);
    int c = 0;
    while (file.exists()) {
      c++;

      file = new java.io.File(fileName + c + extension);
    }
    if (c == 0) {
      return name;
    }
    return fileName + c + extension;
  }

  /**
   * Gets the file name (without ending) and the ending (with leading separator)
   * as separate strings.
   * 
   * @param name
   *          the name of the file
   * @return the tuple (file name, ending)
   */
  public static Pair<String, String> getFileNameAndEnding(String name) {

    String fileName = name;
    String extension = "";

    if (name.lastIndexOf('.') > name.lastIndexOf(java.io.File.separatorChar)) {
      fileName = fileName.substring(0, name.lastIndexOf('.'));
      extension = name.substring(name.lastIndexOf('.'));
    }

    return new Pair<>(fileName, extension);
  }

  /**
   * Gets a unique name for the file by searching for the first file (i.e.
   * minimal index >= startIndex) for which a file '[name][startIndex].[ending]'
   * does not yet exist.
   * 
   * @see Files#getUniqueName(String)
   * 
   * @param name
   *          the name of the file
   * @param startIndex
   *          the start index from which to start looking
   * @return the unique name of format '[name][first free index].[ending]'
   */
  public static synchronized String getUniqueNumberedName(String name,
      int startIndex) {

    Pair<String, String> fileNE = getFileNameAndEnding(name);

    int counter = startIndex;
    java.io.File file =
        new java.io.File(fileNE.getFirstValue() + counter
            + fileNE.getSecondValue());

    while (file.exists()) {
      counter++;
      file =
          new java.io.File(fileNE.getFirstValue() + counter
              + fileNE.getSecondValue());
    }

    return fileNE.getFirstValue() + counter + fileNE.getSecondValue();
  }

  /**
   * Composes a full file name (containing directory / path and the file name).
   * Automatically determines whether an operating system dependent file
   * separator char has to be inserted in between or whether the directory
   * passed already ends on such a character. Thus this method is a convenience
   * method.
   * 
   * @param directory
   *          the file with the filename given in the second parameter is /
   *          shall be located. The directory String might end with the
   *          delimiter char or not.
   * @param filename
   *          of the file in the directory passes
   * @return A string containing the directory + delimiter char + filename
   */
  public static String composeFilename(String directory, String filename) {
    String result = directory;
    String delim = String.valueOf(java.io.File.separatorChar);
    if (!result.endsWith(delim)) {
      result += java.io.File.separatorChar;
    }
    result += filename;
    return result;
  }

  /**
   * Returns true if {@link java.io.File#isAbsolute()} returns true.
   * 
   * @param fName
   * @return
   */
  public static boolean isAbsoluteFilename(String fName) {
    java.io.File file = new java.io.File(fName);
    return file.isAbsolute();
  }

  /**
   * Creates the folders on the path given if they don't exist.
   */
  public static void createDirectory(String path) {
    File file = new File(path);
    if (!file.exists() && !file.mkdirs()) {
      SimSystem.report(Level.WARNING, "Creating the path " + path + " failed!");
    }
  }

  /**
   * Creates or opens a file with the given name and return the object for
   * accessing the file.
   * 
   * @param name
   *          the name of the file to be created
   * @return the object for accessing the file or null if an error occurred
   */
  public static File createOrOpenFile(String name) {

    // open or create the file
    File result = new File(name);

    if (!result.exists()) {
      try {
        result.createNewFile();
      } catch (IOException ioe) {
        SimSystem.report(ioe);
        return null;
      }
    }
    return result;
  }

  /**
   * Write the passed columns to a sepChars separated file.
   * 
   * @param file
   *          the file to write to
   * @param columns
   *          the columns to be written
   * @return true if writing was successful
   */
  public static boolean writeToRAF(File file, String[] columns,
      String sepChars, String lineSep) {
    RandomAccessFile randStorage;
    try {
      randStorage = new RandomAccessFile(file, "rw");
      randStorage.seek(randStorage.length());
      for (int i = 0; i < columns.length; i++) {
        if (i != 0) {
          randStorage.writeBytes(sepChars);
        }
        randStorage.writeBytes(columns[i]);
      }
      randStorage.writeBytes(lineSep);
      randStorage.close();
    } catch (IOException e) {
      SimSystem.report(e);
      return false;
    }
    return true;
  }

  /**
   * Returns the filename (without path information) but including any
   * extension. The return value is equivalent to the java.io.File.getName()
   * methods return value.
   * 
   * @return name of the file
   */
  public static String getFileName(String fileNameWithPath) {
    File file = new File(fileNameWithPath);
    return file.getName();
  }
}
