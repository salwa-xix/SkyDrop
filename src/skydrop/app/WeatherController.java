package skydrop.app;

    //later for API and Network

    public class WeatherController {

        public boolean isWeatherSuitable(String district) {
            String condition = getWeatherCondition(district);
            return !condition.equalsIgnoreCase("Storm")
                    && !condition.equalsIgnoreCase("Heavy Wind")
                    && !condition.equalsIgnoreCase("Rain");
        }

        public String getWeatherCondition(String district) {
            // Placeholder
            //later for API and Network
            return "Clear";
        }
    }

