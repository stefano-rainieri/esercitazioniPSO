package hospital;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 02/12/15.
 */
public class Patient extends Thread {

    private EmergencyCode code;
    private Hospital hospital;

    public Patient(String name, Hospital hospital){
        super(name);

        this.hospital = hospital;
        code = null;
    }

    public EmergencyCode getEmergencyCode(){
        return code;
    }

    public void setEmergencyCode(EmergencyCode code){
        this.code = code;
    }

    public void run(){
        try{
            hospital.requestTherapy(this);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
