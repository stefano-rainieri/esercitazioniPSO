package trainstation;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 23/11/15.
 * Credits to STUDENT1, STUDENT2 etc.
 */
public class Station {

    private String name; // Name of the station
    private int waitingPassengers; // number of currently waiting passengers to get on the next available train
    private Train currentTrainOnPlatform; // reference to the current train in station

    public Station(String name){
        this.name = name;
        waitingPassengers = 0;
        currentTrainOnPlatform = null;
    }

    /**
     * Used by passengers to get on a train.
     * @param passenger the passengers who wants to get in a train
     * @throws InterruptedException
     */
    public synchronized void getOnTrain(Passenger passenger) throws InterruptedException{

        waitingPassengers++;
        passenger.logln("want to take train at station "+getStationName());
        // If no train OR there is one but it is already full(no free seats available), need to wait
        while(currentTrainOnPlatform==null || currentTrainOnPlatform.isFull()){
            if(currentTrainOnPlatform==null)
                passenger.logln("there is no train at station "+getStationName()+", wait");
            else
                passenger.logln("train "+currentTrainOnPlatform.getName()+" is already full, wait");
            printStationStatus();
            wait();
            passenger.logln("someone notified me");
        }
        passenger.logln("boards train "+currentTrainOnPlatform.getName());
        // set the train the passenger got on to the current train on platform
        currentTrainOnPlatform.addPassenger(passenger);
        waitingPassengers--;
        notifyAll(); // to wake up the train that is sleeping
        currentTrainOnPlatform.printStatus();
    }

    /**
     * Method called by the passengers on the ARRIVAL station. This means that they will wait on the arrival station
     * queue, and trains need to notifyAll on the arrival station.
     * @param passenger the passenger who wants to get off the train
     * @throws InterruptedException
     */
    public synchronized void getOffTrain(Passenger passenger) throws InterruptedException{
        // While i'm still on the train
        while(passenger.getTrain()!=null){
            passenger.logln("travelling on train " + passenger.getTrain().getName() + ", wait");
            wait();
            passenger.logln("someone notified me");
        }
        passenger.logln("get off train ");
    }

    /**
     * Used by the trains to load Passengers at the station.
     * @param train the train who wants to board the passengers.
     * @throws InterruptedException
     */
    public synchronized void boardPassengers(Train train) throws InterruptedException{
        train.logln("want to enter station "+getStationName());
        // if there is not a train loading passengers yet, wait
        while(currentTrainOnPlatform!=null){
            train.logln(currentTrainOnPlatform.getName()+" is already in station "+getStationName()+", wait");
            wait();
            train.logln("someone notified me");
        }
        train.logln("entering station "+getStationName()+", wait for passengers");
        // The train is in, so set the train itself as the current train on platform
        currentTrainOnPlatform = train;
        // notifyAll to wake up passengers that may be waiting
        notifyAll();
        // If the train has not reached the minimum number of passengers OR the train is not full and there are other passengers waiting, wait
        while(!train.hasMinPassengers() || (!train.isFull() && waitingPassengers>0)) {
            if(!train.hasMinPassengers())
                train.logln("not enough passengers, wait");
            else
                train.logln("waiting to load as many passengers as possible");
            wait();
            train.logln("someone notified me");
        }
        train.logln("leaving station "+getStationName());
        // notifyAll() to wake up trains willing to enter the station the train just left.
        notifyAll();
        // the platform is now free for another train that is willing to enter.
        currentTrainOnPlatform = null;
    }

    /**
     * Used by trains to drop off passengers.
     * @param train
     * @throws InterruptedException
     */
    public synchronized void dropPassengers(Train train) throws InterruptedException{
        train.logln("drop off passengers at station "+getStationName());
        // Drop off passengers. This method will se
        train.dropOffPassengers();
        notifyAll(); // To wake up waiting passengers on train
    }

    public String getStationName(){
        return name;
    }

    public void printStationStatus(){
        // syncing on System.out guarantees that these prints will be done together.
        synchronized (System.out) {
            System.out.println("----" + getStationName() + "----");
            System.out.println("curTrain: " + (currentTrainOnPlatform == null ? "no train" : currentTrainOnPlatform.getName()));
            System.out.println("#waiting: " + waitingPassengers);
        }
    }

    public static void main(String args[]){
        ArrayList<Station> stations = new ArrayList<Station>();
        stations.add(new Station("Station A"));
        stations.add(new Station("Station B"));

        for(int i=0; i<2; i++){
            new Train("Train#"+i, 2, 4, stations, (int)Math.random()*stations.size()).start();
        }

        for(int i=0; i<20; i++){
            if(Math.random()<0.5){
                new Passenger(stations.get(0), stations.get(1), "Passenger#"+i).start();
            }
            else{
                new Passenger(stations.get(1), stations.get(0), "Passenger#"+i).start();
            }

        }
    }

}
