package org.uma.jmetalsp.algorithm.indm2;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observer.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class InDM2Builder<
        S extends Solution<?>,
        P extends DynamicProblem<S, ?>> {

  private CrossoverOperator<S> crossover;
  private MutationOperator<S> mutation;
  private SelectionOperator<List<S>, S> selection;
  private SolutionListEvaluator<S> evaluator;
  private List<Double> referencePoint = null;
  private double crossoverProbability;
  private double crossoverDistributionIndex;
  private double mutationProbability;
  private Observable<AlgorithmObservedData<S>> observable ;
  private double mutationDistributionIndex;
  private int maxIterations;
  private int populationSize;
  private String weightVectorsFileName;
  public InDM2Builder(CrossoverOperator<S> crossoverOperator,
                      MutationOperator<S> mutationOperator,
                      List<Double> referencePoint,
                      Observable<AlgorithmObservedData<S>> observable) {
    this.crossover = crossoverOperator;
    this.mutation = mutationOperator;
    this.selection = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>());
    this.evaluator = new SequentialSolutionListEvaluator<S>();
    this.crossoverProbability = 0.9;
    this.crossoverDistributionIndex = 20.0;
    this.mutationDistributionIndex = 20.0;
    this.maxIterations = 25000;
    this.populationSize = 100;
    this.observable = observable;
    this.referencePoint = new ArrayList<>();
    this.referencePoint = referencePoint ;
    this.weightVectorsFileName = ""
;  }
  public InDM2Builder(CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      List<Double> referencePoint,
      Observable<AlgorithmObservedData<S>> observable,String weightVectorsFileName) {
    this(crossoverOperator,mutationOperator,referencePoint,observable);
    this.weightVectorsFileName=weightVectorsFileName;
  }

  public InDM2Builder<S, P> setCrossover(CrossoverOperator<S> crossover) {
    this.crossover = crossover;
    return this;
  }

  public InDM2Builder<S, P> setMutation(MutationOperator<S> mutation) {
    this.mutation = mutation;
    return this;
  }

  public InDM2Builder<S, P> setSelection(SelectionOperator<List<S>, S> selection) {
    this.selection = selection;
    return this;
  }

  public InDM2Builder<S, P> setEvaluator(SolutionListEvaluator<S> evaluator) {
    this.evaluator = evaluator;
    return this;
  }

  public InDM2Builder<S, P> setCrossoverProbability(double crossoverProbability) {
    this.crossoverProbability = crossoverProbability;
    return this;
  }

  public InDM2Builder<S, P> setCrossoverDistributionIndex(double crossoverDistributionIndex) {
    this.crossoverDistributionIndex = crossoverDistributionIndex;
    return this;
  }

  public InDM2Builder<S, P> setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
    return this;
  }

  public InDM2Builder<S, P> setMutationDistributionIndex(double mutationDistributionIndex) {
    this.mutationDistributionIndex = mutationDistributionIndex;
    return this;
  }

  public InDM2Builder<S, P> setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
    return this;
  }

  public InDM2Builder<S, P> setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
    return this;
  }

  public InDM2Builder<S, P> setReferencePoint(List<Double> referencePoint) {
    this.referencePoint = referencePoint;
    return this;
  }

  public InDM2<S> build(P problem) {
    mutationProbability = 1.0 / problem.getNumberOfVariables();
    return new InDM2(problem, populationSize, maxIterations, crossover, mutation, selection, evaluator,
            referencePoint, observable,weightVectorsFileName);

  }
}
