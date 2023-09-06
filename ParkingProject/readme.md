# Parking Management System

This project is a simple Parking Management System implemented in Java using Swing for the graphical user interface and MySQL for data storage. It allows users to add vehicles, calculate parking fees, generate tokens and bills, and track parked vehicles.

## Files

### 1. Welcome.java

**Description:** The `Welcome` class is the main entry point of the application. It creates the main window where users can enter vehicle numbers, check available parking spaces, and access various functionalities.

### 2. Parking.java

**Description:** The `Parking` class represents the window for adding vehicles to the parking system. It allows users to set the arrival time, add vehicles to the database, and generate parking tokens.

### 3. Exit.java

**Description:** The `Exit` class represents the window for exiting vehicles from the parking system. It allows users to set the exit time, calculate parking fees based on hourly rates, generate bills, and remove vehicle records from the database.

### 4. MySQL Database

**Description:** The project uses a MySQL database for storing vehicle records. The database is configured with the following parameters:

- **URL:** jdbc:mysql://localhost:3307/parking
- **Username:** root
- **Password:** [blank]

### 5. PDFBox Library

**Description:** The project uses the Apache PDFBox library to generate PDF tokens and bills for vehicles. Make sure to include the PDFBox library in your project's dependencies.

## Getting Started

1. Compile and run the Java files using an IDE or command-line Java compiler.

2. Ensure that a MySQL database server is running with the provided configuration.

3. Make sure to include the PDFBox library in your project's dependencies.

## Usage

1. Run the `Welcome` class to start the Parking Management System.

2. Use the "Search / Add" button to check if a vehicle is parked or add a new vehicle.

3. Use the "Get Total Cars" button to check the total number of parked vehicles.

4. In the `Parking` window, set the arrival time and click "Add Vehicle" to add a vehicle.

5. In the `Exit` window, set the exit time and click "Calculate Price" to calculate parking fees.

6. Click "Generate Bill / Exit" to generate a bill or remove the vehicle record.

7. Use the "Return to Main" button to go back to the main window.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Apache PDFBox](https://pdfbox.apache.org/) - Used for PDF generation.
- [MySQL](https://www.mysql.com/) - Used for database storage.
- [Java Swing](https://docs.oracle.com/en/java/javase/11/docs/api/java.desktop/javax/swing/package-summary.html) - Used for the graphical user interface.