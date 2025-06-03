package com.example.demo.address.location;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeocodingService {

    private double[] tryGeocode(String addressName) {
        try {
            String encodedAddress = URLEncoder.encode(addressName, StandardCharsets.UTF_8);
            String urlStr = "https://nominatim.openstreetmap.org/search?q=" + encodedAddress + "&format=json&limit=1";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "SpringBootApp");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String response = reader.lines().collect(Collectors.joining());
                JSONArray jsonArray = new JSONArray(response);

                if (jsonArray.length() == 0) {
                    return null;
                }

                JSONObject location = jsonArray.getJSONObject(0);
                double lat = location.getDouble("lat");
                double lon = location.getDouble("lon");

                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            System.out.println("Geocoding error: " + e.getMessage());
            return null;
        }
    }

    public double[] getCoordinatesFromAddress(String region, String city, String branchName) {
        String fullAddress = branchName + ", " + city + ", " + region;
        double[] result = tryGeocode(fullAddress);
        if (result != null) return result;

        String fallbackAddress = city + ", " + region;
        result = tryGeocode(fallbackAddress);
        if (result != null) return result;

        return new double[]{41.3111, 69.2797};
    }

    public double[] getCoordinatesFromAddress(String addressOrLink) {
        if (addressOrLink.contains("maps.google.com") || addressOrLink.contains("google.com/maps")) {
            return extractCoordinatesFromLink(addressOrLink);
        }

        double[] result = tryGeocode(addressOrLink);
        if (result != null) return result;

        return new double[]{41.3111, 69.2797};
    }


    private double[] extractCoordinatesFromLink(String url) {
        try {
            // @41.5402735,60.4086114,203m formatidagi linkdan koordinatalar olish
            Pattern patternAt = Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)");
            Matcher matcherAt = patternAt.matcher(url);

            if (matcherAt.find()) {
                double lat = Double.parseDouble(matcherAt.group(1));
                double lon = Double.parseDouble(matcherAt.group(2));
                return new double[]{lat, lon};
            }

            // Agar boshqa formatda bo‘lsa, boshqa parsing qo‘llash
            Pattern patternPlace = Pattern.compile("/place/(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)");
            Matcher matcherPlace = patternPlace.matcher(url);

            if (matcherPlace.find()) {
                double lat = Double.parseDouble(matcherPlace.group(1));
                double lon = Double.parseDouble(matcherPlace.group(2));
                return new double[]{lat, lon};
            }

            // Agar `query=41.5402735,60.4086114` bo‘lsa
            Pattern patternQuery = Pattern.compile("query=(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)");
            Matcher matcherQuery = patternQuery.matcher(url);

            if (matcherQuery.find()) {
                double lat = Double.parseDouble(matcherQuery.group(1));
                double lon = Double.parseDouble(matcherQuery.group(2));
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            throw new RuntimeException("Koordinatalarni linkdan ajratib bo‘lmadi: " + e.getMessage());
        }

        throw new RuntimeException("Linkda koordinatalar topilmadi.");
    }


}
