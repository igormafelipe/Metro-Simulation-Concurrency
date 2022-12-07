import java.io.*;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    List<Thread> threadList = new ArrayList<>();
    for (Train t : mbta.linesHolder.keySet()) {
      threadList.add(new Thread(new trainSimulation(t, mbta, log), t.toString()));
    }
    for (Passenger p : mbta.journeyHolder.keySet()) {
      threadList.add(new Thread(new passengerSimulation(p, mbta, log), p.toString()));
    }
    for (Passenger p : mbta.journeyHolder.keySet()) {
      System.out.println(p.toString() + "  " + mbta.journeyHolder.get(p));
    }
    for (Train t : mbta.linesHolder.keySet()) {
      System.out.println(t.toString() + "  " + mbta.linesHolder.get(t));
    }
    for (Thread t : threadList) {
      t.start();
    }
    try {
      for (Thread t : threadList) {
        t.join();
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}