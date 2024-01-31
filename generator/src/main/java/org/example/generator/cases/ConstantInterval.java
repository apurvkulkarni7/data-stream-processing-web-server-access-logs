package org.example.generator.cases;

import org.example.generator.RandomNumberGenerator;
import org.example.generator.Simulator;
import org.example.generator.util.DataGenerator;
import org.example.generator.util.MyKafkaProducer;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class ConstantInterval {

    private final RandomNumberGenerator generator_;
    private final MyKafkaProducer producer_;
    private final long runtime_;
    private final long messageCount_;
    private final long frequency_;
    private final Logger LOGGER = Simulator.LOGGER;

    public ConstantInterval(RandomNumberGenerator dataGenerator, MyKafkaProducer producer, long runtime, long frequency_){
        this.runtime_ = runtime*1000; // Converting seconds to milliseconds
        this.producer_ = producer;
        this.generator_ = dataGenerator;
//        this.label_ = label;
        this.messageCount_ = 0;
        this.frequency_ = frequency_;
    }

    public long getRuntime(){return this.runtime_;}
    public RandomNumberGenerator getGenerator() {return this.generator_;}
    public MyKafkaProducer getProducer() {return this.producer_;}
//    public String getLabel() {return this.label_;}
    public long getMessageCount() {return messageCount_;}

//    public void printInfo(String type_){
//        String boundaryTop = "--------------------------------------------------";
//        if (type_=="start"){
//            LOGGER.info(getLabel() + " " + boundaryTop);
//            LOGGER.info("Simulator started");
//            LOGGER.info("Runtime:{} msec ({} min)",this.getRuntime(),this.getRuntime()/(1000*60));
//            LOGGER.info("Sending data to Kafka topic: {} Bootstrap-server: {}",this.getProducer().kafkaTopic,this.getProducer().bootstrapServer);
//        } else if (type_=="end"){
//            LOGGER.info("Simulator stopped");
//            LOGGER.info("Total messages sent: {}",getMessageCount());
//        }
//    }



    public void run(){
        try {
            long startTime = System.currentTimeMillis();

            long duration = this.runtime_;

            LOGGER.info("Simulator started");
            LOGGER.info("Runtime:{} msec ({} min)",this.getRuntime(),this.getRuntime()/(1000*60));
            LOGGER.info("Sending data to Kafka topic: {} Bootstrap-server: {}",this.getProducer().kafkaTopic,this.getProducer().bootstrapServer);

            long delay = 0;

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            long period = Math.round(1000000000 / this.frequency_); // convert frequency to period in nanoseconds
            scheduler.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                    String myLogLine = getGenerator().generate();
                    String myString = String.valueOf(getGenerator().generate());
                    // Sending to topic
                    getProducer().send(myString);
                }
            }, delay, period, TimeUnit.NANOSECONDS);

            scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    scheduler.shutdown();
                }
            },duration,TimeUnit.MILLISECONDS);

            LOGGER.info("Simulator stopped");
            LOGGER.info("Total messages sent: {}",getMessageCount());
        } catch (Exception e) {
            getProducer().close();
            LOGGER.error("Program interrupted. Producer closed");
            e.printStackTrace();
        }
    }
}