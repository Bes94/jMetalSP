package org.uma.jmetalsp.qualityindicator;

import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;

import java.util.List;

public class CoverageFront <S extends Solution<?>> {

    private double coverageValue;
    private GenericIndicator<S> indicator;

    private List<S> lastFront;
    public CoverageFront(double coverageValue, GenericIndicator<S> indicator){
        this.coverageValue = coverageValue;
        this.indicator = indicator;
    }

    public boolean isCoverage(List<S> front){
        double coverage = this.indicator.evaluate(front);
        boolean result = coverage>coverageValue;
        if(result){
            lastFront = front;
        }else {
            coverage = this.indicator.evaluate(lastFront) ;
            result = coverage>coverageValue;
            if(result){
                lastFront = front;
            }
        }
        return result;
    }
    public void updateFront(Front front){
        try {
            this.indicator.setReferenceParetoFront(front);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
}
