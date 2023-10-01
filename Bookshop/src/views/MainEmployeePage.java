package views;
import java.io.IOException;

import Command.AddCommand;
import Command.BackCommand;
import Command.Command;
import Command.CreateBookCommand;
import Command.CreateCustomerCommand;
import Command.FindCommand;
import Command.RemoveBookCommand;
import Command.SellCommand;
import javafx.event.ActionEvent;

public class MainEmployeePage {

  // Here I applied Command Design Pattern

  private Command sellCommand;
  private Command findCommand;
  private Command addCommand;
  private Command createCommand;
  private Command createCustomerCommand;
  private Command removeCommand;
  private Command backCommand;

  public MainEmployeePage() {
    sellCommand = new SellCommand();
    findCommand = new FindCommand();
    addCommand = new AddCommand();
    createCommand = new CreateBookCommand();
    createCustomerCommand = new CreateCustomerCommand();
    removeCommand = new RemoveBookCommand();
    backCommand = new BackCommand("Login.fxml");
  }

  public void handleButton(ActionEvent event, Command command) throws IOException {
    command.execute(event);
  }

  // Sell a Book Button
  public void sell(ActionEvent event) throws IOException {
    handleButton(event, sellCommand);
  }

  // Find Book Button
  public void findbook(ActionEvent event) throws IOException {
    handleButton(event, findCommand);
  }

  // Add Book Button
  public void addbook(ActionEvent event) throws IOException {
    handleButton(event, addCommand);
  }

  // Create Book Button
  public void createbook(ActionEvent event) throws IOException {
    handleButton(event, createCommand);
  }

  // Create Customer Page
  public void createcustomer(ActionEvent event) throws IOException {
    handleButton(event, createCustomerCommand);
  }

  // Remove Book Button
  public void removebook(ActionEvent event) throws IOException {
    handleButton(event, removeCommand);
  }

  // Back Button
  public void back(ActionEvent event) throws IOException {
    handleButton(event, backCommand);
  }
}
