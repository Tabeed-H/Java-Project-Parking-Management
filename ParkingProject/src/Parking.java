//import required files
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

public class Parking {
    JFrame frame;
    JTextField vehicleNumberTextField;
    JTextField arrivalTimeTextField;
    JButton setArrivalTimeButton;
    JButton addButton;
    JButton generatePDFButton;
    JButton returnToMainButton;

    final String Url = "jdbc:mysql://localhost:3307/parking";
    final String Username = "root";
    final String Password = "";

    public Parking(String carNumber) {
        frame = new JFrame("Add Car");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(400, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2)); // 3 rows, 2 columns

        JLabel vehicleNumberLabel = new JLabel("Enter Vehicle Number:");
        vehicleNumberTextField = new JTextField(15);
        vehicleNumberTextField.setText(carNumber);

        JLabel arrivalTimeLabel = new JLabel("Arrival Time:");
        arrivalTimeTextField = new JTextField(15);

        setArrivalTimeButton = new JButton("Set Current Time");
        setArrivalTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setArrivalTime();
            }
        });

        addButton = new JButton("Add Vehicle");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addVehicleToDatabase();
            }
        });

        generatePDFButton = new JButton("Generate Token");
        generatePDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePDF();
            }
        });

        returnToMainButton = new JButton("Return to Main"); // New "Return to Main" button
        returnToMainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Welcome w = new Welcome();
                frame.dispose(); // Close the "Add Car" window
            }
        });

        panel.add(vehicleNumberLabel);
        panel.add(vehicleNumberTextField);
        panel.add(arrivalTimeLabel);
        panel.add(arrivalTimeTextField);
        panel.add(setArrivalTimeButton);
        panel.add(addButton);
        panel.add(returnToMainButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.add(generatePDFButton, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Set the arrival time to the current timestamp
    private void setArrivalTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String currentTime = dateFormat.format(now);
        arrivalTimeTextField.setText(currentTime);
    }

    // Add the vehicle to the database
    private void addVehicleToDatabase() {
        String vehicleNumber = vehicleNumberTextField.getText();
        String arrivalTime = arrivalTimeTextField.getText();

        try {
            Connection connection = DriverManager.getConnection(Url, Username, Password);
            String insertSql = "INSERT INTO cars (car_number, time_in) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, vehicleNumber);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(arrivalTime));
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Vehicle added successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding vehicle: " + e.getMessage());
        }
    }

    // Generate a PDF (stubbed)
    private void generatePDF() {
        try {
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

            contentStream.close();

            // Save the PDF to a temporary file
            File tempFile = File.createTempFile("token", ".pdf");
            document.save(new FileOutputStream(tempFile));
            document.close();

            // Open the PDF in the default system viewer (typically a PDF reader)
            Desktop.getDesktop().open(tempFile);

            JOptionPane.showMessageDialog(frame, "Token generated and opened for printing.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating bill: " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        new Parking("jk01AC2001");
    }
}
