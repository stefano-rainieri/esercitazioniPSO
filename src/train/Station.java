package train;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 18/11/15.
 * This version does not provide FIFO-like queueing for waiting passengers.
 * This version allows trains to drop off passengers even if a train is already in the station (like if there
 * were 2 platforms, one for dropping off, and one for picking up)
 */
public class Station {

    private String name; // name of the station
    private Train currentTrainInStation; // train currently waiting for passengers in this station

    public Station(String name){
        this.name = name;
        currentTrainInStation = null;
    }

    public String getStationName(){
        return name;
    }

    /**
     * Used by trains to enter the station.
     * @param train the train which wants to enter the station
      */
    public synchronized void pickUpPassengers(Train train) throws InterruptedException{
        train.logln("want to enter in station "+getStationName());
        // Until there is a train in the station, need to wait
        while(currentTrainInStation !=null){
            train.logln("there is already a train waiting at station's platform, wait()");
            wait();
            train.logln("someone notified me");
        }

        while(!train.isEmpty()){
            train.logln("wait until I have dropped off all the passengers");
            wait();
            train.logln("someone notified me");
        }

        currentTrainInStation = train;
        currentTrainInStation.setCanPassengersGetOff(false);
        train.logln("entered station "+getStationName()+", notifyAll()");
        // The train has entered the station and is now waiting at the platform, need to wait for passengers.
        notifyAll(); // necessary, passengers may be waiting on the platform
        while(!train.canLeave()){
            train.logln("not enough passengers, wait()");
            train.printStatus();
            wait(); // Wait for passengers to get on the train.
            train.logln("someone notified me");
        }

        train.logln("leaving from "+getStationName()+", notifyAll()");
        currentTrainInStation = null; // another train can enter
        notifyAll(); // need to notify other trains they can enter
    }

    /**
     * Used by passengers to get on the train.
     * @param passenger the passenger that wants to get on the train.
     * @throws InterruptedException
     */
    public synchronized void getOnTrain(Passenger passenger) throws InterruptedException{
        passenger.logln("want to get the train at station "+getStationName());
        // If there is no train in the station, or the train is full, we have to wait.
        while(currentTrainInStation == null || currentTrainInStation.isFull()){
            if(currentTrainInStation == null)
                passenger.logln("there is no ready train at station "+getStationName()+", wait()");
            else
                passenger.logln("the train "+ currentTrainInStation.getName()+" is here, but is full, wait()");
            wait();
            passenger.logln("someone notified me");
        }

        passenger.setTrain(currentTrainInStation);
        passenger.getTrain().addPassenger();

        passenger.logln("got on train "+ currentTrainInStation.getName()+". notifyAll() and wait()");
        currentTrainInStation.printStatus();
        // The passenger got on the train
        notifyAll(); // Notify all that i got on the train(wakes up both trains and passengers waiting)
        // While i'm not at destination, I need to wait
        while(!passenger.canIGetOff()){
            passenger.logln("not at destination, wait()");
            wait(); // The passenger has to wait for the train to leave and reach the next station
            passenger.logln("someone notified me");
        }
        passenger.logln("got off train "+passenger.getTrain().getName());
        passenger.getTrain().removePassenger();
        notifyAll();
    }

    /**
     * Used to unload passengers picked up at this station. The semantics of this method is not to drop of passengers
     * at this station.
     * @param train train the passengers have to be dropped off from.
     */
    public synchronized void dropPassengers(Train train){

        train.setCanPassengersGetOff(true);
        train.logln("dropping off passengers");
        //train.empty(); // empty the train
        notifyAll(); // wakes up all the passengers that

    }

    public static void main(String args[]){

        int min = 2;
        int max = 3;

        ArrayList<Station> stations = new ArrayList<Station>();
        stations.add(new Station("Station A"));
        stations.add(new Station("Station B"));


        for(int i=0; i<2; i++){
            new Train(stations, i, min, max).start();
        }

        for(int i=0; i<10; i++){
            new Passenger(stations.get((int)Math.floor(Math.random()*stations.size())), "Passenger#"+i).start();
        }
    }
}
