package models;

public class Book {
  private Integer bookID;
  private String ISBN;
  private String title;
  private String author;
  private String genre;
  private Integer quantity;
  private Double price;

  public Book(String iSBN, String title, Double price) {
    ISBN = iSBN;
    this.title = title;
    this.price = price;
  }

  public Book(Integer bookID, String ISBN, String title, String author, String genre, Integer quantity, Double price) {
    this.bookID = bookID;
    this.ISBN = ISBN;
    this.title = title;
    this.author = author;
    this.quantity = quantity;
    this.genre = genre;
    this.price = price;
  }

  public Integer getBookID() {
    return bookID;
  }

  public void setBookID(Integer bookID) {
    this.bookID = bookID;
  }

  public String getISBN() {
    return ISBN;
  }

  public void setISBN(String ISBN) {
    this.ISBN = ISBN;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
