package  org.uma.jmetalsp.application.fda.streamingDataSource;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.uma.jmetal.solution.DoubleSolution;


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import kafka.serializer.StringDecoder;
import org.uma.jmetalsp.application.fda.sparkutil.StreamingConfigurationFDA;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.problem.fda.FDAUpdateData;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import scala.Tuple2;

/**
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
public class StreamingKafkaFDA implements StreamingDataSource<FDAUpdateData>,Serializable {
  private HashMap<String, String> kafkaParams ;
  private HashSet<String> topicsSet;
  private StreamingConfigurationFDA streamingConfigurationFDA;
  private DynamicProblem<DoubleSolution,FDAUpdateData> problem ;
  public StreamingKafkaFDA(StreamingConfigurationFDA streamingConfigurationFDA ){
    this.streamingConfigurationFDA= streamingConfigurationFDA;
    this.kafkaParams=streamingConfigurationFDA.getKafkaParams();
    this.topicsSet = new HashSet<>(Arrays.asList(streamingConfigurationFDA.getKafkaTopics().split(",")));
  }
  @Override
  public void setProblem(DynamicProblem<?, FDAUpdateData> problem) {
    this.problem= (DynamicProblem<DoubleSolution,FDAUpdateData>)problem;
  }

  @Override
  public void start(JavaStreamingContext context) {
    JavaPairInputDStream<String, String> messages;
    messages = KafkaUtils.createDirectStream(
            context,
            String.class,
            String.class,
            StringDecoder.class,
            StringDecoder.class,
            kafkaParams,
            topicsSet
    );
    JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
      @Override
      public String call(Tuple2<String, String> tuple2) {
        return tuple2._2();
      }
    });

    JavaDStream<FDAUpdateData> timeUpdates =
            lines.map(new Function<String, FDAUpdateData>() {
              @Override
              public FDAUpdateData call(String s) throws Exception {
                FDAUpdateData data = new FDAUpdateData( Integer.valueOf(s));
                return data;
              }
            });

    timeUpdates.foreachRDD(new VoidFunction<JavaRDD<FDAUpdateData>>() {
      @Override
      public void call(JavaRDD<FDAUpdateData> mapJavaRDD) throws Exception {
        List<FDAUpdateData> dataList = mapJavaRDD.collect();
        for (FDAUpdateData data : dataList) {
          problem.update(data);
        }
      }
    });
  }
}
