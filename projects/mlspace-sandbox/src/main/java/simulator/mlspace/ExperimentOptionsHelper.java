package simulator.mlspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Helper class with method {@link #getOptions(String[])} for finding ini file
 * specified in cmd line arguments (second argument, or same name/different
 * extension as first argument) and inserting ini file settings to cmd line
 * argument list.
 *
 * @author Arne Bittig
 */
public final class ExperimentOptionsHelper {

  private static final String NO_ARGUMENTS_HELP_MESSAGE =
      "No model file given. Model name should be first argument. "
          + "Experiment configuration file name may be second argument "
          + "(unless it differs from model file name only in extension)."
          + " Otherwise experiment options may be specified "
          + "with the following command line arguments:\n";

  private static final String NO_INI_FILE_MSG =
      "No experiment configuration (ini) file given "
          + "and none matching the model name found.";

  /** expected extension(s) of configuration file */
  public static final String[] CONFIG_EXTENSIONS =
      { ".ini", ".cfg", ".conf", ".exp" };

  private static final String OVERRIDDEN_MODEL_VARIABLES_INI_SECTION =
      "Overridden model variables";

  // private static final String OBSERVATION_TARGETS_INI_SECTION =
  // "Observation targets";

  private ExperimentOptionsHelper() {}

  public static ExperimentOptions getOptions(String[] args)
      throws CmdLineException, InvalidFileFormatException, IOException {
    ArrayList<String> argsToParse = new ArrayList<>(Arrays.asList(args));
    String iniFileName = null;
    if (args.length > 1 && isConfigFileName(args[1])) {
      iniFileName = argsToParse.remove(1);
    } else if (args.length >= 1) {
      int fileEndingIndex = args[0].indexOf(".mls");
      if (fileEndingIndex == -1) {
        if (isConfigFileName(args[0])) {
          fileEndingIndex = args[0].length() - 4;
        } else {
          fileEndingIndex = args[0].length();
        }
      }
      iniFileName = findConfigFile(args[0].substring(0, fileEndingIndex));
    }

    if (iniFileName != null) {
      addIniContentToArgs(argsToParse, iniFileName);
    }

    final ExperimentOptions options = new ExperimentOptions();
    CmdLineParser cmdLineParser = new CmdLineParser(options);
    cmdLineParser.parseArgument(argsToParse);

    File modelfile = options.modelfile;
    if (modelfile == null || options.help) {
      System.err.println(NO_ARGUMENTS_HELP_MESSAGE);
      cmdLineParser.printUsage(System.out);
      System.exit(options.help ? 0 : 1);
      return null;
    }
    String modelFileNameAndPath = modelfile.getAbsolutePath();
    if (isConfigFileName(modelFileNameAndPath)) {
      // graceful handling of config file as first param if same-name model file
      // is present
      modelfile = new File(
          modelFileNameAndPath.substring(0, modelFileNameAndPath.length() - 4)
              + ".mls");
    }
    if (!modelfile.exists()) {
      modelfile = new File(modelFileNameAndPath + ".mls");
      if (!modelfile.exists()) {
        System.err.println("Model file does not exist: " + options.modelfile);
        System.exit(2);
        return null;
      }
    }
    options.modelfile = modelfile;
    ;

    if (iniFileName == null) {
      System.out.println(NO_INI_FILE_MSG);
    }
    options.iniFileName = iniFileName;
    return options;
  }

  private static String findConfigFile(String fileNameWithoutExtension) {
    for (String ext : CONFIG_EXTENSIONS) {
      String pathname = fileNameWithoutExtension + ext;
      if ((new File(pathname)).exists()) {
        return pathname;
      }
    }
    return null;
  }

  private static boolean isConfigFileName(String name) {
    for (String ext : CONFIG_EXTENSIONS) {
      if (name.endsWith(ext)) {
        return true;
      }
    }
    return false;
  }

  private static void addIniContentToArgs(ArrayList<String> toParse,
      String iniFileName) throws IOException, InvalidFileFormatException {
    Ini ini = new Ini(new File(iniFileName));
    Section overrides = ini.remove(OVERRIDDEN_MODEL_VARIABLES_INI_SECTION);
    if (overrides != null) {
      for (Map.Entry<String, String> e : overrides.entrySet()) {
        toParse.add(1, e.getKey() + "=" + e.getValue());
        toParse.add(1, ExperimentOptions.OVERRIDEN_MODEL_VARS);
      }
    }
    // // separate obs target section not helpful because keys may contain "="
    // // and thus key-value pairs are not parsed as intended
    // Section obsTargets = ini.remove(OBSERVATION_TARGETS_INI_SECTION);
    // StringBuilder obsTargetString = new StringBuilder();
    // if (obsTargets != null) {
    // for (Map.Entry<String, String> e : obsTargets.entrySet()) {
    // obsTargetString.append(dequote(e.getKey()));
    // obsTargetString.append('=');
    // obsTargetString.append(e.getValue());
    // obsTargetString.append(';');
    // }
    // }
    for (Section v : ini.values()) {
      for (Map.Entry<String, String> e : v.entrySet()) {
        // if (ExperimentOptions.obsTargetKeys.contains(e.getKey())) {
        // obsTargetString.append(e.getValue());
        // obsTargetString.append(';');
        // }
        String value = e.getValue();
        if (value != null && !value.isEmpty()
            && !value.equalsIgnoreCase("false")) {
          if (!value.equalsIgnoreCase("true")) {
            toParse.add(1, value);
          }
          toParse.add(1, "-" + e.getKey());
        }
      }
    }
    // if (obsTargetString.length() > 0) {
    // toParse.add(1, obsTargetString.substring(0, obsTargetString.length() - 1)
    // .toString()); // last ';' unwanted
    // toParse.add(1, "-" + ExperimentOptions.obsTargetKeys.get(0));
    // }
  }

  static String dequote(String obsTargetsToParse) {
    while (obsTargetsToParse.startsWith("\"")
        && obsTargetsToParse.endsWith("\"")) {
      obsTargetsToParse =
          obsTargetsToParse.substring(1, obsTargetsToParse.length() - 1);
    }
    // while (obsTargetsToParse.startsWith("'")
    // && obsTargetsToParse.endsWith("'")) {
    // obsTargetsToParse =
    // obsTargetsToParse.substring(1, obsTargetsToParse.length() - 1);
    // }
    return obsTargetsToParse;
  }

}
