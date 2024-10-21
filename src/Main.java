import org.json.simple.JSONObject; // Import JSON library for handling weather data.
import javax.imageio.ImageIO; // Import for image reading and writing.
import javax.swing.*; // Import for GUI components like JFrame, JButton, JLabel, etc.
import java.awt.*; // Import for layout, fonts, and other GUI elements.
import java.awt.event.ActionEvent; // Import for handling button click events.
import java.awt.event.ActionListener; // Import for listening to button click events.
import java.awt.image.BufferedImage; // Import for handling images in Java.
import java.io.File; // Import for file handling.
import java.io.IOException; // Import for handling input/output exceptions.

public class Main extends JFrame { // Main class that extends JFrame to create a window
    private JSONObject weatherData; // Variable to hold weather data as a JSON object

    public Main(){
        // Constructor - runs when an object of this class is created

        // Setup our GUI and set the title of the window
        super("Weather app");


        // Configure the program to close when the window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the size of the window (width x height in pixels)
        setSize(450, 650);

        // Position the window in the center of the screen
        setLocationRelativeTo(null);

        // Set layout manager to null so we can position components manually
        setLayout(null);

        // Prevent the window from being resized by the user
        setResizable(false);

        getContentPane().setBackground(Color.LIGHT_GRAY);

        addGUIcomponents(); // Call a function to add components to our GUI
    }

    private void addGUIcomponents() {
        // Function to add all the components (buttons, text fields, images, etc.) to the GUI

        // Create a text field for the user to enter a city name
        JTextField searchTextField = new JTextField();

        // Set the position (x, y) and size (width, height) of the text field
        searchTextField.setBounds(15, 15, 351, 45);

        // Change the font style and size of the text inside the text field
        searchTextField.setFont(new Font("Monospaced", Font.PLAIN, 24));
        add(searchTextField); // Add the text field to the window

        // Create a label for displaying the weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(27, 190, 400, 217); // Set its position and size
        add(weatherConditionImage); // Add the label to the window

        // Create a label for showing the temperature value
        JLabel temperaturetext = new JLabel("10°"); // Default text
        temperaturetext.setBounds(0, 120, 450, 60);
        temperaturetext.setFont(new Font("Monospaced", Font.BOLD, 50)); // Set font style and size

        // Center the temperature text horizontally
        temperaturetext.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperaturetext);


        // Create a label for the weather condition (e.g., "Cloudy")
        JLabel weatherConditiondec = new JLabel("Cloudy");
        weatherConditiondec.setBounds(0, 415,450,36); // Set position and size
        weatherConditiondec.setFont(new Font("Monospaced", Font.PLAIN, 28)); // Set font style and size
        weatherConditiondec.setHorizontalAlignment(SwingConstants.CENTER); // Center the text horizontally
        add(weatherConditiondec);

        // Create a label for the humidity image
        JLabel humidityimage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityimage.setBounds(0,500,74,66); // Set its position and size
        add(humidityimage);

        // Create a label for displaying humidity text using HTML for formatting
        JLabel humidityText = new JLabel("<html><center><b>Humidity</b>  </center></html>");
        humidityText.setBounds(90, 500, 100, 55); // Set its position and size
        humidityText.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Set font style and size
        add(humidityText);

        // Create a label for the windspeed image
        JLabel windspeedimage = new JLabel(loadImage("src/assets/windspeed.png"));
        windspeedimage.setBounds(220, 500, 74, 66); // Set its position and size
        add(windspeedimage);

        // Create a label for displaying wind speed text using HTML for formatting
        JLabel windspeedtext = new JLabel("<html><center><b>Windspeed</b>  </center></html>");
        windspeedtext.setBounds(310, 500, 100, 55); // Set its position and size
        windspeedtext.setFont(new Font("Monospaced", Font.PLAIN, 16)); // Set font style and size
        add(windspeedtext);

        // Create a button for the search functionality with an image icon
        JButton searchButton = new JButton(loadImage( "src/assets/search.png"));

        // Change the cursor to a hand when hovering over the button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45); // Set position and size of the button

        // Add an action listener to handle click events on the button
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the user input (location) from the text field
                String userInput = searchTextField.getText();

                // Validate input - check if the text is not empty
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return; // Exit if input is empty or only whitespace
                }

                // Retrieve weather data for the entered location
                weatherData = WeatherApp.getWeatherData(userInput);

                // Update the GUI components with new data

                // Get the weather condition from the data
                String weatherCondition = (String) weatherData.get("weather_condition");

                // Change the weather image based on the condition
                switch(weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case "Cloudy":
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case "Rainy":
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;

                }

                // Update the temperature display with the new value
                double temperature = (double) weatherData.get("temperature");
                temperaturetext.setText(temperature + "°C");

                // Update the weather condition text
                weatherConditiondec.setText(weatherCondition);

                // Update the humidity display
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><div style='text-align:center;'><b>Humidity</b><br>" + humidity + "%</div></html>");
                humidityText.setVerticalAlignment(SwingConstants.CENTER);

                // Update the windspeed display
                double windspeed = (double) weatherData.get("windspeed");
                windspeedtext.setText("<html><div style='text-align:center;'><b>Windspeed</b><br>" + windspeed + " km/h</div></html>");
                windspeedtext.setVerticalAlignment(SwingConstants.CENTER);
            }
        });
        add(searchButton); // Add the search button to the window
    }

    // Method to load an image from a file path and return it as an ImageIcon
    private ImageIcon loadImage(String resourcePath){
        try{
            // Read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // Return an ImageIcon so the component can display it
            return new ImageIcon(image);
        }catch(IOException e){
            e.printStackTrace(); // Print an error message if the image fails to load
        }

        // If the image cannot be found, print a message and return null
        System.out.println("Could not find resource");
        return null;
    }
}
