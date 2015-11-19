package foodshop;

/**
 * 
 * Models a customer.
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Customer extends Thread{

	private Restaurant shop;
	private int queueNumber; // identifier of my priority in the restaurant queue
	private boolean orderByPhone;

    public Customer(Restaurant shop, String name){
        this(shop, name, false);
    }
	public Customer(Restaurant shop, String name, boolean orderByPhone){
		super(name);
		this.shop = shop;
		this.queueNumber = -1;
        this.orderByPhone = orderByPhone;
	}
	
	public int getQueueNumber(){
		return queueNumber;
	}

    public boolean hasOrderedByPhone(){
        return orderByPhone;
    }

	public void setQueueNumber(int token){
		this.queueNumber = token;
	}

	public void log(String msg){
        System.out.print(getName()+"["+(hasOrderedByPhone()? "P":"D")+"]");
        if(getQueueNumber()==-1)
            System.out.print(": "+msg);
        else
		    System.out.print("["+getQueueNumber()+"]: "+msg);
	}

	public void logln(String msg){
		log(msg);
		System.out.println();
	}

	public void run(){
		try{	
			shop.orderFood(this);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

	}

}
