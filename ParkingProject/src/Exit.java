// required import statements
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

// import for "PDFBOX" used for Generating Token PDF
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class Exit {
    // Component declarations
    JFrame frame;
    JTextField vehicleNumberTextField;
    JTextField arrivalTimeTextField;
    JTextField exitTimeTextField;
    JButton setExitTimeButton;
    JButton calculatePriceButton;
    JButton generateBillButton;
    JButton returnToMainButton;
    // data base parameters
    final String Url = "jdbc:mysql://localhost:3307/parking";
    final String Username = "root";
    final String Password = "";
    double totalPrice = 0;

    //  Constructor
    public Exit(String carNumber, String arrivalTime) {
        initComponents(carNumber, arrivalTime);
    }

    private  void initComponents(String carNumber, String arrivalTime){
        frame = new JFrame("Vehicle Exit");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 250); // Fixed size

        JPanel mainPanel = new JPanel(new BorderLayout());  // Creates Main Panel
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new GridBagLayout()); // Creates Left Panel
        leftPanel.setBorder(createStyledBorder());  //Sets Border for Left Panel

        JPanel rightPanel = new JPanel();    // Creates Right Panel
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));  // Makes the child components i.e the buttons to fall on Y-axis
        rightPanel.setBorder(createStyledBorder());

        //  Sets Constraints for GridBag Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Left Panel Component "Text Label"
        JLabel vehicleNumberLabel = new JLabel("Vehicle Number:");
        vehicleNumberTextField = new JTextField(carNumber);
        vehicleNumberTextField.setEditable(false);  // makes it uneditable as the component receives the value from the main window

        // Left Panel Component "Text Label"
        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        arrivalTimeTextField = new JTextField(arrivalTime);
        arrivalTimeTextField.setEditable(false);    // makes it uneditable as the component receives the value from the main window

        // Left Panel Component "Text Label"
        JLabel exitTimeLabel = new JLabel("Enter Time of Exit:");
        exitTimeTextField = new JTextField(15);

        // Left Panel Component "Button"
        setExitTimeButton = new JButton("Set Current Time");
        setExitTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setExitTime();
            }
        });


        // Adding Components to Left Panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        leftPanel.add(vehicleNumberLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(vehicleNumberTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        leftPanel.add(arrivalTimeLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(arrivalTimeTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        leftPanel.add(exitTimeLabel, gbc);
        gbc.gridx = 1;
        leftPanel.add(exitTimeTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        leftPanel.add(setExitTimeButton, gbc);

        // Default Values of width and Height of Buttons
        int buttonWidth = 150;
        int buttonHeight = 40;

        // Right Panel Component "Button"
        calculatePriceButton = new JButton("Calculate Price");
        calculatePriceButton.setBackground(Color.ORANGE);    // Set Button Color
        calculatePriceButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align button
        calculatePriceButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set width and height
        calculatePriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePrice();
            }
        });

        // Right Panel Component "Button"
        generateBillButton = new JButton("Generate Bill / Exit");
        generateBillButton.setBackground(Color.GREEN);     // Set Button Color
        generateBillButton.setAlignmentX(Component.CENTER_ALIGNMENT);    // Align button
        generateBillButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set width and height
        generateBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBill();
            }
        });

        // Right Panel Component "Button"
        returnToMainButton = new JButton("Return to Main");
        returnToMainButton.setBackground(Color.RED);   // Set Button Color
        returnToMainButton.setAlignmentX(Component.CENTER_ALIGNMENT);    // Align button
        returnToMainButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set width and height
        returnToMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Welcome w = new Welcome();   // open Welcome Window
                frame.dispose(); // Close the "Vehicle Exit" window
            }
        });

        // Add components to right panel
        rightPanel.add(Box.createVerticalGlue()); // Top spacing
        rightPanel.add(calculatePriceButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        rightPanel.add(generateBillButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        rightPanel.add(returnToMainButton);
        rightPanel.add(Box.createVerticalGlue()); // Bottom spacing

        // Add Child Panels to Main Panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        // Add Main Panel to frame
        frame.add(mainPanel);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Function: setArrivalTime
     * Sets the value of "arrivalTimeTextField" to the current timeStamp
     */
    private void setExitTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // set timestamp format
        Date now = new Date();  // gets current date time
        String currentTime = dateFormat.format(now);     // formats according to the format specified in "dateFormat"
        exitTimeTextField.setText(currentTime);  // sets the value
    }

    /**
     * Function: calculatePrice
     * For the hourly rate of rs 20 this function calculates the ticket the customer has to pay at the end
     *
     * first the function gets the time from the text fields
     * calculate the time difference in milliseconds
     * converts milliseconds to hours
     * then calculates the hourly charges
     *
     */
    private void calculatePrice() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   // set timestamp format

            // get arrival time and exit time from text fields
            Date arrivalTime = dateFormat.parse(arrivalTimeTextField.getText());
            Date exitTime = dateFormat.parse(exitTimeTextField.getText());

            if(exitTimeTextField.getText().isEmpty()){
                JOptionPane.showMessageDialog(frame, "Enter Exit Time!");
            }else{
                // Calculate the time difference in milliseconds
                long timeDifferenceMillis = exitTime.getTime() - arrivalTime.getTime();
                // Calculate the time difference in hours
                double hours = timeDifferenceMillis / (1000.0 * 60 * 60);

                // Calculate the price based on the hourly rate
                double hourlyRate = 20.0; // 20 rupees per hour
                totalPrice = hourlyRate * hours;

                JOptionPane.showMessageDialog(frame, "Total Price: " + totalPrice + " rupees");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error calculating price: " + e.getMessage());
        }
    }

    /**
     * Function: generateBill
     * Generates a pdf file containing the details of the vehicle , arrival time, exit time and price to be paid
     * the pdf file is temporary and can be printed
     */
    private void generateBill() {
        try {
            if(totalPrice == 0) {
                JOptionPane.showMessageDialog(frame, "Calculate Price First");
            }else{
                deleteVehicle(vehicleNumberTextField.getText());
                // Create a new PDF document
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                // Create a content stream for adding content to the page
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Use built-in font constants for font selection
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);

                // Define positions for text elements
                float margin = 50; // Left margin
                float yPosition = page.getMediaBox().getHeight() - margin;
                float textWidth = page.getMediaBox().getWidth() - 2 * margin;

                // Add content to the PDF with proper formatting
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText("Vehicle Number: " + vehicleNumberTextField.getText());
                yPosition -= 20;
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Arrival Time: " + arrivalTimeTextField.getText());
                yPosition -= 20;
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Exit Time: " + exitTimeTextField.getText());
                yPosition -= 20;
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Total Price: " + totalPrice + " rupees");
                contentStream.endText();

                contentStream.close();

                // Save the PDF to a temporary file
                File tempFile = File.createTempFile("bill_", ".pdf");
                document.save(new FileOutputStream(tempFile));
                document.close();

                // Open the PDF in the default system viewer (typically a PDF reader)
                Desktop.getDesktop().open(tempFile);

                JOptionPane.showMessageDialog(frame, "Bill generated and opened for printing.");
            }


        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating bill: " + e.getMessage());
        }

    }

    /**
     * Function deleteRecord
     * @param vehicleNumber
     *
     * deletes the vehicle from the database
     */
    public  void deleteVehicle(String vehicleNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Establish a database connection
            connection = DriverManager.getConnection(Url, Username, Password);

            // Define the SQL query to delete the record
            String sqlQuery = "DELETE FROM cars WHERE car_number = ?";
            preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, vehicleNumber);

            // Execute the query to delete the record
            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the record was deleted successfully
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame,  e.getMessage());
        }
    }


    private Border createStyledBorder() {
        Color shadowColor = new Color(0, 0, 0, 50);
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createMatteBorder(5, 5, 5, 5, shadowColor)
        );
    }

    public static void main(String[] args) {
        new Exit("jk01AC2001", "2023-09-05 12:00:00");
    }
}