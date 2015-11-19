package bridge;

public class Car extends Thread {

	private int direction;
	private Bridge bridge;
	private int weight;
	
	public Car(Bridge bridge, String name, int direction, int weight) {
		super(name);
		this.direction = direction;
		this.bridge = bridge;
		this.weight = weight;
	}
	
	/**
	 * Returns the car's direction: 0 or 1.
	 * @return car direction, 0 or 1;
	 */
	public int getDirection(){
		return direction;
	}
	
	public int getWeight(){
		return weight;
	}
	
	public void log(String msg){
		System.out.print(this.getName()+"[d:"+direction+"][w:"+weight+"]: "+msg);
	}
	public void logln(String msg){
		log(msg);
		System.out.println();
	}
	
	public void run(){
		bridge.enterBridge(this);
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		bridge.exitBridge(this);
	}
}
