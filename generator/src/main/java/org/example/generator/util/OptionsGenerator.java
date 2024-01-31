package org.example.generator.util;

import org.apache.commons.cli.*;

/**
 * Reference: https://commons.apache.org/proper/commons-cli/usage.html
 */
public class OptionsGenerator {

    public String[] args;

    public OptionsGenerator(String[] args) {
        this.args = args;
    }

    public CommandLine build() {

        Options options = new Options();

        // Adding options
        Option help = Option.builder()
                .option("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Prints this message")
                .build();

        Option kafkaTopic = Option.builder()
                .option("kt")
                .longOpt("kafka-topic")
                .hasArg(true)
                .argName("kafka-topic-name")
                .desc("Kafka topic where messages will be published. (Required)")
                .build();
        options.addOption(kafkaTopic);

        Option bootstrapServer = Option.builder()
                .option("bs")
                .longOpt("bootstrap-server")
                .hasArg(true)
                .argName("host:port")
                .desc("Kafka server address where messages will be published. (Required)")
                .build();
        options.addOption(bootstrapServer);

        Option runTime = Option.builder()
                .option("rt")
                .longOpt("run-time")
                .hasArg(true)
                .argName("<milliseconds>")
                .desc("Runtime of the program. Optional argument (default value - 10000).")
                .build();
        options.addOption(runTime);

        Option generatorType = Option.builder()
                .option("gt")
                .longOpt("generator-type")
                .hasArg(true)
                .argName("<constant/random>")
                .desc("To generate events at constant or random rate. For 'constant' option, frequency of events is set using --loadHz. For 'random' option, maximum pause between each event (10 seconds)")
                .build();
        options.addOption(generatorType);

        Option loadHz = Option.builder()
                .option("loadHz")
                .longOpt("loadHz")
                .hasArg(true)
                .argName("<events-per-seconds>")
                .desc("Events per seconds that will be pushed to Kafka topic. Used with '--generator-type constant' option." +
                        "Optional argument (default value - 10000).")
                .build();
        loadHz.setType(long.class);
        options.addOption(loadHz);

        Option randomNumberAmount = Option.builder()
                .option("rma")
                .longOpt("random-number-amount")
                .hasArg(true)
                .argName("<number>")
                .desc("Amount or Number of random numbers in an event.")
                .build();
        randomNumberAmount.setType(int.class);
        options.addOption(randomNumberAmount);

        Option randomNumberMax = Option.builder()
                .option("rmmax")
                .longOpt("random-number-max")
                .hasArg(true)
                .argName("<number>")
                .desc("Maximum value of random numbers in an event.")
                .build();
        randomNumberMax.setType(int.class);
        options.addOption(randomNumberMax);

        Option randomNumberMin = Option.builder()
                .option("rmmin")
                .longOpt("random-number-min")
                .hasArg(true)
                .argName("<number>")
                .desc("Minimum value of random numbers in an event.")
                .build();
        randomNumberMin.setType(int.class);
        options.addOption(randomNumberMin);

        options.addOption(help);

        // create the parser
        CommandLineParser parser = new DefaultParser();

        CommandLine cmd = null;
        try {
            // parse the command line arguments
            cmd = parser.parse(options, this.args);

            if (cmd.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("myProducer.jar", options);
                System.exit(0);
            }

        } catch (ParseException exp) {
            // oops, something went wrong
            throw new RuntimeException(exp);
        }
        return cmd;
    }
}