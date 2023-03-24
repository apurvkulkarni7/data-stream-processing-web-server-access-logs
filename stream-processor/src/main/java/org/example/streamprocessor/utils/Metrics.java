package org.example.streamprocessor.utils;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.example.streamprocessor.data.LogEvent;

public class Metrics {
    public static class MyMapper extends RichMapFunction<LogEvent, LogEvent> {
        private transient Counter counter;
        private transient Counter counter2;
        private long time1;
        //        private long time2;
//        private boolean sub = false;
        private long duration;
        private double rate;
        private long count_temp;
        private long count_sub;

        @Override
        public void open(Configuration config) {
            this.counter = getRuntimeContext()
                    .getMetricGroup()
                    .counter("myCounter_IncomingMessages");
            this.counter2 = getRuntimeContext()
                    .getMetricGroup()
                    .counter("myCounter_IncomingMessages");
            this.time1 = System.currentTimeMillis();
            this.count_temp = 0;
            this.count_sub = 0;
        }

        @Override
        public LogEvent map(LogEvent value) throws Exception {
            this.counter.inc();
            System.out.println("Incoming message count: " + this.counter.getCount());
//            System.out.println(getRuntimeContext().getMetricGroup().getIOMetricGroup().getNumRecordsOutCounter());

            this.duration = System.currentTimeMillis() - this.time1;
            if (this.duration > 3000) {
                this.rate = (this.counter.getCount() - this.count_temp) / 3;
                System.out.println("rate: " + this.rate);
                this.count_temp = this.counter.getCount();
                this.time1 = System.currentTimeMillis();
            }
//            System.out.println(this.time1);
//            System.out.println(System.currentTimeMillis() - this.time1);
            //Thread.sleep(1000);
            return value;
        }

        @Override
        public void close() throws Exception {
            System.out.println("here");
            super.close();
        }
    }

    public static class MyMet extends RichMapFunction<String, String> {
        private final transient int valueToExpose = 0;

        @Override
        public void open(Configuration config) {
            System.out.println(
                    getRuntimeContext().getMetricGroup().getIOMetricGroup());
        }

        @Override
        public String map(String value) throws Exception {
            return value;
        }
    }


}
