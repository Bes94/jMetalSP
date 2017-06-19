package org.uma.jmetalsp.examples.dynamictsp;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.PMXCrossover;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetalsp.*;
import org.uma.jmetalsp.algorithm.indm2.InDM2;
import org.uma.jmetalsp.algorithm.indm2.InDM2Builder;
import org.uma.jmetalsp.consumer.ChartInDM2Consumer;
import org.uma.jmetalsp.consumer.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.examples.dynamictsp.StreamingTSPSource;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingCounterDataSource;
import org.uma.jmetalsp.examples.streamingdatasource.SimpleStreamingDataSourceFromKeyboard;
import org.uma.jmetalsp.impl.DefaultRuntime;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
import org.uma.jmetalsp.observeddata.ListObservedData;
import org.uma.jmetalsp.observeddata.MatrixObservedData;
import org.uma.jmetalsp.observeddata.SingleObservedData;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;
import org.uma.jmetalsp.problem.fda.FDA2;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromFiles;
import org.uma.jmetalsp.problem.tsp.MultiobjectiveTSPBuilderFromNY;
import org.uma.jmetalsp.util.restartstrategy.RestartStrategy;
import org.uma.jmetalsp.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveFirstNSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNRandomSolutions;
import org.uma.jmetalsp.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheHypervolumeContribution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Example of SparkSP application.
 * Features:
 * - Algorithm: InDM2
 * - Problem: Bi-objective TSP (using data files from TSPLIB)
 * - Default streaming runtime (Spark is not used)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class InDM2RunnerForTSP {

  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            MatrixObservedData<Double>,
            AlgorithmObservedData2,
            DynamicProblem<DoubleSolution, MatrixObservedData<Double>>,
            DynamicAlgorithm<List<PermutationSolution<Integer>>, Observable<AlgorithmObservedData2>>,
            StreamingTSPSource,
            AlgorithmDataConsumer<AlgorithmObservedData2, DynamicAlgorithm<List<PermutationSolution<Integer>>, Observable<AlgorithmObservedData2>>>> application;
    application = new JMetalSPApplication<>();

    // Set the streaming data source for the problem
    Observable<MatrixObservedData<Double>> streamingTSPDataObservable =
            new DefaultObservable<>("streamingTSPObservable");
    StreamingDataSource<?, ?> streamingDataSource = new StreamingTSPSource(streamingTSPDataObservable, 20000);

    // Set the streaming data source for the algorithm
    Observable<ListObservedData<Double>> algorithmObservable = new DefaultObservable<>("Algorithm observable");
    StreamingDataSource<ListObservedData<Double>, Observable<ListObservedData<Double>>> streamingDataSource2 =
            new SimpleStreamingDataSourceFromKeyboard(algorithmObservable);

    // Problem configuration
    DynamicProblem<PermutationSolution<Integer>, MatrixObservedData<Double>> problem;
    problem = new MultiobjectiveTSPBuilderFromFiles("data/kroA100.tsp", "data/kroB100.tsp")
            .build(streamingTSPDataObservable);
    //problem = new MultiobjectiveTSPBuilderFromNY("initialDataFile.txt")
    //        .build(streamingTSPDataObservable);
    //System.out.println(problem);

    // Algorithm configuration
    CrossoverOperator<PermutationSolution<Integer>> crossover;
    MutationOperator<PermutationSolution<Integer>> mutation;
    SelectionOperator<List<PermutationSolution<Integer>>, PermutationSolution<Integer>> selection;

    crossover = new PMXCrossover(0.9);

    double mutationProbability = 0.2;
    mutation = new PermutationSwapMutation<Integer>(mutationProbability);

    selection = new BinaryTournamentSelection<>(
            new RankingAndCrowdingDistanceComparator<PermutationSolution<Integer>>());

    InDM2<PermutationSolution<Integer>> algorithm;
    Observable<AlgorithmObservedData2<PermutationSolution<Integer>>> observable = new DefaultObservable<>("InDM2");

    List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(180000.0);
    referencePoint.add(60000.0);

    int populationSize = 50;
    algorithm = new InDM2Builder<
            PermutationSolution<Integer>,
            DynamicProblem<PermutationSolution<Integer>, ?>,
            Observable<AlgorithmObservedData2<PermutationSolution<Integer>>>>(crossover, mutation, referencePoint, observable)
            .setMaxIterations(400000)
            .setPopulationSize(populationSize)
            .build(problem);

    algorithm.setRestartStrategyForProblemChange(new RestartStrategy<PermutationSolution<Integer>>(
            new RemoveNRandomSolutions<>(0),
            //new RemoveNSolutionsAccordingToTheHypervolumeContribution<PermutationSolution<Integer>>(50),
            new CreateNRandomSolutions<PermutationSolution<Integer>>(0)));

    algorithm.setRestartStrategyForReferencePointChange(new RestartStrategy<PermutationSolution<Integer>>(
            new RemoveNRandomSolutions<>(80),
            new CreateNRandomSolutions<>(20)));

    algorithmObservable.register(algorithm);

    application.setStreamingRuntime(new DefaultRuntime<SingleObservedData<Integer>,
            Observable<SingleObservedData<Integer>>,
            SimpleStreamingCounterDataSource>())
            .setProblem(problem)
            .setAlgorithm(algorithm)
            .addStreamingDataSource(streamingDataSource)
            .addStreamingDataSource(streamingDataSource2)
            .addAlgorithmDataConsumer(new ChartInDM2Consumer(algorithm, referencePoint))
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("outputDirectory", algorithm))
            .run();
  }
}
