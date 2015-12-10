package airfield;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 10/12/15.
 */
public class Helicopter extends Thread {

    private Airfield airfield;
    private int maxPassengers;
    private ArrayList<Passenger> currentPassengers;

    public Helicopter(Airfield airfield, String name, int maxPassengers){
        super(name);

        this.maxPassengers = maxPassengers;
        this.airfield = airfield;
        currentPassengers = new ArrayList<>();
    }

    public void addPassengers(Passenger p){
        currentPassengers.add(p);
    }

    public int getNumberOfPassengers(){
        return currentPassengers.size();
    }

    public boolean isFull(){
        return currentPassengers.size()== maxPassengers;
    }

    public void logln(String msg){
        System.out.println(getName()+": "+msg);
    }

    public synchronized void dropOffPassengers(){
        notifyAll();
        for(Passenger p: currentPassengers)
            p.setHelicopter(null);
        currentPassengers.clear();
    }

    public void printStatus(){
        synchronized (System.out){
            System.out.println("----- "+getName()+" -----");
            System.out.println("MaxPass: "+ maxPassengers);
            System.out.println("Current: "+currentPassengers.size());
        }
    }

    public synchronized void waitForFlight(Passenger p) throws InterruptedException{
        p.logln("waiting on helicopter "+getName());
        while(p.getHelicopter()!=null){
            wait();
            p.logln("been notified");
        }
        p.logln("gets off helicopter "+getName()+", bye.");
    }

    public void run(){
        try{
            while(true) {
                airfield.boardPassengers(this);
                Thread.sleep((int) (Math.random() * 2000) + 1000);
                airfield.landAndDropOffPassengers(this);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
