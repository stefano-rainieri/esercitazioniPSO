package hospital;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 03/12/15.
 */
public interface Hospital {

    void requestTherapy(Patient patient) throws InterruptedException;
    void assistPatient(Doctor doctor) throws InterruptedException;
    void dismissPatient(Doctor doctor);
}
