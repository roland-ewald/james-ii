/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.parallel;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.experiments.ComputationSetupException;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionController;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.AbstractTaskRunner;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.util.info.JavaInfo;
import org.jamesii.core.util.misc.Quadruple;

/**
 * This class takes computation task configurations and executes them in
 * parallel. This can be done locally (on machines with parallel computing
 * capabilities) or by using a (remote) master server.
 * 
 * Internally this class uses a TheadPool to execute the parallel computations.
 * How many parallel threads are used can be passed in the constructor. In
 * addition to these threads this class instantiates an additional thread which
 * works on the completed computation tasks ({@link JobDoneThread}). This thread
 * works on a blocking queue of finished computation tasks and thereby allows
 * the thread pool threads to continue immediately with the computation of the
 * next task.
 * 
 * @author Stefan Leye
 * @author Jan Himmelspach
 * 
 */
public class ParallelComputationTaskRunner extends AbstractTaskRunner {

  /**
   * Maps the task configurations to the list of execution jobs (for each
   * replication one job is added). The jobs are kept in this list until they
   * have been computed. Thus if this list is not empty there are still pending
   * jobs (in the {@link #threadPool}) or the {@link #jobDoneThread} has not
   * finished the work on the {@link #jobsDone} list.
   */
  private Map<TaskConfiguration, List<ComputationTaskExecutionJob>> jobs =
      new HashMap<>();

  /**
   * Maps the computation task configurations to their lists of run
   * informations. This list needs to be accessed in a thread-safe manner!
   */
  private Map<TaskConfiguration, List<RunInformation>> runInfos =
      new Hashtable<>();

  /**
   * Map of computation task configurations and the so far required number of
   * replications. If the list of {@link #runInfos} for at least on of the
   * TaskConfigurations contains less entries than the number of replications
   * stored for the task in this map we are not finished with the computation.
   */
  private Map<TaskConfiguration, Integer> replicationCounts = new Hashtable<>();

  /**
   * Maps ComputationTaskRuntimeInfos to the runnable jobs where they are
   * produced. Needs to be thread-safe!
   */
  private Map<ComputationTaskRuntimeInformation, ComputationTaskExecutionJob> waitingCTRTIs =
      new Hashtable<>();

  /**
   * The thread pool handler maintains the thread pool to be used for the
   * parallel computation task runner. Jobs are submitted to the threadPool and
   * will be executed automatically. After the execution the
   * {@link #runExecuted(ComputationTaskExecutionJob, ComputationTaskRuntimeInformation, TaskConfiguration, RunInformation)}
   * method will be called.
   */
  private transient ThreadPoolHandler threadPool;

  /**
   * The job done thread deals with all those jobs returned from the thread pool
   * (after their computation). The jobs this thread is working on are
   * maintained in the {@link #jobsDone} blocking queue. Due to this thread the
   * thread of the thread pool can immediately continue with the computation of
   * the next job.
   */
  private transient JobDoneThread jobDoneThread;

  /**
   * Buffer for finished jobs. A thread will wait on this data structure to
   * finish the processes.
   */
  private BlockingQueue<Quadruple<ComputationTaskExecutionJob, ComputationTaskRuntimeInformation, TaskConfiguration, RunInformation>> jobsDone =
      new LinkedBlockingQueue<>();

  /**
   * Instantiates a new parallel computation task runner. The max number of
   * concurrent will be automatically determined and set to the currently
   * available number of processors on the machine. Use the alternative
   * constructor {@link #ParallelComputationTaskRunner(int)} to specify the
   * number of concurrent threads on your own.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public ParallelComputationTaskRunner() throws RemoteException {
    this(-1);
  }

  /**
   * Instantiate a new parallel computation task runner. The number passed
   * determines the degree of parallelism. The higher the number the more
   * parallel computations can be started. However, the number of parallel
   * computations depend on the number of replications requested. If the
   * criteria used return less replications than we could execute in parallel
   * the potential parallelism is not exploited.<br/>
   * Depending on the computation task and the machine(s) the computation is
   * executed on the best number used for maxThreads might differ. For example,
   * it might be wise to set it to a number below the number of processors
   * available to leave resources for the user / the operating system. In other
   * scenarios it might be useful to set it to a number greater than the number
   * of processors - if the sequential amount of code to be executed after the
   * parallel computation a too small number of concurrent threads might waste
   * computing time.
   * 
   * @param maxThreads
   *          (maximal number of concurrent threads allowed). use -1 to
   *          automatically determine the number of concurrent threads (will
   *          then be set to the number of available processors); use -n to use
   *          "all but n-1" of the available processors
   * 
   * @throws RemoteException
   *           exception from the master server
   */
  public ParallelComputationTaskRunner(int maxThreads) throws RemoteException {

    int threadCount = maxThreads;
    if (maxThreads < 0) {
      // relative interpretation: use all but -maxThreads-1 cores
      JavaInfo info = new JavaInfo();
      threadCount = info.getCpus() + 1 + maxThreads;
    }
    // in rare cases (one cpu, maxThreads < -1) we might end up with too few
    // threads, let's use at least a single one then
    if (threadCount <= 0) {
      threadCount = 1;
    }

    threadPool = new ThreadPoolHandler(threadCount);
    jobDoneThread = new JobDoneThread(this);
  }

