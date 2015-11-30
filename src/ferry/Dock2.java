package ferry;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 26/11/15.
 * This class handles fifo-like queuing of cars.
 */
public class Dock2 implements Dock{

    private String name;
    private ArrayList<Car> waitingCars;
    private Ferry currentFerry;
    private long maxBoardingTime;


    public Dock2(String name, long maxBoardingTime){
        this.name = name;
        waitingCars = new ArrayList<Car>();
        currentFerry = null;
        this.maxBoardingTime = maxBoardingTime;
    }

    public String getDockName(){
        return name;
    }

    /**
     * Checks whether there is another car in the queue which arrived earlier(so, its index in the arraylist is lower)
     * can enter. In this case, returns false, otherwise it returns true.
     * @param car
     * @return
     */
    private boolean checkCarTurn(Car car){
        for(int i=0; i<waitingCars.indexOf(car); i++){
            Car temp = waitingCars.get(i);
            // if there is another object Car in the queue who can enter before Car car, return false
            if(currentFerry.canBoard(temp))
                return false;
        }
        return true;
    }

    /**
     * Used by cars to get on the ferry. No FIFO-like handling.
     * @param car
     */
    public synchronized void getOnFerry(Car car) throws InterruptedException{

        car.logln("wants to enter ferry at dock "+ getDockName());
        waitingCars.add(car);
        // the car need to wait if: 1) no ferry 2) can't board for weight or limit 3) could board, but not its turn
        while(currentFerry==null || !currentFerry.canBoard(car) || !checkCarTurn(car)){
            car.logln("can't board on ferry, wait");
            printStatus();
            wait();
            car.logln("been notified");
        }
        waitingCars.remove(car);
        car.logln("boards ferry "+currentFerry.getName()+", notifyAll");
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
        if(ferry.getNumberOfCars()!=0)
            ferry.logln("dropping off cars("+ferry.getNumberOfCars()+"), notifyAll");
        ferry.empty();
        // notifyAll to wake up the cars that may be waiting to board the ferry
        notifyAll(); // wakes up both the cars waiting to enter, both the cars on the ferry than want to get off
        long currentTime = System.currentTimeMillis();
        // if the ferry is docked for less than maxBoardingTime AND is not full(as soon as it becomes full ferry can leave, little optimization), wait
        while((System.currentTimeMillis()-currentTime) < maxBoardingTime && !ferry.isFull()){
            ferry.logln("waiting for cars");
            wait(200);
        }

        ferry.logln("--------> leaving dock "+getDockName());
        currentFerry = null;
    }

    public void printStatus(){
        synchronized (System.out) {
            if(waitingCars.size()>0) {
                System.out.println("----- "+getDockName()+" ["+(currentFerry==null? "No Ferry": currentFerry.getName())+"] -----");
                System.out.println("#waitingCars: "+waitingCars.size());
                System.out.print("| ");
                for (Car c : waitingCars)
                    System.out.print(" "+c.getName() + " |");
                System.out.println("");
            }
        }
    }

    public static void main(String args[]) throws InterruptedException {

        System.out.println("----- Dock v2 -----");

        Dock A = new Dock2("Livorno", 2000);
        Dock B = new Dock2("Olbia", 2000);

        Ferry ferry = new Ferry("Time Bandit", A, B, 200, 5);
        ferry.start();
        ArrayList<Car> cars = new ArrayList<>();

        // create cars
        for(int i=0; i<20; i++){
            if(Math.random()>=0.5)
                cars.add(new Car("Car#"+i, (int)(Math.random()*80)+10, A, B));
            else
                cars.add(new Car("Car#"+i, (int)(Math.random()*80)+10, B, A));
        }
        Collections.shuffle(cars);

        // start cars in separate threads
        for(Car c: cars){
            c.start();
            Thread.sleep((int)(Math.random()*200));
        }

        // wait for the cars to end, then exit
        for(Car c: cars)
            c.join();

        Thread.sleep(1000);
        System.exit(0);

    }

}
