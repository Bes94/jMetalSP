//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetalsp.algorithm.mocell;

import org.uma.jmetal.algorithm.multiobjective.mocell.MOCell;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;
import org.uma.jmetalsp.algorithm.DynamicAlgorithm;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.khaos.perception.core.Observable;

import java.util.List;

/**
 * Class implementing a dynamic version of MOCell. Most of the code of the original MOCell algorithm
 * is reused, and measures are used to allow external components to access the results of the
 * computation.
 *
 * @todo Explain the behaviour of the dynamic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicMOCell<S extends Solution<?>, O extends Observable>
    extends MOCell<S>
    implements DynamicAlgorithm<List<S>, O> {

  private int completedIterations ;
  protected SimpleMeasureManager measureManager ;
  protected BasicMeasure<List<S>> solutionListMeasure ;
  private boolean stopAtTheEndOfTheCurrentIteration = false ;
  private O observable ;

  public DynamicMOCell(DynamicProblem<S, ?> problem,
                       int maxEvaluations,
                       int populationSize,
                       BoundedArchive<S> archive,
                       Neighborhood<S> neighborhood,
                       CrossoverOperator<S> crossoverOperator,
                       MutationOperator<S> mutationOperator,
                       SelectionOperator<List<S>, S> selectionOperator,
                       SolutionListEvaluator<S> evaluator,
                       O observable) {
    super(problem, maxEvaluations, populationSize, archive, neighborhood, crossoverOperator, mutationOperator,
            selectionOperator, evaluator);

    completedIterations = 0 ;

    solutionListMeasure = new BasicMeasure<>() ;
    measureManager = new SimpleMeasureManager() ;
    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    this.observable = observable ;
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return (DynamicProblem<S, ?>) super.getProblem();
  }

  @Override
  public int getCompletedIterations() {
    return completedIterations ;
  }

  @Override protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {
      solutionListMeasure.push(getPopulation()) ;
      restart(100);
      completedIterations++;

    }
    return stopAtTheEndOfTheCurrentIteration;
  }

  @Override
  public void stopTheAlgorithm() {
    stopAtTheEndOfTheCurrentIteration = true ;
  }

  @Override
  public void restart(int percentageOfSolutionsToRemove) {
    SolutionListUtils.restart(getPopulation(), getDynamicProblem(), percentageOfSolutionsToRemove);
    location = new LocationAttribute<>(getPopulation());
    evaluator.evaluate(getPopulation(), getDynamicProblem()) ;
    initProgress();
    //setPopulation(createInitialPopulation());
    //neighborhood = new C9<S>((int)Math.sqrt(getPopulation().size()), (int)Math.sqrt(getPopulation().size())) ;
    //archive = new CrowdingDistanceArchive<>(getPopulation().size()) ;
  }

  @Override protected void updateProgress() {
    if (getDynamicProblem().hasTheProblemBeenModified()) {
      restart(100);
      getDynamicProblem().reset();
    }
    evaluations += getMaxPopulationSize() ;
    completedIterations ++ ;
    currentIndividual=(currentIndividual+1)%getMaxPopulationSize();
  }

  @Override
  public String getName() {
    return "DynamicMOCell";
  }

  @Override
  public String getDescription() {
    return "Dynamic version of algorithm MOCell";
  }

  @Override
  public O getObservable() {
    return this.observable ;
  }
}
