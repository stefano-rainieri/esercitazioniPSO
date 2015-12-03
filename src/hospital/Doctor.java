package hospital;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 02/12/15.
 */
public class Doctor extends Thread {

    private Hospital hospital;
    private Patient patient;

    public Doctor(String name, Hospital hospital){
        super(name);
        this.hospital = hospital;
        patient = null;
    }

    public void logln(String msg){
        synchronized (System.out){
            System.out.println(getName()+": "+msg);
        }
    }

    /**
     * Returns true if the doctor is taking care of a patient, false otherwie
     * @return true or false.
     */
    public boolean isBusy(){
        return patient!=null;
    }

    public Patient getPatient(){
        return patient;
    }

    public void setPatient(Patient patient){
        this.patient = patient;
    }

    public void run(){
        try{
            while(true) {
                hospital.assistPatient(this);
                Thread.sleep((long)(Math.random()*1000));
                hospital.dismissPatient(this);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