  @Override
  public synchronized void scheduleConfigurations(
      IExperimentExecutionController execController,
      List<TaskConfiguration> taskConfigurations) {
    for (TaskConfiguration taskConfig : taskConfigurations) {
      addExperimentController(taskConfig, execController);
      getTodoList().add(taskConfig);
    }
    notifyAll();
  }

  /**
   * This method has to be executed per replication to notify the execution
   * controller, that a replication of a computation task has been created.
   * 
   * @param job
   *          execution job for which the method is called
   * @param expController
   *          execution controller to be notified
   * @param ctrti
   *          ComputationTaskRuntimeInformation which has been created for the
   *          job
   */
  public void notifyExecutionController(ComputationTaskExecutionJob job,
      IExperimentExecutionController expController,
      ComputationTaskRuntimeInformation ctrti) {
    waitingCTRTIs.put(ctrti, job);
    expController.computationTaskInitialized(this, ctrti);
  }

  @Override
  public boolean cancelAllJobs(IExperimentExecutionController expController) {
    // set the pausing flag true
    setPausing(true);
    // iterate the configurations
    Iterator<TaskConfiguration> it = getTaskConfigurationIterator();
    while (it.hasNext()) {
      TaskConfiguration config = it.next();
      // cancel each configuration
      if (getExperimentController(config) == expController) {
        cancelConfiguration(config);
      }
    }
    return true;
  }

  @Override
  public void cancelConfiguration(TaskConfiguration taskConfiguration) {

    List<ComputationTaskExecutionJob> jobList = jobs.get(taskConfiguration);
    // if there are threads running which execute the configuration
    if (jobList != null) {
      // iterate the threads
      for (int i = 0; i < jobList.size(); i++) {
        // stop the simulation executed by the thread
        jobList.get(i).cancel();
      }
      // else remove the execution controller (if there are threads
      // running
      // we do this later)
    } else {
      removeExperimentController(taskConfiguration);
    }
    // remove the simulation configuration from the todo-list (check whether
    // the
    // configuration
    // is stored there, before)
    int i = getTodoList().indexOf(taskConfiguration);
    if (i >= 0) {
      getTodoList().remove(i);
    }
  }

  @Override
  public void cancelTask(
      ComputationTaskRuntimeInformation taskRuntimeInformation) {
    processNotification(taskRuntimeInformation, false);
  }

  /**
   * Executes a computation task configuration. Before execution it waits for
   * notification form the {@link IExperimentExecutionController}.
   * 
   * @param taskConfig
   *          the task configuration to be executed
   */
  @Override
  protected void executeConfiguration(TaskConfiguration taskConfig) {
    if (isStopping()) {
      return;
    }

    List<RunInformation> runInfo = null;
    try {
      runInfo = setupTaskConfig(taskConfig);
      if (!scheduleNewJobs(taskConfig, runInfo)) {
        throw new ComputationSetupException(
            "For the task "
                + taskConfig
                + " the runner was not able to setup a single execution as the replication number estimated was 0.");
      }
    } catch (Exception t) {
      SimSystem.report(t);
      getExperimentController(taskConfig).computationTaskInitialized(
          this,
          new ComputationTaskRuntimeInformation(null, taskConfig, null, null,
              new RunInformation(true)));
    }
  }

