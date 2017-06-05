package org.uma.jmetalsp.algorithm.wasfga;

import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetalsp.DynamicAlgorithm;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData;
import org.uma.jmetalsp.observeddata.AlgorithmObservedData2;
import org.uma.jmetalsp.observer.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class DynamicWASFGA<S extends Solution<?>, O extends Observable<AlgorithmObservedData2>>
        extends WASFGA<S>
        implements DynamicAlgorithm<List<S>, Observable<AlgorithmObservedData2>> {
    private int completedIterations;
    private boolean stopAtTheEndOfTheCurrentIteration = false;

    O observable;
  private Map<String,List> algorithmData;

    public DynamicWASFGA(Problem<S> problem, int populationSize, int maxIterations, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator, SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator, List<Double> referencePoint, O observable) {
        super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator, referencePoint);
        completedIterations = 0;
        this.observable = observable;
        evaluations = 0;
        maxEvaluations = maxIterations;
    }

    @Override
    public DynamicProblem<S, ?> getDynamicProblem() {
        return (DynamicProblem<S, ?>) super.getProblem();
    }

    @Override
    public int getCompletedIterations() {
        return completedIterations;
    }

    @Override
    public void stopTheAlgorithm() {
        stopAtTheEndOfTheCurrentIteration = true;
    }

    @Override
    public void restart(int percentageOfSolutionsToRemove) {
        SolutionListUtils.restart(getPopulation(), getDynamicProblem(), percentageOfSolutionsToRemove);
        this.evaluatePopulation(this.getPopulation());
        this.initProgress();
        this.specificMOEAComputations();
    }

    @Override
    protected void initProgress() {
        evaluations = 0;
    }

    @Override
    public Observable<AlgorithmObservedData2> getObservable() {
        return this.observable;
    }

    @Override
    public String getName() {
        return "InDM2";
    }

    @Override
    public String getDescription() {
        return "Dynamic version of algorithm WASFGA";
    }

    @Override
    protected boolean isStoppingConditionReached() {
        if (evaluations >= maxEvaluations) {
            observable.setChanged();
          List<Integer> data= new ArrayList<>();
          data.add(completedIterations);
          algorithmData.put("numberOfIterations",data);
            observable.notifyObservers(new AlgorithmObservedData2(getPopulation(), algorithmData));
            restart(100);
            completedIterations++;
        }
        return stopAtTheEndOfTheCurrentIteration;
    }

    @Override
    protected void updateProgress() {
        if (getDynamicProblem().hasTheProblemBeenModified()) {
            restart(100);
            getDynamicProblem().reset();
        }
        evaluations++;
    }
}
