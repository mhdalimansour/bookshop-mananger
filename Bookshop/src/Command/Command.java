package Command;

import java.io.IOException;

import javafx.event.ActionEvent;

public interface Command {
  void execute(ActionEvent event) throws IOException;
}
