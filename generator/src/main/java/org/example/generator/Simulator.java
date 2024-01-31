package org.example.generator;

import org.apache.commons.cli.CommandLine;
import org.example.generator.cases.ConstantInterval;
import org.example.generator.cases.RandomInterval;
import org.example.generator.util.MyKafkaProducer;
import org.example.generator.util.OptionsGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Simulator
{

    /**
     * @param args -f/--file -kt/--kafka-topic -bs/--bootstrap-server -mp/--max-pause
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(Simulator.class);

    public static void main( String[] args ) {

        // Get cli argument and parse them
        CommandLine opt = new OptionsGenerator(args).build();
        //String databaseFile = opt.getOptionValue("database-file","database.json");
        String kafkaTopic = opt.getOptionValue("kafka-topic");
        String bootstrapServer = opt.getOptionValue("bootstrap-server");
        long runTime = Long.parseLong(opt.getOptionValue("run-time", "1800000")); // half hour
        long loadHz = Long.parseLong(opt.getOptionValue("loadHz", "1000")); // events per second
        long maxPause = Long.parseLong(opt.getOptionValue("max-pause", "1000")); // events per second
        String generatorType = opt.getOptionValue("generator-type","constant");
        int randomNumberAmount = Integer.parseInt(opt.getOptionValue("random-number-amount","1"));
        int randomNumberMax = Integer.parseInt(opt.getOptionValue("random-number-max","1"));
        int randomNumberMin = Integer.parseInt(opt.getOptionValue("random-number-min","1000"));

        // Initialize data generator
         RandomNumberGenerator myGenerator = new RandomNumberGenerator(randomNumberAmount,randomNumberMax,randomNumberMin);

        // Initialize producer
        MyKafkaProducer myProducer = new MyKafkaProducer(kafkaTopic,bootstrapServer);

        // Running the simulator
        // Multithreaded approach
        int numOfThreads = 1; // number of threads to create
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            Runnable task = new Runnable() {
                public void run() {
                    if (generatorType.equals("constant")) {
                        // code to run in each thread
                        //new ConstantInterval(myGenerator, myProducer, runTime, loadHz, "Log generator").run();
                        //new ConstantInterval(myGenerator, myProducer, runTime, loadHz, "Log generator").run();
                        new ConstantInterval(myGenerator, myProducer, runTime, loadHz).run();
                    } else if (generatorType.equals("random")) {
                        new RandomInterval(myGenerator, myProducer, runTime, maxPause).run();
                    }
                }
            };
            executor.execute(task);
        }

        executor.shutdown();
    }
}