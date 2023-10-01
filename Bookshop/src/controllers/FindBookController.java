package controllers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Book;

public class FindBookController implements Initializable {
  private Stage stage;
  private Scene scene;
  private Parent root;

  @FXML
  private TableView<Book> bookTableView;
  @FXML
  private TableColumn<Book, Integer> idTableColumn;
  @FXML
  private TableColumn<Book, String> isbnTableColumn;
  @FXML
  private TableColumn<Book, String> titleTableColumn;
  @FXML
  private TableColumn<Book, String> authorTableColumn;
  @FXML
  private TableColumn<Book, String> genreTableColumn;
  @FXML
  private TableColumn<Book, Integer> qtyTableColumn;
  @FXML
  private TableColumn<Book, Double> priceTableColumn;
  @FXML
  private TextField searchBar;

  private final Connection connection = DatabaseConnection.getConnection();

  ObservableList<Book> booksObservableList = FXCollections.observableArrayList();

  @Override
  public void initialize(URL url, ResourceBundle resource) {

    String query = "SELECT bookID, ISBN, title, author, genre, quantity, price FROM book";
    try {

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(query);

      while (resultSet.next()) {
        int id = resultSet.getInt("bookID");
        String isbn = resultSet.getString("ISBN");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        String genre = resultSet.getString("genre");
        int quantity = resultSet.getInt("quantity");
        double price = resultSet.getDouble("price");
        booksObservableList.add(new Book(id, isbn, title, author, genre, quantity, price));
      }

      idTableColumn.setCellValueFactory(new PropertyValueFactory<>("bookID"));
      isbnTableColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
      titleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
      authorTableColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
      genreTableColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
      qtyTableColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
      priceTableColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

      FilteredList<Book> filteredList = new FilteredList<>(booksObservableList, b -> true);

      searchBar.textProperty().addListener((observable, newValue, oldValue) -> {
        filteredList.setPredicate((book) -> {
          if (newValue.isBlank() || newValue.isEmpty() || newValue == null)
            return true;
          String searchKeyword = newValue.toLowerCase();
          if (book.getISBN().indexOf(searchKeyword) > -1)
            return true;
          else if (book.getTitle().toLowerCase().indexOf(searchKeyword) > -1)
            return true;
          else if (book.getAuthor().toLowerCase().indexOf(searchKeyword) > -1)
            return true;
          else if (book.getGenre().toLowerCase().indexOf(searchKeyword) > -1)
            return true;
          else if (book.getBookID().toString().indexOf(searchKeyword) > -1)
            return true;
          else
            return false;
        });
      });

      SortedList<Book> sortedData = new SortedList<>(filteredList);
      sortedData.comparatorProperty().bind(bookTableView.comparatorProperty());

      bookTableView.setItems(sortedData);

    } catch (SQLException e) {
      e.printStackTrace();

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