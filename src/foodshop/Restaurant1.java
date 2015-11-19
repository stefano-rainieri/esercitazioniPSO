package foodshop;

/**
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Restaurant1 implements Restaurant{

	private Customer serving; // reference to the customer currently being served.
	
	public Restaurant1(){
		serving=null; // no customer is served initially
		System.out.println("Restaurant #1. Customers are not served in any particular order.");
		System.out.println();
	}

	public synchronized void orderFood(Customer customer) throws InterruptedException {
		customer.logln("want to order, is the cook free?");
		// Until the cook is not available, wait
		while(serving!=null){
			customer.logln("the cook is not free, he is serving "+serving.getName()+". Wait for him.");
			wait();
			customer.logln("someone notified me("+(serving!=null? serving.getName(): "Cook")+")");
		}
		// The cook may be waiting, need to notify him
		customer.logln("the cook is free, notifyAll and wait for the food.");
		serving = customer; // I'm the customer who is being served right now.
		
		notifyAll(); // notifies every thread waiting, but only the cook will proceed because we've set serving=customer
		wait(); // wait (the cook needs time to prepare my food, and I need to wait him)
		
		customer.logln("I have my food, bye.");
	}

	public synchronized void prepareFood(Cook cook) throws InterruptedException {
		// If I'm not serving anyone
		if(serving==null){
			cook.logln("I'm not serving anyone, wait.");
			wait(); // Wait for a customer to notify me he wants food.
			cook.logln("someone notified me ("+serving.getName()+")");
		}
		cook.logln("serving "+serving.getName());
	}
  
	public synchronized void serveFood(Cook cook) throws InterruptedException {
		cook.logln("finished serving "+serving.getName()+". I'm free again, notifyAll.");
		serving = null; // I'm gonna set myself free, so another customer can be served.
		
		notifyAll(); // Notify everyone, but only the first notified customer thread will find serving==null.
	}


	public static void main(String args[]){

		int nCustomers = 5;
		Restaurant1 fs = new Restaurant1();
		new Cook(fs, "Cook").start();

		for(int i=0; i<nCustomers; i++){
			new Customer(fs, "Customer#"+i).start();
		}

	}

}
