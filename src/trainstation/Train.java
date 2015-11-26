package trainstation;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 23/11/15.
 */
public class Train extends Thread {

    private ArrayList<Station> stations;
    private int startingIndex;
    private int minPassengers, maxPassengers;
    private int currentPassengers;
    private ArrayList<Passenger> boardedPassengers; // The list of boarded passengers

    /**
     * Constructor of a Train.
     * @param name name of the train
     * @param minPassengers minimum amount of passengers the train need in order to leave
     * @param maxPassengers maximum number of passengers the train can carry
     * @param stations reference to a list of stations
     * @param startingStation index of the starting station in the list
     */
    public Train(String name, int minPassengers, int maxPassengers, ArrayList<Station> stations, int startingStation){
        super(name);
        this.stations = stations;

        if(startingStation<0 || startingStation>=stations.size()){
            startingIndex = (int)Math.random()*stations.size();
        }

        this.minPassengers = minPassengers;
        this.maxPassengers = maxPassengers;
        this.currentPassengers = 0;
        boardedPassengers = new ArrayList<>();
    }

    /**
     * This method is used to add a boarder passenger to this train.
     * @param p the passenger that boards this train
     */
    public void addPassenger(Passenger p){
        currentPassengers++;
        boardedPassengers.add(p);
        p.setTrain(this);
    }

    /**
     * Resets number of passengers on train to 0 and sets boarded passenger's train references to null.
     */
    public void dropOffPassengers(){
        currentPassengers = 0;
        for(Passenger p: boardedPassengers) {
            // Sets the references to the train of the passengers I boarded to null, so they will know they have to get off
            p.setTrain(null);
        }
        boardedPassengers.clear(); // remove all the references on trains
    }

    public void printStatus(){
        // syncing on System.out guarantees that these prints will be done together.
        synchronized (System.out) {
            System.out.println("----" + getName() + "----");
            System.out.println("#cur: " + currentPassengers + "\tmin: " + minPassengers + "\t max: " + maxPassengers);
        }
    }

    public boolean isFull(){
        return getCurrentPassengers()>=maxPassengers;
    }

    public boolean hasMinPassengers(){
        return getCurrentPassengers()>=minPassengers;
    }

    public int getCurrentPassengers(){
        return currentPassengers;
    }

    public void logln(String msg){
        System.out.println(getName()+": "+msg);
    }

    public void run(){
        int next = startingIndex;

        while(true){
            try{
                int current = next;
                stations.get(current).boardPassengers(this);
                Thread.sleep((long)(Math.random()*500+100));
                next = (current+1)%stations.size();
                stations.get(next).dropPassengers(this);
            }catch(InterruptedException e){
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

}
