package  org.uma.jmetalsp.application.fda.runner;

import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;
import org.uma.jmetalsp.algorithm.DynamicSMPSO;
import org.uma.jmetalsp.application.JMetalSPApplication;
import org.uma.jmetalsp.application.fda.algorithm.DynamicSMPSOBuilder;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.application.fda.streamingDataSource.StreamingKafkaFDA;
import org.uma.jmetalsp.consumer.impl.LocalDirectoryOutputConsumer;
import org.uma.jmetalsp.consumer.impl.SimpleSolutionListConsumer;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.problem.fda.fda1.FDA1;
import org.uma.jmetalsp.problem.fda.fda5.FDA5ProblemBuilder;
import org.uma.jmetalsp.util.spark.SparkRuntime;

import java.io.IOException;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class DynamicSMPSOFDARunner {
  public static void main(String[] args) throws IOException, InterruptedException {
    JMetalSPApplication<
            FDAUpdateData,
            FDA1,
            DynamicSMPSO> application = new JMetalSPApplication<>();
    StreamingConfigurationFDA streamingConfigurationFDA= new StreamingConfigurationFDA();


    String kafkaServer="localhost";
    int kafkaPort=2181;
    String kafkaTopic="fdadata";
    streamingConfigurationFDA.initializeKafka(kafkaServer,kafkaPort,kafkaTopic);
    application
            .setSparkRuntime(new SparkRuntime(2))
            .setProblemBuilder(new FDA5ProblemBuilder(12,3))
            .setAlgorithmBuilder(new DynamicSMPSOBuilder().setRandomGenerator(new MersenneTwisterGenerator()))
            .addAlgorithmDataConsumer(new SimpleSolutionListConsumer())
            .addAlgorithmDataConsumer(new LocalDirectoryOutputConsumer("/Users/cristobal/Documents/tesis/fda/fda5"))
            .addStreamingDataSource(new StreamingKafkaFDA(streamingConfigurationFDA))
            .run();
  }
}
