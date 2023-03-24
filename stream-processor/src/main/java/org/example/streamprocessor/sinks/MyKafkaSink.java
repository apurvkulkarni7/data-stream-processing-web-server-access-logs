package org.example.streamprocessor.sinks;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;

public class MyKafkaSink {

    private final String bootstrapServer;
    private final String kafkaTopic;

    public MyKafkaSink(String bootstrapServer, String kafkaTopic) {
        this.bootstrapServer = bootstrapServer;
        this.kafkaTopic = kafkaTopic;
    }

    // Sinks
    public KafkaSink build() {
        return KafkaSink
                .<String>builder()
                .setBootstrapServers(bootstrapServer)
                .setRecordSerializer(
                        KafkaRecordSerializationSchema
                                .builder()
                                .setTopic(kafkaTopic)
                                .setValueSerializationSchema(new SimpleStringSchema())
                                //.setKeySerializationSchema(new SimpleStringSchema())
                                .build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
    }
}
