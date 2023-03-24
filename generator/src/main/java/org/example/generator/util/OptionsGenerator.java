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

        Option file = Option.builder()
                .option("f")
                .longOpt("file")
                .hasArg(true)
                .argName("path-to-database-file")
                .desc("Database file. (Required)")
                .build();

        Option kafkaTopic = Option.builder()
                .option("kt")
                .longOpt("kafka-topic")
                .hasArg(true)
                .argName("kafka-topic-name")
                .desc("Kafka topic where messages will be published. (Required)")
                .build();

        Option bootstrapServer = Option.builder()
                .option("bs")
                .longOpt("bootstrap-server")
                .hasArg(true)
                .argName("host:port")
                .desc("Kafka server address where messages will be published. (Required)")
                .build();

        Option databaseFile = Option.builder()
                .option("db")
                .longOpt("database-file")
                .hasArg(true)
                .optionalArg(true)
                .argName("path-to-file")
                .desc("Path to file to which log database will be saved.")
                .build();

        Option runTime = Option.builder()
                .option("rt")
                .longOpt("run-time")
                .hasArg(true)
                .argName("<milliseconds>")
                .desc("Runtime of the program. Optional argument (default value - 10000).")
                .build();
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

        options.addOption(help);
        options.addOption(file);
        options.addOption(kafkaTopic);
        options.addOption(bootstrapServer);
        options.addOption(runTime);
        options.addOption(loadHz);
        options.addOption(databaseFile);

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

//            if (!cmd.hasOption("help")){
//                if (!(cmd.hasOption("kt") && cmd.hasOption("bs"))) {
//                    Exception e = new ParseException("All required arguments are not provided. "+
//                            "Please refer help (-h) for more information");
//                    throw new RuntimeException(e);
//                }
//            }
        } catch (ParseException exp) {
            // oops, something went wrong
            throw new RuntimeException(exp);
        }
        return cmd;
    }
}