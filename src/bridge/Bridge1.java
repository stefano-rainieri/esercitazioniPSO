package bridge;

/**
 * Models a simple bridge. Cars are allowed to pass through it in one direction at a time. No
 * limit to the number of cars on the bridge at the same time, nor to the number of consecutive
 * cars that can pass in a specific direction(to avoid starvation).
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Bridge1 implements Bridge{
	
	private int currentDirection; // last known direction of cars on the bridge.
	private int nCars; // number of cars on the bridge.
	
	public Bridge1(){
		currentDirection = 0; // Initialization does not matter
		nCars = 0; // 0 cars on bridge, initially
		
		System.out.println("Bridge #1");
	}
	
	public synchronized void enterBridge(Car c){
		// if there are cars and their direction is not the same as mine
		while(nCars!=0 && c.getDirection()!=currentDirection){
			c.logln("Not the same direction as cars on the bridge, suspend and wait");
			try {
				wait(); // suspend
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			c.logln("Someone notified me");
		}
		
		nCars++;
		currentDirection = c.getDirection();
		
		c.logln("Enters bridge");
		printBridgeStatus();
	}
	
	public synchronized void exitBridge(Car c){
		nCars--;
		notifyAll();
		c.logln("Exits bridge");
		printBridgeStatus();
	}
	
	private void printBridgeStatus(){
		System.out.println("-----------");
		System.out.println("#Cars:\t"+nCars);
		System.out.println(" Dir:\t"+currentDirection);
		System.out.println("-----------");
	}
	

	public static void main(String[] args) {
		
		Bridge1 b = new Bridge1();
		for(int i=0; i<10; i++){
			new Car(b, "Car"+i, Math.random()>=0.5? 0: 1, (int)(Math.round(Math.random()*50))+1).start();
		}
		
	}

}
