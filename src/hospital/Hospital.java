package hospital;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 02/12/15.
 */
public class Hospital {

    private String name;

    public Hospital(String name){

        this.name = name;
    }

    public synchronized void requestTherapy(Patient patient) throws InterruptedException{

    }

}
