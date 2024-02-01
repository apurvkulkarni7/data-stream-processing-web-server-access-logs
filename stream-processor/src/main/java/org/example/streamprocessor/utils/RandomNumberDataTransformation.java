package org.example.streamprocessor.utils;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple14;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.AllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RandomNumberDataTransformation {
    public static class InputEventToList extends RichMapFunction<String, List<Long>> {
//        @Override
//        public void open(Configuration parameters) throws Exception {
//            // Load database from resource folder
//            try {
//                // Parse json database
//                String jsonStr = Files.readString(Paths.get("database.json"));
//                JSONObject myObj = (JSONObject) ((JSONObject) new JSONParser().parse(jsonStr)).get("ip_address");
//                this.database = myObj;
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            super.open(parameters);
//        }

        @Override
        public List<Long> map(String eventIn) throws Exception {

            return Arrays.stream(eventIn.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        }
    }





    // Functions to calculate visitors per second for given window period
    public static class WindowVisitorsPerSecond implements AllWindowFunction<List<Long>, List<Long>, TimeWindow> {
        @Override
        public void apply(TimeWindow timeWindow, Iterable<List<Long>> iterable, Collector<List<Long>> collector) {
            List<Long> firstElement = iterable.iterator().next();
            long eventSize = firstElement.size();

            List<Long> avgValCounter = new ArrayList<Long>();
            for (int i = 0; i < eventSize; i++) {
                avgValCounter.add(0L);
            }
            List<Long> maxValList = new ArrayList<Long>();
            for (int i = 0; i < eventSize; i++) {
                maxValList.add(0L);
            }
            List<Long> minValList = new ArrayList<Long>();
            for (int i = 0; i < eventSize; i++) {
                minValList.add(0L);
            }

            long count = 0L;
            List<Long> last_event = null;

            // Adding first element
            avgValCounter.set(0,firstElement.get(0));
            avgValCounter.set(1,avgValCounter.get(1) + firstElement.get(1));
            maxValList.set(1,Math.max(maxValList.get(1),firstElement.get(1)));
            minValList.set(1,Math.max(minValList.get(1),firstElement.get(1)));

            for (List<Long> event_i : iterable) {
                count++;
                for (Long element_i: event_i){
                    int element_i_index = event_i.indexOf(element_i);
                    if (element_i_index == 0){
                        avgValCounter.set(element_i_index,firstElement.get(element_i_index));
                        System.out.println(element_i);
                    } else {
                        // Average counter
                        avgValCounter.set(element_i_index, avgValCounter.get(element_i_index) + element_i);

                        // Max counter
                        maxValList.set(element_i_index, Math.max(maxValList.get(element_i_index),element_i));

                        // Min counter
                        minValList.set(element_i_index, Math.min(minValList.get(element_i_index),element_i));
                    }
                }
            }

            double windowLength = (timeWindow.getEnd() - timeWindow.getStart()) / 1000; // in seconds
            Double result = Double.valueOf(count) / windowLength;
            collector.collect(new Tuple2<>((Long) last_event.f0, result));
        }
    }

    // should be used when streams are keyed by ip addresses
    public static class findSpuriousUser implements WindowFunction<Tuple14<Long, String, String, String, Double, Double, String, String, String, String, Long, String, String, Long>, String, String, TimeWindow> {
        @Override
        public void apply(String key, TimeWindow timeWindow, Iterable<Tuple14<Long, String, String, String, Double, Double, String, String, String, String, Long, String, String, Long>> iterable, Collector<String> collector) throws Exception {
            Long sum = 0L;
            for (Tuple14 eventi : iterable) {
                sum += 1;
            }
            if (sum >= 3) {
                collector.collect("Spurious visitor detected with IP Address: " + key);
            }
        }
    }
}
