import java.util.*;

public class DeboardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public DeboardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof DeboardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " deboards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {

    if (!(mbta.checkTrainAtStation(this.t, this.s) && mbta.checkPassengerInTrain(this.p, this.t) && mbta.nextStations.get(this.p).peek().equals(this.s))) {
      throw new RuntimeException("Train not at station or passenger not in train");
    }
    mbta.updatePassengerStation(this.p, this.s);  // Passenger is at station s.
    mbta.updatePassengerTrain(this.p, null); // Passenger is at no train.
  }
}