  /**
   * Initialises data structures for running the given computation task
   * configuration.
   * 
   * @param taskConfig
   *          the computation task configuration to be executed
   * @return reference to the corresponding list of runtime informations (to be
   *         filled later)
   */
  protected List<RunInformation> setupTaskConfig(TaskConfiguration taskConfig) {

    // Create new model reader if necessary
    initModelReader(taskConfig);

    // print out configuration
    reportConfigExecution();

    // taskConfig.

    // get the list of run infos, associated to the actual configuration
    List<RunInformation> runInfo = getRunInfos().get(taskConfig);
    if (runInfo == null) {
      runInfo = new ArrayList<>();
      getRunInfos().put(taskConfig, runInfo);
    }
    return runInfo;
  }

  /**
   * Schedule a new job. The job is specified by the passed task configuration.
   * 
   * This method is not synchronized and can only be called concurrently if the
   * calling context makes sure that no more than two callers will call this
   * method at the same time.
   * 
   * @param taskConfig
   *          the job shall be created for
   * @return the new job to be executed by a thread (pool); null if the
   *         computation task runner is about to terminate.
   */
  protected ComputationTaskExecutionJob scheduleJob(TaskConfiguration taskConfig) {
    // if the runner has been stopped, skip this
    if (isStopping()) {
      return null;
    }
    ComputationTaskExecutionJob job = createTaskExecJob(taskConfig);
    job.setAbsModReaderParams(getAbsModReaderParams());
    job.setModelReader(getModelReader());
    List<ComputationTaskExecutionJob> jobList = jobs.get(taskConfig);
    // create a job list if no list existed before
    if (jobList == null) {
      jobList = new ArrayList<>();
      jobs.put(taskConfig, jobList);
    }
    jobList.add(job);
    return job;
  }

  /**
   * Initialises a model reader for given computation task configuration. This
   * is necessary if there is no master server (model readers are created on the
   * servers) or if a model reader has been created with the right configuration
   * already.
   * 
   * @param taskConfig
   *          task configuration for which the model shall be read
   */
  protected void initModelReader(TaskConfiguration taskConfig) {
    if (!taskConfig.useMasterServer()
        && (getAbsModReaderParams() == null || !getAbsModReaderParams().equals(
            taskConfig.getModelReaderParams()))) {
      ModelReaderFactory modelReaderWriterFactory =
          SimSystem.getRegistry().getFactory(AbstractModelReaderFactory.class,
              taskConfig.getModelReaderParams());
      setModelReader(modelReaderWriterFactory.create(taskConfig
          .getCustomRWParams(), SimSystem.getRegistry().createContext()));
      setAbsModReaderParams(taskConfig.getModelReaderParams());
    }
  }

  /**
   * Creates a computation task execution job for the given computation task
   * configuration and runtime information.
   * 
   * @param taskConfig
   *          the computation task configuration
   * @param runInfo
   *          the runtime information list into which the runtime results have
   *          to be stored
   * @return newly created thread
   */
  protected ComputationTaskExecutionJob createTaskExecJob(
      TaskConfiguration taskConfig) {
    return new ComputationTaskExecutionJob(this, taskConfig,
        getExperimentController(taskConfig));
  }

  @Override
  protected void processNotification(
      ComputationTaskRuntimeInformation taskInfo, boolean run) {
    ComputationTaskExecutionJob job = waitingCTRTIs.remove(taskInfo);
    if (taskInfo == null) {
      SimSystem.report(Level.SEVERE, "Computation task not initialized");
    }
    if (job == null) {
      SimSystem.report(Level.SEVERE, "ComputationTaskExecution job not found");
    }
    if (job == null) {
      getExperimentController(taskInfo.getComputationTaskConfiguration())
          .computationTaskExecuted(this, taskInfo, new RunInformation(true));
    } else {
      job.setRunComputationTask(run);
    }
  }

  /**
   * This method is called after the computation of a job has been finished.
   * 
   * The information will be pushed to the {@link #jobsDone} queue and later on
   * processed by the {@link #jobDoneThread}. Thus this method will return as
   * soon as possible.
   * 
   * @param runnable
   *          execution thread which called the method
   * @param compTaskRTI
   *          ComputationTaskRuntimeInformation which has been created by the
   *          thread
   * @param taskConfig
   *          ComputationTaskConfiguration which has been handled
   * @param results
   *          the results of the run
   */
  public void runExecuted(ComputationTaskExecutionJob job,
      ComputationTaskRuntimeInformation compTaskRTI,
      TaskConfiguration taskConfig, RunInformation results) {

    jobsDone.add(new Quadruple<>(job, compTaskRTI, taskConfig, results));

  }

