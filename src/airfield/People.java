package airfield;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 10/12/15.
 * Models different types of passengers: single passengers are modeled as groups with only one person, while groups
 * have at least two people.
 */
public class People extends Thread {

    private Airfield airfield;
    private Helicopter helicopter;
    private int numberOfPeople;

    public People(Airfield airfield, int id, int numberOfPeople){
        super(numberOfPeople>1? "Group#"+id : "Passenger#"+id);
        this.airfield = airfield;
        helicopter = null;
        this.numberOfPeople = numberOfPeople;
    }

    public Helicopter getHelicopter(){
        return helicopter;
    }

    public void setHelicopter(Helicopter helicopter){
        this.helicopter = helicopter;
    }

    public int getNumberOfPeople(){
        return numberOfPeople;
    }

    public void logln(String msg){
        System.out.println(getName()+"["+getNumberOfPeople()+"]: "+msg);
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
