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
package org.uma.jmetalsp.application.biobjectivetsp.streamingDataSource;

import org.uma.jmetalsp.application.biobjectivetsp.sparkutil.StreamingConfigurationTSP;
import kafka.serializer.StringDecoder;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.uma.jmetal.solution.PermutationSolution;

import org.uma.jmetalsp.application.biobjectivetsp.updateData.MultiobjectiveTSPUpdateData;
import org.uma.jmetalsp.problem.DynamicProblem;
import org.uma.jmetalsp.streamingdatasource.StreamingDataSource;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * StreamingKafkaTSP class for get streaming kafka data for TSP problem
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class StreamingKafkaTSP implements StreamingDataSource<MultiobjectiveTSPUpdateData> {
    private static final int DISTANCE = 0 ;
    private static final int COST = 1 ;

    private DynamicProblem<PermutationSolution<Integer>, MultiobjectiveTSPUpdateData> problem ;
    private HashMap<String, String> kafkaParams ;
    private HashSet<String> topicsSet;
    private StreamingConfigurationTSP streamingConfigurationTSP;
    public StreamingKafkaTSP(StreamingConfigurationTSP streamingConfigurationTSP ){
        this.streamingConfigurationTSP= streamingConfigurationTSP;
        this.kafkaParams=streamingConfigurationTSP.getKafkaParams();
        this.topicsSet = new HashSet<>(Arrays.asList(streamingConfigurationTSP.getKafkaTopics().split(",")));

    }

  /**
   * Add problem to update
   * @param problem
   */
    @Override
    public void setProblem(DynamicProblem<?, MultiobjectiveTSPUpdateData> problem) {
        this.problem = (DynamicProblem<PermutationSolution<Integer>, MultiobjectiveTSPUpdateData>) problem;
    }
  /**
   * Create a MultiobjectiveTSPUpdateData from data has been generated by Kafka Server and add into a Map
   * For each element in the Map, update the problem
   * @param context
   */
    @Override
    public void start(JavaStreamingContext context) {
        //JMetalLogger.logger.info("Starting StreamingDirectoryTSP ......");

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

        JavaDStream<MultiobjectiveTSPUpdateData> routeUpdates =
                lines.map(new Function<String, MultiobjectiveTSPUpdateData>() {
                    @Override
                    public MultiobjectiveTSPUpdateData call(String s) throws Exception {
                        String[] split = s.split(" ");
                        MultiobjectiveTSPUpdateData data =
                                new MultiobjectiveTSPUpdateData(
                                        split[0].equals("c") ? COST : DISTANCE,
                                        Integer.valueOf(split[1]),
                                        Integer.valueOf(split[2]),
                                        Integer.valueOf(split[3])) ;

                        return data;
                    }
                });

        routeUpdates.foreachRDD(new VoidFunction<JavaRDD<MultiobjectiveTSPUpdateData>>() {
            @Override
            public void call(JavaRDD<MultiobjectiveTSPUpdateData> mapJavaRDD) throws Exception {
                List<MultiobjectiveTSPUpdateData> dataList = mapJavaRDD.collect();
                for (MultiobjectiveTSPUpdateData data : dataList) {
                    problem.update(data);
                }
            }
        });

    }
}
