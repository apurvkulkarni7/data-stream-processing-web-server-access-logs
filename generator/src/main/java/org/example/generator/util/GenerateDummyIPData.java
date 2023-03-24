package org.example.generator.util;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Random;

public class GenerateDummyIPData {

    public static JSONObject generate(long numIPAddress) {

        File database = null;
        DatabaseReader dbReader = null;
        try {
            // Path to MaxMind GeoIP2 database
            database = getMyResource("ip_database.mmdb", GenerateDummyIPData.class);

            // Create DatabaseReader object to read from the database
            dbReader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // List to store generated IP address data
        JSONObject ipDataList = new JSONObject();

        // Random object to generate IP addresses
        Random random = new Random();
        float us_country_counter = 0;
        // Generate dummy IP addresses
        for (int i = 0; i < numIPAddress; i++) {
            CityResponse response = null;
            String ipAddress = null;
            // Generate random IP address
            boolean loopTheLoop = true;

            while (loopTheLoop) {
                ipAddress = random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256) + "." + random.nextInt(256);

                // Look up IP address in the MaxMind database
                try {
                    response = dbReader.city(InetAddress.getByName(ipAddress));
                    if (!String.valueOf(response.getCity().getName()).equals("null")) {
                        if (response.getCountry().getName().equals("United States")) {
                            float curr_ratio = (us_country_counter /(float) numIPAddress) *(float) 100;
                            if (curr_ratio < 10){
                                us_country_counter++;
                                loopTheLoop = false;
                            }
                        } else {
                            loopTheLoop = false;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (GeoIp2Exception e) {

                }
            }

            // Get country information
            Country country = response.getCountry();
            String countryName = country.getName();

            // Get city information
            City city = response.getCity();
            String cityName = city.getName();

            // Get location information
            Location location = response.getLocation();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Create JSON object to store IP address data
            JSONObject ipData = new JSONObject();

            ipData.put("country", countryName);
            ipData.put("city", cityName);
            ipData.put("latitude", latitude);
            ipData.put("longitude", longitude);

            // Add IP address data to the list
            ipDataList.put(ipAddress, ipData);
        }

        return ipDataList;
    }

    public static File getMyResource(String resource, Class T) throws IOException {
        ClassLoader classLoader = T.getClassLoader();
        InputStream is = classLoader.getResourceAsStream(resource);
        File myFile = stream2file(is);
        return myFile;
    }

    // This is used to convert "stream input file" that is read from resources location (ip_address.mmdb) to "File"
    // The database is archived in the jar file itself. No need to provide them separately.
    public static File stream2file(InputStream in) throws IOException {
        final File tempFile = File.createTempFile("stream2file", ".tmp");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            IOUtils.copy(in, out);
        }
        return tempFile;
    }

}