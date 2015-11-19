package foodshop;

public interface Restaurant {
	
	void orderFood(Customer customer) throws InterruptedException;
	void prepareFood(Cook cook) throws InterruptedException;
	void serveFood(Cook cook) throws InterruptedException;

}
