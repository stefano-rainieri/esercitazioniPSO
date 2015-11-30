package ferry;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 30/11/15.
 */
public interface Dock {

    void getOnFerry(Car car) throws InterruptedException;
    void getOffFerry(Car car) throws InterruptedException;
    void boardPassengers(Ferry ferry) throws InterruptedException;
    String getDockName();
    void printStatus();

}
