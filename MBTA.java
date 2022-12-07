import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MBTA {

  // Creates an initially empty simulation
  public HashMap<Train, List<Station>> linesHolder = new HashMap<>();
  public HashMap<Passenger, List<Station>> journeyHolder = new HashMap<>(); // The journey can have multiple stations!
  public HashMap<Passenger, Station> PassengerstationAt = new HashMap<>(); // Holds which station a passenger is at.
  public HashMap<Train, Station> TrainStationAt = new HashMap<>(); // Holds which station train is at
  public HashMap<Station, Train> StationTrainHolds = new HashMap<>(); //Holds if a station has a train or not
  public HashMap<Train, Boolean> TrainDirection = new HashMap<>(); // Holds the train direction
  public volatile List<Passenger> PassengersInJourney = new ArrayList<>(); // Passengers not at destination
  public HashMap<Passenger, Queue<Station>> nextStations = new HashMap<>();
  // True = train going right -> index ++
    // False = train going left -> index --
  public MBTA() { reset(); }

  // Checks if the train is at the given station
  public boolean checkTrainAtStation(Train train, Station station){
    return TrainStationAt.get(train).equals(station);
  }

  public Station getLastPassengerStation(Passenger p) {
    int stationIdx = journeyHolder.get(p).size()-1;
    return journeyHolder.get(p).get(stationIdx);
  }

  public boolean checkPassengerAtStation(Passenger passenger, Station station) {
    return PassengerstationAt.get(passenger).equals(station);
  }

  public boolean checkStationInLine(Train train, Station station) {
    if (train == null) { return false; }
    List<Station> stations = linesHolder.get(train);
    return stations.contains(station);
  }

  // Checks if station train wants to go to is within the possible stations it can
  // go to.
  // A train can only go to the next station in order. Therefore, the destination station can only be
  //Directly after or before the current station.
  public boolean checkTrainGoesToStation(Train train, Station station) {
    if (train == null) {
      return false;
    }

    Station currentTrainStation = TrainStationAt.get(train);
    int indexToCheck;
    if (TrainDirection.get(train) == true) {
      indexToCheck = linesHolder.get(train).indexOf(currentTrainStation)+1;
    } else {
      indexToCheck = linesHolder.get(train).indexOf(currentTrainStation)-1;
    }
    if (linesHolder.get(train).get(indexToCheck).equals(station)) {
      return true;
    }
    return false;
  }

  public Station getNextPStation (Passenger passenger, Station currentStation) {
    if (nextStations.get(passenger).size() == 0) {
      throw new RuntimeException("No other station for passenger to go to");
    }
    return nextStations.get(passenger).peek();
//    List<Station> passengerPath = journeyHolder.get(passenger);
////    if (passenger.oldStation == null) {
////      return passengerPath.get(1);
////    }
//    int currStationIdx = passengerPath.indexOf(currentStation);
//    return passengerPath.get(currStationIdx+1);
  }

  public Station getNextTStation(Train train) {
    Station currStation = TrainStationAt.get(train);
    List<Station> trainStations = linesHolder.get(train);
    int currStationIdx = trainStations.indexOf(currStation);
    if (TrainDirection.get(train) == true) {
      return trainStations.get(currStationIdx+1);
    }
    return trainStations.get(currStationIdx-1);
  }

  public boolean checkPassengerInTrain(Passenger passenger, Train train) {
    return passenger.train.equals(train);
  }

  public boolean checkStationEmpty(Station station, Train train) {
    if (StationTrainHolds.get(station) == null) {
      return true;
    }
    return false;
  }

  public void updatePassengerTrain(Passenger passenger, Train train) {
    passenger.train = train;
  }

  // Only called on deboard. Therefore, passenger not at train
  public void updatePassengerStation(Passenger passenger, Station station) {
    if (!station.equals(TrainStationAt.get(passenger.train))) {
      throw new RuntimeException("Failed at updatePassengerStation");
    }
    PassengerstationAt.put(passenger, station);
    nextStations.get(passenger).remove();
  }

  public void updateTrainStation(Train train, Station station) {
    // The previous station the train was Tat now has nothing in it
    // Update it accordingly
    Station toClear = TrainStationAt.get(train);
    StationTrainHolds.put(toClear, null);

    //Update the new station train is at to hold train, and vice versa
    StationTrainHolds.put(station, train);
    TrainStationAt.put(train, station);

    // If the new station is the last, train starts going left
    List<Station> stations = linesHolder.get(train);
    if (TrainStationAt.get(train).equals(stations.get(stations.size()-1))
            && TrainDirection.get(train) == true) {
      TrainDirection.put(train, false);
    } else if (TrainStationAt.get(train).equals(stations.get(0))
            && TrainDirection.get(train) == false) {
      TrainDirection.put(train, true);
    }
  }


  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    List<Station> stationList = new ArrayList<Station>();
    for (String station : stations) {
      stationList.add(Station.make(station));
    }
    Train t = Train.make(name);
    TrainStationAt.put(t, stationList.get(0));
    linesHolder.put(t, stationList);
    TrainDirection.put(t, true);
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    List<Station> stationList = new ArrayList<Station>();
    Queue<Station> stationQueue = new LinkedList<>();
    for (String station : stations) {
      Station newStation = Station.make(station);
      stationList.add(newStation);
      stationQueue.add(newStation);
    }
    Passenger newP = Passenger.make(name);
    nextStations.put(newP, stationQueue);
    PassengerstationAt.put(newP, nextStations.get(newP).remove());
    PassengersInJourney.add(newP);
    journeyHolder.put(newP, stationList);
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
    for (Train t : linesHolder.keySet()) {
      if (!TrainStationAt.get(t).equals(linesHolder.get(t).get(0))) {
        throw new RuntimeException("Failed at checkStart: Train t not at proper station");
      }
    }
    for (Passenger p : journeyHolder.keySet()) {
      if (!PassengerstationAt.get(p).equals(journeyHolder.get(p).get(0))) {
        throw new RuntimeException("failed at CheckStart: Passenger not at proper station");
      }
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
    for (Passenger p : journeyHolder.keySet()) {
      if(!checkPassengerAtStation(p, journeyHolder.get(p).get(journeyHolder.get(p).toArray().length-1))) {
        throw new RuntimeException("Failed at checkEnd: Passenger not at final station");
      }
    }
  }

  // reset to an empty simulation
  public void reset() {
    linesHolder.clear();
    journeyHolder.clear();
    PassengerstationAt.clear();
    TrainStationAt.clear();
    StationTrainHolds.clear();
    TrainDirection.clear();
    PassengersInJourney.clear();
    nextStations.clear();
    Passenger.clear();
    Station.clear();
    Train.clear();
  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {
    try {
      Gson gson = new Gson();
      Reader reader = Files.newBufferedReader(Paths.get(filename));

      //Need to send a json object instead of a reader. How tf do I create a json object???

      jsonConfigC configClass = gson.fromJson(reader, jsonConfigC.class);
      reader.close();
      for (String train : configClass.lines.keySet()) {
        addLine(train, configClass.lines.get(train));
      }
      for (String passenger : configClass.trips.keySet()) {
        addJourney(passenger, configClass.trips.get(passenger));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
