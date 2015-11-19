package deposit;

import java.util.Random;

/**
 * Models a deposit where users can put their items.
 * 
 * @author Marco Galassi, marco.galassi@unimore.it
 *
 */
public class Deposit {

	private int numberOfSlots; // number of slots where users can put items into
	private int maxCapacity; // maximum capacity of the slots
	private int slots[]; // slots array: values are the current number of items in that slot

	/**
	 * Constructor.
	 * @param numberOfSlots number of slots user can place items into.
	 * @param maxCapacity maximum capacity of each slot.
	 */
	public Deposit(int numberOfSlots, int maxCapacity){

		this.numberOfSlots = numberOfSlots;
		this.maxCapacity = maxCapacity;
		this.slots = new int[this.numberOfSlots];
		
		System.out.println("Maximum capacity: "+maxCapacity);
		printSlotsStatus();

	}
	
	/**
	 * Method for requesting a slot to put luggages into. Synchronized to provide mutal exclusive
	 * access to resources shared between different threads.
	 * @param user the user who wants to put the items in the depo.
	 * @return the slot the items have been placed.
	 */
	public synchronized int putItems(User user){
		int slotIndex = -1;
		// till I haven't found a good slot
		while(slotIndex==-1){
			// look for a slot among all of the slots
			for(int i=0; i<slots.length && slotIndex==-1; i++){
				if(maxCapacity-slots[i]>=user.getNumberOfItems()){
					slotIndex=i;
				}
			}
			// if I haven't found any, suspend and wait to be notified
			if(slotIndex==-1){
				try {
					user.log("I haven't found any available slot, wait.");
					this.wait(); // wait
					user.log("someone has notified me.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		user.log("I found slot #"+slotIndex+" available, use it.");
		// now I have found a good slot, use it
		slots[slotIndex]+=user.getNumberOfItems();
		
		printSlotsStatus();

		return slotIndex;
	}

	/**
	 * Method for requesting items back. Synchronized. 
	 * @param user the user who wants his items back.
	 * @param slotIndex the slot he wants to get his items back from.
	 */
	public synchronized void getItems(User user, int slotIndex){
		user.log("I am getting my "+user.getNumberOfItems()+" items from slot #"+slotIndex);
		slots[slotIndex] -= user.getNumberOfItems();
		
		printSlotsStatus();
		
		notifyAll();
	}

	/**
	 * Prints the depo current status.
	 * No need to be synchronized as long as this is called from synchronized methods.
	 */
	private void printSlotsStatus(){
		for(int i=0; i<slots.length; i++) 
			System.out.print("----");
		System.out.println();
		for(int i=0; i<slots.length; i++) 
			System.out.print("| "+slots[i]+" ");
		System.out.print("|\n");
		for(int i=0; i<slots.length; i++) 
			System.out.print("----");
		System.out.println();
	}

	public static void main(String args[]){
		int nSlots = 5;
		int maxCap = 4;
		int nUsers = 10;
		
		Deposit w = new Deposit(nSlots, maxCap);
		for(int i=0; i<nUsers; i++){
			new User("User"+i, new Random().nextInt(maxCap)+1, w).start();
		}
		
	}
}
