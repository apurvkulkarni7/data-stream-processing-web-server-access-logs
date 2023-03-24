package org.example.generator.cases;

import org.example.generator.Simulator;
import org.example.generator.util.DataGenerator;
import org.example.generator.util.MyKafkaProducer;
import org.slf4j.Logger;

public class RandomInterval {

    private final DataGenerator generator_;
    private final MyKafkaProducer producer_;
    private final long runtime_;
    private final String label_;
    private long messageCount_;
    private final Logger LOGGER = Simulator.LOGGER;
    private final long minPause_ = 0;
    private final long maxPause_;
    private final long bias_ = 100; // to generate most numbers ranging from 0 to 10;

    public RandomInterval(DataGenerator dataGenerator, MyKafkaProducer producer, long runtime, long maxPause, String label){
        this.runtime_ = runtime*1000; // Converting seconds to milliseconds
        this.producer_ = producer;
        this.generator_ = dataGenerator;
        this.label_ = label;
        this.messageCount_ = 0;
        this.maxPause_ = maxPause;
    }

    public long getRuntime(){return this.runtime_;}
    public DataGenerator getGenerator() {return this.generator_;}
    public MyKafkaProducer getProducer() {return this.producer_;}
    public String getLabel() {return this.label_;}

    public long getMessageCount() {return messageCount_;}
    public void increaseMessageCount(){this.messageCount_++;}

    public void printInfo(String type_){
        String boundaryTop = "--------------------------------------------------";
        if (type_=="start"){
            LOGGER.info(getLabel() + " " + boundaryTop);
            LOGGER.info("Simulator started");
            LOGGER.info("Runtime:{} msec ({} min)",this.getRuntime(),this.getRuntime()/(1000*60));
            LOGGER.info("Sending data to Kafka topic: {} Bootstrap-server: {}",this.getProducer().kafkaTopic,this.getProducer().bootstrapServer);
        } else if (type_=="end"){
            LOGGER.info("Simulator stopped");
            LOGGER.info("Total messages sent: {}",getMessageCount());
        }
    }

    public void run(){
        try {
            long start = System.currentTimeMillis();
            long end = start + this.getRuntime();

            LOGGER.info("Simulator started");
            LOGGER.info("Runtime:{} msec ({} min)",this.getRuntime(),this.getRuntime()/(1000*60));
            LOGGER.info("Sending data to Kafka topic: {} Bootstrap-server: {}",this.getProducer().kafkaTopic,this.getProducer().bootstrapServer);

            while (System.currentTimeMillis() < end){
                // Constructing a log line
                String myLogLine = getGenerator().generate();

                // Sending to topic
                getProducer().send(myLogLine);

                this.increaseMessageCount();
                Thread.sleep(generateBiasedRandomNumber());
            }
            getProducer().close();
            LOGGER.info("Simulator stopped");
            LOGGER.info("Total messages sent: {}",getMessageCount());
        } catch (Exception e) {
            getProducer().close();
            LOGGER.error("Program interrupted. Producer closed");
            e.printStackTrace();
        }
    }

    public long generateBiasedRandomNumber(){
        double init = Math.random();
        long randomNumber;
        if (init<0.70) {
            randomNumber = Math.round(Math.random()*(this.bias_ - this.minPause_) + this.minPause_);
        } else {
            randomNumber = Math.round(Math.random()*(this.maxPause_ - this.bias_) + this.bias_);
        }
        return randomNumber;
    }
}