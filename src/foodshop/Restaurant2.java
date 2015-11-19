package foodshop;

/**
 * This models the waiting customers of the restaurant as a FIFO queue,
 * allowing a fair access(fifo-based) to the cook among the customers.
 * 
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Restaurant2 implements Restaurant{

	private Customer serving; // reference to the customer currently being served.
	private int queueNumber; // incremental number given to the customers as soon as they get into orderFood, in order to provide a FIFO-like serving mechanism.
	private int servingNumber; // current number of the customer being served(servingNumber+1) is the next one that needs to be served
	
	public Restaurant2(){
		serving=null; // no customer is served initially
		queueNumber = 0;
		servingNumber = -1; // -1 stands for a user who was not given any number
		
		System.out.println("Restaurant #2. Customers are served using a FIFO-like (First In First Out) scheme.");
		System.out.println();
	}

	public synchronized void orderFood(Customer customer) throws InterruptedException {
		customer.setQueueNumber(queueNumber); // The customer will be served with this number(0, initially)
		queueNumber++; // next customer will have the next number
		
		customer.logln("want to order, is the cook free?");
		// Until the cook is not available, or it is not my turn to be server
		while(serving!=null || (servingNumber+1)!=customer.getQueueNumber()){
			if(serving!=null)
				customer.logln("the cook is not free, he is serving "+serving.getName()+". Wait for him.");
			else
				customer.logln("it is not my turn. I have queueNumber "+customer.getQueueNumber()+", current is "+(servingNumber+1));
			wait();
			customer.logln("someone notified me("+(serving!=null? serving.getName(): "Cook")+")");
		}
		customer.logln("the cook is free, notifyAll and wait for the food"); // The cook may be waiting, need to notify him
		
		serving = customer; // I'm the customer who is being served right now.
		servingNumber = customer.getQueueNumber();
		
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
		Restaurant2 fs = new Restaurant2();
		new Cook(fs, "Cook").start();

		for(int i=0; i<nCustomers; i++){
			new Customer(fs, "Customer#"+i).start();
		}

	}

}
