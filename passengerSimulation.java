import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class passengerSimulation implements Runnable {
    Passenger p;
    Station s;
    Log l;
    MBTA mbta;

    passengerSimulation(Passenger passenger, MBTA mbta, Log log) {
        this.p = passenger;
        this.mbta = mbta;
        this.s = null;
        this.l = log;
    };

    public void run() {
        try {
            while (mbta.PassengersInJourney.contains(p)) {
                runPassenger();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Trains will be synchronized based on which station they are at.
    private void runPassenger() throws InterruptedException {
        // If train in station takes passenger to next destination, board it
        Station currentStation = mbta.PassengerstationAt.get(p);
        Station DesiredStation = mbta.getNextPStation(p, currentStation);
        Lock templock;
        // Nao tem trem na estacao = dormindo
        // Tem trem na estacao, mas o trem nao leva ele ao nextStation = dormindo
        if (p.train == null) {
            templock = currentStation.passengerLock;
            templock.lock();
            while (mbta.StationTrainHolds.get(currentStation) == null ||
                    !mbta.checkStationInLine(mbta.StationTrainHolds.get(currentStation), DesiredStation)) {
                currentStation.passengerC.await();
            }
        } else {
            templock = DesiredStation.passengerLock;
            templock.lock();
            while (!mbta.checkTrainAtStation(p.train, DesiredStation)) {
                DesiredStation.passengerC.await();
            }
        }

        Train atStation = mbta.StationTrainHolds.get(currentStation);
        if (p.train == null && atStation != null) {
            mbta.PassengerstationAt.put(p, null); // tira o psg da estacao
            mbta.updatePassengerTrain(p, atStation);
            p.oldStation = currentStation;
            l.passenger_boards(p, atStation, currentStation);
        } else if (mbta.checkTrainAtStation(p.train, DesiredStation)) {
            mbta.updatePassengerStation(p, DesiredStation);
            l.passenger_deboards(p, p.train, DesiredStation);
            p.train = null;
            if (mbta.getLastPassengerStation(p).equals(DesiredStation)) {
                mbta.PassengersInJourney.remove(p);
                System.out.println(mbta.PassengersInJourney);
                System.out.println("MAMAE, CHEGUEI : " + p.toString());
            }
        }
        templock.unlock();
    }
}
