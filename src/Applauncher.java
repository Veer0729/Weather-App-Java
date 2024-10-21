import javax.swing.*; // Import the Swing library for building graphical user interfaces (GUIs)

// Main class to launch the weather application GUI
public class Applauncher {

    // Main method - the entry point of the application
    public static void main(String[] args) {
        // Use SwingUtilities to ensure that GUI updates happen on the Event Dispatch Thread (EDT).
        // This is important for thread safety when working with GUIs.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Display our weather app GUI by creating a new instance of the Main class.
                // The setVisible(true) method makes the GUI visible to the user.
                new Main().setVisible(true);

                // System.out.println(WeatherApp.getLocationData("tokyo"));
                // This would print the location data for "Tokyo" to the console.

                // System.out.println(WeatherApp.getCurrentTime());
                // This would print the current time formatted according to our API's requirements.
            }
        });
    }
}

