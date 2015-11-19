package foodshop;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 18/11/15.
 */
public class DeliveryMan extends Thread {

    private Restaurant3 restaurant;

    public DeliveryMan(Restaurant3 restaurant, String name){
        super(name);

        this.restaurant = restaurant;

    }

    public void log(String msg){
        System.out.print(getName()+": "+msg);
    }

    public void logln(String msg){
        log(msg);
        System.out.println();
    }

    public void run(){
        while(true){
            try {
                restaurant.getReadyFood(this);
                Thread.sleep((long)(Math.random()*500));
                restaurant.deliverFood(this);
                Thread.sleep((long)(Math.random()*500));
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

}
