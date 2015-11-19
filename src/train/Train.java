package train;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 18/11/15.
 */
public class Train extends Thread{

    private ArrayList<Station> stations;
    private int startingStationIndex;
    private int minPassengers; // minimum number of passengers required on the train before leaving the station
    private int maxPassengers; // maximum number of passengers a train can carry
    private int currentPassengers; // current number of passenger aboard the train
    private boolean canPassengersGetOff;

    public Train(ArrayList<Station> stations, int id, int minPassengers, int maxPassengers){
        super("Train#"+id);

        this.stations = stations;
        startingStationIndex = (int)Math.round(Math.random()*stations.size());

        currentPassengers = 0; // no passengers at the beginning

        this.minPassengers = minPassengers;
        this.maxPassengers = maxPassengers;
    }

    public void addPassenger(){
        currentPassengers++;
    }

    public void removePassenger(){
        currentPassengers--;
    }

    public boolean canLeave(){
        return (getCurrentPassengers()>=getMinPassengers());
    }

    public boolean getCanPassengersGetOff(){
        return canPassengersGetOff;
    }

    public void setCanPassengersGetOff(boolean value){
        canPassengersGetOff = value;
    }

    public int getCurrentPassengers(){
        return currentPassengers;
    }

    public int getMinPassengers(){
        return minPassengers;
    }

    public int getMaxPassengers(){
        return maxPassengers;
    }

    public boolean isFull(){
        return (getCurrentPassengers()>=getMaxPassengers());
    }

    public boolean isEmpty(){
        return getCurrentPassengers()==0;
    }

    public void logln(String msg){
        synchronized (System.out){ // synchronize the prints on the output
            System.out.println(getName()+": "+msg);
        }
    }

    public void printStatus(){
        synchronized (System.out){ // synchronize the prints on the output
            System.out.println("-------------");
            System.out.println(getName()+": Max: "+getMaxPassengers()+"\tMin: "+getMinPassengers()+"\tCur: "+getCurrentPassengers());
            System.out.println("-------------");
        }
    }

    public void run(){
        try{
            while(true){
                // for each station
                for(int i=startingStationIndex; i<stations.size()+startingStationIndex; i++){
                    int index = i % stations.size();
                    stations.get(index).pickUpPassengers(this);
                    Thread.sleep(500);
                    stations.get(index).dropPassengers(this);
                }
            }
        }catch (InterruptedException e){
            System.exit(-1);
        }
    }
}
