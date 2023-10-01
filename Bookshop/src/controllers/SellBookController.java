package controllers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import Observer.Observer;
import Observer.Subject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Admin;
import models.Book;

public class SellBookController implements Subject, Initializable {
  @FXML
  private TextField oldPhonenumber;
  @FXML
  private TableView<Book> table;
  @FXML
  private TableColumn<Book, String> isbnColumn;
  @FXML
  private TableColumn<Book, String> titleColumn;
  @FXML
  private TableColumn<Book, Double> priceColumn;
  @FXML
  private TableColumn<Book, String> qtyColumn;
  @FXML
  private TextField bookToAdd;
  @FXML
  private TextField qtyTextField;
  @FXML
  private Label totalLabel;
  HashMap<String, Integer> duplicateBook = new HashMap<>();

  ObservableList<Book> booksObservableList = FXCollections.observableArrayList();

  private final Connection connection = DatabaseConnection.getConnection();

  private Stage stage;
  private Scene scene;
  private Parent root;

  private List<Observer> observers = new ArrayList<>();

  private void admin() {
    registerObserver(new Admin("mhmdali"));
  }

  public void confirm(ActionEvent event) throws Exception {
    String oldphonetext = oldPhonenumber.getText();
    if (oldphonetext.isEmpty() || oldphonetext.isBlank()) {
      Alert err1 = new Alert(AlertType.ERROR);
      err1.show();
    } else {
      int cID = findPhone(oldphonetext);
      if (cID == -1) {
        Alert error = new Alert(AlertType.ERROR, "Costumer Not Fround!");
        ButtonType createCustomer = new ButtonType("Create Customer Page");
        error.getButtonTypes().add(createCustomer);
        error.showAndWait().ifPresent(response -> {
          if (response == createCustomer) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/CreateCustomerPage.fxml"));
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
        insertPurchase(cID);
        String s = "";
        s += "SUCCESS!\n";
        s += "Your Total is: " + total + "\n";
        s += "Books Bought:\n";
        for (String book : duplicateBook.keySet()) {
          s += book + " x " + duplicateBook.get(book) + "\n";
        }
        s += "\nHave a nice day!";
        Alert success = new Alert(AlertType.INFORMATION, s);
        success.show();
      }
    }
    booksObservableList.clear();
    decrementBooks();
    notifyObservers(duplicateBook, total, oldphonetext);
    duplicateBook.clear();
    System.out.println("Order Complete");
  }

  private void insertPurchase(Integer cID) throws Exception {
    for (String isbn : duplicateBook.keySet()) {
      String query = "INSERT INTO customerpurchases (customerID, ISBN,quantity, purchaseDate) VALUES (? ,? ,? ,?)";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, cID.toString());
      preparedStatement.setString(2, isbn);
      preparedStatement.setString(3, duplicateBook.get(isbn).toString());
      Date currentDate = new Date();
      Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
      preparedStatement.setTimestamp(4, currentTimestamp);
      preparedStatement.executeUpdate();
      preparedStatement.close();
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
      Alert phonenot = new Alert(AlertType.ERROR, "Phone Number Not Found!");
      phonenot.show();
      preparedStatement.close();
      output.close();
      return -1;
    }
  }

  double total = 0;

  public void addBook() throws Exception {
    int oldQuantity = 0;
    Integer newQuantity = Integer.parseInt(qtyTextField.getText());
    String toAdd = bookToAdd.getText();
    String query = "SELECT title, price FROM book WHERE ISBN = ?";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, toAdd);
    ResultSet output = preparedStatement.executeQuery();
    if (output.next()) {
      String isbn = toAdd;
      String title = output.getString("title");
      double price = output.getDouble("price");
      total += price * newQuantity;
      if (duplicateBook.get(isbn) != null) {
        oldQuantity = duplicateBook.get(isbn);
        newQuantity += oldQuantity;
        duplicateBook.put(isbn, newQuantity);
      } else {
        booksObservableList.add(new Book(isbn, title, price));
        duplicateBook.put(isbn, newQuantity);
      }
    } else {
      Alert error = new Alert(AlertType.ERROR, "Book Not Found!");
      error.show();
    }

    System.out.println(duplicateBook);
    isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
    titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    qtyColumn.setCellValueFactory(celldata -> {
      String isbn = celldata.getValue().getISBN();
      Integer quantity = duplicateBook.get(isbn);

      if (quantity != null) {
        return new SimpleStringProperty(quantity.toString());
      } else {
        return new SimpleStringProperty("0");
      }
    });
    table.setItems(null);
    table.layout();
    table.setItems(booksObservableList);
    totalLabel.setText(total + "");
    bookToAdd.setText("");
    qtyTextField.setText("");
  }

  public void decrementBooks() throws Exception {
    for (String isbn : duplicateBook.keySet()) {
      String querySelect = "SELECT quantity FROM book WHERE ISBN = ?";
      PreparedStatement statement = connection.prepareStatement(querySelect);
      statement.setString(1, isbn);
      ResultSet selectOut = statement.executeQuery();
      if (selectOut.next()) {

        int oldQuantity = selectOut.getInt("quantity");

        String query = "UPDATE book SET quantity = ? WHERE ISBN = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        Integer qty = oldQuantity - duplicateBook.get(isbn);
        preparedStatement.setString(1, qty.toString());
        preparedStatement.setString(2, isbn);
        preparedStatement.executeUpdate();
        preparedStatement.close();
        statement.close();
      }
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

  @Override
  public void registerObserver(Observer observer) {
    observers.add(observer);
  }

  @Override
  public void removeObserver(Observer observer) {
    observers.remove(observer);
  }

  @Override
  public void notifyObservers(HashMap<String, Integer> duplicateBook2, double price, String oldphonetext) {
    for (Observer observer : observers) {
      observer.update(duplicateBook2, price, oldphonetext);
    }
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    admin();
  }
}
