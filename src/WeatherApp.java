import org.json.simple.JSONArray; // For handling JSON arrays
import org.json.simple.JSONObject; // For handling JSON objects
import org.json.simple.parser.JSONParser; // For parsing JSON strings
import java.io.IOException; // For handling input/output exceptions
import java.net.HttpURLConnection; // For making HTTP requests
import java.net.URL; // For representing URLs
import java.time.LocalDateTime; // For accessing the current date and time
import java.time.format.DateTimeFormatter; // For formatting the date and time
import java.util.Scanner; // For reading input from a source (e.g., API response)

// Class to handle fetching weather data from APIs
public class WeatherApp {

    // Method to fetch weather data based on the provided location name
    public static JSONObject getWeatherData(String locationName) {
        // Get the geographic coordinates (latitude and longitude) for the given location name
        JSONArray locationData = getLocationData(locationName);

        // Extract the first location's data (latitude and longitude)
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = ((Number) location.get("latitude")).doubleValue(); // Get latitude as double
        double longitude = ((Number) location.get("longitude")).doubleValue(); // Get longitude as double

        // Build the URL for the weather API using the coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Asia%2FSingapore";

        try {
            // Make an API call to fetch the weather data
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check if the response code is 200 (HTTP OK), indicating a successful connection
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null; // Return null if there's an error
            }

            // Read the response data from the API and store it in a StringBuilder
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                resultJson.append(scanner.nextLine()); // Append each line of the response to the builder
            }

            // Close the scanner to release resources
            scanner.close();
            // Disconnect the HTTP connection
            conn.disconnect();

            // Parse the JSON response data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // Retrieve the "hourly" data object from the parsed JSON
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // Get the index of the current hour in the hourly data array
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            // Extract various weather parameters for the current time using the index

            // Get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = ((Number) temperatureData.get(index)).doubleValue(); // Convert to double

            // Get weather condition code
            JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index)); // Convert code to readable text

            // Get humidity
            JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relativeHumidity.get(index); // Convert to long

            // Get wind speed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index); // Convert to double

            // Build a JSON object to hold the extracted weather data for easy access
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            // Return the JSON object containing the weather data
            return weatherData;
        } catch (Exception e) {
            e.printStackTrace(); // Print error details if something goes wrong
        }

        // Return null if the weather data couldn't be fetched
        return null;
    }

    // Method to fetch geographic coordinates based on the location name
    public static JSONArray getLocationData(String locationName) {
        // Replace spaces in the location name with '+' to match the API's format
        locationName = locationName.replaceAll(" ", "+");

        // Build the URL for the geolocation API using the formatted location name
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName + "&count=10&language=en&format=json";

        try {
            // Make an API call to fetch the location data
            HttpURLConnection conn = fetchApiResponse(urlString);

            // Check if the response code is 200 (HTTP OK)
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null; // Return null if there's an error
            } else {
                // Read the response data and store it in a StringBuilder
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine()); // Append each line of the response to the builder
                }

                // Close the scanner to release resources
                scanner.close();
                // Disconnect the HTTP connection
                conn.disconnect();

                // Parse the JSON response into a JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // Extract and return the array of location results
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print error details if something goes wrong
        }

        // Return null if the location data couldn't be fetched
        return null;
    }

    // Helper method to fetch API response based on the given URL string
    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Create a URL object from the URL string
            URL url = new URL(urlString);
            // Open an HTTP connection to the URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set the request method to GET
            conn.setRequestMethod("GET");

            // Connect to the API
            conn.connect();
            return conn; // Return the connection object
        } catch (IOException e) {
            e.printStackTrace(); // Print error details if something goes wrong
        }

        // Return null if the connection couldn't be established
        return null;
    }

    // Method to find the index of the current time in the time array
    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime(); // Get the current time formatted as a string

        // Iterate through the time list to find the index matching the current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                return i; // Return the index if found
            }
        }

        // Return 0 if no matching time is found (fallback)
        return 0;
    }

    // Method to get the current time formatted as per the API's requirements
    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now(); // Get the current date and time

        // Define the date and time format (e.g., "yyyy-MM-dd'T'HH':00'")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // Format the current date and time as a string and return it
        String formattedDateTime = currentDateTime.format(formatter);
        return formattedDateTime;
    }

    // Method to convert weather code to a readable weather condition string
    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = ""; // Initialize an empty string for the weather condition
        if (weatherCode == 0L) {
            weatherCondition = "Clear"; // Code 0 represents clear weather
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            weatherCondition = "Cloudy"; // Codes 1 to 3 represent cloudy weather
        } else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)) {
            weatherCondition = "Rain"; // Codes 51-67 and 80-99 represent rainy weather
        } else if (weatherCode >= 71L && weatherCode <= 77L) {
            weatherCondition = "Snow"; // Codes 71-77 represent snowy weather
        }
        return weatherCondition; // Return the readable weather condition
    }
}
