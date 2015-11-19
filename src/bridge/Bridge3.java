package bridge;


/**
 * Models a simple bridge. Cars are allowed to pass through it in one direction at a time.
 * This class, with respect to Bridge 2, sets a limit to the number of consecutive cars that
 * can pass in a specific direction(to avoid starvation).
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Bridge3 implements Bridge{
	
	private int currentDirection; // last known direction of cars on the bridge.
	private int nCars; // number of cars on the bridge.
	private int limit;
	private int throughLimit;
	private int nThrough;
	private int waitingCars[];
	
	public Bridge3(int limit, int throughLimit){
		currentDirection = 0; // Initialization does not matter
		nCars = 0; // 0 cars on bridge, initially
		this.limit = limit;
		this.throughLimit = throughLimit;
		nThrough = 0;

		waitingCars = new int[2];
		
		System.out.println("Bridge #4");
		
	}
	
	public synchronized void enterBridge(Car c){
		// if there are cars and their direction is not the same as mine
		while((nCars!=0 && c.getDirection()!=currentDirection) || (nCars==limit) || (c.getDirection()==currentDirection && waitingCars[otherDir(c.getDirection())]>0 && nThrough>=throughLimit)){
			
			c.log("Conditions not met, I have to suspend ");
			
			if(c.getDirection()==currentDirection && nCars==limit)
				System.out.print("because of bridge limit, ");
			if(nCars>0 && c.getDirection()!=currentDirection)
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
		
		nCars++;
		nThrough++;
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
		System.out.println("Limit:\t"+limit);
		System.out.println("LimPas:\t"+throughLimit);
		System.out.println("Direct:\t"+currentDirection);
		System.out.println("#Cars:\t"+nCars);
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
		
		int limit = 3;
		int throughLimit = 4;
		int nCars = 20;
		
		Bridge3 b = new Bridge3(limit, throughLimit);
		for(int i=0; i<nCars; i++){
			new Car(b, "Car"+i, Math.random()>=0.5? 0: 1, (int)(Math.round(Math.random()*50))+1).start();
		}
		
	}

}
