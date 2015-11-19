package bridge;

/**
 * Models a simple bridge. Cars are allowed to pass through it in one direction at a time.
 * This class, with respect to Bridge 3, sets a weight limit to the bridge. The limit is then 
 * not on the number of cars allowed on the bridge at the same time, rather on the total weight.
 *
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Bridge4 implements Bridge{

    private int currentDirection; // last known direction of cars on the bridge.
    private int totalWeight; // number of cars on the bridge.
    private int weightLimit;
    private int throughLimit;
    private int nThrough;
    private int waitingCars[]; // # of cars waiting to enter per direction

    public Bridge4(int weightLimit, int throughLimit){
        currentDirection = 0; // Initialization does not matter
        totalWeight = 0; // 0 cars on bridge, initially
        this.weightLimit = weightLimit;
        this.throughLimit = throughLimit;
        nThrough = 0;
        waitingCars = new int[2];

        System.out.println("Bridge #4");
    }

    public synchronized void enterBridge(Car c){
        // if there are cars and their direction is not the same as mine
        while((totalWeight!=0 && c.getDirection()!=currentDirection) || ((totalWeight+c.getWeight())>weightLimit) || (c.getDirection()==currentDirection && waitingCars[otherDir(c.getDirection())]>0 && nThrough>=throughLimit)){

            c.log("Conditions not met, I have to suspend ");

            if(c.getDirection()==currentDirection && (totalWeight+c.getWeight())>weightLimit)
                System.out.print("because of bridge weight limit, ");
            if(totalWeight>0 && c.getDirection()!=currentDirection)
                System.out.print("because of direction, ");
            if(waitingCars[otherDir(c.getDirection())]>0 && nThrough==throughLimit)
                System.out.print("because of number of passings, ");
            System.out.println();

            try {
                waitingCars[c.getDirection()]++;
                wait(); // suspend
                waitingCars[c.getDirection()]--;
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            c.logln("Someone has notified me");
        }

        if(currentDirection!=c.getDirection())
            nThrough=0;

        totalWeight += c.getWeight();
        nThrough++;
        currentDirection = c.getDirection();

        c.logln("Enters bridge");
        printBridgeStatus();
    }

    public synchronized void exitBridge(Car c){
        totalWeight -= c.getWeight();
        notifyAll();

        c.logln("Exits bridge");

        printBridgeStatus();
    }

    private void printBridgeStatus(){
        System.out.println("-----------");
        System.out.println("Wlimit:\t"+weightLimit);
        System.out.println("LimPas:\t"+throughLimit);
        System.out.println("Direct:\t"+currentDirection);
        System.out.println("#totW:\t"+totalWeight);
        System.out.println("#Pass:\t"+nThrough);
        System.out.println("#w_c0:\t"+waitingCars[0]);
        System.out.println("#w_c1:\t"+waitingCars[1]);
        System.out.println("-----------");
    }

    private int otherDir(int dir){
        if(dir==0)
            return 1;
        return 0;
    }

    public static void main(String[] args) {

        int weightLimit = 140;
        int throughLimit = 4;
        int nCars = 20;

        Bridge4 b = new Bridge4(weightLimit, throughLimit);
        for(int i=0; i<nCars; i++){
            new Car(b, "Car"+i, Math.random()>=0.5? 0: 1, (int)(Math.round(Math.random()*50))+1).start();
        }

    }

}
