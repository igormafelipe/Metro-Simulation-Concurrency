import java.util.HashMap;

public class Train extends Entity {
  private static HashMap<String, Train> objectHolder = new HashMap<>();
  private Train(String name) {
    super(name);
  }

  public static Train make(String name) {
    // Change this method!
    if (!objectHolder.containsKey(name)) {
      objectHolder.put(name, new Train(name));
    }
    return objectHolder.get(name);
  }

  public static void clear() {
    objectHolder.clear();
  }
}
