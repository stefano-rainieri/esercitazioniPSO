package foodshop;

/**
 * This adds a delivery man. Customers now can order food in 2 ways. Either they go to the shop, and they just have to wait
 * until the cook has prepared their food, or they call the shop, and they also then have to wait for the delivery man to bring
 * the food to them.
 * 
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Restaurant3 implements Restaurant{

	private Customer serving; // reference to the customer currently being served.
    private Customer delivering; // reference to the customer currently being delivered.


	private int queueNumber; // incremental number given to the customers as soon as they get into orderFood, in order to provide a FIFO-like serving mechanism.
	private int servingNumber; // current number of the customer being served(servingNumber+1) is the next one that needs to be served

	public Restaurant3(){
		serving=null; // no customer is served initially
        delivering = null;
		queueNumber = 0;
		servingNumber = -1; // -1 stands for a user who was not given any number

		System.out.println("Restaurant #3. Customer can both order directly or via phone.");
		System.out.println();
	}

    /**
     * Used by customers to order food.
     * @param customer the customer who wants to order food.
     * @throws InterruptedException
     */
	public synchronized void orderFood(Customer customer) throws InterruptedException {
		customer.setQueueNumber(queueNumber); // The customer will be served with this number(0, initially)
		queueNumber++; // next customer will have the next number

		customer.logln("want to order, is the cook free?");
		// Until the cook is not available, or it is not my turn to be server
		while(serving!=null || (servingNumber+1)!=customer.getQueueNumber()){
			if(serving!=null)
				customer.logln("the cook is not free, he is serving "+serving.getName()+". Wait for him.");
			else
				customer.logln("the cook is free, but it is not my turn. I have queueNumber "+customer.getQueueNumber()+", current is "+(servingNumber+1));
			wait();
			customer.logln("someone notified me");
		}
		// If I run code here, I am the one who is being served
        customer.logln("the cook is free, notifyAll and wait for the food"); // The cook may be waiting, need to notify him
		
		serving = customer; // I'm the customer who is being served right now.
		servingNumber = customer.getQueueNumber();
		
		notifyAll(); // notifies every thread which is waiting(customers, cook, delivery man)
        while(serving==customer){
            wait(); // wait (the cook needs time to prepare my food, and I need to wait him)
            if(serving==customer)
                customer.logln("someone notified me, but the cook is still preparing my food, wait again.");
            else
                customer.logln("someone notified me, the cook has finished preparing my food.");
        }

        //notifyAll(); // I need to wake up all of the threads: at this point, in fact, all of the customers may have already entered this method and put themselves on wait for the cook to be free.

        // My food is ready, if I ordered by phone, I need to wait for the delivery, otherwise i'm done
        if(customer.hasOrderedByPhone()){
            customer.logln("my food is ready, is the delivery man available?");

            while(delivering!=null){
                customer.logln("the delivery man is not available now, I need to wait him.");
                wait(); // wait (the cook needs time to prepare my food, and I need to wait him)
                customer.logln("someone notified me.");
            }

            delivering = customer;
            customer.logln("delivery man is available, notifyAll and wait for him to bring me food.");
            notifyAll(); // The delivery man may be waiting, need to wake him up
            while(delivering==customer) {
                wait();
                if(delivering==customer)
                    customer.logln("someone notified me, but the delivery man is still bringing me food, wait again.");
                else
                    customer.logln("someone notified me, the delivery man has delivered me my food.");
            }
        }

        customer.logln("--------------> I HAVE MY FOOD, BYE.");
	}

	public synchronized void prepareFood(Cook cook) throws InterruptedException {
		/*
		 While is necessary. Even unlikely, it may happen that the notifyAll called once the served customer is done
		 wakes up the Cook before any other Customer, so the cook would find serving == null.
		*/
		while(serving==null){
			cook.logln("no one to serve, wait.");
			wait(); // Wait for a customer to notify me he wants food.
			cook.logln("someone notified me");
		}
		cook.logln("serving "+serving.getName());
	}

	public synchronized void serveFood(Cook cook) throws InterruptedException {
		cook.logln("finished serving "+serving.getName()+". I'm free again, notifyAll.");
		notifyAll(); // Notify everyone
        serving = null; // The customer is no more being served by the cook, another customer can be served.
	}


    public synchronized void getReadyFood(DeliveryMan deliveryMan) throws InterruptedException{

        while(delivering==null){
            deliveryMan.logln("No food to be delivered, wait");
            wait();
            deliveryMan.logln("someone notified me.");
        }
        deliveryMan.logln("delivering food to "+delivering.getName());

    }

    public synchronized void deliverFood(DeliveryMan deliveryMan) throws InterruptedException {
        deliveryMan.logln("Food delivered to "+delivering.getName()+", notifyAll.");
        notifyAll();
        delivering=null;
    }

	public static void main(String args[]){
		int nCustomers = 5;
		Restaurant3 fs = new Restaurant3();
		new Cook(fs, "Cook").start();
        new DeliveryMan(fs, "DeliveryMan").start();

		for(int i=0; i<nCustomers; i++){
			new Customer(fs, "Customer#"+i, Math.random()<0.5? false: true).start();
		}

	}

}
