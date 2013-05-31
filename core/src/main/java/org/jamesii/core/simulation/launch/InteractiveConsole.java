/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.launch;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.loadbalancing.plugintype.AbstractLoadBalancingFactory;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.AbstractPartitioningFactory;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.plugintype.AbstractModelFactory;
import org.jamesii.core.model.plugintype.ModelFactory;
import org.jamesii.core.plugins.IFactoryInfo;
import org.jamesii.core.plugins.IId;
import org.jamesii.core.plugins.IPluginData;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.simulation.distributed.model.RemoteModelFactory;
import org.jamesii.core.simulation.distributed.processor.AbstractREMOTEProcessorFactory;
import org.jamesii.core.simulation.distributed.processor.RemoteProcessorFactory;
import org.jamesii.core.simulationrun.SimulationRun;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStop;
import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.eventset.plugintype.EventQueueFactory;
import org.jamesii.core.util.misc.Strings;

/**
 * Parsing of the console input: first token must be a valid command, if this
 * command has subcommands and the next tokens match 'em they are interpreted as
 * sub commands. If not they are interpreted as parameters.
 * 
 * NOTE: Currently the console is slightly out of date. It had been created at a
 * time where an experiment was exactly one simulation run. Hoever, it may still
 * of use if one needs an interactive possibility to step through an execution.
 * 
 * Invalid parameters for commands having no parameters are ignored!
 * 
 * @author Jan Himmelspach
 */
public class InteractiveConsole {

  /**
   * The Class Command.
   * 
   * @author Jan Himmelspach
   */
  private class Command {

    /** The action. */
    private CommandAction action;

    /** The command. */
    private String command;

    /** The comment. */
    private String comment;

    /** StreamTokenizer type e.g. TT_WORD */
    private boolean parameter = false;

    /** The sub commands. */
    private Map<String, Command> subCommands = new HashMap<>();

    /**
     * Instantiates a new command.
     * 
     * @param command
     *          the command
     * @param comment
     *          the comment
     * @param action
     *          the action
     */
    public Command(String command, String comment, CommandAction action) {
      super();
      this.command = command;
      this.comment = comment;
      this.action = action;
    }

    /**
     * Instantiates a new command.
     * 
     * @param command
     *          the command
     * @param comment
     *          the comment
     * @param action
     *          the action
     * @param parameter
     *          the parameter
     */
    public Command(String command, String comment, CommandAction action,
        boolean parameter) {
      this(command, comment, action);
      this.parameter = parameter;
    }

    /**
     * Gets the sub command.
     * 
     * @param commandStr
     *          the command str
     * 
     * @return the sub command
     */
    public Command getSubCommand(String commandStr) {
      return subCommands.get(commandStr);
    }

    /**
     * Checks for sub command.
     * 
     * @return true, if successful
     */
    public boolean hasSubCommand() {
      return subCommands.size() > 0;
    }

    /**
     * Checks for sub command.
     * 
     * @param commandStr
     *          the command str
     * 
     * @return true, if successful
     */
    public boolean hasSubCommand(String commandStr) {
      return subCommands.containsKey(commandStr);
    }

    /**
     * Register sub command.
     * 
     * @param com
     *          the com
     */
    public void registerSubCommand(Command com) {
      subCommands.put(com.command, com);
    }

    /**
     * Takes parameter.
     * 
     * @return true, if successful
     */
    public boolean takesParameter() {
      return parameter;
    }

    @Override
    public String toString() {
      StringBuffer result =
          new StringBuffer(bold + command + normal + "\n"
              + Strings.indent(comment, INDENT));
      if (subCommands.size() > 0) {
        if (subCommands.size() > 1) {
          result.append("\n" + INDENT + "Valid subcommands for '" + command
              + "' are:");
        } else {
          result.append("\n" + INDENT + "Valid subcommand for '" + command
              + "' is:");
        }
        for (Command c : subCommands.values()) {
          result.append("\n" + Strings.indent(c.toString(), INDENT));
        }
      }

      return result.toString();
    }

  }

  /**
   * A command action gets executed when a command has been entered into the
   * command line.
   * 
   * @author Jan Himmelspach
   */
  private abstract class CommandAction {

    /**
     * Execute.
     * 
     * @param param
     *          the param
     * 
     * @return true, if successful
     */
    public abstract boolean execute(String param);
  }

  /**
   * Special tokenizer which treats string encapsulated by two "" as single
   * token ...
   * 
   * @author Jan Himmelspach
   */
  private class ConsoleTokenizer {

    /** The tokenizer. */
    private StringTokenizer tokenizer;

    /**
     * Instantiates a new console tokenizer.
     * 
     * @param s
     *          the s
     */
    public ConsoleTokenizer(String s) {
      super();
      tokenizer = new StringTokenizer(s, " ");
    }

