package indi.anonymity.helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by emily on 17/2/14.
 */
public class DatabaseConnector {

    private final String DRIVER = "com.mysql.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/Valid";
    private final String USER = "root";
    private final String PASSWORD = "19911109";
    private Connection connection = null;

    public void connect() throws Exception {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("mysql connected!");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    public Connection getConnection() {
        return this.connection;
    }
    public void stopConnection() {
        if (this.connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
