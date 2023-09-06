// required import statements
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;

// import for "PDFBOX" used for Generating Token PDF
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class Parking {
    // Component declarations
    JFrame frame;
    JTextField vehicleNumberTextField;
    JTextField arrivalTimeTextField;
    JButton setArrivalTimeButton;
    JButton addButton;
    JButton generatePDFButton;
    JButton returnToMainButton;

    // data base parameters
    final String Url = "jdbc:mysql://localhost:3307/parking";
    final String Username = "root";
    final String Password = "";

    //  Constructor
    public Parking(String carNumber) {
        initComponents(carNumber);  // initializes components
    }

    /**
     * Function: initComponents
     * @param carNumber
     * initializes all the GUI components of the frame
     */
    private void initComponents(String carNumber){
        frame = new JFrame("Add Car");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 300); // Fixed size

        JPanel mainPanel = new JPanel(new BorderLayout());      // Creates Main Panel
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel leftPanel = new JPanel(new GridBagLayout());     // Creates Left Panel
        leftPanel.setBorder(createStyledBorder());  //Sets Border for Left Panel

        JPanel rightPanel = new JPanel();       // Creates Right Panel
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));  // Makes the child components i.e the buttons to fall on Y-axis
        rightPanel.setBorder(createStyledBorder());

        //  Sets Constraints for GridBag Layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding

        // Left Panel Component "Text Label"
        JLabel vehicleNumberLabel = new JLabel("Enter Vehicle Number:");
        vehicleNumberTextField = new JTextField(15);
        vehicleNumberTextField.setText(carNumber);      // Default Value
        vehicleNumberTextField.setEditable(false);      // makes it uneditable as the component receives the value from the main window

        // Left Panel Component "Text Label"
        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        arrivalTimeTextField = new JTextField(15);

        // Left Panel Component "Button"
        setArrivalTimeButton = new JButton("Set Current Time");
        setArrivalTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setArrivalTime();
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
        gbc.gridwidth = 2;
        leftPanel.add(setArrivalTimeButton, gbc);

        // Default Values of width and Height of Buttons
        int buttonWidth = 150;
        int buttonHeight = 40;

        // Right Panel Component "Button"
        addButton = new JButton("Add Vehicle");
        addButton.setBackground(Color.GREEN);   // Set Button Color
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);    // Align button
        addButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set width and height
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicle();
            }
        });

        // Right Panel Component "Button"
        generatePDFButton = new JButton("Generate Token");
        generatePDFButton.setBackground(Color.ORANGE);      // Set Button Color
        generatePDFButton.setAlignmentX(Component.CENTER_ALIGNMENT);       // Align button
        generatePDFButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set width and height
        generatePDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePDF();
            }
        });

        // Right Panel Component "Button"
        returnToMainButton = new JButton("Return to Main");
        returnToMainButton.setBackground(Color.RED);    // Set Button Color
        returnToMainButton.setAlignmentX(Component.CENTER_ALIGNMENT);   // Align button
        returnToMainButton.setPreferredSize(new Dimension(buttonWidth, buttonHeight)); // Set width and height
        returnToMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Welcome w = new Welcome();      // open Welcome Window
                frame.dispose(); // Close the "Add Car" window
            }
        });

        // Add components to right panel
        rightPanel.add(Box.createVerticalGlue()); // Top spacing
        rightPanel.add(addButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing
        rightPanel.add(generatePDFButton);
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
    private void setArrivalTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // set timestamp format
        Date now = new Date();  // gets current date time
        String currentTime = dateFormat.format(now);    // formats according to the format specified in "dateFormat"
        arrivalTimeTextField.setText(currentTime);  // sets the value
    }

    /**
     * Function: addVehicle
     * Addes a new vehicle to the database
     * sets the vehicleNumber as "car_number" in database
     * sets the arrivalTime as "time_in" in database
     */
    private void addVehicle() {

        //  gets values from the textBoxes
        String vehicleNumber = vehicleNumberTextField.getText();
        String arrivalTime = arrivalTimeTextField.getText();
        if(arrivalTime.isEmpty()){
            JOptionPane.showMessageDialog(frame, "Enter Arrival Time First!");
        }else{
            try {
                Connection connection = DriverManager.getConnection(Url, Username, Password);   // connection to database
                String insertSql = "INSERT INTO cars (car_number, time_in) VALUES (?, ?)";  // mySQL query with placeholders
                PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
                preparedStatement.setString(1, vehicleNumber);  // set placeholder value at position   1
                preparedStatement.setTimestamp(2, Timestamp.valueOf(arrivalTime)); // set placeholder value at position 2
                preparedStatement.executeUpdate();  // execute query
                JOptionPane.showMessageDialog(frame, "Vehicle added successfully!");    // set pop-up
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(frame, "Error adding vehicle: " + e.getMessage());
            }
        }


    }

    /**
     * Function: generatedPDF
     * Generates a pdf file containing the details of the vehicle and arrival time
     * the pdf file is temporary and can be printed
     */
    private void generatePDF() {
        try {

            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);

            float margin = 50;
            float yPosition = page.getMediaBox().getHeight() - margin;
            float textWidth = page.getMediaBox().getWidth() - 2 * margin;

            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Vehicle Number: " + vehicleNumberTextField.getText());
            yPosition -= 20;
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Arrival Time: " + arrivalTimeTextField.getText());
            yPosition -= 20;

            contentStream.close();

            File tempFile = File.createTempFile("token", ".pdf");
            document.save(new FileOutputStream(tempFile));
            document.close();

            Desktop.getDesktop().open(tempFile);

            JOptionPane.showMessageDialog(frame, "Token generated and opened for printing.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating token: " + e.getMessage());
        }
    }

    private CompoundBorder createStyledBorder() {
        Color shadowColor = new Color(0, 0, 0, 50);
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createMatteBorder(5, 5, 5, 5, shadowColor)
        );
    }

    public static void main(String[] args) {
        new Parking("jk01AC2001");
    }
}
