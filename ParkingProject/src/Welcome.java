import javax.swing.*;
import java.awt.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.sql.*;
public class Welcome extends JFrame{

//    Class Constructor
    public Welcome(){
        initComponents();   // initializes components
    }


    int capacity = 50;
    final String url = "jdbc:mysql://localhost:3307/parking";
    final String username = "root";
    final String password = "";
    // Component declarations
    JPanel mainPanel;
    JPanel inputPanel;
    JPanel statusPanel;
    JTextField searchBar;
    JButton searchAddButton;
    JButton getTotalCarsButton;
    JLabel mainLabel;
    JLabel spaceLabel;
    JLabel rateLabel;
    JLabel totalCarsLabel;
    private void initComponents(){
        mainPanel = new JPanel();
        inputPanel = new JPanel();
        statusPanel = new JPanel(new GridLayout(4,1));
        mainLabel = new JLabel("Enter Vehicle Number");
        searchBar = new JTextField(20);
        searchAddButton = new JButton("Search / Add");

        spaceLabel = new JLabel("Parking Spaces Left: 50");
        rateLabel = new JLabel("Hourly Rate: 20.00");
        totalCarsLabel = new JLabel("Total Cars Parked: N/A");
        getTotalCarsButton = new JButton("Get Total Cars");

        // Style Label
        Font boldFont = new Font(mainLabel.getFont().getFontName(), Font.BOLD, mainLabel.getFont().getSize());
        mainLabel.setFont(boldFont);

        // add action listener to button
        searchAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String carNumber = searchBar.getText().toLowerCase();
                    if(carNumber.isEmpty()){
                        JOptionPane.showMessageDialog(mainPanel, "Enter Vehicle Number!");
                    }else{
                        boolean carExists = searchCar(carNumber);

                        if(!carExists){
                            openAddCarFrame(carNumber);
                        }else {
                            openExitCarFrame(carNumber);
                        }
                    }


                searchBar.setText("");
            }
        });

        inputPanel.add(mainLabel, BorderLayout.NORTH);   // add main label to panel
        inputPanel.add(searchBar, BorderLayout.CENTER);   // add text box to panel
        inputPanel.add(searchAddButton, BorderLayout.SOUTH); // add button to panel


        getTotalCarsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalCars = getTotalCars();
                totalCarsLabel.setText("Total Cars Parked: " + totalCars);
                int spaceRemainig = capacity - totalCars;
                spaceLabel.setText(("Parking Spaces Left: " + spaceRemainig));
            }
        });


        statusPanel.add(spaceLabel, BorderLayout.NORTH);
        statusPanel.add(rateLabel, BorderLayout.CENTER);
        statusPanel.add(totalCarsLabel, BorderLayout.SOUTH);
        statusPanel.add(getTotalCarsButton, BorderLayout.EAST);

        Border blackline;
        blackline = BorderFactory.createLineBorder(Color.black);

        inputPanel.setBorder(blackline);
        statusPanel.setBorder(blackline);


        mainPanel.add(inputPanel);
        mainPanel.add(statusPanel);
        add(mainPanel); // add panel to frame

        // frame settings
        inputPanel.setPreferredSize(new Dimension(400, 200));
        statusPanel.setPreferredSize(new Dimension(200, 200));
        setSize(700,250);
        setVisible(true);
        setTitle("Parking Management");
    }

    private int getTotalCars() {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet =  null;
        int totalCars = 0;

        try {
            conn = DriverManager.getConnection(url, username, password);
            String sql = "SELECT COUNT(*) AS total_cars FROM cars";
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                totalCars = resultSet.getInt("total_cars");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,  e.getMessage());
            return 0;
        }

        return totalCars;
    }

    // Search for a car in the database
    private boolean searchCar(String carNumber) {
        // Replace with your MySQL database connection details
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            String sql = "SELECT * FROM cars WHERE car_number = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, carNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // Return true if car exists
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,  e.getMessage());
            return false;
        }
    }

    private void openAddCarFrame(String carNumber){
        Parking p = new Parking(carNumber);
        this.dispose();
    }

    private void openExitCarFrame(String carNumber){
        String arrivalTime = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            String sqlQuery = "SELECT time_in FROM cars WHERE car_number = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, carNumber);

            resultSet = preparedStatement.executeQuery();

            // Check if a result was found
            if (resultSet.next()) {
                // Retrieve the arrival time from the result set
                arrivalTime = resultSet.getString("time_in");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,  e.getMessage());
        }

        Exit e = new Exit(carNumber, arrivalTime);
        this.dispose();
    }

    public static void main(String[] args) {
        new Welcome();
    }
}
