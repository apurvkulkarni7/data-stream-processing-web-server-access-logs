package org.example.streamprocessor.jobs;

import org.apache.commons.cli.CommandLine;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.connector.sink.Sink;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple14;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.OutputFileConfig;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.OnCheckpointRollingPolicy;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.api.windowing.windows.Window;
import org.apache.flink.util.Collector;
import org.example.streamprocessor.data.LogEvent;
import org.example.streamprocessor.data.MyTuple3;
import org.example.streamprocessor.sinks.sinkMain;
import org.example.streamprocessor.utils.Transformations.AvgVisitorsPerSecond;
import org.example.streamprocessor.sources.sourceMain;
import org.example.streamprocessor.utils.MetricLoggerMap;
import org.example.streamprocessor.utils.OptionsGenerator;
import org.example.streamprocessor.utils.SetupStreamExecEnv;
import org.example.streamprocessor.utils.Transformations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Duration;
import java.util.Collection;
import java.util.Objects;

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

        SingleOutputStreamOperator<String> averageVisitorPerSecond = streamParsed
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(1)))
                .apply(new AvgVisitorsPerSecond())
                .map(event -> event.toString());

        // Stream sinks
        sinkMain.mySinkTo(averageVisitorPerSecond, opt, "avg_visitor_per_sec");

        try {
            env.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    public static class findSpuriousUser implements WindowFunction<LogEvent, String, String, TimeWindow> {
        @Override
        public void apply(String key, TimeWindow timeWindow, Iterable<LogEvent> iterable, Collector<String> collector) throws Exception {
            long sum = 0L;
            for (LogEvent eventi : iterable) {
                sum += eventi.getCount();
            }
            if (sum >= 3) {
                collector.collect("Spurious visitor detected with IP Address: " + key);
            }
        }
    }

}
