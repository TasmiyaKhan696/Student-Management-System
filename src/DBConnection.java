package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // Database configuration
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/student_ms?useSSL=false&serverTimezone=UTC";

    private static final String DB_USER = "root";
    private static final String DB_PASS = "root"; // change if your MySQL password is different

    private static DBConnection instance;
    private Connection connection;

    // Private constructor (Singleton pattern)
    private DBConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            System.out.println("Database connected successfully");

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("Database connection failed");
            e.printStackTrace();
        }
    }

    // Get Singleton instance
    public static synchronized DBConnection getInstance() {

        if (instance == null) {
            instance = new DBConnection();
        }

        return instance;
    }

    // Return connection
    public Connection getConnection() {
        return connection;
    }

    // Close connection
    public void closeConnection() {

        try {

            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}