package org.example.streamprocessor.utils;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculates throughput in elements/second/core
 */
public class MetricLoggerMap<T> implements MapFunction<T, T> {

    private static final Logger MetricLogger = LoggerFactory.getLogger("metric");
    private final int loggingType;
    private final String identifier;
    private final long loggingInterval; // in ms
    private long totalReceived = 0;
    private int init = -1;
    private double throughput = 0;
    private long latency;
    private long timeOne;
    private long initLogTimeMs;
    private long now;
    private long timeTwo;
    private long myTimeDiff;
    private boolean printHeader = true;

    public MetricLoggerMap() {
        this.loggingInterval = 10_000;
        this.identifier = "";
        this.loggingType = 2;
    }

    public MetricLoggerMap(String identifier) {
        this.loggingInterval = 10_000;
        this.identifier = identifier;
        this.loggingType = 2;
    }
    
    @Override
    public T map(T element) throws Exception {
        totalReceived++;
        timeTwo = System.currentTimeMillis();
        if (timeTwo - timeOne >= loggingInterval) {
            timeOne = timeTwo;
            now = System.currentTimeMillis();
            // Average throughput for the last "logFreq" elements
            if (loggingType == 0 || loggingType == 2) {
                if (init == -1) {
                    // init (the first)
                    init = 1;
                    initLogTimeMs = now - loggingInterval;
                } else {
                    myTimeDiff = now - initLogTimeMs;
                    throughput = Math.round((double) totalReceived * ((double) 1000 / (double) myTimeDiff));
                }
            }

            if (loggingType == 1 || loggingType == 2) {
                // Latency calculation (in miliseconds)
                if (element.getClass() == Tuple3.class) {
                    // For processed events
                    latency = System.currentTimeMillis() - ((Tuple3<Long, String, Long>) element).f0;
                } else {

                    String regex = "\\[(.*?)\\]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(element.toString());

                    long unixTimestamp = 0;
                    if (matcher.find()) {
                        String dateTimeString = matcher.group(1);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                        unixTimestamp = dateTime.toInstant(ZoneOffset.ofHours(1)).getEpochSecond() * 1000; //in ms
                    } else {
                        System.out.println("No match found.");
                    }
                    // For unprocessed event
                    latency = now - unixTimestamp; //in ms
                }
            }

            if (printHeader) {
                MetricLogger.info("identifier,thread_info,throughput_events_per_sec_pr_core,lateny_ms,elements processed");
                printHeader = false;
            }
            // Final log string
            String logString = identifier + "," + Thread.currentThread().getName() + "," +
                    throughput + " ," + latency + "," + totalReceived;
            MetricLogger.info(logString);
        }
        return element;
    }
}
