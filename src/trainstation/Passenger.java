package trainstation;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 23/11/15.
 * Credits to students Alessandro Luppi, Simone Sganzerla.
 */
public class Passenger extends Thread{

    private Station departure, arrival; // Departure and arrival stations
    private Train train; // Reference to the train I get in


    public Passenger(Station departure, Station arrival, String name){
        super(name);

        this.departure = departure;
        this.arrival = arrival;
        train = null;

    }

    public Train getTrain(){
        return train;
    }

    public void setTrain(Train train){
        this.train = train;
    }

    public void logln(String msg){
        System.out.println(getName()+": "+msg);
    }

    public void run(){
        try{
            departure.getOnTrain(this);
            arrival.getOffTrain(this); // this is called on the arrival trainstation
        }catch(InterruptedException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
