package bridge;


/**
 * Models a simple bridge. Cars are allowed to pass through it in one direction at a time.
 * This class, with respect to Bridge1, adds the constraint of a maximum number of cars on the bridge
 * at the same time.  
 * No limit to the number of consecutive cars that can pass in a specific direction(to avoid starvation).
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Bridge2 implements Bridge{
	
	private int currentDirection; // last known direction of cars on the bridge.
	private int nCars; // number of cars on the bridge.
	private int limit; // maximum number of cars that can be on bridge at the same time
	
	public Bridge2(int limit){
		currentDirection = 0; // Initialization does not matter
		nCars = 0; // 0 cars on bridge, initially
		this.limit = limit;
		
		System.out.println("Bridge #2");
	}
	
	public synchronized void enterBridge(Car c){
		// if there are cars and their direction is not the same as mine
		while(nCars!=0 && c.getDirection()!=currentDirection || nCars==limit){
			c.log("No conditions met, I have to suspend ");
			if(c.getDirection()==currentDirection && nCars==limit)
				System.out.println("because of bridge limit");
			else{
				System.out.println("because of direction");
			}
			try {
				wait(); // suspend
			} catch (InterruptedException e){
				e.printStackTrace();
			}
			c.logln("Someone has notified me");
		}
		
		nCars++;
		currentDirection = c.getDirection();
		
		c.logln("Enters bridge");
		printBridgeStatus();
	}
	
	public synchronized void exitBridge(Car c){
		nCars--;
		notifyAll();
		c.log("Exits bridge");
		printBridgeStatus();
	}
	
	private void printBridgeStatus(){
		System.out.println("-----------");
		System.out.println("Limit:\t"+limit);
		System.out.println("#Cars:\t"+nCars);
		System.out.println("Direct:\t"+currentDirection);
		System.out.println("-----------");
	}
	

	public static void main(String[] args) {
		
		int limit = 3;
		Bridge2 b = new Bridge2(limit);
		for(int i=0; i<10; i++){
			new Car(b, "Car"+i, Math.random()>=0.5? 0: 1, (int)(Math.round(Math.random()*50))+1).start();
		}
		
	}

}
