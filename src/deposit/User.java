package deposit;

/**
 * Models a user.
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class User extends Thread{

	private int nItems;
	private Deposit warehouse;
	
	public User(String name, int nItems, Deposit w){
		super(name);
		this.nItems = nItems;
		this.warehouse = w;
	}
	
	/**
	 * Returns the number of items the users has.
	 * @return the number of items.
	 */
	public int getNumberOfItems(){
		return nItems;
	}
	
	/**
	 * Prints a message of this user.
	 * @param msg the message to print.
	 */
	public void log(String msg){
		System.out.println(getName()+"["+nItems+"]: "+msg);
	}
	
	public void run(){
		int slotIndex = warehouse.putItems(this);
		
		try {
			Thread.sleep((long) (Math.random()*1000));
		} catch (InterruptedException e) {
			this.log("Someone interrupted me. Exit.");
			System.exit(-1);
		}
		
		warehouse.getItems(this, slotIndex);
	}
}
