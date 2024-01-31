package org.example.generator.util;

import org.example.generator.RandomNumberGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class DataGenerator {
    private String databaseFile;
    public List<String> ipAddress;            //1
    public JSONArray requestMethod;              //5
    public JSONArray path;              //5
    public JSONArray query;              //5
    private String protocol;
    private JSONArray statusCode;
    private JSONArray referer;
    public JSONArray userAgent;            //9

    // Constructor
//    public DataGenerator() {
//
//    }

    /**
     * @param databaseFile
     * @throws Exception
     */
//    public void build(String databaseFile) {
//        this.databaseFile = databaseFile;
//
//        // Reading file from resources folder
//        Reader mySourceFile;
//        JSONObject myJsonData;
//        try {
//            this.databaseFile = "database.json";
//            String jsonStr = Files.readString(Paths.get(this.databaseFile));
//
//            // Parse json database
//            myJsonData = (JSONObject) new JSONParser().parse(jsonStr);
//
//        } catch (IOException | ParseException e) {
//            throw new RuntimeException(e);
//        }
//
//        JSONObject ipList = (JSONObject) myJsonData.get("ip_address");
//        this.ipAddress = new ArrayList<String>(ipList.keySet());
//        this.requestMethod = (JSONArray) myJsonData.get("request_method");
//        this.path = (JSONArray) myJsonData.get("path");
//        this.query = (JSONArray) myJsonData.get("query");
//        this.protocol = "HTTP/1.1";
//        this.statusCode = (JSONArray) myJsonData.get("status_code");
//        this.referer = (JSONArray) myJsonData.get("referer");
//        this.userAgent = (JSONArray) myJsonData.get("user_agent");
//    }

    /**
     * @return A full log entry with following format:
     * [website] [ip-address] [client-identity] [client-userid] [timestamp(dd/MMM/yyyy:HH:mm:ss Z)] [http-request] [http-response] [data-transferred] [request-origin] [user-agent]
     */
//    public String generate(){
//
//    }
//
//        String ipAddress = generateIPAddress();
//        String timestamp = generateTimestamp();
//        String method = generateMethod();
//        String resource = generateResource();
//        String protocol = generateProtocol();
//        String statusCode = generateStatusCode();
//        String contentSize = generateSize();
//        String referrer = generateReferrer();
//        String userAgent = generateUserAgent();
//
//        return String.format("%s - - [%s] \"%s %s %s\" %s %s \"%s\" \"%s\"",
//                ipAddress, timestamp, method, resource, protocol, statusCode, contentSize, referrer, userAgent);
//    }
//
//    private String generateIPAddress() {
//        Random r = new Random();
//        return String.valueOf(this.ipAddress.get(r.nextInt(this.ipAddress.size())));
//    }
//    private static String generateTimestamp() {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
//        Timestamp timestamp = new Timestamp(new Date().getTime());
//        return dateFormat.format(timestamp);
//    }
//    private String generateMethod() {
//        Random r = new Random();
//        return String.valueOf(this.requestMethod.get(r.nextInt(requestMethod.size())));
//    }
//    // path + query
//    private String generateResource() {
//        Random r = new Random();
//        String path = (String) this.path.get(r.nextInt(this.path.size()));
//        String query = (String) this.query.get(r.nextInt(this.query.size()));
//        return path + query;
//    }
//    private String generateProtocol() {
//        return this.protocol;
//    }
//    private String generateStatusCode() {
//        Random r = new Random();
//        return String.valueOf(this.statusCode.get(r.nextInt(this.statusCode.size())));
//    }
//    private String generateSize() {
//        Random r = new Random();
//        return String.valueOf(r.nextInt(10000));
//    }
//    private String generateReferrer() {
//        Random r = new Random();
//        return String.valueOf(this.referer.get(r.nextInt(this.referer.size())));
//    }
//    private String generateUserAgent() {
//        Random r = new Random();
//        return String.valueOf(this.userAgent.get(r.nextInt(this.userAgent.size())));
//    }
//
//    /**
//     * Replaces null values with '-' string
//     * @param input
//     * @return String without null values
//     */
//    public static String nullReplacer(Object input) {
//        String result;
//        if (input == null) {
//            result = "-";
//        } else {
//            result = input.toString();
//        }
//        return result;
//    }
}