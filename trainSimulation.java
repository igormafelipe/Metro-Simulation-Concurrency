import java.util.Set;

public class trainSimulation implements Runnable{
    final Train t;
    final MBTA mbta;
    final Log l;

    trainSimulation(Train train, MBTA mbta, Log log) {
        this.t = train;
        this.mbta = mbta;
        this.l = log;
    };

    public void run() {
        try {
            while (mbta.PassengersInJourney.size() > 0) {
                runTrain();
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Trains will be synchronized based on which station they are at.
    private void runTrain() throws InterruptedException {
        Station currentStation = mbta.TrainStationAt.get(t);
        Station nextStation = mbta.getNextTStation(t);

        nextStation.trainLock.lock();
        // Goes to sleep while the next station train has to move to is not empty
        while (mbta.StationTrainHolds.get(nextStation) != null) {
            nextStation.trainC.await();
        }
        currentStation.trainLock.lock();

        nextStation.passengerLock.lock();

        //If here, next station is empty and you have the lock. Make updates to mbta

        mbta.updateTrainStation(t, nextStation);
        l.train_moves(t, currentStation, nextStation);


        nextStation.trainC.signalAll();
        nextStation.trainLock.unlock();
        nextStation.passengerC.signalAll(); // Tells all passengers that train is here
        nextStation.passengerLock.unlock();
        currentStation.trainC.signalAll();
        currentStation.trainLock.unlock();
    }
}
