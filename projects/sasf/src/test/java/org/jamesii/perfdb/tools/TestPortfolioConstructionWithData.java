/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;
import org.jamesii.asf.portfolios.ga.ListIndividualFactory;
import org.jamesii.asf.portfolios.ga.abort.GenerationCountAbort;
import org.jamesii.asf.portfolios.ga.fitness.ASRFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.asf.portfolios.stochsearch.StochSearchPortfolioSelector;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.adaptiverunner.AdaptiveTaskRunnerFactory;
import org.jamesii.simspex.adaptiverunner.SimplePolicyObserver;
import org.jamesii.simspex.adaptiverunner.policies.UCB2Factory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;

/**
 * A simple 'script' to test portfolio construction.
 * 
 * TODO make unit tests out of this
 * 
 * @author Roland Ewald
 * 
 */
public class TestPortfolioConstructionWithData {

  static final double STOP_TIME = 60.0;

  static final int NUM_OF_PROBLEMS = 1;

  static final int PORT_GEN_REPS = 8;

  static final int POLICY_REPS = 5;

  static final int ACTUAL_REPLICATIONS = 20;

  static final int NUM_GENERATIONS = 20;

  static final int NUM_INDIVIDUALS = 50;

  private static final int MIN_PORTFOLIO_SIZE = 2;

  private static final int MAX_PORTFOLIO_SIZE = 7;

  private static final double ACCEPTABLE_RISK = 0.5;

  public static void main(String[] args) throws Exception {

    System.out.println("Hit enter to start...");
    System.in.read();

    PortfolioPerformanceData portPerfData =
        (PortfolioPerformanceData) Files.load(args[0]);
    System.err.println("Algorithms:" + portPerfData.performances.length);
    System.err.println(portPerfData.performances[0].length);
    System.out.println(Strings.displayMatrix(portPerfData.performances, '\t'));

    for (SelectionTree config : portPerfData.configurations) {
      System.out.println();
      System.out.println(Strings.dispIterable(config.getUniqueFactories()));
    }

    List<Double> execTimes = new ArrayList<>();

    for (int rep = 0; rep < PORT_GEN_REPS; rep++) {

      PortfolioProblemDescription pdd =
          new PortfolioProblemDescription(
              new PortfolioPerformanceData[] { portPerfData }, ACCEPTABLE_RISK,
              new boolean[] { false }, MIN_PORTFOLIO_SIZE, MAX_PORTFOLIO_SIZE);
      GeneticAlgorithmPortfolioSelector gaps =
          new GeneticAlgorithmPortfolioSelector();
      gaps.setAbortCriterion(new GenerationCountAbort(NUM_GENERATIONS));
      gaps.setFitness(new ASRFitness());
      gaps.setNumIndividuals(NUM_INDIVIDUALS);
      gaps.setMutationRate(0.001);
      gaps.setIndividualFactory(new ListIndividualFactory());

      double[] gaPortfolio = gaps.portfolio(pdd);
      double[] completePortfolio = new double[gaPortfolio.length];
      for (int i = 0; i < completePortfolio.length; i++) {
        completePortfolio[i] = 1.0 / completePortfolio.length;
      }

      StochSearchPortfolioSelector ssps =
          new StochSearchPortfolioSelector(NUM_GENERATIONS * NUM_INDIVIDUALS,
              SimSystem.getRNGGenerator().getNextRNG());
      System.err.println("1");
      double[] stochasticPortfolio = ssps.portfolio(pdd);
      System.err.println("2");
      for (int i = 0; i < NUM_OF_PROBLEMS; i++) {
        System.err.println("Problem #" + i);
        IProblemDefinition problem =
            portPerfData.problems[(int) (portPerfData.problems.length * SimSystem
                .getRNGGenerator().getNextRNG().nextDouble())];

        for (double[] currentPortfolio : new double[][] { gaPortfolio,
        /* completePortfolio, */stochasticPortfolio }) {
          for (int policyRep = 0; policyRep < POLICY_REPS; policyRep++) {
            System.err.println("Portfolio:"
                + Strings.dispArray(currentPortfolio));
            BaseExperiment be = new BaseExperiment();
            be.setDefaultSimStopTime(STOP_TIME);
            be.setModelLocation(problem.getProblemScheme().getUri());

            // @see CyclicChainSystem.REACT_PER_SPECIES
            be.getFixedModelParameters().put("ReactPerSpecies", 1);
            be.setRepeatRuns(ACTUAL_REPLICATIONS);

            List<ParameterBlock> portfolioBlocks =
                getParamBlocksForPortfolio(currentPortfolio, portPerfData);
            System.err.println("Size:" + portfolioBlocks.size());

            List<IObserver<? extends IMinBanditPolicy>> observers =
                new ArrayList<>();
            observers.add(new SimplePolicyObserver());

            ParameterBlock pb = new ParameterBlock();
            pb.addSubBl(AdaptiveTaskRunnerFactory.PORTFOLIO, portfolioBlocks)
                .addSubBl(AdaptiveTaskRunnerFactory.POLICY_OBSERVERS, observers)
                .addSubBl(AdaptiveTaskRunnerFactory.POLICY,
                    new ParameterBlock(UCB2Factory.class.getName()));
            pb.addSubBl(ParallelComputationTaskRunnerFactory.NUM_CORES, 1);
            be.setTaskRunnerFactory(new ParameterizedFactory<TaskRunnerFactory>(
                new AdaptiveTaskRunnerFactory(), pb));

            StopWatch sw = new StopWatch();
            sw.start();
            be.execute();
            sw.stop();
            System.err.println("Execution Time:" + sw.elapsedMilliseconds());
            execTimes.add(sw.elapsedMilliseconds() / 1000.0);
            System.err.println("Execution Times:"
                + Strings.dispIterable(execTimes));
          }
        }
      }
    }
  }

  static List<ParameterBlock> getParamBlocksForPortfolio(double[] portfolio,
      PortfolioPerformanceData portPerfData) {
    List<ParameterBlock> portfolioAlgos = new ArrayList<>();
    for (int i = 0; i < portfolio.length; i++) {
      if (portfolio[i] != 0) {
        portfolioAlgos.add(portPerfData.configurations[i].toParamBlock());
      }
    }
    return portfolioAlgos;
  }
}
