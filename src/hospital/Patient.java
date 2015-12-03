package hospital;

/**
 * Created by Marco Galassi (marco.galassi@unimore.it) on 02/12/15.
 */
public class Patient extends Thread {

    private EmergencyCode code;
    private int queueingNumber;
    private Hospital hospital;

    public Patient(String name, Hospital hospital){
        super(name);

        this.hospital = hospital;
        code = null;
        queueingNumber = -1;
    }

    public EmergencyCode getEmergencyCode(){
        return code;
    }

    public void setEmergencyCode(EmergencyCode code){
        this.code = code;
    }

    public int getQueueingNumber(){
        return queueingNumber;
    }

    public void setQueueingNumber(int val){
        this.queueingNumber = val;
    }

    public void logln(String msg){
        synchronized (System.out){
            System.out.println(getName()+"["+queueingNumber+"]"+"["+(code==null? "None": code)+"]: "+msg);
        }
    }

    public void run(){
        try{
            hospital.requestTherapy(this);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

}
