package ferry;

import java.util.ArrayList;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 26/11/15.
 */
public class Ferry extends Thread {

    private int weightLimit; // Maximum total weight the ferry can bring
    private int carsLimit; // maximum number of cars the ferry can board
    private Dock current, next; // the 2 ferry stations the ferry goes back and forth in between
    private ArrayList<Car> boardedCars; // arraylist of the cars currently boarded on the ferry

    public Ferry(String name, Dock current, Dock next, int weightLimit, int carsLimit){
        super(name);
        this.weightLimit = weightLimit;
        this.carsLimit = carsLimit;

        this.current = current;
        this.next = next;

        boardedCars = new ArrayList<>();

    }

    /**
     * Adds a car to the ferry.
     * @param car
     */
    public void boardCar(Car car){
        boardedCars.add(car);
        car.setFerry(this);
    }

    /**
     * Returns a boolean value indicating if the car can board the ferry or not.
     * @param car the car willing to board.
     * @return true if the car can board, false otherwise.
     */
    public boolean canBoard(Car car){
        return !isFull() && (getCurrentTotalWeight()+car.getWeight())<=weightLimit;
    }

    /**
     * Empties the ferry.
     */
    public void empty(){
        for(Car car: boardedCars){
            car.setFerry(null);
        }
        boardedCars.clear();
    }

    /**
     * Returns the number of cars currently boarded on the ferry.
     * @return the number of cars boarded.
     */
    public int getCurrentTotalWeight(){
        int tot = 0;
        for(Car c: boardedCars){
            tot += c.getWeight();
        }
        return tot;
    }

    public int getNumberOfCars(){
        return boardedCars.size();
    }

    /**
     * Returns a boolean value indicating if the ferry reached its maximum capacity in terms on number of cars.
     * @return true is the number of cars has reached the limit, false otherwise.
     */
    public boolean isFull(){
        return boardedCars.size()==carsLimit;
    }

    public void printStatus(String dockName){
        synchronized (System.out){
            System.out.println("----- Ferry: "+getName()+" - "+dockName+" -----");
            System.out.println("curCars:\t"+boardedCars.size());
            System.out.println("carsLimit:\t"+carsLimit);
            System.out.println("curWeight:\t"+getCurrentTotalWeight());
            System.out.println("weigLimit:\t"+weightLimit);
        }
    }


    public void logln(String msg){
        System.out.println(getName()+": "+msg);
    }

    public void run(){
        Dock temp = current;
        while(true) {
            try {

                current.boardPassengers(this);
                Thread.sleep(500); // time to navigate
                current = next;
                next = temp;
                temp = current;

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
