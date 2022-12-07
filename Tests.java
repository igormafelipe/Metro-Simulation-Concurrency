import static org.junit.Assert.*;
import org.junit.*;

public class Tests {
  @Test public void testPass() {
    assertTrue("true should be true", true);
  }
  @Test public void testTwoLines() {
    MBTA mbta = new MBTA();
    Log log = new Log();

    mbta.loadConfig("sample2.json");

    Passenger p = Passenger.make("Alan");
    Train t = Train.make("Tufts");
    Train t2 = Train.make("Uphill");
    Station s1 = Station.make("Halligan");
    Station s2 = Station.make("SEC");
    Station s3 = Station.make("Pearson");
    Station s4 = Station.make("Cummings");
    Station s5 = Station.make("Eaton");
    Station s6 = Station.make("Barnum");
    Station s7 = Station.make("Braker");
    log.passenger_boards(p, t, s1);
    log.train_moves(t, s1, s2);
    log.passenger_deboards(p, t, s2);
    log.train_moves(t, s2, s3);
    log.train_moves(t2, s5, s2);
    log.train_moves(t, s3, s4);
    log.passenger_boards(p, t2, s2);
    log.train_moves(t, s4, s3);
    log.train_moves(t2, s2, s6);
    log.train_moves(t2, s6, s7);
    log.train_moves(t, s3, s2);
    log.passenger_deboards(p, t2, s7);
    Verify.verify(mbta, log);
  }
}
