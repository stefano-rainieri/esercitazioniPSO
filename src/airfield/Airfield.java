package airfield;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 10/12/15.
 */
public class Airfield {

    private String name;
    private Helicopter currentHelicopter; // reference if the currentHelicopter is in the airfield waiting for passengers.
    private ArrayList<Passenger> waitingPassengers;
    private long maxWaitingTime;
    private int queueNumber;

    public Airfield(String name, long maxWaitingTime){
        this.name = name;
        this.currentHelicopter = null;
        this.maxWaitingTime = maxWaitingTime;
        waitingPassengers = new ArrayList<>();
        queueNumber = 0;

    }

    private String getName(){
        return name;
    }

    private boolean canBoard(Passenger passenger){
        // If i'm not the first of the queue (which means it is my turn to board), return false
        if(!currentHelicopter.isFull() && waitingPassengers.size()>0 && waitingPassengers.get(0)!=passenger)
            return false;

        return true;
    }


    /*
    NOTE: BOARDING SINGLE PASSENGERS AND GROUPS COULD BE DONE BETTER, USING SOME ABSTRACT CLASSES AND/OR INTERFACES TO
    MODEL THEM AND USING THEN A SINGLE METHOD, WHICH WOULD BE SOMETHING LIKE:
    public synchronized void boardHelicopter(AbstractPassenger passenger);
      */

    /**
     * Used by SINGLE passengers to board the currentHelicopter.
     * @param passenger the passenger who wants to board.
     * @throws InterruptedException
     */
    public synchronized Helicopter boardHelicopter(Passenger passenger) throws InterruptedException{
        passenger.setQueueNumber(queueNumber++); // assign a number to the passenger(this is actually used only for printing purposes)

        passenger.logln("wants to board.");
        waitingPassengers.add(passenger);
        // wait if: 1) no currentHelicopter 2) there are waiting groups that can board
        while(currentHelicopter ==null || !canBoard(passenger)){
            passenger.logln("can't board, wait.");
            wait();
            passenger.logln("been notified.");
        }
        waitingPassengers.remove(passenger);

        passenger.logln("boards helicopter "+ currentHelicopter.getName());
        currentHelicopter.addPassengers(passenger);
        passenger.setHelicopter(currentHelicopter);
        currentHelicopter.printStatus();
        notifyAll(); // wake up the helicopter

        return currentHelicopter;
    }

    public synchronized void boardPassengers(Helicopter helicopter) throws InterruptedException {

        this.currentHelicopter = helicopter;
        currentHelicopter.printStatus();

        notifyAll(); // wake up waiting passengers
        helicopter.logln("waiting for passengers.");

        long landingTime = System.currentTimeMillis();
        while(helicopter.getNumberOfPassengers()==0 || !helicopter.isFull() && (System.currentTimeMillis()-landingTime)<=maxWaitingTime){

            helicopter.logln("needs to wait.");
            wait(500);
            helicopter.logln("been notified.");
        }

        this.currentHelicopter = null;
        helicopter.logln("takes off from airfield "+getName());

    }

    public synchronized void landAndDropOffPassengers(Helicopter helicopter) throws InterruptedException{

        helicopter.logln("landing at airfield "+getName()+", dropping off passengers");
        helicopter.dropOffPassengers();


    }

    public static void main(String args[]) throws InterruptedException{

        int nPassengers = 20;
        long maxWaitingTime = 1000;

        Airfield airfield = new Airfield("Campovolo", maxWaitingTime);

        Helicopter he = new Helicopter(airfield, "Helicopter", 6);
        he.start();

        ArrayList<Passenger> passengers = new ArrayList<>();
        for(int i=0; i<nPassengers; i++){
            passengers.add(new Passenger(airfield, "Passenger#"+i));
        }

        for(Passenger p: passengers) {
            p.start();
            Thread.sleep((int)(Math.random()*maxWaitingTime));
        }

        for(Passenger p: passengers) {
            p.join();
        }

        Thread.sleep(3000);
        System.exit(0);
    }

}
