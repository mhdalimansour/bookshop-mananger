package controllers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// this is a singleton design pattern

public class DatabaseConnection {
  private static Connection connection = null;

  private DatabaseConnection() {
  }

  public static Connection getConnection() {
    if (connection == null) {
      try {
        String url = "jdbc:mysql://localhost:3306/bookshop";
        String username = "root";
        String password = "mans";

        connection = DriverManager.getConnection(url, username, password);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return connection;
  }
}
