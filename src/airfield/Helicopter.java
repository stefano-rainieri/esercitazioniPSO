package airfield;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 10/12/15.
 */
public class Helicopter extends Thread {

    private Airfield airfield;
    private int maxPassengers;
    private ArrayList<People> currentPeople;

    public Helicopter(Airfield airfield, String name, int maxPassengers){
        super(name);

        this.maxPassengers = maxPassengers;
        this.airfield = airfield;
        currentPeople = new ArrayList<>();
    }

    public void addPassengers(People p){
        currentPeople.add(p);
    }

    public int getNumberOfPassengers(){
        int n = 0;
        for(People p: currentPeople)
            n += p.getNumberOfPeople();
        return n;
    }

    public int getMaxPassengers(){
        return maxPassengers;
    }

    public boolean isFull(){
        return getNumberOfPassengers() == maxPassengers;
    }

    public void logln(String msg){
        System.out.println(getName()+": "+msg);
    }

    /**
     * Used by the helicopter to board the passengers
     */
    public synchronized void dropOffPassengers(){
        notifyAll();
        for(People p: currentPeople)
            p.setHelicopter(null);
        currentPeople.clear();
    }

    public void printStatus(){
        synchronized (System.out){
            System.out.println("----- "+getName()+" -----");
            System.out.println("MaxPass: "+ maxPassengers);
            System.out.println("Current: "+ getNumberOfPassengers());
            System.out.print("Boarded people: ");
            for(People p: currentPeople)
                System.out.print(p.getName()+", ");
            System.out.println();
            System.out.println();
        }
    }

    /**
     * Used by passengers to wait on the helicopter queue. The helicopter needs to call notifyAll on itself to wake
     * up the passengers
     * @param p
     * @throws InterruptedException
     */
    public synchronized void waitForFlight(People p) throws InterruptedException{
        p.logln("waiting on helicopter "+getName());
        //while(p.getHelicopter()!=null){
        if(p.getHelicopter()!=null){
            wait();
            p.logln("been notified");
        }
        p.logln("gets off helicopter "+getName()+", bye.");
    }

    public void run(){
        try{
            while(true) {
                airfield.boardPassengers(this);
                //Thread.sleep((int) (Math.random() * 2000) + 1000);
                Thread.sleep(2000);
                airfield.landAndDropOffPassengers(this);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