  /**
   * Schedule further replications for execution. The jobs are replications of
   * the taskConfig passed.
   * 
   * @param taskConfig
   *          the computation task configuration to be replicated
   * @param runInfo
   *          the run information of past executions
   * @return true, if new jobs have been / should have been scheduled, false
   *         otherwise (may return true even if no new replications have been
   *         scheduled, but then stopped will be true as well)
   */
  private boolean scheduleNewJobs(TaskConfiguration taskConfig,
      List<RunInformation> runInfo) {

    // get required replication count
    int repCount = getRequiredReplications(taskConfig, runInfo);

    // stop as soon as possible
    if (isStopping()) {
      return repCount != 0;
    }

    // create a job per replication to be computed
    List<ComputationTaskExecutionJob> scheduledJobs = new ArrayList<>();

    for (int i = 0; i < repCount; i++) {
      scheduledJobs.add(scheduleJob(taskConfig));
    }

    // schedule the jobs for computation
    for (ComputationTaskExecutionJob t : scheduledJobs) {

      // stop as soon as possible
      if (isStopping()) {
        return repCount != 0;
      }

      threadPool.submitJob(taskConfig, t);
    }

    // in case that we did not schedule new jobs we will return false
    return repCount != 0;
  }

  /**
   * Removes the task configuration from the maintained maps.
   * 
   * @param taskConfig
   *          the computation task configuration which shall be removed
   */
  private void removeTaskConfig(TaskConfiguration taskConfig) {
    removeExperimentController(taskConfig);
    jobs.remove(taskConfig);

    // inform the data storage (if used) that the computation of the
    // configuration has been
    // completed

    if (taskConfig.hasDataStorage()) {

      // it is important to use the data storage created previously; a new one
      // might allocate extra resources which are not freed if the factory is
      // used here once more
      IDataStorage<?> storage = getDataStorage(taskConfig);
      if (storage != null) {
        storage.computationTaskDone(taskConfig.getUniqueID());
      }
    }

    getRunInfos().remove(taskConfig);
    replicationCounts.remove(taskConfig);
    threadPool.cleanUpConfig(taskConfig);
  }

  /**
   * Returns the count of currently required replications, which is the amount
   * of already initiated replications and the amount of replications estimated
   * by the replication criteria.
   * 
   * @param config
   *          the task configuration
   * @param runInfo
   *          the information but the executed runs
   * @return the replications
   */
  private int getRequiredReplications(TaskConfiguration config,
      List<RunInformation> runInfo) {

    // get the amount of executed replications
    int executedReps = runInfo.size();

    // get the amount of planned replications for the passed task
    // configuration
    // (which may or may not be executed)
    Integer plannedReplications = replicationCounts.get(config);

    // if no replications had been scheduled before the plannedReplications
    // will
    // be null, in this case we set it to 0
    if (plannedReplications == null) {
      plannedReplications = 0;
    }

    // assume that we don't have to do more replications
    int repCount = 0;

    // if we are done with the planned replications we check whether we need
    // more; this method is executed per finished replication
    if (plannedReplications == executedReps) {

      // retrieve amount of additional required replications
      // from the replication criteria
      repCount = config.allowedReplications(runInfo);
      // if new replications are required
      if (repCount > 0) {
        // update the count of initiated replications
        replicationCounts.put(config, repCount + plannedReplications);
      }
    }
    return repCount;
  }

  @Override
  public void stop() {
    // if stopping, we ignore the rest (as the stopping procedure is already
    // under way)
    if (isStopping()) {
      return;
    }

    super.stop();

    // set stop to true to cancel the job done thread ASAP
    jobDoneThread.stop = true;
    // shut down the thread pool computing the jobs
    threadPool.shutDown();

    // report state if finished or unfinished jobs remain
    if (!jobsDone.isEmpty() || !jobs.isEmpty()) {
      SimSystem.report(Level.INFO,
          "The computation has been cancelled before " + jobsDone.size()
              + " computed entries have been finally processed and before "
              + jobs.size() + " configurations have been completed.");
    }

    // add poison pill for job done thread, just in case that the queue is
    // empty, no special job which can be recognized in the thread is required
    // as we have set stop to true before! The stop flag will be checked in the
    // thread right after the wait on new entries.
    jobsDone
        .add(new Quadruple<ComputationTaskExecutionJob, ComputationTaskRuntimeInformation, TaskConfiguration, RunInformation>(
            null, null, null, null));

    // wait until the thread has recognized that it shall stop
    try {
      jobDoneThread.stopped.acquire();
    } catch (InterruptedException e) {
      // ignore the exception here
    }

    // clean up
    for (Map.Entry<TaskConfiguration, List<ComputationTaskExecutionJob>> entry : jobs
        .entrySet()) {
      for (ComputationTaskExecutionJob job : entry.getValue()) {
        job.cancel();
      }
    }
  }

