package train;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 18/11/15.
 */
public class Passenger extends Thread {

    private Station station;
    private Train train;

    public Passenger(Station station, String name){
        super(name);

        this.station = station;
        this.train = null;
    }

    public void logln(String msg){
        synchronized (System.out){
            System.out.println(getName()+": "+msg);
        }
    }

    public boolean canIGetOff(){
        return train.getCanPassengersGetOff();
    }

    public Train getTrain(){
        return train;
    }

    public void setTrain(Train train){
        this.train = train;
    }

    public void run(){
        try{
            station.getOnTrain(this);
        }catch (InterruptedException e){
            System.exit(-1);
        }
    }

}
