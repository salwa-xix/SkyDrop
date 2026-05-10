package skydrop.app;

import java.util.Random;

// Temporary weather simulation until real API integration
public class WeatherController {

    private Random random;

    public WeatherController() {

        // Used to simulate changing weather conditions
        random = new Random();
    }

    // Check if the weather allows drone delivery
    public boolean isWeatherSuitable(String district) {

        String condition = getWeatherCondition(district);

        return !condition.equalsIgnoreCase("Storm")
                && !condition.equalsIgnoreCase("Heavy Wind")
                && !condition.equalsIgnoreCase("Rain");
    }

    // Simulate weather conditions for demo purposes
    public String getWeatherCondition(String district) {

        String[] conditions = {
                "Clear",
                "Clear",
                "Clear",
                "Clear",
                "Clear"
        };

        int index = random.nextInt(conditions.length);

        return conditions[index];
    }
}