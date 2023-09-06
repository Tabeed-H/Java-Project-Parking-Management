import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Welcome extends JFrame {
    private int capacity = 50;      // total number of parking spots available

    // data base parameters
    private final String url = "jdbc:mysql://localhost:3307/parking";
    private final String username = "root";
    private final String password = "";

    // Component declarations
    private JPanel mainPanel;
    private JPanel inputPanel;
    private JPanel statusPanel;
    private JTextField searchBar;
    private JButton searchAddButton;
    private JButton getTotalCarsButton;
    private JLabel mainLabel;
    private JLabel spaceLabel;
    private JLabel rateLabel;
    private JLabel totalCarsLabel;

    //  Constructor
    public Welcome() {
        initComponents();   // initializes components
    }

    /**
     * Function: initComponents
     * initializes all the GUI components of the frame
     */
    private void initComponents() {
        mainPanel = new JPanel(new GridBagLayout());        // creates main panel

        //  Sets Constraints for GridBag Layout
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10); // Add padding

        // Adds styling to child panels
        inputPanel = createStyledPanel();
        statusPanel = createStyledPanel();

        //  Components of Input Panel
        mainLabel = new JLabel("Enter Vehicle Number");     // Label
        searchBar = new JTextField(20);     // inputField for searching
        searchAddButton = new JButton("Search / Add");      // Action button for the search box

        //  Components of Status Panel
        spaceLabel = new JLabel("Parking Spaces Left: 50"); // Displays parking spaces left
        rateLabel = new JLabel("Hourly Rate: 20.00");   // Displays hourly Rates
        totalCarsLabel = new JLabel("Total Cars Parked: N/A");  // Displays number of currently parked cars
        getTotalCarsButton = new JButton("Get Total Cars");     // Action button to get the number of cars currently parked

        // Add action listener to Search button
        /**
         * Anynomous function that gets the "Vehicle Number" from the TextBox
         * First checks if the "Vehicle Number is Empty or not
         * if it's empty pop-ups a window letting the user know
         *
         * for "Vehicle Number" checks if it's already present the parking
         * if Vehicle is not in the database i.e a new car
         * Add Vehicle Window is started
         * if the Vehicle is in the database i.e already parked
         * Ecit Vehicle Window is started
         */
        searchAddButton.addActionListener(e -> {
            String carNumber = searchBar.getText().toLowerCase();
            if (carNumber.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Enter Vehicle Number!");
            } else {
                boolean carExists = searchCar(carNumber);
                if (!carExists) {
                    openAddCarFrame(carNumber);
                } else {
                    openExitCarFrame(carNumber);
                }
            }
            searchBar.setText("");
        });

        // Added components to Input Panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(mainLabel, constraints);

        constraints.gridy = 1;
        inputPanel.add(searchBar, constraints);

        constraints.gridy = 2;
        inputPanel.add(searchAddButton, constraints);

        // Add Action listener to Status Button
        /**
         * Contains a function "getTotalCars"
         * "getTotalCard" returns the total number of cars present in the database
         * The returned value is then subtracted from the totalCapacity of the parking space to get the current available space
         */
        getTotalCarsButton.addActionListener(e -> {
            int totalCars = getTotalCars();
            totalCarsLabel.setText("Total Cars Parked: " + totalCars);
            int spaceRemaining = capacity - totalCars;
            spaceLabel.setText("Parking Spaces Left: " + spaceRemaining);
        });

        // Add components to Status Panel
        constraints.gridx = 0;
        constraints.gridy = 0;
        statusPanel.add(spaceLabel, constraints);

        constraints.gridy = 1;
        statusPanel.add(rateLabel, constraints);

        constraints.gridy = 2;
        statusPanel.add(totalCarsLabel, constraints);

        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.LINE_END;
        statusPanel.add(getTotalCarsButton, constraints);

        // Add child panels to main panel
        mainPanel.add(inputPanel);
        mainPanel.add(statusPanel);

        // Add the main panel to the frame
        add(mainPanel);

        // panel settings
        inputPanel.setPreferredSize(new Dimension(400, 200));
        statusPanel.setPreferredSize(new Dimension(200, 200));

        //  Frame Settings
        setSize(900, 350);
        setVisible(true);
        setTitle("Parking Management");
    }

    // Styling for child panels
    private JPanel createStyledPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int shadowWidth = 5;
                int shadowHeight = 5;
                Color shadowColor = new Color(0, 0, 0, 50);
                int x = 0;
                int y = 0;
                int width = getWidth() - shadowWidth;
                int height = getHeight() - shadowHeight;
                g2d.setColor(shadowColor);
                g2d.fillRect(x + width, y + shadowHeight, shadowWidth, height);
                g2d.fillRect(x + shadowWidth, y + height, width, shadowHeight);
            }
        };
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return panel;
    }

    /**
     * Function : getTotalCars
     * Gets the total number of cars in the database
     * i.e all the cars currently parked in the parking space
     * @return
     */
    private int getTotalCars() {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);     // Starts connection to the database
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT COUNT(*) AS total_cars FROM cars"); // mySQL query
            ResultSet resultSet = preparedStatement.executeQuery();

            // if result is found from successfully running the query
            if (resultSet.next()) {
                return resultSet.getInt("total_cars");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        return 0;
    }

    /**
     * Function: searchCar
     * @param carNumber
     *
     * Searches for a vehicle with "carNumber" in the database
     * @return
     */
    private boolean searchCar(String carNumber) {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM cars WHERE car_number = ?");
            preparedStatement.setString(1, carNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next(); // Return true if car exists
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        return false;
    }

    /**
     * Function: openAddCarFrame
     * @param carNumber
     *
     * Disposes current window and opens "Parking" window
     * "Parking" window enables the operator to onload a new vehicle
     */
    private void openAddCarFrame(String carNumber) {
        new Parking(carNumber);
        dispose();
    }

    /**
     * Function: openExitCarFrame
     * @param carNumber
     *
     * Disposes the Current window and opens "Exit" vehicle window
     * "Exit" enables the operator to offload a vehicle
     *
     * calling "Exit" requrires two parameters
     * the vehicle number as "car_number" in database
     * the vehicle in-timestampe as "time_in" in database
     *
     * carNumber is already known (from the search box)
     * the arrival time for the particular car is derived by searching for the car with CarNumber in the database
     * returing its arrivalTime (Stored as time_in)
     *
     *
     */
    private void openExitCarFrame(String carNumber) {
        String arrivalTime = null;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT time_in FROM cars WHERE car_number = ?")) {

            preparedStatement.setString(1, carNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    arrivalTime = resultSet.getString("time_in");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        new Exit(carNumber, arrivalTime);
        dispose();
    }

    public static void main(String[] args) {
        new Welcome();
    }
}