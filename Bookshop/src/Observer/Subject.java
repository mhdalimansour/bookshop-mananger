package Observer;

import java.util.HashMap;

public interface Subject {
  void registerObserver(Observer observer);

  void removeObserver(Observer observer);

  void notifyObservers(HashMap<String, Integer> books, double price, String customer);
}
