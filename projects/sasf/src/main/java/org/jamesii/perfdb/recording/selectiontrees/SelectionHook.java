/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.AbstractComputationTaskStopPolicyFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.util.Hook;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.perfdb.plugintype.AbstractPerfDBFactory;

/**
 * Extracts 'ordered' tree of selected factories from execution order and
 * additional meta-information provided by the registry.
 * 
 * @see SelectionInformation
 * @see org.jamesii.core.Registry
 * 
 * @author Roland Ewald
 * 
 */
public class SelectionHook extends Hook<SelectionInformation<?>> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3675812863935535299L;

  /**
   * Since the stack trace analysis is called within this hook, we don't have to
   * care about the first few trace elements: we know for sure that these cannot
   * be factories.
   */
  private static final int START_OFFEST = 5;

  /**
   * The map from threads to the tasks that they currently execute (identified
   * by IDs).
   */
  private final Map<Long, IUniqueID> threadToTaskMap =
      new HashMap<>();

  /**
   * The trees storing the selection information.
   */
  private final Map<IUniqueID, SelectionTree> selectionTrees =
      new HashMap<>();

  /**
   * The current siblings. These are factories that have been invoked from the
   * same factory. Key is the ID of the overall task.
   */
  private final Map<IUniqueID, List<SelectedFactoryNode>> currentSiblings =
      new HashMap<>();

  /**
   * List of parents, without the tree root. The tree root is no factory, as
   * several factories are invoked sequentially during the launch of a
   * simulation.
   */
  private final Map<IUniqueID, List<SelectedFactoryNode>> parents =
      new HashMap<>();

  /**
   * Ignore list that holds all abstract factories that are selected but are
   * irrelevant at the same time (e.g., the experiment reader factory).
   */
  private final List<Class<? extends AbstractFactory<?>>> ignoreList =
      new ArrayList<>();
  {
    ignoreList.add(AbstractExperimentReaderFactory.class);
    ignoreList.add(AbstractModelReaderFactory.class);
    ignoreList.add(AbstractPerfDBFactory.class);
    ignoreList.add(AbstractComputationTaskStopPolicyFactory.class);
  }

  /**
   * Flag that determines if hook is active.
   */
  private boolean recordSelections = false;

  /**
   * Default constructor.
   * 
   * @param oldHook
   *          old hook (will be executed before)
   */
  public SelectionHook(Hook<SelectionInformation<?>> oldHook) {
    super(oldHook);
  }

  /**
   * Adds parent to parent list, updates siblings list.
   * 
   * @param taskID
   *          ID of the current task
   * @param node
   *          node to be added
   */
  private void addParent(IUniqueID taskID, SelectedFactoryNode node) {
    List<SelectedFactoryNode> parentNodes = parents.get(taskID);
    parentNodes.add(node);
    currentSiblings.put(taskID, selectionTrees.get(taskID).getChildren(node));
  }

  /**
   * Execute hook.
   * 
   * @param information
   *          the selection information
   */
  @Override
  protected synchronized void executeHook(SelectionInformation<?> information) {

    // Only work when activated
    if (!recordSelections) {
      return;
    }

    long currentThreadId = Thread.currentThread().getId();

    updateThreadToTaskMap(information, currentThreadId);

    IUniqueID taskID = threadToTaskMap.get(currentThreadId);

    SelectionTree selectionTree = selectionTrees.get(taskID);
    if (selectionTree == null) {
      selectionTree = new SelectionTree(null);
      selectionTrees.put(taskID, selectionTree);
    }

    // Analyze stack to determine position in selection tree
    SelectedFactoryNode currParent = getParent(taskID);

    if (ignoreList.contains(information.getAbstractFactory())) {
      return;
    }

    // Add child
    SelectedFactoryNode newChild =
        new SelectedFactoryNode(information,
            currentSiblings.get(taskID).size() + 1, currParent);
    selectionTree.addVertex(newChild);
    if (!selectionTree.addEdge(new Edge<>(newChild,
        currParent))) {
      throw new IllegalArgumentException(
          "Selection tree creation failed: constructed tree is ill-defined.");
    }

    // Set current child as newest parent (in case it invokes additional
    // factories by itself)
    addParent(taskID, newChild);
  }

  /**
   * Updates thread => task id map.
   * 
   * @param selectionInformation
   *          the selection information
   * @param threadId
   *          the id of the current thread
   */
  private void updateThreadToTaskMap(
      SelectionInformation<?> selectionInformation, long threadId) {

    Object taskObj =
        ParameterBlocks.getSubBlockValue(selectionInformation.getParameter(),
            ComputationTaskStopPolicyFactory.COMPTASK);

    if (taskObj instanceof IComputationTask) {
      IComputationTask task = (IComputationTask) taskObj;
      IUniqueID newId = task.getUniqueIdentifier();
      moveTemporaryData(newId, threadId);
      threadToTaskMap.put(threadId, newId);
    }

    // In case there's no valid task ID yet
    if (!threadToTaskMap.containsKey(threadId)) {
      threadToTaskMap.put(threadId, new TemporaryUID());
    }

  }

  /**
   * Move temporary data to its actual place in the data structures, once it is
   * clear what the actual ID of the task is.
   * 
   * @param newId
   *          the new id
   * @param threadId
   *          the thread id (to get the temporary ID)
   */
  public void moveTemporaryData(IUniqueID newId, long threadId) {
    IUniqueID previousId = threadToTaskMap.get(threadId);

    if (!(previousId instanceof TemporaryUID)) {
      return;
    }

    selectionTrees.put(newId, selectionTrees.remove(previousId));
    currentSiblings.put(newId, currentSiblings.remove(previousId));
    parents.put(newId, parents.remove(previousId));
  }

  /**
   * Determines the correct parent node regarding the current function stack.
   * 
   * @param taskID
   *          ID of the current task
   * @return the parent node to be used
   */
  protected SelectedFactoryNode getParent(IUniqueID taskID) {

    // Search for correct invocation level
    int invocationLevel = getInvocationLevel(taskID);

    // Remove parents from list that have not invoked new selections
    List<SelectedFactoryNode> parentNodes =
        new ArrayList<>(parents.get(taskID).subList(0,
            invocationLevel));
    parents.put(taskID, parentNodes);

    SelectionTree selectionTree = selectionTrees.get(taskID);

    SelectedFactoryNode currParent =
        invocationLevel == 0 ? selectionTree.getRoot() : parentNodes
            .get(invocationLevel - 1);

    currentSiblings.put(taskID, selectionTree.getChildren(currParent));
    return currParent;
  }

  /**
   * Analyze stack trace to determine invocation level.
   * 
   * @param taskID
   *          ID of the current thread
   * @return invocation level
   */
  protected int getInvocationLevel(IUniqueID taskID) {

    int invocationLevel = 0;

    List<SelectedFactoryNode> parentNodes = parents.get(taskID);
    if (parentNodes == null) {
      parentNodes = new ArrayList<>();
      parents.put(taskID, parentNodes);
    }

    if (parentNodes.size() == 0) {
      return 0;
    }

    Class<? extends Factory<?>> currentFactory =
        parentNodes.get(invocationLevel).getSelectionInformation()
            .getFactoryClass();

    // We start from START_OFFSET, as we know the first part of the stack
    // trace...
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (int i = START_OFFEST; i < stackTrace.length; i++) {
      if (currentFactory.getCanonicalName()
          .equals(stackTrace[i].getClassName())) {
        invocationLevel++;
        if (parentNodes.size() == invocationLevel) {
          break;
        }
        currentFactory =
            parentNodes.get(invocationLevel).getSelectionInformation()
                .getFactoryClass();
      }
    }

    return invocationLevel;
  }

  /**
   * Gets the selection tree.
   * 
   * @param id
   *          the id
   * @return the selection tree
   */
  public SelectionTree getSelectionTree(IUniqueID id) {
    return selectionTrees.get(id);
  }

  /**
   * Resets hook (all selection information will be cleared).
   */
  public void reset() {
    selectionTrees.clear();
    currentSiblings.clear();
    parents.clear();
  }

  /**
   * Clears all information associated with a certain task.
   * 
   * @param id
   *          the id of the task
   */
  public void clearTask(IUniqueID id) {
    selectionTrees.remove(id);
    currentSiblings.remove(id);
    parents.remove(id);
  }

  /**
   * Adds a factory to the ignore list.
   * 
   * @param factory
   *          factory to be ignored in the future
   */
  public void addToIgnoreList(Class<? extends AbstractFactory<?>> factory) {
    ignoreList.add(factory);
  }

  /**
   * Gets the list of ignored factories.
   * 
   * @return the ignore list
   */
  public List<Class<? extends AbstractFactory<?>>> getIgnoreList() {
    return ignoreList;
  }

  /**
   * Start recording the selections.
   */
  public void start() {
    recordSelections = true;
  }

  /**
   * Stop recording the selections.
   */
  public void stop() {
    recordSelections = false;
  }

  /**
   * Checks if hook is recording selections.
   * 
   * @return true, if it is recording selections
   */
  public boolean isRecordingSelections() {
    return recordSelections;
  }

}

/**
 * Represents temporary IDs.
 */
class TemporaryUID implements IUniqueID {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8447966953019884548L;

  @Override
  public int compareTo(IUniqueID o) {
    return Double.compare(this.hashCode(), o.hashCode());
  }

  @Override
  public String asString() {
    return "Temporary ID";
  }

}