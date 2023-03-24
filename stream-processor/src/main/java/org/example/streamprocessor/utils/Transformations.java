package org.example.streamprocessor.utils;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.example.streamprocessor.data.LogEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transformations {


    public static String getMyKey(String key, LogEvent myEvent) {
        myEvent.setKey(key);
        return key;
    }

    // For parsing the log file line
    public static class InputEventToTuple extends RichMapFunction<String, Tuple14<Long, String, String, String, Double, Double, String, String, String, String, Long, String, String, Long>> {
        private JSONObject database;
        public final String regex = new StringBuilder()
                //.append("([a-zA-Z.]+) ")
                .append("([0-9\\.]+) ")
                .append("(\\S+) ")
                .append("(\\S+) ")
                .append("\\[([\\w:/\\+\\-\\s\\d{4}]+)\\] ")
                .append("\"([A-Z]+) ")
                .append("(.*) ")
                .append("([A-Z]+\\/[\\d.]+)\" ")
                .append("([0-9]+) ")
                .append("([0-9]+) ")
                .append("\"([^\"]*)\" ")
                .append("\"(.+?)\"")
                .toString();

        @Override
        public void open(Configuration parameters) throws Exception {
            // Load database from resource folder
            try {
                // Parse json database
                String jsonStr = Files.readString(Paths.get("database.json"));
                JSONObject myObj = (JSONObject) ((JSONObject) new JSONParser().parse(jsonStr)).get("ip_address");
                this.database = myObj;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            super.open(parameters);
        }

        @Override
        public Tuple14 map(String event) throws Exception {
            if (event.equals("SHUTDOWN")) {
                Tools.StopStreamJob();
            }
            Matcher m = Pattern.compile(this.regex).matcher(event);
            Tuple14 eventTuple = null;
            while (m.find()) {
                /*
                 *  0 :
                 *  1 : Visitor(client) IP address
                 *  2 : Client identity
                 *  3 : Something client
                 *  4 : Date and time
                 *  5 : Request type
                 *  6 : Requested resource
                 *  7 : HTTP Protokol
                 *  8 : HTTP response
                 * 9 : Size of the returned object
                 * 10 : Origin of the request (HTTP referrer)
                 * 11 : User agent (Browser/Machine details)
                 * */
                // Timestamp
                Long timestamp = new Timestamp(new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z").parse(m.group(4)).getTime()).getTime();

                // IP Address, Country, City, Longitude, Latitude
                String ipAddress = m.group(1);
                JSONObject ipInfo = (JSONObject) this.database.get(ipAddress);
                String country = (String) ipInfo.get("country");
                String city = (String) ipInfo.get("city");
                Double longitude = (Double) ipInfo.get("longitude");
                Double latitude = (Double) ipInfo.get("latitude");
                // Request method
                String requestMethod = m.group(5);
                String resource = m.group(6);
                String protocol = m.group(7);
                String statusCode = m.group(8);
                Long resourceSize = Long.parseLong(m.group(9));
                String referer = m.group(10);
                String userAgent = m.group(11);

                eventTuple = new Tuple14<>(
                        timestamp, ipAddress, country, city, longitude, latitude, requestMethod, resource, protocol,
                        statusCode, resourceSize, referer, userAgent, 1L
                );
            }
            return eventTuple;
        }
    }

    // Functions using LogEvent data structure
    // For parsing the log file line
    public static class InputLogParser extends RichMapFunction<String, LogEvent> {
        private File geoDb;
        private JSONObject database;

        @Override
        public void open(Configuration parameters) throws Exception {
            InputStreamReader mySourceFile;
            try {
                // Parse json database
                String jsonStr = Files.readString(Paths.get("database.json"));
                JSONObject myObj = (JSONObject) ((JSONObject) new JSONParser().parse(jsonStr)).get("ip_address");
                this.database = myObj;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            super.open(parameters);
        }

        @Override
        public LogEvent map(String input) throws Exception {
            if (input.equals("SHUTDOWN")) {
                Tools.StopStreamJob();
            }

            LogEvent myEvent = new LogEvent(input);
            myEvent.logParser(this.database);
            myEvent.setCount(1L);
            return myEvent;
        }
    }

    public static class LogEventToTuple2 implements MapFunction<LogEvent, Tuple2<String, Long>> {
        @Override
        public Tuple2<String, Long> map(LogEvent logEvent) throws Exception {
            return new Tuple2<>(logEvent.getKey(), logEvent.getCount());
        }
    }

    public static class AvgVisitorsPerSecond implements AllWindowFunction<Tuple14<Long, String, String, String, Double, Double, String, String, String, String, Long, String, String, Long>, Tuple2<Long,Double>, TimeWindow> {
        @Override
        public void apply(TimeWindow timeWindow, Iterable<Tuple14<Long, String, String, String, Double, Double, String, String, String, String, Long, String, String, Long>> iterable, Collector<Tuple2<Long,Double>> collector) throws Exception {
            long count = 0L;
            Tuple14 last_event = null;
            for (Tuple14 event : iterable) {
                count++;
                last_event = event;
            }
            double windowLength = (timeWindow.getEnd() - timeWindow.getStart()) / 1000; // in seconds
            Double result = Double.valueOf(count) / windowLength;
            collector.collect(new Tuple2<>((Long)last_event.f0, result));
        }
    }
}

