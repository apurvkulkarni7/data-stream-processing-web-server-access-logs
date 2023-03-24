package org.example.streamprocessor.data;

import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEvent {
    public String visitedWebsite;
    public String protocol;
    public String clientIpAddress;
    public String clientIdentity;
    public String clientUserId;
    public Timestamp timestamp;
    public String requestType;
    public String requestedResource;
    public String httpProtocol;
    public String httpResponse;
    public long dataTransferred;
    public String httpReferrer;
    public String userAgent;
    public String clientCountry;
    public String clientLatitude;
    public String clientLongitude;
    public String key;
    public long count;
    public String input;

    public final String regex = new StringBuilder()
            //.append("([a-zA-Z.]+) ")
            .append("([0-9\\.]+) ")
            .append("(\\S+) ")
            .append("(\\S+) ")
            .append("\\[([\\w:/\\+\\-\\s\\d{4}]+)\\] ")
            .append("\"([A-Z]+) ")
            .append("(.*) ")
            .append("([A-Z]+)\\/([\\d.]+)\" ")
            .append("([0-9]+) ")
            .append("([0-9]+) ")
            .append("\"([^\"]*)\" ")
            .append("\"(.+?)\"")
            .toString();

    public LogEvent() {
    }

    public LogEvent(String input) {
        this.input = input;
    }

    public String getClientIpAddress() {
        return this.clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public String getClientIdentity() {
        return this.clientIdentity;
    }

    public void setClientIdentity(String clientIdentity) {
        this.clientIdentity = clientIdentity;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestType() {
        return this.requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestedResource() {
        return this.requestedResource;
    }

    public void setRequestedResource(String requestedResource) {
        this.requestedResource = requestedResource;
    }

    public String getHttpProtocol() {
        return this.httpProtocol;
    }

    public void setHttpProtocol(String httpProtocol) {
        this.httpProtocol = httpProtocol;
    }

    public String getHttpResponse() {
        return this.httpResponse;
    }

    public void setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
    }

    public long getDataTransferred() {
        return this.dataTransferred;
    }

    public void setDataTransferred(long dataTransferred) {
        this.dataTransferred = dataTransferred;
    }

    public String getHttpReferrer() {
        return this.httpReferrer;
    }

    public void setHttpReferrer(String httpReferrer) {
        this.httpReferrer = httpReferrer;
    }

    public String getClientCountry() {
        return this.clientCountry;
    }

    public void setClientCountry(String clientCountry) {
        this.clientCountry = clientCountry;
    }

    public String getClientLatitude() {
        return this.clientLatitude;
    }

    public void setClientLatitude(String clientLatitude) {
        this.clientLatitude = clientLatitude;
    }

    public String getClientLongitude() {
        return this.clientLongitude;
    }

    public void setClientLongitude(String clientLongitude) {
        this.clientLongitude = clientLongitude;
    }

    public String getUserAgent() {
        return this.userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getCount() {
        return this.count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = key;
    }

//    public void logParser(DatabaseReader geoDB) {
    public void logParser(JSONObject database) {

        Matcher m = Pattern.compile(this.regex).matcher(this.getInput());

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
             *  8 : HTTP Protokol number
             *  9 : HTTP response
             * 10 : Size of the returned object
             * 11 : Origin of the request (HTTP referrer)
             * 12 : User agent (Browser/Machine details)
             * */

            this.setClientIpAddress(m.group(1));
            this.setClientIdentity("-");
            try {
                this.setTimestamp(new Timestamp(new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z").parse(m.group(4)).getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            this.setRequestType(m.group(5));
            this.setRequestedResource(m.group(6));
            this.setHttpProtocol(m.group(7) + "/" + m.group(8));
            this.setHttpResponse(m.group(9));
            this.setDataTransferred(Long.valueOf(m.group(10)));
            this.setHttpReferrer(m.group(11));
            this.setUserAgent(m.group(12));

            // Extracting geolocation org.data based on IP address
            JSONObject ipInfo = (JSONObject) database.get(this.clientIpAddress);
            this.setClientCountry(String.valueOf(ipInfo.get("country")));
            this.setClientLatitude(String.valueOf(ipInfo.get("city")));
            this.setClientLongitude(String.valueOf(ipInfo.get("longitude")));
            this.setClientLatitude(String.valueOf(ipInfo.get("latitude")));
        }
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "key='" + key + '\'' +
                ", count='" + count + '\'' +
                ", visitedWebsite='" + visitedWebsite + '\'' +
                ", clientIpAddress='" + clientIpAddress + '\'' +
                ", clientIdentity='" + clientIdentity + '\'' +
                ", clientUserId='" + clientUserId + '\'' +
                ", timestamp=" + timestamp +
                ", requestType='" + requestType + '\'' +
                ", requestedResource='" + requestedResource + '\'' +
                ", httpProtocol='" + httpProtocol + '\'' +
                ", httpResponse='" + httpResponse + '\'' +
                ", dataTransferred='" + dataTransferred + '\'' +
                ", httpReferrer='" + httpReferrer + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", clientCountry='" + clientCountry + '\'' +
                ", clientLatitude='" + clientLatitude + '\'' +
                ", clientLongitude='" + clientLongitude + '\'' +
                ", input='" + input + '\'' +
                '}';
    }

}
