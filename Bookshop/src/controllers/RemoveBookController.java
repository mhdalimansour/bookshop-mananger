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

public class RemoveBookController {

  @FXML
  private TextField isbnText;

  private Stage stage;
  private Scene scene;
  private Parent root;

  private final Connection connection = DatabaseConnection.getConnection();

  // Deletes a Book from the db
  public void remove(ActionEvent event) throws Exception {
    String isbn = isbnText.getText();
    int bookid = findisbn(isbn);
    if (bookid == -1) {
      Alert a = new Alert(AlertType.ERROR, "Book not with ISBN " + isbn + " not found!");
      a.show();
    } else {
      Alert a1 = new Alert(AlertType.CONFIRMATION);
      a1.setTitle("Confirmation");
      a1.setHeaderText("Are you sure you want to delete this User?");
      a1.setContentText("This action cannot be undone.");
      ButtonType yesButton = new ButtonType("Yes");
      ButtonType noButton = new ButtonType("No");
      a1.getButtonTypes().setAll(yesButton, noButton);
      a1.showAndWait().ifPresent(response -> {
        if (response == yesButton) {
          String query = "DELETE from book WHERE isbn = ?";
          try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, isbn);
            preparedStatement.executeUpdate();
            preparedStatement.close();
          } catch (SQLException e) {
            e.printStackTrace();
          }
          Alert alert = new Alert(AlertType.INFORMATION, "Book with ISBN: " + isbn + " is removed!");
          alert.show();
        }
      });
      FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
      root = loader.load();
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    }
  }

  // Returns the BookID of the ISBN, if not found -1
  private int findisbn(String isbn) throws Exception {
    String query = "SELECT bookID FROM book WHERE isbn = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, isbn);
    ResultSet output = preparedStatement.executeQuery();
    if (output.next()) {
      int id = output.getInt("bookID");
      preparedStatement.close();
      output.close();
      return id;
    } else {
      preparedStatement.close();
      output.close();
      return -1;
    }
  }

  // Back Button
  public void back(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
