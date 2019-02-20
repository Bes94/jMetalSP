package org.uma.jmetalsp.problem.fda;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetalsp.DynamicProblem;
import org.uma.jmetalsp.observeddata.ObservedIntegerValue;
import org.uma.jmetalsp.observeddata.ObservedValue;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observer.impl.DefaultObservable;

import java.io.Serializable;

/**
 * Cristóbal Barba <cbarba@lcc.uma.es>
 */
public abstract class FDA
				extends AbstractDoubleProblem
				implements DynamicProblem<DoubleSolution, ObservedValue<Integer>>, Serializable {
	protected double time;
	protected boolean theProblemHasBeenModified;
	protected Observable<ObservedValue<Integer>> observable ;

  private int tauT=5;
  private int nT=10;

	public FDA (Observable<ObservedValue<Integer>> observable) {
		this.observable = observable ;
		this.time = 1.0;
		observable.register(this);
	}

	public FDA () {
		this(new DefaultObservable<>()) ;
	}

	@Override
	public void update(Observable<ObservedValue<Integer>> observable, ObservedValue<Integer> counter) {
        time = (1.0d/(double)nT) * Math.floor(counter.getValue()/(double)tauT) ;
		theProblemHasBeenModified = true ;
	}


}
