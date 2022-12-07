import java.util.*;

public class MoveEvent implements Event {
  public static final String RESET  = "\u001B[0m";
  public static final String RED    = "\u001B[31m";
  public static final String GREEN  = "\u001B[32m";
  public static final String YELLOW = "\u001B[33m";
  public static final String BLUE   = "\u001B[34m";
  public static final String PURPLE = "\033[0;35m";

  public final Train t; public final Station s1, s2;
  public MoveEvent(Train t, Station s1, Station s2) {
    this.t = t; this.s1 = s1; this.s2 = s2;
  }
  public boolean equals(Object o) {
    if (o instanceof MoveEvent e) {
      return t.equals(e.t) && s1.equals(e.s1) && s2.equals(e.s2);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(t, s1, s2);
  }
  public String toString() {
    String color = t.toString();
    String ansi  = switch (color) {
      case "red"    -> RED;
      case "blue"   -> BLUE;
      case "orange" -> YELLOW;
      case "green"  -> GREEN;
      case "purple" -> PURPLE;
      default -> "{no_color}";
    };

    return ansi + "Train " + t + " moves from " + s1 + " to " + s2 + RESET;
  }
  public List<String> toStringList() {
    return List.of(t.toString(), s1.toString(), s2.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    if (!(mbta.checkTrainAtStation(this.t, this.s1)
            && mbta.checkTrainGoesToStation(this.t, this.s2)
            && mbta.checkStationEmpty(this.s2, this.t))) {
      throw new RuntimeException("Train either not at station or does not go to station");
    }
    mbta.updateTrainStation(this.t, this.s2);
  }
}
