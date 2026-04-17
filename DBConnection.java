package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:sqlite:C:/Users/1/IdeaProjects/Helix/smart_healthcare.db";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Database connected successfully");
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}