package ferry;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 26/11/15.
 * This class do not handle any particular priority (eg. FIFO).
 */
public class Dock1 implements Dock{

    private String name;
    private int waitingCars;
    private Ferry currentFerry;
    private long maxBoardingTime;


    public Dock1(String name, long maxBoardingTime){
        this.name = name;
        waitingCars = 0;
        currentFerry = null;
        this.maxBoardingTime = maxBoardingTime;
    }

    public String getDockName(){
        return name;
    }

    /**
     * Used by cars to get on the ferry. No FIFO-like handling.
     * @param car
     */
    public synchronized void getOnFerry(Car car) throws InterruptedException{

        car.logln("wants to enter ferry at dock "+ getDockName());
        waitingCars++;
        while(currentFerry==null || !currentFerry.canBoard(car)){
            car.logln("can't board on ferry, wait");
            wait();
            car.logln("been notified");
        }
        waitingCars--;
        car.logln("boards ferry "+currentFerry.getName());
        // car boarded the ferry
        currentFerry.boardCar(car);
        // notifyAll to wake up the ferry
        notifyAll();
        currentFerry.printStatus(this);
        printStatus();
    }

    /**
     * Used by cars to get off ferry, called on the arrival dock.
     * @param car
     */
    public synchronized void getOffFerry(Car car) throws InterruptedException{
        // while the ferry has not arrived is still
        while(car.getFerry()!=null){
            car.logln("travelling on ferry, wait");
            wait();
            car.logln("been notified");
        }
        car.logln("got off ferry, bye");
    }

    /**
     * Used by ferries to board cars.
     * @param ferry the ferry that want to board cars.
     * @throws InterruptedException
     */
    public synchronized void boardPassengers(Ferry ferry) throws InterruptedException{
        ferry.logln("entering dock "+getDockName());
        // no need to check whether there is already a ferry, because there is only one.
        currentFerry = ferry;
        // drop off passengers first.
        ferry.logln("dropping off cars("+ferry.getNumberOfCars()+")");
        ferry.empty();
        // notifyAll to wake up the cars that may be waiting to board the ferry
        notifyAll(); // wakes up both the cars waiting to enter, both the cars on the ferry than want to get off
        long currentTime = System.currentTimeMillis();
        // if the ferry is docked for less than maxBoardingTime AND is not full(as soon as it becomes full ferry can leave, little optimization), wait
        while((System.currentTimeMillis()-currentTime) < maxBoardingTime && !ferry.isFull()){
            ferry.logln("waiting for cars");
            wait(200);
            ferry.logln("someone notified me");
        }

        ferry.logln("leaving dock");
        currentFerry = null;
    }

    public void printStatus(){
        synchronized (System.out) {
            System.out.println("----- "+getDockName()+" -----");
            System.out.println("waiting: "+waitingCars);
        }
    }

    public static void main(String args[]) throws InterruptedException {

        Dock A = new Dock1("Livorno", 500);
        Dock B = new Dock1("Olbia", 500);

        Ferry ferry = new Ferry("Time Bandit", A, B, 200, 5);
        ferry.start();
        ArrayList<Car> cars = new ArrayList<>();

        for(int i=0; i<20; i++){
            if(Math.random()>=0.5)
                cars.add(new Car("Car#"+i, (int)(Math.random()*50)+10, A, B));
            else
                cars.add(new Car("Car#"+i, (int)(Math.random()*50)+10, B, A));
        }
        Collections.shuffle(cars);

        for(Car c: cars){
            c.start();
            Thread.sleep((int)(Math.random()*50));
        }

        for(Car c: cars)
            c.join();

        Thread.sleep(1000);
        System.exit(0);

    }

}
