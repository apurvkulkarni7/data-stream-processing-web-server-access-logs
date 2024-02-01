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
import org.example.streamprocessor.utils.RandomNumberDataTransformation;
import org.example.streamprocessor.utils.SetupStreamExecEnv;
import org.example.streamprocessor.utils.Transformations;
import org.example.streamprocessor.utils.Transformations.WindowVisitorsPerSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        SingleOutputStreamOperator<List<Long>> streamParsed = sourceStream
                .map(new RandomNumberDataTransformation.InputEventToList());

        // Instantaneous visitors per second for given window
        SingleOutputStreamOperator<String> averageVisitorPerSecond = streamParsed
                .windowAll(TumblingProcessingTimeWindows.of(Time.milliseconds(Long.parseLong(opt.getOptionValue("window-length","10000")))))
                .apply(new RandomNumberDataTransformation.WindowComputation());

        // Stream sinks
        sinkMain.mySinkTo(averageVisitorPerSecond, opt, "events-sink");

        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
