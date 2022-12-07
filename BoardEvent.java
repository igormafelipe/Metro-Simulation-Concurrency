import java.util.*;

public class BoardEvent implements Event {
  public final Passenger p; public final Train t; public final Station s;
  public BoardEvent(Passenger p, Train t, Station s) {
    this.p = p; this.t = t; this.s = s;
  }
  public boolean equals(Object o) {
    if (o instanceof BoardEvent e) {
      return p.equals(e.p) && t.equals(e.t) && s.equals(e.s);
    }
    return false;
  }
  public int hashCode() {
    return Objects.hash(p, t, s);
  }
  public String toString() {
    return "Passenger " + p + " boards " + t + " at " + s;
  }
  public List<String> toStringList() {
    return List.of(p.toString(), t.toString(), s.toString());
  }
  public void replayAndCheck(MBTA mbta) {
    //Passenger must be at station, train must be at station.
    // Passenger must be at the NEXT STATION he wants to be AT!!!!
    if (!(mbta.checkTrainAtStation(this.t, this.s) &&
            mbta.checkPassengerAtStation(this.p, this.s))) {
      //Raise an exception
      throw new RuntimeException("Train or Passenger not at proper station");
    }
    mbta.updatePassengerTrain(this.p, this.t);
  }
}