  @Override
  public void recoverTask(long taskUID,
      ComputationTaskRuntimeInformation runtimeInfo) {
    SimSystem.report(Level.SEVERE, "recoverTask(): Not implemented.");
  }

  @Override
  public void restartTask(long taskUID) {
    SimSystem.report(Level.SEVERE, "restartTask(): Not implemented.");
  }

  /**
   * @return the runInfos
   */
  protected final Map<TaskConfiguration, List<RunInformation>> getRunInfos() {
    return runInfos;
  }

  /**
   * Simple thread which works on the queue of finished tasks. This thread has
   * been introduced to decouple computation task computation and maintenance
   * affairs of the parallel computation task runner. Basically the run method
   * can run concurrently to all computations to be made, only the last
   * execution per replication count per configuration will be executed after
   * the last replication has been computed. In this case the
   * {@link ParallelComputationTaskRunner#scheduleNewJobs(TaskConfiguration, List)}
   * might schedule new jobs. This might take longer and thus means that the
   * computation of further replications from other configurations can continue
   * without any delay. If there is only one computation and if the replications
   * have approximately identical computation demands it might happen that this
   * thread is executed after all pool threads - however, this is not worse than
   * the pool threads waiting for a synchronized pass through this method.
   * 
   * @author Jan Himmelspach
   * 
   */
  private class JobDoneThread extends Thread {

    /**
     * The task runner the job done thread belongs to.
     */
    private ITaskRunner owner = null;

    /**
     * Flag to stop the run method ASAP.
     */
    private volatile boolean stop = false;

    /**
     * stopped or not.
     */
    private Semaphore stopped = new Semaphore(0);

    public JobDoneThread(ITaskRunner owner) {
      super("pctr-job-done");
      this.owner = owner;
      start();
    }

    private void stopped() {
      stopped.release();
    }

    @Override
    public void run() {

      // there is no need to synchronize this method as it operates on a
      // blocking queue only

      while (!isInterrupted() && !stop) {
        Quadruple<ComputationTaskExecutionJob, ComputationTaskRuntimeInformation, TaskConfiguration, RunInformation> quad;

        // fetch the information about the job finished from the queue,
        // will
        // wait (block) until such an information is available

        try {
          quad = jobsDone.take();
        } catch (InterruptedException e) {
          stopped();
          return;
        }
        if (stop) {
          stopped();
          return;
        }

        // System.out.println("Finishing a job!");

        ComputationTaskExecutionJob job = quad.getE1();
        ComputationTaskRuntimeInformation compTaskRTI = quad.getE2();
        TaskConfiguration taskConfig = quad.getE3();
        RunInformation results = quad.getE4();

        // if we have a data storage attached we will inform the storage that no
        // more data for this computation task will be written

        if (taskConfig.hasDataStorage()) {
          taskConfig.createPlainDataStorage().computationTaskDone(
              results.getComputationTaskID().getId());
        }

        // get the run information for the task configuration
        List<RunInformation> runInfo = getRunInfos().get(taskConfig);

        // add the results to this run information
        runInfo.add(results);

        // schedule further jobs (if required; will be determined by the
        // method
        // called)
        scheduleNewJobs(taskConfig, runInfo);

        // check once more for stop as scheduleNewJobs might need some time
        // (e.g., due to replication criteria ...)
        if (stop) {
          stopped();
          return;
        }

        // flag to determine whether the TaskConfiguration is finished
        // (no
        // replications are left)
        boolean jobDone = true;

        if ((jobs.get(taskConfig).size() > 1)) {
          jobDone = false;
        } else {
          results.setJobDone(true);
        }

        // get the execution controller of the
        // ComputationTaskConfiguration
        IExperimentExecutionController execControl =
            getExperimentController(taskConfig);

        // notify it about the finished replication of the computation
        // task
        execControl.computationTaskExecuted(owner, compTaskRTI, results);

        // remove the job from the jobs list
        jobs.get(taskConfig).remove(job);

        // if the complete computation task is finished (all required
        // replications are computed) we can remove the task
        // configuration from
        // the list of pending configurations
        if (jobDone) {
          removeTaskConfig(taskConfig);
        }

      }
      stopped();
    }
  }

}
