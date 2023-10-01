package controllers;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import Adapter.XLSAdapter;
import Adapter.XLSXFile;
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
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class AddBookController {

  private final Connection connection = DatabaseConnection.getConnection();

  private File selected;

  private Stage stage;
  private Scene scene;
  private Parent root;

  @FXML
  private TextField isbnTextField;
  @FXML
  private TextField qtyTextField;
  @FXML
  private Label labelPath;

  // Add book manually
  public void addnewbook(ActionEvent event) throws Exception {
    String isbn = isbnTextField.getText();
    int quantity = Integer.parseInt(qtyTextField.getText());
    FXMLLoader loader;
    int nQuantity = findisbn(isbn, quantity);

    if (nQuantity != 0) {
      Alert a = new Alert(AlertType.CONFIRMATION, "Book Added!\nNew Quantity = " + nQuantity);
      a.show();
    } else {
      Alert a = new Alert(AlertType.ERROR, "ISBN not found\nProceeding to Create New Book page");
      a.show();
      loader = new FXMLLoader(getClass().getResource("fxml/CreateNewBook.fxml"));
      root = loader.load();
      stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
      scene = new Scene(root);
      stage.setScene(scene);
      stage.show();
    }
  }

  // Choose File
  public void choose() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open a file");
    fileChooser.setInitialDirectory(new File("D:\\CS\\Bookshop\\E-Manager"));
    ExtensionFilter excelFilter = new ExtensionFilter("Excel Files (*.xlsx, *.xls)", "*.xlsx", "*.xls");
    fileChooser.getExtensionFilters().addAll(excelFilter);
    File selectedfile = fileChooser.showOpenDialog(stage);
    labelPath.setText(selectedfile.getName());
    if (selectedfile.getPath().endsWith(".xlsx")) {
      XLSAdapter adapter = new XLSAdapter(new XLSXFile(selectedfile.getPath()));
      String convertedFile = adapter.convert("output.xls");
      selected = new File(convertedFile);
    } else
      selected = selectedfile;

    System.out.println(selected.getPath());
  }

  // Read excel file selected, go through each row, add the quantities, if book
  // not found, adds it to a Map<ISBN,Quantity> , go through the map, adds the new
  // books to an excel file
  public void excel(ActionEvent event) throws Exception {
    if (selected == null) {
      Alert fileError = new Alert(AlertType.ERROR, "File Error");
      fileError.show();
      return;
    }
    System.out.println(selected.getPath());
    Workbook workbook = Workbook.getWorkbook(selected);
    Sheet sheet = workbook.getSheet(0);
    int numRows = sheet.getRows();
    HashMap<String, Integer> booksToCreate = new HashMap<>();
    for (int i = 1; i < numRows; i++) {
      Cell isbncell = sheet.getCell(0, i);
      Cell qtycell = sheet.getCell(1, i);
      String isbn = isbncell.getContents();
      int quantity = Integer.parseInt(qtycell.getContents());
      int flag = findisbn(isbn, quantity);
      if (flag == 0) {
        booksToCreate.put(isbn, quantity);
      }
    }
    if (!booksToCreate.isEmpty()) {
      String newPath = "D:\\CS\\Bookshop\\E-Manager\\CREATE.xls";

      WritableWorkbook toWrite = Workbook.createWorkbook(new File(newPath));
      WritableSheet excelSheet = toWrite.createSheet("Sheet 1", 0);
      excelSheet.addCell(new jxl.write.Label(0, 0, "ISBN"));
      excelSheet.addCell(new jxl.write.Label(1, 0, "Quantity"));
      excelSheet.addCell(new jxl.write.Label(2, 0, "Title"));
      excelSheet.addCell(new jxl.write.Label(3, 0, "Author"));
      excelSheet.addCell(new jxl.write.Label(4, 0, "Genre"));
      int row = 1;
      String s = "";
      s += "The Following ISBNs are added to the new Excel Sheet:\n";
      for (Map.Entry<String, Integer> entry : booksToCreate.entrySet()) {
        excelSheet.addCell(new jxl.write.Label(0, row, entry.getKey())); // ISBN column
        excelSheet.addCell(new jxl.write.Number(1, row, entry.getValue())); // Quantity column
        s += entry.getKey() + "\n";
        row++;
      }
      System.out.println(s);
      Alert booksNotFound = new Alert(AlertType.INFORMATION);
      booksNotFound.setTitle("New Excel Created");
      booksNotFound.setContentText("New Excel Location: " + newPath + "\n" + s);
      booksNotFound.show();
      toWrite.write();
      toWrite.close();
    }
    Alert booksadded = new Alert(AlertType.INFORMATION, "Books added!");
    booksadded.show();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainEmployeePage.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }

  // Returns the quantity of the ISBN after update, if it returned 0, no ISBN was
  // found, should be created
  public int findisbn(String isbn, int quantity) throws Exception {
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