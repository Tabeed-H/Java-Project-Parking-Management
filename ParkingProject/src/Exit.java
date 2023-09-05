
import javax.swing.*;
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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class Exit {
    JFrame frame;
    JTextField vehicleNumberTextField;
    JTextField arrivalTimeTextField;
    JTextField exitTimeTextField;
    JButton setExitTimeButton;
    JButton calculatePriceButton;
    JButton generateBillButton;
    JButton returnToMainButton;
    final String Url = "jdbc:mysql://localhost:3307/parking";
    final String Username = "root";
    final String Password = "";
    double totalPrice = 0;

    public Exit(String vehicleNumber, String arrivalTime) {
        frame = new JFrame("Vehicle Exit");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 250);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2)); // 5 rows, 2 columns

        JLabel vehicleNumberLabel = new JLabel("Vehicle Number:");
        vehicleNumberTextField = new JTextField(vehicleNumber);
        vehicleNumberTextField.setEditable(false);

        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        arrivalTimeTextField = new JTextField(arrivalTime);
        arrivalTimeTextField.setEditable(false);

        JLabel exitTimeLabel = new JLabel("Enter Time of Exit:");
        exitTimeTextField = new JTextField(15);
        setExitTimeButton = new JButton("Set Current Time");
        setExitTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setExitTime();
            }
        });

        calculatePriceButton = new JButton("Calculate Price");
        calculatePriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePrice();
            }
        });

        generateBillButton = new JButton("Generate Bill / Exit");
        generateBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateBill();
            }
        });

        returnToMainButton = new JButton("Return to Main");
        returnToMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Welcome w = new Welcome();
                frame.dispose(); // Close the "Vehicle Exit" window
            }
        });

        panel.add(vehicleNumberLabel);
        panel.add(vehicleNumberTextField);
        panel.add(arrivalTimeLabel);
        panel.add(arrivalTimeTextField);
        panel.add(exitTimeLabel);
        panel.add(exitTimeTextField);
        panel.add(setExitTimeButton);
        panel.add(calculatePriceButton);
        panel.add(generateBillButton);
        panel.add(returnToMainButton);

        frame.add(panel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Set the exit time to the current timestamp
    private void setExitTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String currentTime = dateFormat.format(now);
        exitTimeTextField.setText(currentTime);
    }

    // Calculate the price (stubbed)
    private void calculatePrice() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Parse the arrival time and exit time from text fields
            Date arrivalTime = dateFormat.parse(arrivalTimeTextField.getText());
            Date exitTime = dateFormat.parse(exitTimeTextField.getText());

            // Calculate the time difference in milliseconds
            long timeDifferenceMillis = exitTime.getTime() - arrivalTime.getTime();

            // Calculate the time difference in hours
            double hours = timeDifferenceMillis / (1000.0 * 60 * 60);

            // Calculate the price based on the hourly rate
            double hourlyRate = 20.0; // 20 rupees per hour
            totalPrice = hourlyRate * hours;

            // Display the calculated price
            JOptionPane.showMessageDialog(frame, "Total Price: " + totalPrice + " rupees");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error calculating price: " + e.getMessage());
        }
    }

    // Generate a bill (stubbed)
    private void generateBill() {
        try {
            deleteRecordByVehicleNumber(vehicleNumberTextField.getText());
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

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating bill: " + e.getMessage());
        }

    }

    public  void deleteRecordByVehicleNumber(String vehicleNumber) {
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

        public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Example of how to open the "Vehicle Exit" frame
                new Exit("ABC123", "2023-09-05 12:00:00");
            }
        });
    }
}