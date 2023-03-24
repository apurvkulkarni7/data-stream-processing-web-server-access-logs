package org.example.streamprocessor.sinks;

import org.apache.commons.cli.CommandLine;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.io.File;
import java.time.Duration;

import static org.example.streamprocessor.jobs.StreamProcessing.LOGGER;
import static org.example.streamprocessor.utils.Tools.deleteDirectory;

public class sinkMain {
    public static void mySinkTo(DataStream inputStream, CommandLine opt, String identifier) {
        if (opt.getOptionValue("sink-type").equals("kafka")) {
            // String kafkaTopic = opt.getOptionValue("sink-kafka-topic", "flink-out");
            String kafkaTopic = identifier;
            String kafkaBootstrapServer = opt.getOptionValue("sink-bootstrap-server", "localhost:9092");
            LOGGER.info("Writing output to Kafka-topic:{}, Kafka-Bootstrap-Server:{}", kafkaTopic, kafkaBootstrapServer);
            inputStream.sinkTo(new MyKafkaSink(kafkaBootstrapServer, kafkaTopic).build()).name("sink"); // Kafka
        } else if (opt.getOptionValue("sink-type").equals("stdout")) {
            inputStream
                    .print()
                    .name(identifier);
        } else if (opt.getOptionValue("sink-type").equals("filesystem")) {
            Path outFile = new Path("./out/" + identifier);
            deleteDirectory(new File(outFile.toString()));
            LOGGER.info("Writing output to: {}", outFile.getPath());
            StreamingFileSink<String> sink = StreamingFileSink.forRowFormat(
                            outFile,
                            new SimpleStringEncoder<String>("UTF-8")
                    )
                    .withRollingPolicy(
                            DefaultRollingPolicy
                                    .builder()
                                    .withRolloverInterval(Duration.ofSeconds(Long.parseLong(opt.getOptionValue("runtime", "300"))))
                                    .withMaxPartSize(MemorySize.parse("2", MemorySize.MemoryUnit.GIGA_BYTES))
                                    .build()
                    )
                    .build();
            // write the stream to the file system
            inputStream.addSink(sink);
        }
    }


}
