package org.uma.jmetalsp.algorithm.smpso;

import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;
import org.uma.jmetalsp.algorithm.DynamicAlgorithmBuilder;
import org.uma.jmetalsp.algorithm.mocell.DynamicMOCell;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.khaos.perception.core.Observable;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class DynamicSMPSOBuilder<
				P extends DynamicProblem<DoubleSolution, ?>,
				O extends Observable> implements DynamicAlgorithmBuilder<DynamicSMPSO<O>, P> {

	private double c1Max;
	private double c1Min;
	private double c2Max;
	private double c2Min;
	private double r1Max;
	private double r1Min;
	private double r2Max;
	private double r2Min;
	private double weightMax;
	private double weightMin;
	private double changeVelocity1;
	private double changeVelocity2;

	private int swarmSize;
	private int maxIterations;

	protected MutationOperator<DoubleSolution> mutationOperator;
  private O observable ;
	private BoundedArchive<DoubleSolution> leaders;

	private SolutionListEvaluator<DoubleSolution> evaluator;

	public DynamicSMPSOBuilder(MutationOperator<DoubleSolution> mutationOperator,
														 BoundedArchive<DoubleSolution> leaders,
														 O observable) {
		this.leaders = leaders;

		swarmSize = 100;
		maxIterations = 250;

		r1Max = 1.0;
		r1Min = 0.0;
		r2Max = 1.0;
		r2Min = 0.0;
		c1Max = 2.5;
		c1Min = 1.5;
		c2Max = 2.5;
		c2Min = 1.5;
		weightMax = 0.1;
		weightMin = 0.1;
		changeVelocity1 = -1;
		changeVelocity2 = -1;

		this.mutationOperator = mutationOperator ;
		evaluator = new SequentialSolutionListEvaluator<DoubleSolution>() ;
		this.observable = observable ;
	}

	public DynamicSMPSOBuilder<P,O> setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setMutation(MutationOperator<DoubleSolution> mutation) {
		mutationOperator = mutation;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setC1Max(double c1Max) {
		this.c1Max = c1Max;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setC1Min(double c1Min) {
		this.c1Min = c1Min;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setC2Max(double c2Max) {
		this.c2Max = c2Max;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setC2Min(double c2Min) {
		this.c2Min = c2Min;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setR1Max(double r1Max) {
		this.r1Max = r1Max;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setR1Min(double r1Min) {
		this.r1Min = r1Min;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setR2Max(double r2Max) {
		this.r2Max = r2Max;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setR2Min(double r2Min) {
		this.r2Min = r2Min;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setWeightMax(double weightMax) {
		this.weightMax = weightMax;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setWeightMin(double weightMin) {
		this.weightMin = weightMin;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setChangeVelocity1(double changeVelocity1) {
		this.changeVelocity1 = changeVelocity1;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setChangeVelocity2(double changeVelocity2) {
		this.changeVelocity2 = changeVelocity2;

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setRandomGenerator(PseudoRandomGenerator randomGenerator) {
		JMetalRandom.getInstance().setRandomGenerator(randomGenerator);

		return this;
	}

	public DynamicSMPSOBuilder<P,O> setSolutionListEvaluator(SolutionListEvaluator<DoubleSolution> evaluator) {
		this.evaluator = evaluator ;

		return this ;
	}

	@Override
	public DynamicSMPSO build(P problem) {
		return new DynamicSMPSO(problem, swarmSize, leaders, mutationOperator, maxIterations, r1Min, r1Max,
						r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, changeVelocity1,
						changeVelocity2, evaluator, observable);
	}
}
