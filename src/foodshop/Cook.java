package foodshop;

/**
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Cook extends Thread {

	private Restaurant restaurant; // reference to the restaurant
	
	
	public Cook(Restaurant restaurant, String name) {
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
				restaurant.prepareFood(this);
				Thread.sleep((long)(Math.random()*500));
				restaurant.serveFood(this);
				Thread.sleep((long)(Math.random()*500));
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}

}
