package ferry;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 26/11/15.
 */
public class Car extends Thread {

    private int weight; // weight of the car
    private Dock departure, arrival;
    private Ferry ferry; // ferry the car boards on

    public Car(String name, int weight, Dock departure, Dock arrival){
        super(name+"["+weight+"]");
        this.weight = weight;
        this.departure = departure;
        this.arrival = arrival;

    }

    public void setFerry(Ferry f){
        this.ferry = f;
    }

    public Ferry getFerry(){
        return ferry;
    }

    public int getWeight(){
        return weight;
    }

    public void logln(String msg){
        System.out.println(getName()+": "+msg);
    }

    public void run(){
        try{

            departure.getOnFerry(this);
            arrival.getOffFerry(this);

        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
