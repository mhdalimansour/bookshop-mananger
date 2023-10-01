package views;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import controllers.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LogInPage {
  class Pair {
    boolean auth, isAdmin;

    public Pair(boolean auth, boolean isAdmin) {
      this.auth = auth;
      this.isAdmin = isAdmin;
    }
  }

  @FXML
  TextField usernameText;
  @FXML
  PasswordField passwordText;

  private final Connection connection = DatabaseConnection.getConnection();

  private Stage stage;
  private Scene scene;
  private Parent root;

  // Authintication
  public void login(ActionEvent event) throws IOException {
    String username = usernameText.getText();
    String password = passwordText.getText();
    Pair authi = auth(username, password);

    if (authi.auth) {
      FXMLLoader loader;
      if (authi.isAdmin) {
        loader = new FXMLLoader(getClass().getResource("fxml/MainAdminPage.fxml"));
      } else {
        loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
      }
      root = loader.load();

    } else if (foundUsername(username)) {
      Alert a = new Alert(AlertType.ERROR);
      a.setContentText("Wrong Password");
      a.show();

    } else {
      Alert a = new Alert(AlertType.ERROR);
      a.setContentText("Username not found!\nCheck with your boss for your account credentials...");
      a.show();
    }

    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  private Pair auth(String username, String password) {
    try {
      String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, username);
      statement.setString(2, password);

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        boolean isAdmin = resultSet.getBoolean("isAdmin");
        System.out.println("Authentication successful!");
        return new Pair(true, isAdmin);
      }
      resultSet.close();
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new Pair(false, false);
  }

  // Returns if the username if found or not
  private boolean foundUsername(String username) {
    try {
      String sql = "SELECT * FROM employee WHERE username = ?";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, username);

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        return true;
      }
      resultSet.close();
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

}
