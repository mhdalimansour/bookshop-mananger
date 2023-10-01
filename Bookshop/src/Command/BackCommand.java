package Command;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BackCommand implements Command {

  private String backto;

  public BackCommand(String backto) {
    this.backto = backto;
  }

  public void execute(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/" + backto));
    Parent root = loader.load();
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
  }
}