    /**
     * Checks for more tokens.
     * 
     * @return true, if successful
     */
    public boolean hasMoreTokens() {
      return tokenizer.hasMoreTokens();
    }

    /**
     * get the next token, thereby a token may either be a single word
     * (delimited by a space) or any string encapsulated by ".
     * 
     * @return the string
     */
    public String nextToken() {
      String result = tokenizer.nextToken();
      if (result.charAt(0) == '"') {
        // skip/remove leading "
        result = result.substring(1);
        while (tokenizer.hasMoreTokens()) {
          String token = tokenizer.nextToken();
          result += " " + token;
          if (token.charAt(token.length() - 1) == '"') {
            // remove trailing "
            result = result.substring(0, result.length() - 1);
            break;
          }
        }
      }
      return result;
    }

  }

  /**
   * The Class HelpCommandAction.
   */
  private class HelpCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      help(param);
      return true;
    }
  }

  /**
   * The Class HelpOnAllIdentsCommandAction.
   */
  private class HelpOnAllIdentsCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      out.println(SimSystem.getRegistry().getInfo());
      return true;
    }
  }

  /**
   * The Class HelpOnCommandAction.
   */
  private class HelpOnCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      helpOn(param);
      return true;
    }
  }

  /**
   * A history list for the old commands.
   * 
   * @author Jan Himmelspach
   */
  private class LastCommands {

    /** The commands. */
    private List<String> commands = new ArrayList<>();

    /** The current. */
    private int current = -1;

    /** The max num. */
    private int maxNum = -1;

    /**
     * Instantiates a new last commands.
     */
    public LastCommands() {
      super();
    }

    /**
     * Instantiates a new last commands.
     * 
     * @param maxNum
     *          the max num
     */
    @SuppressWarnings("unused")
    public LastCommands(int maxNum) {
      super();
      this.maxNum = maxNum;
    }

    /**
     * Adds the.
     * 
     * @param command
     *          the command
     */
    public void add(String command) {
      if ((maxNum != -1) && (commands.size() == maxNum)) {
        commands.remove(0);
      }
      commands.add(command);
      current = commands.size();
    }

    /**
     * Gets the.
     * 
     * @return the string
     */
    public String get() {
      return commands.get(current);
    }

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public String getList() {
      StringBuilder result = new StringBuilder();
      for (String s : commands) {
        result.append(s);
        result.append("\n");
      }
      return result.toString();
    }

    /**
     * Gets the next.
     * 
     * @return the next
     */
    @SuppressWarnings("unused")
    public String getNext() {
      current++;
      if (current == commands.size()) {
        current = 0;
      }
      return get();
    }

    /**
     * Gets the prev.
     * 
     * @return the prev
     */
    @SuppressWarnings("unused")
    public String getPrev() {
      current--;
      if (current < 0) {
        current = commands.size();
      }
      return get();
    }

  }

  /**
   * The Class NextStepCommandAction.
   */
  private class NextStepCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      ((IRunnable) simulation.getProcessorInfo().getLocal()).next(1);
      return true;
    }
  }

  /**
   * The Class PauseCommandAction.
   */
  private class PauseCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      ((IRunnable) simulation.getProcessorInfo().getLocal()).pause();
      return true;
    }
  }

  /**
   * Simple parser for prefix expressions.
   * 
   * @author Jan Himmelspach
   */
  private class PrefixParser {

    /** The expression. */
    private String expression;

    /** The OPSYMBOLS. */
    private final char[] OPSYMBOLS = new char[] { '+', '-', '*', '/', ' ', '^',
        '§' };

    /**
     * Instantiates a new prefix parser.
     * 
     * @param expression
     *          the expression
     */
    public PrefixParser(String expression) {
      super();
      this.expression = expression;
    }

    /**
     * Gets the math token.
     * 
     * @param expressionStr
     *          the expression str
     * 
     * @return the math token
     */
    private String getMathToken(String expressionStr) {
      int i = 1;
      while (!Strings.startsWith(expressionStr.substring(i - 1), OPSYMBOLS)
          && (expressionStr.length() > i)) {
        i++;
      }
      if ((i > 1) && (expressionStr.length() != i)) {
        i--;
      }
      return expressionStr.substring(0, i);
    }

    /**
     * Parses the math expression.
     * 
     * @return the double
     */
    private Double parseMathExpression() {

      if (expression.charAt(0) == ' ') {
        expression = expression.substring(1);
      }

      String token = getMathToken(expression);

      expression = expression.substring(token.length());

      // out.println(token);

      char c = token.charAt(0);

      switch (c) {
      case '+': {
        Double t1 = parseMathExpression();
        // out.println(expression);
        return t1 + parseMathExpression();
      }
      case '-': {
        return parseMathExpression() - parseMathExpression();
      }
      case '*': {
        return parseMathExpression() * parseMathExpression();
      }
      case '/': {
        return parseMathExpression() / parseMathExpression();
      }
      case '^': {
        return Math.pow(parseMathExpression(), parseMathExpression());
      }
      case '§': {
        return Math.sqrt(parseMathExpression());
      }
      default: {
        // Double d;
        try {
          return new Double(token.trim());
        } catch (RuntimeException e) {
          return new Double((String) variables.get(token));
        }

      }
      }
    }

  }

  // private String UNDERLINE = "";
  //
  // private String CYAN = "";
  //
  // private String GREEN = "";

  /**
   * The Class QuitCommandAction.
   */
  private class QuitCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      out.println("User quit - quitting " + SimSystem.SIMSYSTEM);
      SimSystem.shutDown(0);
      return true;
    }
  }

  /**
   * The Class RunGarbageCollectorCommandAction.
   */
  private class RunGarbageCollectorCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      System.gc(); // NOSONAR: Garbage collecting cmd command needs to call the
                   // GC
      return true;
    }
  }

  /**
   * The Class RunStepsCommandAction.
   */
  private class RunStepsCommandAction extends CommandAction implements Runnable {

    /** The steps. */
    private int steps = 0;

    @Override
    public boolean execute(String param) {
      try {
        steps = Integer.parseInt(param);
      } catch (Exception e) {
        out.println("Invalid parameter for this command! (" + param
            + ") is not a valid number.");
        return false;
      }

      if (((IRunnable) simulation.getProcessorInfo().getLocal()).isRunning()) {
        return false;
      }

      if (executeModel != null) {
        executeModel = null;
      }

      if (executeModel == null) {
        executeModel = new Thread(this);
        executeModel.start();
        return true;
      }
      return false;

    }

    @Override
    public void run() {
      ((IRunnable) simulation.getProcessorInfo().getLocal()).next(steps);
    }
  }

  /**
   * The Class RunToCommandAction.
   */
  private class RunToCommandAction extends CommandAction implements Runnable {

    /** The runto. */
    private double runto = -1;

    @Override
    public boolean execute(String param) {

      if (((IRunnable) simulation.getProcessorInfo().getLocal()).isRunning()) {
        return false;
      }

      try {
        runto = ((Double.parseDouble(param)));
      } catch (Exception e) {
        out.println("Invalid parameter for this command! (" + param
            + ") is not a valid number.");
        return false;
      }

      if (executeModel != null) {
        executeModel = null;
      }

      if (executeModel == null) {
        executeModel = new Thread(this);
        executeModel.start();
        return true;
      }
      return false;

    }

    @Override
    public void run() {
      ((IRunnable) simulation.getProcessorInfo().getLocal())
          .run(new SimTimeStop(simulation, runto));
    }
  }

  // --- ASCII-COMMANDLINE TO CONTROL SIMULATION DYNAMICALLY -------------

  /**
   * The Class ShowCommandAction.
   */
  private class ShowCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      out.println(SimSystem.SIMSYSTEM + " - running interactively");
      out.println("Version " + SimSystem.VERSION + "\n");
      System.out
          .println("For more information use the show subcommands (help show)");
      return true;
    }
  }

  /**
   * Can be directly used (for a fixed list of plugins) or in a more generic way
   * by using a descendant class.
   * 
   * @param <E>
   *          *
   * @author Jan Himmelspach
   */
  private class ShowDetailedPluginsCommandAction<E> extends CommandAction {

    /** The list. */
    private List<E> list;

    /**
     * Instantiates a new show detailed plugins command action.
     */
    public ShowDetailedPluginsCommandAction() {
      super();
      this.setList(null);
    }

    /**
     * Instantiates a new show detailed plugins command action.
     * 
     * @param list
     *          the list
     */
    public ShowDetailedPluginsCommandAction(List<E> list) {
      super();
      this.setList(list);
    }

    @Override
    public boolean execute(String param) {

      StringBuilder s = new StringBuilder();

      List<IPluginData> plugins = SimSystem.getRegistry().getPlugins();

      for (E pf : getList()) {
        String name = pf.getClass().getName();
        s.append("\n");
        s.append(pf);
        for (IPluginData pd : plugins) {

          for (IFactoryInfo fi : pd.getFactories()) {
            if (fi.getClassname().equals(name)) {
              List<IId> dependencies = pd.getDependencies();
              s.append("\n depends on");
              for (IId id : dependencies) {
                s.append("\n - ");
                s.append(id.getName());
              }
              if (dependencies.size() == 0) {
                s.append("\n  nothing given");
              }
            }
          }
        }
      }
      out.println(s);

      return true;
    }

    /**
     * @return the list
     */
    public List<E> getList() {
      return list;
    }

    /**
     * @param list
     *          the list to set
     */
    public void setList(List<E> list) {
      this.list = list;
    }
  }

  /**
   * The Class ShowEventQueuePluginsCommandAction.
   */
  private class ShowEventQueuePluginsCommandAction extends
      ShowDetailedPluginsCommandAction<EventQueueFactory> {

    /**
     * Instantiates a new show event queue plugins command action.
     */
    public ShowEventQueuePluginsCommandAction() {
      super();
      List<EventQueueFactory> facs =
          SimSystem.getRegistry().getFactories(EventQueueFactory.class);
      setList(facs);
    }

  }

  /**
   * The Class ShowModelCommandAction.
   */
  private class ShowModelCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      out.println(simulation.getModel().getCompleteInfoString());

      return true;
    }
  }

  /**
   * The Class ShowModellingFormalismsPluginsCommandAction.
   */
  private class ShowModellingFormalismsPluginsCommandAction extends
      ShowDetailedPluginsCommandAction<ModelFactory> {

    /**
     * Instantiates a new show modelling formalisms plugins command action.
     */
    public ShowModellingFormalismsPluginsCommandAction() {
      super();
      List<ModelFactory> facs =
          SimSystem.getRegistry().getFactories(ModelFactory.class);
      setList(facs);
    }

  }

  /**
   * The Class ShowPluginsCommandAction.
   */
  private class ShowPluginsCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      out.println("List of all installed plugins\n"
          + SimSystem.getRegistry().getPluginList());

      return true;
    }
  }

  /**
   * The Class ShowProcessorCommandAction.
   */
  private class ShowProcessorCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      out.println("Information about the processor\n");

      out.println("Processor: "
          + simulation.getProcessorInfo().getLocal().getCompleteInfoString()
          + "\n");

      return true;
    }
  }

  /**
   * The Class ShowREMOTEModelPluginsCommandAction.
   */
  private class ShowREMOTEModelPluginsCommandAction extends
      ShowDetailedPluginsCommandAction<RemoteModelFactory<?>> {

    /**
     * Instantiates a new show remote model plugins command action.
     */
    public ShowREMOTEModelPluginsCommandAction() {
      super();

      List<RemoteModelFactory<?>> facs =
          SimSystem.getRegistry().getFactories(AbstractModelFactory.class);
      setList(facs);
    }

  }

  /**
   * The Class ShowREMOTEProcessorPluginsCommandAction.
   */
  private class ShowREMOTEProcessorPluginsCommandAction extends
      ShowDetailedPluginsCommandAction<RemoteProcessorFactory<?>> {

    /**
     * Instantiates a new show remote processor plugins command action.
     */
    public ShowREMOTEProcessorPluginsCommandAction() {
      super();
      List<RemoteProcessorFactory<?>> facs =
          SimSystem.getRegistry().getFactories(
              AbstractREMOTEProcessorFactory.class);
      setList(facs);
    }

  }

  /**
   * The Class ShowSelectedPluginsCommandAction.
   */
  private class ShowSelectedPluginsCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      System.out.println("List of plugins currently used");

      System.out.println(" - not implemented yet!!!");

      return true;
    }

  }

  /**
   * The Class ShowSimulationAlgorithmPluginsCommandAction.
   */
  private class ShowSimulationAlgorithmPluginsCommandAction extends
      ShowDetailedPluginsCommandAction<ProcessorFactory> {

    /**
     * Instantiates a new show simulation algorithm plugins command action.
     */
    public ShowSimulationAlgorithmPluginsCommandAction() {
      super();
      List<ProcessorFactory> facs =
          SimSystem.getRegistry().getFactories(AbstractProcessorFactory.class);
      setList(facs);
    }

  }

  /**
   * The Class ShowSimulationCommandAction.
   */
  private class ShowSimulationCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      out.println(simulation.getCompleteInfoString());

      return true;
    }
  }

  /**
   * The Class ShowStatusCommandAction.
   */
  private class ShowStatusCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      IRunnable r = ((IRunnable) simulation.getProcessorInfo().getLocal());

      if (r.isRunning()) {
        out.print("Running");
        if (r.isPausing()) {
          out.print(" but currently paused");
        }
        if (r.isStopping()) {
          out.print(" and trying to stop");
        }
      } else {
        if (r.isStopping()) {
          out.print("Simulation stopped.");
        }
//        } else {
//          if (simulation.getProcessorInfo().getLocal().getTime() > 0) {
//            out.print("Simulation has run up to "
//                + simulation.getProcessorInfo().getLocal().getTime()
//                + " but is currently not running");
//          } else {
//            out.print("Simulation not started yet");
//          }
//        }
      }

      out.println("\nCurrent time: "
          + simulation.getProcessorInfo().getLocal().getTime());

      return true;
    }
  }

  /**
   * The Class ShowVMCommandAction.
   */
  private class ShowVMCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {

      out.println(SimSystem.getVMInfo());

      return true;
    }
  }

  /**
   * The Class StartCommandAction.
   */
  private class StartCommandAction extends CommandAction implements Runnable {

    @Override
    public boolean execute(String param) {
      if (executeModel == null) {
        executeModel = new Thread(this);
        executeModel.start();
        return true;
      }
      return false;
    }

    @Override
    public void run() {
      ((IRunnable) simulation.getProcessorInfo().getLocal()).run(simulation
          .getStopPolicy());
    }
  }

  /**
   * The Class StopCommandAction.
   */
  private class StopCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      if (executeModel == null) {
        return false;
      }
      simulation.stopProcessor();

      return true;
    }
  }

  /**
   * The Class VariableCommandAction.
   */
  private class VariableCommandAction extends CommandAction {

    @Override
    public boolean execute(String param) {
      // param is either ident=xyz or simply an ident
      if (param.indexOf('=') == -1) {
        out.println(variables.get(param));
      } else {
        String ident = param.substring(0, param.indexOf('='));
        String value = param.substring(param.indexOf('=') + 1, param.length());
        if (value.compareTo("null") == 0) {
          variables.add(ident, null);
        } else {
          variables.add(ident, parseExpression(value));
        }

      }
      return true;
    }
  }

  /**
   * Internal class which provides some slots to be used at the command line.
   * 
   * @author Jan Himmelspach
   */
  private class Variables {

    /** The slots. */
    private Map<String, Object> slots = new HashMap<>();

    /**
     * Adds the ident of a variable to the list of slots maintained by this
     * instance.
     * 
     * @param ident
     *          the ident to be added
     */
    @SuppressWarnings("unused")
    public void add(String ident) {
      add(ident, null);
    }

    /**
     * Adds the.
     * 
     * @param ident
     *          the ident
     * @param value
     *          the value
     */
    public void add(String ident, Object value) {
      slots.put(ident, value);
    }

    /**
     * Gets the.
     * 
     * @param <T>
     * 
     * @param ident
     *          the ident
     * 
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String ident) {
      return (T) slots.get(ident);
    }

    /**
     * Gets the list.
     * 
     * @return the list
     */
    public String getList() {
      StringBuilder result = new StringBuilder();
      for (String s : slots.keySet()) {
        result.append(s);
        result.append(" = ");
        result.append(slots.get(s).toString());
        result.append("\n");
      }
      return result.toString();
    }

  }

  // private static final String BOLD = "\u001b[1m";
  /** The BOLD. */
  private String bold = "";

  /** The list of available commands See registerCommand for adding commands. */
  private Map<String, Command> commands = new HashMap<>();

  /**
   * Thread used internally for concurrent model execution so that further
   * inputs to the console are possible.
   */
  private Thread executeModel = null;

  /** The in. */
  private InputStream in = System.in;

  /** The INDENT. */
  public static final String INDENT = "  ";// "\t";

  /** List of last commands. */
  private LastCommands lastCommands = new LastCommands();

  /**
   * Internal flag for inidacting whether to print the time needed for execution
   * the command or not.
   */
  private boolean measureTime = false;

  // private static final String NORMAL = "\u001b[0m";
  /** The NORMAL. */
  private String normal = "";

  /**
   * The stream outputs of the console are written to, maybe System.out Does
   * only redirect console outputs!
   */
  private PrintStream out = System.out;

  /** The prompt of the console. */
  private final String PROMPT = "%";

  /** A link to a simulation object with a loaded and initialized model. */
  private SimulationRun simulation = null;

  /** Provide some slots for storing information to be reused. */
  private Variables variables = new Variables();

  /**
   * Instantiates a new interactive console.
   */
  public InteractiveConsole() {
    this(false);
  }

  /**
   * NOTE: enableColouring does only work if the output stream/console supports
   * ANSI. Under Windows that's usually not true.
   * 
   * @param enableColouring
   *          the enable colouring
   */
  public InteractiveConsole(boolean enableColouring) {
    this(System.out, System.in, enableColouring);
  }

  /**
   * The Constructor.
   * 
   * @param out
   *          the out
   * @param in
   *          the in
   * @param enableColouring
   *          the enable colouring
   */
  public InteractiveConsole(PrintStream out, InputStream in,
      boolean enableColouring) {
    super();

    if (enableColouring) {
      normal = "\u001b[0m";
      bold = "\u001b[1m";
      // UNDERLINE = "\u001b[4m";
      // CYAN = "\u001b[36m";
      // GREEN = "\u001b[32m";
    }

    this.out = out;
    this.in = in;

    init();

  }

  /**
   * This method starts a simple ascii commandline, where you can control this
   * simulation with. This simulation is blocked when this method runs, as it is
   * synchronized.
   * <P>
   * 
   * For a complete list of all commands, that are available at the prompt, type
   * 'help;' and end the line up with a semicolon followed by return.
   * <P>
   * 
   * If you exit the command line and the given flag <CODE>onExitKillVM</CODE>
   * is set to <CODE>false</CODE> you may restart the command line later on, as
   * this simulation's virtual machine is not killed on exit. Otherwise the
   * virtual machine is killed and errorcode 0 is returned to the underlying
   * operating system. <BR>
   * If <CODE>measureTime</CODE> is <CODE>true</CODE> then the time spent to
   * execute the typed command is shown afterwards, otherwise not.
   */
  public final synchronized void commandLine() {

    /**
     * This slot holds a buffered reader wrapping stream System.in.
     */
    BufferedReader reader;

    // setup stream tokenizer

    reader = new BufferedReader(new InputStreamReader(in));

    out.println(SimSystem.SIMSYSTEM
        + " running in interactive mode. For a list of commands type 'help'.");

    // command line follows command line
    while (true) {

      // show prompt
      /*
       * out.print("\n[" + simulationBeginTime + ";" + timeOfLastEvent + "|" +
       * timeOfNextEvent + "] ");
       */

      out.print("\n" + getPrompt() + " ");

      try {

        // +++ PARSE COMMAND LINE +++
        parseAndExecuteCommand(reader.readLine());

      } catch (Exception e) {
        SimSystem.report(e);
      }
    }
  }

  /**
   * Loads factory list, swallowing any exceptions.
   * 
   * @param <F>
   *          the type of the factories to be returned
   * 
   * @param abstractFactory
   *          the abstract factory
   * @param type
   *          the type
   * 
   * @return the factory list
   * 
   * 
   */
  <F extends Factory<?>> List<F> getFactoryList(
      Class<? extends AbstractFactory<F>> abstractFactory, String type) {

    List<F> factories = null;

    try {
      factories = SimSystem.getRegistry().getFactoryList(abstractFactory, null);
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "An error occurred while attempting to load plugins for type '"
              + type + "'. No factories have been loaded.");
      factories = new ArrayList<>();
    }

    return factories;
  }

  /**
   * Returns the prompt. In the most simple variant the prompt is just the
   * string defined in PROMPT. This method must be replaced / extended for more
   * complex prompts ....
   * 
   * @return the prompt
   */
  protected String getPrompt() {
    return PROMPT;
  }

  /**
   * Reads the array of all known commands and displays it on the screen.
   * 
   * @param param
   *          the param
   */
  private final void help(String param) {
    if (param == null) {

      out.println("\n** command list **\n");

      for (Command c : commands.values()) {
        out.println(c);
      }

      return;
    }
    if (param.compareTo("general") == 0) {
      out.print("\n** general console info **\n\n");
      out.println("Simply type in commands and confirm 'em by pressing ENTER");
      out.println("  for a list of commands type 'help'");
      out.println("  for help on a special command type 'help on theCommand'");
      out.println("  for this help type 'help general'");
      out.println("Use a pair \" \" for encapsulating parameters containing spaces");
      out.println("You can use simple variables (read/write). Define 'em by '$ ident=xyz'");
      out.println("You can use simple arithmetic expressions (must be in prefix notation).");
      out.println("  " + getPrompt() + " +- 1 2 3");
      out.println("  " + getPrompt() + " prefix \"+- 1 3 4\"");
      out.println("  " + getPrompt() + " skip til \"+- 1 3 4\"");
      out.println("  " + getPrompt() + " skip til \"+- 1 3 var1\"");
      out.println("");
    } else {
      Command c = commands.get(param);
      if (c == null) {
        out.println("Unknown command " + param);
      } else {
        out.println(c);
      }

    }
    out.println();
  }

  /**
   * Help on.
   * 
   * @param aboutWhat
   *          the about what
   */
  private final void helpOn(String aboutWhat) {
    out.println(SimSystem.getRegistry().getInformation(aboutWhat));
  }

  /**
   * Creates the list of available commandline commands
   * 
   * The commands are registered into the gloal command's list.
   */
  public void init() {

    registerCommand("start", "Starts a simulation run",
        new StartCommandAction());
    registerCommand("stop", "Stops a simulation run", new StopCommandAction());
    registerCommand("pause", "Pause a simulation run", new PauseCommandAction());

    Command skip =
        registerCommand(
            "skip",
            "Next step of a simulation run. Without an additional parameter only one step gets executed.",
            new NextStepCommandAction());
    Command skipNext =
        new Command("next",
            "usage: skip next m\nNext m steps of a simulation run",
            new RunStepsCommandAction(), true);
    skip.registerSubCommand(skipNext);
    Command skipTil =
        new Command("til",
            "usage: skip til time\nSkip until given time is reached",
            new RunToCommandAction(), true);
    skip.registerSubCommand(skipTil);

    registerCommand("quit", "Quit " + SimSystem.SIMSYSTEM,
        new QuitCommandAction());
    Command hc =
        registerCommand(
            "help",
            "Print help - without a valid sub command the list of all available interactive commandline commands is printed.\n Help 'command' gives the help for command.",
            new HelpCommandAction());
    Command hcOn =
        new Command(
            "on",
            "usage: help on ???\nthereby ??? must be a valid ident out of all registered information objects",
            new HelpOnCommandAction(), true);
    hc.registerSubCommand(hcOn);
    Command hcOnAllIdent =
        new Command("allidents", "List all idents",
            new HelpOnAllIdentsCommandAction());
    hcOn.registerSubCommand(hcOnAllIdent);

    Command show =
        registerCommand(
            "show",
            "Show (sparse) general information. Use one of the subcommands for getting more detailed information.",
            new ShowCommandAction());
    Command showModel =
        new Command(
            "model",
            "Shows the model. WARNING: Large model structures may lead to incredible memory consumption during collection - thus it may take a while or even crash your VM.",
            new ShowModelCommandAction());
    show.registerSubCommand(showModel);
    Command showProcessor =
        new Command(
            "processor",
            "Shows the processor. WARNING: Large processing structures may lead to incredible memory consumption during collection - thus it may take a while or even crash your VM",
            new ShowProcessorCommandAction());
    show.registerSubCommand(showProcessor);
    Command showSimulation =
        new Command("simulation", "Shows information about the simulation",
            new ShowSimulationCommandAction());
    show.registerSubCommand(showSimulation);
    Command showVM =
        new Command("vm", "Shows information about the virtual machine",
            new ShowVMCommandAction());
    show.registerSubCommand(showVM);
    Command showStatus =
        new Command("status",
            "Shows information about the simulation's status",
            new ShowStatusCommandAction());
    show.registerSubCommand(showStatus);
    Command showElapsedTime =
        new Command(
            "ctime",
            "Toggle flag whether to show the time needed for executing a command or not",
            new CommandAction() {
              @Override
              public boolean execute(String param) {
                measureTime = !measureTime;
                if (measureTime) {
                  out.println("Measuring of time now enabled");
                } else {
                  out.println("Measuring of time now disabled");
                }

                return true;
              }
            });
    show.registerSubCommand(showElapsedTime);
    Command showPlugins =
        new Command("plugins", "Shows a list of all installed plugins",
            new ShowPluginsCommandAction());
    show.registerSubCommand(showPlugins);

    Command showModellingFormalismsPlugins =
        new Command("formalisms",
            "Shows a list of all installed modelling formalism plugins",
            new ShowModellingFormalismsPluginsCommandAction());
    show.registerSubCommand(showModellingFormalismsPlugins);

    Command showSimulationAlgorithmPlugins =
        new Command("simalgs",
            "Shows a list of all installed simulation algorithms",
            new ShowSimulationAlgorithmPluginsCommandAction());
    show.registerSubCommand(showSimulationAlgorithmPlugins);

    Command showEventQueuePlugins =
        new Command("eventqueues",
            "Shows a list of all installed event queues",
            new ShowEventQueuePluginsCommandAction());
    show.registerSubCommand(showEventQueuePlugins);

    Command showREMOTEProcessorPlugins =
        new Command(
            "remoteprocessor",
            "Shows a list of all installed remote processor communication schemes",
            new ShowREMOTEProcessorPluginsCommandAction());
    show.registerSubCommand(showREMOTEProcessorPlugins);

    Command showREMOTEPModelPlugins =
        new Command("remotemodel",
            "Shows a list of all installed remote model communication schemes",
            new ShowREMOTEModelPluginsCommandAction());
    show.registerSubCommand(showREMOTEPModelPlugins);

    Command showPartitioningPlugins =
        new Command("partalgs",
            "Shows a list of all installed partioning algorithms",
            new ShowDetailedPluginsCommandAction<>(getFactoryList(
                AbstractPartitioningFactory.class, "partalgs")));
    show.registerSubCommand(showPartitioningPlugins);

    Command showLoadBalancingPlugins =
        new Command("lbalgs",
            "Shows a list of all installed load balancing algorithms",
            new ShowDetailedPluginsCommandAction<>(getFactoryList(
                AbstractLoadBalancingFactory.class, "lbalgs")));
    show.registerSubCommand(showLoadBalancingPlugins);

    Command showUsedPlugins =
        new Command("usedplugins",
            "Shows a list of all currently used plugins",
            new ShowSelectedPluginsCommandAction());
    show.registerSubCommand(showUsedPlugins);

    Command usedcommands =
        new Command("usedcommands",
            "Show list of commands already used during this console uptime",
            new CommandAction() {
              @Override
              public boolean execute(String param) {
                out.print(lastCommands.getList());

                return true;
              }
            });
    show.registerSubCommand(usedcommands);

    Command showVariables =
        new Command("variables",
            "Shows the currently available/used/set (command line) variables",
            new CommandAction() {
              @Override
              public boolean execute(String param) {
                out.print(variables.getList());

                return true;
              }
            });
    show.registerSubCommand(showVariables);

    registerCommand(new Command(
        "&",
        "Variable operation. Syntax:\n $ ident=xyz sets variable named ident to xyz - Thereby xyz maybe a valid prefix expression\n $ident retrieves variable content",
        new VariableCommandAction(), true));

    registerCommand(new Command(
        "prefix",
        "Simple prefix evaluator. Just type in an prefix expression and get back the result. E.g. \"+- 1 2 3\" or \"+* 2 var1 3\" whereby var1 must be a previously defined variable.",
        new CommandAction() {
          @Override
          public boolean execute(String param) {
            out.print(param);// parseExpression(param));
            return true;
          }
        }, true));

    registerCommand("rungc", "Run the garbage collector",
        new RunGarbageCollectorCommandAction());
  }

  /**
   * Gets a string (which is hopefully a valid commandLine) and parses it. Thus
   * gets the main command, sub commands and parameters, looks up the command
   * list for the approbiate actionm to be taken and executes this action
   * 
   * @param commandLine
   *          the command line
   */
  public void parseAndExecuteCommand(String commandLine) {

    /**
     * Stores the found command (1st word on the commandline).
     */
    String command;

    /**
     * Stopwatch for measuring execution time
     */
    StopWatch stopwatch = new StopWatch();

    /**
     * This slot holds the string tonkenizer used to break the data, typed by
     * the user into the stream System.in, into tokens.
     */
    ConsoleTokenizer tokenizer;

    // slots indicating chosen command
    // CommandAction action = null; whatever I wanted to do with this ...

    String param = null;

    lastCommands.add(commandLine);

    tokenizer = new ConsoleTokenizer(commandLine);

    if (tokenizer.hasMoreTokens()) {

      // read first command (must be a word)
      command = tokenizer.nextToken();

      /*
       * if ((command.compareTo("") == 0) && (tokenizer.hasMoreTokens())) {
       * command = parseExpression(c) }
       */

      Command com = commands.get(command);

      if (com == null) {
        try {
          out.println(parseExpression(commandLine));
        } catch (RuntimeException e) {
          out.println("Invalid expression and unknown command '" + command
              + "' Type help for getting more information about valid commands");
        }

      } else {

        Command subCom = com;
        while ((subCom.hasSubCommand() || subCom.takesParameter())
            && tokenizer.hasMoreTokens()) {

          // System.err.println(command + " with sub part");

          // read next token and skip first token
          command = tokenizer.nextToken();

          // System.err.println(command + " is next part");

          if (subCom.hasSubCommand(command)) {
            subCom = subCom.getSubCommand(command);
            // System.err.println(subCom);
          } else {
            if (param == null) {
              param = parseExpression(command);
            } else {
              param += " " + command;
            }

          }

        } // end of while

        stopwatch.start();

        // execute the command
        subCom.action.execute(param);

        stopwatch.stop();

        if (measureTime) {
          out.println("Execution of the command " + command + " ("
              + commandLine + ") took " + stopwatch.elapsedSeconds()
              + " seconds");
        }

      }
    } // if at least one token

  }

  /**
   * Parse a given expression.
   * 
   * @param expression
   *          the expression
   * 
   * @return the string
   */
  protected String parseExpression(String expression) {

    PrefixParser parser = new PrefixParser(expression);

    if (Strings.startsWith(expression, parser.OPSYMBOLS)) {
      try {

        return parser.parseMathExpression().toString();
      } catch (RuntimeException e) {
        out.println("Error on evaluating the input!!!");
        throw new RuntimeException("Error on evaluating the input!!!", e);
      }
    }

    if (expression.charAt(0) == '&') {
      return variables.get(expression.substring(1));
    }

    return expression;
  }

  /**
   * Register command.
   * 
   * @param command
   *          the command
   * 
   * @return the command
   */
  protected final Command registerCommand(Command command) {
    commands.put(command.command, command);
    return command;
  }

  /**
   * Register command.
   * 
   * @param command
   *          the command
   * @param comment
   *          the comment
   * @param action
   *          the action
   * 
   * @return the command
   */
  protected final Command registerCommand(String command, String comment,
      CommandAction action) {
    Command c = new Command(command, comment, action);
    commands.put(command, c);
    return c;
  }

  /**
   * Set a new simulation object.
   * 
   * @param simulation
   *          the simulation
   */
  public void setSimulation(IComputationTask simulation) {
    // FIXME
    this.simulation = (SimulationRun) simulation;
  }

}
