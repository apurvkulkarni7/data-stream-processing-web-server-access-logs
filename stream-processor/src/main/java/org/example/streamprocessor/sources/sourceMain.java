package org.example.streamprocessor.sources;

import org.apache.commons.cli.CommandLine;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.io.TextInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
import org.example.streamprocessor.jobs.StreamProcessing;

public class sourceMain {
    public static DataStream<String> fromSource(StreamExecutionEnvironment env, CommandLine opt) {
        if (opt.getOptionValue("source-type").equals("kafka")) {
            String kafkaTopic = opt.getOptionValue("source-kafka-topic", "logs");
            String kafkaBootstrapServer = opt.getOptionValue("source-bootstrap-server", "localhost:9092");
            String kafkaGroupId = opt.getOptionValue("source-group-id", "logs-group");
            StreamProcessing.LOGGER.info("Reading data from Kafka-topic:{}, Bootstrap-server:{}", kafkaTopic, kafkaBootstrapServer);
            return env.fromSource(
                    new MyKafkaSource(kafkaBootstrapServer, kafkaTopic, kafkaGroupId).build(),
                    WatermarkStrategy.noWatermarks(),
                    "Kafka source"
            ).name("source");
        } else if (opt.getOptionValue("source-type").equals("file")) {
            StreamProcessing.LOGGER.info("Reading data from: {}", opt.getOptionValue("srcFile"));
            // method 1
            //inputStream = env.readTextFile(opt.getRequired("srcFile"));

            // method 2
            String path = opt.getOptionValue("source-file");
            TextInputFormat inputFormat = new TextInputFormat(new Path(path));
            inputFormat.setCharsetName("UTF-8");
            return env.readFile(
                    inputFormat,
                    path,
                    FileProcessingMode.PROCESS_CONTINUOUSLY,
                    1l,
                    BasicTypeInfo.STRING_TYPE_INFO
            );
        } else {
            StreamProcessing.LOGGER.error("Invalid/Unimplemented source selected");
            System.exit(0);
            return null;
        }
    }
}
