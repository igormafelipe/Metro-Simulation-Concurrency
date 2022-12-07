import java.util.HashMap;
import java.util.List;

public class jsonConfigC {
    public HashMap<String, List<String>> lines;
    public HashMap<String, List<String>> trips;

    jsonConfigC(HashMap<String, List<String>> linesG, HashMap<String, List<String>> tripsG) {
        lines = linesG;
        trips = tripsG;
    }

    jsonConfigC() {
        lines = new HashMap<String, List<String>>();
        trips = new HashMap<String, List<String>>();
    }

    jsonConfigC(List<HashMap<String, List<String>>> meh) {
        lines = meh.get(0);
        trips = meh.get(1);
    }
}