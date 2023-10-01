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
import models.Customer;

public class CreateCustomerController {
  private Stage stage;
  private Scene scene;
  private Parent root;

  private final Connection connection = DatabaseConnection.getConnection();

  @FXML
  private TextField firstNameField, lastNameField, emailField, phoneField;

  private boolean create_customer(Customer customer) {
    try {
      String query = "INSERT INTO customer (firstname,lastname,email,phonenumber) VALUES (?,?,?,?)";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, customer.getFirstname());
      preparedStatement.setString(2, customer.getLastname());
      preparedStatement.setString(3, customer.getEmail());
      preparedStatement.setString(4, customer.getPhonenumer());
      preparedStatement.executeUpdate();
      preparedStatement.close();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  public void create(ActionEvent event) throws Exception {
    String firstname = firstNameField.getText();
    String lastname = lastNameField.getText();
    String email = emailField.getText();
    String phone = phoneField.getText();
    Customer newCustomer = new Customer(firstname, lastname, phone, email);
    if (findPhone(phone) != -1) {
      Alert info = new Alert(AlertType.INFORMATION, "Customer Exists!");
      ButtonType newbutton = new ButtonType("Go to Sell Book Page");
      info.getButtonTypes().add(newbutton);
      info.showAndWait().ifPresent(respone -> {
        if (respone == newbutton) {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/SellBook.fxml"));
          try {
            root = loader.load();
          } catch (IOException e) {
            e.printStackTrace();
          }
          stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
          scene = new Scene(root);
          stage.setScene(scene);
          stage.show();
        }
      });
    } else {
      if (create_customer(newCustomer)) {
        Alert success = new Alert(AlertType.INFORMATION, "Customer Added");
        success.show();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
      } else {
        Alert error = new Alert(AlertType.ERROR, "Error Creating Customer");
        error.show();
      }
    }
  }

  // Returns id of customer from phone nb ,-1 if not found
  public int findPhone(String phone) throws Exception {
    String query = "SELECT customerID FROM customer WHERE phonenumber = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, phone);
    ResultSet output = preparedStatement.executeQuery();
    if (output.next()) {
      int id = output.getInt("customerID");
      preparedStatement.close();
      output.close();
      return id;
    } else {
      preparedStatement.close();
      output.close();
      return -1;
    }
  }

  public void back(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
