package views;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Employee;

public class CreateEmployeePage {
  @FXML
  TextField firstName;
  @FXML
  TextField lastName;
  @FXML
  TextField usernameNew;
  @FXML
  TextField passwordNew;
  @FXML
  TextField salaryNew;

  private Stage stage;
  private Scene scene;
  private Parent root;

  private final Connection connection = DatabaseConnection.getConnection();

  public void createEmployee(ActionEvent event) throws IOException {
    String username_txt = usernameNew.getText();
    String firstname_txt = firstName.getText();
    String lastname_txt = lastName.getText();
    String password_txt = passwordNew.getText();
    int salary = Integer.parseInt(salaryNew.getText());
    Employee newEmployee = new Employee(username_txt, password_txt, firstname_txt, lastname_txt, salary);
    insertEmployee(newEmployee);
    Alert a = new Alert(AlertType.INFORMATION, "Employee Created!");
    a.show();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainAdminPage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  // Add given Employee to the db
  private void insertEmployee(Employee employee) {
    try {
      String sql = "INSERT INTO user (username, first_name, last_name, password, salary) VALUES (?, ?, ?, ?, ?)";
      PreparedStatement statement = connection.prepareStatement(sql);
      statement.setString(1, employee.getUsername());
      statement.setString(2, employee.getFirstName());
      statement.setString(3, employee.getLastName());
      statement.setString(4, employee.getPassword());
      statement.setInt(5, employee.getSalary());

      statement.executeUpdate();
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
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
