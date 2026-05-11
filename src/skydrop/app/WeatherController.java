package skydrop.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Handle weather checks using the wttr.in API
public class WeatherController {

    // Check if the weather is suitable for drone delivery
    public boolean isWeatherSuitable(String district) {

        try {

            WeatherData weather = getWeatherForDistrict(district);

            System.out.println(
                    district + " weather -> "
                            + weather.city
                            + " | Condition: " + weather.condition
            );

            String condition = weather.condition.toLowerCase();

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

    // Load live weather data using the district mapping
    private WeatherData getWeatherForDistrict(String district) throws Exception {

        /*
         * Demo case:
         * Al Naeem always returns bad weather
         * so the drone delivery will be rejected.
         */
        if (district.equals("Al Naeem")) {
            return new WeatherData("Jeddah", "Rain");
        }

        /*
         * Al Rawdah and Al Hamra use real Jeddah weather.
         */
        String city = getCityForDistrict(district);

        String urlString = "https://wttr.in/"
                + city.replace(" ", "%20")
                + "?format=j1";

        URL url = new URL(urlString);

        HttpURLConnection connection =
                (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "SkyDrop");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        )) {

            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {

                response.append(line);
            }

            String json = response.toString();

            String condition = extractString(
                    json,
                    "\"weatherDesc\":[{\"value\":\"",
                    "\""
            );

            return new WeatherData(city, condition);
        }
    }

    // Map each district to Jeddah
    private String getCityForDistrict(String district) {

        return switch (district) {

            case "Al Rawdah" -> "Jeddah";

            case "Al Hamra" -> "Jeddah";

            case "Al Naeem" -> "Jeddah";

            default -> "Jeddah";
        };
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

    // Store weather values returned from the API
    private static class WeatherData {

        String city;
        String condition;

        public WeatherData(String city,
                           String condition) {

            this.city = city;
            this.condition = condition;
        }
    }
}