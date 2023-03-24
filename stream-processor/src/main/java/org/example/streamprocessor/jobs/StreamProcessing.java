package org.example.streamprocessor.jobs;

import org.apache.commons.cli.CommandLine;
import org.apache.flink.api.java.tuple.Tuple14;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.example.streamprocessor.sinks.sinkMain;
import org.example.streamprocessor.sources.sourceMain;
import org.example.streamprocessor.utils.OptionsGenerator;
import org.example.streamprocessor.utils.SetupStreamExecEnv;
import org.example.streamprocessor.utils.Transformations;
import org.example.streamprocessor.utils.Transformations.WindowVisitorsPerSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamProcessing {
    public static final Logger LOGGER = LoggerFactory.getLogger(StreamProcessing.class);

    public static void main(String[] args) {

        // Get cli argument and parse them
        CommandLine opt = new OptionsGenerator(args).build();

        // Defining stream execution environment
        StreamExecutionEnvironment env = new SetupStreamExecEnv(opt).build();

        // Stream source
        DataStream<String> sourceStream = sourceMain
                .fromSource(env, opt);

        // Stream transformations
        SingleOutputStreamOperator<Tuple14<Long, String, String, String, Double, Double, String, String, String, String, Long, String, String, Long>> streamParsed = sourceStream
                .map(new Transformations.InputEventToTuple());

        // Instantaneous visitors per second for given window
        SingleOutputStreamOperator<String> averageVisitorPerSecond = streamParsed
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(1)))
                .apply(new WindowVisitorsPerSecond())
                .map(event -> event.toString());

        //TODO: Develop function for average visitors per second

        // Stream sinks
        sinkMain.mySinkTo(averageVisitorPerSecond, opt, "window_visitor_per_sec");

        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
