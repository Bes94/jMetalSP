package org.uma.jmetalsp.application.fda.problem.fda2;

import org.uma.jmetalsp.application.fda.problem.fda1.FDA1;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.ProblemBuilder;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class FDA2ProblemBuilder implements ProblemBuilder,Serializable {
  private  int numberOfVariables;
  private int numberOfObjectives;
  public FDA2ProblemBuilder(int numberOfVariables, int numberOfObjectives){
    this.numberOfVariables=numberOfVariables;
    this.numberOfObjectives=numberOfObjectives;
  }
  @Override
  public DynamicProblem<?, ?> build() throws IOException {
    FDA2 problem = new FDA2(numberOfVariables,numberOfObjectives);
    return (DynamicProblem<?, ?>)problem;
  }
}
