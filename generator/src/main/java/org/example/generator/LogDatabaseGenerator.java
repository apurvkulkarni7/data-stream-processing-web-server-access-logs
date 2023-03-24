package org.example.generator;

import org.apache.commons.cli.CommandLine;
import org.example.generator.util.GenerateDummyIPData;
import org.example.generator.util.OptionsGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogDatabaseGenerator {

    public static JSONArray getPaths(){
        String[] path = {"/index.html", "/about.html", "/contact.html", "/services.html"};
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(List.of(path));
        return jsonArray;
    }
    public static JSONArray getQuery(){
        String[] query_string = {"", "?id=1", "?id=2", "?id=3"};
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(List.of(query_string));
        return jsonArray;
    }

    public static JSONArray getUserAgents(){
        // User Agent information
        List<String> userAgents = new ArrayList<>();
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0");
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.85 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.101 Safari/537.36 OPR/77.0.4054.172");
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Edge/91.0.864.37");
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 Vivaldi/4.1");
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36 Edg/92.0.902.73");
        userAgents.add("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36");
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(userAgents);
        return jsonArray;

    }

    public static JSONArray getRequestMethod() {
        String[] methods = {"GET", "POST", "PUT", "DELETE"};
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(List.of(methods));
        return jsonArray;
    }

    public static JSONArray getStatusCode() {
        Integer[] codes = {200, 201, 202, 203, 204, 205, 206, 300, 301, 302, 303, 304, 305, 306, 307, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505};
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(List.of(codes));
        return jsonArray;
    }

    public static JSONArray getReferer() {
        String[] urls = {"http://www.google.com", "http://www.yahoo.com", "http://www.bing.com", "http://www.msn.com"};
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(List.of(urls));
        return jsonArray;
    }

    public static void main(String[] args) {

        CommandLine opt = new OptionsGenerator(args).build();
        String databaseFile = opt.getOptionValue("database-file","database.json");

        JSONObject jsonDatabase = new JSONObject();

        GenerateDummyIPData IpDataGenerator = new GenerateDummyIPData();
        jsonDatabase.put("ip_address", IpDataGenerator.generate(100)); // IP Address
                                                                    // Timestamp
        jsonDatabase.put("request_method",getRequestMethod());      // Request method
        jsonDatabase.put("path",getPaths());                        // Url
        jsonDatabase.put("query",getQuery());                       // Url
        jsonDatabase.put("status_code",getStatusCode());            // Status code
                                                                    // Response size
        jsonDatabase.put("referer",getReferer());                   // Referer
        jsonDatabase.put("user_agent",getUserAgents());             // User agent

        // Write the JSON object to a file
        try (FileWriter fileWriter = new FileWriter(databaseFile)) {
            fileWriter.write(jsonDatabase.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
