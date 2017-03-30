package org.uma.jmetalsp.examples.dynamictsp;

import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetalsp.StreamingDataSource;
import org.uma.jmetalsp.observer.Observable;
import org.uma.jmetalsp.observeddata.MatrixObservedData;

/**
 * This class emits a value periodically after a given delay (in milliseconds)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StreamingTSPSource implements StreamingDataSource<MatrixObservedData<Double>, Observable<MatrixObservedData<Double>>> {
	private Observable<MatrixObservedData<Double>> updateData ;
	private int dataDelay ;

  /**
   *
   * @param updateData
   * @param dataDelay Delay in milliseconds
   */
	public StreamingTSPSource(Observable<MatrixObservedData<Double>> updateData, int dataDelay) {
		this.updateData = updateData ;
		this.dataDelay = dataDelay ;
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(dataDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Emitting new data") ;
      int x = JMetalRandom.getInstance().nextInt(0, 100) ;
      int y = JMetalRandom.getInstance().nextInt(0, 100) ;
      double value = JMetalRandom.getInstance().nextDouble(1.0, 4000) ;
			updateData.setChanged(); ;
			updateData.notifyObservers(new MatrixObservedData<>("COST", x, y, value));
		}
	}
}
