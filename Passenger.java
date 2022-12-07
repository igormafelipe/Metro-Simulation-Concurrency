import java.util.HashMap;

public class Passenger extends Entity {
  private static HashMap<String, Passenger> objectHolder = new HashMap<>();
  Train train = null;
  Station oldStation = null;

  private Passenger(String name) {
    super(name);
  }

  public static Passenger make(String name) {
    // Change this method!
    if (!objectHolder.containsKey(name)) {
      objectHolder.put(name, new Passenger(name));
    }
    return objectHolder.get(name);
  }

  public static void clear() {
    objectHolder.clear();
  }
}
