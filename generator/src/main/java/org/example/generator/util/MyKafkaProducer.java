package org.example.generator.util;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class MyKafkaProducer {

    public String kafkaTopic;
    public String bootstrapServer;
    public Producer producer;
    public int partitions_num = 3;

    //public MyKafkaProducer(String kafkaTopic, String bootstrapServer, int partitions_num) {
    //    this.kafkaTopic = kafkaTopic;
    //    this.bootstrapServer = bootstrapServer;
    //    this.producer = build();
    //    //this.partitions_num = partitions_num;
    //}

    public MyKafkaProducer(String kafkaTopic, String bootstrapServer) {
        this.kafkaTopic = kafkaTopic;
        this.bootstrapServer = bootstrapServer;
        this.producer = build();
        //this.partitions_num = 3;
    }

    public Producer<String, String> build() {

        // Creating instance of properties to access producer config
        Properties props = new Properties();

        // Assign localhost id
        props.put("bootstrap.servers", bootstrapServer);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return new KafkaProducer<String, String>(props);
    }

    public void send(String input){
        //producer.send(new ProducerRecord<>(this.kafkaTopic, partition_i, "ad", input));
        producer.send(new ProducerRecord<>(this.kafkaTopic, input));
    }

    public void send(String input, Integer partition_i){
        producer.send(new ProducerRecord<>(this.kafkaTopic, partition_i, null, input));
    }

    public void close() {
        producer.close();
    }
}
