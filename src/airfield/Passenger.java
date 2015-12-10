package airfield;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 10/12/15.
 */
public class Passenger extends Thread {

    private Airfield airfield;
    private int queueNumber;
    private Helicopter helicopter;


    public Passenger(Airfield airfield, String name){
        super(name);

        this.airfield = airfield;
        queueNumber = -1;
        helicopter = null;
    }

    public Helicopter getHelicopter(){
        return helicopter;
    }

    public void setHelicopter(Helicopter helicopter){
        this.helicopter = helicopter;
    }

    public int getQueueNumber(){
        return queueNumber;
    }

    public void setQueueNumber(int number){
        this.queueNumber = number;
    }

    public void logln(String msg){
        System.out.println(getName()+"["+getQueueNumber()+"]: "+msg);
    }


    public void run(){
        try {

            Helicopter he = airfield.boardHelicopter(this);
            he.waitForFlight(this);


        }catch(InterruptedException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
