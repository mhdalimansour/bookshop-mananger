package models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;

import Observer.Observer;

public class Admin extends User implements Observer {
  private String filePath = "D:\\CS\\Bookshop\\E-Manager\\updates.txt";

  private File updates = new File(filePath);

  public Admin(String username, String password, String firstname, String lastname) {
    super(username, password, firstname, lastname);
    isAdmin = true;
  }

  public Admin(String username) {
    super(username);
  }

  public void update(HashMap<String, Integer> books, double total, String customer) {
    try {
      FileWriter fileWriter = new FileWriter(updates, true);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
      String upd = "";
      Date d = new Date();
      upd += "Admin: " + this.getUsername() + "\n";
      upd += "Customer Phone #: " + customer + "\n";
      upd += "Books sold on: " + d + "\n";
      for (String isbn : books.keySet()) {
        int qty = books.get(isbn);
        upd += isbn + " x " + qty + "\n";
      }
      upd += "Total = " + total + "\n";
      bufferedWriter.write(upd);
      bufferedWriter.newLine();
      bufferedWriter.close();
      System.out.println("Updates Appended Succcessfully!");
    } catch (Exception e) {
      System.out.println("Error Appending: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
