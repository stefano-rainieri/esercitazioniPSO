package airfield;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 10/12/15.
 */
public class Airfield {
    private String name; // name of the airfield
    private Helicopter currentHelicopter; // reference if the currentHelicopter is in the airfield waiting for passengers.
    private ArrayList<People> waitingPassengers; // waiting queue for single passengers
    private ArrayList<People> waitingGroups; // waiting queue for groups
    private long maxWaitingTime; // max time the helicopter is allowed to wait

    public Airfield(String name, long maxWaitingTime){
        this.name = name;
        this.currentHelicopter = null;
        this.maxWaitingTime = maxWaitingTime;
        waitingPassengers = new ArrayList<>();
        waitingGroups = new ArrayList<>();

    }

    private String getName(){
        return name;
    }

    /**
     * Used by people to know if they can board. This method accounts both for passengers number and priority
     * @param people
     * @return
     */
    private boolean canBoard(People people){
        // if the helicopter is full or entering the helicopter would exceed the maximum, you can't board
        if(currentHelicopter.isFull() ||
                currentHelicopter.getNumberOfPassengers() + people.getNumberOfPeople() > currentHelicopter.getMaxPassengers()) {
            return false;
        }
        // if here, people may enter (talking about number of passengers), check priority

        for (People g: waitingGroups) {
            // If group g can board, group people can board only if group==g
            if(g.getNumberOfPeople()+currentHelicopter.getNumberOfPassengers()<=currentHelicopter.getMaxPassengers()) {
                return g == people;
            }
        }

        if(waitingPassengers.get(0)!=people) {
            return false;
        }
        return true;
    }


    /**
     * Used by people to board the currentHelicopter.
     * @param people the people who wants to board.
     * @throws InterruptedException
     */
    public synchronized Helicopter boardHelicopter(People people) throws InterruptedException{

        people.logln("wants to board.");

        if(people.getNumberOfPeople()>1)
            waitingGroups.add(people);
        else
            waitingPassengers.add(people);

        // while not conditions met, wait to board
        while(currentHelicopter ==null || !canBoard(people)){
            people.logln("can't board, wait.");
            wait();
            people.logln("been notified.");
        }

        if(people.getNumberOfPeople()>1)
            waitingGroups.remove(people);
        else
            waitingPassengers.remove(people);

        people.logln("boards helicopter "+ currentHelicopter.getName());
        currentHelicopter.addPassengers(people);
        people.setHelicopter(currentHelicopter);
        currentHelicopter.printStatus();
        notifyAll(); // wake up the helicopter

        return currentHelicopter;
    }

    /**
     * Used by the helicopter to board the passengers. The heli waits until it is full or there is at least one passenger
     * and a certain amount of time has passed.
     * @param helicopter
     * @throws InterruptedException
     */
    public synchronized void boardPassengers(Helicopter helicopter) throws InterruptedException {

        printStatus();

        this.currentHelicopter = helicopter;
        currentHelicopter.printStatus();

        notifyAll(); // wake up waiting passengers
        helicopter.logln("waiting for people.");

        long landingTime = System.currentTimeMillis();
        while(helicopter.getNumberOfPassengers()==0 || !helicopter.isFull() && (System.currentTimeMillis()-landingTime)<=maxWaitingTime){

            helicopter.logln("needs to wait.");
            wait(500);
            helicopter.logln("been notified.");
        }

        this.currentHelicopter = null;
        helicopter.logln("takes off from airfield "+getName());
    }

    private void printStatus(){
        synchronized (System.out){
            System.out.println("----- "+getName()+" -----");
            System.out.print("Waiting Groups: ");
            for(People g: waitingGroups)
                System.out.print(g.getName()+",");

            System.out.println();
            System.out.print("Waiting Passen: ");
            for(People g: waitingPassengers)
                System.out.print(g.getName()+",");
            System.out.println();
        }
    }

    /**
     * Used by the helicopter to land and drop off the boarded passengers.
     * @param helicopter
     * @throws InterruptedException
     */
    public synchronized void landAndDropOffPassengers(Helicopter helicopter) throws InterruptedException{

        helicopter.logln("landing at airfield "+getName()+", dropping off people");
        helicopter.dropOffPassengers();
    }

    public static void main(String args[]) throws InterruptedException{

        int nPassengers = 10;
        long maxWaitingTime = 1000;
        int maxPassengers = 6;

        Airfield airfield = new Airfield("Airfield", maxWaitingTime);

        Helicopter he = new Helicopter(airfield, "Helicopter", maxPassengers);
        he.start();


        int groupsC = 0;
        int passenC = 0;
        ArrayList<People> peoples = new ArrayList<>();
        for(int i=0; i<nPassengers/2; i++){
            int n =  2+(int)(Math.random()*(maxPassengers-1));
            peoples.add(new People(airfield, groupsC++, n));
        }
        for(int i=0; i<nPassengers/2; i++){
            peoples.add(new People(airfield, passenC++, 1));
        }

        for(People p: peoples) {
            p.start();
            //Thread.sleep((int)(Math.random()*maxWaitingTime));
        }

        for(People p: peoples) {
            p.join();
        }

        Thread.sleep(3000);
        System.exit(0);
    }

}
