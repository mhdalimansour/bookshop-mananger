package controllers;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class CreateNewBook {
  private final Connection connection = DatabaseConnection.getConnection();

  private File selected;

  private Stage stage;
  private Scene scene;
  private Parent root;

  @FXML
  private TextField isbnTextField;
  @FXML
  private TextField titleTextField;
  @FXML
  private TextField authorTextField;
  @FXML
  private TextField genreTextField;
  @FXML
  private TextField qtyTextField;
  @FXML
  private Label labelPath;

  // Uses the text fields to Create 1 book at a time
  public void createnewbook(ActionEvent event) throws Exception {
    String isbn = isbnTextField.getText();
    FXMLLoader loader;
    String title = titleTextField.getText();
    String author = authorTextField.getText();
    String genre = genreTextField.getText();
    int quantity = Integer.parseInt(qtyTextField.getText());
    if (findisbn(isbn)) {
      add_book(isbn, quantity);
      Alert a = new Alert(AlertType.INFORMATION, "Book Added!");
      a.show();
    } else {
      create_book(isbn, title, author, genre, quantity);
      Alert a = new Alert(AlertType.INFORMATION, "Book Created!");
      a.show();
    }

    loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  // Creates a new book and adds it to the db
  private void create_book(String isbn, String title, String author, String genre, int quantity)
      throws Exception {
    String query = "INSERT INTO book (ISBN, title, author, genre, quantity) VALUES (?,?,?,?,?)";
    PreparedStatement statement = connection.prepareStatement(query);
    statement.setString(1, isbn);
    statement.setString(2, title);
    statement.setString(3, author);
    statement.setString(4, genre);
    statement.setInt(5, quantity);
    statement.executeUpdate();
    statement.close();
  }

  // returns true if the isbn is found
  public boolean findisbn(String isbn) throws Exception {
    String selectQuery = "SELECT quantity FROM book WHERE ISBN = ?";

    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
    selectStatement.setString(1, isbn);
    ResultSet resultSet = selectStatement.executeQuery();

    if (resultSet.next()) {

      selectStatement.close();
      resultSet.close();

      return true;
    } else {
      selectStatement.close();
      resultSet.close();
      return false;
    }
  }

  // Reads the excel files row by row, if the isbn is found we use add_book else
  // we create the book
  public void excel(ActionEvent event) throws Exception {
    if (selected == null) {
      Alert fileError = new Alert(AlertType.ERROR, "File Error");
      fileError.show();
      return;
    }
    Workbook workbook = Workbook.getWorkbook(selected);
    Sheet sheet = workbook.getSheet(0);
    int numRows = sheet.getRows();
    for (int i = 1; i < numRows; i++) {
      Cell isbncell = sheet.getCell(0, i);
      Cell qtycell = sheet.getCell(1, i);
      Cell titleCell = sheet.getCell(2, i);
      Cell authorCell = sheet.getCell(3, i);
      Cell genreCell = sheet.getCell(4, i);

      String isbn = isbncell.getContents();
      int quantity = Integer.parseInt(qtycell.getContents());
      String title = titleCell.getContents();
      String author = authorCell.getContents();
      String genre = genreCell.getContents();

      if (findisbn(isbn)) {
        add_book(isbn, quantity);
      } else {
        create_book(isbn, title, author, genre, quantity);
      }
    }

    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  // Choose file button, stores it in selected (File)
  public void choose() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open a file");
    fileChooser.setInitialDirectory(new File("C:\\"));
    ExtensionFilter excelFilter = new ExtensionFilter("Excel Files (*.xlsx, *.xls)", "*.xlsx", "*.xls");
    fileChooser.getExtensionFilters().addAll(excelFilter);
    File selectedfile = fileChooser.showOpenDialog(stage);
    labelPath.setText(selectedfile.getName());
    selected = selectedfile;
  }

  // Search for the isbn (100% found) , get the old qty , update the new qty for
  // old-qty + parameter-qty
  public int add_book(String isbn, int quantity) throws Exception {
    String selectQuery = "SELECT quantity FROM book WHERE ISBN = ?";
    String updateQuery = "UPDATE book SET quantity = ? WHERE ISBN = ?";

    PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
    selectStatement.setString(1, isbn);
    ResultSet resultSet = selectStatement.executeQuery();

    if (resultSet.next()) {
      int currentQuantity = resultSet.getInt("quantity");
      int newQuantity = currentQuantity + quantity;

      PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
      updateStatement.setInt(1, newQuantity);
      updateStatement.setString(2, isbn);

      updateStatement.executeUpdate();

      updateStatement.close();
      selectStatement.close();
      resultSet.close();

      return newQuantity;
    } else {
      selectStatement.close();
      resultSet.close();
      return 0;
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