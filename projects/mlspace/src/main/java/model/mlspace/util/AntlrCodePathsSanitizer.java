package model.mlspace.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Arne Bittig
 * @date 11.09.2012
 */
public final class AntlrCodePathsSanitizer {

  private static final String PATH_PART_TO_KEEP =
      "model\\mlspace\\reader\\antlr\\";

  private static final String PATH_STRING_TO_REMOVE =
      "C:\\Users\\Arne\\Code\\ab358\\projects\\mlspace\\src\\main\\java\\";

  private AntlrCodePathsSanitizer() {
  }

  /**
   * Remove references to absolute path from text-based (e.g. Java code) file.
   * Also, in the first line, remove the part after ".g " (where antlr usually
   * puts the build date & time, which is inconvenient in a version control
   * system)
   * 
   * @param path
   *          Path of file, also string to remove from file
   * @param filename
   *          Name and relative path of file
   * @return success value: true if no IOException was caught
   */
  private static boolean sanitize(String path, String filename) {
    try {
      String pathDQ = path.replace("\\", "\\\\");
      File inFile = new File(path + filename);
      File tmpFile = new File(path + filename + ".bak");
      if (tmpFile.exists() && !tmpFile.delete()) {
        System.err.println("Failed to delete tmp file " + tmpFile.getName());
      }
      if (!inFile.renameTo(tmpFile)) {
        System.err.println("Failed to rename input " + inFile.getName());
      }
      BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
      String line = reader.readLine();
      line = line.replace(pathDQ, "");
      int dotGPos = line.indexOf(".g ");
      if (dotGPos >= 0) {
        // else file has already been sanitized
        FileWriter writer = new FileWriter(inFile);
        writer.append(line.substring(0, dotGPos + 2) + "\n");
        while ((line = reader.readLine()) != null) {
          line = line.replace(pathDQ, "");
          writer.append(line);
          writer.append("\n");
        }
        writer.close();
      }
      reader.close();
      if (dotGPos == -1) {
        // file has already been sanitized -> undo rename
        if (!tmpFile.renameTo(inFile)) {
          System.err.println("Failed to undo rename of " + tmpFile.getName());
        }
      } else {
        tmpFile.deleteOnExit();
      }
      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  private static boolean sanitizebib(String pathandfilename) {
    try {
      File inFile = new File(pathandfilename);
      File tmpFile = new File(pathandfilename + ".bak");
      if (tmpFile.exists() && !tmpFile.delete()) {
        System.err.println("Failed to delete tmp file " + tmpFile.getName());
      }
      if (!inFile.renameTo(tmpFile)) {
        System.err.println("Failed to rename input " + inFile.getName());
      }
      BufferedReader reader = new BufferedReader(new FileReader(tmpFile));
      String line = reader.readLine();
      FileWriter writer = new FileWriter(inFile);
      while ((line = reader.readLine()) != null) {
        if (!line.contains("citeulike-") && !line.contains("keywords = ")
            && !line.contains("posted-at") && !line.contains("priority = ")
            && !line.contains("owner = ")) {
          writer.append(line);
          writer.append("\n");
        }
      }
      writer.close();
      reader.close();
      return true;

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      for (String arg : args) {
        sanitizebib(arg);
      }
      return;
    }
    String[] fileList =
        new File(PATH_STRING_TO_REMOVE + PATH_PART_TO_KEEP).list();
    for (String fileName : fileList) {
      if (fileName.endsWith(".java")) {
        sanitize(PATH_STRING_TO_REMOVE, PATH_PART_TO_KEEP + fileName);
        System.out.println("Treated " + fileName);
      }
    }
  }

}
