import java.util.HashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Station extends Entity {
  private static HashMap<String, Station> objectHolder = new HashMap<>();

  public Lock trainLock = new ReentrantLock();
  public Condition trainC = trainLock.newCondition();

  public Lock passengerLock = new ReentrantLock();
  public Condition passengerC = passengerLock.newCondition();

  private Station(String name) { super(name); }

  public static Station make(String name) {
    // Change this method!
    if (!objectHolder.containsKey(name)) {
      objectHolder.put(name, new Station(name));
    }
    return objectHolder.get(name);
  }

  public static void clear() {
    objectHolder.clear();
  }
}
