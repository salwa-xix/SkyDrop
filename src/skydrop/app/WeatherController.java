package skydrop.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherController {

    // Check if the weather is suitable for drone delivery
    public boolean isWeatherSuitable(String district) {

        try {

            String condition = getWeatherForDistrict(district).toLowerCase();

            System.out.println( district + " weather -> Condition: " + condition);

            boolean badWeather =
                    condition.contains("rain")
                            || condition.contains("storm")
                            || condition.contains("thunder")
                            || condition.contains("snow")
                            || condition.contains("fog")
                            || condition.contains("mist")
                            || condition.contains("hail")
                            || condition.contains("cloud")
                            || condition.contains("bad weather");

            return !badWeather;

        } catch (Exception e) {

            System.out.println("Weather API error: " + e.getMessage());

            return true;
        }
    }

    // Load weather data for the selected district
    private String getWeatherForDistrict(String district) throws Exception {

        // Al Naeem always returns bad weather for demo testing
        if (district.equals("Al Naeem")) {
            return "Rain";
        }

        // Other districts use live Jeddah weather
        String urlString = "https://wttr.in/Jeddah?format=j1";

        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "SkyDrop");

        try (BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream())))
        {

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String json = response.toString();

            return extractString(
                    json,
                    "\"weatherDesc\":[{\"value\":\"",
                    "\""
            );
        }
    }

    // Extract a text value from the JSON response
    private String extractString(String json,
                                 String startKey,
                                 String endKey) {

        int start = json.indexOf(startKey);

        if (start == -1) {
            return "Clear";
        }

        start += startKey.length();

        int end = json.indexOf(endKey, start);

        if (end == -1) {
            return "Clear";
        }

        return json.substring(start, end);
    }
}