package controllers;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RemoveEmployeeController {
  @FXML
  private TextField employeeUsername;

  private final Connection connection = DatabaseConnection.getConnection();

  private Stage stage;
  private Scene scene;
  private Parent root;

  // Deletes the username from the db
  public void remove(ActionEvent event) throws Exception {
    String username = employeeUsername.getText();
    int userID = findusername(username);
    if (userID == -1) {
      Alert a = new Alert(AlertType.ERROR, "User Not Found!");
      a.show();
    } else {
      Alert a = new Alert(AlertType.CONFIRMATION);
      a.setTitle("Confirmation");
      a.setHeaderText("Are you sure you want to delete this User?");
      a.setContentText("This action cannot be undone.");

      ButtonType yesButton = new ButtonType("Yes");
      ButtonType noButton = new ButtonType("No");
      a.getButtonTypes().setAll(yesButton, noButton);
      a.showAndWait().ifPresent(response -> {
        if (response == yesButton) {
          String id = userID + "";
          String query = "DELETE FROM user WHERE userID = ?";
          try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          Alert alert = new Alert(AlertType.INFORMATION,
              "User with username: " + username + " is removed!");
          alert.show();
        }
      });
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainAdminPage.fxml"));
      root = loader.load();
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    }
  }

  // Returns the id of the username, if not found -1
  private int findusername(String username) throws Exception {
    String query = "SELECT userID FROM user WHERE username = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, username);
    ResultSet output = preparedStatement.executeQuery();
    if (output.next()) {
      int id = output.getInt("userID");
      output.close();
      preparedStatement.close();
      return id;
    } else {
      output.close();
      preparedStatement.close();
      return -1;
    }
  }

  // Back Button
  public void back(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainAdminPage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
